package com.endshop.job.network;

import com.endshop.job.EndshopJob;
import com.endshop.job.data.AttributeDataAttachment;
import com.endshop.job.data.JobDataAttachment;
import com.endshop.job.profession.Profession;
import com.endshop.job.skill.SkillDataAttachment;
import com.endshop.job.skill.SkillRegistry;
import net.neoforged.neoforge.network.PacketDistributor;
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
            
            // 根据职业调整属性
            applyJobAttributes(serverPlayer, profession);
            
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
        professionSkills.put(Profession.GUARD, List.of("strength_boost", "true_blade", "slash_dash", "cleave_charge", "sequence_shock"));
        professionSkills.put(Profession.VANGUARD, List.of("quick_heal", "wisdom_boost", "agility_boost", "willpower_boost", "noise", "condensing_voice"));
        professionSkills.put(Profession.SPECIALIST, List.of("burnout", "inferno"));
        professionSkills.put(Profession.DEFENDER, List.of("hypothermia", "saturation_defense"));
            
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
                    net.minecraft.network.chat.Component.literal("§a 解锁技能：§f" + 
                        SkillRegistry.getSkill(skillId).getName()));
            }
        }
    }
        
    /**
     * 根据职业调整属性
     */
    private static void applyJobAttributes(ServerPlayer player, Profession profession) {
        // 获取当前属性
        AttributeDataAttachment.PlayerAttributes currentAttrs = AttributeDataAttachment.getAttributes(player);
            
        // 默认属性值
        int wisdom = AttributeDataAttachment.DEFAULT_WISDOM;
        int strength = AttributeDataAttachment.DEFAULT_STRENGTH;
        int agility = AttributeDataAttachment.DEFAULT_AGILITY;
        int willpower = AttributeDataAttachment.DEFAULT_WILLPOWER;
            
        // 根据职业调整属性
        if (profession == Profession.VANGUARD) {
            // 先锋干员：智识 12，力量 8，敏捷 14，意志 11
            wisdom = 12;
            strength = 8;
            agility = 14;
            willpower = 11;
        }
        // 可以在这里添加其他职业的属性调整
        // else if (profession == Profession.MEDIC) {
        //     // 医疗职业属性...
        // }
            
        // 应用新属性
        AttributeDataAttachment.PlayerAttributes newAttributes = new AttributeDataAttachment.PlayerAttributes(
            wisdom, strength, agility, willpower
        );
        AttributeDataAttachment.setAttributes(player, newAttributes);
            
        // 发送同步包到客户端
        SyncAttributePacket packet = new SyncAttributePacket(wisdom, strength, agility, willpower);
        PacketDistributor.sendToPlayer(player, packet);
            
        // 通知玩家属性变化
        player.sendSystemMessage(
            net.minecraft.network.chat.Component.literal(
                "§a 属性调整：智识 §f" + wisdom + 
                "§a, 力量 §f" + strength + 
                "§a, 敏捷 §f" + agility + 
                "§a, 意志 §f" + willpower
            )
        );
    }
}
