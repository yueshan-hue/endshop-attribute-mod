package com.endshop.job.operators;

import com.endshop.job.skill.SkillRegistry;
import com.endshop.job.profession.Profession;

/**
 * 伊冯技能组注册
 */
public class YvonSkills {
    
    /**
     * 注册伊冯的所有技能
     */
    public static void register() {
        // 注意: 技能已在 SkillInitializer 中注册
        // 这里仅作为文档说明伊冯可用的技能
        
        // true_slash - 真斩 (已在 SkillInitializer 注册)
        // slash_dash - 斩击突进 (已在 SkillInitializer 注册)
        // cleave_charge - 劈砍冲锋 (已在 SkillInitializer 注册)
        // sequence_shock - 序列冲击 (已在 SkillInitializer 注册)
    }
    
    /**
     * 获取伊冯的职业
     */
    public static Profession getProfession() {
        return YvonProfile.PROFESSION;
    }
    
    /**
     * 获取伊冯的技能列表
     */
    public static String[] getSkills() {
        return YvonProfile.SKILLS;
    }
}
