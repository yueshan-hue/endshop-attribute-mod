package com.endshop.job.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * GeckoLib 技能特效实体 - 支持基岩版模型和动画
 */
public class GeckoSkillEffectEntity extends Entity implements GeoEntity {
    
    // GeckoLib 动画实例缓存
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    
    // 特效类型
    private String effectType = "ice_crystal";
    
    // 生存时间
    private int lifetime = 0;
    private int maxLifetime = 100; // 默认 5 秒
    
    // 动画控制器
    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin()
        .thenLoop("animation.model.idle");
    
    private static final RawAnimation APPEAR_ANIMATION = RawAnimation.begin()
        .thenPlayAndHold("animation.model.appear");
    
    private static final RawAnimation DISAPPEAR_ANIMATION = RawAnimation.begin()
        .thenPlayAndHold("animation.model.disappear");
    
    public GeckoSkillEffectEntity(EntityType<? extends Entity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // 不需要额外的同步数据
    }
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 注册动画控制器
        controllers.add(new AnimationController<>(this, "effect_controller", 0, this::predicate));
    }
    
    /**
     * 动画控制器逻辑
     */
    private PlayState predicate(AnimationState<GeckoSkillEffectEntity> event) {
        // 根据生命周期阶段播放不同动画
        if (lifetime < 10) {
            // 出现动画
            return event.setAndContinue(APPEAR_ANIMATION);
        } else if (lifetime >= maxLifetime - 10) {
            // 消失动画
            return event.setAndContinue(DISAPPEAR_ANIMATION);
        } else {
            // 待机动画
            return event.setAndContinue(IDLE_ANIMATION);
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
        }
    }
    
    /**
     * 设置特效类型
     */
    public void setEffectType(String type) {
        this.effectType = type;
    }
    
    /**
     * 获取特效类型
     */
    public String getEffectType() {
        return this.effectType;
    }
    
    /**
     * 设置生存时间 (tick)
     */
    public void setLifetime(int ticks) {
        this.maxLifetime = ticks;
    }
    
    @Override
    public boolean isPickable() {
        return false; // 不可被选中
    }
    
    @Override
    public boolean shouldBeSaved() {
        return false; // 不保存到世界
    }
    
    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.lifetime = compound.getInt("Lifetime");
        this.maxLifetime = compound.getInt("MaxLifetime");
        if (compound.contains("EffectType")) {
            this.effectType = compound.getString("EffectType");
        }
    }
    
    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Lifetime", this.lifetime);
        compound.putInt("MaxLifetime", this.maxLifetime);
        compound.putString("EffectType", this.effectType);
    }
}
