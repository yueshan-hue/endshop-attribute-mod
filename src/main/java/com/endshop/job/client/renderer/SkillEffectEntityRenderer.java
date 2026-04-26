package com.endshop.job.client.renderer;

import com.endshop.job.EndshopJob;
import com.endshop.job.entity.SkillEffectEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

/**
 * 技能特效实体渲染器
 */
public class SkillEffectEntityRenderer extends EntityRenderer<SkillEffectEntity> {
    
    public SkillEffectEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    
    @Override
    public void render(SkillEffectEntity entity, float entityYaw, float partialTick, 
                      PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        
        // 这里可以根据 entity.getEffectType() 渲染不同的模型
        // 目前先使用简单的粒子效果作为占位
        
        String effectType = entity.getEffectType();
        
        // TODO: 根据特效类型渲染不同的自定义模型
        // 例如: if ("ice_crystal".equals(effectType)) { renderIceCrystal(...); }
    }
    
    @Override
    public ResourceLocation getTextureLocation(SkillEffectEntity entity) {
        // 返回对应特效类型的纹理
        String effectType = entity.getEffectType();
        return ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, 
            "textures/entity/skill_effects/" + effectType + ".png");
    }
}
