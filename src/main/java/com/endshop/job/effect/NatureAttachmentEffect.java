package com.endshop.job.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;

/**
 * 自然附着效果
 * 持续造成自然伤害(荆棘),产生植被粒子
 */
public class NatureAttachmentEffect extends MobEffect {
    
    public NatureAttachmentEffect() {
        super(MobEffectCategory.HARMFUL, 0x228B22); // 森林绿
    }
    
    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // 每20tick(1秒)造成一次自然伤害
        if (entity.tickCount % 20 == 0) {
            float damage = 1.0F + amplifier * 0.5F;
            entity.hurt(entity.damageSources().magic(), damage);
        }
        
        // 产生植被粒子效果
        Level level = entity.level();
        if (!level.isClientSide) {
            double x = entity.getX() + (level.random.nextDouble() - 0.5) * entity.getBbWidth();
            double y = entity.getY() + level.random.nextDouble() * entity.getBbHeight();
            double z = entity.getZ() + (level.random.nextDouble() - 0.5) * entity.getBbWidth();
            level.addParticle(ParticleTypes.HAPPY_VILLAGER, x, y, z, 0.0, 0.05, 0.0);
        }
        
        return true;
    }
    
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
