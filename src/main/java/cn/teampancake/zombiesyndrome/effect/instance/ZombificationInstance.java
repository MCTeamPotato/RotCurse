package cn.teampancake.zombiesyndrome.effect.instance;

import cn.teampancake.zombiesyndrome.ZombieSyndrome;
import cn.teampancake.zombiesyndrome.registry.ZSEffects;
import net.minecraft.world.effect.MobEffectInstance;

import javax.annotation.Nullable;
import java.util.UUID;

import static cn.teampancake.zombiesyndrome.config.effect.ZombificationConfig.*;

public class ZombificationInstance extends MobEffectInstance {
    private final @Nullable UUID source;
    public ZombificationInstance(@Nullable UUID source) {
        super(ZSEffects.ZOMBIFICATION.get(), ZombieSyndrome.nextInt(MIN.get(), MAX.get() + 1), 0, false, VISIBLE.get(), SHOW_ICON.get(), null);
        this.source = source;
    }

    public @Nullable UUID getSource() {
        return this.source;
    }
}
