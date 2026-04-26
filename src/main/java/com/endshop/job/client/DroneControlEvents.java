package com.endshop.job.client;

import com.endshop.job.EndshopJob;
import com.endshop.job.entity.JetDroneEntity;
import com.endshop.job.network.DroneShootPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 无人机控制事件 - 监听玩家左键点击控制无人机
 */
@EventBusSubscriber(modid = EndshopJob.MODID, value = Dist.CLIENT)
public class DroneControlEvents {
    
    @SubscribeEvent
    public static void onPlayerLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Minecraft mc = Minecraft.getInstance();
        
        if (mc.player == null || mc.level == null) {
            return;
        }
        
        // 无条件输出日志，确认事件被触发
        EndshopJob.LOGGER.info("[无人机控制] 检测到左键点击事件");
        
        // 直接发送数据包到服务端，让服务端处理所有逻辑
        // 服务端会查找附近的无人机并执行射击
        PacketDistributor.sendToServer(new DroneShootPayload());
        EndshopJob.LOGGER.info("[无人机控制] 已发送射击指令到服务端（由服务端查找无人机）");
    }
}
