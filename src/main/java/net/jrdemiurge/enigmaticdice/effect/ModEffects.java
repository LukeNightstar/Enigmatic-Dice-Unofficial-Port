package net.jrdemiurge.enigmaticdice.effect;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.effect.custom.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, EnigmaticDice.MOD_ID);

    public static final DeferredHolder<MobEffect, SoulEaterChargedHit> SOUL_EATER_CHARGED_HIT = MOB_EFFECTS.register("soul_eater_charged_hit",
            () -> new SoulEaterChargedHit(0x5084d4));

    public static final DeferredHolder<MobEffect, SoulEaterHealthBoost> SOUL_EATER_HEALTH_BOOST = MOB_EFFECTS.register("soul_eater_health_boost",
            () -> new SoulEaterHealthBoost(0x72cee1));

    public static final DeferredHolder<MobEffect, UnequalExchangeDebuffs> UNEQUAL_EXCHANGE_DEBUFFS = MOB_EFFECTS.register("unequal_exchange_debuffs",
            () -> new UnequalExchangeDebuffs(0x411214));

    public static final DeferredHolder<MobEffect, FrostHit> FROST_HIT = MOB_EFFECTS.register("frost_hit",
            () -> new FrostHit(0x9ec2f6));

    public static final DeferredHolder<MobEffect, FrostAura> FROST_AURA = MOB_EFFECTS.register("frost_aura",
            () -> new FrostAura(0x9ec2f6));

    public static final DeferredHolder<MobEffect, CrucibleOfRileArmorBoost> CRUCIBLE_OF_RILE_ARMOR_BOOST = MOB_EFFECTS.register("crucible_of_rile_armor_boost",
            () -> new CrucibleOfRileArmorBoost(0xc15507));

    public static final DeferredHolder<MobEffect, DivineShieldInvulnerability> DIVINE_SHIELD_INVULNERABILITY = MOB_EFFECTS.register("divine_shield_invulnerability",
            () -> new DivineShieldInvulnerability(0xffee7d));

    public static final DeferredHolder<MobEffect, MobEffect> DAY_ON_THE_MOON = MOB_EFFECTS.register("day_on_the_moon",
            () -> new DayOnTheMoon(0xccccff)
                    .addAttributeModifier(
                            Attributes.GRAVITY,
                            ResourceLocation.fromNamespaceAndPath(EnigmaticDice.MOD_ID, "day_on_the_moon_gravity"),
                            -0.06,
                            AttributeModifier.Operation.ADD_VALUE
                    )
    );

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
