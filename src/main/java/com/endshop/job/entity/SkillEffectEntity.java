package com.endshop.job.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * 技能特效实体 - 用于显示自定义模型
 */
public class SkillEffectEntity extends Entity {
    
    // 同步数据 - 存储特效类型
    private static final EntityDataAccessor<String> DATA_EFFECT_TYPE = 
        SynchedEntityData.defineId(SkillEffectEntity.class, EntityDataSerializers.STRING);
    
    // 生存时间
    private int lifetime;
    private int maxLifetime;
    
    public SkillEffectEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.maxLifetime = 100; // 默认5秒
        this.lifetime = 0;
    }
    
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_EFFECT_TYPE, "default");
    }
    
    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.lifetime = compound.getInt("Lifetime");
        this.maxLifetime = compound.getInt("MaxLifetime");
        if (compound.contains("EffectType")) {
            this.entityData.set(DATA_EFFECT_TYPE, compound.getString("EffectType"));
        }
    }
    
    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Lifetime", this.lifetime);
        compound.putInt("MaxLifetime", this.maxLifetime);
        compound.putString("EffectType", this.getEffectType());
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
        
        // 旋转效果(可选)
        this.setYRot(this.getYRot() + 2.0f);
    }
    
    /**
     * 设置特效类型
     */
    public void setEffectType(String type) {
        this.entityData.set(DATA_EFFECT_TYPE, type);
    }
    
    /**
     * 获取特效类型
     */
    public String getEffectType() {
        return this.entityData.get(DATA_EFFECT_TYPE);
    }
    
    /**
     * 设置生存时间(tick)
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
}
