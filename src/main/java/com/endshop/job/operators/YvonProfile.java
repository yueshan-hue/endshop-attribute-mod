package com.endshop.job.operators;

import com.endshop.job.profession.Profession;

/**
 * 干员档案 - 伊冯 (Yvon)
 * 
 * 职业: 近卫
 * 定位: 物理输出/控制
 */
public class YvonProfile {
    
    // ========== 基本信息 ==========
    public static final String OPERATOR_NAME = "伊冯";
    public static final String OPERATOR_ID = "yvon";
    public static final Profession PROFESSION = Profession.GUARD;
    
    // ========== 能力值配置 ==========
    public static final int BASE_WISDOM = 10;    // 智识
    public static final int BASE_STRENGTH = 14;  // 力量 (主能力)
    public static final int BASE_AGILITY = 13;   // 敏捷 (副能力)
    public static final int BASE_WILLPOWER = 8;  // 意志
    
    // ========== 技能列表 ==========
    public static final String[] SKILLS = {
        "true_blade",           // 真斩
        "slash_dash",           // 斩击突进
        "cleave_charge",        // 劈砍冲锋
        "sequence_shock"        // 序列冲击
    };
    
    // ========== 干员描述 ==========
    public static final String DESCRIPTION = 
        "擅长对敌人造成物理伤害，并通过击飞、倒地和震退等物理效果控制敌人。\n" +
        "主能力: 力量 | 副能力: 敏捷";
}
