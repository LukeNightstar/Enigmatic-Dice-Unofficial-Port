package net.jrdemiurge.enigmaticdice.event;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.custom.CrucibleOfRile;
import net.jrdemiurge.enigmaticdice.item.custom.DivineShield;
import net.jrdemiurge.enigmaticdice.item.custom.RingOfAgility;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class LivingAttackHandler {

    @SubscribeEvent
    public static void onPlayerHurt(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide()) return;

        if (RingOfAgility.isWearingRingOfAgility(entity) && RingOfAgility.shouldDodge(entity)) {
            event.setCanceled(true);
            return;
        }

        if (DivineShield.isWearingDivineShield(entity)) {
            /*if (DivineShield.hasActiveImmunity(entity)) {
                event.setCanceled(true);
                return;
            }*/
            if (!DivineShield.isOnCooldown(entity)) {
                event.setCanceled(true);
                entity.invulnerableTime = 20;
                // DivineShield.giveImmunity(entity);
                DivineShield.triggerCooldown(entity);
                return;
            }
        }
    }
}
