package net.jrdemiurge.enigmaticdice.sound;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, EnigmaticDice.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> UNEQUAL_EXCHANGE_HIT = registerSoundEvents("unequal_exchange_hit");

    public static final DeferredHolder<SoundEvent, SoundEvent> SOUL_EATER_CHARGED_HIT = registerSoundEvents("soul_eater_charged_hit");

    public static final DeferredHolder<SoundEvent, SoundEvent> PERMAFROST_FROZEN = registerSoundEvents("permafrost_frozen");

    public static final DeferredHolder<SoundEvent, SoundEvent> PERMAFROST_UNFROZEN = registerSoundEvents("permafrost_unfrozen");

    public static final DeferredHolder<SoundEvent, SoundEvent> CRUCIBLE_OF_RILE_PREATTACK = registerSoundEvents("crucible_of_rile_preattack");

    public static final DeferredHolder<SoundEvent, SoundEvent> CRUCIBLE_OF_RILE_ATTACK = registerSoundEvents("crucible_of_rile_attack");

    public static final DeferredHolder<SoundEvent, SoundEvent> CRUCIBLE_OF_RILE_USE = registerSoundEvents("crucible_of_rile_use");

    public static final DeferredHolder<SoundEvent, SoundEvent> CRUCIBLE_OF_RILE_HELIX = registerSoundEvents("crucible_of_rile_helix");

    public static final DeferredHolder<SoundEvent, SoundEvent> CRUCIBLE_OF_RILE_UNIQUE_KILL = registerSoundEvents("crucible_of_rile_unique_kill");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(EnigmaticDice.location(name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
