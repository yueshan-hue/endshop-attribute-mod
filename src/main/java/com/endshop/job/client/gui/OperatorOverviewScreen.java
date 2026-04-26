package com.endshop.job.client.gui;

import com.endshop.job.EndshopJob;
import com.endshop.job.operators.*;
import com.endshop.job.profession.Profession;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 干员概览界面 - 显示所有可召唤的干员列表
 */
public class OperatorOverviewScreen extends Screen {

    // 布局常量
    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 280;
    private static final int SCROLL_AREA_HEIGHT = 180;
    private static final int OPERATOR_HEIGHT = 35;
    private static final int OPERATOR_GAP = 8;
    private static final int TITLE_HEIGHT = 30;
    private static final int FOOTER_HEIGHT = 40;
    private static final int SCROLLBAR_WIDTH = 12;

    private int panelX, panelY;
    private int scrollOffset = 0;
    private final List<OperatorInfo> operators = new ArrayList<>();
    private boolean isDragging = false;
    private int lastMouseY = 0;

    public OperatorOverviewScreen() {
        super(Component.translatable("gui.endshopattribute.operator_overview.title"));
        loadOperators();
    }

    private void loadOperators() {
        operators.clear();
        
        // 近卫
        operators.add(new OperatorInfo("伊冯", "Yvon", "yvon", Profession.GUARD, "擅长物理伤害与控制"));
        operators.add(new OperatorInfo("佩丽卡", "Perlica", "perlica", Profession.GUARD, "高效的多面手近卫"));
        operators.add(new OperatorInfo("史尔特尔", "Surtr", "surtr", Profession.GUARD, "强大的法术近卫"));
        operators.add(new OperatorInfo("洁尔佩塔", "Gerpeita", "gerpeita", Profession.GUARD, "均衡的近卫干员"));
        operators.add(new OperatorInfo("洛茜", "Rosie", "rosie", Profession.GUARD, "灵活的近卫干员"));
        operators.add(new OperatorInfo("别礼", "Bieli", "bieli", Profession.GUARD, "独特的近卫干员"));
        operators.add(new OperatorInfo("庄方宜", "ZhuangFangyi", "zhuang_fangyi", Profession.GUARD, "科研型近卫天师"));
        
        // 重装
        operators.add(new OperatorInfo("管理员B", "AdminB", "admin_b", Profession.DEFENDER, "强力的重装干员"));
        
        // 术师
        operators.add(new OperatorInfo("陈千语", "ChenQianyu", "chen_qianyu", Profession.CASTER, "源石技艺专家"));
        
        // 辅助
        operators.add(new OperatorInfo("萤石", "Fluorite", "fluorite", Profession.SUPPORTER, "支援型辅助干员"));
        operators.add(new OperatorInfo("赛希", "Sehy", "sehy", Profession.SUPPORTER, "战术辅助干员"));
        
        // 特种
        operators.add(new OperatorInfo("汤汤", "Tangtang", "tangtang", Profession.SPECIALIST, "多功能特种干员"));
        operators.add(new OperatorInfo("管理员A", "AdminA", "admin_a", Profession.SPECIALIST, "特殊能力干员"));
        
        // 先锋
        operators.add(new OperatorInfo("埃特拉", "Etra", "etra", Profession.VANGUARD, "快速部署先锋"));
    }

    @Override
    protected void init() {
        super.init();

        panelX = (this.width - PANEL_WIDTH) / 2;
        panelY = (this.height - PANEL_HEIGHT) / 2;

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
        g.drawCenteredString(this.font, this.title,
                this.width / 2, panelY + 10, 0xFFFFCC44);

        // 渲染按钮层
        super.render(g, mx, my, pt);

        // 渲染干员列表
        renderOperatorList(g, mx, my);
    }

    private void renderOperatorList(GuiGraphics g, int mx, int my) {
        int listX = panelX + 15;
        int listY = panelY + TITLE_HEIGHT + 10;
        int listWidth = PANEL_WIDTH - 30 - SCROLLBAR_WIDTH;

        // 列表背景 - 更透明
        g.fill(listX, listY, listX + listWidth, listY + SCROLL_AREA_HEIGHT, 0x33000000);

        // 渲染干员
        int maxY = Math.min(operators.size(), (SCROLL_AREA_HEIGHT / (OPERATOR_HEIGHT + OPERATOR_GAP)));
        for (int i = 0; i < maxY; i++) {
            int index = i + scrollOffset;
            if (index >= operators.size()) break;

            OperatorInfo op = operators.get(index);
            int yPos = listY + i * (OPERATOR_HEIGHT + OPERATOR_GAP);

            // 干员卡片背景 - 更透明,悬停时金色
            boolean hovered = mx >= listX && mx < listX + listWidth &&
                            my >= yPos && my < yPos + OPERATOR_HEIGHT;
            g.fill(listX, yPos, listX + listWidth, yPos + OPERATOR_HEIGHT,
                    hovered ? 0x44FFD700 : 0x22000000);
            
            // 添加卡片边框 - 天蓝色
            int borderColor = hovered ? 0xFF87CEEB : 0x4487CEEB;
            g.fill(listX, yPos, listX + listWidth, yPos + 1, borderColor); // 上边框
            g.fill(listX, yPos + OPERATOR_HEIGHT - 1, listX + listWidth, yPos + OPERATOR_HEIGHT, borderColor); // 下边框
            g.fill(listX, yPos, listX + 1, yPos + OPERATOR_HEIGHT, borderColor); // 左边框
            g.fill(listX + listWidth - 1, yPos, listX + listWidth, yPos + OPERATOR_HEIGHT, borderColor); // 右边框

            // 干员信息
            int textColor = getProfessionColor(op.profession);
            g.drawString(this.font, op.name, listX + 5, yPos + 5, textColor);
            g.drawString(this.font, op.englishName, listX + 5, yPos + 17, 0xAAAAAA);
            g.drawString(this.font, op.description, listX + 100, yPos + 11, 0xCCCCCC);

            // 职业标签 - 深色背景与边框
            String profName = op.profession.getDisplayName();
            int profWidth = this.font.width(profName);
            int profX = listX + listWidth - profWidth - 10;
            int profY = yPos + 10;
            int profH = 12;
            
            // 标签背景 - 深色半透明
            g.fill(profX, profY, profX + profWidth + 6, profY + profH, 0xAA222222);
            // 标签边框 - 天蓝色
            g.fill(profX, profY, profX + profWidth + 6, profY + 1, 0x8887CEEB);
            g.fill(profX, profY + profH - 1, profX + profWidth + 6, profY + profH, 0x8887CEEB);
            g.fill(profX, profY, profX + 1, profY + profH, 0x8887CEEB);
            g.fill(profX + profWidth + 5, profY, profX + profWidth + 6, profY + profH, 0x8887CEEB);
            
            // 使用职业颜色文字
            g.drawString(this.font, profName, profX + 3, profY + 2, getProfessionColor(op.profession));
        }

        // 滚动条
        if (operators.size() > maxY) {
            int scrollbarX = listX + listWidth + 5;
            int scrollbarHeight = SCROLL_AREA_HEIGHT;
            int thumbHeight = Math.max(30, scrollbarHeight * maxY / operators.size());
            int thumbY = listY + (scrollOffset * (scrollbarHeight - thumbHeight) / (operators.size() - maxY));

            g.fill(scrollbarX, listY, scrollbarX + SCROLLBAR_WIDTH, listY + scrollbarHeight, 0x44000000);
            g.fill(scrollbarX, thumbY, scrollbarX + SCROLLBAR_WIDTH, thumbY + thumbHeight, 0xFFD4A017);
        }

        // 统计信息
        String stats = String.format("共 %d 位干员", operators.size());
        g.drawString(this.font, stats, listX, listY + SCROLL_AREA_HEIGHT + 10, 0xAAAAAA);
    }

    private int getProfessionColor(Profession profession) {
        return switch (profession) {
            case VANGUARD -> 0x55FF55;
            case GUARD -> 0xFF5555;
            case DEFENDER -> 0xFFFF55;
            case SNIPER -> 0x55FFFF;
            case SPECIALIST -> 0xAA55FF;
            case MEDIC -> 0xFFFFFF;
            case CASTER -> 0x5555FF;
            case SUPPORTER -> 0xAAAAAA;
            default -> 0xFFFFFF;
        };
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int maxY = Math.min(operators.size(), (SCROLL_AREA_HEIGHT / (OPERATOR_HEIGHT + OPERATOR_GAP)));
        int maxScroll = Math.max(0, operators.size() - maxY);
        
        if (scrollY < 0) {
            scrollOffset = Math.min(scrollOffset + 1, maxScroll);
        } else {
            scrollOffset = Math.max(scrollOffset - 1, 0);
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 检查是否点击了干员卡片
        int listX = panelX + 15;
        int listY = panelY + TITLE_HEIGHT + 10;
        int listWidth = PANEL_WIDTH - 30 - SCROLLBAR_WIDTH;
        int maxY = Math.min(operators.size(), (SCROLL_AREA_HEIGHT / (OPERATOR_HEIGHT + OPERATOR_GAP)));
        
        for (int i = 0; i < maxY; i++) {
            int index = i + scrollOffset;
            if (index >= operators.size()) break;
            
            int yPos = listY + i * (OPERATOR_HEIGHT + OPERATOR_GAP);
            if (mouseX >= listX && mouseX < listX + listWidth &&
                mouseY >= yPos && mouseY < yPos + OPERATOR_HEIGHT) {
                // 点击了干员，打开详情界面
                OperatorInfo op = operators.get(index);
                this.onClose();
                net.minecraft.client.Minecraft.getInstance().setScreen(new OperatorDetailScreen(op.id));
                return true;
            }
        }
        
        // 检查滚动条拖拽
        if (mouseX >= panelX + PANEL_WIDTH - 17 && mouseX <= panelX + PANEL_WIDTH - 5 &&
            mouseY >= panelY + TITLE_HEIGHT + 10 && mouseY <= panelY + TITLE_HEIGHT + 10 + SCROLL_AREA_HEIGHT) {
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
        if (isDragging) {
            int deltaY = (int) mouseY - lastMouseY;
            int maxY = Math.min(operators.size(), (SCROLL_AREA_HEIGHT / (OPERATOR_HEIGHT + OPERATOR_GAP)));
            int maxScroll = Math.max(0, operators.size() - maxY);
            
            scrollOffset = Math.max(0, Math.min(scrollOffset - deltaY / 5, maxScroll));
            lastMouseY = (int) mouseY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record OperatorInfo(String name, String englishName, String id, Profession profession, String description) {
    }
}
