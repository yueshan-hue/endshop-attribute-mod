package com.endshop.job.client.renderer;

import com.endshop.job.EndshopJob;
import com.endshop.job.entity.EndshopEntity;
import com.endshop.job.entity.EndshopEntityTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

/**
 * 终末地实体渲染器注册
 */
@EventBusSubscriber(modid = EndshopJob.MODID, value = Dist.CLIENT)
public class EndshopEntityRenderers {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 注册终末地实体渲染器
        registerEntityRenderer(event, "yvon", EndshopEntityTypes.YVON.get());
        registerEntityRenderer(event, "perica", EndshopEntityTypes.PERICA.get());
        registerEntityRenderer(event, "surtr", EndshopEntityTypes.SURTR.get());
        registerEntityRenderer(event, "gerpeita", EndshopEntityTypes.GERPEITA.get());
        registerEntityRenderer(event, "rosie", EndshopEntityTypes.ROSIE.get());
        registerEntityRenderer(event, "admin_b", EndshopEntityTypes.ADMIN_B.get());
        registerEntityRenderer(event, "chen_qianyu", EndshopEntityTypes.CHEN_QIANYU.get());
        registerEntityRenderer(event, "bieli", EndshopEntityTypes.BIELI.get());
        registerEntityRenderer(event, "etera", EndshopEntityTypes.ETERA.get());
        registerEntityRenderer(event, "zhuang_fangyi", EndshopEntityTypes.ZHUANG_FANGYI.get());
        registerEntityRenderer(event, "tangtang", EndshopEntityTypes.TANGTANG.get());
        registerEntityRenderer(event, "qiuli", EndshopEntityTypes.QIULI.get());
        registerEntityRenderer(event, "admin_a", EndshopEntityTypes.ADMIN_A.get());
        registerEntityRenderer(event, "yingshi", EndshopEntityTypes.YINGSHI.get());
        registerEntityRenderer(event, "saixi", EndshopEntityTypes.SAIXI.get());
    }

    private static void registerEntityRenderer(EntityRenderersEvent.RegisterRenderers event, String name, EntityType<? extends EndshopEntity> entityType) {
        event.registerEntityRenderer(entityType, (EntityRendererProvider.Context context) -> {
            ResourceLocation texture = EndshopEntityRenderer.getTextureForEntity(name);
            return new EndshopEntityRenderer(context, texture);
        });
    }
}
