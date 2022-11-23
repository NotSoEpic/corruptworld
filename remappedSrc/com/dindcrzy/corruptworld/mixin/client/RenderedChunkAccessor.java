package com.dindcrzy.corruptworld.mixin.client;

import net.minecraft.client.render.chunk.RenderedChunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderedChunk.class)
public interface RenderedChunkAccessor {
    @Accessor
    WorldChunk getChunk();
}
