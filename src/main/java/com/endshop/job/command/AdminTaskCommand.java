package com.endshop.job.command;

import com.endshop.job.entity.EndshopEntity;
import com.endshop.job.entity.EndshopEntityTypes;
import com.endshop.job.entity.EntitySingletonManager;
import com.endshop.job.task.TaskDataAttachment;
import com.endshop.job.task.TaskType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.List;

/**
 * 管理员任务指令 - 控制管理员执行任务
 * 用法：/管理员 帮我 <任务类型>
 * 示例：/管理员 帮我 挖矿
 */
public class AdminTaskCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // 注册主命令 - 管理员指令
        dispatcher.register(
                Commands.literal("管理员")
                        .requires(source -> true) // 允许所有玩家使用
                        // 注册 帮我 子命令
                        .then(Commands.literal("帮我")
                                // 为每个任务类型注册单独的子命令
                                .then(Commands.literal("挖矿").executes(ctx -> executeTask(ctx, TaskType.MINING)))
                                .then(Commands.literal("伐木").executes(ctx -> executeTask(ctx, TaskType.WOODCUTTING)))
                                .then(Commands.literal("钓鱼").executes(ctx -> executeTask(ctx, TaskType.FISHING)))
                                .then(Commands.literal("建筑").executes(ctx -> executeTask(ctx, TaskType.BUILDING)))
                                .then(Commands.literal("打怪").executes(ctx -> executeTask(ctx, TaskType.COMBAT)))
                                .then(Commands.literal("守护").executes(ctx -> executeTask(ctx, TaskType.GUARD)))
                        )
                        // 注册 召唤 子命令
                        .then(Commands.literal("召唤")
                                // 为每个角色注册单独的召唤子命令
                                .then(Commands.literal("伊冯").executes(ctx -> executeSummon(ctx, EndshopEntityTypes.YVON.get(), "伊冯")))
                                .then(Commands.literal("佩丽卡").executes(ctx -> executeSummon(ctx, EndshopEntityTypes.PERICA.get(), "佩丽卡")))
                                .then(Commands.literal("史尔特尔").executes(ctx -> executeSummon(ctx, EndshopEntityTypes.SURTR.get(), "史尔特尔")))
                                .then(Commands.literal("洁尔佩塔").executes(ctx -> executeSummon(ctx, EndshopEntityTypes.GERPEITA.get(), "洁尔佩塔")))
                                .then(Commands.literal("洛茜").executes(ctx -> executeSummon(ctx, EndshopEntityTypes.ROSIE.get(), "洛茜")))
                                .then(Commands.literal("管理员B").executes(ctx -> executeSummon(ctx, EndshopEntityTypes.ADMIN_B.get(), "管理员B")))
                                .then(Commands.literal("陈千语").executes(ctx -> executeSummon(ctx, EndshopEntityTypes.CHEN_QIANYU.get(), "陈千语")))
                                .then(Commands.literal("别礼").executes(ctx -> executeSummon(ctx, EndshopEntityTypes.BIELI.get(), "别礼")))
                                .then(Commands.literal("埃特拉").executes(ctx -> executeSummon(ctx, EndshopEntityTypes.ETERA.get(), "埃特拉")))
                                .then(Commands.literal("庄方宜").executes(ctx -> executeSummon(ctx, EndshopEntityTypes.ZHUANG_FANGYI.get(), "庄方宜")))
                                .then(Commands.literal("汤汤").executes(ctx -> executeSummon(ctx, EndshopEntityTypes.TANGTANG.get(), "汤汤")))
                                .then(Commands.literal("秋栗").executes(ctx -> executeSummon(ctx, EndshopEntityTypes.QIULI.get(), "秋栗")))
                                .then(Commands.literal("管理员A").executes(ctx -> executeSummon(ctx, EndshopEntityTypes.ADMIN_A.get(), "管理员A")))
                                .then(Commands.literal("萤石").executes(ctx -> executeSummon(ctx, EndshopEntityTypes.YINGSHI.get(), "萤石")))
                                .then(Commands.literal("赛希").executes(ctx -> executeSummon(ctx, EndshopEntityTypes.SAIXI.get(), "赛希")))
                                // 默认召唤管理员A（保持向后兼容）
                                .executes(ctx -> executeSummon(ctx, EndshopEntityTypes.ADMIN_A.get(), "管理员A"))
                        )
                        // 显示帮助信息
                        .executes(ctx -> {
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    "§e用法：§f/管理员 帮我 <任务类型>\n" +
                                    "§e      §f/管理员 召唤 <角色名>\n" +
                                    "§e      §f/<角色名> 帮我 <任务类型>\n" +
                                    "§e可用任务：§f挖矿 伐木 钓鱼 建筑 打怪 守护\n" +
                                    "§e示例：§f/管理员 帮我 挖矿\n" +
                                    "§e示例：§f/庄方宜 帮我 伐木\n" +
                                    "§e示例：§f/管理员 召唤 庄方宜"
                            ), false);
                            return 0;
                        })
        );
        
        // 注册角色专属指令 - 让每个角色都能直接接受命令
        registerCharacterCommand(dispatcher, "伊冯", EndshopEntityTypes.YVON.get());
        registerCharacterCommand(dispatcher, "佩丽卡", EndshopEntityTypes.PERICA.get());
        registerCharacterCommand(dispatcher, "史尔特尔", EndshopEntityTypes.SURTR.get());
        registerCharacterCommand(dispatcher, "洁尔佩塔", EndshopEntityTypes.GERPEITA.get());
        registerCharacterCommand(dispatcher, "洛茜", EndshopEntityTypes.ROSIE.get());
        registerCharacterCommand(dispatcher, "管理员B", EndshopEntityTypes.ADMIN_B.get());
        registerCharacterCommand(dispatcher, "陈千语", EndshopEntityTypes.CHEN_QIANYU.get());
        registerCharacterCommand(dispatcher, "别礼", EndshopEntityTypes.BIELI.get());
        registerCharacterCommand(dispatcher, "埃特拉", EndshopEntityTypes.ETERA.get());
        registerCharacterCommand(dispatcher, "庄方宜", EndshopEntityTypes.ZHUANG_FANGYI.get());
        registerCharacterCommand(dispatcher, "汤汤", EndshopEntityTypes.TANGTANG.get());
        registerCharacterCommand(dispatcher, "秋栗", EndshopEntityTypes.QIULI.get());
        registerCharacterCommand(dispatcher, "管理员A", EndshopEntityTypes.ADMIN_A.get());
        registerCharacterCommand(dispatcher, "萤石", EndshopEntityTypes.YINGSHI.get());
        registerCharacterCommand(dispatcher, "赛希", EndshopEntityTypes.SAIXI.get());
    }

    /**
     * 注册角色专属指令
     * 用法：/角色名 帮我 <任务类型>
     * 示例：/庄方宜 帮我 伐木
     */
    private static void registerCharacterCommand(CommandDispatcher<CommandSourceStack> dispatcher, 
                                                 String characterName,
                                                 net.minecraft.world.entity.EntityType<EndshopEntity> entityType) {
        dispatcher.register(
                Commands.literal(characterName)
                        .requires(source -> true) // 允许所有玩家使用
                        .then(Commands.literal("帮我")
                                .then(Commands.literal("挖矿").executes(ctx -> executeCharacterTask(ctx, characterName, entityType, TaskType.MINING)))
                                .then(Commands.literal("伐木").executes(ctx -> executeCharacterTask(ctx, characterName, entityType, TaskType.WOODCUTTING)))
                                .then(Commands.literal("钓鱼").executes(ctx -> executeCharacterTask(ctx, characterName, entityType, TaskType.FISHING)))
                                .then(Commands.literal("建筑").executes(ctx -> executeCharacterTask(ctx, characterName, entityType, TaskType.BUILDING)))
                                .then(Commands.literal("打怪").executes(ctx -> executeCharacterTask(ctx, characterName, entityType, TaskType.COMBAT)))
                                .then(Commands.literal("守护").executes(ctx -> executeCharacterTask(ctx, characterName, entityType, TaskType.GUARD)))
                        )
                        .executes(ctx -> {
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    "§e用法：§f/" + characterName + " 帮我 <任务类型>\n" +
                                    "§e可用任务：§f挖矿 伐木 钓鱼 建筑 打怪 守护\n" +
                                    "§e示例：§f/" + characterName + " 帮我 伐木"
                            ), false);
                            return 0;
                        })
        );
    }

    /**
     * 执行针对特定角色的任务
     */
    private static int executeCharacterTask(CommandContext<CommandSourceStack> ctx, 
                                           String characterName,
                                           net.minecraft.world.entity.EntityType<EndshopEntity> entityType,
                                           TaskType taskType) {
        try {
            Level level = ctx.getSource().getLevel();
            Vec3 pos = ctx.getSource().getPosition();
            AABB aabb = new AABB(pos.x - 50, pos.y - 50, pos.z - 50, pos.x + 50, pos.y + 50, pos.z + 50);
            
            // 查找指定类型的角色实体
            List<Entity> characters = level.getEntitiesOfClass(Entity.class, aabb, entity -> {
                return entity.getType() == entityType;
            });

            if (characters.isEmpty()) {
                ctx.getSource().sendFailure(Component.literal("§c附近没有找到【" + characterName + "】，请先召唤她/他。"));
                return 0;
            }

            // 选择最近的目标
            Entity target = null;
            double minDistance = Double.MAX_VALUE;
            
            for (Entity character : characters) {
                double distance = character.distanceToSqr(pos.x, pos.y, pos.z);
                if (distance < minDistance) {
                    minDistance = distance;
                    target = character;
                }
            }

            if (target != null) {
                // 设置任务
                TaskDataAttachment.setTask(target, taskType);

                // 发送成功消息
                String targetName = target.getName().getString();
                if (taskType == TaskType.NONE) {
                    ctx.getSource().sendSuccess(() -> Component.literal(
                            "§a已清除 " + targetName + " 的任务"
                    ), true);
                } else {
                    ctx.getSource().sendSuccess(() -> Component.literal(
                            "§a已命令 " + targetName + " 开始" + taskType.getDisplayName()
                    ), true);
                    
                    // 通知角色
                    target.sendSystemMessage(Component.literal(
                            "§e收到指令：开始" + taskType.getDisplayName()
                    ));
                }
            }

            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§c执行指令时出错：" + e.getMessage()));
            return 0;
        }
    }

    /**
     * 执行具体任务（通用，查找所有管理员干员）
     */
    private static int executeTask(CommandContext<CommandSourceStack> ctx, TaskType taskType) {
        try {
            // 查找附近的管理员干员
            Level level = ctx.getSource().getLevel();
            Vec3 pos = ctx.getSource().getPosition();
            AABB aabb = new AABB(pos.x - 50, pos.y - 50, pos.z - 50, pos.x + 50, pos.y + 50, pos.z + 50);
            
            List<Entity> admins = level.getEntitiesOfClass(Entity.class, aabb, entity -> {
                net.minecraft.resources.ResourceLocation key = level.registryAccess().registry(Registries.ENTITY_TYPE).get().getKey(entity.getType());
                return key != null && key.getNamespace().equals("endshopattribute") && key.getPath().startsWith("admin_");
            });

            if (admins.isEmpty()) {
                ctx.getSource().sendFailure(Component.literal("§c附近没有管理员干员，请先召唤一个。"));
                return 0;
            }

            // 选择最近的管理员干员
            Entity target = null;
            double minDistance = Double.MAX_VALUE;
            
            for (Entity admin : admins) {
                double distance = admin.distanceToSqr(pos.x, pos.y, pos.z);
                if (distance < minDistance) {
                    minDistance = distance;
                    target = admin;
                }
            }

            if (target != null) {
                // 设置任务
                TaskDataAttachment.setTask(target, taskType);

                // 发送成功消息
                String targetName = target.getName().getString();
                if (taskType == TaskType.NONE) {
                    ctx.getSource().sendSuccess(() -> Component.literal(
                            "§a已清除 " + targetName + " 的任务"
                    ), true);
                } else {
                    ctx.getSource().sendSuccess(() -> Component.literal(
                            "§a已命令 " + targetName + " 开始" + taskType.getDisplayName()
                    ), true);
                    
                    // 通知管理员干员
                    target.sendSystemMessage(Component.literal(
                            "§e收到指令：开始" + taskType.getDisplayName()
                    ));
                }
            }

            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§c执行指令时出错：" + e.getMessage()));
            return 0;
        }
    }

    /**
     * 执行召唤终末地角色
     */
    private static int executeSummon(CommandContext<CommandSourceStack> ctx, net.minecraft.world.entity.EntityType<EndshopEntity> entityType, String entityName) {
        try {
            // 检查命令源是否是玩家
            if (!(ctx.getSource().getEntity() instanceof ServerPlayer player)) {
                ctx.getSource().sendFailure(Component.literal("§c只有玩家可以执行此命令"));
                return 0;
            }

            // 获取实体类型的注册名称
            net.minecraft.resources.ResourceLocation key = player.level().registryAccess().registry(Registries.ENTITY_TYPE).get().getKey(entityType);
            String entityTypeKey = key != null ? key.getPath() : "unknown";
            
            // 检查是否已经存在该类型的实体
            if (EntitySingletonManager.isEntityTypeSpawned(entityTypeKey)) {
                ctx.getSource().sendFailure(Component.literal("§c终末地角色【" + entityName + "】已经存在，每个角色在服务器中只能出现一次！"));
                return 0;
            }

            // 获取玩家位置
            net.minecraft.world.level.Level level = player.level();
            Vec3 pos = player.position();

            // 创建终末地角色实体
            EndshopEntity endshopEntity = new EndshopEntity(
                    entityType,
                    level
            );

            // 设置实体位置（在玩家前方2格）
            endshopEntity.setPos(pos.x, pos.y, pos.z);

            // 生成实体
            level.addFreshEntity(endshopEntity);

            // 标记该类型实体已被召唤
            EntitySingletonManager.markEntitySpawned(entityTypeKey, endshopEntity.getUUID());

            // 发送成功消息
            ctx.getSource().sendSuccess(() -> Component.literal(
                    "§a成功召唤终末地角色【" + entityName + "】！"
            ), true);

            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§c执行指令时出错：" + e.getMessage()));
            return 0;
        }
    }

    /**
     * 旧的执行方法（保留以保持兼容性）
     */
    private static int execute(CommandContext<CommandSourceStack> ctx) {
        try {
            String taskArg = StringArgumentType.getString(ctx, "任务");
            TaskType taskType = TaskType.fromDisplayName(taskArg);

            if (taskType == null) {
                ctx.getSource().sendFailure(Component.literal("§c未知任务类型：" + taskArg));
                return 0;
            }

            return executeTask(ctx, taskType);
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§c执行指令时出错：" + e.getMessage()));
            return 0;
        }
    }
}
