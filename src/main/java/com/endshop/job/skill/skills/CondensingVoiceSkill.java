package com.endshop.job.skill.skills;

import com.endshop.job.skill.ActiveSkill;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
            
        var level = player.level();
            
        // 计算伤害
        float damage = 9.0f * getCurrentLevel();
            
        // 获取玩家朝向
        var look = player.getLookAngle();
            
        // ========== 特效部分 ==========
            
        // 1. 播放音效 - 冰霜声音
        level.playSound(
            null,
            player.getX(), player.getY(), player.getZ(),
            SoundEvents.GLASS_BREAK,  // 玻璃破碎声(类似冰裂)
            SoundSource.PLAYERS,
            1.0f,
            1.2f
        );
            
        // 2. 在玩家前方生成雪花粒子效果
        for (int i = 0; i < 30; i++) {
            double distance = Math.random() * 8.0; // 0-8格距离
            double spread = 0.5; // 扩散范围
                
            double x = player.getX() + look.x * distance + (Math.random() - 0.5) * spread;
            double y = player.getY() + 1.0 + (Math.random() - 0.5) * spread;
            double z = player.getZ() + look.z * distance + (Math.random() - 0.5) * spread;
                
            level.addParticle(
                ParticleTypes.SNOWFLAKE,  // 雪花粒子
                x, y, z,
                0.0, 0.0, 0.0
            );
        }
            
        // 3. 在直线路径上生成爆炸粒子(模拟寒气爆发)
        for (double d = 1.0; d <= 10.0; d += 1.5) {
            double x = player.getX() + look.x * d;
            double y = player.getY() + 1.0;
            double z = player.getZ() + look.z * d;
                
            level.addParticle(
                ParticleTypes.EXPLOSION,
                x, y, z,
                0.0, 0.0, 0.0
            );
        }
            
        // ========== 逻辑部分 ==========
            
        // 对直线范围内的敌人造成寒冷伤害
        player.level().getEntitiesOfClass(net.minecraft.world.entity.Mob.class, 
            player.getBoundingBox().inflate(10.0, 2.0, 10.0)).forEach(entity -> {
            var toEntity = entity.position().subtract(player.position()).normalize();
            double dot = look.dot(toEntity);
                
            if (dot > 0.7) { // 直线前方
                entity.hurt(player.damageSources().playerAttack(player), damage);
                // 施加缓慢效果(寒冷)
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
                entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 2));
                    
                // 在被击中的敌人身上生成冰冻粒子
                for (int i = 0; i < 10; i++) {
                    level.addParticle(
                        ParticleTypes.SNOWFLAKE,
                        entity.getX() + (Math.random() - 0.5) * 1.5,
                        entity.getY() + Math.random() * 2.0,
                        entity.getZ() + (Math.random() - 0.5) * 1.5,
                        0.0, 0.1, 0.0
                    );
                }
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
