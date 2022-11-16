package com.dindcrzy.corruptworld;

import org.jetbrains.annotations.Nullable;

public interface IChunk {
    @Nullable
    default ChunkCorruption getChunkCorruption() { return null; }
    default void setChunkCorruption(ChunkCorruption set) { }
    default boolean getDirty() { return false; }
    default void setCorruptionAbs(boolean v, int x, int z) { }
    default boolean getCorruptionAbs(int x, int z) { return false; }
}
