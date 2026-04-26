package com.endshop.job.client.gui;

import com.endshop.job.EndshopJob;
import com.endshop.job.tool.ToolManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/**
 * 工具召唤轮盘界面 - 8个槽位的圆形菜单
 */
public class ToolWheelScreen extends Screen {
    
    private static final int SLOT_COUNT = 8;
    private static final double RADIUS = 100; // 轮盘半径
    private static final double SLOT_SIZE = 40; // 槽位大小
    
    private int selectedSlot = 0; // 当前选中的槽位 (0-7)
    private long lastScrollTime = 0;
    private static final long SCROLL_COOLDOWN = 150; // 滚轮冷却时间(ms)
    
    // 槽位图标路径（可以根据需要配置）
    private static final String[] SLOT_ICONS = {
        "jet_drone",      // 0: 射流无人机
        "empty",          // 1: 空
        "empty",          // 2: 空
        "empty",          // 3: 空
        "empty",          // 4: 空
        "empty",          // 5: 空
        "empty",          // 6: 空
        "empty"           // 7: 空
    };
    
    public ToolWheelScreen() {
        super(Component.literal("工具召唤轮盘"));
        com.endshop.job.EndshopJob.LOGGER.info("[终末地职业] ToolWheelScreen 构造函数被调用");
    }
    
    @Override
    protected void init() {
        super.init();
        com.endshop.job.EndshopJob.LOGGER.info("[终末地职业] ToolWheelScreen init 被调用, minecraft={}, width={}, height={}", minecraft, this.width, this.height);
        
        // 暂时注释掉鼠标捕获，测试是否是这个导致的问题
        // if (minecraft != null) {
        //     minecraft.mouseHandler.grabMouse();
        // }
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        long currentTime = System.currentTimeMillis();
        
        // 检查冷却时间
        if (currentTime - lastScrollTime < SCROLL_COOLDOWN) {
            return true;
        }
        
        // 根据滚动方向切换选中槽位
        if (scrollY > 0) {
            // 向上滚动 - 逆时针
            selectedSlot = (selectedSlot - 1 + SLOT_COUNT) % SLOT_COUNT;
        } else if (scrollY < 0) {
            // 向下滚动 - 顺时针
            selectedSlot = (selectedSlot + 1) % SLOT_COUNT;
        }
        
        lastScrollTime = currentTime;
        return true;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 按 ESC 关闭界面
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return true;
        }
        
        // 按数字键 1-8 直接选择对应槽位
        if (keyCode >= GLFW.GLFW_KEY_1 && keyCode <= GLFW.GLFW_KEY_8) {
            selectedSlot = keyCode - GLFW.GLFW_KEY_1;
            return true;
        }
        
        // 按空格或回车确认选择
        if (keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_ENTER) {
            confirmSelection();
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    /**
     * 确认选择并执行对应功能
     */
    private void confirmSelection() {
        // 检查冷却时间
        if (ToolManager.isOnCooldown()) {
            long remaining = ToolManager.getRemainingCooldown();
            if (minecraft.player != null) {
                minecraft.player.sendSystemMessage(
                    Component.literal("§e召唤工具正在冷却中，还需 §c" + remaining + "§e 秒")
                );
            }
            return;
        }
        
        switch (selectedSlot) {
            case 0:
                // 召唤射流无人机
                ToolManager.summonJetDrone();
                ToolManager.recordSummon();
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                // 空槽位，显示提示
                if (minecraft.player != null) {
                    minecraft.player.sendSystemMessage(
                        Component.literal("§e该槽位尚未配置工具")
                    );
                }
                break;
        }
        
        // 关闭界面
        onClose();
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 先调用父类渲染（确保渲染状态正确）
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        if (minecraft == null) return;
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        // 绘制半透明深色背景（不模糊游戏世界）
        int bgAlpha = 0x60; // 较低的透明度，保持背景可见
        guiGraphics.fill(0, 0, this.width, this.height, (bgAlpha << 24) | 0x000000);
        
        // 绘制标题（更醒目）
        guiGraphics.drawCenteredString(
            minecraft.font,
            "§l§e工具召唤轮盘",
            centerX,
            centerY - 160,
            0xFFFFFF
        );
        
        // 绘制提示文字
        guiGraphics.drawCenteredString(
            minecraft.font,
            "§7滚轮选择 · §f空格确认 · §7ESC取消",
            centerX,
            centerY + 160,
            0xAAAAAA
        );
        
        // 绘制8个槽位
        for (int i = 0; i < SLOT_COUNT; i++) {
            double angle = Math.toRadians(i * 45 - 90); // 从顶部开始，每个槽位间隔45度
            double x = centerX + Math.cos(angle) * RADIUS;
            double y = centerY + Math.sin(angle) * RADIUS;
            
            boolean isSelected = (i == selectedSlot);
            
            if (isSelected) {
                // 选中槽位：发光效果
                // 外层光晕
                guiGraphics.fill(
                    (int)x - 28, (int)y - 28,
                    (int)x + 28, (int)y + 28,
                    0x4055FF55
                );
                // 边框
                guiGraphics.fill(
                    (int)x - 25, (int)y - 25,
                    (int)x + 25, (int)y + 25,
                    0xFF55FF55
                );
                // 内部背景
                guiGraphics.fill(
                    (int)x - 23, (int)y - 23,
                    (int)x + 23, (int)y + 23,
                    0xCC333333
                );
            } else {
                // 未选中槽位：普通样式
                // 边框
                guiGraphics.fill(
                    (int)x - 22, (int)y - 22,
                    (int)x + 22, (int)y + 22,
                    0xFF666666
                );
                // 内部背景
                guiGraphics.fill(
                    (int)x - 20, (int)y - 20,
                    (int)x + 20, (int)y + 20,
                    0xAA222222
                );
            }
            
            // 绘制槽位编号（左上角小字）
            String slotNumber = String.valueOf(i + 1);
            guiGraphics.drawString(
                minecraft.font,
                slotNumber,
                (int)x - 16,
                (int)y - 18,
                isSelected ? 0xFFFF55 : 0x888888
            );
            
            // 绘制槽位名称（中央大字）
            String slotName = getSlotName(i);
            guiGraphics.drawCenteredString(
                minecraft.font,
                isSelected ? "§e§l" + slotName : "§7" + slotName,
                (int)x,
                (int)y - 3,
                0xFFFFFF
            );
        }
        
        // 绘制中心圆点（装饰）
        guiGraphics.fill(
            centerX - 8, centerY - 8,
            centerX + 8, centerY + 8,
            0xAAFFFFFF
        );
        guiGraphics.fill(
            centerX - 6, centerY - 6,
            centerX + 6, centerY + 6,
            0xFF555555
        );
        
        // 绘制当前选中物品的详细信息（底部）
        String selectedToolName = getSlotName(selectedSlot);
        guiGraphics.drawCenteredString(
            minecraft.font,
            "§e§l▶ " + selectedToolName + " ◀",
            centerX,
            centerY + 130,
            0xFFFFFF
        );
    }
    
    /**
     * 获取槽位名称
     */
    private String getSlotName(int slot) {
        switch (slot) {
            case 0: return "无人机";
            case 1: return "空";
            case 2: return "空";
            case 3: return "空";
            case 4: return "空";
            case 5: return "空";
            case 6: return "空";
            case 7: return "空";
            default: return "";
        }
    }
    
    /**
     * 绘制圆形
     */
    private void drawCircle(GuiGraphics guiGraphics, int centerX, int centerY, int radius, int fillColor, int borderColor) {
        // 绘制填充圆形（简化为矩形，实际可以使用更复杂的绘制）
        RenderSystem.enableBlend();
        
        // 外边框
        guiGraphics.fill(
            centerX - radius - 2,
            centerY - radius - 2,
            centerX + radius + 2,
            centerY + radius + 2,
            borderColor
        );
        
        // 内部填充
        guiGraphics.fill(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius,
            fillColor
        );
        
        RenderSystem.disableBlend();
    }
    
    @Override
    public boolean isPauseScreen() {
        return false; // 不暂停游戏
    }
    
    @Override
    public void onClose() {
        if (minecraft != null) {
            minecraft.mouseHandler.releaseMouse();
            minecraft.setScreen(null);
        }
        super.onClose();
    }
}
