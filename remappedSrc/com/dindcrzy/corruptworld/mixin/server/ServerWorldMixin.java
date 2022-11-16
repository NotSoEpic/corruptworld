package com.dindcrzy.corruptworld.mixin.server;

import com.dindcrzy.corruptworld.ChunkCorruption;
import com.dindcrzy.corruptworld.CorruptWorld;
import com.dindcrzy.corruptworld.IChunk;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.BitSet;
import java.util.List;
import java.util.function.Predicate;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Shadow public abstract List<ServerPlayerEntity> getPlayers(Predicate<? super ServerPlayerEntity> predicate);

    @Inject(method = "tickChunk", at = @At("TAIL"))
    private void syncCorruption(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        ChunkCorruption corruption = ((IChunk)chunk).getChunkCorruption();
        if (corruption != null && corruption.dirty) {
            corruption.dirty = false;
            BitSet testData = new BitSet(256);
            testData.set(0, 255, true);
            //PacketByteBuf buf = new ChunkCorruption(testData, chunk.getPos()).writePacket();
            PacketByteBuf buf = corruption.writePacket();
            getPlayers((player) ->
                    player.squaredDistanceTo(
                            chunk.getPos().getCenterX(), player.getY(), chunk.getPos().getCenterZ()
                    ) < 32 * 32
            ).forEach((player) -> ServerPlayNetworking.send(
                player, 
                ChunkCorruption.PACKET_ID, 
                buf
            ));
        }
    }
}
