package cn.teampancake.zombiesyndrome.config;

import cn.teampancake.zombiesyndrome.ZombieSyndrome;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MainConfig {
    public static final ForgeConfigSpec MAIN_CONFIG;
    public static final ForgeConfigSpec.ConfigValue<String> CURE_ITEM;
    public static final ForgeConfigSpec.IntValue POSSIBILITY, SLOWNESS_DURATION, WEAKNESS_DURATION;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> UNREMOVEABLE_EFFECTS_LIST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> INFECTION_SOURCES_LIST;
    public static final ForgeConfigSpec.BooleanValue COPY_ARMORS, PERSISTENCE, PICK_UP_LOOT;

    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        configBuilder.push("ZombieSyndrome");
        SLOWNESS_DURATION = configBuilder.defineInRange("TickDurationOfSlownessWhenPlayerGetCuredFromZombification", 600, 0, Integer.MAX_VALUE);
        WEAKNESS_DURATION = configBuilder.defineInRange("TickDurationOfWeaknessWhenPlayerGetCuredFromZombification", 600, 0, Integer.MAX_VALUE);
        POSSIBILITY = configBuilder.defineInRange("InfectionPossibilityOnZombieAttack", 60, 0, 100);
        COPY_ARMORS = configBuilder.define("ShouldZombieSpawnedOnZombificationExpiryHaveUndroppablePlayersArmors", true);
        PERSISTENCE = configBuilder.define("ShouldZombieSpawnedOnZombificationExpiryBePersistent", true);
        PICK_UP_LOOT = configBuilder.define("ShouldZombieSpawnedOnZombificationExpiryBeAbleToPickUpItemsOnGround", true);
        CURE_ITEM = configBuilder.comment("You can change this to a custom item certainly").define("TheItemUsedToCureZombificationEffect", "minecraft:golden_apple");
        UNREMOVEABLE_EFFECTS_LIST = configBuilder.comment("If you write registry names of effects down here, it will not be removed by milk-like cure").defineList("EffectsThatWillNotBeRemovedByMilkCure", Lists.newArrayList(ZombieSyndrome.MOD_ID + ":zombification"), o -> o instanceof String);
        INFECTION_SOURCES_LIST = configBuilder.comment("Some mods may not implement their zombie-like entities properly which cause the infection invalid on their 'zombies'. But you can add their registry names here to mark them as infection source").defineList("InfectionSourcesEntitiesList", new ArrayList<>(), o -> o instanceof String);
        configBuilder.pop();
        MAIN_CONFIG = configBuilder.build();
    }

    /**
     * {@link List#contains(Object)} is expensive for iteration, so we transform it to {@link java.util.HashSet} instead
     **/
    public static final Supplier<Set<MobEffect>> UNREMOVEABLE_EFFECTS = Suppliers.memoize(() -> UNREMOVEABLE_EFFECTS_LIST.get().stream().map(string -> ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(string))).collect(Collectors.toSet()));
    public static final Supplier<Set<EntityType<?>>> INFECTION_SOURCES = Suppliers.memoize(() -> INFECTION_SOURCES_LIST.get().stream().map(string -> ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(string))).collect(Collectors.toSet()));
}
