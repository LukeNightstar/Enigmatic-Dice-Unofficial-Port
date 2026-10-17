package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntitySpawnReason;

public class BlackHoleBombEvent extends RandomEvent {

    public BlackHoleBombEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
        }

        BlockPos pos = pPlayer.blockPosition();

        pLevel.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.2F, 1.0F);

        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse("terramity:black_hole_bomb_entity"));
        if (entityType == null || entityType == EntityType.PIG) return false;

        Entity entity = entityType.create(pLevel, EntitySpawnReason.EVENT);
        if (entity == null) return false;

        entity.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        pLevel.addFreshEntity(entity);

        pPlayer.sendSystemMessage(Component.translatable("enigmaticdice.event.black_hole_bomb"));
        return true;
    }
}
