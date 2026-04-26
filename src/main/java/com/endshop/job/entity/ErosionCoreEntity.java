package com.endshop.job.entity;

import com.endshop.job.config.ModConfig;
import com.endshop.job.fluid.ModFluids;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 侵蚀核心 - 敌对实体
 * 在侵蚀液体中生成，会攻击任何试图靠近侵蚀流体的生物
 */
public class ErosionCoreEntity extends Monster implements GeoEntity {
    
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    
    // 动画定义
    private static final RawAnimation ATTACK_ANIMATION = RawAnimation.begin()
        .thenPlay("animation.erosion_core.attack");
    private static final RawAnimation DEATH_ANIMATION = RawAnimation.begin()
        .thenPlay("animation.erosion_core.death");
    private static final RawAnimation SPAWN_ANIMATION = RawAnimation.begin()
        .thenPlay("animation.erosion_core.spawn");
    
    private boolean isAttacking = false;
    private int attackCooldown = 0;
    
    public ErosionCoreEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }
    
    private PlayState predicate(AnimationState<ErosionCoreEntity> event) {
        if (isAttacking) {
            return event.setAndContinue(ATTACK_ANIMATION);
        }
        
        if (this.isDeadOrDying()) {
            return event.setAndContinue(DEATH_ANIMATION);
        }
        
        // 默认待机状态（没有动画时保持静止）
        return PlayState.STOP;
    }
    
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        // 近战攻击目标（攻击所有活体生物）
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, true));
        
        // 随机移动
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        
        // 看向目标
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, LivingEntity.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)  // 较高生命值
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)  // 中等攻击力
                .add(Attributes.FOLLOW_RANGE, 20.0D);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (!this.level().isClientSide) {
            // 检查配置是否启用侵蚀系统
            if (!ModConfig.ENABLE_EROSION.get()) {
                return; // 配置关闭，不执行侵蚀相关逻辑
            }
            
            // 每2秒检查是否在侵蚀流体中
            if (this.tickCount % 40 == 0) {
                checkErosionFluidEnvironment();
            }
            
            // 寻找并攻击靠近侵蚀流体的生物
            if (this.tickCount % 20 == 0) {
                findAndAttackNearbyEntities();
            }
        }
    }
    
    /**
     * 检查是否在侵蚀流体环境中
     * 如果不在侵蚀流体中，会逐渐受到伤害
     */
    private void checkErosionFluidEnvironment() {
        BlockPos pos = this.blockPosition();
        BlockState state = this.level().getBlockState(pos);
        
        // 检查脚下或周围是否有侵蚀流体
        boolean isInErosionFluid = isNearErosionFluid(pos);
        
        if (!isInErosionFluid) {
            // 不在侵蚀流体中，每秒受到2点伤害
            this.hurt(this.damageSources().magic(), 2.0F);
        }
    }
    
    /**
     * 寻找并攻击靠近侵蚀流体的生物
     */
    private void findAndAttackNearbyEntities() {
        // 查找周围10格内的所有活体生物
        java.util.List<LivingEntity> nearbyEntities = this.level().getEntitiesOfClass(
            LivingEntity.class,
            this.getBoundingBox().inflate(10.0),
            entity -> entity != this && !entity.isAlliedTo(this) && entity.isAlive()
        );
        
        for (LivingEntity entity : nearbyEntities) {
            // 检查该生物是否靠近侵蚀流体
            if (isEntityNearErosionFluid(entity)) {
                // 设置为攻击目标
                this.setTarget(entity);
                break; // 一次只攻击一个目标
            }
        }
    }
    
    /**
     * 检查生物是否靠近侵蚀流体
     */
    private boolean isEntityNearErosionFluid(LivingEntity entity) {
        BlockPos pos = entity.blockPosition();
        return isNearErosionFluid(pos);
    }
    
    /**
     * 检查位置附近是否有侵蚀流体
     */
    private boolean isNearErosionFluid(BlockPos pos) {
        // 检查当前位置及周围6格范围内是否有侵蚀流体
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    if (this.level().getFluidState(checkPos).is(ModFluids.EROSION_SOURCE.get()) ||
                        this.level().getFluidState(checkPos).is(ModFluids.EROSION_FLOWING.get())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
