package com.endshop.job;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * 客户端专用类 - 仅在客户端加载
 */
@Mod(value = EndshopJob.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = EndshopJob.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class EndshopJobClient {

    public EndshopJobClient() {
        // 客户端初始化
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EndshopJob.LOGGER.info("[终末地职业] 客户端加载完成，玩家: {}",
                Minecraft.getInstance().getUser().getName());
    }
}
