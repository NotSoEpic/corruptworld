package com.dindcrzy.corruptworld.mixin.client;

import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccessor {
    @Accessor("chunkBuilder")
    ChunkBuilder chunkBuilder();
    
    @Accessor("chunks")
    BuiltChunkStorage chunks();
}
