package net.jrdemiurge.enigmaticdice.item.custom;

import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.effect.ModEffects;
import net.jrdemiurge.enigmaticdice.item.custom.unequalexchange.UnequalExchangeData;
import net.jrdemiurge.enigmaticdice.item.custom.unequalexchange.UnequalExchangeDataStorage;
import net.jrdemiurge.enigmaticdice.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

public class UnequalExchange extends Item {

    public UnequalExchange(Properties pProperties) {
        super(pProperties);
    }

    private static final Identifier HEALTH_DEBUFF_ID = EnigmaticDice.location("unequal_exchange_health_debuff");
    private static final Identifier ATTACK_SPEED_DEBUFF_ID = EnigmaticDice.location("unequal_exchange_attack_speed_debuff");
    private static final Identifier ARMOR_DEBUFF_ID = EnigmaticDice.location("unequal_exchange_armor_debuff");
    private static final Identifier ARMOR_TOUGHNESS_DEBUFF_ID = EnigmaticDice.location("unequal_exchange_armor_toughness_debuff");
    private static final Identifier SPEED_DEBUFF_ID = EnigmaticDice.location("unequal_exchange_speed_debuff");

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide() && attacker instanceof Player player) {
            if (player.getAttackStrengthScale(0.5F) > 0.9F) {
                removeModifierIfExists(player, Attributes.MAX_HEALTH, HEALTH_DEBUFF_ID);
                float targetMaxHealth = target.getMaxHealth();
                float attackerMaxHealth = attacker.getMaxHealth();

                float targetNewHealth = target.getHealth() - targetMaxHealth * (float) Config.UnequalExchangeTargetHealthReduction;
                target.setHealth(Math.max(targetNewHealth, 0.0F));
                if (target.getHealth() <= 0.0F) {
                    target.die(attacker.damageSources().playerAttack(player));
                }

                float attackerNewHealth = attacker.getHealth() - attackerMaxHealth * (float) Config.UnequalExchangePlayerHealthReduction;
                attacker.setHealth(Math.max(attackerNewHealth, 0.0F));
                if (attacker.getHealth() <= 0.0F) {
                    attacker.die(attacker.damageSources().playerAttack(player));
                }

                UnequalExchangeData data = UnequalExchangeDataStorage.get(player);
                data.onHit();
                updateModifiers(player, data);

                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.UNEQUAL_EXCHANGE_HIT.get(), SoundSource.PLAYERS, 0.3F, 1.0F);

            }
        }
        super.hurtEnemy(stack, target, attacker);
    }

    public static void updateModifiers(Player player, UnequalExchangeData data) {
        double reductionFactor = - Config.UnequalExchangeStatDebuff * data.getHitCount();
        double healthReduction = Math.max(reductionFactor, -0.8);

        removeModifierIfExists(player, Attributes.MAX_HEALTH, HEALTH_DEBUFF_ID);
        removeModifierIfExists(player, Attributes.ATTACK_SPEED, ATTACK_SPEED_DEBUFF_ID);
        removeModifierIfExists(player, Attributes.ARMOR, ARMOR_DEBUFF_ID);
        removeModifierIfExists(player, Attributes.ARMOR_TOUGHNESS, ARMOR_TOUGHNESS_DEBUFF_ID);
        removeModifierIfExists(player, Attributes.MOVEMENT_SPEED, SPEED_DEBUFF_ID);

        player.getAttribute(Attributes.MAX_HEALTH).addTransientModifier(new AttributeModifier(
                HEALTH_DEBUFF_ID, healthReduction, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        player.getAttribute(Attributes.ATTACK_SPEED).addTransientModifier(new AttributeModifier(
                ATTACK_SPEED_DEBUFF_ID, reductionFactor, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        player.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(
                ARMOR_DEBUFF_ID, reductionFactor, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        player.getAttribute(Attributes.ARMOR_TOUGHNESS).addTransientModifier(new AttributeModifier(
                ARMOR_TOUGHNESS_DEBUFF_ID, reductionFactor, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        player.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(new AttributeModifier(
                SPEED_DEBUFF_ID, reductionFactor, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }

        player.addEffect(new MobEffectInstance(ModEffects.UNEQUAL_EXCHANGE_DEBUFFS, data.getTimeLeftTicks(), data.getHitCount() - 1));
    }

    private static void removeModifierIfExists(Player player, Holder<Attribute> attribute, Identifier id) {
        if (player.getAttribute(attribute) != null && player.getAttribute(attribute).getModifier(id) != null) {
            player.getAttribute(attribute).removeModifier(id);
        }
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, TooltipDisplay pTooltipDisplay, Consumer<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Minecraft.getInstance().hasShiftDown()) {
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.unequal_exchange_1"));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.unequal_exchange_2", String.valueOf(Config.UnequalExchangeTargetHealthReduction   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.unequal_exchange_3", String.valueOf(Config.UnequalExchangePlayerHealthReduction   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));

            pTooltipComponents.accept(Component.literal(" "));

            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.unequal_exchange_4", Config.UnequalExchangeDebuffDuration)
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.unequal_exchange_5", String.valueOf(Config.UnequalExchangeStatDebuff   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.unequal_exchange_6", String.valueOf(Config.UnequalExchangeStatDebuff   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.unequal_exchange_7", String.valueOf(Config.UnequalExchangeStatDebuff   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.unequal_exchange_8", String.valueOf(Config.UnequalExchangeStatDebuff   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.unequal_exchange_9", String.valueOf(Config.UnequalExchangeStatDebuff   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));
        } else {
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.unequal_exchange_0"));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
        super.appendHoverText(pStack, pContext, pTooltipDisplay, pTooltipComponents, pIsAdvanced);
    }
}
