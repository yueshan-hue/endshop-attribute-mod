package com.endshop.job.skill;

import com.endshop.job.skill.skills.QuickHealSkill;
import com.endshop.job.skill.skills.StrengthBoostSkill;
import com.endshop.job.profession.Profession;

/**
 * 技能初始化器 - 注册所有技能
 */
public class SkillInitializer {
    
    /**
     * 初始化并注册所有技能
     */
    public static void init() {
        // 注册通用技能
        registerGenericSkills();
        
        // 注册职业技能
        registerJobSkills();
    }
    
    /**
     * 注册通用技能（所有职业都可以学习）
     */
    private static void registerGenericSkills() {
        // 快速治疗 - 所有职业都可以使用
        SkillRegistry.register(new QuickHealSkill());
    }
    
    /**
     * 注册职业技能（特定职业专属）
     */
    private static void registerJobSkills() {
        // 医疗职业专属技能
        SkillRegistry.register(new StrengthBoostSkill(), Profession.MEDIC.name());
        
        // TODO: 为其他职业添加专属技能
        // 先锋职业专属技能
        // SkillRegistry.register(new VanguardSkill(), Profession.VANGUARD.name());
        
        // 近卫职业专属技能
        // SkillRegistry.register(new GuardSkill(), Profession.GUARD.name());
        
        // 重装职业专属技能
        // SkillRegistry.register(new DefenderSkill(), Profession.DEFENDER.name());
        
        // 狙击职业专属技能
        // SkillRegistry.register(new SniperSkill(), Profession.SNIPER.name());
        
        // 特种职业专属技能
        // SkillRegistry.register(new SpecialistSkill(), Profession.SPECIALIST.name());
        
        // 术师职业专属技能
        // SkillRegistry.register(new CasterSkill(), Profession.CASTER.name());
        
        // 辅助职业专属技能
        // SkillRegistry.register(new SupporterSkill(), Profession.SUPPORTER.name());
    }
}
