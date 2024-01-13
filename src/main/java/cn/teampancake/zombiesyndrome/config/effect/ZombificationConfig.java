package cn.teampancake.zombiesyndrome.config.effect;

import net.minecraftforge.common.ForgeConfigSpec;

public class ZombificationConfig {
    public static final ForgeConfigSpec ZOMBIFICATION_CONFIG;
    public static final ForgeConfigSpec.IntValue MIN;
    public static final ForgeConfigSpec.IntValue MAX;
    public static final ForgeConfigSpec.BooleanValue VISIBLE, SHOW_ICON;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("ZombificationProperties");
        MIN = builder.defineInRange("MinimalTicksDurationOfThisEffectOnZombieAttack", 1800, 0, Integer.MAX_VALUE);
        MAX = builder.defineInRange("MaximumTicksDurationOfThisEffectOnZombieAttack", 3600, 0, Integer.MAX_VALUE);
        VISIBLE = builder.define("WhetherThisEffectWillRenderAmbientParticles", true);
        SHOW_ICON = builder.define("WhetherTheEffectIconWillBeRenderedInGuis", true);
        builder.pop();
        ZOMBIFICATION_CONFIG = builder.build();
    }
}
