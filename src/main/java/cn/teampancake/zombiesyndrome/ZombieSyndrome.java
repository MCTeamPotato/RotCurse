package cn.teampancake.zombiesyndrome;

import cn.teampancake.zombiesyndrome.config.DesinfectionConfig;
import cn.teampancake.zombiesyndrome.config.MainConfig;
import cn.teampancake.zombiesyndrome.config.ZombificationConfig;
import cn.teampancake.zombiesyndrome.effect.Desinfection;
import cn.teampancake.zombiesyndrome.effect.Zombification;
import cn.teampancake.zombiesyndrome.effect.instance.DesinfectionInstance;
import cn.teampancake.zombiesyndrome.effect.instance.ZombificationInstance;
import com.google.common.base.Suppliers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod(ZombieSyndrome.MOD_ID)
public class ZombieSyndrome {
    public static final String MOD_ID = "zombiesyndrome";
    private static final DeferredRegister<MobEffect> EFFECT_REGISTER = DeferredRegister.create(ForgeRegistries.POTIONS, MOD_ID);
    public static final RegistryObject<Desinfection> DESINFECTION = EFFECT_REGISTER.register("desinfection", Desinfection::new);
    public static final RegistryObject<Zombification> ZOMBIFICATION = EFFECT_REGISTER.register("zombification", Zombification::new);

    /**
     * {@link List#contains(Object)} is expensive for iteration, so we transform it to {@link java.util.HashSet} instead
     **/
    public static final Supplier<Set<MobEffect>> UNREMOVEABLE_EFFECTS = Suppliers.memoize(() -> MainConfig.UNREMOVEABLE_EFFECTS_LIST.get().stream().map(string -> ForgeRegistries.POTIONS.getValue(new ResourceLocation(string))).collect(Collectors.toSet()));
    public static final Supplier<Set<EntityType<?>>> INFECTION_SOURCES = Suppliers.memoize(() -> MainConfig.INFECTION_SOURCES_LIST.get().stream().map(string -> ForgeRegistries.ENTITIES.getValue(new ResourceLocation(string))).collect(Collectors.toSet()));

    private static final EquipmentSlot[] EQUIPMENT_SLOTS = EquipmentSlot.values();

    public ZombieSyndrome() {
        final IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        final ModLoadingContext context = ModLoadingContext.get();

        context.registerConfig(ModConfig.Type.COMMON, MainConfig.MAIN_CONFIG, MOD_ID + "/main.toml");
        context.registerConfig(ModConfig.Type.COMMON, DesinfectionConfig.DESINFECTION_CONFIG, MOD_ID + "/desinfection.toml");
        context.registerConfig(ModConfig.Type.COMMON, ZombificationConfig.ZOMBIFICATION_CONFIG, MOD_ID + "/zombification.toml");
        EFFECT_REGISTER.register(modBus);

        forgeBus.addListener(EventPriority.LOWEST, (LivingAttackEvent event) -> {
            if (event.isCanceled() || nextInt(0, 101) < MainConfig.POSSIBILITY.get()) return;
            LivingEntity entity = event.getEntityLiving();
            if (entity.level.isClientSide || entity.hasEffect(DESINFECTION.get())) return;
            Entity source = event.getSource().getDirectEntity();
            if (entity instanceof Player && (source instanceof Zombie || (source != null && INFECTION_SOURCES.get().contains(source.getType())))) {
                entity.addEffect(new ZombificationInstance());
            }
        });

        forgeBus.addListener(EventPriority.LOWEST, (PotionEvent.PotionExpiryEvent event) -> {
            if (event.isCanceled()) return;
            MobEffectInstance effectInstance = event.getPotionEffect();
            LivingEntity entity = event.getEntityLiving();
            Level level = entity.level;
            if (level instanceof ServerLevel && effectInstance != null && effectInstance.getEffect().equals(ZOMBIFICATION.get())) {
                ServerLevel serverLevel = (ServerLevel) level;
                entity.hurt(Zombification.DAMAGE_SOURCE, Float.MAX_VALUE);
                serverLevel.addFreshEntity(customZombie(new Zombie(serverLevel), entity, serverLevel));
            }
        });

        forgeBus.addListener(EventPriority.LOWEST, (LivingEntityUseItemEvent.Finish event) -> {
            if (event.isCanceled()) return;
            ResourceLocation item = event.getItem().getItem().getRegistryName();
            LivingEntity entity = event.getEntityLiving();
            if (item == null || !Objects.equals(item.toString(), MainConfig.CURE_ITEM.get()) || entity.level.isClientSide) return;
            if (entity.hasEffect(ZOMBIFICATION.get())) {
                entity.removeEffect(ZOMBIFICATION.get());
                entity.addEffect(new DesinfectionInstance());
            }
        });
    }

    public static int nextInt(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }

    private static Zombie customZombie(Zombie zombie, LivingEntity entity, ServerLevel serverLevel) {
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
}
