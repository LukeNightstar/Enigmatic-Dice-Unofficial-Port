package net.jrdemiurge.enigmaticdice.item.custom;

import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.effect.ModEffects;
import net.jrdemiurge.enigmaticdice.item.custom.souleater.SoulEaterData;
import net.jrdemiurge.enigmaticdice.item.custom.souleater.SoulEaterDataStorage;
import net.jrdemiurge.enigmaticdice.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.world.InteractionResult;

public class SoulEater extends Item {

    public SoulEater(Properties pProperties) {
        super(pProperties);
    }

    private static final Identifier SOUL_EATER_HEALTH_BUFF_ID = EnigmaticDice.location("soul_eater_bonus_health");
    private static final Map<UUID, Long> lastAbilityUseTime = new HashMap<>();

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide() && attacker instanceof Player player) {
            String dataKey = "soul_eater_spent_health_sum";
            float usedHp = attacker.getPersistentData().getFloatOr(dataKey, 0.0F);
            float magicDamage = usedHp * (float) Config.SoulEaterChargedAttackDamagePerHP;
            long elapsedTime = attacker.level().getGameTime() - lastAbilityUseTime.getOrDefault(player.getUUID(), -1L);

            if (magicDamage > 0F && elapsedTime < 20 * (long) Config.SoulEaterChargeDuration) {
                target.invulnerableTime = 0;
                target.hurt(attacker.damageSources().indirectMagic(attacker, attacker), magicDamage);
                attacker.getPersistentData().putFloat(dataKey, 0F);
                attacker.removeEffect(ModEffects.SOUL_EATER_CHARGED_HIT);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.SOUL_EATER_CHARGED_HIT.get(), SoundSource.PLAYERS, 1F, 1.0F);
                if (target.getHealth() <= 0F){
                    attacker.heal(usedHp / 2);
                }
            }
            if (target.getHealth() <= 0F){
                attacker.heal(target.getMaxHealth() * (float) Config.SoulEaterHealPercentOnKill);

                var attr = player.getAttribute(Attributes.MAX_HEALTH);
                if (attr != null) {
                    if (attr.getModifier(SOUL_EATER_HEALTH_BUFF_ID) != null) {
                        attr.removeModifier(SOUL_EATER_HEALTH_BUFF_ID);
                    } else {
                        SoulEaterDataStorage.get(player).reset();
                    }

                    SoulEaterData data = SoulEaterDataStorage.get(player);
                    float bonusHealth = target.getMaxHealth() * (float) Config.SoulEaterMaxHealthStealPercent;
                    float newBonusHealth = Math.min(bonusHealth + data.getHpCount(), player.getMaxHealth() * (float) Config.SoulEaterMaxHealthMultiplierLimit);
                    data.onKill(newBonusHealth);

                    attr.addTransientModifier(new AttributeModifier(
                            SOUL_EATER_HEALTH_BUFF_ID,
                            newBonusHealth,
                            AttributeModifier.Operation.ADD_VALUE
                    ));

                    attacker.addEffect(new MobEffectInstance(ModEffects.SOUL_EATER_HEALTH_BOOST, 20 * Config.SoulEaterMaxHealthBuffDuration, 0));
                }
            }
        }
        super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResult use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack item = pPlayer.getItemInHand(pUsedHand);
        String dataKey = "soul_eater_spent_health_sum";
        pPlayer.getPersistentData().putFloat(dataKey, 0F);
        pPlayer.startUsingItem(pUsedHand);
        return InteractionResult.CONSUME;
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        if (!pLevel.isClientSide() && pRemainingUseDuration % 10 == 0) {
            String dataKey = "soul_eater_spent_health_sum";

            float userNewHealth = pLivingEntity.getHealth() - pLivingEntity.getMaxHealth() * 0.2F;
            userNewHealth = Math.max(userNewHealth, 1.0F);

            float spentHealth = pLivingEntity.getHealth() - userNewHealth;
            pLivingEntity.setHealth(userNewHealth);

            float spentHealthSum = pLivingEntity.getPersistentData().getFloatOr(dataKey, 0.0F) + spentHealth;
            pLivingEntity.getPersistentData().putFloat(dataKey, spentHealthSum);
            lastAbilityUseTime.put(pLivingEntity.getUUID(), pLevel.getGameTime());

            pLivingEntity.removeEffect(ModEffects.SOUL_EATER_CHARGED_HIT);
            if (spentHealthSum > 0F) {
                pLivingEntity.addEffect(new MobEffectInstance(ModEffects.SOUL_EATER_CHARGED_HIT, 20 * Config.SoulEaterChargeDuration, (int) (spentHealthSum - 1)));
            }
            if (userNewHealth <= 1.0F && pLivingEntity instanceof Player player) {
                player.stopUsingItem();
            }
        }
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (livingEntity instanceof Player player) {
            player.getCooldowns().addCooldown(stack, 40);
        }
        return super.releaseUsing(stack, level, livingEntity, timeCharged);
    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity pEntity) {
        return 100;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack pStack) {
        return ItemUseAnimation.NONE;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, TooltipDisplay pTooltipDisplay, Consumer<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Minecraft.getInstance().hasShiftDown()) {
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.soul_eater_1"));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.soul_eater_2"));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.soul_eater_3", Config.SoulEaterChargedAttackDamagePerHP)
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.soul_eater_4"));

            pTooltipComponents.accept(Component.literal(" "));

            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.soul_eater_5"));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.soul_eater_6", String.valueOf(Config.SoulEaterHealPercentOnKill   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.soul_eater_7", String.valueOf(Config.SoulEaterMaxHealthStealPercent   * 100) + "%", Config.SoulEaterMaxHealthBuffDuration/60D, String.valueOf(Config.SoulEaterMaxHealthMultiplierLimit   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));

        } else {
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.soul_eater_0"));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
        super.appendHoverText(pStack, pContext, pTooltipDisplay, pTooltipComponents, pIsAdvanced);
    }
}
