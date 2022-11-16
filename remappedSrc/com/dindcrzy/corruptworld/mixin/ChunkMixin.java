package com.dindcrzy.corruptworld.mixin;

import com.dindcrzy.corruptworld.ChunkCorruption;
import com.dindcrzy.corruptworld.CorruptWorld;
import com.dindcrzy.corruptworld.IChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Chunk.class)
public abstract class ChunkMixin implements IChunk {
    @Shadow public abstract void setNeedsSaving(boolean needsSaving);

    ChunkCorruption corruption = null;

    @Override
    public @Nullable ChunkCorruption getChunkCorruption() {
        return corruption;
    }

    @Override
    public void setChunkCorruption(ChunkCorruption set) {
        corruption = set;
    }

    @Override
    public boolean getDirty() {
        return corruption != null && corruption.dirty;
    }

    @Override
    public void setCorruptionAbs(boolean v, int x, int z) {
        if (corruption == null) {
            corruption = new ChunkCorruption(new ChunkPos(new BlockPos(x, 0, z)));
        }
        if (corruption.setAbs(v, x, z)) {
            // only makes chunk save if a value actually changed
            setNeedsSaving(true);
        }
    }

    @Override
    public boolean getCorruptionAbs(int x, int z) {
        if (corruption != null) {
            return corruption.getAbs(x, z);
        }
        return false;
    }
}