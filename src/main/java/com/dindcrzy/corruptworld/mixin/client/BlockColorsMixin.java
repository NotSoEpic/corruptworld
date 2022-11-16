package com.dindcrzy.corruptworld.mixin.client;

import com.dindcrzy.corruptworld.blocks.CBlocks;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockColors.class)
public class BlockColorsMixin {
    @Inject(method = "create", at = @At(value = "TAIL"))
    private static void extraColours(CallbackInfoReturnable<BlockColors> cir) {
        cir.getReturnValue().registerColorProvider((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) {
                return FoliageColors.getDefaultColor();
            }
            return BiomeColors.getFoliageColor(world, pos);
        }, CBlocks.CORRUPT_VINE);
    }
}
