package com.dindcrzy.corruptworld;

import com.dindcrzy.corruptworld.blocks.CBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class ClientMod implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(CBlocks.CORRUPT_VINE, RenderLayer.getCutout());
    }
}
