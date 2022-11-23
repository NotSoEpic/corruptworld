package com.dindcrzy.corruptworld.mixin.server;

import com.dindcrzy.corruptworld.blocks.CBlocks;
import com.dindcrzy.corruptworld.blocks.CorruptScumBlock;
import com.dindcrzy.corruptworld.blocks.CorruptVineBlock;
import com.dindcrzy.corruptworld.chunkcorrpution.CardinalChunk;
import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.VineBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> registryEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        super(properties, registryRef, registryEntry, profiler, isClient, debugWorld, seed);
    }

    @Inject(method = "tickChunk", at = @At(value = "TAIL"))
    private void corruptionTick(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        if (randomTickSpeed > 0) {
            if (!CardinalChunk.isEmpty(chunk)) {
                int i = chunk.getPos().getStartX();
                int k = chunk.getPos().getStartZ();
                boolean full = CardinalChunk.isFull(chunk);
                for (ChunkSection section : chunk.getSectionArray()) {
                    int j = section.getYOffset();
                    for (int l = 0; l < randomTickSpeed; l++) {
                        BlockPos pos = getRandomPosInChunk(i, j, k, 15);
                        tryGrowAt(chunk, pos, full);
                    }
                }
            }
        }
    }
    
    private void tryGrowAt(Chunk chunk, BlockPos pos, boolean full) {
        if (full || CardinalChunk.getCorruption(chunk, pos.getX(), pos.getZ())) {
            BlockState oldState = getBlockState(pos);
            if (CorruptScumBlock.canSpreadTo(this, pos)) {
                setBlockState(pos, CBlocks.CORRUPT_SCUM.getDefaultState());
            } else if (CorruptVineBlock.canReplace(oldState)) {
                BlockState vine = CBlocks.CORRUPT_VINE.getDefaultState();
                if (oldState.contains(Properties.WATERLOGGED)) {
                    vine = vine.with(Properties.WATERLOGGED, oldState.get(Properties.WATERLOGGED));
                }
                for (Direction dir : Direction.values()) {
                    BlockState offState = getBlockState(pos.offset(dir));
                    if (CorruptVineBlock.canGrowOn(this, dir, pos.offset(dir), offState)) {
                        vine = vine.with(AbstractLichenBlock.getProperty(dir), true);
                    }
                }
                // why java
                BlockState finalVine = vine;
                if (Arrays.stream(Direction.values()).anyMatch(direction -> CorruptVineBlock.hasDirection(finalVine, direction))) {
                    setBlockState(pos, vine);
                }
            }
        }
    }
}
