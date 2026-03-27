package com.endshop.job.client.gui;

import com.endshop.job.data.JobDataAttachment;
import com.endshop.job.profession.Profession;
import com.endshop.job.skill.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 技能选择界面 - 选择要装备的技能
 */
public class SkillSelectionScreen extends Screen {
    
    private final SkillEquipScreen parent;
    private final int targetSlot;
    private final List<String> availableSkills;
    
    // 职业与技能的映射关系
    private static final Map<Profession, List<String>> PROFESSION_SKILLS = new HashMap<>();
    
    static {
        // 初始化各职业可使用的技能
        PROFESSION_SKILLS.put(Profession.MEDIC, List.of("quick_heal")); // 医疗职业 - 快速治疗
        PROFESSION_SKILLS.put(Profession.GUARD, List.of("strength_boost")); // 近卫 - 力量强化
        PROFESSION_SKILLS.put(Profession.VANGUARD, List.of("quick_heal", "strength_boost")); // 先锋 - 两个技能都可以
        // 可以继续添加其他职业的技能...
        
        // 默认所有职业都可以使用所有技能（如果需要）
        for (Profession p : Profession.values()) {
            PROFESSION_SKILLS.putIfAbsent(p, List.of("quick_heal", "strength_boost"));
        }
    }
    
    public SkillSelectionScreen(SkillEquipScreen parent, int targetSlot) {
        super(Component.translatable("gui.endshopattribute.skill.selection"));
        this.parent = parent;
        this.targetSlot = targetSlot;
        
        // 获取玩家的职业
        Minecraft mc = Minecraft.getInstance();
        Profession playerJob = JobDataAttachment.getJob(mc.player);
        
        System.out.println("[DEBUG] 打开技能选择界面");
        System.out.println("[DEBUG] 当前职业：" + playerJob.name() + " (" + playerJob.getDisplayName() + ")");
        
        // 获取该职业可用的技能列表
        List<String> jobSkills = PROFESSION_SKILLS.getOrDefault(playerJob, List.of());
        
        System.out.println("[DEBUG] 该职业可用技能：" + jobSkills);
        
        // 获取所有已解锁的技能
        this.availableSkills = new ArrayList<>();
        SkillDataAttachment.SkillData skillData = SkillDataAttachment.getSkillData(mc.player);
        
        System.out.println("[DEBUG] 已解锁技能数量：" + skillData.getUnlockedSkills().size());
        for (String unlockedSkill : skillData.getUnlockedSkills()) {
            System.out.println("[DEBUG]  - 已解锁：" + unlockedSkill);
        }
        
        for (String skillId : jobSkills) {
            // 只显示玩家已解锁的技能
            boolean isUnlocked = skillData.isUnlocked(skillId);
            System.out.println("[DEBUG] 技能 " + skillId + " 解锁状态：" + isUnlocked);
            
            if (isUnlocked) {
                Skill skill = SkillRegistry.getSkill(skillId);
                if (skill instanceof ActiveSkill) {
                    availableSkills.add(skillId);
                    System.out.println("[DEBUG] 添加到可用技能：" + skillId);
                } else {
                    System.out.println("[DEBUG] 跳过非主动技能：" + skillId);
                }
            }
        }
        
        System.out.println("[DEBUG] 最终可用技能数量：" + availableSkills.size());
    }
    
    @Override
    protected void init() {
        super.init();
            
        int buttonWidth = 200;
        int buttonHeight = 20;
        int startX = (this.width - buttonWidth) / 2;
        int startY = 50;
            
        // 如果没有可用技能，添加一个提示标签
        if (availableSkills.isEmpty()) {
            Component noSkillMsg = Component.literal("§e没有可用技能！");
            Component jobMsg = Component.literal("§e请先选择职业并解锁技能。");
                
            // 创建提示标签
            int msgWidth1 = this.font.width(noSkillMsg.getString());
            int msgWidth2 = this.font.width(jobMsg.getString());
                
            // 添加在界面中间
            addRenderableWidget(new net.minecraft.client.gui.components.StringWidget(
                (this.width - msgWidth1) / 2, 
                this.height / 2 - 10, 
                msgWidth1, 
                10, 
                noSkillMsg,
                this.font
            ));
                
            addRenderableWidget(new net.minecraft.client.gui.components.StringWidget(
                (this.width - msgWidth2) / 2, 
                this.height / 2 + 10, 
                msgWidth2, 
                10, 
                jobMsg,
                this.font
            ));
        }
            
        // 为每个可用技能创建按钮
        for (int i = 0; i < availableSkills.size(); i++) {
            String skillId = availableSkills.get(i);
            Skill skill = SkillRegistry.getSkill(skillId);
                
            int buttonY = startY + i * (buttonHeight + 4);
                
            Button button = Button.builder(
                    Component.literal(skill.getName()),
                    btn -> onSkillSelected(skillId)
                )
                .bounds(startX, buttonY, buttonWidth, buttonHeight)
                .build();
                
            addRenderableWidget(button);
        }
            
        // 卸下技能按钮（如果当前槽位已有技能）
        SkillDataAttachment.SkillData skillData = SkillDataAttachment.getSkillData(Minecraft.getInstance().player);
        String currentSkill = skillData.getEquippedSkill(targetSlot);
            
        if (currentSkill != null) {
            Button unequipButton = Button.builder(
                    Component.translatable("gui.endshopattribute.skill.unequip"),
                    btn -> onUnequip()
                )
                .bounds(startX, startY + availableSkills.size() * (buttonHeight + 4) + 10, buttonWidth, buttonHeight)
                .build();
                
            addRenderableWidget(unequipButton);
        }
            
        // 返回按钮
        Button backButton = Button.builder(
                Component.literal("返回"),
                btn -> onClose()
            )
            .bounds(startX, this.height - 30, buttonWidth, 20)
            .build();
            
        addRenderableWidget(backButton);
    }
    
    /**
     * 选择技能并装备
     */
    private void onSkillSelected(String skillId) {
        SkillDataAttachment.SkillData skillData = SkillDataAttachment.getSkillData(Minecraft.getInstance().player);
        
        boolean success = skillData.equipSkill(skillId, targetSlot);
        
        if (success) {
            Skill skill = SkillRegistry.getSkill(skillId);
            Minecraft.getInstance().player.sendSystemMessage(
                Component.literal("§a已将 §f" + skill.getName() + " §a装备到 " + 
                    SkillEquipHelper.getSlotName(targetSlot).getString())
            );
        }
        
        // 返回父界面并更新显示
        parent.updateFromSelection();
        Minecraft.getInstance().setScreen(parent);
    }
    
    /**
     * 卸下当前槽位的技能
     */
    private void onUnequip() {
        SkillDataAttachment.SkillData skillData = SkillDataAttachment.getSkillData(Minecraft.getInstance().player);
        skillData.unequipSkill(targetSlot);
        
        Minecraft.getInstance().player.sendSystemMessage(
            Component.literal("§e已卸下 " + SkillEquipHelper.getSlotName(targetSlot).getString() + " 的技能")
        );
        
        // 返回父界面并更新显示
        parent.updateFromSelection();
        Minecraft.getInstance().setScreen(parent);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 渲染深色半透明背景
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        
        // 绘制标题
        guiGraphics.drawString(
            this.font,
            this.title.getString(),
            this.width / 2 - this.font.width(this.title.getString()) / 2,
            20,
            0xFFFFFF,
            true
        );
        
        // 显示当前目标槽位
        Component slotInfo = Component.literal("当前选择：").append(SkillEquipHelper.getSlotName(targetSlot));
        guiGraphics.drawString(
            this.font,
            slotInfo.getString(),
            this.width / 2 - this.font.width(slotInfo.getString()) / 2,
            35,
            0xFFFF00,
            true
        );
        
        // 调用父类渲染（渲染按钮等）
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
