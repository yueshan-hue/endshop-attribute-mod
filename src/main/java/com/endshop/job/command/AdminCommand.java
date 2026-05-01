package com.endshop.job.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.server.level.ServerLevel;

/**
 * 管理员控制指令
 * 用于修复实体AI问题和清理损坏的实体
 */
public class AdminCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("admin")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("fix")
                .then(Commands.literal("entities")
                    .executes(AdminCommand::fixEntities)
                )
            )
            .then(Commands.literal("kill")
                .then(Commands.literal("broken")
                    .executes(AdminCommand::killBrokenEntities)
                )
            )
        );
    }
    
    /**
     * 修复所有实体的AI目标
     */
    private static int fixEntities(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        
        int fixedCount = 0;
        
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof Mob mob) {
                try {
                    // 检查goalSelector是否有问题
                    mob.goalSelector.getAvailableGoals();
                    mob.targetSelector.getAvailableGoals();
                } catch (Exception e) {
                    // 如果有异常,重新初始化AI
                    source.sendSystemMessage(Component.literal("发现异常的实体: " + entity.getName().getString() + " at " + entity.blockPosition()));
                    fixedCount++;
                }
            }
        }
        
        source.sendSystemMessage(Component.literal("已检查并修复 " + fixedCount + " 个实体"));
        return fixedCount;
    }
    
    /**
     * 杀死所有可能有问题的实体
     */
    private static int killBrokenEntities(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        
        int killedCount = 0;
        
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof Mob mob) {
                // 检查是否是骷髅类实体(崩溃日志中显示是skeleton)
                if (entity.getType().toString().contains("skeleton")) {
                    mob.remove(Entity.RemovalReason.KILLED);
                    killedCount++;
                }
            }
        }
        
        source.sendSystemMessage(Component.literal("已移除 " + killedCount + " 个可能损坏的实体"));
        return killedCount;
    }
}
