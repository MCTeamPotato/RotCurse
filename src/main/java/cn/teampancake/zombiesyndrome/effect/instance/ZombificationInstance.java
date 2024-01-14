package cn.teampancake.zombiesyndrome.effect.instance;

import cn.teampancake.zombiesyndrome.ZombieSyndrome;
import cn.teampancake.zombiesyndrome.config.MainConfig;
import cn.teampancake.zombiesyndrome.registry.ZSEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;
import java.util.UUID;

import static cn.teampancake.zombiesyndrome.config.effect.ZombificationConfig.*;

@SuppressWarnings("resource")
public class ZombificationInstance extends MobEffectInstance {
    public ZombificationInstance() {
        super(ZSEffects.ZOMBIFICATION, ZombieSyndrome.nextInt(MIN.get(), MAX.get() + 1), 0, false, VISIBLE.get(), SHOW_ICON.get());
    }

    public boolean tick(LivingEntity livingEntity, Runnable runnable) {
        if (this.getDuration() == 1) {
            Level level = livingEntity.level();
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.addFreshEntity(customZombie(livingEntity, serverLevel));
                livingEntity.hurt(serverLevel.damageSources().magic(), Float.MAX_VALUE);
            }
        }
        return super.tick(livingEntity, runnable);
    }


    @Unique
    private static final EquipmentSlot[] EQUIPMENT_SLOTS = EquipmentSlot.values();

    @Unique
    private static @NotNull Zombie customZombie(@NotNull LivingEntity entity, ServerLevel serverLevel) {
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

        UUID uuid = Mth.createInsecureUUID();
        while (serverLevel.getEntity(uuid) != null) uuid = Mth.createInsecureUUID();
        zombie.setUUID(uuid);

        zombie.setHealth(zombie.getMaxHealth());
        return zombie;
    }
}
