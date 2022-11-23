package com.dindcrzy.corruptworld.entities.projectiles.attackspore;

import com.dindcrzy.corruptworld.CorruptWorld;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class SporeModel<T extends Entity> extends EntityModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(CorruptWorld.ModId("attack_spore"), "main");
    private final ModelPart bb_main;

    public SporeModel(ModelPart root) {
        this.bb_main = root.getChild(EntityModelPartNames.CUBE);
    }

    public static TexturedModelData createBodyLayer() {
        ModelData modelData = new ModelData();
        // why is it offset specifically 20/16 blocks upwards??????
        int vOff = -20;
        ModelPartData modelPartData = modelData.getRoot(); // -----------i have no clue how else to fix the massive vertical offset--------------V 
        modelPartData.addChild(EntityModelPartNames.CUBE, ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -3.0F + vOff, -3.0F, 6.0F, 6.0F, 6.0F)
                .uv(8, 12).cuboid(-4.0F, -4.0F + vOff, 0.0F, 2.0F, 2.0F, 2.0F)
                .uv(7, 16).cuboid(-1.0F, 0.0F + vOff, 3.0F, 3.0F, 2.0F, 1.0F)
                .uv(0, 12).cuboid(2.0F, 1.0F + vOff, -1.0F, 2.0F, 3.0F, 2.0F)
                .uv(0, 0).cuboid(-1.0F, -1.0F + vOff, -4.0F, 2.0F, 3.0F, 1.0F)
                .uv(14, 14).cuboid(-2.0F, 3.0F + vOff, -2.0F, 2.0F, 1.0F, 2.0F), 
                ModelTransform.pivot(-4, -2 + vOff, -4)); // todo: fix pivoting or whatever

        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        ImmutableList.of(this.bb_main).forEach((modelPart -> {
            modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        }));
    }
}