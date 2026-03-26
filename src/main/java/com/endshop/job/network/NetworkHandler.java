package com.endshop.job.network;

import com.endshop.job.EndshopJob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * 网络包注册
 */
@EventBusSubscriber(modid = EndshopJob.MODID, bus = EventBusSubscriber.Bus.MOD) // MOD bus
public class NetworkHandler {

    @SubscribeEvent
    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        // 注册 客户端→服务端 的设置职业包
        registrar.playToServer(
                SetJobPacket.TYPE,
                SetJobPacket.CODEC,
                SetJobPacket::handle
        );
    }
}
