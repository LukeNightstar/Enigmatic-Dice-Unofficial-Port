package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class GoldenRain extends RandomEvent{
    private static final int NUM_GOLDEN_NUGGETS = 128 * 2;
    private static final int RADIUS = 2;
    private static final int DELAY_TICKS = 1;

    public GoldenRain(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel,pPlayer, rarity)) return false;
        }

        BlockPos pos = pPlayer.blockPosition();

        pPlayer.sendSystemMessage(Component.literal("\u00A7eIt looks like rain is coming."));

        for (int i = 0; i < NUM_GOLDEN_NUGGETS; i++) {
            int offsetX = pLevel.getRandom().nextInt(2*RADIUS +1) - RADIUS;
            int offsetZ = pLevel.getRandom().nextInt(2*RADIUS +1) - RADIUS;

            BlockPos spawnPos = pos.offset(offsetX, 12, offsetZ);

            Scheduler.schedule(() -> {
                ItemEntity goldNugget = new ItemEntity(pLevel, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), Items.GOLD_NUGGET.getDefaultInstance());
                pLevel.addFreshEntity(goldNugget);
            }, i * DELAY_TICKS);
        }
        return true;
    }
}
