package com.dindcrzy.corruptworld.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class ThornCoreBlock extends Block implements Fertilizable {
    public ThornCoreBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        tryGrow(state, world, pos, random);
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return Stream.of(DIRECTIONS).anyMatch(direction -> canGrowTo(world, pos.offset(direction)));
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        tryGrow(state, world, pos, random);
    }

    public static void tryGrow(BlockState state, WorldAccess world, BlockPos pos, Random random) {
        List<Direction> randDir = Arrays.asList(Direction.values());
        Collections.shuffle(randDir, random);
        for (Direction dir : randDir) {
            BlockPos oPos = pos.offset(dir);
            if (ThornBlock.canGrowTo(world, oPos, state)) {
                world.setBlockState(oPos, ThornBlock.updateDistanceFromGround(CBlocks.THORN.getDefaultState(), world, oPos), Block.NOTIFY_ALL);
            }
        }
    }
    
    public static boolean canGrowTo(BlockView world, BlockPos toPos) {
        // return Stream.of(DIRECTIONS).anyMatch(direction -> canGrowAt(world, toPos.offset(direction)));
        return ThornBlock.canReplace(world.getBlockState(toPos)) && ThornBlock.canGrowAt(world, toPos, true);
    }
}
