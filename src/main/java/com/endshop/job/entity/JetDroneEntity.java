package com.endshop.job.entity;

import com.endshop.job.fluid.ModFluids;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.material.FluidState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 射流无人机实体 - 支持基岩版模型和动画
 */
public class JetDroneEntity extends EndshopEntity implements GeoEntity {
    
    // GeckoLib 动画实例缓存
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    
    // 当前状态: 0=待机, 1=飞行, 2=下降, 3=进场, 4=退场
    private int state = 0;
    
    // 生存时间
    private int lifetime = 0;
    private int maxLifetime = 600; // 默认 30 秒
    
    // 跟随参数
    private static final double FOLLOW_DISTANCE = 3.0; // 跟随距离
    private static final double FOLLOW_HEIGHT = 2.0; // 跟随高度
    private static final double FOLLOW_SPEED = 0.15; // 跟随速度
    
    // 发射粒子参数
    private static final double BEAM_RANGE = 10.0; // 粒子束范围
    private static final int COOLDOWN_TICKS = 20; // 冷却时间（1秒）
    private int shootCooldown = 0;
    private boolean isShooting = false;
    
    // 动画定义
    private static final RawAnimation FLY_ANIMATION = RawAnimation.begin()
        .thenLoop("animation.jet_drone.fly");
    
    private static final RawAnimation DESCEND_ANIMATION = RawAnimation.begin()
        .thenPlayAndHold("animation.jet_drone.descend");
    
    private static final RawAnimation ENTER_ANIMATION = RawAnimation.begin()
        .thenPlayAndHold("animation.jet_drone.enter");
    
    private static final RawAnimation EXIT_ANIMATION = RawAnimation.begin()
        .thenPlayAndHold("animation.jet_drone.exit");
    
    public JetDroneEntity(EntityType<? extends EndshopEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        // 不需要额外的同步数据
    }
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 注册动画控制器
        controllers.add(new AnimationController<>(this, "drone_controller", 0, this::predicate));
    }
    
    /**
     * 动画控制器逻辑
     */
    private PlayState predicate(AnimationState<JetDroneEntity> event) {
        switch (this.state) {
            case 1:
                return event.setAndContinue(FLY_ANIMATION);
            case 2:
                return event.setAndContinue(DESCEND_ANIMATION);
            case 3:
                return event.setAndContinue(ENTER_ANIMATION);
            case 4:
                return event.setAndContinue(EXIT_ANIMATION);
            default:
                // 待机状态不播放动画
                return PlayState.STOP;
        }
    }
    
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // 增加生存时间
        this.lifetime++;
        
        // 超过最大生存时间则移除
        if (this.lifetime >= this.maxLifetime) {
            this.discard();
            return;
        }
        
        // 射击冷却倒计时
        if (shootCooldown > 0) {
            shootCooldown--;
        }
        
        // 跟随玩家逻辑（只在服务端执行）
        if (!level().isClientSide()) {
            followPlayer();
        }
    }
    
    /**
     * 跟随玩家
     */
    private void followPlayer() {
        Player nearestPlayer = level().getNearestPlayer(this, 32.0);
        
        if (nearestPlayer != null) {
            // 计算目标位置（玩家前方3格，上方2格）
            double yaw = Math.toRadians(nearestPlayer.getYRot());
            double targetX = nearestPlayer.getX() - Math.sin(yaw) * FOLLOW_DISTANCE;
            double targetY = nearestPlayer.getY() + FOLLOW_HEIGHT;
            double targetZ = nearestPlayer.getZ() + Math.cos(yaw) * FOLLOW_DISTANCE;
            
            // 计算当前到目标的距离
            double dx = targetX - this.getX();
            double dy = targetY - this.getY();
            double dz = targetZ - this.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            
            // 如果距离超过阈值，开始移动
            if (distance > 0.5) {
                // 归一化方向向量
                double nx = dx / distance;
                double ny = dy / distance;
                double nz = dz / distance;
                
                // 平滑移动
                this.setDeltaMovement(
                    nx * FOLLOW_SPEED,
                    ny * FOLLOW_SPEED,
                    nz * FOLLOW_SPEED
                );
                
                // 设置朝向（面向玩家）
                double lookYaw = Math.atan2(
                    nearestPlayer.getX() - this.getX(),
                    nearestPlayer.getZ() - this.getZ()
                );
                this.setYRot((float) Math.toDegrees(lookYaw));
            } else {
                // 距离足够近，停止移动
                this.setDeltaMovement(Vec3.ZERO);
            }
            
            // 每个 tick 执行移动
            this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
        }
    }
    
    /**
     * 设置无人机状态
     */
    public void setState(int state) {
        this.state = state;
    }
    
    /**
     * 获取无人机状态
     */
    public int getState() {
        return this.state;
    }
    
    /**
     * 设置生存时间 (tick)
     */
    public void setLifetime(int ticks) {
        this.maxLifetime = ticks;
    }
    
    /**
     * 发射粒子束并清除侵蚀流体（只在服务端调用）
     */
    public void shootBeam() {
        if (!level().isClientSide() && shootCooldown <= 0) {
            // 重置冷却
            shootCooldown = COOLDOWN_TICKS;
            isShooting = true;
            
            // 获取最近的玩家，使用其视角方向
            Player nearestPlayer = level().getNearestPlayer(this, 32.0);
            if (nearestPlayer == null) {
                isShooting = false;
                return;
            }
            
            // 获取玩家视角方向
            double yaw = Math.toRadians(nearestPlayer.getYRot());
            double pitch = Math.toRadians(nearestPlayer.getXRot());
            
            // 计算方向向量
            double dirX = -Math.sin(yaw) * Math.cos(pitch);
            double dirY = -Math.sin(pitch);
            double dirZ = Math.cos(yaw) * Math.cos(pitch);
            
            // 清除路径上的侵蚀流体
            List<net.minecraft.core.BlockPos> clearedBlocks = new ArrayList<>();
            
            for (double d = 0; d < BEAM_RANGE; d += 0.5) {
                double x = this.getX() + dirX * d;
                double y = this.getY() + dirY * d;
                double z = this.getZ() + dirZ * d;
                
                net.minecraft.core.BlockPos pos = net.minecraft.core.BlockPos.containing(x, y, z);
                FluidState fluidState = level().getFluidState(pos);
                
                // 检查是否是侵蚀流体
                if (fluidState.is(ModFluids.EROSION_SOURCE.get()) || 
                    fluidState.is(ModFluids.EROSION_FLOWING.get())) {
                    // 设置为空气
                    level().setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
                    clearedBlocks.add(pos);
                }
            }
            
            // 生成粒子束效果（使用 sendParticles 让所有客户端都能看到）
            for (int i = 0; i < 50; i++) {  // 增加粒子数量
                double t = (double) i / 50.0;
                double px = this.getX() + dirX * t * BEAM_RANGE;
                double py = this.getY() + dirY * t * BEAM_RANGE;
                double pz = this.getZ() + dirZ * t * BEAM_RANGE;
                
                // 使用 sendParticles 让粒子在所有客户端显示
                if (level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    // 创建绿色尘埃粒子选项
                    org.joml.Vector3f greenColor = new org.joml.Vector3f(0.0F, 1.0F, 0.0F);
                    net.minecraft.core.particles.DustParticleOptions greenDust = new net.minecraft.core.particles.DustParticleOptions(
                        greenColor,
                        1.0F // 粒子大小
                    );
                    serverLevel.sendParticles(
                        greenDust,
                        px + (Math.random() - 0.5) * 0.3,
                        py + (Math.random() - 0.5) * 0.3,
                        pz + (Math.random() - 0.5) * 0.3,
                        1,  // 粒子数量
                        (Math.random() - 0.5) * 0.1,
                        (Math.random() - 0.5) * 0.1,
                        (Math.random() - 0.5) * 0.1,
                        0.01  // 速度
                    );
                }
            }
            
            // 在被清除的位置生成额外粒子（使用 sendParticles）
            for (net.minecraft.core.BlockPos pos : clearedBlocks) {
                for (int i = 0; i < 10; i++) {
                    if (level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        serverLevel.sendParticles(
                            ParticleTypes.END_ROD,
                            pos.getX() + 0.5 + (Math.random() - 0.5),
                            pos.getY() + 0.5 + (Math.random() - 0.5),
                            pos.getZ() + 0.5 + (Math.random() - 0.5),
                            1,  // 粒子数量
                            (Math.random() - 0.5) * 0.2,
                            Math.random() * 0.2,
                            (Math.random() - 0.5) * 0.2,
                            0.02  // 速度
                        );
                    }
                }
            }
            
            // 结束射击标记
            isShooting = false;
        }
    }
    
    @Override
    public boolean isPickable() {
        return false; // 不可被选中
    }
    
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.lifetime = compound.getInt("Lifetime");
        this.maxLifetime = compound.getInt("MaxLifetime");
        this.state = compound.getInt("State");
    }
    
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Lifetime", this.lifetime);
        compound.putInt("MaxLifetime", this.maxLifetime);
        compound.putInt("State", this.state);
    }
}
