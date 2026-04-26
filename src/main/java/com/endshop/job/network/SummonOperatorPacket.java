package com.endshop.job.network;

import com.endshop.job.EndshopJob;
import com.endshop.job.entity.EndshopEntity;
import com.endshop.job.entity.EndshopEntityTypes;
import com.endshop.job.entity.EntitySingletonManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

/**
 * 客户端→服务端：召唤干员
 */
public record SummonOperatorPacket(String operatorId) implements CustomPacketPayload {

    public static final Type<SummonOperatorPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, "summon_operator")
    );

    public static final StreamCodec<FriendlyByteBuf, SummonOperatorPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SummonOperatorPacket::operatorId,
            SummonOperatorPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * 处理召唤请求（在服务端线程执行）
     */
    public static void handle(SummonOperatorPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                handleSummon(player, packet.operatorId());
            }
        });
    }

    private static void handleSummon(ServerPlayer player, String operatorId) {
        try {
            // 检查是否已达上限
            java.util.Set<String> spawnedOperators = EntitySingletonManager.getSpawnedEntityTypes();
            if (spawnedOperators.size() >= 4) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c干员数量已达上限(4个)!"));
                return;
            }

            // 检查该干员是否已经存在
            if (EntitySingletonManager.isEntityTypeSpawned(operatorId)) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c干员【" + getOperatorName(operatorId) + "】已经存在！"));
                return;
            }

            // 获取实体类型
            var entityType = getEntityTypeById(operatorId);
            if (entityType == null) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c未知的干员ID：" + operatorId));
                return;
            }

            // 在玩家位置召唤实体
            var level = player.level();
            var pos = player.position();
            
            EndshopEntity entity = new EndshopEntity(entityType, level);
            entity.setPos(pos.x, pos.y, pos.z);
            level.addFreshEntity(entity);

            // 标记已召唤
            EntitySingletonManager.markEntitySpawned(operatorId, entity.getUUID());

            // 同步更新所有客户端的干员列表
            syncOperatorListToAll(player);

            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§a成功召唤干员【" + getOperatorName(operatorId) + "】！"));

        } catch (Exception e) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c召唤干员失败：" + e.getMessage()));
            EndshopJob.LOGGER.error("召唤干员失败", e);
        }
    }

    /**
     * 根据ID获取实体类型
     */
    private static net.minecraft.world.entity.EntityType<EndshopEntity> getEntityTypeById(String operatorId) {
        return switch (operatorId) {
            case "yvon" -> EndshopEntityTypes.YVON.get();
            case "perica" -> EndshopEntityTypes.PERICA.get();
            case "surtr" -> EndshopEntityTypes.SURTR.get();
            case "gerpeita" -> EndshopEntityTypes.GERPEITA.get();
            case "rosie" -> EndshopEntityTypes.ROSIE.get();
            case "admin_b" -> EndshopEntityTypes.ADMIN_B.get();
            case "chen_qianyu" -> EndshopEntityTypes.CHEN_QIANYU.get();
            case "bieli" -> EndshopEntityTypes.BIELI.get();
            case "etera" -> EndshopEntityTypes.ETERA.get();
            case "zhuang_fangyi" -> EndshopEntityTypes.ZHUANG_FANGYI.get();
            case "tangtang" -> EndshopEntityTypes.TANGTANG.get();
            case "qiuli" -> EndshopEntityTypes.QIULI.get();
            case "admin_a" -> EndshopEntityTypes.ADMIN_A.get();
            case "yingshi" -> EndshopEntityTypes.YINGSHI.get();
            case "saixi" -> EndshopEntityTypes.SAIXI.get();
            case "junwei" -> EndshopEntityTypes.JUNWEI.get();
            case "aidera" -> EndshopEntityTypes.AIDERA.get();
            default -> null;
        };
    }

    /**
     * 获取干员中文名
     */
    private static String getOperatorName(String operatorId) {
        return switch (operatorId) {
            case "yvon" -> "伊冯";
            case "perica" -> "佩丽卡";
            case "surtr" -> "史尔特尔";
            case "gerpeita" -> "洁尔佩塔";
            case "rosie" -> "洛茜";
            case "admin_b" -> "管理员B";
            case "chen_qianyu" -> "陈千语";
            case "bieli" -> "别礼";
            case "etera" -> "埃特拉";
            case "zhuang_fangyi" -> "庄方宜";
            case "tangtang" -> "汤汤";
            case "qiuli" -> "秋栗";
            case "admin_a" -> "管理员A";
            case "yingshi" -> "萤石";
            case "saixi" -> "赛希";
            case "junwei" -> "骏卫";
            case "aidera" -> "艾尔黛拉";
            default -> operatorId;
        };
    }

    /**
     * 同步干员列表到所有玩家
     */
    private static void syncOperatorListToAll(ServerPlayer player) {
        List<String> operatorList = new java.util.ArrayList<>(EntitySingletonManager.getSpawnedEntityTypes());
        SyncOperatorListPacket packet = new SyncOperatorListPacket(operatorList);
        PacketDistributor.sendToAllPlayers(packet);
    }
}
