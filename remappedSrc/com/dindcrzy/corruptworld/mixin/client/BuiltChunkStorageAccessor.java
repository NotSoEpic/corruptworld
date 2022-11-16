package com.dindcrzy.corruptworld.mixin.client;

import net.minecraft.client.render.BuiltChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BuiltChunkStorage.class)
public interface BuiltChunkStorageAccessor {
    // why the FUCK does this not work?
    /*@Accessor("getChunkIndex")
    int chunkIndex(int x, int y, int z);*/
}
