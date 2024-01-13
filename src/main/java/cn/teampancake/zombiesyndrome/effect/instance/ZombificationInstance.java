package cn.teampancake.zombiesyndrome.effect.instance;

import cn.teampancake.zombiesyndrome.ZombieSyndrome;
import net.minecraft.world.effect.MobEffectInstance;

import static cn.teampancake.zombiesyndrome.config.ZombificationConfig.*;

public class ZombificationInstance extends MobEffectInstance {
    public ZombificationInstance() {
        super(ZombieSyndrome.ZOMBIFICATION.get(), ZombieSyndrome.nextInt(MIN.get(), MAX.get() + 1), 0, false, VISIBLE.get(), SHOW_ICON.get(), null);
    }
}
