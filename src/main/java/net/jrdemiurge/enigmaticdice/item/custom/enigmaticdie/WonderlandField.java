package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import net.minecraft.world.entity.EntitySpawnReason;
import net.jrdemiurge.enigmaticdice.util.EntityNbt;


public class WonderlandField extends RandomEvent {
    private static final int MIMIC_COUNT = 10;
    private static final int SPAWN_RADIUS = 30;

    public WonderlandField(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean  execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
        }

        EntityType<?> entityType = EntityType.byString("artifacts:mimic").orElse(null);
        if (entityType == null) return false;

        Random random = new Random();

        for (int i = 0; i < MIMIC_COUNT; i++) {
            Vec3 spawnPos = findValidSpawnPosition(pLevel, pPlayer, random);

            Entity entity = entityType.create(pLevel, EntitySpawnReason.EVENT);
            if (entity == null) continue;

            entity.snapTo(spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);

            pLevel.addFreshEntity(entity);

            Scheduler.schedule(() -> {
                if (entity instanceof Mob mob) {
                    mob.setNoAi(true);
                    EntityNbt.edit(mob, entityData -> entityData.putString("DeathLootTable", "enigmaticdice:entities/mimic_loot_table"));
                    mob.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(0);
                }
            }, 20);

        }

        Vec3 spawnPos = findValidSpawnPosition(pLevel, pPlayer, random);

        Entity entity = entityType.create(pLevel, EntitySpawnReason.EVENT);
        if (entity == null) return false;

        entity.snapTo(spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);

        if (entity instanceof Mob mob) {
            mob.setNoAi(true);
            mob.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, MobEffectInstance.INFINITE_DURATION, 0, false, true));
        }

        pLevel.addFreshEntity(entity);

        MutableComponent message = Component.translatable("enigmaticdice.event.wonderland_field." + pLevel.getRandom().nextInt(3));
        pPlayer.sendSystemMessage(message);
        return true;
    }

    private Vec3 findValidSpawnPosition(Level level, Player player, Random random) {
        double angle = random.nextDouble() * Math.PI * 2;
        double distance = SPAWN_RADIUS * (0.15 + 0.85 * random.nextDouble());
        int x = (int) (player.getX() + Math.cos(angle) * distance);
        int z = (int) (player.getZ() + Math.sin(angle) * distance);
        BlockPos surfacePos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(x, 0, z));

        return new Vec3(surfacePos.getX() + 0.5, surfacePos.getY(), surfacePos.getZ() + 0.5);
    }
}
