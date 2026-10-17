package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.Optional;

public class AccelerateDayCycleEvent extends RandomEvent {
    public static boolean active;

    public AccelerateDayCycleEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
        }
        if (!(pLevel instanceof ServerLevel serverLevel)) return false;
        if (!serverLevel.getGameRules().get(GameRules.ADVANCE_TIME)) return false;

        // Since 26.1 the day time lives on world clocks shared by all dimensions. Advancing the overworld's
        // clock is synced to every client, so no client-side time handling is needed any more.
        MinecraftServer server = serverLevel.getServer();
        Optional<Holder<WorldClock>> clock = server.overworld().dimensionType().defaultClock();
        if (clock.isEmpty()) return false;

        if (active) return false;
        active = true;

        int multiplier = 40;
        int durationTicks = 20 * 60 * 5;

        Scheduler.schedule(() -> server.clockManager().addTicks(clock.get(), multiplier - 1), 0, 1, durationTicks);

        Scheduler.schedule(() -> active = false, durationTicks);

        MutableComponent message = Component.translatable("enigmaticdice.event.accelerate_day_cycle");
        pPlayer.sendSystemMessage(message);
        return true;
    }
}
