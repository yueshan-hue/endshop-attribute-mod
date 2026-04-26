package com.endshop.job.skill;

import com.endshop.job.skill.skills.QuickHealSkill;
import com.endshop.job.skill.skills.StrengthBoostSkill;
import com.endshop.job.skill.skills.WisdomBoostSkill;
import com.endshop.job.skill.skills.AgilityBoostSkill;
import com.endshop.job.skill.skills.WillpowerBoostSkill;
import com.endshop.job.skill.skills.TrueBladeSkill;
import com.endshop.job.skill.skills.SlashDashSkill;
import com.endshop.job.skill.skills.CleaveChargeSkill;
import com.endshop.job.skill.skills.SequenceShockSkill;
import com.endshop.job.skill.skills.BurnoutSkill;
import com.endshop.job.skill.skills.InfernoSkill;
import com.endshop.job.skill.skills.NoiseSkill;
import com.endshop.job.skill.skills.CondensingVoiceSkill;
import com.endshop.job.skill.skills.HypothermiaSkill;
import com.endshop.job.skill.skills.SaturationDefenseSkill;
import com.endshop.job.skill.skills.IceCrystalSkill;
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
        
        // 注册管理员干员技能
        registerAdminSkills();
        
        // 注册职业技能
        registerJobSkills();
    }
    
    /**
     * 注册通用技能（所有职业都可以学习）
     */
    private static void registerGenericSkills() {
        // 快速治疗 - 所有职业都可以使用
        SkillRegistry.register(new QuickHealSkill());
        
        // 智识强化 - 所有职业都可以使用
        SkillRegistry.register(new WisdomBoostSkill());
        
        // 敏捷强化 - 所有职业都可以使用
        SkillRegistry.register(new AgilityBoostSkill());
        
        // 意志强化 - 所有职业都可以使用
        SkillRegistry.register(new WillpowerBoostSkill());
    }
    
    /**
     * 注册管理员干员技能
     */
    private static void registerAdminSkills() {
        // 注册管理员干员的所有技能
        com.endshop.job.skill.admin.AdminSkills.register();
    }
    
    /**
     * 注册职业技能（特定职业专属）
     */
    private static void registerJobSkills() {
        // 近卫职业专属技能 - 管理员
        SkillRegistry.register(new TrueBladeSkill(), Profession.GUARD.name());
        SkillRegistry.register(new SlashDashSkill(), Profession.GUARD.name());
        SkillRegistry.register(new CleaveChargeSkill(), Profession.GUARD.name());
        SkillRegistry.register(new SequenceShockSkill(), Profession.GUARD.name());
        
        // 术师职业专属技能 - 莱万汀
        SkillRegistry.register(new BurnoutSkill(), Profession.SPECIALIST.name());
        SkillRegistry.register(new InfernoSkill(), Profession.SPECIALIST.name());
        
        // 先锋职业专属技能 - 埃特拉
        SkillRegistry.register(new NoiseSkill(), Profession.VANGUARD.name());
        SkillRegistry.register(new CondensingVoiceSkill(), Profession.VANGUARD.name());
        
        // 重装职业专属技能 - 昼雪
        SkillRegistry.register(new HypothermiaSkill(), Profession.DEFENDER.name());
        SkillRegistry.register(new SaturationDefenseSkill(), Profession.DEFENDER.name());
        
        // 测试技能 - 冰晶（所有职业可用）
        SkillRegistry.register(new IceCrystalSkill());
    }
}
