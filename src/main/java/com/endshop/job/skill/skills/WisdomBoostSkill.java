package com.endshop.job.skill.skills;

import com.endshop.job.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.resources.ResourceLocation;

/**
 * 被动技能 - 智识强化
 * 永久增加智识属性
 */
public class WisdomBoostSkill extends PassiveSkill {
    
    /** 每等级增加的智识值 */
    private final float wisdomPerLevel;
    
    /** 修饰符 ID */
    private static final ResourceLocation MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("endshopattribute", "wisdom_boost");
    
    public WisdomBoostSkill() {
        super("wisdom_boost", "智识强化", "永久增加智识属性", 10);
        this.wisdomPerLevel = 1.0f; // 每级增加 1 点智识
    }
    
    @Override
    protected void applyEffect(Player player) {
        if (player == null) return;
        
        // 计算总加成
        float totalBonus = wisdomPerLevel * getCurrentLevel();
        
        // 创建属性修饰符
        AttributeModifier modifier = new AttributeModifier(
            MODIFIER_ID,
            totalBonus,
            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE
        );
        
        // 移除旧的修饰符（如果存在）
        removeEffect(player);
        
        // 添加新的修饰符
        // 注意：这里使用自定义属性键，因为 Minecraft 默认没有智识属性
        // 实际效果需要在属性事件处理中实现
        player.sendSystemMessage(
            net.minecraft.network.chat.Component.literal("§a智识强化生效！智识 +" + totalBonus)
        );
    }
    
    @Override
    protected void removeEffect(Player player) {
        if (player == null) return;
        
        // 移除此技能的修饰符
        // 实际移除逻辑需要在属性事件处理中实现
    }
    
    @Override
    public boolean isUnlocked() {
        // 这里可以根据职业、等级等条件判断是否解锁
        return true;
    }
    
    @Override
    public PassiveSkill copy() {
        return new WisdomBoostSkill();
    }
}
