package com.dindcrzy.corruptworld.mixin.client;

import com.dindcrzy.corruptworld.chunkcorrpution.CardinalChunk;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.function.BooleanSupplier;

@Mixin(ClientChunkManager.class)
public abstract class ClientChunkManagerMixin extends ChunkManager {
    @Shadow
    volatile ClientChunkManager.ClientChunkMap chunks;

    @Shadow @Final
    ClientWorld world;

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(BooleanSupplier shouldKeepTicking, boolean tickChunks, CallbackInfo ci) {
        // only forces a re-render every second, instead of every frame that theres a change
        if (world.getLevelProperties().getTime() % 20 == 0) {
            HashSet<ChunkPos> toReload = new HashSet<>();
            for (int i = 0; i < chunks.chunks.length(); i++) {
                // a chunks corruption is considered "dirty" if it was changed
                if (CardinalChunk.getDirty(chunks.chunks.get(i))) {
                    CardinalChunk.setDirty(chunks.chunks.get(i), false);
                    ChunkPos centerPos = chunks.chunks.get(i).getPos();
                    // marks the 3x3 square of chunks around the change to be reloaded
                    // accounts for biome blend
                    for (int j = -1; j <= 1 ; j++) {
                        for (int k = -1; k <= 1; k++) {
                            toReload.add(new ChunkPos(centerPos.x + j, centerPos.z + k));
                        }
                    }
                }
            }
            for (ChunkPos pos : toReload) {
                for (int i = 0; i < world.countVerticalSections(); i++) {
                    world.resetChunkColor(pos);
                    ((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer)
                            .chunkRender(pos.x, i, pos.z, true);
                }
            }
        }
    }
}
