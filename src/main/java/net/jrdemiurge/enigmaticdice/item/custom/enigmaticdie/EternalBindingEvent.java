package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.Optional;


public class EternalBindingEvent extends RandomEvent {
    private static final ResourceLocation ETERNAL_BINDING_ID = ResourceLocation.fromNamespaceAndPath("enigmaticlegacyplus", "eternal_binding_curse");

    public EternalBindingEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
        }

        if (!(pLevel instanceof ServerLevel serverLevel)) return false;

        Optional<Holder.Reference<Enchantment>> eternalBindingOpt = serverLevel.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolder(ETERNAL_BINDING_ID);

        if (eternalBindingOpt.isEmpty()) return false;
        Holder<Enchantment> eternalBinding = eternalBindingOpt.get();

        boolean enchantedAny = false;

        for (EquipmentSlot slot : EnumSet.of(
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) {

            ItemStack itemStack = pPlayer.getItemBySlot(slot);

            if (!itemStack.isEmpty()) {
                ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(itemStack);
                if (enchantments.getLevel(eternalBinding) <= 0) {
                    itemStack.enchant(eternalBinding, 1);
                    enchantedAny = true;
                }
            }
        }

        if (enchantedAny) {
            pPlayer.displayClientMessage(Component.translatable("enigmaticdice.event.eternal_binding"), false);
            return true;
        }

        return false;
    }
}
