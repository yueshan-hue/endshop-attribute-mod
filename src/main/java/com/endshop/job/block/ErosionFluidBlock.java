package com.endshop.job.block;

import com.endshop.job.config.ModConfig;
import com.endshop.job.effect.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;

/**
 * 侵蚀流体方块
 * 进入的生物会获得侵蚀效果，每分钟累积一层
 */
public class ErosionFluidBlock extends LiquidBlock {
    
    public ErosionFluidBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }
    
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        
        // 检查配置是否启用侵蚀流体效果
        if (!ModConfig.ENABLE_EROSION.get() || !ModConfig.ENABLE_EROSION_FLUID.get()) {
            return; // 配置关闭，不施加效果
        }
        
        // 只对活体实体生效
        if (entity instanceof LivingEntity livingEntity) {
            // 检查是否已有侵蚀效果
            MobEffectInstance existingEffect = livingEntity.getEffect(ModEffects.EROSION);
            
            if (existingEffect == null) {
                // 如果没有侵蚀效果，添加一个无限持续时间的效果
                // 效果的tick逻辑会处理层数累积
                livingEntity.addEffect(new MobEffectInstance(
                    ModEffects.EROSION,
                    Integer.MAX_VALUE,  // 无限持续时间
                    0,                  // 等级0
                    false,              // 不显示粒子
                    false               // 不显示图标（隐藏效果）
                ));
            }
        }
    }
}
