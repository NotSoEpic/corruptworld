package com.dindcrzy.corruptworld.mixin.client;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccessor {
    @Invoker("scheduleChunkRender")
    void chunkRender(int x, int y, int z, boolean important);
}
