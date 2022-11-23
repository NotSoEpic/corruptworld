package com.dindcrzy.corruptworld.chunkcorrpution;

import com.dindcrzy.corruptworld.Helper;
import com.dindcrzy.corruptworld.mixin.client.ChunkRendererRegionAccessor;
import com.dindcrzy.corruptworld.mixin.client.RenderedChunkAccessor;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.client.render.chunk.RenderedChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.BitSet;
import java.util.Optional;

import static com.dindcrzy.corruptworld.CorruptWorld.ModId;

public final class CardinalChunk implements ChunkComponentInitializer {
    public static final ComponentKey<ChunkComponent> CORRUPTION =
            ComponentRegistry.getOrCreate(ModId("corruption"), ChunkComponent.class);
    
    @Override
    public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
        registry.register(CORRUPTION, ChunkData::new);
    }
    
    public static boolean setCorruption(Chunk provider, int x, int z, boolean v) {
        Optional<ChunkComponent> optional = CORRUPTION.maybeGet(provider);
        boolean changed = false;
        if (optional.isPresent()) {
            ChunkComponent corruption = optional.get();
            if (corruption.setAbs(x, z, v)) {
                changed = true;
                provider.setNeedsSaving(true);
            }
        }
        return changed;
    }
    
    public static boolean setCorruption(World world, int x, int z, boolean v) {
        Chunk provider = world.getChunk(new BlockPos(x, 0, z));
        return setCorruption(provider, x, z, v);
    }
    
    public static boolean isEmpty(Chunk provider) {
        Optional<ChunkComponent> optional = CORRUPTION.maybeGet(provider);
        if (optional.isPresent()) {
            ChunkComponent corruption = optional.get();
            return corruption.isEmpty();
        }
        return true;
    }
    
    public static boolean isFull(Chunk provider) {
        Optional<ChunkComponent> optional = CORRUPTION.maybeGet(provider);
        if (optional.isPresent()) {
            ChunkComponent corruption = optional.get();
            return corruption.isFull();
        }
        return false;
    }
    
    public static void syncCorruption(Chunk provider) {
        if (CORRUPTION.maybeGet(provider).isPresent()) {
            CORRUPTION.sync(provider);
        }
    }
    
    public static void syncCorruption(World world, int x, int z) {
        Chunk provider = world.getChunk(new BlockPos(x, 0, z));
        syncCorruption(provider);
    }
    
    public static boolean getCorruption(Chunk provider, int x, int z) {
        Optional<ChunkComponent> optional = CORRUPTION.maybeGet(provider);
        if (optional.isPresent()) {
            ChunkComponent corruption = optional.get();
            return corruption.getAbs(x, z);
        }
        return false;
    }
    
    public static boolean getCorruption(World world, int x, int z) {
        Chunk provider = world.getChunk(new BlockPos(x, 0, z));
        return getCorruption(provider, x, z);
    }
    
    public static boolean getCorruption(BlockRenderView view, int x, int z) {
        if (view instanceof Chunk chunk) {
            return getCorruption(chunk, x, z);
        } else if (view instanceof World world) {
            return getCorruption(world, x, z);
        } else if (view instanceof ChunkRendererRegionAccessor renderer) {
            RenderedChunk[][] chunks = renderer.getChunks();
            int cx = ChunkSectionPos.getSectionCoord(x) - renderer.getChunkXOffset();
            int cz = ChunkSectionPos.getSectionCoord(z) - renderer.getChunkZOffset();
             return getCorruption(((RenderedChunkAccessor)chunks[cx][cz]).getChunk(), x, z);
        }
        return false;
    }
    
    public static boolean getDirty(Chunk provider) {
        Optional<ChunkComponent> optional = CORRUPTION.maybeGet(provider);
        return optional.map(ChunkComponent::isDirty).orElse(false);
    }
    
    public static void setDirty(Chunk provider, boolean b) {
        Optional<ChunkComponent> optional = CORRUPTION.maybeGet(provider);
        optional.ifPresent(corruption -> corruption.setDirty(b));
    }
    
    public static int countChunk(Chunk provider) {
        Optional<ChunkComponent> optional = CORRUPTION.maybeGet(provider);
        if (optional.isPresent()) {
            ChunkComponent corruption = optional.get();
            return corruption.getData().cardinality();
        }
        return 0;
    }
    
    public static int countChunkSlice(Chunk provider, int x1, int y1, int x2, int y2) {
        Optional<ChunkComponent> optional = CORRUPTION.maybeGet(provider);
        if (optional.isPresent()) {
            x1 = ChunkSectionPos.getLocalCoord(x1);
            x2 = ChunkSectionPos.getLocalCoord(x2);
            y1 = ChunkSectionPos.getLocalCoord(y1);
            y2 = ChunkSectionPos.getLocalCoord(y2);
            ChunkComponent corruption = optional.get();
            BitSet slice = Helper.slice2D(x1, y1, x2, y2, 16, 16);
            slice.and(corruption.getData());
            return slice.cardinality();
        }
        return 0;
    }
    
    // this code is a mess
    public static int countWorldSlice(World world, int x1, int y1, int x2, int y2) {
        int minCX = ChunkSectionPos.getSectionCoord(Math.min(x1, x2));
        int maxCX = ChunkSectionPos.getSectionCoord(Math.max(x1, x2));
        int minCY = ChunkSectionPos.getSectionCoord(Math.min(y1, y2));
        int maxCY = ChunkSectionPos.getSectionCoord(Math.max(y1, y2));
        int sum = 0;
        for (int i = minCX; i <= maxCX; i++) {
            for (int j = minCY; j <= maxCY; j++) {
                int sliceMinX = 0;
                int sliceMaxX = 15;
                int sliceMinY = 0;
                int sliceMaxY = 15;
                boolean edge = false;
                if (minCX == maxCX) { // horizontal strip within 1 chunk
                    edge = true;
                    sliceMinX = ChunkSectionPos.getLocalCoord(Math.min(x1, x2));
                    sliceMaxX = ChunkSectionPos.getLocalCoord(Math.max(x1, x2));
                } else if (i == minCX) { // left edge
                    edge = true;
                    sliceMinX = ChunkSectionPos.getLocalCoord(Math.min(x1, x2));
                } else if (i == maxCX) { // right edge
                    edge = true;
                    sliceMaxX = ChunkSectionPos.getLocalCoord(Math.max(x1, x2));
                }
                if (minCY == maxCY) { // vertical strip within 1 chunk
                    edge = true;
                    sliceMinY = ChunkSectionPos.getLocalCoord(Math.min(y1, y2));
                    sliceMaxY = ChunkSectionPos.getLocalCoord(Math.max(y1, y2));
                } else if (j == minCY) { // bottom edge
                    edge = true;
                    sliceMinY = ChunkSectionPos.getLocalCoord(Math.min(y1, y2));
                } else if (j == maxCY) { // upper edge
                    edge = true;
                    sliceMaxY = ChunkSectionPos.getLocalCoord(Math.max(y1, y2));
                }
                
                Chunk chunk = world.getChunk(i, j);
                if (edge) {
                    sum += countChunkSlice(chunk, sliceMinX, sliceMinY, sliceMaxX, sliceMaxY);
                } else {
                    sum += countChunk(chunk);
                }
            }
        }
        return sum;
    }
    
    public static int countWorldSlice(World world, BlockPos pos1, BlockPos pos2) {
        return countWorldSlice(world, pos1.getX(), pos1.getZ(), pos2.getX(), pos2.getZ());
    }
}
