package com.endshop.job.skill;

import net.minecraft.world.entity.player.Player;

/**
 * 主动技能 - 需要玩家主动触发的技能
 */
public abstract class ActiveSkill extends Skill {
    
    /** 技能冷却时间（秒） */
    protected final int cooldown;
    
    /** 当前冷却进度 */
    protected int currentCooldown;
    
    /**
     * 主动技能构造器
     * @param id 技能 ID
     * @param name 技能名称
     * @param description 技能描述
     * @param maxLevel 最大等级
     * @param cooldown 冷却时间（秒）
     */
    public ActiveSkill(String id, String name, String description, int maxLevel, int cooldown) {
        super(id, name, description, maxLevel, null);
        this.cooldown = cooldown;
        this.currentCooldown = 0;
    }
    
    /**
     * 主动技能构造器（带图标）
     * @param id 技能 ID
     * @param name 技能名称
     * @param description 技能描述
     * @param maxLevel 最大等级
     * @param cooldown 冷却时间（秒）
     * @param iconPath 技能图标路径
     */
    public ActiveSkill(String id, String name, String description, int maxLevel, int cooldown, String iconPath) {
        super(id, name, description, maxLevel, iconPath);
        this.cooldown = cooldown;
        this.currentCooldown = 0;
    }
    
    /**
     * 获取冷却时间
     */
    public int getCooldown() {
        return cooldown;
    }
    
    /**
     * 获取当前冷却进度
     */
    public int getCurrentCooldown() {
        return currentCooldown;
    }
    
    /**
     * 检查技能是否可用（不在冷却中）
     */
    public boolean isAvailable() {
        return currentCooldown <= 0;
    }
    
    /**
     * 激活技能
     * @param player 使用技能的玩家
     * @return 是否激活成功
     */
    public boolean activate(Player player) {
        if (!isUnlocked() || !isAvailable()) {
            return false;
        }
        
        // 执行技能效果
        execute(player);
        
        // 开始冷却
        startCooldown();
        
        return true;
    }
    
    /**
     * 开始冷却
     */
    public void startCooldown() {
        this.currentCooldown = cooldown;
    }
    
    /**
     * 更新冷却（每 tick 调用）
     */
    public void tick() {
        if (currentCooldown > 0) {
            currentCooldown--;
        }
    }
    
    /**
     * 执行技能效果（子类实现）
     * @param player 使用技能的玩家
     */
    public abstract void execute(Player player);
    
    /**
     * 复制技能实例
     */
    @Override
    public abstract ActiveSkill copy();
}
