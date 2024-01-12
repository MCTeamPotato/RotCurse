package com.teampotato.rotcurse.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class Blessing extends MobEffect {
    public Blessing() {
        super(MobEffectCategory.BENEFICIAL, 191981);
    }

    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {}

    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity livingEntity, int amplifier, double health) {}

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    public boolean isBeneficial() {
        return true;
    }
}
