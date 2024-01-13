package cn.teampancake.zombiesyndrome.effect;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class Zombification extends MobEffect {
    public Zombification() {
        super(MobEffectCategory.HARMFUL, 114514);
    }

    private static final DamageSource DAMAGE_SOURCE = new DamageSource("zombification").bypassArmor().setMagic();

    public static DamageSource getDamageSource() {
        return DAMAGE_SOURCE;
    }

    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {}

    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity livingEntity, int amplifier, double health) {}

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    public boolean isBeneficial() {
        return false;
    }
}
