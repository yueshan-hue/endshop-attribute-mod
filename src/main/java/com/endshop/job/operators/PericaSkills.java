package com.endshop.job.operators;

import com.endshop.job.profession.Profession;

/**
 * 佩丽卡技能组
 */
public class PericaSkills {
    
    public static void register() {
        // 技能已在 SkillInitializer 中注册
    }
    
    public static Profession getProfession() {
        return PericaProfile.PROFESSION;
    }
    
    public static String[] getSkills() {
        return PericaProfile.SKILLS;
    }
}
