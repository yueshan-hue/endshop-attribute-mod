package com.endshop.job.skill;

import net.minecraft.network.chat.Component;

/**
 * 技能装备工具类 - 帮助管理技能槽位
 */
public class SkillEquipHelper {
    
    /**
     * 获取槽位的显示名称
     */
    public static Component getSlotName(int slot) {
        SkillSlot skillSlot = SkillSlot.fromIndex(slot);
        if (skillSlot == null) {
            return Component.literal("未知槽位");
        }
        
        String key = "skill.slot." + skillSlot.getName();
        return Component.translatable(key);
    }
    
    /**
     * 获取槽位绑定的按键提示
     */
    public static String getSlotKeyBinding(int slot) {
        SkillSlot skillSlot = SkillSlot.fromIndex(slot);
        if (skillSlot == null) {
            return "Unknown";
        }
        return skillSlot.getKeyBinding().toUpperCase();
    }
    
    /**
     * 检查技能是否可以装备到指定槽位
     */
    public static boolean canEquipSkill(SkillDataAttachment.SkillData skillData, 
                                        String skillId, int slot) {
        if (slot < 0 || slot > 3) {
            return false;
        }
        
        if (!skillData.isUnlocked(skillId)) {
            return false;
        }
        
        // 检查是否是主动技能（只有主动技能才能装备）
        Skill skill = SkillRegistry.getSkill(skillId);
        if (!(skill instanceof ActiveSkill)) {
            return false;
        }
        
        return true;
    }
}
