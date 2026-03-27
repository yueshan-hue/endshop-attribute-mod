package com.endshop.job.skill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 技能注册表 - 管理所有技能的注册和获取
 */
public class SkillRegistry {
    
    /** 存储所有已注册的技能 */
    private static final Map<String, Skill> SKILL_MAP = new HashMap<>();
    
    /** 按职业存储技能列表 */
    private static final Map<String, List<String>> JOB_SKILLS_MAP = new HashMap<>();
    
    /**
     * 注册技能
     * @param skill 技能实例
     * @param jobId 职业 ID（可选，null 表示通用技能）
     */
    public static void register(Skill skill, String jobId) {
        if (skill == null || skill.getId() == null) {
            throw new IllegalArgumentException("技能和 ID 不能为空");
        }
        
        // 注册到全局技能表
        SKILL_MAP.put(skill.getId(), skill);
        
        // 如果是职业技能，添加到职业列表
        if (jobId != null) {
            JOB_SKILLS_MAP.computeIfAbsent(jobId, k -> new ArrayList<>())
                    .add(skill.getId());
        }
    }
    
    /**
     * 注册技能（不指定职业）
     */
    public static void register(Skill skill) {
        register(skill, null);
    }
    
    /**
     * 通过 ID 获取技能
     * @param skillId 技能 ID
     * @return 技能实例，不存在返回 null
     */
    public static Skill getSkill(String skillId) {
        return SKILL_MAP.get(skillId);
    }
    
    /**
     * 获取所有已注册的技能
     */
    public static Map<String, Skill> getAllSkills() {
        return Collections.unmodifiableMap(SKILL_MAP);
    }
    
    /**
     * 获取特定职业的所有技能 ID
     * @param jobId 职业 ID
     * @return 技能 ID 列表
     */
    public static List<String> getSkillsByJob(String jobId) {
        List<String> skills = JOB_SKILLS_MAP.get(jobId);
        return skills != null ? Collections.unmodifiableList(skills) : Collections.emptyList();
    }
    
    /**
     * 检查技能是否已注册
     * @param skillId 技能 ID
     * @return 是否已注册
     */
    public static boolean isRegistered(String skillId) {
        return SKILL_MAP.containsKey(skillId);
    }
    
    /**
     * 清空所有注册（用于重新加载）
     */
    public static void clear() {
        SKILL_MAP.clear();
        JOB_SKILLS_MAP.clear();
    }
    
    /**
     * 获取注册的技能数量
     */
    public static int size() {
        return SKILL_MAP.size();
    }
}
