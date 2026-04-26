package com.endshop.job.network;

import com.endshop.job.EndshopJob;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务端→客户端：同步已召唤的干员列表
 */
public record SyncOperatorListPacket(List<String> operatorIds) implements CustomPacketPayload {

    public static final Type<SyncOperatorListPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, "sync_operator_list")
    );

    public static final StreamCodec<FriendlyByteBuf, SyncOperatorListPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.STRING_UTF8),
            SyncOperatorListPacket::operatorIds,
            SyncOperatorListPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * 处理接收到的包（在客户端线程执行）
     */
    public static void handle(SyncOperatorListPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            // 更新客户端的干员列表缓存
            com.endshop.job.client.OperatorListCache.update(packet.operatorIds());
        });
    }
}
