package com.dindcrzy.corruptworld.mixin.client;

import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.chunk.RenderedChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkRendererRegion.class)
public interface ChunkRendererRegionAccessor {
    @Accessor
    RenderedChunk[][] getChunks();
    
    @Accessor
    int getChunkXOffset();
    
    @Accessor
    int getChunkZOffset();
}
