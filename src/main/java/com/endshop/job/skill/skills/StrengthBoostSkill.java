package com.endshop.job.skill.skills;

import com.endshop.job.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.resources.ResourceLocation;

/**
 * 示例被动技能 - 力量强化
 */
public class StrengthBoostSkill extends PassiveSkill {
    
    /** 每等级增加的力量值 */
    private final float strengthPerLevel;
    
    /** 修饰符 ID */
    private static final ResourceLocation MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("endshopattribute", "strength_boost");
    
    public StrengthBoostSkill() {
        super("strength_boost", "力量强化", "永久增加攻击力", 10);
        this.strengthPerLevel = 0.5f; // 每级增加 0.5 点攻击力
    }
    
    @Override
    protected void applyEffect(Player player) {
        if (player == null) return;
        
        // 计算总加成
        float totalBonus = strengthPerLevel * getCurrentLevel();
        
        // 创建属性修饰符
        AttributeModifier modifier = new AttributeModifier(
            MODIFIER_ID,
            totalBonus,
            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE
        );
        
        // 移除旧的修饰符（如果存在）
        removeEffect(player);
        
        // 添加新的修饰符
        player.getAttribute(Attributes.ATTACK_DAMAGE)
              .addTransientModifier(modifier);
    }
    
    @Override
    protected void removeEffect(Player player) {
        if (player == null) return;
        
        // 移除此技能的修饰符
        player.getAttribute(Attributes.ATTACK_DAMAGE)
              .removeModifier(MODIFIER_ID);
    }
    
    @Override
    public boolean isUnlocked() {
        // 这里可以根据职业、等级等条件判断是否解锁
        return true;
    }
    
    @Override
    public PassiveSkill copy() {
        return new StrengthBoostSkill();
    }
}
