package com.endshop.job.skill;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * 技能基类 - 所有技能的父类
 */
public abstract class Skill {
    
    /** 技能 ID（唯一标识） */
    protected final String id;
    
    /** 技能名称 */
    protected final String name;
    
    /** 技能描述 */
    protected final String description;
    
    /** 最大等级 */
    protected final int maxLevel;
    
    /** 当前等级 */
    protected int currentLevel;
    
    /** 技能图标路径 */
    protected final String iconPath;
    
    /**
     * 技能构造器
     * @param id 技能唯一 ID
     * @param name 技能名称
     * @param description 技能描述
     * @param maxLevel 最大等级
     */
    public Skill(String id, String name, String description, int maxLevel) {
        this(id, name, description, maxLevel, null);
    }
    
    /**
     * 技能构造器（带图标）
     * @param id 技能唯一 ID
     * @param name 技能名称
     * @param description 技能描述
     * @param maxLevel 最大等级
     * @param iconPath 技能图标路径（不含扩展名）
     */
    public Skill(String id, String name, String description, int maxLevel, String iconPath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxLevel = maxLevel;
        this.currentLevel = 1;
        this.iconPath = iconPath;
    }
    
    /**
     * 获取技能 ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * 获取技能名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取技能描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 获取当前等级
     */
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    /**
     * 设置技能等级
     */
    public void setCurrentLevel(int level) {
        if (level >= 1 && level <= maxLevel) {
            this.currentLevel = level;
        }
    }
    
    /**
     * 获取最大等级
     */
    public int getMaxLevel() {
        return maxLevel;
    }
    
    /**
     * 获取技能显示名称（带等级）
     */
    public Component getDisplayName() {
        return Component.translatable("skill." + id + ".name")
                .append(Component.literal(" Lv." + currentLevel));
    }
    
    /**
     * 获取翻译后的描述
     */
    public Component getTranslatedDescription() {
        return Component.translatable("skill." + id + ".desc");
    }
    
    /**
     * 升级技能
     * @return 是否升级成功
     */
    public boolean levelUp() {
        if (currentLevel < maxLevel) {
            currentLevel++;
            onLevelUp(currentLevel);
            return true;
        }
        return false;
    }
    
    /**
     * 降级技能
     * @return 是否降级成功
     */
    public boolean levelDown() {
        if (currentLevel > 1) {
            currentLevel--;
            onLevelDown(currentLevel);
            return true;
        }
        return false;
    }
    
    /**
     * 技能升级时的回调
     */
    protected void onLevelUp(int newLevel) {
        // 子类可以重写此方法
    }
    
    /**
     * 技能降级时的回调
     */
    protected void onLevelDown(int newLevel) {
        // 子类可以重写此方法
    }
    
    /**
     * 检查技能是否已解锁
     */
    public abstract boolean isUnlocked();
    
    /**
     * 检查技能是否可以升级
     */
    public boolean canLevelUp() {
        return currentLevel < maxLevel;
    }
    
    /**
     * 复制技能实例
     */
    public abstract Skill copy();
    
    /**
     * 获取技能图标
     */
    public ResourceLocation getIconLocation() {
        if (iconPath == null || iconPath.isEmpty()) {
            return null;
        }
        return ResourceLocation.fromNamespaceAndPath("endshopattribute", "textures/skill/" + iconPath + ".png");
    }
}
