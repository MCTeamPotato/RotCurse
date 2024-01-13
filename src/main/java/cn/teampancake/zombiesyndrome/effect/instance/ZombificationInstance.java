package cn.teampancake.zombiesyndrome.effect.instance;

import cn.teampancake.zombiesyndrome.ZombieSyndrome;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;

import static cn.teampancake.zombiesyndrome.config.effect.ZombificationConfig.*;

public class ZombificationInstance extends MobEffectInstance {
    private final Entity source;
    public ZombificationInstance(Entity source) {
        super(ZombieSyndrome.ZOMBIFICATION.get(), ZombieSyndrome.nextInt(MIN.get(), MAX.get() + 1), 0, false, VISIBLE.get(), SHOW_ICON.get(), null);
        this.source = source;
    }

    public Entity getSource() {
        return this.source;
    }
}
