package com.endshop.job.network;

import com.endshop.job.EndshopJob;
import com.endshop.job.entity.JetDroneEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DroneShootPayload() implements CustomPacketPayload {
    public static final Type<DroneShootPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, "drone_shoot"));
    public static final StreamCodec<FriendlyByteBuf, DroneShootPayload> CODEC = StreamCodec.unit(new DroneShootPayload());

    public static void handle(DroneShootPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) {
                EndshopJob.LOGGER.info("[无人机控制-服务端] 收到射击指令，玩家: {}", player.getName().getString());
                
                // 在服务端查找玩家周围的无人机并执行射击
                var entities = player.level().getEntities(player, player.getBoundingBox().inflate(32), e -> e instanceof JetDroneEntity);
                EndshopJob.LOGGER.info("[无人机控制-服务端] 找到 {} 个无人机", entities.size());
                
                for (Entity entity : entities) {
                    if (entity instanceof JetDroneEntity drone) {
                        EndshopJob.LOGGER.info("[无人机控制-服务端] 无人机执行射击，距离: {}", player.distanceTo(drone));
                        drone.shootBeam();
                    }
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
