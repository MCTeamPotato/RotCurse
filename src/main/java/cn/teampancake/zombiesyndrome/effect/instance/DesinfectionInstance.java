package cn.teampancake.zombiesyndrome.effect.instance;

import cn.teampancake.zombiesyndrome.ZombieSyndrome;
import net.minecraft.world.effect.MobEffectInstance;

import static cn.teampancake.zombiesyndrome.config.effect.DesinfectionConfig.*;

public class DesinfectionInstance extends MobEffectInstance {
    public DesinfectionInstance() {
        super(ZombieSyndrome.ZOMBIFICATION.get(), DURATION.get(), 0, false, VISIBLE.get(), SHOW_ICON.get(), null);
    }
}
