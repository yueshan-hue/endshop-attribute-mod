package com.endshop.job.network;

import com.endshop.job.EndshopJob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = EndshopJob.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModNetwork {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        
        // 注册召唤无人机数据包（客户端 -> 服务端）
        registrar.playToServer(
            SummonDronePayload.TYPE,
            SummonDronePayload.CODEC,
            SummonDronePayload::handle
        );
        
        // 注册射击指令数据包（客户端 -> 服务端）
        registrar.playToServer(
            DroneShootPayload.TYPE,
            DroneShootPayload.CODEC,
            DroneShootPayload::handle
        );
    }
}
