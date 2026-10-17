package net.jrdemiurge.enigmaticdice.item;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.custom.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EnigmaticDice.MOD_ID);

    public static final DeferredItem<Item> ENIGAMTIC_DIE = ITEMS.register("enigmatic_die",
            () -> new EnigmaticDie(new Item.Properties()));

    public static final DeferredItem<Item> UNEQUAL_EXCHANGE = ITEMS.register("unequal_exchange",
            () -> new UnequalExchange(Tiers.NETHERITE, -4, -2.4F, new Item.Properties().fireResistant().component(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)));

    public static final DeferredItem<Item> SOUL_EATER = ITEMS.register("soul_eater",
            () -> new SoulEater(Tiers.NETHERITE, 5, -2F, new Item.Properties().fireResistant().component(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)));

    public static final DeferredItem<Item> ANTIMATTER = ITEMS.register("antimatter",
            () -> new Antimatter(new Item.Properties()));

    public static final DeferredItem<Item> FOUR_LEAF_CLEVER = ITEMS.register("four_leaf_clever",
            () -> new FourLeafClover(new Item.Properties()));

    public static final DeferredItem<Item> GIANTS_RING = ITEMS.register("giants_ring",
            () -> new GiantsRing(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> MOON_SHARD = ITEMS.register("moon_shard",
            () -> new MoonShard(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> GRAVITY_CORE = ITEMS.register("gravity_core",
            () -> new GravityCore(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> MOON = ITEMS.register("moon",
            () -> new Moon(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> RING_OF_AGILITY = ITEMS.register("ring_of_agility",
            () -> new RingOfAgility(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> DIVINE_SHIELD = ITEMS.register("divine_shield",
            () -> new DivineShield(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> PERMAFROST = ITEMS.register("permafrost",
            () -> new Permafrost(Tiers.NETHERITE, 5, -2F, new Item.Properties().fireResistant().component(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)));

    public static final DeferredItem<Item> CRUCIBLE_OF_RILE = ITEMS.register("crucible_of_rile",
            () -> new CrucibleOfRile(Tiers.NETHERITE, 11, -2.6F, new Item.Properties().fireResistant().component(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)));

    public static final DeferredItem<Item> MOAI_CHARM = ITEMS.register("moai_charm",
            () -> new MoaiCharm(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
