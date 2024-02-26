package cn.teampancake.zombiesyndrome.effect;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

public class Zombification extends MobEffect {
    public Zombification() {
        super(MobEffectCategory.HARMFUL, 114514);
    }

    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {}

    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity livingEntity, int amplifier, double health) {}

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    public boolean isBeneficial() {
        return false;
    }

    public static final class ZombificationDamageSource extends DamageSource {
        private final @Nullable UUID source;
        public ZombificationDamageSource(@Nullable UUID source) {
            super("zombification");
            this.source = source;
        }

        public boolean isBypassArmor() {
            return true;
        }

        public boolean isBypassInvul() {
            return true;
        }

        public boolean isBypassMagic() {
            return true;
        }

        public @Nullable UUID getEntityUUID() {
            return this.source;
        }

        @Contract("_ -> new")
        public @NotNull Component getLocalizedDeathMessage(@NotNull LivingEntity dead) {
            Component message = new TranslatableComponent("death.attack.zombification", dead.getDisplayName());
            if (!(dead.level instanceof ServerLevel)) return message;
            UUID source = this.getEntityUUID();
            if (source == null) return message;
            Entity entity = ((ServerLevel)dead.level).getEntity(source);
            if (entity == null) return message;
            return new TranslatableComponent("death.attack.zombification.has_source", dead.getDisplayName(), entity.getDisplayName());
        }
    }
}
