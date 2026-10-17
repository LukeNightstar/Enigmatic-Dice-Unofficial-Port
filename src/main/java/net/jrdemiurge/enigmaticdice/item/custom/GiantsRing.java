package net.jrdemiurge.enigmaticdice.item.custom;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.attribute.ModAttributes;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.Team;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

public class GiantsRing extends Item implements ICurioItem {

    private static final Identifier STEP_HEIGHT_ID = EnigmaticDice.location("giants_ring_step_height");
    private static final Identifier SIZE_SCALE_ID = EnigmaticDice.location("giants_ring_size_scale");

    private static final AttributeModifier STEP_HEIGHT_BONUS = new AttributeModifier(
            STEP_HEIGHT_ID, 1, AttributeModifier.Operation.ADD_VALUE);
    private static final AttributeModifier SIZE_SCALE_MULTIPLIER = new AttributeModifier(
            SIZE_SCALE_ID, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    private final Map<LivingEntity, Integer> stompCooldowns = new WeakHashMap<>();

    public GiantsRing(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity().level().isClientSide() || !(slotContext.entity() instanceof Player))
            return;
        AttributeInstance stepHeight = slotContext.entity().getAttribute(Attributes.STEP_HEIGHT);
        if (stepHeight != null && stepHeight.hasModifier(STEP_HEIGHT_ID)) {
            stepHeight.removeModifier(STEP_HEIGHT_ID);
        }
        AttributeInstance sizeScale = slotContext.entity().getAttribute(ModAttributes.SIZE_SCALE);
        if (sizeScale != null && sizeScale.hasModifier(SIZE_SCALE_ID)) {
            sizeScale.removeModifier(SIZE_SCALE_ID);
        }
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (slotContext.entity().level().isClientSide() || !(slotContext.entity() instanceof Player))
            return;
        AttributeInstance stepHeight = slotContext.entity().getAttribute(Attributes.STEP_HEIGHT);
        if (stepHeight != null && !stepHeight.hasModifier(STEP_HEIGHT_ID)) {
            stepHeight.addTransientModifier(STEP_HEIGHT_BONUS);
        }
        AttributeInstance sizeScale = slotContext.entity().getAttribute(ModAttributes.SIZE_SCALE);
        if (sizeScale != null && !sizeScale.hasModifier(SIZE_SCALE_ID)) {
            sizeScale.addTransientModifier(SIZE_SCALE_MULTIPLIER);
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity().level().isClientSide() || !(slotContext.entity() instanceof Player))
            return;

        Player player = (Player) slotContext.entity();

        if (!player.isSprinting() || player.isSwimming()) return;

        int currentTick = player.tickCount;

        double totalDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE);

        ItemStack weapon = player.getMainHandItem();
        // In 1.21.1, we can get weapon damage from the item's attributes
        var weaponModifiers = weapon.getAttributeModifiers();
        for (var entry : weaponModifiers.modifiers()) {
            if (entry.attribute().equals(Attributes.ATTACK_DAMAGE) && entry.modifier().operation() == AttributeModifier.Operation.ADD_VALUE) {
                totalDamage -= entry.modifier().amount();
            }
        }

        float radius = 1f;

        AABB playerBox = player.getBoundingBox();

        AABB stompBox = new AABB(
                playerBox.minX - radius, player.getY() - 0.1, playerBox.minZ - radius,
                playerBox.maxX + radius, player.getY() + 0.4, playerBox.maxZ + radius
        );

        List<LivingEntity> victims = player.level().getEntitiesOfClass(
                LivingEntity.class, stompBox,
                e -> e != player && e.isAlive() && e.isPickable()
        );

        for (LivingEntity victim : victims) {
            Integer nextAvailableTick = stompCooldowns.getOrDefault(victim, 0);

            if (currentTick >= nextAvailableTick) {
                double playerVolume = player.getBoundingBox().getXsize() * player.getBoundingBox().getYsize() * player.getBoundingBox().getZsize();
                double victimVolume = victim.getBoundingBox().getXsize() * victim.getBoundingBox().getYsize() * victim.getBoundingBox().getZsize();

                if (playerVolume > victimVolume && checkFriendlyFire(victim, player)) {
                    victim.hurt(player.damageSources().playerAttack(player), (float) totalDamage);
                    stompCooldowns.put(victim, currentTick + 20);
                }
            }
        }
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

    public static boolean isWearingGiantRing(LivingEntity livingEntity) {
        return CuriosApi.getCuriosInventory(livingEntity)
                .map(handler -> !handler.findCurios(ModItems.GIANTS_RING.get()).isEmpty())
                .orElse(false);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, Identifier id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();
        attributes.put(Attributes.MAX_HEALTH, new AttributeModifier(
                EnigmaticDice.location("giants_ring_health_bonus"),
                Config.GiantsRingMaxHealth,
                AttributeModifier.Operation.ADD_VALUE
        ));
        attributes.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(
                EnigmaticDice.location("giants_ring_speed_bonus"),
                Config.GiantsRingSpeedBonus,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        ));
        attributes.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                EnigmaticDice.location("giants_ring_damage_bonus"),
                Config.GiantsRingAttackDamage,
                AttributeModifier.Operation.ADD_VALUE
        ));
        return attributes;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, TooltipDisplay pTooltipDisplay, Consumer<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Minecraft.getInstance().hasShiftDown()) {
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.giants_ring_1"));
            pTooltipComponents.accept(Component.literal(" "));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.giants_ring_2"));
        } else {
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.giants_ring_0"));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
    }
}
