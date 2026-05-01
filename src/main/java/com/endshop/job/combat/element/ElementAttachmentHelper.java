package com.endshop.job.combat.element;

import net.minecraft.world.entity.LivingEntity;

/**
 * 元素附着工具类
 * 提供便捷方法为实体添加元素附着，触发元素反应
 */
public class ElementAttachmentHelper {
    
    /**
     * 为实体添加元素附着
     * @param entity 目标实体
     * @param element 元素类型
     * @param duration 持续时间（tick）
     * @param intensity 强度（影响反应伤害）
     */
    public static void applyElement(LivingEntity entity, ElementType element, float duration, float intensity) {
        if (entity == null || element == null) return;
        
        ElementReactionRegistry registry = ElementReactionRegistry.getInstance();
        registry.applyElement(entity, element, duration, intensity);
    }
    
    /**
     * 为实体添加元素附着（默认强度5.0）
     */
    public static void applyElement(LivingEntity entity, ElementType element, float duration) {
        applyElement(entity, element, duration, 5.0f);
    }
    
    /**
     * 为实体添加火元素附着
     */
    public static void applyBurning(LivingEntity entity, float duration, float intensity) {
        applyElement(entity, ElementType.BURNING, duration, intensity);
    }
    
    /**
     * 为实体添加冰元素附着
     */
    public static void applyCold(LivingEntity entity, float duration, float intensity) {
        applyElement(entity, ElementType.COLD, duration, intensity);
    }
    
    /**
     * 为实体添加雷元素附着
     */
    public static void applyElectromagnetic(LivingEntity entity, float duration, float intensity) {
        applyElement(entity, ElementType.ELECTROMAGNETIC, duration, intensity);
    }
    
    /**
     * 为实体添加草元素附着
     */
    public static void applyNature(LivingEntity entity, float duration, float intensity) {
        applyElement(entity, ElementType.NATURE, duration, intensity);
    }
    
    /**
     * 清除实体的元素附着
     */
    public static void clearElement(LivingEntity entity) {
        if (entity != null) {
            ElementReactionRegistry.getInstance().clearAttachment(entity);
        }
    }
    
    /**
     * 检查实体是否有元素附着
     */
    public static boolean hasElementAttachment(LivingEntity entity) {
        if (entity == null) return false;
        
        ElementReactionRegistry.ElementAttachment attachment = 
            ElementReactionRegistry.getInstance().getAttachment(entity);
        
        return attachment != null && attachment.getElement() != null;
    }
    
    /**
     * 获取实体当前的元素附着
     */
    public static ElementType getCurrentElement(LivingEntity entity) {
        if (entity == null) return null;
        
        ElementReactionRegistry.ElementAttachment attachment = 
            ElementReactionRegistry.getInstance().getAttachment(entity);
        
        return attachment != null ? attachment.getElement() : null;
    }
}
