package com.dindcrzy.corruptworld.entities.projectiles.attackspore;

import com.dindcrzy.corruptworld.entities.projectiles.ABezierPE;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class AttackSporeProjectile extends ABezierPE {
    public AttackSporeProjectile(EntityType<? extends ABezierPE> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void onBlockHit(BlockHitResult result) {
        kill();
    }

    @Override
    protected void onEntityHit(EntityHitResult result) {
        result.getEntity().damage(DamageSource.MAGIC, 4);
        discard();
    }
}
