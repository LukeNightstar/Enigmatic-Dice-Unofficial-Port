package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


public class BroodAwakensEvent extends RandomEvent {

    public BroodAwakensEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
        }

        Vec3 lookVec = pPlayer.getLookAngle();
        lookVec = new Vec3(lookVec.x, 0, lookVec.z).normalize();

        Vec3 spawnPos = pPlayer.position().add(lookVec.scale(5)).add(0, 2, 0);

        if (!pLevel.getBlockState(BlockPos.containing(spawnPos)).getCollisionShape(pLevel, BlockPos.containing(spawnPos)).isEmpty()) {
            return false;
        }

        // Try to get the OBSESSION effect from Born in Chaos mod via registry
        var obsessionEffect = BuiltInRegistries.MOB_EFFECT.getHolder(Identifier.parse("born_in_chaos_v1:obsession"));

        for (int i = 0; i < 2; i++) {
            EntityType<?> entityType = EntityType.byString("born_in_chaos_v1:swarmer").orElse(null);
            if (entityType == null) return false;

            Entity entity = entityType.create(pLevel);
            if (entity == null) return false;

            entity.snapTo(spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);
            pLevel.addFreshEntity(entity);

            if (entity instanceof Mob mob) {
                mob.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);

                mob.addEffect(new MobEffectInstance(
                        MobEffects.WITHER,
                        Integer.MAX_VALUE,
                        4,
                        false,
                        false
                ));
                obsessionEffect.ifPresent(effect -> mob.addEffect(new MobEffectInstance(
                        effect,
                        Integer.MAX_VALUE,
                        0,
                        false,
                        false
                )));
            }
        }

        for (int i = 0; i < 2; i++) {
            EntityType<?> entityType = EntityType.byString("born_in_chaos_v1:mother_spider").orElse(null);
            if (entityType == null) return false;

            Entity entity = entityType.create(pLevel);
            if (entity == null) return false;

            entity.snapTo(spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);
            pLevel.addFreshEntity(entity);
        }

        MutableComponent message = Component.translatable("enigmaticdice.event.brood_awakens");
        pPlayer.sendSystemMessage(message);
        return true;
    }
}
