package com.dindcrzy.corruptworld.mixin.client;

import com.dindcrzy.corruptworld.ChunkCorruption;
import com.dindcrzy.corruptworld.IChunk;
import com.dindcrzy.corruptworld.IChunkData;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.BitSet;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Shadow private ClientWorld world;

    // chunkdata -> chunk
    @Inject(method = "loadChunk", at = @At("TAIL"))
    private void deserializeChunkData(int x, int z, ChunkData chunkData, CallbackInfo ci) {
        byte[] data = ((IChunkData) chunkData).getCorruptionData();
        ChunkCorruption corruption = new ChunkCorruption(BitSet.valueOf(data), new ChunkPos(x, z));
        ((IChunk)world.getChunk(x, z)).setChunkCorruption(corruption);
    }
}
