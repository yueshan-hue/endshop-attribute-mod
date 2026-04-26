package com.endshop.job.skill.skills;

import com.endshop.job.skill.ActiveSkill;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.phys.Vec3;

/**
 * 近卫干员 - 管理员（Endministrator）
 * 战技：能量飞弹 - 发射多个追踪能量球
 */
public class EnergyMissileSkill extends ActiveSkill {
    
    public EnergyMissileSkill() {
        super("energy_missile", "Energy Missile", "Launch multiple tracking energy balls", 10, 12, "energy_missile");
    }
    
    @Override
    public void execute(Player player) {
        if (player == null) return;
        
        var level = player.level();
        
        // 计算伤害
        float damage = 12.0f * getCurrentLevel();
        
        // ========== 特效部分 ==========
        
        // 1. 播放音效
        level.playSound(
            null,
            player.getX(), player.getY(), player.getZ(),
            SoundEvents.EVOKER_CAST_SPELL,  // 施法音效
            SoundSource.PLAYERS,
            1.0f,
            1.5f
        );
        
        // 2. 发射多个能量球(使用Snowball作为载体)
        int missileCount = 5;
        for (int i = 0; i < missileCount; i++) {
            // 创建雪球实体作为能量球载体
            Snowball energyBall = new Snowball(level, player);
            
            // 设置初始位置(在玩家前方)
            Vec3 look = player.getLookAngle();
            double offsetX = (Math.random() - 0.5) * 1.5;
            double offsetZ = (Math.random() - 0.5) * 1.5;
            
            energyBall.setPos(
                player.getX() + look.x * 2 + offsetX,
                player.getY() + 1.5,
                player.getZ() + look.z * 2 + offsetZ
            );
            
            // 设置飞行方向(稍微分散)
            double spreadX = (Math.random() - 0.5) * 0.3;
            double spreadY = (Math.random() - 0.5) * 0.2;
            double spreadZ = (Math.random() - 0.5) * 0.3;
            
            energyBall.shoot(
                look.x + spreadX,
                look.y + spreadY,
                look.z + spreadZ,
                2.0f,  // 速度
                1.0f   // 精确度
            );
            
            // 添加到世界
            level.addFreshEntity(energyBall);
            
            // 为每个能量球添加拖尾粒子效果
            for (int j = 0; j < 8; j++) {
                level.addParticle(
                    ParticleTypes.ENCHANT,
                    energyBall.getX() + (Math.random() - 0.5) * 0.3,
                    energyBall.getY() + (Math.random() - 0.5) * 0.3,
                    energyBall.getZ() + (Math.random() - 0.5) * 0.3,
                    0.0, 0.05, 0.0
                );
            }
        }
        
        // 3. 玩家周围生成充能效果
        AreaEffectCloud chargeEffect = new AreaEffectCloud(level, player.getX(), player.getY(), player.getZ());
        chargeEffect.setRadius(2.0f);
        chargeEffect.setDuration(15);
        chargeEffect.setParticle(ParticleTypes.ENCHANT);
        chargeEffect.setRadiusPerTick(-0.1f); // 每tick缩小0.1格
        
        level.addFreshEntity(chargeEffect);
        
        // 4. 生成爆炸烟雾
        for (int i = 0; i < 10; i++) {
            level.addParticle(
                ParticleTypes.LARGE_SMOKE,
                player.getX() + (Math.random() - 0.5) * 2.0,
                player.getY() + Math.random() * 2.0,
                player.getZ() + (Math.random() - 0.5) * 2.0,
                0.0, 0.1, 0.0
            );
        }
        
        // ========== 逻辑部分 ==========
        
        // 对范围内的敌人造成伤害
        level.getEntitiesOfClass(net.minecraft.world.entity.Mob.class, 
            player.getBoundingBox().inflate(15.0)).forEach(entity -> {
            
            // 检查是否在玩家前方
            Vec3 toEntity = entity.position().subtract(player.position()).normalize();
            double dot = player.getLookAngle().dot(toEntity);
            
            if (dot > 0.3) { // 在前方60度范围内
                entity.hurt(player.damageSources().playerAttack(player), damage);
                
                // 施加减速效果
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                
                // 在被击中的敌人身上生成魔法粒子
                for (int i = 0; i < 10; i++) {
                    level.addParticle(
                        ParticleTypes.ENCHANT,
                        entity.getX() + (Math.random() - 0.5) * 1.5,
                        entity.getY() + Math.random() * 2.0,
                        entity.getZ() + (Math.random() - 0.5) * 1.5,
                        0.0, 0.1, 0.0
                    );
                }
            }
        });
        
        player.sendSystemMessage(Component.literal("§d§l⚡ 能量飞弹 §7- 发射 §d" + missileCount + "§7 个能量球造成 §c" + damage + "§7 点伤害"));
    }
    
    @Override
    public boolean isUnlocked() {
        return true;
    }
    
    @Override
    public ActiveSkill copy() {
        return new EnergyMissileSkill();
    }
}
