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

/**
 * 先锋干员 - 埃特拉（Estella）
 * 终结技：冰霜新星 - 生成冰晶实体爆炸，大范围冰冻
 */
public class FrostNovaSkill extends ActiveSkill {
    
    public FrostNovaSkill() {
        super("frost_nova", "Frost Nova", "Create ice crystal explosion with large freeze area", 10, 15, "frost_nova");
    }
    
    @Override
    public void execute(Player player) {
        if (player == null) return;
        
        var level = player.level();
        
        // 计算伤害
        float damage = 15.0f * getCurrentLevel();
        
        // ========== 特效部分 ==========
        
        // 1. 播放音效
        level.playSound(
            null,
            player.getX(), player.getY(), player.getZ(),
            SoundEvents.GLASS_BREAK,
            SoundSource.PLAYERS,
            1.0f,
            0.8f
        );
        
        // 2. 生成多个冰晶实体(AreaEffectCloud)
        int crystalCount = 6; // 生成6个冰晶
        for (int i = 0; i < crystalCount; i++) {
            double angle = Math.toRadians(i * (360.0 / crystalCount));
            double radius = 3.0;
            
            double x = player.getX() + Math.cos(angle) * radius;
            double y = player.getY() + 1.0;
            double z = player.getZ() + Math.sin(angle) * radius;
            
            // 创建区域效果云作为冰晶
            AreaEffectCloud iceCrystal = new AreaEffectCloud(level, x, y, z);
            iceCrystal.setRadius(0.5f);
            iceCrystal.setDuration(30); // 存在0.5秒后爆炸
            iceCrystal.setParticle(ParticleTypes.SNOWFLAKE);
            iceCrystal.setWaitTime(20); // 等待20tick后开始效果
            iceCrystal.setRadiusPerTick(0.15f); // 每tick扩大0.15格
            
            level.addFreshEntity(iceCrystal);
            
            // 为每个冰晶添加初始粒子
            for (int j = 0; j < 5; j++) {
                level.addParticle(
                    ParticleTypes.SNOWFLAKE,
                    x + (Math.random() - 0.5) * 0.5,
                    y + Math.random() * 1.0,
                    z + (Math.random() - 0.5) * 0.5,
                    0.0, 0.1, 0.0
                );
            }
        }
        
        // 3. 延迟执行爆炸效果(模拟冰晶汇聚后爆炸)
        level.scheduleTick(
            new net.minecraft.core.BlockPos((int)player.getX(), (int)player.getY(), (int)player.getZ()),
            level.getBlockState(new net.minecraft.core.BlockPos((int)player.getX(), (int)player.getY(), (int)player.getZ())).getBlock(),
            30 // 30tick后执行
        );
        
        // 由于scheduleTick需要Block,我们改用另一种方式
        // 直接在当前位置创建主要爆炸效果
        
        // 4. 中心爆发效果
        AreaEffectCloud mainExplosion = new AreaEffectCloud(level, player.getX(), player.getY(), player.getZ());
        mainExplosion.setRadius(1.0f);
        mainExplosion.setDuration(20);
        mainExplosion.setParticle(ParticleTypes.EXPLOSION);
        mainExplosion.setRadiusPerTick(0.4f); // 每tick扩大0.4格
        mainExplosion.setWaitTime(25); // 稍晚触发
        
        level.addFreshEntity(mainExplosion);
        
        // ========== 逻辑部分 ==========
        
        // 对范围内敌人造成伤害和冰冻
        level.getEntitiesOfClass(net.minecraft.world.entity.Mob.class, 
            player.getBoundingBox().inflate(8.0)).forEach(entity -> {
            
            entity.hurt(player.damageSources().playerAttack(player), damage);
            
            // 施加强烈冰冻效果
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 3));
            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 120, 3));
            
            // 在被击中的敌人身上生成大量冰霜粒子
            for (int i = 0; i < 15; i++) {
                level.addParticle(
                    ParticleTypes.SNOWFLAKE,
                    entity.getX() + (Math.random() - 0.5) * 2.0,
                    entity.getY() + Math.random() * 2.5,
                    entity.getZ() + (Math.random() - 0.5) * 2.0,
                    0.0, 0.2, 0.0
                );
            }
        });
        
        player.sendSystemMessage(Component.literal("§b§l❄ 冰霜新星 §7- 造成 §c" + damage + "§7 点冰霜伤害并§9冻结§7敌人"));
    }
    
    @Override
    public boolean isUnlocked() {
        return true;
    }
    
    @Override
    public ActiveSkill copy() {
        return new FrostNovaSkill();
    }
}
