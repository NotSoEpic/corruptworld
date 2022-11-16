package com.dindcrzy.corruptworld.blocks.purifier;

import com.dindcrzy.corruptworld.blocks.CBlocks;
import com.dindcrzy.corruptworld.chunkcorrpution.CardinalChunk;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PurifierBlockEntity extends BlockEntity {
    private int count = 0;
    
    public PurifierBlockEntity(BlockPos pos, BlockState state) {
        super(CBlocks.PURIFIER_ENTITY, pos, state);
    }
    
    public static void tick(World world, BlockPos pos, BlockState state, PurifierBlockEntity be) {
        // tries cleansing once every second
        if ((world.getLevelProperties().getTime() % 20) == 0) {
            //spiralCleanse(world, pos);
            be.recalcCount(world, pos, be);
        }
    }
    
    public void recalcCount(World world, BlockPos pos, PurifierBlockEntity be) {
        be.count = CardinalChunk.countWorldSlice(
                world,
                pos.getX() - 4,
                pos.getZ() - 4,
                pos.getX() + 4,
                pos.getZ() + 4
        );
    }
    
    public int getCount() {
        return count;
    }
    
    // https://stackoverflow.com/questions/3706219/algorithm-for-iterating-over-an-outward-spiral-on-a-discrete-2d-grid-from-the-or
    public static void spiralCleanse(World world, BlockPos pos) {
        float cleansePower = 1;

        // vector (di, dj)
        // -1 % 2 = -1, 0 % 2 = 0, 1 % 2 = 1, 2 % 2 = 0
        int di = world.getRandom().nextInt(-1, 3) % 2;
        int dj = di == 0 ? world.getRandom().nextInt(0, 2) * 2 -1 : 0;
        boolean cw = world.getRandom().nextBoolean();
        // even chance of getting either (1, 0), (-1, 0), (0, 1), or (0, -1)

        // position (i, j)
        int i = 0;
        int j = 0;
        int segment_length = 1;
        int segment_passed = 0;

        int iter = 17 * 19; // iterations for square length 19, no clue why it isnt 19*19
        for (int k = 0; k < iter && cleansePower > 0; k++) {
            if (CardinalChunk.setCorruption(world, pos.getX() + i, pos.getZ() + j, false)) {
                cleansePower -= Math.max(0.25f, k / (double)iter);
            }

            if (segment_passed == segment_length) {
                segment_passed = 0;

                if (cw) {
                    int buffer = dj;
                    dj = -di;
                    di = buffer;
                } else {
                    int buffer = di;
                    di = -dj;
                    dj = buffer;
                }

                if (dj == 0) {
                    ++segment_length;
                }
            }
            i += di;
            j += dj;
            ++segment_passed;
        }
    }
}
