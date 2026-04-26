package com.endshop.job.client.renderer;

import com.endshop.job.EndshopJob;
import com.endshop.job.entity.ErosionCoreEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * 侵蚀核心模型
 */
public class ErosionCoreModel extends GeoModel<ErosionCoreEntity> {
    
    @Override
    public ResourceLocation getModelResource(ErosionCoreEntity object) {
        return ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, "geo/entity/erosion_core.geo.json");
    }
    
    @Override
    public ResourceLocation getTextureResource(ErosionCoreEntity object) {
        return ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, "textures/entity/erosion_core.png");
    }
    
    @Override
    public ResourceLocation getAnimationResource(ErosionCoreEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, "animations/entity/erosion_core.animation.json");
    }
}
