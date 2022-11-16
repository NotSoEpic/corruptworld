package com.dindcrzy.corruptworld.mixin;

import com.dindcrzy.corruptworld.ChunkCorruption;
import com.dindcrzy.corruptworld.IChunk;
import com.dindcrzy.corruptworld.IWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
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
                    ((IWorld) getWorld()).setCorruption(true, getBlockX() + i, getBlockZ() + j);
                }
            }
        }
    }
}
