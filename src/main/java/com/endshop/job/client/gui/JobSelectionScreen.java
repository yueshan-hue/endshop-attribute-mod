package com.endshop.job.client.gui;

import com.endshop.job.EndshopJob;
import com.endshop.job.data.JobDataAttachment;
import com.endshop.job.network.SetJobPacket;
import com.endshop.job.profession.Profession;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * 职业选择界面 - 使用PNG职业图标作为可点击按钮
 */
public class JobSelectionScreen extends Screen {

    // ── 布局常量 ──────────────────────────────────────────
    // 新贴图尺寸约 48x48，稍微放大显示
    private static final int ICON_SIZE = 48;
    private static final int COLS    = 4;
    private static final int GAP     = 16;
    private static final int TITLE_H = 32;
    private static final int FOOTER_H = 32;

    // 面板参数
    private int panelW, panelH, panelX, panelY;
    private final List<ProfessionCard> cards = new ArrayList<>();

    public JobSelectionScreen() {
        super(Component.translatable("gui." + EndshopJob.MODID + ".job_selection.title"));
    }

    @Override
    protected void init() {
        super.init();
        cards.clear();

        Profession[] profs = Profession.selectableValues();
        int rows = (int) Math.ceil((double) profs.length / COLS);

        panelW = COLS * ICON_SIZE + (COLS + 1) * GAP;
        panelH = TITLE_H + rows * (ICON_SIZE + GAP) + GAP + FOOTER_H;
        panelX = (this.width  - panelW) / 2;
        panelY = (this.height - panelH) / 2;

        for (int i = 0; i < profs.length; i++) {
            final Profession prof = profs[i];
            int col = i % COLS;
            int row = i / COLS;
            int cx  = panelX + GAP + col * (ICON_SIZE + GAP);
            int cy  = panelY + TITLE_H + row * (ICON_SIZE + GAP);

            cards.add(new ProfessionCard(prof, cx, cy));
        }

        // 清除职业按钮 - 右下
        int clearX = panelX + panelW - 90 - 10;
        int clearY = panelY + panelH - FOOTER_H + 4;
        Button clearBtn = Button.builder(
                Component.translatable("gui." + EndshopJob.MODID + ".job_selection.clear"),
                b -> selectJob(Profession.NONE)
        ).pos(clearX, clearY).size(90, 20).build();
        this.addRenderableWidget(clearBtn);

        // 属性按钮 - 左下
        int attrX = panelX + 10;
        int attrY = panelY + panelH - FOOTER_H + 4;
        Button attrBtn = Button.builder(
                Component.translatable("gui." + EndshopJob.MODID + ".attributes.button"),
                b -> {
                    this.onClose();
                    net.minecraft.client.Minecraft.getInstance().setScreen(new AttributeScreen());
                }
        ).pos(attrX, attrY).size(50, 20).build();
        this.addRenderableWidget(attrBtn);
    }

    private void selectJob(Profession profession) {
        // 先更新客户端数据（立即显示）
        var player = net.minecraft.client.Minecraft.getInstance().player;
        if (player != null) {
            JobDataAttachment.setJob(player, profession);
        }
        
        // 再发送到服务端（同步到服务器）
        PacketDistributor.sendToServer(new SetJobPacket(profession.name()));
        this.onClose();
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        this.renderBackground(g, mx, my, pt);

        // 面板背景
        g.fill(panelX,          panelY,          panelX + panelW, panelY + panelH, 0xCC1A0E05);
        // 金色外框
        g.fill(panelX,           panelY,          panelX + panelW, panelY + 2,      0xFFD4A017);
        g.fill(panelX,           panelY + panelH - 2, panelX + panelW, panelY + panelH, 0xFFD4A017);
        g.fill(panelX,           panelY,          panelX + 2,      panelY + panelH, 0xFFD4A017);
        g.fill(panelX + panelW - 2, panelY,      panelX + panelW, panelY + panelH, 0xFFD4A017);

        // 先渲染按钮层（清除职业按钮）
        super.render(g, mx, my, pt);

        // 再渲染职业卡片（贴图作为按钮）- 确保在最上层
        for (ProfessionCard card : cards) {
            renderJobCard(g, card, mx, my);
        }

        // 最后渲染标题 - 确保在最最上层
        g.drawCenteredString(this.font, this.title,
                this.width / 2, panelY + 9, 0xFFFFCC44);
    }

    /** 渲染单个职业卡片 - 贴图本身作为可点击区域 */
    private void renderJobCard(GuiGraphics g, ProfessionCard card, int mx, int my) {
        boolean hovered = mx >= card.x && mx < card.x + ICON_SIZE
                       && my >= card.y && my < card.y + ICON_SIZE;

        // 绘制职业图标（PNG）
        ResourceLocation iconLocation = getJobIconLocation(card.profession);
        renderIcon(g, iconLocation, card.x, card.y, ICON_SIZE, ICON_SIZE);

        // 悬停效果：金色描边
        if (hovered) {
            g.fill(card.x - 2, card.y - 2, card.x + ICON_SIZE + 2, card.y, 0xFFFFD700);
            g.fill(card.x - 2, card.y + ICON_SIZE, card.x + ICON_SIZE + 2, card.y + ICON_SIZE + 2, 0xFFFFD700);
            g.fill(card.x - 2, card.y, card.x, card.y + ICON_SIZE, 0xFFFFD700);
            g.fill(card.x + ICON_SIZE, card.y, card.x + ICON_SIZE + 2, card.y + ICON_SIZE, 0xFFFFD700);
        }

        // 职业中文名（彩色）- 显示在图标下方
        g.drawCenteredString(this.font, card.profession.getDisplayName(),
                card.x + ICON_SIZE / 2, card.y + ICON_SIZE + 4, getTextColor(card.profession));
    }

    /** 渲染图标 - 使用GuiGraphics内置方法 */
    private void renderIcon(GuiGraphics g, ResourceLocation texture, int x, int y, int width, int height) {
        g.blit(texture, x, y, 0, 0, width, height, width, height);
    }

    /** 获取职业图标的ResourceLocation */
    private ResourceLocation getJobIconLocation(Profession profession) {
        return ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, "textures/gui/jobs/" + profession.name().toLowerCase() + ".png");
    }

    /** 根据职业返回文本颜色 */
    private int getTextColor(Profession p) {
        return switch (p) {
            case VANGUARD   -> 0xFF55FF55;
            case GUARD      -> 0xFFFF5555;
            case DEFENDER   -> 0xFFFFFF55;
            case SNIPER     -> 0xFF55FFFF;
            case SPECIALIST -> 0xFFAA55FF;
            case MEDIC      -> 0xFFFFFFFF;
            case CASTER     -> 0xFF5555FF;
            case SUPPORTER  -> 0xFFAAAAAA;
            default         -> 0xFFFFFFFF;
        };
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 检查是否点击了职业图标
        for (ProfessionCard card : cards) {
            if (mouseX >= card.x && mouseX < card.x + ICON_SIZE
                && mouseY >= card.y && mouseY < card.y + ICON_SIZE) {
                selectJob(card.profession);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() { return false; }

    private record ProfessionCard(Profession profession, int x, int y) {}
}
