package com.endshop.job.keybind;

import com.endshop.job.client.gui.SkillEquipScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

/**
 * 技能按键绑定 - 定义小键盘1/2/3/4四个技能键
 */
@EventBusSubscriber(modid = "endshopattribute", value = Dist.CLIENT)
public class ModKeyMappings {
    
    public static final String CATEGORY = "key.categories.endshopattribute.skills";
    
    // 四个技能按键
    public static KeyMapping KEY_SKILL_SLOT_1; // 小键盘 1
    public static KeyMapping KEY_SKILL_SLOT_2; // 小键盘 2
    public static KeyMapping KEY_SKILL_SLOT_3; // 小键盘 3
    public static KeyMapping KEY_SKILL_SLOT_4; // 小键盘 4
    
    // 打开技能装配界面
    public static KeyMapping KEY_OPEN_SKILL_SCREEN; // B 键
    
    // 打开工具召唤轮盘
    public static KeyMapping KEY_SUMMON_TOOL; // V 键
    
    /**
     * 注册按键绑定
     */
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        KEY_SKILL_SLOT_1 = new KeyMapping(
            "key.endshopattribute.skill_slot_1",
            GLFW.GLFW_KEY_KP_1,
            CATEGORY
        );
        
        KEY_SKILL_SLOT_2 = new KeyMapping(
            "key.endshopattribute.skill_slot_2",
            GLFW.GLFW_KEY_KP_2,
            CATEGORY
        );
        
        KEY_SKILL_SLOT_3 = new KeyMapping(
            "key.endshopattribute.skill_slot_3",
            GLFW.GLFW_KEY_KP_3,
            CATEGORY
        );
        
        KEY_SKILL_SLOT_4 = new KeyMapping(
            "key.endshopattribute.skill_slot_4",
            GLFW.GLFW_KEY_KP_4,
            CATEGORY
        );
        
        // 注册打开技能装配界面的按键（默认为 B 键）
        KEY_OPEN_SKILL_SCREEN = new KeyMapping(
            "key.endshopattribute.open_skill_screen",
            GLFW.GLFW_KEY_B,
            CATEGORY
        );
        
        // 注册打开工具召唤轮盘的按键（默认为 V 键）
        KEY_SUMMON_TOOL = new KeyMapping(
            "key.endshopattribute.summon_tool",
            GLFW.GLFW_KEY_V,
            CATEGORY
        );
        
        event.register(KEY_SKILL_SLOT_1);
        event.register(KEY_SKILL_SLOT_2);
        event.register(KEY_SKILL_SLOT_3);
        event.register(KEY_SKILL_SLOT_4);
        event.register(KEY_OPEN_SKILL_SCREEN);
        event.register(KEY_SUMMON_TOOL);
    }
    
    /**
     * 客户端 tick 事件 - 监听按键按下
     */
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
        
        // 检查是否按下打开技能装配界面的按键
        while (KEY_OPEN_SKILL_SCREEN.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.screen == null) {
                // 打开技能装配界面
                mc.setScreen(new SkillEquipScreen());
            }
        }
        
        // 检查是否按下打开工具召唤轮盘的按键
        if (KEY_SUMMON_TOOL.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            com.endshop.job.EndshopJob.LOGGER.info("[终末地职业] V键被按下！screen={}, player={}, isLocal={}", 
                mc.screen, mc.player, mc.isLocalServer());
            if (mc.player != null && mc.screen == null) {
                com.endshop.job.EndshopJob.LOGGER.info("[终末地职业] 打开工具召唤轮盘");
                // 打开工具召唤轮盘
                com.endshop.job.client.gui.ToolWheelScreen wheelScreen = new com.endshop.job.client.gui.ToolWheelScreen();
                mc.setScreen(wheelScreen);
                com.endshop.job.EndshopJob.LOGGER.info("[终末地职业] 设置界面后 screen={}", mc.screen);
            } else {
                com.endshop.job.EndshopJob.LOGGER.info("[终末地职业] 条件不满足 - player={}, screen={}", mc.player, mc.screen);
            }
        }
    }
}
