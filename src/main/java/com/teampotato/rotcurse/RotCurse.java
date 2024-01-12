package com.teampotato.rotcurse;

import com.google.common.base.Suppliers;
import com.teampotato.rotcurse.effects.Blessing;
import com.teampotato.rotcurse.effects.Zombification;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
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

@Mod(RotCurse.MOD_ID)
public class RotCurse {
    public static final String MOD_ID = "rotcurse";
    private static final DeferredRegister<MobEffect> EFFECT_REGISTER = DeferredRegister.create(ForgeRegistries.POTIONS, MOD_ID);
    private static final RegistryObject<Blessing> BLESSING = EFFECT_REGISTER.register("zombification", Blessing::new);
    private static final RegistryObject<Zombification> ZOMBIFICATION = EFFECT_REGISTER.register("zombification", Zombification::new);

    private static final ForgeConfigSpec CONFIG_SPEC;
    public static final ForgeConfigSpec.ConfigValue<String> CURE_ITEM;
    public static final ForgeConfigSpec.IntValue MIN, MAX, POSSIBILITY, DURATION;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> UNREMOVEABLE_EFFECTS_LIST;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("RotCurse");
        CURE_ITEM = builder.define("TheItemUsedToCureTheEffect", "minecraft:golden_apple");
        MIN = builder.defineInRange("MinimalTicksOfZombificationEffectOnUndeadAttack", 180, 0, Integer.MAX_VALUE);
        MAX = builder.defineInRange("MaximumTicksOfZombificationEffectOnUndeadAttack", 360, 0, Integer.MAX_VALUE);
        DURATION = builder.defineInRange("TicksDurationOfBlessingEffect", 600, 0, Integer.MAX_VALUE);
        POSSIBILITY = builder.defineInRange("InfectedPossibilityPercentage", 60, 0, 100);
        UNREMOVEABLE_EFFECTS_LIST = builder.defineList("EffectsThatWillNotBeRemovedByMilkCure", new ArrayList<>(), o -> o instanceof String);
        builder.pop();
        CONFIG_SPEC = builder.build();
    }

    public static final Supplier<Set<MobEffect>> UNREMOVEABLE_EFFECTS = Suppliers.memoize(() -> UNREMOVEABLE_EFFECTS_LIST.get().stream().map(string -> ForgeRegistries.POTIONS.getValue(new ResourceLocation(string))).collect(Collectors.toSet()));

    public RotCurse() {
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext context = ModLoadingContext.get();

        context.registerConfig(ModConfig.Type.COMMON, CONFIG_SPEC);
        EFFECT_REGISTER.register(modBus);

        forgeBus.addListener(EventPriority.LOWEST, (LivingAttackEvent event) -> {
            if (event.isCanceled() || nextInt(0, 101) < POSSIBILITY.get()) return;
            LivingEntity entity = event.getEntityLiving();
            if (entity.level.isClientSide || entity.hasEffect(BLESSING.get())) return;
            Entity source = event.getSource().getDirectEntity();
            if (entity instanceof Player && source instanceof Mob && ((Mob) source).getMobType().equals(MobType.UNDEAD)) {
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
                entity.addEffect(new MobEffectInstance(BLESSING.get(), DURATION.get()));
            }
        });
    }

    private static int nextInt(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }
}
