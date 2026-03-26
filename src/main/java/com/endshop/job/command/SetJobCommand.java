package com.endshop.job.command;

import com.endshop.job.data.JobDataAttachment;
import com.endshop.job.profession.Profession;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.CompletableFuture;

/**
 * /setjob <职业名> 指令
 * 支持中文名和英文枚举名，Tab键自动补全
 */
public class SetJobCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("setjob")
                        .then(Commands.argument("job", StringArgumentType.string())
                                .suggests(SetJobCommand::suggestJobs)
                                .executes(SetJobCommand::execute)
                        )
                        .executes(ctx -> {
                            // /setjob 不带参数：显示帮助
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    "§e用法：§f/setjob <职业名>\n" +
                                    "§e可用职业：§f先锋 近卫 重装 狙击 特种 医疗 术师 辅助\n" +
                                    "§e清除职业：§f/setjob 无职业"
                            ), false);
                            return 0;
                        })
        );
    }

    /** Tab 自动补全：列出所有职业的中文名 */
    private static CompletableFuture<Suggestions> suggestJobs(
            CommandContext<CommandSourceStack> ctx,
            SuggestionsBuilder builder) {
        String input = builder.getRemaining().toLowerCase();
        for (Profession p : Profession.values()) {
            String name = p.getDisplayName();
            if (name.contains(input) || p.name().toLowerCase().contains(input)) {
                builder.suggest(name);
            }
        }
        return builder.buildFuture();
    }

    /** 执行职业设置 */
    private static int execute(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getEntity() instanceof ServerPlayer player)) {
            ctx.getSource().sendFailure(Component.literal("§c此指令只能由玩家执行。"));
            return 0;
        }

        String jobArg = StringArgumentType.getString(ctx, "job");
        Profession profession = Profession.fromDisplayName(jobArg);

        if (profession == null) {
            player.sendSystemMessage(Component.literal(
                    "§c未知职业：" + jobArg + "\n§e请使用Tab键查看可用职业。"
            ));
            return 0;
        }

        JobDataAttachment.setJob(player, profession);

        if (profession == Profession.NONE) {
            player.sendSystemMessage(Component.literal("§7你已清除职业。"));
        } else {
            player.sendSystemMessage(Component.literal(
                    "§a职业已设置为 " + profession.getColoredPrefix() + profession.getDisplayName() + "§a！"
            ));
        }
        return 1;
    }
}
