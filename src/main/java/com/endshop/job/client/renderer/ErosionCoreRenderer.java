package com.endshop.job.client.renderer;

import com.endshop.job.entity.ErosionCoreEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 侵蚀核心渲染器
 */
public class ErosionCoreRenderer extends GeoEntityRenderer<ErosionCoreEntity> {
    
    public ErosionCoreRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ErosionCoreModel());
        
        // 设置阴影大小
        this.shadowRadius = 0.5f;
    }
    
    @Override
    public void render(ErosionCoreEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                      MultiBufferSource bufferSource, int packedLight) {
        // 缩放实体
        poseStack.scale(1.2f, 1.2f, 1.2f);
        
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
