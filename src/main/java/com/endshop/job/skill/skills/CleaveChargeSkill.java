package com.endshop.job.skill.skills;

import com.endshop.job.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;

/**
 * 近卫干员 - 管理员（Endministrator）
 * 战技 2：劈砍突进 - 突进范围伤害 + 击退
 */
public class CleaveChargeSkill extends ActiveSkill {
    
    public CleaveChargeSkill() {
        super("cleave_charge", "Cleave Charge", "Charge forward dealing AoE damage and knocking back enemies", 10, 10, "cleave_charge");
    }
    
    @Override
    public void execute(Player player) {
        if (player == null) return;
        
        // 获取玩家朝向
        var look = player.getLookAngle();
        float speed = 2.5f;
        
        // 突进效果
        player.setDeltaMovement(look.x * speed, look.y * speed, look.z * speed);
        
        // 计算伤害
        float damage = 10.0f * getCurrentLevel();
        
        // 对周围敌人造成伤害并击退
        player.level().getEntitiesOfClass(net.minecraft.world.entity.Mob.class, 
            player.getBoundingBox().inflate(4.0)).forEach(entity -> {
            entity.hurt(player.damageSources().playerAttack(player), damage);
            // 击退效果
            entity.push(look.x * 2, 0.5, look.z * 2);
        });
        
        player.sendSystemMessage(Component.literal("§e§l劈砍突进 §7- 造成 §c" + damage + "§7 点范围伤害并击退敌人"));
    }
    
    @Override
    public boolean isUnlocked() {
        return true;
    }
    
    @Override
    public ActiveSkill copy() {
        return new CleaveChargeSkill();
    }
}
