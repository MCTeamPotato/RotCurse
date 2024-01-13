package cn.teampancake.zombiesyndrome.event;

import cn.teampancake.zombiesyndrome.ZombieSyndrome;
import cn.teampancake.zombiesyndrome.config.MainConfig;
import cn.teampancake.zombiesyndrome.effect.Zombification;
import cn.teampancake.zombiesyndrome.effect.instance.DesinfectionInstance;
import cn.teampancake.zombiesyndrome.effect.instance.ZombificationInstance;
import cn.teampancake.zombiesyndrome.registry.ZSEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = ZombieSyndrome.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeBusEventSubscriber {
    private static final EquipmentSlot[] EQUIPMENT_SLOTS = EquipmentSlot.values();

    private static Zombie customZombie(LivingEntity entity, ServerLevel serverLevel) {
        Zombie zombie = new Zombie(serverLevel);
        zombie.setPos(entity.getX(), entity.getY(), entity.getZ());
        zombie.setCanPickUpLoot(MainConfig.PICK_UP_LOOT.get());
        if (MainConfig.PERSISTENCE.get()) zombie.setPersistenceRequired();
        Optional.ofNullable(zombie.getAttribute(Attributes.MAX_HEALTH)).ifPresent(attributeInstance -> attributeInstance.setBaseValue(entity.getMaxHealth()));
        if (MainConfig.COPY_ARMORS.get()) {
            for (EquipmentSlot slot : EQUIPMENT_SLOTS) {
                zombie.setItemSlot(slot, entity.getItemBySlot(slot));
                zombie.setDropChance(slot, 0.0F);
            }
        }

        UUID uuid = Mth.createInsecureUUID(ThreadLocalRandom.current());
        while (serverLevel.getEntity(uuid) != null) uuid = Mth.createInsecureUUID(ThreadLocalRandom.current());
        zombie.setUUID(uuid);

        zombie.setHealth(zombie.getMaxHealth());
        return zombie;
    }

    @SubscribeEvent
    public static void onPotionExpiry(PotionEvent.PotionExpiryEvent event) {
        if (event.isCanceled()) return;
        MobEffectInstance effectInstance = event.getPotionEffect();
        LivingEntity entity = event.getEntityLiving();
        Level level = entity.level;
        if (level instanceof ServerLevel && effectInstance instanceof ZombificationInstance) {
            ServerLevel serverLevel = (ServerLevel) level;
            serverLevel.addFreshEntity(customZombie(entity, serverLevel));
            entity.hurt(new Zombification.ZombificationDamageSource(((ZombificationInstance)effectInstance).getSource()), Float.MAX_VALUE);
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.isCanceled() || ZombieSyndrome.nextInt(0, 101) < MainConfig.POSSIBILITY.get()) return;
        LivingEntity entity = event.getEntityLiving();
        if (entity.level.isClientSide || entity.hasEffect(ZSEffects.DESINFECTION.get())) return;
        Entity source = event.getSource().getDirectEntity();
        if (entity instanceof LivingEntity && (source instanceof Zombie || (source != null && MainConfig.INFECTION_SOURCES.get().contains(source.getType()))) && !entity.hasEffect(ZSEffects.ZOMBIFICATION.get())) {
            entity.addEffect(new ZombificationInstance(source));
        }
    }

    @SubscribeEvent
    public static void onLivingUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.isCanceled()) return;
        ResourceLocation item = event.getItem().getItem().getRegistryName();
        LivingEntity entity = event.getEntityLiving();
        if (item == null || !Objects.equals(item.toString(), MainConfig.CURE_ITEM.get()) || entity.level.isClientSide) return;
        if (entity.hasEffect(ZSEffects.ZOMBIFICATION.get())) entity.removeEffect(ZSEffects.ZOMBIFICATION.get());
        entity.addEffect(new DesinfectionInstance());
    }
}
