package com.endshop.job.skill.skills;

import com.endshop.job.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;

/**
 * 近卫干员 - 管理员（Endministrator）
 * 普攻技能：真刃 - 3 段物理伤害
 */
public class TrueBladeSkill extends ActiveSkill {
    
    public TrueBladeSkill() {
        super("true_blade", "True Blade", "3-hit physical damage attack", 10, 5, "true_blade");
    }
    
    @Override
    public void execute(Player player) {
        if (player == null) return;
        
        // 计算伤害（基于技能等级）
        float damage = 5.0f * getCurrentLevel();
        
        // 对周围敌人造成伤害
        player.level().getEntitiesOfClass(net.minecraft.world.entity.Mob.class, 
            player.getBoundingBox().inflate(5.0)).forEach(entity -> {
            entity.hurt(player.damageSources().playerAttack(player), damage);
        });
        
        player.sendSystemMessage(Component.literal("§f§l真刃 §7- 造成 §c" + damage + "§7 点物理伤害"));
    }
    
    @Override
    public boolean isUnlocked() {
        return true;
    }
    
    @Override
    public ActiveSkill copy() {
        return new TrueBladeSkill();
    }
}
