package com.dindcrzy.corruptworld.mixin.client;

import com.dindcrzy.corruptworld.blocks.CBlocks;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemColors.class)
public class ItemColorsMixin {
    @Inject(method = "create", at = @At("TAIL"))
    private static void extraColours(BlockColors blockColors, CallbackInfoReturnable<ItemColors> cir) {
        cir.getReturnValue().register((stack, tintIndex) -> 0x843BA8, CBlocks.CORRUPT_VINE);
    }
}
