package net.jrdemiurge.enigmaticdice.event;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;

@EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class AdvancementHandler {

    @SubscribeEvent
    public static void onAdvancementEarned(AdvancementEvent.AdvancementEarnEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        Level level = player.level();
        AdvancementHolder advancement = event.getAdvancement();
        DisplayInfo display = advancement.value().display().orElse(null);

        if (display != null) {
            AdvancementType frame = display.getType();
            if (frame == AdvancementType.GOAL || frame == AdvancementType.CHALLENGE) {
                ItemStack drop = new ItemStack(ModItems.ENIGAMTIC_DIE.get());

                ItemEntity entity = new ItemEntity(level,
                        player.getX() + 0.5,
                        player.getY() + 0.5,
                        player.getZ() + 0.5,
                        drop);

                level.addFreshEntity(entity);
                player.displayClientMessage(Component.translatable("enigmaticdice.advancement_earned." + level.random.nextInt(5)), false);
            }
        }
    }
}
