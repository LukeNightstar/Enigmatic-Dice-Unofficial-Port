package net.jrdemiurge.enigmaticdice.item.custom;

import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.effect.ModEffects;
import net.jrdemiurge.enigmaticdice.item.custom.souleater.SoulEaterData;
import net.jrdemiurge.enigmaticdice.item.custom.souleater.SoulEaterDataStorage;
import net.jrdemiurge.enigmaticdice.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SoulEater extends SwordItem {

    public SoulEater(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pProperties.attributes(SwordItem.createAttributes(pTier, pAttackDamageModifier, pAttackSpeedModifier)));
    }

    private static final ResourceLocation SOUL_EATER_HEALTH_BUFF_ID = EnigmaticDice.location("soul_eater_bonus_health");
    private static final Map<UUID, Long> lastAbilityUseTime = new HashMap<>();

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide && attacker instanceof Player player) {
            String dataKey = "soul_eater_spent_health_sum";
            float usedHp = attacker.getPersistentData().getFloat(dataKey);
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
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack item = pPlayer.getItemInHand(pUsedHand);
        String dataKey = "soul_eater_spent_health_sum";
        pPlayer.getPersistentData().putFloat(dataKey, 0F);
        pPlayer.startUsingItem(pUsedHand);
        return InteractionResultHolder.consume(item);
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        if (!pLevel.isClientSide && pRemainingUseDuration % 10 == 0) {
            String dataKey = "soul_eater_spent_health_sum";

            float userNewHealth = pLivingEntity.getHealth() - pLivingEntity.getMaxHealth() * 0.2F;
            userNewHealth = Math.max(userNewHealth, 1.0F);

            float spentHealth = pLivingEntity.getHealth() - userNewHealth;
            pLivingEntity.setHealth(userNewHealth);

            float spentHealthSum = pLivingEntity.getPersistentData().getFloat(dataKey) + spentHealth;
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
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (livingEntity instanceof Player player) {
            player.getCooldowns().addCooldown(this, 40);
        }
        super.releaseUsing(stack, level, livingEntity, timeCharged);
    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity pEntity) {
        return 100;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.NONE;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.soul_eater_1"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.soul_eater_2"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.soul_eater_3", Config.SoulEaterChargedAttackDamagePerHP)
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.soul_eater_4"));

            pTooltipComponents.add(Component.literal(" "));

            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.soul_eater_5"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.soul_eater_6", String.valueOf(Config.SoulEaterHealPercentOnKill   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.soul_eater_7", String.valueOf(Config.SoulEaterMaxHealthStealPercent   * 100) + "%", Config.SoulEaterMaxHealthBuffDuration/60D, String.valueOf(Config.SoulEaterMaxHealthMultiplierLimit   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));

        } else {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.soul_eater_0"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
        super.appendHoverText(pStack, pContext, pTooltipComponents, pIsAdvanced);
    }
}
