package com.dindcrzy.corruptworld.mixin.client;

import com.dindcrzy.corruptworld.blocks.CBlocks;
import net.minecraft.client.color.block.BlockColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockColors.class)
public class BlockColorsMixin {
    @Inject(method = "create", at = @At("TAIL"))
    private static void appendColours(CallbackInfoReturnable<BlockColors> cir) {
        BlockColors blockColors = cir.getReturnValue();
        // this is the only way i managed to get biome blending working
        blockColors.registerColorProvider(CBlocks.corruptBlockProvier, CBlocks.CORRUPT_VINE, CBlocks.CORRUPT_SCUM);
    }
}
