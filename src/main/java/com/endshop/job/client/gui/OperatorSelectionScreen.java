package com.endshop.job.client.gui;

import com.endshop.job.EndshopJob;
import com.endshop.job.network.SummonOperatorPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * 干员选择界面 - 与职业选择界面风格一致
 */
public class OperatorSelectionScreen extends Screen {
    private final Screen parentScreen;
    
    // 可用干员列表
    private static final List<OperatorOption> AVAILABLE_OPERATORS = new ArrayList<>();
    
    static {
        AVAILABLE_OPERATORS.add(new OperatorOption("zhuang_fangyi", "庄方宜", "电磁突击"));
        AVAILABLE_OPERATORS.add(new OperatorOption("gerpeita", "洁尔佩塔", "辅助/重力"));
        AVAILABLE_OPERATORS.add(new OperatorOption("bieli", "别礼", "突击/寒冷"));
        AVAILABLE_OPERATORS.add(new OperatorOption("rosie", "洛茜", "近卫/物理"));
        AVAILABLE_OPERATORS.add(new OperatorOption("junwei", "骏卫", "先锋/物理"));
        AVAILABLE_OPERATORS.add(new OperatorOption("aidera", "艾尔黛拉", "辅助/自然"));
        AVAILABLE_OPERATORS.add(new OperatorOption("admin_a", "管理员A", "管理者"));
        AVAILABLE_OPERATORS.add(new OperatorOption("admin_b", "管理员B", "管理者(女)"));
        AVAILABLE_OPERATORS.add(new OperatorOption("levante", "莱万汀", "术师/灼热"));
        AVAILABLE_OPERATORS.add(new OperatorOption("tangtang", "汤汤", "先锋/物理"));
        AVAILABLE_OPERATORS.add(new OperatorOption("chen_qianyu", "陈千语", "近卫/物理"));
        AVAILABLE_OPERATORS.add(new OperatorOption("fluorite", "萤石", "狙击/物理"));
    }
    
    // 布局常量 - 与JobSelectionScreen保持一致
    private static final int ICON_SIZE = 40;
    private static final int COLS = 4;
    private static final int GAP = 12;
    private static final int TITLE_H = 40;
    private static final int FOOTER_H = 36;
    
    private int panelW, panelH, panelX, panelY;
    private final List<OperatorCard> cards = new ArrayList<>();
    
    public OperatorSelectionScreen(Screen parentScreen) {
        super(Component.literal("选择干员"));
        this.parentScreen = parentScreen;
    }
    
    @Override
    protected void init() {
        super.init();
        cards.clear();
        
        int rows = (int) Math.ceil((double) AVAILABLE_OPERATORS.size() / COLS);
        
        // 计算面板尺寸和位置（居中）
        panelW = COLS * ICON_SIZE + (COLS + 1) * GAP;
        panelH = TITLE_H + rows * (ICON_SIZE + GAP) + GAP + FOOTER_H;
        panelX = (this.width - panelW) / 2;
        panelY = (this.height - panelH) / 2;
        
        // 创建干员卡片
        for (int i = 0; i < AVAILABLE_OPERATORS.size(); i++) {
            final OperatorOption operator = AVAILABLE_OPERATORS.get(i);
            int col = i % COLS;
            int row = i / COLS;
            int cx = panelX + GAP + col * (ICON_SIZE + GAP);
            int cy = panelY + TITLE_H + row * (ICON_SIZE + GAP);
            
            cards.add(new OperatorCard(operator, cx, cy));
        }
    }
    
    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // 先渲染按钮等组件（避免覆盖文字）
        super.render(g, mouseX, mouseY, partialTick);
        
        // 渲染背景（模糊效果）
        this.renderBackground(g, mouseX, mouseY, partialTick);
        
        // 渲染面板背景
        g.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xE01A0E05);
        
        // 金色外框 - 3像素
        g.fill(panelX, panelY, panelX + panelW, panelY + 3, 0xFFD4A017);
        g.fill(panelX, panelY + panelH - 3, panelX + panelW, panelY + panelH, 0xFFD4A017);
        g.fill(panelX, panelY, panelX + 3, panelY + panelH, 0xFFD4A017);
        g.fill(panelX + panelW - 3, panelY, panelX + panelW, panelY + panelH, 0xFFD4A017);
        
        // 分隔线
        int lineY = panelY + panelH - FOOTER_H;
        g.fill(panelX + 3, lineY, panelX + panelW - 3, lineY + 2, 0x88D4A017);
        
        // 渲染干员卡片
        for (OperatorCard card : cards) {
            renderOperatorCard(g, card, mouseX, mouseY);
        }
        
        // 标题 - 最后渲染确保在最上层
        g.drawCenteredString(this.font, this.title, panelX + panelW / 2, panelY + 12, 0xFFEEDD88);
    }
    
    /**
     * 渲染单个干员卡片
     */
    private void renderOperatorCard(GuiGraphics g, OperatorCard card, int mx, int my) {
        boolean hovered = mx >= card.x && mx < card.x + ICON_SIZE
                       && my >= card.y && my < card.y + ICON_SIZE;
        
        // 绘制干员图标背景（半透明深色方块）
        g.fill(card.x, card.y, card.x + ICON_SIZE, card.y + ICON_SIZE, 0x88222222);
        
        // 渲染头像或首字母
        ResourceLocation avatarLocation = ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, 
                "textures/gui/operators/" + card.operator.id() + ".png");
        
        // 检查纹理是否存在
        boolean hasAvatar = false;
        try {
            var resource = this.minecraft.getResourceManager().getResource(avatarLocation);
            hasAvatar = resource.isPresent();
        } catch (Exception e) {
            hasAvatar = false;
        }
        
        if (hasAvatar) {
            // 渲染头像
            g.blit(avatarLocation, card.x + 2, card.y + 2, 0, 0, ICON_SIZE - 4, ICON_SIZE - 4, ICON_SIZE - 4, ICON_SIZE - 4);
        } else {
            // 显示干员名称首字母 - 提高亮度
            String firstChar = card.operator.name().substring(0, 1);
            g.drawCenteredString(this.font, firstChar, card.x + ICON_SIZE / 2, card.y + ICON_SIZE / 2 - 4, 0xFFEEDD88);
        }
        
        // 图标边框 - 天蓝色细线
        g.fill(card.x, card.y, card.x + ICON_SIZE, card.y + 1, 0x6687CEEB);
        g.fill(card.x, card.y + ICON_SIZE - 1, card.x + ICON_SIZE, card.y + ICON_SIZE, 0x6687CEEB);
        g.fill(card.x, card.y, card.x + 1, card.y + ICON_SIZE, 0x6687CEEB);
        g.fill(card.x + ICON_SIZE - 1, card.y, card.x + ICON_SIZE, card.y + ICON_SIZE, 0x6687CEEB);
        
        // 悬停效果：金色描边加粗到3像素，并添加背景高亮
        if (hovered) {
            // 高亮背景（半透明金色）
            g.fill(card.x + 1, card.y + 1, card.x + ICON_SIZE - 1, card.y + ICON_SIZE - 1, 0x33FFD700);
            // 金色外框 - 3像素宽
            g.fill(card.x - 3, card.y - 3, card.x + ICON_SIZE + 3, card.y, 0xFFFFD700);
            g.fill(card.x - 3, card.y + ICON_SIZE, card.x + ICON_SIZE + 3, card.y + ICON_SIZE + 3, 0xFFFFD700);
            g.fill(card.x - 3, card.y, card.x, card.y + ICON_SIZE, 0xFFFFD700);
            g.fill(card.x + ICON_SIZE, card.y, card.x + ICON_SIZE + 3, card.y + ICON_SIZE, 0xFFFFD700);
        }
        
        // 干员中文名 - 显示在图标下方 - 提高亮度
        g.drawCenteredString(this.font, card.operator.name(),
                card.x + ICON_SIZE / 2, card.y + ICON_SIZE + 6, 0xFFEEDD88);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 检查是否点击了干员卡片
        for (OperatorCard card : cards) {
            if (mouseX >= card.x && mouseX < card.x + ICON_SIZE
                && mouseY >= card.y && mouseY < card.y + ICON_SIZE) {
                // 召唤选中的干员
                PacketDistributor.sendToServer(new SummonOperatorPacket(card.operator.id()));
                
                // 关闭选择界面
                this.minecraft.setScreen(parentScreen);
                return true;
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public void onClose() {
        // 关闭时返回父界面
        this.minecraft.setScreen(parentScreen);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    /**
     * 干员选项数据类
     */
    private record OperatorOption(String id, String name, String description) {}
    
    /**
     * 干员卡片数据类
     */
    private record OperatorCard(OperatorOption operator, int x, int y) {}
}
