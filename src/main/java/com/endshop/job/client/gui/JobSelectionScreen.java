package com.endshop.job.client.gui;

import com.endshop.job.EndshopJob;
import com.endshop.job.client.OperatorListCache;
import com.endshop.job.data.JobDataAttachment;
import com.endshop.job.network.SetJobPacket;
import com.endshop.job.network.SummonOperatorPacket;
import com.endshop.job.operators.OperatorProfileManager;
import com.endshop.job.profession.Profession;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
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
    private static final int ICON_SIZE = 40;
    private static final int COLS    = 4;
    private static final int GAP     = 12;
    private static final int TITLE_H = 40;    // 增加标题区域高度
    private static final int FOOTER_H = 36;   // 增加底部区域高度,容纳按钮背景
    
    // 干员列表区域
    private static final int OPERATOR_LIST_WIDTH = 60;  // 干员列表宽度
    private static final int OPERATOR_SLOT_SIZE = 44;   // 干员槽位大小
    private static final int OPERATOR_GAP = 8;          // 干员槽位间距
    
    // 右侧属性面板
    private static final int INFO_PANEL_WIDTH = 90;     // 右侧属性面板宽度
    private static final int INFO_PANEL_GAP = 8;        // 与主面板的间距

    // 面板参数
    private int panelW, panelH, panelX, panelY;
    private final List<ProfessionCard> cards = new ArrayList<>();
    
    // 当前选中的干员（用于右侧面板展示）
    private String selectedOperatorId = null;

    public JobSelectionScreen() {
        super(Component.translatable("gui." + EndshopJob.MODID + ".job_selection.title"));
    }

    @Override
    protected void init() {
        super.init();
        cards.clear();

        Profession[] profs = Profession.selectableValues();
        int rows = (int) Math.ceil((double) profs.length / COLS);

        // 计算面板位置（考虑左侧干员列表 + 右侧信息面板）
        panelW = COLS * ICON_SIZE + (COLS + 1) * GAP;
        panelH = TITLE_H + rows * (ICON_SIZE + GAP) + GAP + FOOTER_H;
        
        // 面板居中（左: OPERATOR_LIST_WIDTH + GAP, 右: INFO_PANEL_GAP + INFO_PANEL_WIDTH）
        int totalWidth = OPERATOR_LIST_WIDTH + GAP + panelW + INFO_PANEL_GAP + INFO_PANEL_WIDTH;
        panelX = (this.width - totalWidth) / 2 + OPERATOR_LIST_WIDTH + GAP;
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
        int clearX = panelX + panelW - 65 - 8;
        int clearY = panelY + panelH - FOOTER_H + 9;
        Button clearBtn = Button.builder(
                Component.translatable("gui." + EndshopJob.MODID + ".job_selection.clear"),
                b -> selectJob(Profession.NONE)
        ).pos(clearX, clearY).size(65, 20).build();
        this.addRenderableWidget(clearBtn);

        // 属性按钮 - 左下
        int attrX = panelX + 8;
        int attrY = panelY + panelH - FOOTER_H + 9;
        Button attrBtn = Button.builder(
                Component.translatable("gui." + EndshopJob.MODID + ".attributes.button"),
                b -> {
                    this.onClose();
                    net.minecraft.client.Minecraft.getInstance().setScreen(new AttributeScreen());
                }
        ).pos(attrX, attrY).size(55, 20).build();
        this.addRenderableWidget(attrBtn);

        // 干员概览按钮 - 中下
        int overviewX = panelX + (panelW - 65) / 2;
        int overviewY = panelY + panelH - FOOTER_H + 9;
        Button overviewBtn = Button.builder(
                Component.translatable("gui." + EndshopJob.MODID + ".operator_overview.button"),
                b -> {
                    this.onClose();
                    net.minecraft.client.Minecraft.getInstance().setScreen(new OperatorOverviewScreen());
                }
        ).pos(overviewX, overviewY).size(65, 20).build();
        this.addRenderableWidget(overviewBtn);
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
        // 先渲染Minecraft背景（模糊效果）
        this.renderBackground(g, mx, my, pt);

        // 再渲染面板背景（在模糊层上面）
        g.fill(panelX,          panelY,          panelX + panelW, panelY + panelH, 0xE01A0E05);
        // 金色外框 - 加粗到3像素,更显眼
        g.fill(panelX,           panelY,          panelX + panelW, panelY + 3,      0xFFD4A017);
        g.fill(panelX,           panelY + panelH - 3, panelX + panelW, panelY + panelH, 0xFFD4A017);
        g.fill(panelX,           panelY,          panelX + 3,      panelY + panelH, 0xFFD4A017);
        g.fill(panelX + panelW - 3, panelY,      panelX + panelW, panelY + panelH, 0xFFD4A017);
        
        // 绘制内容区与按钮区的分隔线（在按钮之前绘制）
        int lineY = panelY + panelH - FOOTER_H;
        g.fill(panelX + 3, lineY, panelX + panelW - 3, lineY + 2, 0x88D4A017);
        
        // 渲染按钮层（在分隔线上）
        super.render(g, mx, my, pt);

        // 渲染职业卡片（贴图作为按钮）- 确保在最上层
        for (ProfessionCard card : cards) {
            renderJobCard(g, card, mx, my);
        }

        // 最后渲染干员列表（在面板之后，确保不被遮挡）
        renderOperatorList(g, mx, my);
        
        // 渲染右侧属性信息面板
        renderInfoPanel(g, mx, my);
        
        // 最后渲染标题 - 确保在最最上层
        g.drawCenteredString(this.font, this.title,
                this.width / 2, panelY + 12, 0xFFFFCC44);
    }

    /** 渲染单个职业卡片 - 贴图本身作为可点击区域 */
    private void renderJobCard(GuiGraphics g, ProfessionCard card, int mx, int my) {
        boolean hovered = mx >= card.x && mx < card.x + ICON_SIZE
                       && my >= card.y && my < card.y + ICON_SIZE;

        // 绘制职业图标背景（半透明深色方块,让图标更突出）
        g.fill(card.x, card.y, card.x + ICON_SIZE, card.y + ICON_SIZE, 0x88222222);
        
        // 绘制职业图标（PNG）
        ResourceLocation iconLocation = getJobIconLocation(card.profession);
        renderIcon(g, iconLocation, card.x + 2, card.y + 2, ICON_SIZE - 4, ICON_SIZE - 4);
        
        // 图标边框 - 天蓝色细线（降低透明度）
        g.fill(card.x, card.y, card.x + ICON_SIZE, card.y + 1, 0x6687CEEB);
        g.fill(card.x, card.y + ICON_SIZE - 1, card.x + ICON_SIZE, card.y + ICON_SIZE, 0x6687CEEB);
        g.fill(card.x, card.y, card.x + 1, card.y + ICON_SIZE, 0x6687CEEB);
        g.fill(card.x + ICON_SIZE - 1, card.y, card.x + ICON_SIZE, card.y + ICON_SIZE, 0x6687CEEB);

        // 悬停效果：金色描边加粗到3像素,并添加背景高亮
        if (hovered) {
            // 高亮背景（半透明金色）
            g.fill(card.x + 1, card.y + 1, card.x + ICON_SIZE - 1, card.y + ICON_SIZE - 1, 0x33FFD700);
            // 金色外框 - 3像素宽
            g.fill(card.x - 3, card.y - 3, card.x + ICON_SIZE + 3, card.y, 0xFFFFD700);
            g.fill(card.x - 3, card.y + ICON_SIZE, card.x + ICON_SIZE + 3, card.y + ICON_SIZE + 3, 0xFFFFD700);
            g.fill(card.x - 3, card.y, card.x, card.y + ICON_SIZE, 0xFFFFD700);
            g.fill(card.x + ICON_SIZE, card.y, card.x + ICON_SIZE + 3, card.y + ICON_SIZE, 0xFFFFD700);
        }

        // 职业中文名（彩色）- 显示在图标下方,增加间距避免拥挤感
        g.drawCenteredString(this.font, card.profession.getDisplayName(),
                card.x + ICON_SIZE / 2, card.y + ICON_SIZE + 6, getTextColor(card.profession));
    }

    /** 渲染图标 - 使用GuiGraphics内置方法 */
    private void renderIcon(GuiGraphics g, ResourceLocation texture, int x, int y, int width, int height) {
        g.blit(texture, x, y, 0, 0, width, height, width, height);
    }

    /** 获取职业图标的ResourceLocation */
    private ResourceLocation getJobIconLocation(Profession profession) {
        return ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, "textures/gui/jobs/" + profession.name().toLowerCase() + ".png");
    }

    /** 根据职业返回文本颜色（明日方舟八大职业） */
    private int getTextColor(Profession p) {
        return switch (p) {
            case VANGUARD   -> 0xFF55FF55;  // 先锋 - 绿色
            case GUARD      -> 0xFFFF5555;  // 近卫 - 红色
            case DEFENDER   -> 0xFFFFFF55;  // 重装 - 黄色
            case SNIPER     -> 0xFF55FFFF;  // 狙击 - 青色
            case SPECIALIST -> 0xFFAA55FF;  // 特种 - 紫色
            case MEDIC      -> 0xFFFFFFFF;  // 医疗 - 白色
            case CASTER     -> 0xFF5555FF;  // 术师 - 蓝色
            case SUPPORTER  -> 0xFFAAAAAA;  // 辅助 - 灰色
            default         -> 0xFFFFFFFF;  // 默认白色
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
        
        // 检查是否点击了干员列表的槽位（包括+号和已召唤干员）
        if (handleOperatorListClick(mouseX, mouseY)) {
            return true;
        }
        
        // 检查是否点击了右侧信息面板（点击面板打开详情）
        if (selectedOperatorId != null) {
            int infoX = panelX + panelW + INFO_PANEL_GAP;
            int infoY = panelY;
            if (mouseX >= infoX && mouseX < infoX + INFO_PANEL_WIDTH
                && mouseY >= infoY && mouseY < infoY + panelH) {
                // 打开干员详情页
                this.minecraft.setScreen(new OperatorDetailScreen(selectedOperatorId));
                return true;
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    /**
     * 渲染干员列表（左侧）
     */
    private void renderOperatorList(GuiGraphics g, int mx, int my) {
        // 将干员列表向左移动：从屏幕左边距30像素开始
        int listX = 40;
        int listY = panelY;
        int listH = panelH;
        
        // 绘制背景
        g.fill(listX, listY, listX + OPERATOR_LIST_WIDTH, listY + listH, 0xE01A0E05);
        // 金色边框
        g.fill(listX, listY, listX + OPERATOR_LIST_WIDTH, listY + 3, 0xFFD4A017);
        g.fill(listX, listY + listH - 3, listX + OPERATOR_LIST_WIDTH, listY + listH, 0xFFD4A017);
        g.fill(listX, listY, listX + 3, listY + listH, 0xFFD4A017);
        g.fill(listX + OPERATOR_LIST_WIDTH - 3, listY, listX + OPERATOR_LIST_WIDTH, listY + listH, 0xFFD4A017);
        
        // 标题
        g.drawCenteredString(this.font, "干员", listX + OPERATOR_LIST_WIDTH / 2, listY + 8, 0xFFFFCC44);
        
        // 分隔线
        g.fill(listX + 4, listY + 20, listX + OPERATOR_LIST_WIDTH - 4, listY + 22, 0x88D4A017);
        
        // 渲染干员槽位
        List<String> operatorIds = OperatorListCache.getOperatorIds();
        int slotStartY = listY + 28;
        
        for (int i = 0; i < OperatorListCache.getMaxOperators(); i++) {
            int slotY = slotStartY + i * (OPERATOR_SLOT_SIZE + OPERATOR_GAP);
            
            if (i < operatorIds.size()) {
                // 已召唤的干员 - 显示图标
                String operatorId = operatorIds.get(i);
                renderOperatorSlot(g, operatorId, listX, slotY, mx, my, false);
            } else {
                // 空槽位 - 显示+号
                renderOperatorSlot(g, null, listX, slotY, mx, my, true);
            }
        }
        
        // 底部显示数量
        String countText = operatorIds.size() + "/" + OperatorListCache.getMaxOperators();
        g.drawCenteredString(this.font, countText, listX + OPERATOR_LIST_WIDTH / 2, 
                listY + listH - 18, 0xFFAAAAAA);
    }
    
    /**
     * 渲染单个干员槽位
     */
    private void renderOperatorSlot(GuiGraphics g, String operatorId, int listX, int slotY, 
                                     int mx, int my, boolean isEmpty) {
        int slotX = listX + 8;
        int slotSize = OPERATOR_SLOT_SIZE - 16; // 44 - 16 = 28
        
        boolean hovered = mx >= slotX && mx < slotX + slotSize
                       && my >= slotY && my < slotY + slotSize;
        
        // 背景
        g.fill(slotX, slotY, slotX + slotSize, slotY + slotSize, 0x88222222);
        
        // 边框
        g.fill(slotX, slotY, slotX + slotSize, slotY + 1, 0x6687CEEB);
        g.fill(slotX, slotY + slotSize - 1, slotX + slotSize, slotY + slotSize, 0x6687CEEB);
        g.fill(slotX, slotY, slotX + 1, slotY + slotSize, 0x6687CEEB);
        g.fill(slotX + slotSize - 1, slotY, slotX + slotSize, slotY + slotSize, 0x6687CEEB);
        
        if (isEmpty) {
            // 空槽位 - 直接在槽位中央绘制+号，无需矩阵变换
            int plusColor = OperatorListCache.isFull() ? 0xFF666666 : 0xFF55FF55;
            g.drawCenteredString(this.font, "+", slotX + slotSize / 2, slotY + slotSize / 2 - 4, plusColor);
            
            // 悬停效果
            if (hovered && !OperatorListCache.isFull()) {
                g.fill(slotX + 1, slotY + 1, slotX + slotSize - 1, slotY + slotSize - 1, 0x3355FF55);
            }
        } else {
            // 已召唤干员 - 显示头像
            ResourceLocation avatarLocation = getOperatorAvatarLocation(operatorId);
            renderOperatorAvatar(g, avatarLocation, slotX, slotY, slotSize);
            
            // 悬停效果
            if (hovered) {
                g.fill(slotX + 1, slotY + 1, slotX + slotSize - 1, slotY + slotSize - 1, 0x33FFD700);
            }
        }
    }
    
    /**
     * 获取干员名称缩写（保留备用）
     */
    private String getOperatorShortName(String operatorId) {
        return switch (operatorId) {
            case "yvon" -> "冯";
            case "perica" -> "佩";
            case "surtr" -> "史";
            case "gerpeita" -> "洁";
            case "rosie" -> "茜";
            case "admin_b" -> "B";
            case "chen_qianyu" -> "千";
            case "bieli" -> "礼";
            case "etera" -> "埃";
            case "zhuang_fangyi" -> "庄";
            case "tangtang" -> "汤";
            case "qiuli" -> "栗";
            case "admin_a" -> "A";
            case "yingshi" -> "萤";
            case "saixi" -> "希";
            case "junwei" -> "卫";
            case "aidera" -> "黛";
            default -> "?";
        };
    }
    
    /**
     * 获取干员头像的ResourceLocation
     */
    private ResourceLocation getOperatorAvatarLocation(String operatorId) {
        return ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, 
                "textures/gui/operators/" + operatorId + ".png");
    }
    
    /**
     * 渲染干员头像
     */
    private void renderOperatorAvatar(GuiGraphics g, ResourceLocation texture, int x, int y, int size) {
        try {
            // 使用blit渲染头像图片
            g.blit(texture, x, y, 0, 0, size, size, size, size);
        } catch (Exception e) {
            // 如果头像加载失败，显示名称缩写作为后备
            String shortName = getOperatorShortName(texture.getPath().split("/")[2].replace(".png", ""));
            g.drawCenteredString(this.font, shortName, x + size / 2, y + size / 2 - 4, 0xFFFFCC44);
        }
    }
    
    /**
     * 处理干员列表点击
     */
    private boolean handleOperatorListClick(double mouseX, double mouseY) {
        // 与renderOperatorList保持一致
        int listX = 30;
        int slotStartY = panelY + 28;
        
        List<String> operatorIds = OperatorListCache.getOperatorIds();
        
        for (int i = 0; i < OperatorListCache.getMaxOperators(); i++) {
            int slotY = slotStartY + i * (OPERATOR_SLOT_SIZE + OPERATOR_GAP);
            int slotX = listX + 8;
            int slotSize = OPERATOR_SLOT_SIZE - 16;
            
            if (mouseX >= slotX && mouseX < slotX + slotSize
                && mouseY >= slotY && mouseY < slotY + slotSize) {
                
                if (i < operatorIds.size()) {
                    // 点击已召唤干员：切换选中，在右侧显示属性
                    String opId = operatorIds.get(i);
                    selectedOperatorId = opId.equals(selectedOperatorId) ? null : opId;
                    return true;
                } else if (!OperatorListCache.isFull()) {
                    // 点击空槽位：弹出干员选择界面
                    showOperatorSelectionDialog();
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 渲染右侧属性信息面板
     * 当有选中干员时显示其角色名、生命值、攻击力等信息；
     * 否则显示当前玩家（自身）的状态
     */
    private void renderInfoPanel(GuiGraphics g, int mx, int my) {
        // 面板位置：主面板右侧
        int infoX = panelX + panelW + INFO_PANEL_GAP;
        int infoY = panelY;
        int infoH = panelH;
        int infoW = INFO_PANEL_WIDTH;
        
        // 面板背景
        g.fill(infoX, infoY, infoX + infoW, infoY + infoH, 0xE01A0E05);
        // 金色边框
        g.fill(infoX,            infoY,             infoX + infoW, infoY + 3,       0xFFD4A017);
        g.fill(infoX,            infoY + infoH - 3, infoX + infoW, infoY + infoH,   0xFFD4A017);
        g.fill(infoX,            infoY,             infoX + 3,     infoY + infoH,   0xFFD4A017);
        g.fill(infoX + infoW - 3, infoY,            infoX + infoW, infoY + infoH,   0xFFD4A017);
        
        // 标题
        g.drawCenteredString(this.font, "信息", infoX + infoW / 2, infoY + 8, 0xFFFFCC44);
        // 分隔线
        g.fill(infoX + 4, infoY + 20, infoX + infoW - 4, infoY + 22, 0x88D4A017);
        
        int cy = infoY + 28;
        
        if (selectedOperatorId != null) {
            // ── 显示选中干员的档案信息 ──
            // 头像（大图）
            int avatarSize = infoW - 16;
            int avatarX = infoX + 8;
            renderOperatorAvatar(g, getOperatorAvatarLocation(selectedOperatorId), avatarX, cy, avatarSize);
            cy += avatarSize + 6;
            
            // 角色名
            String name = OperatorProfileManager.getOperatorName(selectedOperatorId);
            g.drawCenteredString(this.font, name, infoX + infoW / 2, cy, 0xFFFFCC44);
            cy += 12;
            
            // 分隔线
            g.fill(infoX + 4, cy, infoX + infoW - 4, cy + 1, 0x66D4A017);
            cy += 6;
            
            // 从档案读取 stats
            JsonObject profile = OperatorProfileManager.getProfile(selectedOperatorId);
            if (profile != null && profile.has("stats")) {
                JsonObject stats = profile.getAsJsonObject("stats");
                int hp  = stats.has("base_health")  ? stats.get("base_health").getAsInt()  : 0;
                int atk = stats.has("base_attack")  ? stats.get("base_attack").getAsInt()  : 0;
                int def = stats.has("base_defense") ? stats.get("base_defense").getAsInt() : 0;
                
                cy = renderInfoRow(g, infoX, cy, infoW, "❤", String.valueOf(hp),  0xFFFF5555);
                cy = renderInfoRow(g, infoX, cy, infoW, "⚔", String.valueOf(atk), 0xFFFFAA00);
                cy = renderInfoRow(g, infoX, cy, infoW, "🛡", String.valueOf(def), 0xFF55AAFF);
            }
            
            // 职业
            cy += 4;
            String prof = OperatorProfileManager.getOperatorProfession(selectedOperatorId);
            if (!"UNKNOWN".equals(prof)) {
                g.drawCenteredString(this.font, "[" + prof + "]", infoX + infoW / 2, cy, 0xFF55FF55);
                cy += 12;
            }
            
            // 提示：点击可查看详情
            cy += 4;
            g.fill(infoX + 4, cy, infoX + infoW - 4, cy + 1, 0x44D4A017);
            cy += 6;
            renderSmallText(g, infoX, cy, infoW, "点击查看", 0xFF888888);
            
        } else {
            // ── 无选中干员时，显示玩家自身信息 ──
            var player = Minecraft.getInstance().player;
            if (player != null) {
                // 玩家名
                String playerName = player.getName().getString();
                // 名字可能很长，截断处理
                if (this.font.width(playerName) > infoW - 10) {
                    playerName = this.font.plainSubstrByWidth(playerName, infoW - 10) + "..";
                }
                g.drawCenteredString(this.font, playerName, infoX + infoW / 2, cy, 0xFFFFCC44);
                cy += 14;
                
                // 分隔线
                g.fill(infoX + 4, cy, infoX + infoW - 4, cy + 1, 0x66D4A017);
                cy += 6;
                
                // 生命值
                float hp    = player.getHealth();
                float maxHp = player.getMaxHealth();
                cy = renderInfoRow(g, infoX, cy, infoW, "❤", String.format("%.0f/%.0f", hp, maxHp), 0xFFFF5555);
                
                // 血条
                int barW = infoW - 16;
                int barX = infoX + 8;
                g.fill(barX, cy, barX + barW, cy + 4, 0x66333333);
                int fillW = (int) (barW * (hp / maxHp));
                g.fill(barX, cy, barX + fillW, cy + 4, 0xAAFF4444);
                cy += 8;
                
                // 攻击力（从 Minecraft 属性系统获取）
                double atk = player.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
                cy = renderInfoRow(g, infoX, cy, infoW, "⚔", String.format("%.1f", atk), 0xFFFFAA00);
                
                // 职业
                cy += 4;
                com.endshop.job.profession.Profession job = com.endshop.job.data.JobDataAttachment.getJob(player);
                if (job != com.endshop.job.profession.Profession.NONE) {
                    g.drawCenteredString(this.font, "[" + job.getDisplayName() + "]",
                            infoX + infoW / 2, cy, 0xFF55FF55);
                    cy += 12;
                }
            }
            
            // 提示
            cy = infoY + infoH - 28;
            g.fill(infoX + 4, cy, infoX + infoW - 4, cy + 1, 0x44D4A017);
            cy += 6;
            renderSmallText(g, infoX, cy, infoW, "点击干员", 0xFF666666);
            renderSmallText(g, infoX, cy + 10, infoW, "查看属性", 0xFF666666);
        }
    }
    
    /**
     * 渲染一行信息：图标 + 数值，返回下一行的 Y
     */
    private int renderInfoRow(GuiGraphics g, int panelX, int y, int panelW, String icon, String value, int valueColor) {
        int cx = panelX + 8;
        g.drawString(this.font, icon, cx, y, 0xFFFFFFFF);
        // 数值右对齐
        int vw = this.font.width(value);
        g.drawString(this.font, value, panelX + panelW - 8 - vw, y, valueColor);
        return y + 12;
    }
    
    /**
     * 居中绘制小号提示文字
     */
    private void renderSmallText(GuiGraphics g, int panelX, int y, int panelW, String text, int color) {
        g.drawCenteredString(this.font, text, panelX + panelW / 2, y, color);
    }
    
    /**
     * 显示干员选择对话框
     */
    private void showOperatorSelectionDialog() {
        // 打开干员选择界面
        this.minecraft.setScreen(new OperatorSelectionScreen(this));
    }

    @Override
    public boolean isPauseScreen() { return false; }

    private record ProfessionCard(Profession profession, int x, int y) {}
}
