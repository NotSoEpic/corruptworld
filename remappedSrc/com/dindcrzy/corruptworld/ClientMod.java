package com.dindcrzy.corruptworld;

import com.dindcrzy.corruptworld.blocks.CBlocks;
import com.dindcrzy.corruptworld.entities.CEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRendererHookContainer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.Fluids;

public class ClientMod implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        CBlocks.clientInitialize();
        CEntity.clientInitialize();
    }
}
