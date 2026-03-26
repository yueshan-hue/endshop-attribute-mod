package com.endshop.job.client.renderer;

import com.endshop.job.EndshopJob;
import com.endshop.job.entity.EndshopEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

/**
 * 终末地实体渲染器
 */
public class EndshopEntityRenderer extends HumanoidMobRenderer<EndshopEntity, PlayerModel<EndshopEntity>> {

    private final ResourceLocation texture;

    public EndshopEntityRenderer(EntityRendererProvider.Context context, ResourceLocation texture) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
        this.texture = texture;
        // 移除装甲层，暂时不添加
    }

    @Override
    public ResourceLocation getTextureLocation(EndshopEntity entity) {
        return texture;
    }

    // 实体纹理路径映射
    public static ResourceLocation getTextureForEntity(String entityName) {
        return ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, "textures/entity/" + entityName + ".png");
    }
}
