package com.dindcrzy.corruptworld.blocks;

import com.dindcrzy.corruptworld.chunkcorrpution.CardinalChunk;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class CorruptScumBlock extends LilyPadBlock {
    public CorruptScumBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    public static boolean canReplace(BlockState state) {
        return state.isAir() || state.isIn(CustomTags.SCUM_REPLACEABLE);
    }

    // the strangest, most mundane things are private, not static, protected, etc. for no reason
    public static boolean canPlantOver(BlockState floor, BlockView world, BlockPos pos) {
        FluidState fluidState = world.getFluidState(pos);
        BlockState upBlockState = world.getBlockState(pos.up());
        return (fluidState.getFluid() == Fluids.WATER || floor.getMaterial() == Material.ICE) && canReplace(upBlockState);
    }
    
    // pos -> block position to be replaced, not position of water its over
    public static boolean canSpreadTo(BlockView world, BlockPos pos) {
        return canPlantOver(world.getBlockState(pos.down()), world, pos.down());
    }
    
    public static void trySpreadRandomly(ServerWorld world, BlockPos pos, Random random) {
        Direction dir = Direction.Type.HORIZONTAL.random(random);
        if (canSpreadTo(world, pos.offset(dir))) {
            world.setBlockState(pos, CBlocks.CORRUPT_SCUM.getDefaultState());
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (CardinalChunk.getCorruption(world, pos.getX(), pos.getZ())) {
            for (int i = 0; i < 2; i++) {
                trySpreadRandomly(world, pos, random);
            }
        } else {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }
}
