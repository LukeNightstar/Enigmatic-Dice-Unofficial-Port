package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class GiveAncientTomeEvent extends RandomEvent {

    public GiveAncientTomeEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean  execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
        }

        if (!(pLevel instanceof ServerLevel serverLevel)) return false;

        ResourceLocation resourceLocation = ResourceLocation.parse("quark:ancient_tome");
        Item item = BuiltInRegistries.ITEM.get(resourceLocation);
        if (item == null || item == BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace("air"))) {
            EnigmaticDice.LOGGER.error("Item not found: {}", "quark:ancient_tome");
            return false;
        }

        Registry<Enchantment> enchantmentRegistry = serverLevel.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        List<Holder.Reference<Enchantment>> allEnchantments = new ArrayList<>(enchantmentRegistry.holders().toList());
        if (allEnchantments.isEmpty()) {
            EnigmaticDice.LOGGER.warn("No enchantments found in registry.");
            return false;
        }

        Holder<Enchantment> chosenEnchantment = allEnchantments.get(pLevel.random.nextInt(allEnchantments.size()));

        ItemStack itemStack = new ItemStack(item);
        itemStack.enchant(chosenEnchantment, 1);

        ItemEntity keyEntity = new ItemEntity(
                pLevel,
                pPlayer.getX(),
                pPlayer.getY() + 1,
                pPlayer.getZ(),
                itemStack
        );
        pLevel.addFreshEntity(keyEntity);

        MutableComponent message = Component.translatable("enigmaticdice.gift.blue");
        pPlayer.displayClientMessage(message, false);
        return true;
    }
}
