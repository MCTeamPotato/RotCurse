package cn.teampancake.zombiesyndrome.effect.instance;

import cn.teampancake.zombiesyndrome.ZombieSyndrome;
import cn.teampancake.zombiesyndrome.registry.ZSEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

import static cn.teampancake.zombiesyndrome.config.effect.ZombificationConfig.*;

public class ZombificationInstance extends MobEffectInstance {
    private final @Nullable Entity source;
    public ZombificationInstance(@Nullable Entity source) {
        super(ZSEffects.ZOMBIFICATION.get(), ZombieSyndrome.nextInt(MIN.get(), MAX.get() + 1), 0, false, VISIBLE.get(), SHOW_ICON.get());
        this.source = source;
    }

    public @Nullable Entity getSource() {
        return this.source;
    }
}
