package cn.teampancake.zombiesyndrome.registry;

import cn.teampancake.zombiesyndrome.ZombieSyndrome;
import cn.teampancake.zombiesyndrome.effect.Desinfection;
import cn.teampancake.zombiesyndrome.effect.Zombification;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ZSEffects {
    public static final DeferredRegister<MobEffect> EFFECT_REGISTER = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ZombieSyndrome.MOD_ID);
    public static final RegistryObject<Zombification> ZOMBIFICATION = EFFECT_REGISTER.register("zombification", Zombification::new);
    public static final RegistryObject<Desinfection> DESINFECTION = EFFECT_REGISTER.register("desinfection", Desinfection::new);
}
