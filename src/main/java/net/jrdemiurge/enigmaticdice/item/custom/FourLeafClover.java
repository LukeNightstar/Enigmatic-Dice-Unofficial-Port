package net.jrdemiurge.enigmaticdice.item.custom;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class FourLeafClover extends Item {

    private static final ResourceLocation LUCK_MODIFIER_ID = EnigmaticDice.location("four_leaf_clover_luck");

    public FourLeafClover(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pLevel.isClientSide && pEntity instanceof Player player) {

            boolean isFirstClover = false;
            for (ItemStack itemStack : player.getInventory().items) {
                if (itemStack.getItem() instanceof FourLeafClover) {
                    if (itemStack == pStack) {
                        isFirstClover = true;
                    }
                    break;
                }
            }

            if (!isFirstClover) return;

            int cloverCount = 0;
            for (ItemStack itemStack : player.getInventory().items) {
                if (itemStack.getItem() instanceof FourLeafClover) {
                    cloverCount += itemStack.getCount();
                }
            }

            AttributeInstance luckAttribute = player.getAttribute(Attributes.LUCK);
            if (luckAttribute != null) {
                AttributeModifier existingModifier = luckAttribute.getModifier(LUCK_MODIFIER_ID);

                if (existingModifier == null) {
                    luckAttribute.addTransientModifier(new AttributeModifier(
                            LUCK_MODIFIER_ID,
                            cloverCount,
                            AttributeModifier.Operation.ADD_VALUE
                    ));
                } else if (existingModifier.amount() != (double) cloverCount) {
                    luckAttribute.removeModifier(LUCK_MODIFIER_ID);
                    luckAttribute.addTransientModifier(new AttributeModifier(
                            LUCK_MODIFIER_ID,
                            cloverCount,
                            AttributeModifier.Operation.ADD_VALUE
                    ));
                }
            }

            Scheduler.schedule(() -> {
                boolean hasClover = false;
                for (ItemStack item : player.getInventory().items) {
                    if (item.getItem() instanceof FourLeafClover) {
                        hasClover = true;
                        break;
                    }
                }

                AttributeInstance luckAttr = player.getAttribute(Attributes.LUCK);
                if (!hasClover && luckAttr != null) {
                    AttributeModifier mod = luckAttr.getModifier(LUCK_MODIFIER_ID);
                    if (mod != null) {
                        luckAttr.removeModifier(LUCK_MODIFIER_ID);
                    }
                }
            }, 10);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.four_leaf_clever"));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
        super.appendHoverText(pStack, pContext, pTooltipComponents, pIsAdvanced);
    }
}
