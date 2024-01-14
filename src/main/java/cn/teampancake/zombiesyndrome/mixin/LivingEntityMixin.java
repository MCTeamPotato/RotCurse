package cn.teampancake.zombiesyndrome.mixin;

import cn.teampancake.zombiesyndrome.ZombieSyndrome;
import cn.teampancake.zombiesyndrome.config.MainConfig;
import cn.teampancake.zombiesyndrome.effect.instance.DesinfectionInstance;
import cn.teampancake.zombiesyndrome.effect.instance.ZombificationInstance;
import cn.teampancake.zombiesyndrome.registry.ZSEffects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"resource"})
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract ItemStack getUseItem();

    @Shadow @Final private Map<MobEffect, MobEffectInstance> activeEffects;

    @Shadow protected abstract void onEffectRemoved(MobEffectInstance mobEffectInstance);

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "removeAllEffects", at = @At("HEAD"), cancellable = true)
    private void onRemoveAllEffects(CallbackInfoReturnable<Boolean> cir) {
        if (this.level().isClientSide) {
            cir.setReturnValue(false);
        } else {
            Iterator<MobEffectInstance> iterator = this.activeEffects.values().iterator();

            boolean bl;
            for(bl = false; iterator.hasNext(); bl = true) {
                MobEffectInstance instance = iterator.next();
                if (MainConfig.UNREMOVEABLE_EFFECTS.get().contains(instance.getEffect())) continue;
                this.onEffectRemoved(instance);
                iterator.remove();
            }

            cir.setReturnValue(bl);
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"))
    private void onHurt(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        if (ZombieSyndrome.nextInt(0, 101) < MainConfig.POSSIBILITY.get()) return;
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.level().isClientSide || entity.hasEffect(ZSEffects.DESINFECTION)) return;
        Entity source = damageSource.getDirectEntity();
        if (MainConfig.INFECTABLE_ENTITIES.get().contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType())) && (source instanceof Zombie || (source != null && MainConfig.INFECTION_SOURCES.get().contains(source.getType()))) && !entity.hasEffect(ZSEffects.ZOMBIFICATION)) {
            entity.addEffect(new ZombificationInstance());
        }
    }

    @Inject(method = "completeUsingItem", at = @At("HEAD"))
    private void onLivingUseItemFinish(CallbackInfo ci) {
        ResourceLocation item = BuiltInRegistries.ITEM.getKey(this.getUseItem().getItem());
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!Objects.equals(item.toString(), MainConfig.CURE_ITEM.get()) || entity.level().isClientSide) return;
        if (entity.hasEffect(ZSEffects.ZOMBIFICATION)) {
            entity.removeEffect(ZSEffects.ZOMBIFICATION);
        }
        entity.addEffect(new DesinfectionInstance());
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, MainConfig.SLOWNESS_DURATION.get()));
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, MainConfig.WEAKNESS_DURATION.get()));
    }
}
