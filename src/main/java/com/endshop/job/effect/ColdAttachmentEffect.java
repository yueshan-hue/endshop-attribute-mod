package com.endshop.job.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * 寒冷附着效果
 * 降低移动速度,持续造成寒冷伤害
 */
public class ColdAttachmentEffect extends MobEffect {
    
    public ColdAttachmentEffect() {
        super(MobEffectCategory.HARMFUL, 0x87CEEB); // 天蓝色
    }
    
    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // 每20tick(1秒)造成一次伤害
        if (entity.tickCount % 20 == 0) {
            float damage = 1.0F + amplifier * 0.5F;
            entity.hurt(entity.damageSources().freeze(), damage);
        }
        return true;
    }
    
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
