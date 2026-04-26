package com.endshop.job.damage;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * 伤害类型工具类
 * 提供便捷方法创建自定义伤害来源
 * 
 * 伤害分类:
 * - 物理伤害: Minecraft 自带的 playerAttack 等
 * - 源石伤害: 冰霜、热能、电磁、自然四种元素
 */
public class DamageHelper {
    
    // ========== 源石伤害 ==========
    
    /**
     * 创建冰霜伤害来源(源石-冰霜元素)
     */
    public static DamageSource frost(ServerLevel level) {
        return new DamageSource(level.registryAccess().registryOrThrow(
                net.minecraft.core.registries.Registries.DAMAGE_TYPE).getHolderOrThrow(ModDamageTypes.FROST));
    }
    
    /**
     * 创建热能伤害来源(源石-热能元素)
     */
    public static DamageSource thermal(ServerLevel level) {
        return new DamageSource(level.registryAccess().registryOrThrow(
                net.minecraft.core.registries.Registries.DAMAGE_TYPE).getHolderOrThrow(ModDamageTypes.THERMAL));
    }
    
    /**
     * 创建电磁伤害来源(源石-电磁元素)
     */
    public static DamageSource electromagnetic(ServerLevel level) {
        return new DamageSource(level.registryAccess().registryOrThrow(
                net.minecraft.core.registries.Registries.DAMAGE_TYPE).getHolderOrThrow(ModDamageTypes.ELECTROMAGNETIC));
    }
    
    /**
     * 创建自然伤害来源(源石-自然元素)
     */
    public static DamageSource nature(ServerLevel level) {
        return new DamageSource(level.registryAccess().registryOrThrow(
                net.minecraft.core.registries.Registries.DAMAGE_TYPE).getHolderOrThrow(ModDamageTypes.NATURE));
    }
    
    /**
     * 对实体造成冰霜伤害(源石-冰霜元素)
     */
    public static boolean dealFrostDamage(LivingEntity target, ServerLevel level, float amount) {
        return target.hurt(frost(level), amount);
    }
    
    /**
     * 对实体造成热能伤害(源石-热能元素)
     */
    public static boolean dealThermalDamage(LivingEntity target, ServerLevel level, float amount) {
        return target.hurt(thermal(level), amount);
    }
    
    /**
     * 对实体造成电磁伤害(源石-电磁元素)
     */
    public static boolean dealElectromagneticDamage(LivingEntity target, ServerLevel level, float amount) {
        return target.hurt(electromagnetic(level), amount);
    }
    
    /**
     * 对实体造成自然伤害(源石-自然元素)
     */
    public static boolean dealNatureDamage(LivingEntity target, ServerLevel level, float amount) {
        return target.hurt(nature(level), amount);
    }
}
