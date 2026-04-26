package com.endshop.job.client;

import com.endshop.job.EndshopJob;
import com.endshop.job.fluid.ModFluidTypes;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

/**
 * 客户端专用类 - 处理流体渲染等客户端功能
 */
@Mod(value = EndshopJob.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = EndshopJob.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientModConstructor {
    
    /**
     * 注册客户端流体扩展（用于渲染）
     */
    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        EndshopJob.LOGGER.info("[终末地职业] 注册客户端流体渲染扩展");
        
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, "block/erosion_still");
            }
            
            @Override
            public ResourceLocation getFlowingTexture() {
                return ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, "block/erosion_flow");
            }
            
            @Override
            public int getTintColor() {
                return 0xFF8B00FF; // 紫色 (ARGB格式)
            }
        }, ModFluidTypes.EROSION_FLUID_TYPE.get());
    }
}
