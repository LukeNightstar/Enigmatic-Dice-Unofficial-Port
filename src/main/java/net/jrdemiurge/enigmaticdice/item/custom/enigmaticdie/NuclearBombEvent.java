package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class NuclearBombEvent extends RandomEvent {

    public NuclearBombEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
        }

        Vec3 lookVec = pPlayer.getLookAngle();
        lookVec = new Vec3(lookVec.x, 0, lookVec.z).normalize();

        Vec3 spawnPos = pPlayer.position().add(lookVec.scale(2.5)).add(0, 2, 0);

        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse("alexscaves:nuclear_bomb"));
        if (entityType == null || entityType == EntityType.PIG) return false;

        Scheduler.schedule(() -> {
            Entity entity = entityType.create(pLevel);
            if (entity != null) {
                entity.snapTo(spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);
                pLevel.addFreshEntity(entity);
                pLevel.playSound(null, BlockPos.containing(spawnPos), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.2F, 1.0F);
            }
        }, 20);

        if (pPlayer instanceof ServerPlayer serverPlayer) {
            Component title = Component.literal("RUN").withStyle(ChatFormatting.DARK_RED);
            serverPlayer.connection.send(new ClientboundSetTitleTextPacket(title));
            serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(0, 20, 0));
        }
        return true;
    }
}
