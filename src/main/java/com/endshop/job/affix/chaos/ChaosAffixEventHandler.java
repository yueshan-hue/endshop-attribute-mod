package com.endshop.job.affix.chaos;

import com.endshop.job.EndshopJob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

import java.util.List;
import java.util.Map;

/**
 * 混沌词缀属性事件处理器
 * 监听装备变化，自动应用/移除词缀属性修改器
 */
@EventBusSubscriber(modid = EndshopJob.MODID)
public class ChaosAffixEventHandler {
    
    /**
     * 装备变化事件 - 应用或移除词缀属性
     */
    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        
        // 只在服务端处理
        if (player.level().isClientSide) return;
        
        ItemStack oldStack = event.getFrom();
        ItemStack newStack = event.getTo();
        
        // 移除旧物品的词缀属性
        if (!oldStack.isEmpty() && ChaosModifier.hasChaosData(oldStack)) {
            removeChaosAffixModifiers(entity, oldStack);
        }
        
        // 添加新物品的词缀属性
        if (!newStack.isEmpty() && ChaosModifier.hasChaosData(newStack)) {
            applyChaosAffixModifiers(entity, newStack);
        }
    }
    
    /**
     * 应用混沌词缀的属性修改器
     */
    private static void applyChaosAffixModifiers(LivingEntity entity, ItemStack stack) {
        Map<Attribute, List<AttributeModifier>> modifiers = ChaosModifier.getAttributeModifiers(stack);
        
        for (Map.Entry<Attribute, List<AttributeModifier>> entry : modifiers.entrySet()) {
            Attribute attribute = entry.getKey();
            
            try {
                var attributeRegistry = entity.level().registryAccess()
                    .registryOrThrow(net.minecraft.core.registries.Registries.ATTRIBUTE);
                var resourceKey = attributeRegistry.getResourceKey(attribute).orElse(null);
                
                if (resourceKey == null) continue;
                
                var attributeHolder = attributeRegistry.getHolderOrThrow(resourceKey);
                
                var attributeInstance = entity.getAttribute(attributeHolder);
                if (attributeInstance == null) continue;
                
                for (AttributeModifier modifier : entry.getValue()) {
                    // 创建新的修改器（使用原始ID）
                    AttributeModifier newModifier = new AttributeModifier(
                        modifier.id(),
                        modifier.amount(),
                        modifier.operation()
                    );
                    
                    // 添加修改器
                    attributeInstance.addTransientModifier(newModifier);
                }
            } catch (Exception e) {
                // 忽略无法解析的属性
            }
        }
    }
    
    /**
     * 移除混沌词缀的属性修改器
     */
    private static void removeChaosAffixModifiers(LivingEntity entity, ItemStack stack) {
        Map<Attribute, List<AttributeModifier>> modifiers = ChaosModifier.getAttributeModifiers(stack);
        
        for (Map.Entry<Attribute, List<AttributeModifier>> entry : modifiers.entrySet()) {
            Attribute attribute = entry.getKey();
            
            try {
                var attributeRegistry = entity.level().registryAccess()
                    .registryOrThrow(net.minecraft.core.registries.Registries.ATTRIBUTE);
                var resourceKey = attributeRegistry.getResourceKey(attribute).orElse(null);
                
                if (resourceKey == null) continue;
                
                var attributeHolder = attributeRegistry.getHolderOrThrow(resourceKey);
                
                var attributeInstance = entity.getAttribute(attributeHolder);
                if (attributeInstance == null) continue;
                
                for (AttributeModifier modifier : entry.getValue()) {
                    // 找到对应的修改器并移除
                    var existingModifier = attributeInstance.getModifier(modifier.id());
                    if (existingModifier != null) {
                        attributeInstance.removeModifier(existingModifier);
                    }
                }
            } catch (Exception e) {
                // 忽略无法解析的属性
            }
        }
    }
}
