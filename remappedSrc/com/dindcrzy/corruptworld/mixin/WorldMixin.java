package com.dindcrzy.corruptworld.mixin;

import com.dindcrzy.corruptworld.CorruptWorld;
import com.dindcrzy.corruptworld.IChunk;
import com.dindcrzy.corruptworld.IWorld;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(World.class)
public abstract class WorldMixin implements IWorld {
    @Shadow public abstract WorldChunk getChunk(int i, int j);

    @Shadow @Nullable public abstract Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create);

    @Override
    public void setCorruption(boolean v, int x, int z) {
        int cx = ChunkSectionPos.getSectionCoord(x);
        int cz = ChunkSectionPos.getSectionCoord(z);
        ((IChunk) getChunk(cx, cz)).setCorruptionAbs(v, x, z);
    }

    @Override
    public boolean getCorruption(int x, int z) {
        int cx = ChunkSectionPos.getSectionCoord(x);
        int cz = ChunkSectionPos.getSectionCoord(z);
        return ((IChunk) getChunk(cx, cz)).getCorruptionAbs(x, z);
    }
}
