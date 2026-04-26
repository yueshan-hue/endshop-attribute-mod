package com.endshop.job.network;

import com.endshop.job.EndshopJob;
import com.endshop.job.entity.EndshopEntityTypes;
import com.endshop.job.entity.JetDroneEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 召唤无人机数据包 - 客户端发送给服务端，请求召唤无人机
 */
public record SummonDronePayload() implements CustomPacketPayload {
    public static final Type<SummonDronePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, "summon_drone"));
    public static final StreamCodec<FriendlyByteBuf, SummonDronePayload> CODEC = StreamCodec.unit(new SummonDronePayload());

    public static void handle(SummonDronePayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) {
                var level = player.serverLevel();
                
                // 计算生成位置（玩家前方3格，上方2格）
                double spawnDistance = 3.0;
                double yaw = Math.toRadians(player.getYRot());
                
                double spawnX = player.getX() - Math.sin(yaw) * spawnDistance;
                double spawnY = player.getY() + 2.0;
                double spawnZ = player.getZ() + Math.cos(yaw) * spawnDistance;
                
                EndshopJob.LOGGER.info("[无人机召唤-服务端] 召唤无人机 at X:{} Y:{} Z:{}", spawnX, spawnY, spawnZ);
                
                // 创建无人机实体
                JetDroneEntity drone = new JetDroneEntity(
                    EndshopEntityTypes.JET_DRONE.get(),
                    level
                );
                
                drone.setPos(spawnX, spawnY, spawnZ);
                drone.setYRot(player.getYRot());
                drone.setState(1); // 设置为飞行状态
                drone.setLifetime(600); // 生存 30 秒
                
                level.addFreshEntity(drone);
                
                EndshopJob.LOGGER.info("[无人机召唤-服务端] 无人机实体已添加到世界");
                
                // 生成额外的粒子爆发效果
                for (int i = 0; i < 15; i++) {
                    level.sendParticles(
                        ParticleTypes.CLOUD,
                        spawnX + (Math.random() - 0.5) * 1.0,
                        spawnY + (Math.random() - 0.5) * 1.0,
                        spawnZ + (Math.random() - 0.5) * 1.0,
                        1,
                        (Math.random() - 0.5) * 0.2,
                        Math.random() * 0.2,
                        (Math.random() - 0.5) * 0.2,
                        0.0
                    );
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
