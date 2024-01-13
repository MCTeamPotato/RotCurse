package cn.teampancake.zombiesyndrome.registry;

import cn.teampancake.zombiesyndrome.ZombieSyndrome;
import cn.teampancake.zombiesyndrome.config.MainConfig;
import cn.teampancake.zombiesyndrome.effect.Desinfection;
import cn.teampancake.zombiesyndrome.effect.Zombification;
import com.google.common.base.Suppliers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ZSEffects {
    public static final DeferredRegister<MobEffect> EFFECT_REGISTER = DeferredRegister.create(ForgeRegistries.POTIONS, ZombieSyndrome.MOD_ID);
    public static final RegistryObject<Zombification> ZOMBIFICATION = EFFECT_REGISTER.register("zombification", Zombification::new);
    public static final RegistryObject<Desinfection> DESINFECTION = EFFECT_REGISTER.register("desinfection", Desinfection::new);

    /**
     * {@link List#contains(Object)} is expensive for iteration, so we transform it to {@link java.util.HashSet} instead
     **/
    public static final Supplier<Set<MobEffect>> UNREMOVEABLE_EFFECTS = Suppliers.memoize(() -> MainConfig.UNREMOVEABLE_EFFECTS_LIST.get().stream().map(string -> ForgeRegistries.POTIONS.getValue(new ResourceLocation(string))).collect(Collectors.toSet()));
    public static final Supplier<Set<EntityType<?>>> INFECTION_SOURCES = Suppliers.memoize(() -> MainConfig.INFECTION_SOURCES_LIST.get().stream().map(string -> ForgeRegistries.ENTITIES.getValue(new ResourceLocation(string))).collect(Collectors.toSet()));
}
