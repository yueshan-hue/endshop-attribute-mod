package com.endshop.job.ai.task;

import com.endshop.job.ai.state.AIState;
import com.endshop.job.entity.EndshopEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 钓鱼任务处理器
 * 状态机流程：IDLE -> SEARCHING_WATER -> MOVING_TO_WATER -> CASTING -> WAITING -> REELING -> GATHERING -> IDLE
 */
public class FishingTaskHandler implements TaskHandler {
    private final EndshopEntity entity;
    private BlockPos targetWaterPos;
    private int searchRange;

    // 钓鱼状态
    private enum FishState {
        SEARCHING, MOVING, CASTING, WAITING, REELING
    }
    private FishState fishState = FishState.SEARCHING;

    // 等待计时（模拟等鱼上钩）
    private int waitTicks = 0;
    // 钓鱼等待时间：60~200 tick 随机（3~10秒）
    private int waitTarget = 100;
    // 钓鱼竿实体
    private FishingHook fishingHook = null;
    // 防刷屏冷却
    private int castCooldown = 0;
    private static final int CAST_COOLDOWN = 20; // 抛出后至少等20tick再判断

    public FishingTaskHandler(EndshopEntity entity) {
        this.entity = entity;
        this.searchRange = 20;
    }

    @Override
    public void initialize() {
        targetWaterPos = null;
        fishState = FishState.SEARCHING;
        waitTicks = 0;
        fishingHook = null;
        castCooldown = 0;
    }

    @Override
    public AIState handle() {
        switch (fishState) {
            case SEARCHING:
                return handleSearching();
            case MOVING:
                return handleMoving();
            case CASTING:
                return handleCasting();
            case WAITING:
                return handleWaiting();
            case REELING:
                return handleReeling();
            default:
                fishState = FishState.SEARCHING;
                return AIState.SEARCHING;
        }
    }

    @Override
    public void reset() {
        // 收回鱼钩
        retractHook();
        initialize();
    }

    @Override
    public EndshopEntity getEntity() {
        return entity;
    }

    /**
     * 搜索最近的水源
     */
    private AIState handleSearching() {
        searchForWater();
        if (targetWaterPos == null) {
            return AIState.NO_TARGET_FOUND;
        }
        fishState = FishState.MOVING;
        return AIState.MOVING;
    }

    /**
     * 移动到水边
     */
    private AIState handleMoving() {
        if (targetWaterPos == null) {
            fishState = FishState.SEARCHING;
            return AIState.SEARCHING;
        }

        // 目标是水面上方一格（站在水边）
        BlockPos standPos = findStandingPosNearWater(targetWaterPos);
        if (standPos == null) {
            // 找不到站立位置，重新搜索
            targetWaterPos = null;
            fishState = FishState.SEARCHING;
            return AIState.SEARCHING;
        }

        // 检查是否到达
        double distSq = entity.distanceToSqr(
            standPos.getX() + 0.5, standPos.getY(), standPos.getZ() + 0.5);
        if (distSq <= 9.0) { // 3格以内就算到了
            fishState = FishState.CASTING;
            return AIState.WORKING;
        }

        // 移动过去
        entity.moveTo(standPos.getX() + 0.5, standPos.getY(), standPos.getZ() + 0.5, 0.6);
        return AIState.MOVING;
    }

    /**
     * 抛钩
     */
    private AIState handleCasting() {
        if (targetWaterPos == null) {
            fishState = FishState.SEARCHING;
            return AIState.SEARCHING;
        }

        // 装备钓鱼竿
        entity.setItemInHand(InteractionHand.MAIN_HAND, Items.FISHING_ROD.getDefaultInstance());

        // 面向水面
        entity.lookAt(
            targetWaterPos.getX() + 0.5,
            targetWaterPos.getY() + 0.5,
            targetWaterPos.getZ() + 0.5
        );

        // 抛出鱼钩实体
        if (!entity.level().isClientSide()) {
            // 收回旧钩
            retractHook();
            // 创建新钩 - 使用EntityType构造
            fishingHook = net.minecraft.world.entity.EntityType.FISHING_BOBBER.create(entity.level());
            if (fishingHook != null) {
                // 计算抛出方向
                Vec3 eyePos = entity.getEyePosition();
                Vec3 target = new Vec3(
                    targetWaterPos.getX() + 0.5,
                    targetWaterPos.getY() + 0.1,
                    targetWaterPos.getZ() + 0.5
                );
                Vec3 dir = target.subtract(eyePos).normalize();
                fishingHook.setPos(eyePos.x, eyePos.y, eyePos.z);
                fishingHook.setDeltaMovement(dir.scale(0.5));
                entity.level().addFreshEntity(fishingHook);
            }
        }

        // 播放抛竿动画
        entity.swing(InteractionHand.MAIN_HAND);

        // 设置等待时间（模拟随机上钩时间）
        waitTicks = 0;
        waitTarget = 80 + entity.level().getRandom().nextInt(120); // 4~10秒
        castCooldown = CAST_COOLDOWN;
        fishState = FishState.WAITING;
        return AIState.WORKING;
    }

    /**
     * 等待上钩
     */
    private AIState handleWaiting() {
        if (castCooldown > 0) {
            castCooldown--;
            return AIState.WORKING;
        }

        waitTicks++;

        // 检查鱼钩是否还在水里（可能被打掉或失效）
        if (fishingHook != null && !fishingHook.isAlive()) {
            fishingHook = null;
            // 重新抛钩
            fishState = FishState.CASTING;
            return AIState.WORKING;
        }

        // 等够了，收竿
        if (waitTicks >= waitTarget) {
            fishState = FishState.REELING;
            return AIState.WORKING;
        }

        // 继续等待，偶尔抖动一下表示在钓鱼
        if (waitTicks % 20 == 0) {
            entity.lookAt(
                targetWaterPos.getX() + 0.5,
                targetWaterPos.getY(),
                targetWaterPos.getZ() + 0.5
            );
        }

        return AIState.WORKING;
    }

    /**
     * 收竿（获得战利品）
     */
    private AIState handleReeling() {
        // 收回鱼钩
        retractHook();

        // 播放收竿动画
        entity.swing(InteractionHand.MAIN_HAND);

        // 给实体背包添加鱼（简化处理：直接生成物品掉落）
        if (!entity.level().isClientSide() && targetWaterPos != null) {
            // 在钓鱼位置附近生成物品
            net.minecraft.world.entity.item.ItemEntity fishItem =
                new net.minecraft.world.entity.item.ItemEntity(
                    entity.level(),
                    targetWaterPos.getX() + 0.5,
                    targetWaterPos.getY() + 0.3,
                    targetWaterPos.getZ() + 0.5,
                    new ItemStack(Items.COD)
                );
            fishItem.setDefaultPickUpDelay();
            entity.level().addFreshEntity(fishItem);
        }

        // 回到抛钩，继续钓
        waitTicks = 0;
        fishState = FishState.CASTING;
        return AIState.WORKING;
    }

    /**
     * 收回鱼钩实体
     */
    private void retractHook() {
        if (fishingHook != null && fishingHook.isAlive()) {
            fishingHook.discard();
        }
        fishingHook = null;
    }

    /**
     * 搜索水源
     */
    private void searchForWater() {
        BlockPos center = entity.blockPosition();
        double minDistance = Double.MAX_VALUE;

        for (int x = -searchRange; x <= searchRange; x++) {
            for (int y = -2; y <= 5; y++) {
                for (int z = -searchRange; z <= searchRange; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState state = entity.level().getBlockState(pos);

                    if (isWaterSurface(state, pos)) {
                        // 检查这个水面旁边能否站人
                        BlockPos stand = findStandingPosNearWater(pos);
                        if (stand != null) {
                            double distance = entity.distanceToSqr(
                                pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                            if (distance < minDistance) {
                                minDistance = distance;
                                targetWaterPos = pos;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 在水面旁边找一个可以站立钓鱼的位置
     */
    private BlockPos findStandingPosNearWater(BlockPos waterPos) {
        // 水面旁边 1~3 格，找陆地
        for (int radius = 1; radius <= 3; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx == 0 && dz == 0) continue;
                    BlockPos checkPos = waterPos.offset(dx, 0, dz);
                    // 这格是空气/可通行
                    BlockState checkState = entity.level().getBlockState(checkPos);
                    BlockState belowState = entity.level().getBlockState(checkPos.below());
                    if ((!checkState.isSolid() || checkState.isAir()) &&
                            belowState.isSolid() && !belowState.isAir()) {
                        return checkPos;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 检查是否是水面（水方块且上方是空气）
     */
    private boolean isWaterSurface(BlockState state, BlockPos pos) {
        if (!state.is(Blocks.WATER) && !state.is(Blocks.BUBBLE_COLUMN)) return false;
        BlockState above = entity.level().getBlockState(pos.above());
        return above.isAir();
    }
}
