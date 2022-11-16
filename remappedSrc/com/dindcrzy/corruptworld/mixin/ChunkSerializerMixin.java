package com.dindcrzy.corruptworld.mixin;

import com.dindcrzy.corruptworld.ChunkCorruption;
import com.dindcrzy.corruptworld.CorruptWorld;
import com.dindcrzy.corruptworld.IChunk;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {
    // nbt -> chunk
    // todo: figure out how the FUCK this goes wrong
    @Inject(method = "deserialize", at = @At("RETURN"))
    private static void appendDeserialize(ServerWorld world, 
                                          PointOfInterestStorage poiStorage,
                                          ChunkPos chunkPos, 
                                          NbtCompound nbt, 
                                          CallbackInfoReturnable<ProtoChunk> cir) {
        ProtoChunk chunk = cir.getReturnValue();
        ((IChunk) chunk).setChunkCorruption(new ChunkCorruption(nbt));
    }
    
    // chunk -> nbt
    @Inject(method = "serialize", at = @At("RETURN"))
    private static void appendSerialize(ServerWorld world, 
                                        Chunk chunk, 
                                        CallbackInfoReturnable<NbtCompound> cir) {
        NbtCompound nbt = cir.getReturnValue();
        ChunkCorruption corruption = ((IChunk) chunk).getChunkCorruption();
        if (corruption != null) {
            corruption.writeNbt(nbt);
        }
    }
}
