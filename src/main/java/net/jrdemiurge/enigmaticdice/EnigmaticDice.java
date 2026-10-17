package net.jrdemiurge.enigmaticdice;

import com.mojang.logging.LogUtils;
import net.jrdemiurge.enigmaticdice.attribute.ModAttributes;
import net.jrdemiurge.enigmaticdice.commands.EnigmaticDiceCommand;
import net.jrdemiurge.enigmaticdice.commands.EnigmaticDiceGetLuckCommand;
import net.jrdemiurge.enigmaticdice.commands.EnigmaticDiceSimulateCommand;
import net.jrdemiurge.enigmaticdice.effect.ModEffects;
import net.jrdemiurge.enigmaticdice.entity.ModEntities;
import net.jrdemiurge.enigmaticdice.entity.client.DragonclawHookRender;
import net.jrdemiurge.enigmaticdice.item.ModCreativeTabs;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.jrdemiurge.enigmaticdice.item.custom.Antimatter;
import net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie.RandomEventManager;
import net.jrdemiurge.enigmaticdice.network.DoubleJumpPacket;
import net.jrdemiurge.enigmaticdice.network.LookAtTargetPacket;
import net.jrdemiurge.enigmaticdice.network.TimeAccelStartPacket;
import net.jrdemiurge.enigmaticdice.sound.ModSounds;
import net.jrdemiurge.enigmaticdice.stat.ModStats;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

@Mod(EnigmaticDice.MOD_ID)
public class EnigmaticDice {
    public static final String MOD_ID = "enigmaticdice";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static RandomEventManager eventManager;

    public EnigmaticDice(IEventBus modEventBus, ModContainer container) {
        ModCreativeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModSounds.register(modEventBus);
        ModEffects.register(modEventBus);
        ModEntities.register(modEventBus);
        ModAttributes.register(modEventBus);
        ModStats.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onPacketSetup);

        NeoForge.EVENT_BUS.register(this);

        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(Antimatter::init);
    }

    public void onPacketSetup(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MOD_ID + ".1.0").optional();
        registrar.playToServer(DoubleJumpPacket.TYPE, DoubleJumpPacket.STREAM_CODEC, DoubleJumpPacket::handle);
        registrar.playToClient(TimeAccelStartPacket.TYPE, TimeAccelStartPacket.STREAM_CODEC, TimeAccelStartPacket::handle);
        registrar.playToClient(LookAtTargetPacket.TYPE, LookAtTargetPacket.STREAM_CODEC, LookAtTargetPacket::handle);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        event.getServer().getCommands().getDispatcher().register(
                EnigmaticDiceCommand.create()
        );
        event.getServer().getCommands().getDispatcher().register(
                EnigmaticDiceSimulateCommand.create()
        );
        event.getServer().getCommands().getDispatcher().register(
                EnigmaticDiceGetLuckCommand.create()
        );
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.DRAGONCLAW_HOOK.get(), DragonclawHookRender::new);
        }
    }
}
