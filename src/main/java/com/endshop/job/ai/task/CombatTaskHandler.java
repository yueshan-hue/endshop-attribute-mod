package com.endshop.job.ai.task;

import com.endshop.job.ai.state.AIState;
import com.endshop.job.entity.EndshopEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.Items;

import java.util.List;

/**
 * 战斗任务处理器
 */
public class CombatTaskHandler implements TaskHandler {
    private final EndshopEntity entity;
    private LivingEntity targetEntity;
    private int searchRange;
    
    public CombatTaskHandler(EndshopEntity entity) {
        this.entity = entity;
        this.searchRange = 30;
    }
    
    @Override
    public void initialize() {
        // 初始化战斗任务
        targetEntity = null;
    }
    
    @Override
    public AIState handle() {
        // 搜索敌人
        if (targetEntity == null) {
            searchForEnemies();
            if (targetEntity == null) {
                return AIState.NO_TARGET_FOUND;
            }
            return AIState.MOVING;
        }
        
        // 检查目标是否仍然有效
        if (!targetEntity.isAlive() || targetEntity.distanceToSqr(entity) > searchRange * searchRange) {
            targetEntity = null;
            return AIState.SEARCHING;
        }
        
        // 移动到目标
        if (!moveToTarget(targetEntity)) {
            return AIState.MOVING;
        }
        
        // 攻击目标
        attackTarget(targetEntity);
        return AIState.WORKING;
    }
    
    @Override
    public void reset() {
        initialize();
    }
    
    @Override
    public EndshopEntity getEntity() {
        return entity;
    }
    
    /**
     * 搜索敌人
     */
    private void searchForEnemies() {
        TargetingConditions targetingConditions = TargetingConditions.forCombat().range(searchRange);
        List<LivingEntity> potentialTargets = entity.level().getNearbyEntities(
            LivingEntity.class,
            targetingConditions,
            entity,
            entity.getBoundingBox().inflate(searchRange)
        );
        
        for (LivingEntity potentialTarget : potentialTargets) {
            if (potentialTarget instanceof Monster && potentialTarget.isAlive()) {
                targetEntity = potentialTarget;
                break;
            }
        }
    }
    
    /**
     * 移动到目标
     */
    private boolean moveToTarget(LivingEntity target) {
        entity.moveTo(target, 0.6, 3.0f);
        
        // 检查距离
        double distance = entity.distanceToSqr(target);
        return distance <= 4.0;
    }
    
    /**
     * 攻击目标
     */
    private void attackTarget(LivingEntity target) {
        // 装备剑
        entity.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, Items.IRON_SWORD.getDefaultInstance());
        
        // 面向目标
        entity.lookAt(target.getX(), target.getY(), target.getZ());
        
        // 攻击目标
        entity.doHurtTarget(target);
    }
}
