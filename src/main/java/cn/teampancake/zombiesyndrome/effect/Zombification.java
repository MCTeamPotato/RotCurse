package cn.teampancake.zombiesyndrome.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class Zombification extends MobEffect {
    public Zombification() {
        super(MobEffectCategory.HARMFUL, 114514);
    }

    public void applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {}

    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, @NotNull LivingEntity livingEntity, int amplifier, double health) {}

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    public boolean isBeneficial() {
        return false;
    }
}
