package com.nightfall.stalkers.client.render;

import com.nightfall.stalkers.entity.StalkerEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/** Uses the unnaturally tall enderman silhouette while providing a custom skin. */
public final class StalkerEntityRenderer extends MobEntityRenderer<StalkerEntity, BipedEntityModel<StalkerEntity>> {
    private static final Identifier TEXTURE = new Identifier("minecraft", "textures/entity/enderman/enderman.png");

    public StalkerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BipedEntityModel<>(context.getPart(EntityModelLayers.PLAYER)), 0.45F);
    }

    @Override
    public Identifier getTexture(StalkerEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(StalkerEntity entity, MatrixStack matrices, float amount) {
        matrices.scale(0.78F, 1.7F, 0.78F);
    }
}
