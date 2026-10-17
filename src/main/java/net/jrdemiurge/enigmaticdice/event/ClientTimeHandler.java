package net.jrdemiurge.enigmaticdice.event;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = EnigmaticDice.MOD_ID, value = Dist.CLIENT)
public class ClientTimeHandler {

    private static boolean active = false;
    private static int multiplier = 1;
    private static int ticksLeft = 0;

    public static void start(int mul, int duration) {
        active = true;
        multiplier = mul - 1;
        ticksLeft = duration;
    }
    public static void stop() { active = false; }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (!active || mc.level == null) return;
        if (mc.isPaused()) return;

        long t = mc.level.getDayTime();
        mc.level.setDayTime(t + multiplier);

        if (--ticksLeft <= 0) stop();
    }
}
