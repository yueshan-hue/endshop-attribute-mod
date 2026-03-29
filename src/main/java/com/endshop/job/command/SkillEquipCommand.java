package com.endshop.job.command;

import com.endshop.job.network.SyncSkillDataPacket;
import com.endshop.job.skill.SkillDataAttachment;
import com.endshop.job.skill.SkillRegistry;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;

/**
 * 技能装备指令
 */
@EventBusSubscriber
public class SkillEquipCommand {
    
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("skillequip")
                .then(Commands.argument("skillId", StringArgumentType.string())
                    .suggests((context, builder) -> {
                        // 建议所有已注册的技能 ID
                        for (String skillId : SkillRegistry.getAllSkills().keySet()) {
                            builder.suggest(skillId);
                        }
                        return builder.buildFuture();
                    })
                    .then(Commands.argument("slot", IntegerArgumentType.integer(0, 3))
                        .executes(context -> {
                            String skillId = StringArgumentType.getString(context, "skillId");
                            int slot = IntegerArgumentType.getInteger(context, "slot");
                            
                            var player = context.getSource().getPlayerOrException();
                            SkillDataAttachment.SkillData skillData = 
                                SkillDataAttachment.getSkillData(player);
                            
                            boolean success = skillData.equipSkill(skillId, slot);
                            
                            if (success) {
                                // 发送同步包到客户端
                                SyncSkillDataPacket packet = new SyncSkillDataPacket(
                                    skillData.getUnlockedSkillsSet(),
                                    skillData.getSkillLevelsMap(),
                                    skillData.getEquippedSkillsMap(),
                                    new HashMap<>()  // 空的冷却时间映射
                                );
                                PacketDistributor.sendToPlayer((ServerPlayer) player, packet);
                                                            
                                var skill = SkillRegistry.getSkill(skillId);
                                context.getSource().sendSuccess(
                                    () -> Component.literal("§a 已将 §f" + skill.getName() + 
                                        " §a 装备到槽位 " + (slot + 1)),
                                    true
                                );
                                return 1;
                            } else {
                                context.getSource().sendFailure(
                                    Component.literal("§c无法装备此技能！请检查技能是否已解锁")
                                );
                                return 0;
                            }
                        })
                    )
                )
        );
    }
}
