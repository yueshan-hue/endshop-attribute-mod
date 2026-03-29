package com.endshop.job.network;

import com.endshop.job.EndshopJob;
import com.endshop.job.data.AttributeDataAttachment;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 服务端→客户端：同步属性数据
 */
public record SyncAttributePacket(int wisdom, int strength, int agility, int willpower) implements CustomPacketPayload {

    public static final Type<SyncAttributePacket> TYPE = 
            new Type<>(ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, "sync_attributes"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncAttributePacket> CODEC = 
            StreamCodec.composite(
                    ByteBufCodecs.INT,
                    SyncAttributePacket::wisdom,
                    ByteBufCodecs.INT,
                    SyncAttributePacket::strength,
                    ByteBufCodecs.INT,
                    SyncAttributePacket::agility,
                    ByteBufCodecs.INT,
                    SyncAttributePacket::willpower,
                    SyncAttributePacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /** 客户端处理逻辑 */
    public static void handle(SyncAttributePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof Player player)) return;
            
            // 更新客户端属性数据
            AttributeDataAttachment.setAttributes(player, 
                new AttributeDataAttachment.PlayerAttributes(
                    packet.wisdom(),
                    packet.strength(),
                    packet.agility(),
                    packet.willpower()
                )
            );
            
            System.out.println("[DEBUG] 客户端收到属性同步包");
            System.out.println("[DEBUG] 智识：" + packet.wisdom());
            System.out.println("[DEBUG] 力量：" + packet.strength());
            System.out.println("[DEBUG] 敏捷：" + packet.agility());
            System.out.println("[DEBUG] 意志：" + packet.willpower());
        });
    }
}
