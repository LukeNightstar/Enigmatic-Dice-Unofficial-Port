package net.jrdemiurge.enigmaticdice.item;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.custom.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EnigmaticDice.MOD_ID);

    public static final DeferredItem<Item> ENIGAMTIC_DIE = ITEMS.registerItem("enigmatic_die", EnigmaticDie::new);

    public static final DeferredItem<Item> UNEQUAL_EXCHANGE = ITEMS.registerItem("unequal_exchange", UnequalExchange::new,
            p -> p.fireResistant().sword(ToolMaterial.NETHERITE, -4F, -2.4F).component(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY));

    public static final DeferredItem<Item> SOUL_EATER = ITEMS.registerItem("soul_eater", SoulEater::new,
            p -> p.fireResistant().sword(ToolMaterial.NETHERITE, 5F, -2F).component(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY));

    public static final DeferredItem<Item> ANTIMATTER = ITEMS.registerItem("antimatter", Antimatter::new);

    public static final DeferredItem<Item> FOUR_LEAF_CLEVER = ITEMS.registerItem("four_leaf_clever", FourLeafClover::new);

    public static final DeferredItem<Item> GIANTS_RING = ITEMS.registerItem("giants_ring", GiantsRing::new, p -> p.stacksTo(1));

    public static final DeferredItem<Item> MOON_SHARD = ITEMS.registerItem("moon_shard", MoonShard::new, p -> p.stacksTo(1));

    public static final DeferredItem<Item> GRAVITY_CORE = ITEMS.registerItem("gravity_core", GravityCore::new, p -> p.stacksTo(1));

    public static final DeferredItem<Item> MOON = ITEMS.registerItem("moon", Moon::new, p -> p.stacksTo(1));

    public static final DeferredItem<Item> RING_OF_AGILITY = ITEMS.registerItem("ring_of_agility", RingOfAgility::new, p -> p.stacksTo(1));

    public static final DeferredItem<Item> DIVINE_SHIELD = ITEMS.registerItem("divine_shield", DivineShield::new, p -> p.stacksTo(1));

    public static final DeferredItem<Item> PERMAFROST = ITEMS.registerItem("permafrost", Permafrost::new,
            p -> p.fireResistant().sword(ToolMaterial.NETHERITE, 5F, -2F).component(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY));

    public static final DeferredItem<Item> CRUCIBLE_OF_RILE = ITEMS.registerItem("crucible_of_rile", CrucibleOfRile::new,
            p -> p.fireResistant().sword(ToolMaterial.NETHERITE, 11F, -2.6F).component(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY));

    public static final DeferredItem<Item> MOAI_CHARM = ITEMS.registerItem("moai_charm", MoaiCharm::new, p -> p.stacksTo(1));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
