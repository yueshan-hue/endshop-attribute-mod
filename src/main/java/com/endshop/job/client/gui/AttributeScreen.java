package com.endshop.job.client.gui;

import com.endshop.job.EndshopJob;
import com.endshop.job.data.AttributeDataAttachment;
import com.endshop.job.data.JobDataAttachment;
import com.endshop.job.profession.Profession;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * 属性查看界面 - 显示智识、力量、敏捷、意志
 */
public class AttributeScreen extends Screen {

    // 布局常量
    private static final int PANEL_WIDTH = 200;
    private static final int PANEL_HEIGHT = 220;
    private static final int ATTR_HEIGHT = 30;
    private static final int ATTR_GAP = 8;
    private static final int BORDER_WIDTH = 2;
    private static final int FOOTER_SPACE = 50; // 底部预留空间（总属性 + 关闭按钮）
    private static final int TITLE_SPACE = 40; // 标题和职业信息区域高度

    private int panelX, panelY;

    public AttributeScreen() {
        super(Component.translatable("gui." + EndshopJob.MODID + ".attributes.title"));
    }

    @Override
    protected void init() {
        super.init();

        panelX = (this.width - PANEL_WIDTH) / 2;
        panelY = (this.height - PANEL_HEIGHT) / 2;

        // 关闭按钮 - 最底部居中
        int closeX = panelX + (PANEL_WIDTH - 80) / 2;
        int closeY = panelY + PANEL_HEIGHT - 25;
        Button closeBtn = Button.builder(
                Component.translatable("gui." + EndshopJob.MODID + ".attributes.close"),
                b -> this.onClose()
        ).pos(closeX, closeY).size(80, 20).build();
        this.addRenderableWidget(closeBtn);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        // 渲染背景
        super.render(g, mouseX, mouseY, delta);

        // 渲染金色边框
        int borderColor = 0xFFFFD700; // 金色
        g.fill(panelX - BORDER_WIDTH, panelY - BORDER_WIDTH, 
               panelX + PANEL_WIDTH + BORDER_WIDTH, 
               panelY + PANEL_HEIGHT + BORDER_WIDTH, 
               borderColor);

        // 渲染面板
        g.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0x80000000);
        g.fill(panelX + 1, panelY + 1, panelX + PANEL_WIDTH - 1, panelY + PANEL_HEIGHT - 1, 0x80333333);

        // 渲染标题
        g.drawCenteredString(this.font,
                this.title.getString(),
                this.width / 2, panelY + 10, 0xFFFFAA00);

        // 渲染玩家职业和等级（顶部居中）
        var player = Minecraft.getInstance().player;
        if (player != null) {
            Profession job = JobDataAttachment.getJob(player);
            int jobLevel = JobDataAttachment.getJobLevel(player);
                    
            // 职业名称和等级（顶部居中）
            g.drawCenteredString(this.font, "职业：" + job.getDisplayName(), 
                        this.width / 2, panelY + 25, 0xFFFFFF00);
            g.drawCenteredString(this.font, "等级：" + jobLevel, 
                        this.width / 2, panelY + 40, 0xFFFFFF00);
        }
        
        // 渲染属性 - 从上到下依次排列（智识、力量、敏捷、意志）
        var attrs = AttributeDataAttachment.getAttributes(Minecraft.getInstance().player);
        int startY = panelY + TITLE_SPACE + 10; // 属性起始 Y 坐标（在职业信息下方）
        
        // 智识（第一个，最上面）
        renderAttribute(g, "gui." + EndshopJob.MODID + ".attributes.wisdom",
                attrs.wisdom(), 0xFF5555FF, startY + (ATTR_HEIGHT + ATTR_GAP) * 0);
        
        // 力量（第二个）
        renderAttribute(g, "gui." + EndshopJob.MODID + ".attributes.strength",
                attrs.strength(), 0xFFFF5555, startY + (ATTR_HEIGHT + ATTR_GAP) * 1);
        
        // 敏捷（第三个）
        renderAttribute(g, "gui." + EndshopJob.MODID + ".attributes.agility",
                attrs.agility(), 0xFF55FF55, startY + (ATTR_HEIGHT + ATTR_GAP) * 2);
        
        // 意志（第四个，最下面）
        renderAttribute(g, "gui." + EndshopJob.MODID + ".attributes.willpower",
                attrs.willpower(), 0xFFFFAA00, startY + (ATTR_HEIGHT + ATTR_GAP) * 3);
        
        // 总属性 - 在属性下方，关闭按钮上方
        int totalY = panelY + PANEL_HEIGHT - FOOTER_SPACE + 5;
        g.drawCenteredString(this.font,
                Component.translatable("gui." + EndshopJob.MODID + ".attributes.total", attrs.getTotal()),
                this.width / 2, totalY, 0xFFFFFFFF);
    }

    /**
     * 渲染单个属性
     */
    private void renderAttribute(GuiGraphics g, String key, int value, int color, int y) {
        String attrName = Component.translatable(key).getString();
        g.drawString(this.font, attrName, panelX + 20, y + 5, 0xFFFFFFFF);
        g.drawString(this.font, String.valueOf(value), panelX + PANEL_WIDTH - 30, y + 5, color);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}