package com.endshop.job.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;

/**
 * 电磁附着效果
 * 造成电击伤害,产生闪电粒子,并有概率麻痹目标
 */
public class ElectromagneticAttachmentEffect extends MobEffect {
    
    public ElectromagneticAttachmentEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFD700); // 金黄色
    }
    
    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // 每20tick(1秒)造成一次电击伤害
        if (entity.tickCount % 20 == 0) {
            float damage = 1.5F + amplifier * 0.5F;
            entity.hurt(entity.damageSources().lightningBolt(), damage);
            
            // 高等级时有概率附加麻痹效果(缓慢)
            if (amplifier >= 2 && entity.getRandom().nextFloat() < 0.3F) {
                entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 
                    40, 
                    1
                ));
            }
        }
        
        // 产生闪电粒子效果
        Level level = entity.level();
        if (!level.isClientSide) {
            double x = entity.getX() + (level.random.nextDouble() - 0.5) * entity.getBbWidth();
            double y = entity.getY() + level.random.nextDouble() * entity.getBbHeight();
            double z = entity.getZ() + (level.random.nextDouble() - 0.5) * entity.getBbWidth();
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, x, y, z, 0.0, 0.1, 0.0);
        }
        
        return true;
    }
    
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
