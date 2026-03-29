package com.endshop.job.skill.skills;

import com.endshop.job.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * 突击干员 - 莱万汀（Laevatain）
 * 普攻技能：燃烬 - 5 段灼热伤害
 */
public class BurnoutSkill extends ActiveSkill {
    
    public BurnoutSkill() {
        super("burnout", "Burnout", "5-hit fire damage attack", 10, 6, "burnout");
    }
    
    @Override
    public void execute(Player player) {
        if (player == null) return;
        
        // 计算伤害
        float damage = 6.0f * getCurrentLevel();
        
        // 对周围敌人造成灼热伤害
        player.level().getEntitiesOfClass(net.minecraft.world.entity.Mob.class, 
            player.getBoundingBox().inflate(5.0)).forEach(entity -> {
            entity.hurt(player.damageSources().playerAttack(player), damage);
            // 施加点燃效果
            entity.setRemainingFireTicks(entity.getRemainingFireTicks() + 60);
        });
        
        player.sendSystemMessage(Component.literal("§c§l燃烬 §7- 造成 §c" + damage + "§7 点灼热伤害"));
    }
    
    @Override
    public boolean isUnlocked() {
        return true;
    }
    
    @Override
    public ActiveSkill copy() {
        return new BurnoutSkill();
    }
}
