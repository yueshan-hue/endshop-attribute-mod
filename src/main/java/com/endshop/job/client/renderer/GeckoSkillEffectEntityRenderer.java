package com.endshop.job.client.renderer;

import com.endshop.job.EndshopJob;
import com.endshop.job.entity.GeckoSkillEffectEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * GeckoLib 技能特效实体渲染器
 */
public class GeckoSkillEffectEntityRenderer extends GeoEntityRenderer<GeckoSkillEffectEntity> {
    
    public GeckoSkillEffectEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new GeckoSkillEffectModel());
    }
    
    @Override
    public ResourceLocation getTextureLocation(GeckoSkillEffectEntity entity) {
        String effectType = entity.getEffectType();
        if (effectType == null || effectType.isEmpty() || effectType.equals("default")) {
            effectType = "ice_crystal";
        }
        return ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, 
            "textures/entity/skill_effects/" + effectType + ".png");
    }
}
