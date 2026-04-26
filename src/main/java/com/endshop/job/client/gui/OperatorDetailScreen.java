package com.endshop.job.client.gui;

import com.endshop.job.EndshopJob;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.endshop.job.operators.OperatorProfileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 干员详情界面 - 显示干员简介和生平档案
 */
public class OperatorDetailScreen extends Screen {

    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 280;
    private static final int TITLE_HEIGHT = 40;
    private static final int FOOTER_HEIGHT = 40;
    private static final int SCROLLBAR_WIDTH = 12;

    private final String operatorId;
    private String operatorName = "";
    private String description = "";
    private String race = ""; // 种族
    private String affiliation = ""; // 所属
    private String birthday = ""; // 生日
    private String gender = ""; // 性别
    private int baseHealth = 0; // 基础生命值
    private int baseAttack = 0; // 基础攻击力
    private int baseDefense = 0; // 基础防御力
    private final List<String> archives = new ArrayList<>();
    
    private int panelX, panelY;
    private int scrollOffset = 0;
    private int maxScroll = 0;
    private boolean isDragging = false;
    private int lastMouseY = 0;
    private int contentHeight = 0;

    public OperatorDetailScreen(String operatorId) {
        super(Component.literal(""));
        this.operatorId = operatorId;
        loadOperatorData();
    }

    private void loadOperatorData() {
        try {
            // 直接从类路径读取JSON文件
            String resourcePath = "/data/endshopattribute/operators/" + operatorId + ".json";
            java.net.URL url = OperatorDetailScreen.class.getResource(resourcePath);
            
            System.out.println("尝试加载干员档案: " + resourcePath + ", URL: " + url);
            
            if (url != null) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    
                    if (json.has("name")) {
                        operatorName = json.get("name").getAsString();
                    }
                    if (json.has("description")) {
                        description = json.get("description").getAsString();
                    }
                    
                    // 加载profile信息
                    if (json.has("profile")) {
                        JsonObject profile = json.getAsJsonObject("profile");
                        if (profile.has("race")) race = profile.get("race").getAsString();
                        if (profile.has("affiliation")) affiliation = profile.get("affiliation").getAsString();
                        if (profile.has("birthday")) birthday = profile.get("birthday").getAsString();
                        if (profile.has("gender")) gender = profile.get("gender").getAsString();
                    }
                    
                    // 加载stats信息
                    if (json.has("stats")) {
                        JsonObject stats = json.getAsJsonObject("stats");
                        if (stats.has("base_health")) baseHealth = stats.get("base_health").getAsInt();
                        if (stats.has("base_attack")) baseAttack = stats.get("base_attack").getAsInt();
                        if (stats.has("base_defense")) baseDefense = stats.get("base_defense").getAsInt();
                    }
                    
                    // 加载档案资料
                    for (int i = 1; i <= 4; i++) {
                        String key = "archive_" + i;
                        if (json.has(key)) {
                            archives.add(json.get(key).getAsString());
                            System.out.println("加载档案" + i + ": " + archives.get(archives.size() - 1).substring(0, Math.min(50, archives.get(archives.size() - 1).length())));
                        }
                    }
                    
                    // 如果没有档案，加载人事简述
                    if (archives.isEmpty() && json.has("personnel_summary")) {
                        archives.add(json.get("personnel_summary").getAsString());
                        System.out.println("加载人事简述");
                    }
                    
                    System.out.println("成功加载干员: " + operatorName + ", 档案数量: " + archives.size());
                }
            } else {
                // 如果 JSON 不存在，使用默认值
                operatorName = operatorId;
                description = "暂无简介";
                System.err.println("未找到干员档案: " + resourcePath);
            }
        } catch (Exception e) {
            System.err.println("加载干员档案失败: " + operatorId);
            e.printStackTrace();
            operatorName = operatorId;
            description = "档案加载失败";
        }
    }

    @Override
    protected void init() {
        super.init();

        panelX = (this.width - PANEL_WIDTH) / 2;
        panelY = (this.height - PANEL_HEIGHT) / 2;

        // 计算内容高度
        contentHeight = 80; // 简介区域
        for (String archive : archives) {
            contentHeight += 30 + this.font.wordWrapHeight(Component.literal(archive), PANEL_WIDTH - 40);
        }
        maxScroll = Math.max(0, contentHeight - (PANEL_HEIGHT - TITLE_HEIGHT - FOOTER_HEIGHT - 20));

        // 返回按钮
        int backX = panelX + PANEL_WIDTH - 90 - 10;
        int backY = panelY + PANEL_HEIGHT - FOOTER_HEIGHT + 10;
        Button backBtn = Button.builder(
                Component.literal("← 返回"),
                b -> this.onClose()
        ).pos(backX, backY).size(90, 20).build();
        this.addRenderableWidget(backBtn);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        this.renderBackground(g, mx, my, pt);

        // 面板背景
        g.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xCC1A0E05);
        
        // 金色边框
        g.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + 2, 0xFFD4A017);
        g.fill(panelX, panelY + PANEL_HEIGHT - 2, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xFFD4A017);
        g.fill(panelX, panelY, panelX + 2, panelY + PANEL_HEIGHT, 0xFFD4A017);
        g.fill(panelX + PANEL_WIDTH - 2, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xFFD4A017);

        // 标题
        g.drawCenteredString(this.font, operatorName + " - 档案",
                this.width / 2, panelY + 12, 0xFFFFCC44);
        g.drawCenteredString(this.font, operatorId,
                this.width / 2, panelY + 24, 0xAAAAAA);

        // 渲染按钮
        super.render(g, mx, my, pt);

        // 渲染内容
        renderContent(g, mx, my);
    }

    private void renderContent(GuiGraphics g, int mx, int my) {
        int contentX = panelX + 20;
        int contentY = panelY + TITLE_HEIGHT + 10;
        int contentWidth = PANEL_WIDTH - 40 - SCROLLBAR_WIDTH;
        int contentHeightLimit = PANEL_HEIGHT - TITLE_HEIGHT - FOOTER_HEIGHT - 20;

        // 内容背景
        g.fill(contentX, contentY, contentX + contentWidth, contentY + contentHeightLimit, 0x66000000);

        g.enableScissor(contentX, contentY, contentX + contentWidth, contentY + contentHeightLimit);

        int currentY = contentY - scrollOffset;

        // 基础信息
        g.drawString(this.font, "【基础信息】", contentX + 5, currentY, 0xFFD4A017);
        currentY += 15;
        
        // 显示基础信息
        if (!gender.isEmpty()) {
            g.drawString(this.font, "性别: " + gender, contentX + 5, currentY, 0xEEEEEE);
            currentY += 12;
        }
        if (!race.isEmpty()) {
            g.drawString(this.font, "种族: " + race, contentX + 5, currentY, 0xEEEEEE);
            currentY += 12;
        }
        if (!affiliation.isEmpty()) {
            g.drawString(this.font, "所属: " + affiliation, contentX + 5, currentY, 0xEEEEEE);
            currentY += 12;
        }
        if (!birthday.isEmpty()) {
            g.drawString(this.font, "生日: " + birthday, contentX + 5, currentY, 0xEEEEEE);
            currentY += 12;
        }
        if (baseHealth > 0) {
            g.drawString(this.font, "基础生命值: " + baseHealth, contentX + 5, currentY, 0xEEEEEE);
            currentY += 12;
        }
        if (baseAttack > 0) {
            g.drawString(this.font, "基础攻击力: " + baseAttack, contentX + 5, currentY, 0xEEEEEE);
            currentY += 12;
        }
        if (baseDefense > 0) {
            g.drawString(this.font, "基础防御力: " + baseDefense, contentX + 5, currentY, 0xEEEEEE);
            currentY += 12;
        }
        currentY += 10;

        // 简介
        g.drawString(this.font, "【干员简介】", contentX + 5, currentY, 0xFFD4A017);
        currentY += 15;
        var descLines = this.font.split(Component.literal(description), contentWidth - 10);
        for (var line : descLines) {
            g.drawString(this.font, line, contentX + 5, currentY, 0xEEEEEE);
            currentY += 12;
        }
        currentY += 10;

        // 档案资料
        for (int i = 0; i < archives.size(); i++) {
            g.drawString(this.font, "【档案资料·" + (i + 1) + "】", contentX + 5, currentY, 0xFFD4A017);
            currentY += 15;
            var lines = this.font.split(Component.literal(archives.get(i)), contentWidth - 10);
            for (var line : lines) {
                g.drawString(this.font, line, contentX + 5, currentY, 0xDDDDDD);
                currentY += 12;
            }
            currentY += 15;
        }

        g.disableScissor();

        // 滚动条
        if (maxScroll > 0) {
            int scrollbarX = contentX + contentWidth + 5;
            int thumbHeight = Math.max(30, contentHeightLimit * contentHeightLimit / contentHeight);
            int thumbY = contentY + (scrollOffset * (contentHeightLimit - thumbHeight) / maxScroll);

            g.fill(scrollbarX, contentY, scrollbarX + SCROLLBAR_WIDTH, contentY + contentHeightLimit, 0x44000000);
            g.fill(scrollbarX, thumbY, scrollbarX + SCROLLBAR_WIDTH, thumbY + thumbHeight, 0xFFD4A017);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY < 0) {
            scrollOffset = Math.min(scrollOffset + 15, maxScroll);
        } else {
            scrollOffset = Math.max(scrollOffset - 15, 0);
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 检测滚动条拖拽
        int contentX = panelX + 20;
        int contentY = panelY + TITLE_HEIGHT + 10;
        int contentWidth = PANEL_WIDTH - 40 - SCROLLBAR_WIDTH;
        int contentHeightLimit = PANEL_HEIGHT - TITLE_HEIGHT - FOOTER_HEIGHT - 20;
        int scrollbarX = contentX + contentWidth + 5;

        if (mouseX >= scrollbarX && mouseX <= scrollbarX + SCROLLBAR_WIDTH &&
            mouseY >= contentY && mouseY <= contentY + contentHeightLimit) {
            isDragging = true;
            lastMouseY = (int) mouseY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDragging && maxScroll > 0) {
            int deltaY = (int) mouseY - lastMouseY;
            int contentHeightLimit = PANEL_HEIGHT - TITLE_HEIGHT - FOOTER_HEIGHT - 20;
            scrollOffset = Math.max(0, Math.min(scrollOffset + deltaY, maxScroll));
            lastMouseY = (int) mouseY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
