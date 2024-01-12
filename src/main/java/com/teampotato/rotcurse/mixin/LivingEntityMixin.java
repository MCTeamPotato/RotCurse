package com.teampotato.rotcurse.mixin;

import com.teampotato.rotcurse.RotCurse;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Redirect(method = "curePotionEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;isCurativeItem(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean rotcurse$onCheckCurative(@NotNull MobEffectInstance instance, ItemStack itemStack) {
        if (RotCurse.UNREMOVEABLE_EFFECTS.get().contains(instance.getEffect())) return false;
        return instance.isCurativeItem(itemStack);
    }
}
