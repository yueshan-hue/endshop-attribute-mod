package com.endshop.job.network;

import com.endshop.job.EndshopJob;
import com.endshop.job.skill.SkillDataAttachment;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 服务端→客户端：同步技能数据
 */
public record SyncSkillDataPacket(Set<String> unlockedSkills, Map<String, Integer> skillLevels, 
                                   Map<Integer, String> equippedSkills, Map<String, Integer> skillCooldowns) implements CustomPacketPayload {

    public static final Type<SyncSkillDataPacket> TYPE = 
            new Type<>(ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, "sync_skill_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncSkillDataPacket> CODEC = 
            StreamCodec.composite(
                    ByteBufCodecs.collection(HashSet::new, ByteBufCodecs.STRING_UTF8),
                    SyncSkillDataPacket::unlockedSkills,
                    ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.VAR_INT),
                    SyncSkillDataPacket::skillLevels,
                    ByteBufCodecs.map(HashMap::new, ByteBufCodecs.VAR_INT, ByteBufCodecs.STRING_UTF8),
                    SyncSkillDataPacket::equippedSkills,
                    ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.VAR_INT),
                    SyncSkillDataPacket::skillCooldowns,
                    SyncSkillDataPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /** 客户端处理逻辑 */
    public static void handle(SyncSkillDataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof Player player)) return;
            
            // 获取客户端的技能数据
            SkillDataAttachment.SkillData skillData = SkillDataAttachment.getSkillData(player);
            
            // 使用公开方法设置数据（包含冷却时间）
            skillData.setSkillData(packet.unlockedSkills(), packet.skillLevels(), 
                                   packet.equippedSkills(), packet.skillCooldowns());
            
            System.out.println("[DEBUG] 客户端收到技能同步包");
            System.out.println("[DEBUG] 解锁技能：" + packet.unlockedSkills());
            System.out.println("[DEBUG] 技能等级：" + packet.skillLevels());
            System.out.println("[DEBUG] 装备技能：" + packet.equippedSkills());
            System.out.println("[DEBUG] 技能冷却：" + packet.skillCooldowns());
        });
    }
}
