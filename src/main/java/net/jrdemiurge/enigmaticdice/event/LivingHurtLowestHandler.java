package net.jrdemiurge.enigmaticdice.event;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.custom.CrucibleOfRile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/*@EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class LivingHurtLowestHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        Entity src = event.getSource().getEntity();
        double dmg = event.getAmount();

        if (entity.level().isClientSide) return;

        if (src instanceof LivingEntity attacker) {
            if (Kyomu.isHeldMainHand(attacker) && !event.isCanceled()) {
                CompoundTag atag = attacker.getPersistentData();
                if (atag.getBoolean(Kyomu.KYOMU_RELEASING)) {
                    return;
                }

                System.out.println(event.getSource().toString());

                CompoundTag tag = entity.getPersistentData();
                CompoundTag map = tag.getCompound(Kyomu.VICTIM_ACCUM_MAP);
                String key = attacker.getUUID().toString();
                double prev = map.contains(key, Tag.TAG_DOUBLE) ? map.getDouble(key) : 0.0;
                map.putDouble(key, prev + dmg);
                tag.put(Kyomu.VICTIM_ACCUM_MAP, map);

                Kyomu.addTargetForAttacker(attacker, entity.getUUID());

                event.setCanceled(true);
            }
        }
    }
}*/
