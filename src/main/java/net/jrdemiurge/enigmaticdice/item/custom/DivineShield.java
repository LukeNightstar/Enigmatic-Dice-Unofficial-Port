package net.jrdemiurge.enigmaticdice.item.custom;

import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.effect.ModEffects;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DivineShield extends Item implements ICurioItem {

    private static final Map<UUID, Long> activeImmunity = new HashMap<>();

    public DivineShield(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity().level().isClientSide || !(slotContext.entity() instanceof Player player))
            return;

        player.removeEffect(ModEffects.DIVINE_SHIELD_INVULNERABILITY);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (slotContext.entity().level().isClientSide || !(slotContext.entity() instanceof Player player))
            return;

        player.removeEffect(ModEffects.DIVINE_SHIELD_INVULNERABILITY);
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity().level().isClientSide || !(slotContext.entity() instanceof Player player))
            return;

        if (Config.DivineShieldGiveEffect) {
            if (isOnCooldown(player)) {
                player.removeEffect(ModEffects.DIVINE_SHIELD_INVULNERABILITY);
            } else {
                player.addEffect(new MobEffectInstance(ModEffects.DIVINE_SHIELD_INVULNERABILITY, MobEffectInstance.INFINITE_DURATION, 0, true, true));
            }
        }
    }

    public static boolean isWearingDivineShield(LivingEntity livingEntity) {
        return CuriosApi.getCuriosInventory(livingEntity)
                .map(handler -> !handler.findCurios(ModItems.DIVINE_SHIELD.get()).isEmpty())
                .orElse(false);
    }

    public static boolean isOnCooldown(LivingEntity entity) {
        if (entity instanceof Player player) {
            return player.getCooldowns().isOnCooldown(ModItems.DIVINE_SHIELD.get());
        }
        return false;
    }

    public static void triggerCooldown(LivingEntity entity) {
        if (!(entity instanceof Player player)) return;

        player.getCooldowns().addCooldown(ModItems.DIVINE_SHIELD.get(), Config.DivineShieldCooldownTicks);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Screen.hasShiftDown()) {
            String formattedCooldown = String.format("%.1f", Config.DivineShieldCooldownTicks / 20F);

            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.divine_shield_1", formattedCooldown)
                    .withStyle(ChatFormatting.GOLD));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.divine_shield_0"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
    }
}
