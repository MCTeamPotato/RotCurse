package cn.teampancake.zombiesyndrome;

import cn.teampancake.zombiesyndrome.config.MainConfig;
import cn.teampancake.zombiesyndrome.config.effect.DesinfectionConfig;
import cn.teampancake.zombiesyndrome.config.effect.ZombificationConfig;
import cn.teampancake.zombiesyndrome.registry.ZSEffects;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.concurrent.ThreadLocalRandom;

@Mod(ZombieSyndrome.MOD_ID)
public class ZombieSyndrome {
    public static final String MOD_ID = "zombiesyndrome";

    public ZombieSyndrome() {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        final ModLoadingContext context = ModLoadingContext.get();
        final ModConfig.Type common = ModConfig.Type.COMMON;

        context.registerConfig(common, MainConfig.MAIN_CONFIG, MOD_ID + "/main.toml");
        context.registerConfig(common, DesinfectionConfig.DESINFECTION_CONFIG, MOD_ID + "/desinfection.toml");
        context.registerConfig(common, ZombificationConfig.ZOMBIFICATION_CONFIG, MOD_ID + "/zombification.toml");

        ZSEffects.EFFECT_REGISTER.register(modBus);
    }

    public static int nextInt(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }
}
