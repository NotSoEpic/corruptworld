package com.dindcrzy.corruptworld.blocks;

import com.dindcrzy.corruptworld.entities.projectiles.attackspore.AttackSporeProjectile;
import com.dindcrzy.corruptworld.entities.CEntity;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class ThornBlossomBlock extends Block {
    public static int MAX_AGE = 2; // 3 different states
    public static final BooleanProperty GROWING = BooleanProperty.of("growing");
    public static final IntProperty AGE = IntProperty.of("age", 0, MAX_AGE);
    public static final DirectionProperty FACING = Properties.FACING;
    
    private static final VoxelShape upShape = Block.createCuboidShape(0, 0, 0, 16, 16, 16);
    
    public ThornBlossomBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(GROWING, true).with(AGE, 0).with(FACING, Direction.UP));
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return state.get(GROWING);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(GROWING)) {
            int age = state.get(AGE);
            if (age < MAX_AGE) {
                world.setBlockState(pos, state.with(AGE, age + 1));
            } else {
                tryAttack(state, world, pos);
                // send out spores
                // world.setBlockState(pos, state.with(AGE, 0));
            }
        }
    }
    
    public static boolean tryAttack(BlockState state, World world, BlockPos pos) {
        Vec3d origin = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        Vec3d handle = origin.add(Vec3d.of(state.get(FACING).getVector()).multiply(5));
        List<PlayerEntity> targets = world.getNonSpectatingEntities(PlayerEntity.class, 
                Box.of(origin, 16, 16, 16));
        boolean success = false;
        for (PlayerEntity entity : targets) {
            AttackSporeProjectile projectile = CEntity.ATTACK_SPORE.create(world);
            projectile.setSpeed(2);
            projectile.setPoints(origin, handle, entity.getPos());
            world.spawnEntity(projectile);
            success = true;
        }
        return success;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
       builder.add(GROWING, AGE, FACING);
    }

    // todo: proper block outlines
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
            case NORTH:
                return upShape;
            case SOUTH:
                return upShape;
        }
        return upShape;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction direction = state.get(FACING);
        // can only place on thorn blocks
        return world.getBlockState(pos.offset(direction.getOpposite())).isOf(CBlocks.THORN);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == state.get(FACING).getOpposite()) {
            if (!state.canPlaceAt(world, pos)) {
                return Blocks.AIR.getDefaultState();
            }
            return state.with(GROWING, neighborState.isOf(CBlocks.THORN) && neighborState.get(ThornBlock.NATURAL));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getSide());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (tryAttack(state, world, pos)) {
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }
}
