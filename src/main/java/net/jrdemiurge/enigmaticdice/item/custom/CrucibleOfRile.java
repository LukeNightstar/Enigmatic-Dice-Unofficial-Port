package net.jrdemiurge.enigmaticdice.item.custom;

import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.effect.ModEffects;
import net.jrdemiurge.enigmaticdice.network.LookAtTargetPacket;
import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.jrdemiurge.enigmaticdice.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.Team;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.world.InteractionResult;

public class CrucibleOfRile extends Item {
    public static final String PDATA_RILE_HITS = "enigmaticdice_crucible_of_rile_hits";
    public static final String TAG_UNIQUE_KILLS = "enigmaticdice_crucible_of_rile_unique_kills";
    private static final Identifier ARMOR_MODIFIER_ID = EnigmaticDice.location("crucible_of_rile_armor");
    private static final Identifier ARMOR_TOUGHNESS_MODIFIER_ID = EnigmaticDice.location("crucible_of_rile_armor_toughness");
    private static final Identifier ARMOR_BOOST_ID = EnigmaticDice.location("crucible_of_rile_use_armor_buff");
    private static final Identifier TOUGHNESS_BOOST_ID = EnigmaticDice.location("crucible_of_rile_use_armor_toughness_buff");

    public CrucibleOfRile(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide() && attacker instanceof Player player) {

            player.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                    ModSounds.CRUCIBLE_OF_RILE_ATTACK.get(), SoundSource.PLAYERS, 0.5F, 1.0F);

            if (target.getHealth() <= Config.CrucibleOfRileExecuteThreshold) {
                target.setHealth(0);
                if (target.getHealth() <= 0) {
                    target.getPersistentData().putBoolean("enigmaticdice_should_die", true);
                    target.die(attacker.damageSources().playerAttack(player));
                }
            }
        }
        super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResult use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack item = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide()) return InteractionResult.PASS;

        aggroMobsAndLockPlayers((ServerLevel) pLevel, (ServerPlayer) pPlayer);

        giveArmorBoost(pPlayer);

        pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                ModSounds.CRUCIBLE_OF_RILE_USE.get(), SoundSource.PLAYERS, 0.5F, 1.0F);

        pPlayer.getCooldowns().addCooldown(item, Config.CrucibleOfRileCooldown);
        return InteractionResult.SUCCESS;
    }

    public static void aggroMobsAndLockPlayers(ServerLevel level, ServerPlayer caster) {
        double radius = Config.CrucibleOfRileAggroRadius;
        int durationTicks = Config.CrucibleOfRileAggroDuration;

        List<Mob> mobs = level.getEntitiesOfClass(
                Mob.class,
                caster.getBoundingBox().inflate(radius),
                m -> m.isAlive() && checkFriendlyFire(m, caster)
        );

        for (Mob mob : mobs) {
            mob.setTarget(caster);
        }

        if (Config.CrucibleOfRileAggroPlayers) {
            List<ServerPlayer> victims = level.getEntitiesOfClass(
                    ServerPlayer.class,
                    caster.getBoundingBox().inflate(radius),
                    sp -> sp != caster && sp.isAlive() && checkFriendlyFire(sp, caster)
            );

            for (ServerPlayer sp : victims) {
                PacketDistributor.sendToPlayer(sp, new LookAtTargetPacket(caster.getUUID(), durationTicks));
            }
        }
    }

    public static void giveArmorBoost(Player player) {
        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        AttributeInstance toughness = player.getAttribute(Attributes.ARMOR_TOUGHNESS);

        float effectValue = (float) Config.CrucibleOfRileArmorBuffValue;
        int buffDuration = Config.CrucibleOfRileArmorBuffDuration;

        if (armor == null || toughness == null) return;

        AttributeModifier armorBoost = new AttributeModifier(
                ARMOR_BOOST_ID,
                effectValue,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        AttributeModifier toughnessBoost = new AttributeModifier(
                TOUGHNESS_BOOST_ID,
                effectValue,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );

        if (!armor.hasModifier(ARMOR_BOOST_ID)) armor.addTransientModifier(armorBoost);
        if (!toughness.hasModifier(TOUGHNESS_BOOST_ID)) toughness.addTransientModifier(toughnessBoost);

        Scheduler.schedule(() -> {
            AttributeInstance a = player.getAttribute(Attributes.ARMOR);
            AttributeInstance t = player.getAttribute(Attributes.ARMOR_TOUGHNESS);

            if (a != null) a.removeModifier(ARMOR_BOOST_ID);
            if (t != null) t.removeModifier(TOUGHNESS_BOOST_ID);
        }, buffDuration);

        player.addEffect(new MobEffectInstance(ModEffects.CRUCIBLE_OF_RILE_ARMOR_BOOST, buffDuration, 0));
    }

    public static boolean checkFriendlyFire(LivingEntity target, LivingEntity attacker) {
        Team attackerTeam = attacker.getTeam();
        Team entityTeam = target.getTeam();
        if (entityTeam != null && attackerTeam == entityTeam && !attackerTeam.isAllowFriendlyFire()) {
            return false;
        } else {
            if (target instanceof OwnableEntity tameable && tameable.getOwner() != null) {
                LivingEntity owner = tameable.getOwner();
                if (owner == attacker) {
                    return false;
                }
                Team ownerTeam = owner.getTeam();
                if (ownerTeam != null && attackerTeam == ownerTeam && !attackerTeam.isAllowFriendlyFire()) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean isHeldMainHand(LivingEntity livingEntity) {
        return livingEntity.getMainHandItem().getItem() instanceof CrucibleOfRile;
    }

    public static void handleOnOwnerAttacked(LivingEntity livingEntity) {
        CompoundTag data = livingEntity.getPersistentData();
        int hits = data.getIntOr(PDATA_RILE_HITS, 0) + 1;

        if (hits >= Config.CrucibleOfRileHitsForCounterattack) {
            data.putInt(PDATA_RILE_HITS, 0);
            doAoEDamage(livingEntity);
        } else {
            data.putInt(PDATA_RILE_HITS, hits);
        }
    }

    private static void doAoEDamage(LivingEntity livingEntity) {
        livingEntity.level().playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                ModSounds.CRUCIBLE_OF_RILE_HELIX.get(), SoundSource.PLAYERS, 0.5F, 1.0F);

        AABB box = livingEntity.getBoundingBox().inflate(Config.CrucibleOfRileCounterattackRadius);

        List<LivingEntity> targets = livingEntity.level().getEntitiesOfClass(
                LivingEntity.class, box,
                e -> e.isAlive() && e != livingEntity  && checkFriendlyFire(e, livingEntity)
        );

        float dmg = (float) livingEntity.getAttributeValue(Attributes.ATTACK_DAMAGE);

        DamageSource src;
        if (livingEntity instanceof Player player) {
            src = livingEntity.damageSources().playerAttack(player);
        } else {
            src = livingEntity.damageSources().mobAttack(livingEntity);
        }

        for (LivingEntity t : targets) {
            t.hurt(src, dmg);
        }
    }

    public static Set<String> getUniqueKills(ItemStack stack) {
        Set<String> set = new HashSet<>();
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        for (Tag t : tag.getListOrEmpty(TAG_UNIQUE_KILLS)) t.asString().ifPresent(set::add);
        return set;
    }

    public static void setUniqueKills(ItemStack stack, Set<String> ids) {
        ListTag list = new ListTag();
        for (String s : ids) list.add(StringTag.valueOf(s));
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        tag.put(TAG_UNIQUE_KILLS, list);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, TooltipDisplay pTooltipDisplay, Consumer<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Minecraft.getInstance().hasShiftDown()) {
            double crucibleOfRileExecuteThreshold = Config.CrucibleOfRileExecuteThreshold;
            double crucibleOfRileAggroRadius = Config.CrucibleOfRileAggroRadius;
            double crucibleOfRileArmorBuffValue = Config.CrucibleOfRileArmorBuffValue * 100;
            int crucibleOfRileArmorBuffDuration = Config.CrucibleOfRileArmorBuffDuration / 20;
            int crucibleOfRileHitsForCounterattack = Config.CrucibleOfRileHitsForCounterattack;
            double crucibleOfRileCounterattackRadius = Config.CrucibleOfRileCounterattackRadius;
            double crucibleOfRilePermanentArmorPerUnique = Config.CrucibleOfRilePermanentArmorPerUnique * 100;
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.on_use"));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.crucible_of_rile_1", crucibleOfRileAggroRadius)
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.crucible_of_rile_2", crucibleOfRileArmorBuffValue, crucibleOfRileArmorBuffDuration)
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.accept(Component.literal(" "));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.crucible_of_rile_3", crucibleOfRileHitsForCounterattack)
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.crucible_of_rile_4", crucibleOfRileCounterattackRadius)
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.accept(Component.literal(" "));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.on_hit"));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.crucible_of_rile_5", crucibleOfRileExecuteThreshold)
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.accept(Component.literal(" "));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.crucible_of_rile_6"));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.crucible_of_rile_7", crucibleOfRilePermanentArmorPerUnique)
                    .withStyle(ChatFormatting.GOLD));
        } else {
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
        super.appendHoverText(pStack, pContext, pTooltipDisplay, pTooltipComponents, pIsAdvanced);
    }
}
