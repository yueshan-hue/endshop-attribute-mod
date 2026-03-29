package com.endshop.job.skill.skills;

import com.endshop.job.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;

/**
 * 近卫干员 - 管理员（Endministrator）
 * 战技 1：斩击突进 - 突进路径伤害 + 击倒
 */
public class SlashDashSkill extends ActiveSkill {
    
    public SlashDashSkill() {
        super("slash_dash", "Slash Dash", "Dash forward dealing damage and knocking down enemies", 10, 8, "slash_dash");
    }
    
    @Override
    public void execute(Player player) {
        if (player == null) return;
        
        // 获取玩家朝向
        var look = player.getLookAngle();
        float speed = 2.0f;
        
        // 突进效果
        player.setDeltaMovement(look.x * speed, look.y * speed, look.z * speed);
        
        // 计算伤害
        float damage = 8.0f * getCurrentLevel();
        
        // 对路径上的敌人造成伤害并击倒
        player.level().getEntitiesOfClass(net.minecraft.world.entity.Mob.class, 
            player.getBoundingBox().inflate(3.0)).forEach(entity -> {
            entity.hurt(player.damageSources().playerAttack(player), damage);
            entity.setTicksFrozen(40); // 击倒效果
        });
        
        player.sendSystemMessage(Component.literal("§c§l斩击突进 §7- 造成 §c" + damage + "§7 点伤害并击倒敌人"));
    }
    
    @Override
    public boolean isUnlocked() {
        return true;
    }
    
    @Override
    public ActiveSkill copy() {
        return new SlashDashSkill();
    }
}
