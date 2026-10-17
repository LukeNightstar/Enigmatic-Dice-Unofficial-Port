package net.jrdemiurge.enigmaticdice.network;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.custom.GravityCore;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DoubleJumpPacket() implements CustomPacketPayload {
    public static final Type<DoubleJumpPacket> TYPE = new Type<>(EnigmaticDice.location("double_jump"));
    public static final StreamCodec<FriendlyByteBuf, DoubleJumpPacket> STREAM_CODEC = StreamCodec.unit(new DoubleJumpPacket());

    public static void handle(DoubleJumpPacket packet, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    GravityCore.jump(player);
                }
            });
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
