package net.jrdemiurge.enigmaticdice.network;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.custom.crucibleofrile.ClientLookController;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record LookAtTargetPacket(UUID targetUuid, int durationTicks) implements CustomPacketPayload {
    public static final Type<LookAtTargetPacket> TYPE = new Type<>(EnigmaticDice.location("look_at_target"));
    public static final StreamCodec<FriendlyByteBuf, LookAtTargetPacket> STREAM_CODEC = CustomPacketPayload.codec(
            LookAtTargetPacket::write, LookAtTargetPacket::read
    );

    private void write(FriendlyByteBuf buf) {
        buf.writeUUID(targetUuid);
        buf.writeVarInt(durationTicks);
    }

    private static LookAtTargetPacket read(FriendlyByteBuf buf) {
        return new LookAtTargetPacket(buf.readUUID(), buf.readVarInt());
    }

    public static void handle(LookAtTargetPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientLookController.startOrExtend(packet.targetUuid, packet.durationTicks));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
