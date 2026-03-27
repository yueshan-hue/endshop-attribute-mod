package com.endshop.job.skill;

import net.minecraft.world.entity.player.Player;

/**
 * 被动技能 - 自动生效的技能
 */
public abstract class PassiveSkill extends Skill {
    
    /** 是否已激活 */
    protected boolean isActive;
    
    /**
     * 被动技能构造器
     * @param id 技能 ID
     * @param name 技能名称
     * @param description 技能描述
     * @param maxLevel 最大等级
     */
    public PassiveSkill(String id, String name, String description, int maxLevel) {
        super(id, name, description, maxLevel);
        this.isActive = false;
    }
    
    /**
     * 检查技能是否激活
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * 设置技能激活状态
     */
    public void setActive(boolean active) {
        isActive = active;
    }
    
    /**
     * 应用技能效果（当技能解锁或升级时调用）
     * @param player 玩家
     */
    public void apply(Player player) {
        if (isUnlocked()) {
            applyEffect(player);
            isActive = true;
        }
    }
    
    /**
     * 移除技能效果（当技能被移除时调用）
     * @param player 玩家
     */
    public void remove(Player player) {
        removeEffect(player);
        isActive = false;
    }
    
    /**
     * 应用技能效果（子类实现）
     * @param player 玩家
     */
    protected abstract void applyEffect(Player player);
    
    /**
     * 移除技能效果（子类实现）
     * @param player 玩家
     */
    protected abstract void removeEffect(Player player);
    
    /**
     * 技能升级时的回调
     */
    @Override
    protected void onLevelUp(int newLevel) {
        // 被动技能升级时重新应用效果
        Player player = getCurrentPlayer();
        if (player != null) {
            removeEffect(player);
            applyEffect(player);
        }
    }
    
    /**
     * 获取当前玩家（用于被动技能效果应用）
     * 这个方法需要由外部提供玩家实例
     */
    private Player getCurrentPlayer() {
        // 这里返回 null，实际使用时需要通过其他方式获取玩家
        return null;
    }
    
    /**
     * 复制技能实例
     */
    @Override
    public abstract PassiveSkill copy();
}
