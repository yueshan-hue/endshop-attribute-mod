package com.endshop.job.skill.skills;

import com.endshop.job.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;

/**
 * 先锋干员 - 埃特拉（Estella）
 * 普攻技能：噪点 - 4 段物理伤害
 */
public class NoiseSkill extends ActiveSkill {
    
    public NoiseSkill() {
        super("noise", "Noise", "4-hit physical damage attack", 10, 5, "noise");
    }
    
    @Override
    public void execute(Player player) {
        if (player == null) return;
        
        // 计算伤害
        float damage = 5.5f * getCurrentLevel();
        
        // 对周围敌人造成伤害
        player.level().getEntitiesOfClass(net.minecraft.world.entity.Mob.class, 
            player.getBoundingBox().inflate(5.0)).forEach(entity -> {
            entity.hurt(player.damageSources().playerAttack(player), damage);
        });
        
        player.sendSystemMessage(Component.literal("§f§l噪点 §7- 造成 §c" + damage + "§7 点物理伤害"));
    }
    
    @Override
    public boolean isUnlocked() {
        return true;
    }
    
    @Override
    public ActiveSkill copy() {
        return new NoiseSkill();
    }
}
