package com.endshop.job.keybind;

import com.endshop.job.network.UseSkillPacket;
import com.endshop.job.skill.ActiveSkill;
import com.endshop.job.skill.Skill;
import com.endshop.job.skill.SkillDataAttachment;
import com.endshop.job.skill.SkillRegistry;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 技能执行器 - 处理按键释放技能
 */
public class SkillExecutor {
    
    /**
     * 使用指定槽位的技能
     * @param slot 槽位索引 (0-3)
     */
    public static void useSkill(int slot) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        // 获取玩家的技能数据
        SkillDataAttachment.SkillData skillData = SkillDataAttachment.getSkillData(mc.player);
        
        // 获取该槽位装备的技能
        String skillId = skillData.getEquippedSkill(slot);
        if (skillId == null) {
            mc.player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§e该槽位未装备技能！"));
            return;
        }
        
        // 获取技能
        Skill skill = SkillRegistry.getSkill(skillId);
        if (!(skill instanceof ActiveSkill)) {
            mc.player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§e该技能不是主动技能！"));
            return;
        }
        
        ActiveSkill activeSkill = (ActiveSkill) skill;
                
        // 检查冷却时间（从 SkillData 中读取）
        int cooldown = skillData.getSkillCooldown(skillId);
        if (cooldown > 0) {
            mc.player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§e 技能冷却中：§c" + cooldown + "§e 秒"));
            return;
        }
        
        // 发送使用技能包到服务端
        UseSkillPacket packet = new UseSkillPacket(slot);
        PacketDistributor.sendToServer(packet);
    }
}
