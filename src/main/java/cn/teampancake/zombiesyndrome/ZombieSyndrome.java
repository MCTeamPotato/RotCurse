package cn.teampancake.zombiesyndrome;

import cn.teampancake.zombiesyndrome.config.MainConfig;
import cn.teampancake.zombiesyndrome.config.effect.DesinfectionConfig;
import cn.teampancake.zombiesyndrome.config.effect.ZombificationConfig;
import cn.teampancake.zombiesyndrome.effect.Desinfection;
import cn.teampancake.zombiesyndrome.effect.Zombification;
import cn.teampancake.zombiesyndrome.registry.ZSEffects;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraftforge.fml.config.ModConfig;

import java.util.concurrent.ThreadLocalRandom;

public class ZombieSyndrome implements ModInitializer {
    public static final String MOD_ID = "zombiesyndrome";

    public static int nextInt(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }

    @Override
    public void onInitialize() {
        final ModConfig.Type common = ModConfig.Type.COMMON;
        ForgeConfigRegistry.INSTANCE.register(MOD_ID, common, MainConfig.MAIN_CONFIG, MOD_ID + "/main.toml");
        ForgeConfigRegistry.INSTANCE.register(MOD_ID, common, DesinfectionConfig.DESINFECTION_CONFIG, MOD_ID + "/desinfection.toml");
        ForgeConfigRegistry.INSTANCE.register(MOD_ID, common, ZombificationConfig.ZOMBIFICATION_CONFIG, MOD_ID + "/zombification.toml");
        ZSEffects.ZOMBIFICATION = Registry.register(BuiltInRegistries.MOB_EFFECT, ZombieSyndrome.MOD_ID + ":zombification", new Zombification());
        ZSEffects.DESINFECTION = Registry.register(BuiltInRegistries.MOB_EFFECT, ZombieSyndrome.MOD_ID + ":desinfection", new Desinfection());
    }
}
