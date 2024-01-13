package com.teampotato.zombiesyndrome;

import com.google.common.base.Suppliers;
import com.teampotato.zombiesyndrome.effects.Desinfection;
import com.teampotato.zombiesyndrome.effects.Zombification;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod(ZombieSyndrome.MOD_ID)
public class ZombieSyndrome {
    public static final String MOD_ID = "zombiesyndrome";
    private static final DeferredRegister<MobEffect> EFFECT_REGISTER = DeferredRegister.create(ForgeRegistries.POTIONS, MOD_ID);
    private static final RegistryObject<Desinfection> DESINFECTION = EFFECT_REGISTER.register("desinfection", Desinfection::new);
    private static final RegistryObject<Zombification> ZOMBIFICATION = EFFECT_REGISTER.register("zombification", Zombification::new);

    private static final ForgeConfigSpec CONFIG_SPEC;
    public static final ForgeConfigSpec.ConfigValue<String> CURE_ITEM;
    public static final ForgeConfigSpec.IntValue MIN, MAX, POSSIBILITY, DURATION;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> UNREMOVEABLE_EFFECTS_LIST, INFECTION_SOURCES_LIST;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("ZombieSyndrome");
        CURE_ITEM = builder.define("TheItemUsedToCureTheEffect", "minecraft:golden_apple");
        MIN = builder.defineInRange("MinimalTicksOfZombificationEffectOnUndeadAttack", 180, 0, Integer.MAX_VALUE);
        MAX = builder.defineInRange("MaximumTicksOfZombificationEffectOnUndeadAttack", 360, 0, Integer.MAX_VALUE);
        DURATION = builder.defineInRange("TicksDurationOfDesinfectionEffect", 600, 0, Integer.MAX_VALUE);
        POSSIBILITY = builder.defineInRange("InfectedPossibilityPercentage", 60, 0, 100);
        UNREMOVEABLE_EFFECTS_LIST = builder.comment("If you write registry names of effects down here, it will not be removed by milk-like cure").defineList("EffectsThatWillNotBeRemovedByMilkCure", new ArrayList<>(), o -> o instanceof String);
        INFECTION_SOURCES_LIST = builder.comment("Some mods may not implement their zombie-like entities properly which cause the infection invalid on their 'zombies'. But you can add their registry names here to mark them as infection source").defineList("InfectionSourcesEntitiesList", new ArrayList<>(), o -> o instanceof String);
        builder.pop();
        CONFIG_SPEC = builder.build();
    }

    public static final Supplier<Set<MobEffect>> UNREMOVEABLE_EFFECTS = Suppliers.memoize(() -> UNREMOVEABLE_EFFECTS_LIST.get().stream().map(string -> ForgeRegistries.POTIONS.getValue(new ResourceLocation(string))).collect(Collectors.toSet()));
    public static final Supplier<Set<EntityType<?>>> INFECTION_SOURCES = Suppliers.memoize(() -> INFECTION_SOURCES_LIST.get().stream().map(string -> ForgeRegistries.ENTITIES.getValue(new ResourceLocation(string))).collect(Collectors.toSet()));

    public ZombieSyndrome() {
        final IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        final ModLoadingContext context = ModLoadingContext.get();

        context.registerConfig(ModConfig.Type.COMMON, CONFIG_SPEC);
        EFFECT_REGISTER.register(modBus);

        forgeBus.addListener(EventPriority.LOWEST, (LivingAttackEvent event) -> {
            if (event.isCanceled() || nextInt(0, 101) < POSSIBILITY.get()) return;
            LivingEntity entity = event.getEntityLiving();
            if (entity.level.isClientSide || entity.hasEffect(DESINFECTION.get())) return;
            Entity source = event.getSource().getDirectEntity();
            if (entity instanceof Player && (source instanceof Zombie || (source != null && INFECTION_SOURCES.get().contains(source.getType())))) {
                entity.addEffect(new MobEffectInstance(ZOMBIFICATION.get(), nextInt(MIN.get(), MAX.get() + 1)));
            }
        });

        forgeBus.addListener(EventPriority.LOWEST, (PotionEvent.PotionExpiryEvent event) -> {
            if (event.isCanceled()) return;
            MobEffectInstance effectInstance = event.getPotionEffect();
            LivingEntity entity = event.getEntityLiving();
            if (entity.level.isClientSide) return;
            if (effectInstance != null && effectInstance.getEffect().equals(ZOMBIFICATION.get())) {
                event.getEntityLiving().hurt(Zombification.getDamageSource(), Float.MAX_VALUE);
            }
        });

        forgeBus.addListener(EventPriority.LOWEST, (LivingEntityUseItemEvent.Finish event) -> {
            if (event.isCanceled()) return;
            ResourceLocation item = event.getItem().getItem().getRegistryName();
            LivingEntity entity = event.getEntityLiving();
            if (item == null || !Objects.equals(item.toString(), CURE_ITEM.get()) || entity.level.isClientSide) return;
            if (entity.hasEffect(ZOMBIFICATION.get())) {
                entity.removeEffect(ZOMBIFICATION.get());
                entity.addEffect(new MobEffectInstance(DESINFECTION.get(), DURATION.get()));
            }
        });
    }

    private static int nextInt(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }
}
