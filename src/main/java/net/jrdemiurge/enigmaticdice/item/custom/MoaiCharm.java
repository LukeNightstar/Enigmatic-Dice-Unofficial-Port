package net.jrdemiurge.enigmaticdice.item.custom;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

public class MoaiCharm extends Item implements ICurioItem {

    public MoaiCharm(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    public static boolean isWearingMoaiCharm(LivingEntity livingEntity) {
        return CuriosApi.getCuriosInventory(livingEntity)
                .map(handler -> !handler.findCurios(ModItems.MOAI_CHARM.get()).isEmpty())
                .orElse(false);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, Identifier id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        attributes.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(
                EnigmaticDice.location("moai_charm_knockback_resistance_bonus"),
                Config.MoaiCharmKnockbackResistanceBonus,
                AttributeModifier.Operation.ADD_VALUE
        ));
        return attributes;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, TooltipDisplay pTooltipDisplay, Consumer<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Minecraft.getInstance().hasShiftDown()) {
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.moai_charm"));
        } else {
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
    }
}
