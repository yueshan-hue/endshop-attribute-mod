package com.endshop.job.client.renderer;

import com.endshop.job.EndshopJob;
import com.endshop.job.entity.JetDroneEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * 射流无人机模型
 */
public class JetDroneModel extends GeoModel<JetDroneEntity> {
    
    @Override
    public ResourceLocation getModelResource(JetDroneEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, 
            "geo/entity/jet_drone.geo.json");
    }
    
    @Override
    public ResourceLocation getTextureResource(JetDroneEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, 
            "textures/entity/jet_drone.png");
    }
    
    @Override
    public ResourceLocation getAnimationResource(JetDroneEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, 
            "animations/entity/jet_drone.animation.json");
    }
}
