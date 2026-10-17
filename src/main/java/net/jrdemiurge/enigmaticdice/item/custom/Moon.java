package net.jrdemiurge.enigmaticdice.item.custom;

import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.ai.attributes.Attributes;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import org.jspecify.annotations.Nullable;

public class Moon extends Item {

    private static final Identifier GRAVITY_MODIFIER_ID = EnigmaticDice.location("moon_gravity_reduction");

    public Moon(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void inventoryTick(ItemStack pStack, ServerLevel pLevel, Entity pEntity, @Nullable EquipmentSlot pSlot) {
        if (!pLevel.isClientSide() && pEntity instanceof Player player) {
            boolean isSneaking = player.isShiftKeyDown();
            boolean isInMainHand = player.getMainHandItem().is(ModItems.MOON.get());
            boolean isInOffHand = player.getOffhandItem().is(ModItems.MOON.get());
            AttributeInstance gravityAttribute = player.getAttribute(Attributes.GRAVITY);

            if (gravityAttribute != null) {
                AttributeModifier existingModifier = gravityAttribute.getModifier(GRAVITY_MODIFIER_ID);

                if (isInMainHand || isInOffHand) {
                    if (!isSneaking) {
                        if (existingModifier == null) {
                            gravityAttribute.addTransientModifier(new AttributeModifier(
                                    GRAVITY_MODIFIER_ID,
                                    Config.MoonGravityReduction,
                                    AttributeModifier.Operation.ADD_VALUE
                            ));
                        }
                    } else {
                        if (existingModifier != null) {
                            gravityAttribute.removeModifier(GRAVITY_MODIFIER_ID);
                        }
                    }
                }
            }

            Scheduler.schedule(() -> {
                boolean stillHolding = player.getMainHandItem().is(ModItems.MOON.get()) || player.getOffhandItem().is(ModItems.MOON.get());
                AttributeInstance gravityAttributeLater = player.getAttribute(Attributes.GRAVITY);

                if (!stillHolding && gravityAttributeLater != null) {
                    AttributeModifier modifier = gravityAttributeLater.getModifier(GRAVITY_MODIFIER_ID);
                    if (modifier != null) {
                        gravityAttributeLater.removeModifier(GRAVITY_MODIFIER_ID);
                    }
                }
            }, 4);
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, TooltipDisplay pTooltipDisplay, Consumer<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Minecraft.getInstance().hasShiftDown()) {
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.while_held"));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.moon_1"));
            pTooltipComponents.accept(Component.literal(" "));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.while_hotbar"));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.moon_2"));
            pTooltipComponents.accept(Component.literal(" "));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.moon_3"));
        } else {
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
        super.appendHoverText(pStack, pContext, pTooltipDisplay, pTooltipComponents, pIsAdvanced);
    }
}
