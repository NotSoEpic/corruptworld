package com.dindcrzy.corruptworld;

import com.dindcrzy.corruptworld.mixin.client.WorldRendererAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.chunk.ChunkBuilder;

public class ClientMod implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(
            ChunkCorruption.PACKET_ID, ((client, handler, buf, sender) -> {
                ChunkCorruption corruption = new ChunkCorruption(buf);
                if (client.world != null) {
                    IChunk chunk = (IChunk) client.world.getChunk(corruption.pos.x, corruption.pos.z);
                    if (chunk != null) {
                        chunk.setChunkCorruption(corruption);
                        /*client.world.reloadColor();
                        int x = corruption.pos.x;
                        int miny = client.world.getBottomSectionCoord();
                        int maxy = client.world.getTopSectionCoord();
                        int z = corruption.pos.z;
                        client.worldRenderer.scheduleBlockRenders(x-1, miny, z-1, x+1, maxy, z+1);
                        for (int i = x-1; i <= x+1; i++) {
                            for (int j = z-1; j <= z+1; j++) {
                                client.world.markChunkRenderability(x, z);
                            }
                        }
                        client.worldRenderer.scheduleTerrainUpdate();*/
                    }
                }
            })
        );
    }
}
