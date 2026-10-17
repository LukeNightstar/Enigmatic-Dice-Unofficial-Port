package net.jrdemiurge.enigmaticdice.item.custom;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie.RandomEventManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.world.InteractionResult;

public class EnigmaticDie extends Item {

    public EnigmaticDie(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (EnigmaticDice.eventManager == null) EnigmaticDice.eventManager = new RandomEventManager();

        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide()) {
            return InteractionResult.PASS;
        }

        EnigmaticDice.eventManager.triggerRandomEvent(pLevel, pPlayer);

        itemStack.shrink(1);
        pPlayer.getCooldowns().addCooldown(ModItems.ENIGAMTIC_DIE.toStack(), 20);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, TooltipDisplay pTooltipDisplay, Consumer<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Minecraft.getInstance().hasShiftDown()) {
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.enigmatic_die_2"));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.enigmatic_die_3"));
        } else {
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.enigmatic_die"));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
        super.appendHoverText(pStack, pContext, pTooltipDisplay, pTooltipComponents, pIsAdvanced);
    }
}
