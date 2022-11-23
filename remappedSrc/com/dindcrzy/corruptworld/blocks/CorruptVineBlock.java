package com.dindcrzy.corruptworld.blocks;

import com.dindcrzy.corruptworld.chunkcorrpution.CardinalChunk;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

// there is a LOT of copying from GlowLichenBlock
// mostly because one static method was private, and not referenced the right way
@SuppressWarnings("deprecation")
public class CorruptVineBlock extends Block implements Fertilizable, Waterloggable {
    private static final VoxelShape UP_SHAPE = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape DOWN_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
    private static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES;
    private static final Map<Direction, VoxelShape> SHAPES_FOR_DIRECTIONS = Util.make(Maps.newEnumMap(Direction.class), shapes -> {
        shapes.put(Direction.NORTH, SOUTH_SHAPE);
        shapes.put(Direction.EAST, WEST_SHAPE);
        shapes.put(Direction.SOUTH, NORTH_SHAPE);
        shapes.put(Direction.WEST, EAST_SHAPE);
        shapes.put(Direction.UP, UP_SHAPE);
        shapes.put(Direction.DOWN, DOWN_SHAPE);
    });
    protected static final Direction[] DIRECTIONS = Direction.values();
    private final ImmutableMap<BlockState, VoxelShape> SHAPES;
    public CorruptVineBlock(Settings settings) {
        super(settings);
        this.setDefaultState(withAllDirections(this.stateManager).with(Properties.WATERLOGGED, false));
        this.SHAPES = this.getShapesForStates(this::getShapeForState);
    }

    // this is the one method i wanted to override >:(
    public static boolean canGrowOn(BlockView world, Direction direction, BlockPos pos, BlockState state) {
        return Block.isFaceFullSquare(state.getCollisionShape(world, pos), direction.getOpposite()) &&
                !world.getBlockState(pos).isOf(CBlocks.THORN);
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return !context.getStack().isOf(CBlocks.CORRUPT_VINE.asItem()) || isNotFullBlock(state);
    }

    public static boolean canReplace(BlockState state) {
        return state.getBlock().equals(Blocks.AIR) || state.isIn(CustomTags.VINE_REPLACEABLE);
    }

    private boolean canGrowIn(BlockState state) {
        return state.isAir() || state.isOf(this) || canReplace(state) || state.isOf(Blocks.WATER) && state.getFluidState().isStill();
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (CardinalChunk.getCorruption(world, pos.getX(), pos.getZ())) {
            for (int i = 0; i < 3; i++) {
                trySpreadRandomly(state, world, pos, random);
            }
        } else {
            if (state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED)) {
                world.setBlockState(pos, Blocks.WATER.getDefaultState());
            } else {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return Stream.of(DIRECTIONS).anyMatch(direction -> canSpread(state, world, pos, direction.getOpposite()));
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        this.trySpreadRandomly(state, world, pos, random);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        for (Direction direction : DIRECTIONS) {
            builder.add(AbstractLichenBlock.getProperty(direction));
        }
        builder.add(Properties.WATERLOGGED);
    }
    
    
    
    private static BlockState withAllDirections(StateManager<Block, BlockState> stateManager) {
        BlockState blockState = stateManager.getDefaultState();
        for (BooleanProperty booleanProperty : FACING_PROPERTIES.values()) {
            if (!blockState.contains(booleanProperty)) continue;
            blockState = blockState.with(booleanProperty, false);
        }
        return blockState;
    }

    private VoxelShape getShapeForState(BlockState state) {
        VoxelShape voxelShape = VoxelShapes.empty();
        for (Direction direction : DIRECTIONS) {
            if (!hasDirection(state, direction)) continue;
            voxelShape = VoxelShapes.union(voxelShape, SHAPES_FOR_DIRECTIONS.get(direction));
        }
        return voxelShape.isEmpty() ? VoxelShapes.fullCube() : voxelShape;
    }

    protected static boolean hasAnyDirection(BlockState state) {
        return Arrays.stream(DIRECTIONS).anyMatch(direction -> hasDirection(state, direction));
    }

    private static boolean isNotFullBlock(BlockState state) {
        return Arrays.stream(DIRECTIONS).anyMatch(direction -> !hasDirection(state, direction));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!hasAnyDirection(state)) {
            return Blocks.AIR.getDefaultState();
        }
        if (!hasDirection(state, direction) || canGrowOn(world, direction, neighborPos, neighborState)) {
            return state;
        }
        return disableDirection(state, AbstractLichenBlock.getProperty(direction));
    }

    private static BlockState disableDirection(BlockState state, BooleanProperty direction) {
        BlockState blockState = state.with(direction, false);
        if (hasAnyDirection(blockState)) {
            return blockState;
        }
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        boolean bl = false;
        for (Direction direction : DIRECTIONS) {
            if (!hasDirection(state, direction)) continue;
            BlockPos blockPos = pos.offset(direction);
            if (!canGrowOn(world, direction, blockPos, world.getBlockState(blockPos))) {
                return false;
            }
            bl = true;
        }
        return bl;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.SHAPES.get(state);
    }

    public static boolean hasDirection(BlockState state, Direction direction) {
        BooleanProperty booleanProperty = ConnectingBlock.FACING_PROPERTIES.get(direction);
        return state.contains(booleanProperty) && state.get(booleanProperty);
    }

    protected boolean canSpread(BlockState state, BlockView world, BlockPos pos, Direction from) {
        return Stream.of(DIRECTIONS).anyMatch(to -> getSpreadLocation(state, world, pos, from, to).isPresent());
    }

    public boolean trySpreadRandomly(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        ArrayList<Direction> list = Lists.newArrayList(DIRECTIONS);
        Collections.shuffle(list);
        return list.stream().filter(from -> hasDirection(state, from)).anyMatch(to -> this.trySpreadRandomly(state, world, pos, to, random, false));
    }

    public boolean trySpreadRandomly(BlockState state, WorldAccess world, BlockPos pos, Direction from, Random random, boolean postProcess) {
        List<Direction> list = Arrays.asList(DIRECTIONS);
        Collections.shuffle(list, random);
        return list.stream().anyMatch(to -> this.trySpreadTo(state, world, pos, from, to, postProcess));
    }

    public boolean trySpreadTo(BlockState state, WorldAccess world, BlockPos pos, Direction from, Direction to, boolean postProcess) {
        Optional<Pair<BlockPos, Direction>> optional = getSpreadLocation(state, world, pos, from, to);
        if (optional.isPresent()) {
            Pair<BlockPos, Direction> pair = optional.get();
            return addDirection(world, pair.getFirst(), pair.getSecond(), postProcess);
        }
        return false;
    }

    private boolean addDirection(WorldAccess world, BlockPos pos, Direction direction, boolean postProcess) {
        BlockState blockState = world.getBlockState(pos);
        BlockState blockState2 = withDirection(blockState, world, pos, direction);
        if (blockState2 != null) {
            if (postProcess) {
                world.getChunk(pos).markBlockForPostProcessing(pos);
            }
            return world.setBlockState(pos, blockState2, Block.NOTIFY_LISTENERS);
        }
        return false;
    }

    @Nullable
    public BlockState withDirection(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        BlockState blockState;
        if (state.isOf(this)) {
            if (hasDirection(state, direction)) {
                return null;
            }
            blockState = state;
        } else {
            blockState = isWaterlogged() && state.getFluidState().isEqualAndStill(Fluids.WATER) ? this.getDefaultState().with(Properties.WATERLOGGED, true) : this.getDefaultState();
        }
        BlockPos blockPos = pos.offset(direction);
        if (canGrowOn(world, direction, blockPos, world.getBlockState(blockPos))) {
            return blockState.with(AbstractLichenBlock.getProperty(direction), true);
        }
        return null;
    }

    private boolean isWaterlogged() {
        return this.stateManager.getProperties().contains(Properties.WATERLOGGED);
    }

    private Optional<Pair<BlockPos, Direction>> getSpreadLocation(BlockState state, BlockView world, BlockPos pos, Direction from, Direction to) {
        Direction direction;
        if (to.getAxis() == from.getAxis() || !hasDirection(state, from) || hasDirection(state, to)) {
            return Optional.empty();
        }
        if (canSpreadTo(world, pos, to)) {
            return Optional.of(Pair.of(pos, to));
        }
        BlockPos blockPos = pos.offset(to);
        if (canSpreadTo(world, blockPos, from)) {
            return Optional.of(Pair.of(blockPos, from));
        }
        BlockPos blockPos2 = blockPos.offset(from);
        if (canSpreadTo(world, blockPos2, direction = to.getOpposite())) {
            return Optional.of(Pair.of(blockPos2, direction));
        }
        return Optional.empty();
    }

    private boolean canSpreadTo(BlockView world, BlockPos pos, Direction direction) {
        BlockState blockState = world.getBlockState(pos);
        if (!canGrowIn(blockState)) {
            return false;
        }
        BlockState blockState2 = this.withDirection(blockState, world, pos, direction);
        return blockState2 != null;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(Properties.WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        return Arrays.stream(ctx.getPlacementDirections()).map(direction -> this.withDirection(blockState, world, blockPos, direction)).filter(Objects::nonNull).findFirst().orElse(null);
    }
}
