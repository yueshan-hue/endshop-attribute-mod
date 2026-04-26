package com.endshop.job.ai.task;

import com.endshop.job.ai.state.AIState;
import com.endshop.job.ai.task.base.BaseTaskHandler;
import com.endshop.job.entity.EndshopEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 守卫任务处理器 - 参考 Minecolonies 的 EntityAIKnight
 * 
 * 状态机流程:
 * PATROLLING -> DETECTING_ENEMY -> MOVING_TO_ENEMY -> COMBAT -> RETURN_TO_POST -> PATROLLING
 */
public class GuardTaskHandler extends BaseTaskHandler {
    
    // 搜索范围
    private static final int SEARCH_RANGE = 30;
    private static final int ATTACK_RANGE = 3;
    
    // 巡逻相关
    @Nullable
    private BlockPos guardPost; // 守卫位置
    
    @Nullable
    private LivingEntity currentTarget; // 当前攻击目标
    
    private int combatCooldown = 0; // 战斗冷却
    private static final int COMBAT_COOLDOWN_TIME = 20; // 1秒冷却
    
    public GuardTaskHandler(@NotNull EndshopEntity entity) {
        super(entity);
        this.searchRange = SEARCH_RANGE;
        this.guardPost = entity.blockPosition();
    }
    
    @Override
    public void initialize() {
        super.initialize();
        guardPost = entity.blockPosition();
        currentTarget = null;
        combatCooldown = 0;
    }
    
    @Override
    public AIState handle() {
        // 状态机主循环
        switch (currentState) {
            case IDLE:
                return handlePatrolling();
            
            case SEARCHING:
                return handleDetectingEnemy();
            
            case MOVING:
                return handleMovingToEnemy();
            
            case WORKING:
                return handleCombat();
            
            default:
                return AIState.IDLE;
        }
    }
    
    /**
     * 巡逻状态：在守卫位置附近巡逻
     */
    private AIState handlePatrolling() {
        // 首先检测敌人
        setCurrentState(AIState.SEARCHING);
        return AIState.SEARCHING;
    }
    
    /**
     * 检测敌人状态
     */
    private AIState handleDetectingEnemy() {
        // 搜索附近的敌人
        currentTarget = findNearestEnemy();
        
        if (currentTarget != null && currentTarget.isAlive()) {
            // 发现敌人，进入战斗准备
            setCurrentState(AIState.MOVING);
            return AIState.MOVING;
        }
        
        // 没有敌人，回到守卫位置
        if (!isAtGuardPost()) {
            entity.moveTo(guardPost.getX() + 0.5, guardPost.getY(), guardPost.getZ() + 0.5, 0.4);
            return AIState.MOVING;
        }
        
        // 在守卫位置，保持空闲
        setCurrentState(AIState.IDLE);
        return AIState.IDLE;
    }
    
    /**
     * 移动到敌人状态
     */
    private AIState handleMovingToEnemy() {
        if (currentTarget == null || !currentTarget.isAlive()) {
            currentTarget = null;
            setCurrentState(AIState.SEARCHING);
            return AIState.SEARCHING;
        }
        
        // 检查是否在攻击范围内
        double distance = entity.distanceToSqr(currentTarget);
        if (distance <= ATTACK_RANGE * ATTACK_RANGE) {
            // 进入战斗状态
            setCurrentState(AIState.WORKING);
            return AIState.WORKING;
        }
        
        // 移动到敌人附近
        entity.moveTo(currentTarget, 0.6, 2.0f);
        checkIfStuck();
        
        return AIState.MOVING;
    }
    
    /**
     * 战斗状态：攻击敌人
     */
    private AIState handleCombat() {
        if (currentTarget == null || !currentTarget.isAlive()) {
            currentTarget = null;
            setCurrentState(AIState.SEARCHING);
            return AIState.SEARCHING;
        }
        
        // 检查是否在攻击范围内
        double distance = entity.distanceToSqr(currentTarget);
        if (distance > ATTACK_RANGE * ATTACK_RANGE) {
            // 敌人跑远了，重新追击
            setCurrentState(AIState.MOVING);
            return AIState.MOVING;
        }
        
        // 战斗冷却检查
        if (combatCooldown > 0) {
            combatCooldown--;
            return AIState.WORKING;
        }
        
        // 执行攻击
        attackTarget(currentTarget);
        combatCooldown = COMBAT_COOLDOWN_TIME;
        
        // 继续战斗状态
        return AIState.WORKING;
    }
    
    /**
     * 查找最近的敌人
     */
    @Nullable
    private LivingEntity findNearestEnemy() {
        TargetingConditions targetingConditions = TargetingConditions.forCombat()
            .range(searchRange);
        
        List<Mob> potentialTargets = entity.level().getNearbyEntities(
            Mob.class,
            targetingConditions,
            entity,
            entity.getBoundingBox().inflate(searchRange)
        );
        
        LivingEntity nearestEnemy = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Mob potentialTarget : potentialTargets) {
            // 只攻击敌对生物
            if (potentialTarget instanceof Monster && potentialTarget.isAlive()) {
                double distance = entity.distanceToSqr(potentialTarget);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestEnemy = potentialTarget;
                }
            }
        }
        
        return nearestEnemy;
    }
    
    /**
     * 检查是否在守卫位置
     */
    private boolean isAtGuardPost() {
        if (guardPost == null) return false;
        
        double distance = entity.distanceToSqr(guardPost.getX() + 0.5, guardPost.getY(), guardPost.getZ() + 0.5);
        return distance <= 4.0;
    }
    
    /**
     * 攻击目标
     */
    private void attackTarget(LivingEntity target) {
        // 装备剑
        Item sword = getBestSword();
        equipToolFromInventory(sword);
        
        // 面向目标
        entity.lookAt(target.getX(), target.getEyeY(), target.getZ());
        
        // 攻击目标
        entity.doHurtTarget(target);
        
        // 播放攻击动画
        entity.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
    }
    
    /**
     * 获取最好的剑
     */
    @NotNull
    private Item getBestSword() {
        // 按优先级返回剑的类型
        if (hasItemInInventory(Items.NETHERITE_SWORD)) {
            return Items.NETHERITE_SWORD;
        } else if (hasItemInInventory(Items.DIAMOND_SWORD)) {
            return Items.DIAMOND_SWORD;
        } else if (hasItemInInventory(Items.IRON_SWORD)) {
            return Items.IRON_SWORD;
        } else if (hasItemInInventory(Items.STONE_SWORD)) {
            return Items.STONE_SWORD;
        } else {
            return Items.WOODEN_SWORD;
        }
    }
    
    /**
     * 检查背包中是否有指定物品
     */
    private boolean hasItemInInventory(Item item) {
        // 简化处理：假设总是有剑
        return true;
    }
    
    @Override
    protected Item getRequiredTool(BlockState state) {
        // 守卫不需要挖掘工具
        return null;
    }
    
    @Override
    protected boolean isItemWorthPickingUp(net.minecraft.world.item.ItemStack stack) {
        // 守卫不收集物品
        return false;
    }
}
