package com.endshop.job.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;

/**
 * 灼燃附着效果
 * 持续造成火焰伤害,并产生火焰粒子
 */
public class BurningAttachmentEffect extends MobEffect {
    
    public BurningAttachmentEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF4500); // 橙红色
    }
    
    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // 每20tick(1秒)造成一次火焰伤害
        if (entity.tickCount % 20 == 0) {
            float damage = 1.0F + amplifier * 0.5F;
            entity.hurt(entity.damageSources().onFire(), damage);
        }
        
        // 产生火焰粒子效果
        Level level = entity.level();
        if (!level.isClientSide) {
            double x = entity.getX() + (level.random.nextDouble() - 0.5) * entity.getBbWidth();
            double y = entity.getY() + level.random.nextDouble() * entity.getBbHeight();
            double z = entity.getZ() + (level.random.nextDouble() - 0.5) * entity.getBbWidth();
            level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.1, 0.0);
        }
        
        return true;
    }
    
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
