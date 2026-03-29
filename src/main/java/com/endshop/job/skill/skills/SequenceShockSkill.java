package com.endshop.job.skill.skills;

import com.endshop.job.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;

/**
 * 近卫干员 - 管理员（Endministrator）
 * 终结技：序列冲击 - 锥形高额物理伤害，背击 1.5 倍伤
 */
public class SequenceShockSkill extends ActiveSkill {
    
    public SequenceShockSkill() {
        super("sequence_shock", "Sequence Shock", "Cone high physical damage, 1.5x damage from back attack", 10, 20, "sequence_shock");
    }
    
    @Override
    public void execute(Player player) {
        if (player == null) return;
        
        // 计算基础伤害
        float baseDamage = 20.0f * getCurrentLevel();
        
        // 对锥形范围内的敌人造成伤害
        player.level().getEntitiesOfClass(net.minecraft.world.entity.Mob.class, 
            player.getBoundingBox().inflate(8.0)).forEach(entity -> {
            
            // 判断是否为背击（敌人在玩家身后）
            var playerLook = player.getLookAngle();
            var toEntity = entity.position().subtract(player.position()).normalize();
            double dot = playerLook.dot(toEntity);
            
            float damage = baseDamage;
            if (dot > 0.5) { // 背击
                damage *= 1.5f;
            }
            
            entity.hurt(player.damageSources().playerAttack(player), damage);
        });
        
        player.sendSystemMessage(Component.literal("§9§l序列冲击 §7- 造成 §c" + baseDamage + "§7 点锥形伤害（背击§e1.5 倍§7）"));
    }
    
    @Override
    public boolean isUnlocked() {
        return true;
    }
    
    @Override
    public ActiveSkill copy() {
        return new SequenceShockSkill();
    }
}
