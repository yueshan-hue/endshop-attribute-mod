package com.endshop.job.network;

import com.endshop.job.skill.ActiveSkill;
import com.endshop.job.skill.Skill;
import com.endshop.job.skill.SkillDataAttachment;
import com.endshop.job.skill.SkillRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;

/**
 * 客户端→服务端：使用技能请求
 */
public record UseSkillPacket(int slot) implements CustomPacketPayload {
    
    public static final Type<UseSkillPacket> TYPE = 
            new Type<>(ResourceLocation.fromNamespaceAndPath("endshopattribute", "use_skill"));
    
    public static final StreamCodec<FriendlyByteBuf, UseSkillPacket> CODEC = 
            StreamCodec.of(
                UseSkillPacket::encode,
                UseSkillPacket::decode
            );
    
    private static UseSkillPacket decode(FriendlyByteBuf buf) {
        return new UseSkillPacket(buf.readInt());
    }
    
    private static void encode(FriendlyByteBuf buf, UseSkillPacket packet) {
        buf.writeInt(packet.slot);
    }
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    /**
     * 服务端处理逻辑
     */
    public static void handle(UseSkillPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            
            // 获取玩家的技能数据
            SkillDataAttachment.SkillData skillData = SkillDataAttachment.getSkillData(player);
            
            // 获取该槽位装备的技能
            String skillId = skillData.getEquippedSkill(packet.slot);
            if (skillId == null) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§e 该槽位未装备技能！"));
                return;
            }
                        
            // 获取技能
            Skill skill = SkillRegistry.getSkill(skillId);
            if (!(skill instanceof ActiveSkill)) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§e 该技能不是主动技能！"));
                return;
            }
                        
            ActiveSkill activeSkill = (ActiveSkill) skill;
                        
            // 检查冷却时间（从 SkillData 中读取）
            int cooldown = skillData.getSkillCooldown(skillId);
            if (cooldown > 0) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§e 技能冷却中：§c" + cooldown + "§e 秒"));
                return;
            }
                        
            // 执行技能
            activeSkill.execute(player);
                        
            // 开始冷却（写入 SkillData）
            skillData.setSkillCooldown(skillId, activeSkill.getCooldown());
                        
            // 同步技能数据到客户端
            PacketDistributor.sendToPlayer(
                player,
                new SyncSkillDataPacket(
                    skillData.getUnlockedSkillsSet(),
                    skillData.getSkillLevelsMap(),
                    skillData.getEquippedSkillsMap(),
                    Map.of(skillId, activeSkill.getCooldown())
                )
            );
        });
    }
}
