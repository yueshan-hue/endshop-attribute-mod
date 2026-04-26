package com.endshop.job.ai.task.base;

import com.endshop.job.ai.state.AIState;
import com.endshop.job.ai.task.TaskHandler;
import com.endshop.job.entity.EndshopEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础任务处理器 - 参考 Minecolonies 的 AbstractEntityAIInteract
 * 提供通用的方块交互、物品收集、工具检查等功能
 */
public abstract class BaseTaskHandler implements TaskHandler {
    protected final EndshopEntity entity;
    
    // 当前工作状态
    protected AIState currentState = AIState.IDLE;
    
    // 工作位置缓存
    @Nullable
    protected BlockPos currentWorkingLocation;
    
    // 物品收集相关
    @Nullable
    protected List<BlockPos> itemsToPickup;
    
    // 搜索范围
    protected int searchRange = 20;
    
    // 延迟计数器
    private int delayCounter = 0;
    // 延迟目标值（需要达到的tick数）
    private int delayTarget = 0;
    
    // 卡住检测
    private int stillTicks = 0;
    private int previousPathIndex = 0;
    
    /**
     * 构造函数
     */
    public BaseTaskHandler(@NotNull EndshopEntity entity) {
        this.entity = entity;
    }
    
    @Override
    public void initialize() {
        currentState = AIState.IDLE;
        currentWorkingLocation = null;
        itemsToPickup = null;
        delayCounter = 0;
        delayTarget = 0;
        stillTicks = 0;
        previousPathIndex = 0;
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
     * 模拟挖掘方块（带延迟和动画）
     * 参考 Minecolonies 的 mineBlock 方法
     * 
     * @param blockToMine 要挖掘的方块位置
     * @return true 表示挖掘完成，false 表示仍在进行中
     */
    protected boolean mineBlock(@NotNull BlockPos blockToMine) {
        BlockState state = entity.level().getBlockState(blockToMine);
        
        // 检查是否是空气或基岩
        if (state.isAir() || state.getBlock().defaultDestroyTime() == -1.0F) {
            return true;
        }
        
        // 确保持有正确工具（每次都检查并装备，不依赖上一帧状态）
        Item requiredTool = getRequiredTool(state);
        if (requiredTool != null) {
            ItemStack mainHand = entity.getMainHandItem();
            // 如果当前手持的不是正确工具，立即换上
            if (mainHand.isEmpty() || mainHand.getItem() != requiredTool) {
                equipToolFromInventory(requiredTool);
                return false; // 换工具花一tick
            }
        }
        
        // 检查工具效率（确保工具能挖掘该方块）
        ItemStack currentTool = entity.getMainHandItem();
        if (!currentTool.isEmpty()) {
            float speed = currentTool.getItem().getDestroySpeed(currentTool, state);
            if (speed <= 1.0F) {
                // 工具不够好，尝试更换
                if (requiredTool != null) {
                    equipToolFromInventory(requiredTool);
                }
                return false;
            }
        }
        
        // 计算挖掘延迟（基于方块硬度和工具效率）
        int miningDelay = calculateMiningDelay(state, blockToMine);
        
        // 延迟检查
        if (hasNotDelayed(miningDelay)) {
            // 面向目标并播放动画
            entity.lookAt(blockToMine.getX() + 0.5, blockToMine.getY(), blockToMine.getZ() + 0.5);
            entity.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
            return false;
        }
        
        // 执行挖掘（仅在服务端执行，避免客户端重复操作）
        if (!entity.level().isClientSide()) {
            entity.level().destroyBlock(blockToMine, true);
        }
        // 不清空工具，保持装备状态以便继续挖掘
        
        // 增加经验（可选）
        addExperienceForMining(state);
        
        return true;
    }
    
    /**
     * 放置方块
     */
    protected boolean placeBlock(@NotNull BlockPos pos, @NotNull Item item) {
        if (!entity.level().isEmptyBlock(pos)) {
            return true; // 已经有方块了
        }
        
        // 检查背包中是否有该物品
        int slot = findItemInInventory(item);
        if (slot == -1) {
            return false;
        }
        
        // 装备物品并放置（简化处理）
        entity.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new ItemStack(item));
        
        // 这里需要实际的放置逻辑，暂时跳过
        return true;
    }
    
    /**
     * 检查是否持有高效的工具
     */
    protected boolean holdEfficientTool(@NotNull BlockState state) {
        ItemStack mainHand = entity.getMainHandItem();
        
        if (mainHand.isEmpty()) {
            // 没有工具，尝试从背包获取
            Item tool = getRequiredTool(state);
            if (tool != null) {
                equipToolFromInventory(tool);
            }
            return false;
        }
        
        // 检查工具是否适合挖掘该方块
        float speed = mainHand.getItem().getDestroySpeed(mainHand, state);
        return speed > 1.0F;
    }
    
    /**
     * 获取挖掘指定方块所需的工具类型
     */
    @Nullable
    protected abstract Item getRequiredTool(BlockState state);
    
    /**
     * 从背包中装备工具
     */
    protected void equipToolFromInventory(Item tool) {
        // 简化处理：直接设置工具
        entity.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new ItemStack(tool));
    }
    
    /**
     * 在背包中查找物品
     * @return 物品槽位，-1 表示未找到
     */
    protected int findItemInInventory(Item item) {
        // 简化处理：假设总是有工具
        return 0;
    }
    
    /**
     * 计算挖掘延迟（tick）
     */
    protected int calculateMiningDelay(@NotNull BlockState state, @NotNull BlockPos pos) {
        ItemStack tool = entity.getMainHandItem();
        if (tool.isEmpty()) {
            return (int) state.getDestroySpeed(entity.level(), pos);
        }
        
        float destroySpeed = tool.getItem().getDestroySpeed(tool, state);
        if (destroySpeed <= 0) {
            return 100; // 无法挖掘
        }
        
        // 基础延迟 / 挖掘速度
        return Math.max(1, (int) (20.0F / destroySpeed));
    }
    
    /**
     * 延迟检查
     * @param ticks 需要的延迟 tick 数
     * @return true 如果延迟还未结束
     */
    protected boolean hasNotDelayed(int ticks) {
        if (delayCounter < ticks) {
            delayCounter++;
            return true;
        }
        delayCounter = 0;
        delayTarget = 0;
        return false;
    }
    
    /**
     * 设置延迟（重置延迟计数器，使下次调用hasNotDelayed时从头开始计时）
     */
    protected void setDelay(int ticks) {
        delayCounter = 0;
        delayTarget = ticks;
    }
    
    /**
     * 收集附近的掉落物
     * 参考 Minecolonies 的 gatherItems 方法
     */
    protected void gatherItems() {
        if (itemsToPickup == null || itemsToPickup.isEmpty()) {
            fillItemsList();
            return;
        }
        
        // 启用拾取
        entity.setCanPickUpLoot(true);
        
        // 移动到最近的物品
        BlockPos nearestItem = getNearestItemPosition();
        if (nearestItem != null) {
            double distance = entity.distanceToSqr(nearestItem.getX() + 0.5, nearestItem.getY(), nearestItem.getZ() + 0.5);
            
            if (distance > 2.0) {
                // 还在移动中
                entity.moveTo(nearestItem.getX() + 0.5, nearestItem.getY(), nearestItem.getZ() + 0.5, 0.6);
                
                // 卡住检测
                checkIfStuck();
            } else {
                // 到达物品位置，移除该物品
                itemsToPickup.remove(nearestItem);
            }
        }
    }
    
    /**
     * 填充物品列表
     */
    protected void fillItemsList() {
        itemsToPickup = new ArrayList<>();
        
        // 搜索附近的掉落物实体
        List<net.minecraft.world.entity.item.ItemEntity> nearbyItems = entity.level()
            .getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, 
                entity.getBoundingBox().inflate(5.0, 3.0, 5.0));
        
        for (net.minecraft.world.entity.item.ItemEntity itemEntity : nearbyItems) {
            if (itemEntity.isAlive() && isItemWorthPickingUp(itemEntity.getItem())) {
                itemsToPickup.add(itemEntity.blockPosition());
            }
        }
    }
    
    /**
     * 判断物品是否值得拾取
     */
    protected boolean isItemWorthPickingUp(ItemStack stack) {
        return !stack.isEmpty();
    }
    
    /**
     * 获取最近的物品位置
     */
    @Nullable
    protected BlockPos getNearestItemPosition() {
        if (itemsToPickup == null || itemsToPickup.isEmpty()) {
            return null;
        }
        
        BlockPos nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (BlockPos pos : itemsToPickup) {
            double distance = entity.distanceToSqr(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = pos;
            }
        }
        
        return nearest;
    }
    
    /**
     * 重置物品收集
     */
    protected void resetGatheringItems() {
        itemsToPickup = null;
    }
    
    /**
     * 卡住检测
     */
    protected void checkIfStuck() {
        // 简化的卡住检测
        if (entity.getNavigation().isDone()) {
            stillTicks = 0;
            return;
        }
        
        int currentIndex = entity.getNavigation().getPath() != null ? 
            entity.getNavigation().getPath().getNextNodeIndex() : 0;
        
        if (currentIndex != previousPathIndex) {
            stillTicks = 0;
            previousPathIndex = currentIndex;
        } else {
            stillTicks++;
            
            // 卡住超过 20 tick，尝试重新寻路
            if (stillTicks > 20) {
                entity.getNavigation().stop();
                stillTicks = 0;
            }
        }
    }
    
    /**
     * 添加挖掘经验
     */
    protected void addExperienceForMining(BlockState state) {
        // 可以在这里添加经验系统
    }
    
    /**
     * 更新当前状态
     */
    protected void setCurrentState(AIState state) {
        this.currentState = state;
    }
    
    /**
     * 获取当前状态
     */
    public AIState getCurrentState() {
        return currentState;
    }
}
