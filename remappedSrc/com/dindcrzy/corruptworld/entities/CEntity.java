package com.dindcrzy.corruptworld.entities;

import com.dindcrzy.corruptworld.CorruptWorld;
import com.dindcrzy.corruptworld.entities.projectiles.attackspore.AttackSporeProjectile;
import com.dindcrzy.corruptworld.entities.projectiles.attackspore.AttackSporeProjectileRenderer;
import com.dindcrzy.corruptworld.entities.projectiles.attackspore.SporeModel;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;

public class CEntity {
    public static final EntityType<AttackSporeProjectile> ATTACK_SPORE = Registry.register(
            Registry.ENTITY_TYPE,
            CorruptWorld.ModId("attack_spore"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, AttackSporeProjectile::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                .build()
    );
    
    public static void initialize() {
        ExtraDataTracker.initialize();
    }
    
    
    public static void clientInitialize() {
        EntityRendererRegistry.register(ATTACK_SPORE, AttackSporeProjectileRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(SporeModel.LAYER_LOCATION, SporeModel::createBodyLayer);
    }
}
