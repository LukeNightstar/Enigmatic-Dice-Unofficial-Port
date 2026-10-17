package net.jrdemiurge.enigmaticdice.stat;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModStats {
    private static final DeferredRegister<Identifier> CUSTOM_STATS =
            DeferredRegister.create(Registries.CUSTOM_STAT, EnigmaticDice.MOD_ID);

    public static final Identifier OBTAINED_DICE_FROM_BLOCK =
            Identifier.fromNamespaceAndPath(EnigmaticDice.MOD_ID, "obtained_dice_from_block");
    public static final Identifier OBTAINED_DICE_FROM_MOB =
            Identifier.fromNamespaceAndPath(EnigmaticDice.MOD_ID, "obtained_dice_from_mob");

    static {
        CUSTOM_STATS.register("obtained_dice_from_block", () -> OBTAINED_DICE_FROM_BLOCK);
        CUSTOM_STATS.register("obtained_dice_from_mob", () -> OBTAINED_DICE_FROM_MOB);
    }

    public static void register(IEventBus modEventBus) {
        CUSTOM_STATS.register(modEventBus);
    }
}
