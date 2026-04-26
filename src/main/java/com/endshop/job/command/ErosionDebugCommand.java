package com.endshop.job.command;

import com.endshop.job.effect.ModEffects;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * 侵蚀效果调试命令
 */
public class ErosionDebugCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("侵蚀调试")
            .then(Commands.literal("添加效果")
                .executes(context -> {
                    Entity entity = context.getSource().getEntity();
                    if (entity instanceof LivingEntity livingEntity) {
                        // 添加侵蚀效果（无限持续）
                        livingEntity.addEffect(new MobEffectInstance(
                            ModEffects.EROSION,
                            Integer.MAX_VALUE,
                            0,
                            false,
                            false
                        ));
                        context.getSource().sendSuccess(
                            () -> Component.literal("§e已添加侵蚀效果"),
                            true
                        );
                        return 1;
                    }
                    return 0;
                })
            )
            .then(Commands.literal("设置层数")
                .then(Commands.argument("层数", IntegerArgumentType.integer(0, 100))
                    .executes(context -> {
                        Entity entity = context.getSource().getEntity();
                        int stacks = IntegerArgumentType.getInteger(context, "层数");
                        
                        if (entity instanceof LivingEntity livingEntity) {
                            // 设置侵蚀层数
                            livingEntity.getPersistentData().putInt("erosion_stacks", stacks);
                            context.getSource().sendSuccess(
                                () -> Component.literal("§e已设置侵蚀层数为: §c" + stacks + "§e/100"),
                                true
                            );
                            return 1;
                        }
                        return 0;
                    })
                )
            )
            .then(Commands.literal("查看层数")
                .executes(context -> {
                    Entity entity = context.getSource().getEntity();
                    if (entity instanceof LivingEntity livingEntity) {
                        int stacks = livingEntity.getPersistentData().getInt("erosion_stacks");
                        context.getSource().sendSuccess(
                            () -> Component.literal("§e当前侵蚀层数: §c" + stacks + "§e/100"),
                            true
                        );
                        return 1;
                    }
                    return 0;
                })
            )
            .then(Commands.literal("清除层数")
                .executes(context -> {
                    Entity entity = context.getSource().getEntity();
                    if (entity instanceof LivingEntity livingEntity) {
                        livingEntity.getPersistentData().putInt("erosion_stacks", 0);
                        livingEntity.removeEffect(ModEffects.EROSION);
                        context.getSource().sendSuccess(
                            () -> Component.literal("§e已清除侵蚀效果和层数"),
                            true
                        );
                        return 1;
                    }
                    return 0;
                })
            )
        );
    }
}
