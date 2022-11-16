package com.dindcrzy.corruptworld.mixin;

import com.dindcrzy.corruptworld.ChunkCorruption;
import com.dindcrzy.corruptworld.IChunk;
import com.dindcrzy.corruptworld.IChunkData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkData.class)
public class ChunkDataMixin implements IChunkData {
    private byte[] corruptionData = new byte[]{};
    
    // chunk -> chunkdata
    @Inject(method = "<init>(Lnet/minecraft/world/chunk/WorldChunk;)V", at = @At("TAIL"))
    private void serializeChunk(WorldChunk chunk, CallbackInfo ci) {
        ChunkCorruption corruption = ((IChunk) chunk).getChunkCorruption();
        if (corruption != null) {
            corruptionData = corruption.data.toByteArray();
        }
    }
    
    // chunkdata -> packet
    @Inject(method = "write", at = @At("TAIL"))
    private void serializeChunkData(PacketByteBuf buf, CallbackInfo ci) {
        buf.writeByteArray(corruptionData);
    }
    
    // packet -> chunkdata
    @Inject(method = "<init>(Lnet/minecraft/network/PacketByteBuf;II)V", at = @At("TAIL"))
    private void deserializePacket(PacketByteBuf buf, int x, int z, CallbackInfo ci) {
        corruptionData = buf.readByteArray();
    }
    
    // chunkdata -> chunk
    // 
    
    @Override
    public void setCorruptionData(byte[] corruptionData) {
        this.corruptionData = corruptionData;
    }

    @Override
    public byte[] getCorruptionData() {
        return corruptionData;
    }
}
