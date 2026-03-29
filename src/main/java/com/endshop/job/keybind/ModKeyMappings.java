package com.endshop.job.keybind;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

/**
 * 技能按键绑定 - 定义 Q/E/R/F 四个技能键
 */
@EventBusSubscriber(modid = "endshopattribute", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModKeyMappings {
    
    public static final String CATEGORY = "key.categories.endshopattribute.skills";
    
    // 四个技能按键
    public static KeyMapping KEY_SKILL_SLOT_1; // Q 键
    public static KeyMapping KEY_SKILL_SLOT_2; // E 键
    public static KeyMapping KEY_SKILL_SLOT_3; // R 键
    public static KeyMapping KEY_SKILL_SLOT_4; // F 键
    
    /**
     * 注册按键绑定
     */
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        KEY_SKILL_SLOT_1 = new KeyMapping(
            "key.endshopattribute.skill_slot_1",
            GLFW.GLFW_KEY_Q,
            CATEGORY
        );
        
        KEY_SKILL_SLOT_2 = new KeyMapping(
            "key.endshopattribute.skill_slot_2",
            GLFW.GLFW_KEY_E,
            CATEGORY
        );
        
        KEY_SKILL_SLOT_3 = new KeyMapping(
            "key.endshopattribute.skill_slot_3",
            GLFW.GLFW_KEY_R,
            CATEGORY
        );
        
        KEY_SKILL_SLOT_4 = new KeyMapping(
            "key.endshopattribute.skill_slot_4",
            GLFW.GLFW_KEY_F,
            CATEGORY
        );
        
        event.register(KEY_SKILL_SLOT_1);
        event.register(KEY_SKILL_SLOT_2);
        event.register(KEY_SKILL_SLOT_3);
        event.register(KEY_SKILL_SLOT_4);
    }
    
    /**
     * 客户端 tick 事件 - 监听按键按下
     */
    @EventBusSubscriber(modid = "endshopattribute", value = Dist.CLIENT)
    public static class KeyInputHandler {
        
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            // 检查各个技能键是否被按下
            while (KEY_SKILL_SLOT_1.consumeClick()) {
                SkillExecutor.useSkill(0);
            }
            
            while (KEY_SKILL_SLOT_2.consumeClick()) {
                SkillExecutor.useSkill(1);
            }
            
            while (KEY_SKILL_SLOT_3.consumeClick()) {
                SkillExecutor.useSkill(2);
            }
            
            while (KEY_SKILL_SLOT_4.consumeClick()) {
                SkillExecutor.useSkill(3);
            }
        }
    }
}
