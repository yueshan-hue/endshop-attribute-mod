package com.endshop.job.operators;

import com.endshop.job.profession.Profession;

/**
 * 史尔特尔技能组
 */
public class SurtrSkills {
    
    public static void register() {
        // 技能已在 SkillInitializer 中注册
    }
    
    public static Profession getProfession() {
        return SurtrProfile.PROFESSION;
    }
    
    public static String[] getSkills() {
        return SurtrProfile.SKILLS;
    }
}
