package com.dindcrzy.corruptworld.mixin.server;

import com.dindcrzy.corruptworld.blocks.CBlocks;
import com.dindcrzy.corruptworld.blocks.CorruptVineBlock;
import com.dindcrzy.corruptworld.chunkcorrpution.CardinalChunk;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> registryEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        super(properties, registryRef, registryEntry, profiler, isClient, debugWorld, seed);
    }

    @Inject(method = "tickChunk", at = @At(value = "TAIL"))
    private void corruptionTick(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        if (!CardinalChunk.isEmpty(chunk)) {
            int i = chunk.getPos().getStartX();
            int k = chunk.getPos().getStartZ();
            boolean full = CardinalChunk.isFull(chunk);
            if (randomTickSpeed > 0) {
                for (ChunkSection section : chunk.getSectionArray()) {
                    int j = section.getYOffset();
                    for (int l = 0; l < randomTickSpeed; l++) {
                        BlockPos pos = getRandomPosInChunk(i, j, k, 15);
                        if (full || CardinalChunk.getCorruption(chunk, pos.getX(), pos.getZ())) {
                            if (chunk.getBlockState(pos).isAir()) {
                                BlockState vineState = CBlocks.CORRUPT_VINE.getDefaultState();
                                boolean canExist = false;
                                for (Direction dir : Direction.values()) {
                                    if (CorruptVineBlock.FACING_PROPERTIES.containsKey(dir) &&
                                            CorruptVineBlock.shouldConnectTo(this, pos, dir)) {
                                        vineState.with(CorruptVineBlock.FACING_PROPERTIES.get(dir), true);
                                        canExist = true;
                                    }
                                }
                                if (canExist) {
                                    setBlockState(pos, vineState);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
