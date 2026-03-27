package com.endshop.job.skill;

import net.minecraft.network.chat.Component;

/**
 * 技能使用示例 - 演示如何装备和释放技能
 */
public class SkillUsageExample {
    
    /**
     * 示例：玩家装备技能到槽位
     * 
     * @param player 玩家
     * @param skillId 技能 ID
     * @param slot 槽位 (0-3)
     */
    public static void exampleEquipSkill(net.minecraft.world.entity.player.Player player, 
                                         String skillId, int slot) {
        // 获取玩家的技能数据
        SkillDataAttachment.SkillData skillData = SkillDataAttachment.getSkillData(player);
        
        // 尝试装备技能
        boolean success = skillData.equipSkill(skillId, slot);
        
        if (success) {
            // 显示成功消息
            Component slotName = SkillEquipHelper.getSlotName(slot);
            Skill skill = SkillRegistry.getSkill(skillId);
            
            player.sendSystemMessage(
                Component.literal("§a已将 §f" + skill.getName() + " §a装备到 §e" + slotName.getString())
            );
        } else {
            // 显示失败消息
            player.sendSystemMessage(
                Component.literal("§c无法装备此技能！请检查是否已解锁该技能")
            );
        }
    }
    
    /**
     * 示例：玩家使用槽位技能
     * 
     * @param player 玩家
     * @param slot 槽位 (0-3)
     */
    public static void exampleUseSkill(net.minecraft.world.entity.player.Player player, int slot) {
        SkillDataAttachment.SkillData skillData = SkillDataAttachment.getSkillData(player);
        
        // 获取槽位中的技能
        String skillId = skillData.getEquippedSkill(slot);
        
        if (skillId == null) {
            player.sendSystemMessage(
                Component.literal("§c该槽位未装备技能！按 [B] 键打开技能界面进行装备")
            );
            return;
        }
        
        // 获取技能实例
        Skill skill = SkillRegistry.getSkill(skillId);
        
        if (!(skill instanceof ActiveSkill activeSkill)) {
            player.sendSystemMessage(
                Component.literal("§c该技能不是主动技能！")
            );
            return;
        }
        
        // 检查冷却
        if (!activeSkill.isAvailable()) {
            int cooldown = activeSkill.getCurrentCooldown();
            player.sendSystemMessage(
                Component.literal("§e技能正在冷却中：§c" + cooldown + "§e秒")
            );
            return;
        }
        
        // 激活技能
        if (activeSkill.activate(player)) {
            // 技能激活成功
            Component slotName = SkillEquipHelper.getSlotName(slot);
            player.sendSystemMessage(
                Component.literal("§b§l使用了技能：§r" + activeSkill.getDisplayName())
            );
        }
    }
    
    /**
     * 示例：查看当前装备的技能
     */
    public static void exampleViewEquippedSkills(net.minecraft.world.entity.player.Player player) {
        SkillDataAttachment.SkillData skillData = SkillDataAttachment.getSkillData(player);
        
        player.sendSystemMessage(Component.literal("§6====== 技能槽位 ======"));
        
        for (int i = 0; i < 4; i++) {
            String skillId = skillData.getEquippedSkill(i);
            Component slotName = SkillEquipHelper.getSlotName(i);
            
            if (skillId != null) {
                Skill skill = SkillRegistry.getSkill(skillId);
                player.sendSystemMessage(
                    Component.literal("§e" + slotName.getString() + ": §f" + skill.getName())
                );
            } else {
                player.sendSystemMessage(
                    Component.translatable("gui.endshopattribute.skill.no_skill")
                        .withStyle(s -> s.withColor(0xAAAAAA))
                );
            }
        }
        
        player.sendSystemMessage(Component.literal("§6===================="));
    }
}
