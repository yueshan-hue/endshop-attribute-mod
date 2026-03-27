package com.endshop.job.network;

import com.endshop.job.EndshopJob;
import com.endshop.job.data.JobDataAttachment;
import com.endshop.job.profession.Profession;
import com.endshop.job.skill.SkillDataAttachment;
import com.endshop.job.skill.SkillRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户端→服务端：请求设置职业（从GUI选择后发送）
 */
public record SetJobPacket(String jobName) implements CustomPacketPayload {

    public static final Type<SetJobPacket> TYPE = 
            new Type<>(ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, "set_job"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetJobPacket> CODEC = 
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    SetJobPacket::jobName,
                    SetJobPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /** 服务端处理逻辑 */
    public static void handle(SetJobPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) return;
            Profession profession = Profession.fromDisplayName(packet.jobName());
            if (profession == null) {
                // 尝试按枚举名解析
                try {
                    profession = Profession.valueOf(packet.jobName());
                } catch (IllegalArgumentException e) {
                    EndshopJob.LOGGER.warn("收到无效职业名：{}", packet.jobName());
                    return;
                }
            }
            JobDataAttachment.setJob(serverPlayer, profession);
            
            // 自动解锁该职业的初始技能
            unlockJobSkills(serverPlayer, profession);
            
            // 通知玩家
            if (profession == Profession.NONE) {
                serverPlayer.sendSystemMessage(
                        net.minecraft.network.chat.Component.literal("§7你已清除职业。"));
            } else {
                serverPlayer.sendSystemMessage(
                        net.minecraft.network.chat.Component.literal(
                                "§a你的职业已更改为 " + profession.getColoredPrefix() + profession.getDisplayName() + "§a！"));
            }
        });
    }
    
    /**
     * 自动解锁该职业的初始技能
     */
    private static void unlockJobSkills(ServerPlayer player, Profession profession) {
        SkillDataAttachment.SkillData skillData = SkillDataAttachment.getSkillData(player);
        
        // 获取该职业对应的技能（直接在这里定义映射）
        Map<Profession, List<String>> professionSkills = new HashMap<>();
        professionSkills.put(Profession.MEDIC, List.of("quick_heal"));
        professionSkills.put(Profession.GUARD, List.of("strength_boost"));
        professionSkills.put(Profession.VANGUARD, List.of("quick_heal", "strength_boost"));
        
        // 默认所有职业都可以使用所有技能
        for (Profession p : Profession.values()) {
            professionSkills.putIfAbsent(p, List.of("quick_heal", "strength_boost"));
        }
        
        List<String> jobSkills = professionSkills.getOrDefault(profession, List.of());
        
        // 自动解锁这些技能
        for (String skillId : jobSkills) {
            if (!skillData.isUnlocked(skillId)) {
                skillData.unlockSkill(skillId);
                player.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal("§a解锁技能：§f" + 
                        SkillRegistry.getSkill(skillId).getName()));
            }
        }
    }
}
