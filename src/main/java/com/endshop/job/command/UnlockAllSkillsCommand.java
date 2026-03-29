package com.endshop.job.command;

import com.endshop.job.network.SyncSkillDataPacket;
import com.endshop.job.skill.SkillDataAttachment;
import com.endshop.job.skill.SkillRegistry;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;

/**
 * 全解锁技能指令 - 管理员专用
 */
@EventBusSubscriber
public class UnlockAllSkillsCommand {
    
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("unlockallskills")
                .requires(source -> source.hasPermission(2)) // 需要管理员权限
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    SkillDataAttachment.SkillData skillData = 
                        SkillDataAttachment.getSkillData(player);
                                    
                    int[] unlockedCount = {0};
                                    
                    // 解锁所有已注册的技能
                    for (String skillId : SkillRegistry.getAllSkills().keySet()) {
                        if (!skillData.isUnlocked(skillId)) {
                            skillData.unlockSkill(skillId);
                            unlockedCount[0]++;
                        }
                    }
                                    
                    int count = unlockedCount[0];
                    context.getSource().sendSuccess(
                        () -> Component.literal("§a 已解锁 §f" + count + " §a 个技能！"),
                        true
                    );
                                        
                    // 发送同步包到客户端
                    SyncSkillDataPacket packet = new SyncSkillDataPacket(
                        skillData.getUnlockedSkillsSet(),
                        skillData.getSkillLevelsMap(),
                        skillData.getEquippedSkillsMap(),
                        new HashMap<>()  // 空的冷却时间映射
                    );
                    PacketDistributor.sendToPlayer(player, packet);
                                        
                    return count;
                })
        );
    }
}
