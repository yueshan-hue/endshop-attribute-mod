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
 * 突击干员 - 莱万汀（Laevatain）
 * 战技：焚灭 - 熔核碎片持续攻击
 */
public class InfernoSkill extends ActiveSkill {
    
    public InfernoSkill() {
        super("inferno", "Inferno", "Molten core fragments continuous attack", 10, 8, "inferno");
    }
    
    @Override
    public void execute(Player player) {
        if (player == null) return;
        
        var level = player.level();
        
        // 计算伤害
        float damage = 8.0f * getCurrentLevel();
        
        // ========== 特效部分 ==========
        
        // 1. 播放火焰音效
        level.playSound(
            null,
            player.getX(), player.getY(), player.getZ(),
            SoundEvents.FIRECHARGE_USE,  // 火球使用音效
            SoundSource.PLAYERS,
            1.0f,
            1.0f
        );
        
        // 2. 在玩家周围生成火焰环
        for (int i = 0; i < 36; i++) {
            double angle = Math.toRadians(i * 10);  // 360度分成36份
            double radius = 4.0;  // 半径
            
            double x = player.getX() + Math.cos(angle) * radius;
            double y = player.getY() + 1.0;
            double z = player.getZ() + Math.sin(angle) * radius;
            
            level.addParticle(
                ParticleTypes.FLAME,
                x, y, z,
                0.0, 0.2, 0.0  // 向上飘动
            );
        }
        
        // 3. 生成爆炸烟雾效果
        for (int i = 0; i < 20; i++) {
            double offsetX = (Math.random() - 0.5) * 6.0;
            double offsetY = Math.random() * 3.0;
            double offsetZ = (Math.random() - 0.5) * 6.0;
            
            level.addParticle(
                ParticleTypes.LARGE_SMOKE,
                player.getX() + offsetX,
                player.getY() + offsetY,
                player.getZ() + offsetZ,
                0.0, 0.1, 0.0
            );
        }
        
        // 4. 中心爆发效果
        for (int i = 0; i < 15; i++) {
            level.addParticle(
                ParticleTypes.EXPLOSION_EMITTER,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                (Math.random() - 0.5) * 0.5,
                Math.random() * 0.5,
                (Math.random() - 0.5) * 0.5
            );
        }
        
        // ========== 逻辑部分 ==========
        
        // 对周围敌人造成持续灼热伤害
        player.level().getEntitiesOfClass(net.minecraft.world.entity.Mob.class, 
            player.getBoundingBox().inflate(6.0)).forEach(entity -> {
            entity.hurt(player.damageSources().playerAttack(player), damage);
            entity.setRemainingFireTicks(entity.getRemainingFireTicks() + 100);
            // 施加虚弱效果
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1));
            
            // 在被击中的敌人身上生成燃烧粒子
            for (int i = 0; i < 8; i++) {
                level.addParticle(
                    ParticleTypes.FLAME,
                    entity.getX() + (Math.random() - 0.5) * 1.0,
                    entity.getY() + Math.random() * 2.0,
                    entity.getZ() + (Math.random() - 0.5) * 1.0,
                    0.0, 0.15, 0.0
                );
            }
        });
        
        player.sendSystemMessage(Component.literal("§4§l焚灭 §7- 造成 §c" + damage + "§7 点熔核伤害并点燃敌人"));
    }
    
    @Override
    public boolean isUnlocked() {
        return true;
    }
    
    @Override
    public ActiveSkill copy() {
        return new InfernoSkill();
    }
}
