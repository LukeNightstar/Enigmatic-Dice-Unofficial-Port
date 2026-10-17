package net.jrdemiurge.enigmaticdice.network;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.event.ClientTimeHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TimeAccelStartPacket(int multiplier, int durationTicks) implements CustomPacketPayload {
    public static final Type<TimeAccelStartPacket> TYPE = new Type<>(EnigmaticDice.location("time_accel_start"));
    public static final StreamCodec<FriendlyByteBuf, TimeAccelStartPacket> STREAM_CODEC = CustomPacketPayload.codec(
            TimeAccelStartPacket::write, TimeAccelStartPacket::read
    );

    private void write(FriendlyByteBuf buf) {
        buf.writeVarInt(multiplier);
        buf.writeVarInt(durationTicks);
    }

    private static TimeAccelStartPacket read(FriendlyByteBuf buf) {
        int mul = buf.readVarInt();
        int dur = buf.readVarInt();
        return new TimeAccelStartPacket(mul, dur);
    }

    public static void handle(TimeAccelStartPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientTimeHandler.start(packet.multiplier, packet.durationTicks));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
