package cn.teampancake.zombiesyndrome.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class MainConfig {
    public static final ForgeConfigSpec MAIN_CONFIG;
    public static final ForgeConfigSpec.ConfigValue<String> CURE_ITEM;
    public static final ForgeConfigSpec.IntValue POSSIBILITY;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> UNREMOVEABLE_EFFECTS_LIST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> INFECTION_SOURCES_LIST;
    public static final ForgeConfigSpec.BooleanValue COPY_ARMORS, PERSISTENCE, PICK_UP_LOOT;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("ZombieSyndrome");
        CURE_ITEM = builder.define("TheItemUsedToCureZombificationEffect", "minecraft:golden_apple");
        POSSIBILITY = builder.defineInRange("InfectionPossibilityOnZombieAttack", 60, 0, 100);
        COPY_ARMORS = builder.define("ShouldZombieSpawnedOnZombificationExpiryHavePlayersArmors", true);
        PERSISTENCE = builder.define("ShouldZombieSpawnedOnZombificationExpiryBePersistent", true);
        PICK_UP_LOOT = builder.define("ShouldZombieSpawnedOnZombificationExpiryBeAbleToPickUpItemsOnGround", true);
        UNREMOVEABLE_EFFECTS_LIST = builder.comment("If you write registry names of effects down here, it will not be removed by milk-like cure").defineList("EffectsThatWillNotBeRemovedByMilkCure", new ArrayList<>(), o -> o instanceof String);
        INFECTION_SOURCES_LIST = builder.comment("Some mods may not implement their zombie-like entities properly which cause the infection invalid on their 'zombies'. But you can add their registry names here to mark them as infection source").defineList("InfectionSourcesEntitiesList", new ArrayList<>(), o -> o instanceof String);
        builder.pop();
        MAIN_CONFIG = builder.build();
    }
}
