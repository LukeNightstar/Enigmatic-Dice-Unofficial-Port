package net.jrdemiurge.enigmaticdice.config;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class ConfigHandler {
    @SubscribeEvent
    public static void onServerStarting(ServerAboutToStartEvent event) {
        EnigmaticDiceConfig.loadConfig();
        EnigmaticDice.LOGGER.info("Enigmatic Dice config loading...");
    }
}
