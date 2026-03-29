package com.endshop.job.skill.skills;

import com.endshop.job.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * 先锋干员 - 埃特拉（Estella）
 * 战技：凝声 - 直线寒冷伤害 + 寒冷附着
 */
public class CondensingVoiceSkill extends ActiveSkill {
    
    public CondensingVoiceSkill() {
        super("condensing_voice", "Condensing Voice", "Linear ice damage and applying chill", 10, 9, "condensing_voice");
    }
    
    @Override
    public void execute(Player player) {
        if (player == null) return;
        
        // 计算伤害
        float damage = 9.0f * getCurrentLevel();
        
        // 获取玩家朝向
        var look = player.getLookAngle();
        
        // 对直线范围内的敌人造成寒冷伤害
        player.level().getEntitiesOfClass(net.minecraft.world.entity.Mob.class, 
            player.getBoundingBox().inflate(10.0, 2.0, 10.0)).forEach(entity -> {
            var toEntity = entity.position().subtract(player.position()).normalize();
            double dot = look.dot(toEntity);
            
            if (dot > 0.7) { // 直线前方
                entity.hurt(player.damageSources().playerAttack(player), damage);
                // 施加缓慢效果（寒冷）
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
                entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 2));
            }
        });
        
        player.sendSystemMessage(Component.literal("§b§l凝声 §7- 造成 §c" + damage + "§7 点寒冷伤害并附着§9寒冷§7"));
    }
    
    @Override
    public boolean isUnlocked() {
        return true;
    }
    
    @Override
    public ActiveSkill copy() {
        return new CondensingVoiceSkill();
    }
}
