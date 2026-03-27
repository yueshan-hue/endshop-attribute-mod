package com.endshop.job.client.gui;

import com.endshop.job.skill.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceLocation;

/**
 * 技能装备界面 - 管理四个技能槽位
 */
public class SkillEquipScreen extends Screen {
    
    private static final ResourceLocation BACKGROUND_LOCATION = 
            ResourceLocation.fromNamespaceAndPath("endshopattribute", "textures/gui/skill_equipment.png");
    
    private final SkillDataAttachment.SkillData skillData;
    
    // 界面尺寸
    private int imageWidth = 256;
    private int imageHeight = 166;
    private int leftPos;
    private int topPos;
    
    // 技能槽位按钮
    private Button[] slotButtons;
    
    public SkillEquipScreen() {
        super(Component.translatable("gui.endshopattribute.skill.equipment"));
        this.skillData = SkillDataAttachment.getSkillData(Minecraft.getInstance().player);
    }
    
    @Override
    protected void init() {
        super.init();
        
        // 计算居中位置
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        
        // 初始化槽位按钮数组
        this.slotButtons = new Button[4];
        
        // 为每个技能槽位创建按钮
        for (int i = 0; i < 4; i++) {
            final int slotIndex = i;
            int buttonX = this.leftPos + 8 + (i % 2) * 60; // 每行 2 个
            int buttonY = this.topPos + 20 + (i / 2) * 60; // 2 行
            
            String skillId = skillData.getEquippedSkill(slotIndex);
            Component buttonText;
            ResourceLocation iconLocation = null;
            
            if (skillId != null) {
                Skill skill = SkillRegistry.getSkill(skillId);
                buttonText = Component.literal(SkillEquipHelper.getSlotKeyBinding(slotIndex))
                        .append("\n")
                        .append(skill.getName());
                iconLocation = skill.getIconLocation();
            } else {
                buttonText = Component.literal(SkillEquipHelper.getSlotKeyBinding(slotIndex))
                        .append("\n")
                        .append(Component.translatable("gui.endshopattribute.skill.no_skill"));
            }
            
            // 创建带图标的按钮
            final ResourceLocation finalIconLocation = iconLocation;
            slotButtons[i] = new Button(
                    buttonX, buttonY, 50, 50,
                    buttonText,
                    btn -> onSlotClicked(slotIndex),
                    (msg) -> Component.empty()
            ) {
                @Override
                public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                    super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
                    
                    // 如果有图标，渲染在按钮上
                    if (finalIconLocation != null) {
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                        guiGraphics.blit(finalIconLocation, getX() + 2, getY() + 2, 0, 0, 46, 46, 46, 46);
                    }
                }
            };
            
            addRenderableWidget(slotButtons[i]);
        }
        
        // 关闭按钮
        addRenderableWidget(Button.builder(Component.literal("关闭"), btn -> onClose())
                .bounds(this.leftPos + this.imageWidth / 2 - 40, this.topPos + this.imageHeight - 30, 80, 20)
                .build());
    }
    
    /**
     * 槽位被点击时打开技能选择界面
     */
    private void onSlotClicked(int slotIndex) {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new SkillSelectionScreen(this, slotIndex));
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 渲染背景
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        
        // 绘制背景纹理
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(BACKGROUND_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        
        // 绘制标题
        guiGraphics.drawString(
            this.font, 
            this.title.getString(), 
            this.leftPos + this.imageWidth / 2 - this.font.width(this.title.getString()) / 2,
            this.topPos + 6,
            0x404040,
            false
        );
        
        // 绘制槽位标签
        for (int i = 0; i < 4; i++) {
            int slotX = this.leftPos + 8 + (i % 2) * 60;
            int slotY = this.topPos + 20 + (i / 2) * 60;
            
            // 槽位名称
            Component slotName = SkillEquipHelper.getSlotName(i);
            guiGraphics.drawString(
                this.font,
                slotName.getString(),
                slotX + 25 - this.font.width(slotName.getString()) / 2,
                slotY - 10,
                0xFFFFFF,
                false
            );
        }
        
        // 调用父类的渲染（渲染按钮等）
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    /**
     * 从技能选择界面返回时更新显示
     */
    public void updateFromSelection() {
        // 更新所有槽位按钮的显示
        for (int i = 0; i < 4; i++) {
            String skillId = skillData.getEquippedSkill(i);
            
            if (skillId != null) {
                Skill skill = SkillRegistry.getSkill(skillId);
                slotButtons[i].setMessage(
                    Component.literal(SkillEquipHelper.getSlotKeyBinding(i))
                            .append("\n")
                            .append(skill.getName())
                );
            } else {
                slotButtons[i].setMessage(
                    Component.literal(SkillEquipHelper.getSlotKeyBinding(i))
                            .append("\n")
                            .append(Component.translatable("gui.endshopattribute.skill.no_skill"))
                );
            }
        }
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
