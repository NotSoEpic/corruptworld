package com.dindcrzy.corruptworld.mixin.client;

import com.dindcrzy.corruptworld.IChunk;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.BiomeColorCache;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.random.SimpleRandom;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Redirect(method = "calculateColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ColorResolver;getColor(Lnet/minecraft/world/biome/Biome;DD)I"))
    private int yoWhyHeOurple(ColorResolver instance, Biome biome, double x, double z) {
        IChunk chunk = (IChunk)MinecraftClient.getInstance().world.getChunk(new BlockPos(x, 0, z));
        if (chunk != null && chunk.getChunkCorruption() != null && chunk.getChunkCorruption().getAbs((int)x, (int)z)) {
            return 0x843BA8;
        }
        return instance.getColor(biome, x, z);
    }
}
