package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import com.mojang.datafixers.util.Pair;
import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.config.EnigmaticDiceConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;

public class TeleportToBiomeEvent extends RandomEvent {

    public TeleportToBiomeEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
        }

        if (!(pLevel instanceof ServerLevel serverLevel)) {
            return false;
        }

        List<String> allBiomes = EnigmaticDiceConfig.configData.teleportBiomes;
        if (allBiomes == null || allBiomes.isEmpty()) {
            EnigmaticDice.LOGGER.error("Biome list is empty in config!");
            return false;
        }

        List<String> validBiomes = allBiomes.stream()
                .filter(biomeId -> {
                    Identifier biomeLocation = Identifier.parse(biomeId);
                    return serverLevel.registryAccess().lookupOrThrow(Registries.BIOME).containsKey(biomeLocation);
                })
                .toList();

        if (validBiomes.isEmpty()) {
            EnigmaticDice.LOGGER.error("No valid biomes found! Check your config.");
            return false;
        }

        String biomeIdentifier = validBiomes.get(pLevel.getRandom().nextInt(validBiomes.size()));

        try {
            Identifier biomeLocation = Identifier.parse(biomeIdentifier);
            Pair<BlockPos, Holder<Biome>> biomePair = serverLevel.findClosestBiome3d(
                    biome -> biome.is(biomeLocation),
                    pPlayer.blockPosition(),
                    Config.BiomeSearchRadius,
                    Config.BiomeHorizontalStep,
                    Config.BiomeVerticalStep
            );

            if (biomePair == null) {
                EnigmaticDice.LOGGER.info("Biome not found: {}", biomeIdentifier);
                return false;
            }

            BlockPos biomePos = biomePair.getFirst();
            serverLevel.getChunkSource().getChunk(biomePos.getX() >> 4, biomePos.getZ() >> 4, ChunkStatus.FULL, true);

            int surfaceY = serverLevel.getHeight(Heightmap.Types.WORLD_SURFACE, biomePos.getX(), biomePos.getZ());

            pPlayer.teleportTo(biomePos.getX() + 0.5, surfaceY + 1, biomePos.getZ() + 0.5);

            MutableComponent message = Component.translatable("enigmaticdice.event.teleport_to_biome." + pLevel.getRandom().nextInt(6));
            pPlayer.sendSystemMessage(message);
            return true;

        } catch (Exception e) {
            EnigmaticDice.LOGGER.error("Error while teleporting to biome {}: ", biomeIdentifier, e);
            return false;
        }
    }
}
