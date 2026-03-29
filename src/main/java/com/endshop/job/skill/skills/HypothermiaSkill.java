package com.endshop.job.skill.skills;

import com.endshop.job.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * 重装干员 - 昼雪
 * 普攻技能：失温猛击 - 3 段物理伤害
 */
public class HypothermiaSkill extends ActiveSkill {
    
    public HypothermiaSkill() {
        super("hypothermia", "Hypothermia", "3-hit physical damage attack", 10, 7, "hypothermia");
    }
    
    @Override
    public void execute(Player player) {
        if (player == null) return;
        
        // 计算伤害
        float damage = 7.0f * getCurrentLevel();
        
        // 对周围敌人造成伤害
        player.level().getEntitiesOfClass(net.minecraft.world.entity.Mob.class, 
            player.getBoundingBox().inflate(4.0)).forEach(entity -> {
            entity.hurt(player.damageSources().playerAttack(player), damage);
            // 施加缓慢效果
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
        });
        
        player.sendSystemMessage(Component.literal("§f§l失温猛击 §7- 造成 §c" + damage + "§7 点物理伤害"));
    }
    
    @Override
    public boolean isUnlocked() {
        return true;
    }
    
    @Override
    public ActiveSkill copy() {
        return new HypothermiaSkill();
    }
}
