package com.endshop.job.client.renderer;

import com.endshop.job.EndshopJob;
import com.endshop.job.entity.JetDroneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 射流无人机渲染器
 */
public class JetDroneRenderer extends GeoEntityRenderer<JetDroneEntity> {
    
    public JetDroneRenderer(EntityRendererProvider.Context context) {
        super(context, new JetDroneModel());
    }
    
    @Override
    public ResourceLocation getTextureLocation(JetDroneEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, 
            "textures/entity/jet_drone.png");
    }
}
