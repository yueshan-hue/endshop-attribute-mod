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
        
        // 注册 服务端→客户端 的技能数据同步包
        registrar.playToClient(
                SyncSkillDataPacket.TYPE,
                SyncSkillDataPacket.CODEC,
                SyncSkillDataPacket::handle
        );
        
        // 注册 服务端→客户端 的属性数据同步包
        registrar.playToClient(
                SyncAttributePacket.TYPE,
                SyncAttributePacket.CODEC,
                SyncAttributePacket::handle
        );
        
        // 注册 客户端→服务端 的使用技能包
        registrar.playToServer(
                UseSkillPacket.TYPE,
                UseSkillPacket.CODEC,
                UseSkillPacket::handle
        );
    }
}
