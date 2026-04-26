package com.endshop.job.skill.skills;

import com.endshop.job.skill.ActiveSkill;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

/**
 * 突击干员 - 莱万汀（Laevatain）
 * 终结技：炎爆术 - 生成火焰实体，产生连锁爆炸
 */
public class FireBlastSkill extends ActiveSkill {
    
    public FireBlastSkill() {
        super("fire_blast", "Fire Blast", "Create flame entities with chain explosion", 10, 20, "fire_blast");
    }
    
    @Override
    public void execute(Player player) {
        if (player == null) return;
        
        var level = player.level();
        
        // 计算伤害
        float damage = 20.0f * getCurrentLevel();
        
        // ========== 特效部分 ==========
        
        // 1. 播放音效
        level.playSound(
            null,
            player.getX(), player.getY(), player.getZ(),
            SoundEvents.BLAZE_SHOOT,
            SoundSource.PLAYERS,
            1.2f,
            1.0f
        );
        
        // 2. 生成火焰实体(Marker)作为临时特效点
        // Marker是一个无碰撞、不可见的实体，适合做特效定位点
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * (360.0 / 8));
            double radius = 2.0;
            
            double x = player.getX() + Math.cos(angle) * radius;
            double y = player.getY() + 1.0;
            double z = player.getZ() + Math.sin(angle) * radius;
            
            // 创建Marker实体作为火焰中心点
            Marker flameMarker = EntityType.MARKER.create(level);
            if (flameMarker != null) {
                flameMarker.setPos(x, y, z);
                
                // 在Marker位置添加火焰粒子效果
                for (int j = 0; j < 10; j++) {
                    level.addParticle(
                        ParticleTypes.FLAME,
                        x + (Math.random() - 0.5) * 0.5,
                        y + Math.random() * 0.5,
                        z + (Math.random() - 0.5) * 0.5,
                        0.0, 0.1, 0.0
                    );
                }
                
                // 短暂添加到世界然后移除，仅用于特效
                level.addFreshEntity(flameMarker);
                
                // 延迟移除
                level.getServer().execute(() -> {
                    if (!flameMarker.isRemoved()) {
                        flameMarker.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
                    }
                });
            }
        }
        
        // 3. 生成连锁爆炸效果
        // 创建多个小型AreaEffectCloud形成连锁反应
        for (int i = 0; i < 5; i++) {
            // 计算随机位置
            double offsetX = (Math.random() - 0.5) * 6.0;
            double offsetZ = (Math.random() - 0.5) * 6.0;
            
            double x = player.getX() + offsetX;
            double y = player.getY();
            double z = player.getZ() + offsetZ;
            
            // 创建爆炸云
            AreaEffectCloud blastCloud = new AreaEffectCloud(level, x, y, z);
            blastCloud.setRadius(0.1f);
            blastCloud.setDuration(30);
            blastCloud.setParticle(ParticleTypes.EXPLOSION);
            blastCloud.setRadiusPerTick(0.3f);
            blastCloud.setWaitTime(i * 5); // 错开触发时间，形成连锁效果
            
            level.addFreshEntity(blastCloud);
        }
        
        // 4. 中心主爆炸
        AreaEffectCloud mainBlast = new AreaEffectCloud(level, player.getX(), player.getY(), player.getZ());
        mainBlast.setRadius(0.5f);
        mainBlast.setDuration(40);
        mainBlast.setParticle(ParticleTypes.FLAME);
        mainBlast.setRadiusPerTick(0.5f);
        mainBlast.setWaitTime(0);
        
        level.addFreshEntity(mainBlast);
        
        // ========== 逻辑部分 ==========
        
        // 对范围内敌人造成伤害和点燃
        level.getEntitiesOfClass(net.minecraft.world.entity.Mob.class, 
            player.getBoundingBox().inflate(10.0)).forEach(entity -> {
            
            entity.hurt(player.damageSources().playerAttack(player), damage);
            
            // 施加点燃效果
            entity.setRemainingFireTicks(entity.getRemainingFireTicks() + 150);
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 2));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
            
            // 在被击中的敌人身上生成火焰粒子
            for (int i = 0; i < 12; i++) {
                level.addParticle(
                    ParticleTypes.FLAME,
                    entity.getX() + (Math.random() - 0.5) * 1.5,
                    entity.getY() + Math.random() * 2.0,
                    entity.getZ() + (Math.random() - 0.5) * 1.5,
                    0.0, 0.1, 0.0
                );
            }
        });
        
        player.sendSystemMessage(Component.literal("§4§l🔥 炎爆术 §7- 造成 §c" + damage + "§7 点火焰伤害并§6点燃§7敌人"));
    }
    
    @Override
    public boolean isUnlocked() {
        return true;
    }
    
    @Override
    public ActiveSkill copy() {
        return new FireBlastSkill();
    }
}