package com.dindcrzy.corruptworld.entities.projectiles.attackspore;

import com.dindcrzy.corruptworld.CorruptWorld;
import com.dindcrzy.corruptworld.entities.CEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class AttackSporeProjectileRenderer extends EntityRenderer<AttackSporeProjectile> {
    protected SporeModel<AttackSporeProjectile> model;
    
    public AttackSporeProjectileRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new SporeModel<>(context.getPart(SporeModel.LAYER_LOCATION));
    }

    @Override
    public void render(AttackSporeProjectile entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        matrixStack.push();
        RenderLayer renderLayer = model.getLayer(getTexture(entity));
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        float theta = entity.age + tickDelta;
        //matrixStack.translate(-0.25f, -0.25f, -0.25f);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.sin(theta * 0.1f) * 180.0f));
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(MathHelper.cos(theta * 0.1f) * 180.0f));
        matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.sin(theta * 0.15f) * 360.0f));
        //matrixStack.translate(-0.25f, -0.25f, -0.25f);
        if (renderLayer != null) {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
            boolean translucent = entity.isInvisible() && !entity.isInvisibleTo(minecraftClient.player);
            // todo: why the fuck is it red
            model.render(matrixStack, vertexConsumer, light, 0, 1.0f, 1.0f, 1.0f, translucent ? 0.15f : 1.0f);
        }
        matrixStack.pop();
        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);
    }

    @Override
    public Identifier getTexture(AttackSporeProjectile entity) {
        return CorruptWorld.ModId("textures/entity/attack_spore.png");
    }
}
