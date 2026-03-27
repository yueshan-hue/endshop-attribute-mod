package com.endshop.job.skill.skills;

import com.endshop.job.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;

/**
 * 示例主动技能 - 快速治疗
 */
public class QuickHealSkill extends ActiveSkill {
    
    /** 治疗量 */
    private final float healAmount;
    
    public QuickHealSkill() {
        super("quick_heal", "快速治疗", "立即恢复生命值", 5, 30, "heal_wave");
        this.healAmount = 6.0f;
    }
    
    @Override
    public void execute(Player player) {
        if (player == null) return;
        
        // 计算实际治疗量（可以基于技能等级）
        float actualHeal = healAmount * getCurrentLevel();
        
        // 治疗玩家
        player.heal(actualHeal);
        
        // 发送消息提示
        player.sendSystemMessage(
            Component.literal("§a使用快速治疗！恢复了 " + actualHeal + " 点生命值")
        );
    }
    
    @Override
    public boolean isUnlocked() {
        // 这里可以根据职业、等级等条件判断是否解锁
        // 暂时返回 true
        return true;
    }
    
    @Override
    public ActiveSkill copy() {
        return new QuickHealSkill();
    }
}
