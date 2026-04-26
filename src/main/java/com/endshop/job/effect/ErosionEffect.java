package com.endshop.job.effect;

import com.endshop.job.EndshopJob;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;

/**
 * 侵蚀效果 - 隐藏效果，累积层数
 * 达到100层时：玩家回到出生点，生物被移除
 */
public class ErosionEffect extends MobEffect {
    
    private static final String EROSION_STACKS_TAG = "erosion_stacks";
    private static final int MAX_STACKS = 100;
    
    public ErosionEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B4513); // 棕色
    }
    
    public boolean isHiddenEffect() {
        return true; // 隐藏该效果
    }
    
    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // 只在服务端执行
        if (entity.level().isClientSide) {
            return true;
        }
        
        // 获取当前层数
        int currentStacks = getErosionStacks(entity);
        
        // 每1200tick(1分钟)增加1层
        if (entity.tickCount % 1200 == 0) {
            currentStacks++;
            setErosionStacks(entity, currentStacks);
            
            // 调试日志
            EndshopJob.LOGGER.info("[侵蚀效果] {} 的侵蚀层数: {}/100", entity.getName().getString(), currentStacks);
            
            // 检查是否达到100层
            if (currentStacks >= MAX_STACKS) {
                handleMaxErosion(entity);
            }
        }
        
        return true;
    }
    
    /**
     * 处理达到最大层数的情况
     */
    private void handleMaxErosion(LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            // 玩家：传送到出生点
            EndshopJob.LOGGER.info("玩家 {} 因侵蚀层数达到100层被传送回出生点", player.getName().getString());
            
            // 获取出生点位置
            net.minecraft.core.BlockPos spawnPos = player.getRespawnPosition();
            net.minecraft.server.level.ServerLevel spawnLevel = player.server.getLevel(player.getRespawnDimension());
            
            if (spawnPos != null && spawnLevel != null) {
                // 传送到出生点（使用正确的teleportTo方法）
                player.teleportTo(spawnLevel, spawnPos.getX() + 0.5, spawnPos.getY() + 1.0, spawnPos.getZ() + 0.5, java.util.Set.of(), 0.0F, 0.0F);
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "§c你已被侵蚀完全吞噬！\n§7已传送回出生点"
                ));
            } else {
                // 如果没有设置出生点，传送到世界出生点
                net.minecraft.server.level.ServerLevel worldSpawnLevel = player.server.getLevel(net.minecraft.world.level.Level.OVERWORLD);
                if (worldSpawnLevel != null) {
                    net.minecraft.world.level.border.WorldBorder worldBorder = worldSpawnLevel.getWorldBorder();
                    player.teleportTo(worldSpawnLevel, worldBorder.getCenterX(), 100.0, worldBorder.getCenterZ(), java.util.Set.of(), 0.0F, 0.0F);
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§c你已被侵蚀完全吞噬！\n§7已传送回世界出生点"
                    ));
                }
            }
            
            // 传送后清除侵蚀效果和层数
            player.removeEffect(ModEffects.EROSION);
            player.getPersistentData().putInt(EROSION_STACKS_TAG, 0);
            EndshopJob.LOGGER.info("已清除玩家 {} 的侵蚀效果", player.getName().getString());
        } else {
            // 生物：移除实体
            EndshopJob.LOGGER.info("生物 {} 因侵蚀层数达到100层被移除", entity.getName().getString());
            entity.discard();
        }
    }
    
    /**
     * 获取实体的侵蚀层数
     */
    private int getErosionStacks(LivingEntity entity) {
        CompoundTag tag = entity.getPersistentData();
        return tag.getInt(EROSION_STACKS_TAG);
    }
    
    /**
     * 设置实体的侵蚀层数
     */
    private void setErosionStacks(LivingEntity entity, int stacks) {
        CompoundTag tag = entity.getPersistentData();
        tag.putInt(EROSION_STACKS_TAG, stacks);
    }
    
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // 每tick都执行，用于累积层数
        return true;
    }
}
