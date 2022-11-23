package com.dindcrzy.corruptworld.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class ThornBlock extends Block implements Fertilizable {
    private static final int MAX_LENGTH = 12;
    // only natural (not placed) blocks grow
    public static final BooleanProperty NATURAL = BooleanProperty.of("natural");
    public static final IntProperty DISTANCE = IntProperty.of("distance", 1, MAX_LENGTH);
    
    public ThornBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(NATURAL, true).with(DISTANCE, MAX_LENGTH));
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(NATURAL)) {
            tryGrow(state, world, pos, random);
        }
    }
    
    public static void tryGrow(BlockState state, WorldAccess world, BlockPos pos, Random random) {
        if (canGrowAt(world, pos, false)) {
            int dist = state.get(DISTANCE);
            if (dist < MAX_LENGTH) {
                List<Direction> randDir = Arrays.asList(Direction.values());
                Collections.shuffle(randDir, random);
                for (Direction dir : randDir) {
                    BlockPos oPos = pos.offset(dir);
                    if (canGrowTo(world, oPos, state)) {
                        BlockState addState = dist == MAX_LENGTH - 1 ?
                                CBlocks.THORN_BLOSSOM.getDefaultState().with(ThornBlossomBlock.FACING, dir) :
                                updateDistanceFromGround(CBlocks.THORN.getDefaultState(), world, oPos);
                        world.setBlockState(oPos, addState, Block.NOTIFY_ALL);
                        return;
                    }
                }
            }
        }
    }
    
    public static boolean canGrowTo(BlockView world, BlockPos toPos, BlockState fromState) {
        // return Stream.of(DIRECTIONS).anyMatch(direction -> canGrowAt(world, toPos.offset(direction)));
        return canReplace(world.getBlockState(toPos)) && fromState.get(DISTANCE) < MAX_LENGTH && canGrowAt(world, toPos, true);
    }
    
    public static boolean canGrowAt(BlockView world, BlockPos pos, boolean checkSelfState) {
        BlockState state = world.getBlockState(pos);
        if (!checkSelfState || canReplace(state)) {
            int i = 3; // maximum adjacent blocks (i - 1 == maximum adjacent to be able to grow)
            int minDist = MAX_LENGTH;
            for (Direction dir : Direction.values()) {
                BlockState offState = world.getBlockState(pos.offset(dir));
                if (!canReplace(offState)) {
                    i -= 1;
                }
                minDist = Math.min(minDist, getDistanceFromGround(offState));
                if (i == 0) {
                    return false;
                }
            }
            return minDist <= MAX_LENGTH;
        }
        return false;
    }
    
    public static boolean canReplace(BlockState state) {
        return state.isOf(Blocks.AIR) || state.isIn(CustomTags.THORN_REPLACEABLE);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockState newState = updateDistanceFromGround(state, world, pos);
        if (newState.get(DISTANCE) == MAX_LENGTH && newState.get(NATURAL)) {
            // max distance thorns break
            world.breakBlock(pos, false);
        } else {
            world.setBlockState(pos, newState, Block.NOTIFY_ALL);
        }
    }
    
    // gets potential new blockstate when adjacent block is updated
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        int i = getDistanceFromGround(neighborState) + 1;
        if (i != 1 || state.get(DISTANCE) != i) {
            world.createAndScheduleBlockTick(pos, this, 1);
        }
        return state;
    }
    
    public static BlockState updateDistanceFromGround(BlockState state, WorldAccess world, BlockPos pos) {
        int i = MAX_LENGTH;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Direction dir : Direction.values()) {
            mutable.set(pos, dir);
            i = Math.min(i, getDistanceFromGround(world.getBlockState(mutable)) + 1);
            if (i == 1) break;
        }
        return state.with(DISTANCE, i);
    }
    
    public static int getDistanceFromGround(BlockState state) {
        if (state.isIn(CustomTags.THORN_CORES)) {
            return 0;
        }
        if (state.getBlock() instanceof ThornBlock) {
            return state.get(DISTANCE);
        }
        return MAX_LENGTH;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DISTANCE, NATURAL);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return updateDistanceFromGround(getDefaultState().with(NATURAL, false), ctx.getWorld(), ctx.getBlockPos());
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return canGrowAt(world, pos, false) && Stream.of(DIRECTIONS).anyMatch(direction -> canGrowTo(world, pos.offset(direction), state));
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        tryGrow(state, world, pos, random);
    }
}
