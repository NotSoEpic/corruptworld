package com.dindcrzy.corruptworld.mixin;

import com.dindcrzy.corruptworld.chunkcorrpution.CardinalChunk;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Shadow public abstract boolean isSpectator();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void testTick(CallbackInfo ci) {
        if (isSpectator()) {
            for (int i = -2; i < 3; i++) {
                for (int j = -2; j < 3; j++) {
                    int x = getBlockX() + i;
                    int z = getBlockZ() + j;
                    CardinalChunk.setCorruption(getWorld(), x, z, true);
                    CardinalChunk.syncCorruption(getWorld(), x, z);
                }
            }
        }
    }
}
