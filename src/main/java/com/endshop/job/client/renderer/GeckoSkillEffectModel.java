package com.endshop.job.client.renderer;

import com.endshop.job.EndshopJob;
import com.endshop.job.entity.GeckoSkillEffectEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * GeckoLib 技能特效实体模型
 */
public class GeckoSkillEffectModel extends GeoModel<GeckoSkillEffectEntity> {
    
    @Override
    public ResourceLocation getModelResource(GeckoSkillEffectEntity entity) {
        String effectType = entity.getEffectType();
        // 如果 effectType 为空，使用默认的冰晶模型
        if (effectType == null || effectType.isEmpty()) {
            effectType = "ice_crystal";
        }
        return ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, 
            "geo/entity/skill_effects/" + effectType + ".geo.json");
    }
    
    @Override
    public ResourceLocation getTextureResource(GeckoSkillEffectEntity entity) {
        String effectType = entity.getEffectType();
        // 如果 effectType 为空，使用默认的冰晶纹理
        if (effectType == null || effectType.isEmpty()) {
            effectType = "ice_crystal";
        }
        return ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, 
            "textures/entity/skill_effects/" + effectType + ".png");
    }
    
    @Override
    public ResourceLocation getAnimationResource(GeckoSkillEffectEntity entity) {
        String effectType = entity.getEffectType();
        // 如果 effectType 为空，使用默认的冰晶动画
        if (effectType == null || effectType.isEmpty()) {
            effectType = "ice_crystal";
        }
        return ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, 
            "animations/entity/skill_effects/" + effectType + ".animation.json");
    }
}
