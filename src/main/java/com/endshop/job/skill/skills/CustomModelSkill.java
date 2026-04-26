package com.endshop.job.skill.skills;

import com.endshop.job.entity.EndshopEntityTypes;
import com.endshop.job.entity.SkillEffectEntity;
import com.endshop.job.skill.ActiveSkill;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

/**
 * 示例技能 - 使用自定义实体显示模型特效
 */
public class CustomModelSkill extends ActiveSkill {
    
    public CustomModelSkill() {
        super("custom_model", "Custom Model Skill", "Display custom model entity effect", 10, 10, "custom_model");
    }
    
    @Override
    public void execute(Player player) {
        if (player == null) return;
        
        var level = player.level();
        
        // 计算伤害
        float damage = 10.0f * getCurrentLevel();
        
        // ========== 特效部分 ==========
        
        // 1. 播放音效
        level.playSound(
            null,
            player.getX(), player.getY(), player.getZ(),
            SoundEvents.EVOKER_CAST_SPELL,
            SoundSource.PLAYERS,
            1.0f,
            1.0f
        );
        
        // 2. 生成自定义特效实体
        for (int i = 0; i < 4; i++) {
            double angle = Math.toRadians(i * 90);
            double radius = 2.0;
            
            double x = player.getX() + Math.cos(angle) * radius;
            double y = player.getY() + 1.0;
            double z = player.getZ() + Math.sin(angle) * radius;
            
            // 创建技能特效实体
            SkillEffectEntity effectEntity = new SkillEffectEntity(
                EndshopEntityTypes.SKILL_EFFECT.get(), 
                level
            );
            
            effectEntity.setPos(x, y, z);
            effectEntity.setEffectType("ice_crystal"); // 设置特效类型
            effectEntity.setLifetime(60); // 生存3秒
            
            level.addFreshEntity(effectEntity);
            
            // 添加粒子效果
            for (int j = 0; j < 5; j++) {
                level.addParticle(
                    ParticleTypes.ENCHANT,
                    x + (Math.random() - 0.5) * 0.5,
                    y + Math.random() * 1.0,
                    z + (Math.random() - 0.5) * 0.5,
                    0.0, 0.1, 0.0
                );
            }
        }
        
        // ========== 逻辑部分 ==========
        
        // 对范围内敌人造成伤害
        level.getEntitiesOfClass(net.minecraft.world.entity.Mob.class, 
            player.getBoundingBox().inflate(6.0)).forEach(entity -> {
            
            entity.hurt(player.damageSources().playerAttack(player), damage);
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 2));
        });
        
        player.sendSystemMessage(Component.literal("§d§l✨ 自定义模型技能 §7- 造成 §c" + damage + "§7 点伤害"));
    }
    
    @Override
    public boolean isUnlocked() {
        return true;
    }
    
    @Override
    public ActiveSkill copy() {
        return new CustomModelSkill();
    }
}
