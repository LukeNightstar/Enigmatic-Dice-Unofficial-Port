package net.jrdemiurge.enigmaticdice.event;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.custom.UnequalExchange;
import net.jrdemiurge.enigmaticdice.item.custom.souleater.SoulEaterDataStorage;
import net.jrdemiurge.enigmaticdice.item.custom.unequalexchange.UnequalExchangeDataStorage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class PlayerTickHandler {

    //unequal exchange
    private static final ResourceLocation HEALTH_DEBUFF_ID = ResourceLocation.fromNamespaceAndPath("enigmaticdice", "health_debuff");
    private static final ResourceLocation ATTACK_SPEED_DEBUFF_ID = ResourceLocation.fromNamespaceAndPath("enigmaticdice", "attack_speed_debuff");
    private static final ResourceLocation ARMOR_DEBUFF_ID = ResourceLocation.fromNamespaceAndPath("enigmaticdice", "armor_debuff");
    private static final ResourceLocation ARMOR_TOUGHNESS_DEBUFF_ID = ResourceLocation.fromNamespaceAndPath("enigmaticdice", "armor_toughness_debuff");
    private static final ResourceLocation SPEED_DEBUFF_ID = ResourceLocation.fromNamespaceAndPath("enigmaticdice", "speed_debuff");
    //soul eater
    private static final ResourceLocation SOUL_EATER_HEALTH_BUFF_ID = ResourceLocation.fromNamespaceAndPath("enigmaticdice", "soul_eater_health_buff");

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide) {
            // unequal exchange
            var data = UnequalExchangeDataStorage.get(player);
            data.tick();

            if (data.isExpired()) {
                if (player.getAttribute(Attributes.MAX_HEALTH).getModifier(HEALTH_DEBUFF_ID) != null) {
                    player.getAttribute(Attributes.MAX_HEALTH).removeModifier(HEALTH_DEBUFF_ID);
                }
                if (player.getAttribute(Attributes.ATTACK_SPEED).getModifier(ATTACK_SPEED_DEBUFF_ID) != null) {
                    player.getAttribute(Attributes.ATTACK_SPEED).removeModifier(ATTACK_SPEED_DEBUFF_ID);
                }
                if (player.getAttribute(Attributes.ARMOR).getModifier(ARMOR_DEBUFF_ID) != null) {
                    player.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_DEBUFF_ID);
                }
                if (player.getAttribute(Attributes.ARMOR_TOUGHNESS).getModifier(ARMOR_TOUGHNESS_DEBUFF_ID) != null) {
                    player.getAttribute(Attributes.ARMOR_TOUGHNESS).removeModifier(ARMOR_TOUGHNESS_DEBUFF_ID);
                }
                if (player.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(SPEED_DEBUFF_ID) != null) {
                    player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_DEBUFF_ID);
                }
                data.reset();
            }

            if (data.getHitCount() > 0 && player.getAttribute(Attributes.MAX_HEALTH).getModifier(HEALTH_DEBUFF_ID) == null) {
                UnequalExchange.updateModifiers(player, data);
            }

            // soul eater
            var soulEaterData = SoulEaterDataStorage.get(player);
            soulEaterData.tick();

            if (soulEaterData.isExpired()) {
                if (player.getAttribute(Attributes.MAX_HEALTH).getModifier(SOUL_EATER_HEALTH_BUFF_ID) != null) {
                    player.getAttribute(Attributes.MAX_HEALTH).removeModifier(SOUL_EATER_HEALTH_BUFF_ID);

                    if (player.getHealth() > player.getMaxHealth()) {
                        player.setHealth(player.getMaxHealth());
                    }
                }

                soulEaterData.reset();
            }
        }
    }
}
