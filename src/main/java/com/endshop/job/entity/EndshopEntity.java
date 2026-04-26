package com.endshop.job.entity;

import com.endshop.job.molang.runtime.ExecutionContext;
import com.endshop.job.molang.runtime.ExpressionEvaluatorImpl;
import com.endshop.job.molang.runtime.binding.StandardBindings;
import com.endshop.job.skill.Skill;
import com.endshop.job.skill.SkillRegistry;
import com.endshop.job.skill.admin.AdminSkills;
import com.endshop.job.ai.AIManager;
import com.endshop.job.task.TaskDataAttachment;
import com.endshop.job.task.TaskType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 终末地实体基类
 */
public class EndshopEntity extends Mob {
    private final Map<String, Object> variables = new HashMap<>();
    private final ExpressionEvaluatorImpl evaluator = new ExpressionEvaluatorImpl();
    
    // 技能相关字段
    private final Map<String, Integer> skillCooldowns = new HashMap<>(); // 技能冷却时间
    private final Map<String, Integer> skillLevels = new HashMap<>(); // 技能等级
    private int skillEnergy = 0; // 技能能量
    private int maxSkillEnergy = 100; // 最大技能能量
    private int ultimateEnergy = 0; // 终结能量
    private int maxUltimateEnergy = 80; // 最大终结能量
    
    // AI 管理器
    private final AIManager aiManager;

    public EndshopEntity(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
        // 初始化 AI 管理器
        this.aiManager = new AIManager(this);
        // 初始化标准绑定
        variables.putAll(StandardBindings.create());
        // 禁用默认的跟随行为
        setPersistenceRequired();
        
        // 初始化技能等级和冷却时间
        initializeSkills();
    }
    
    /**
     * 初始化技能等级和冷却时间
     */
    private void initializeSkills() {
        // 初始化管理员技能等级
        skillLevels.put("admin_damage_sequence", 1);
        skillLevels.put("admin_composition_sequence", 1);
        skillLevels.put("admin_lockdown_sequence", 1);
        skillLevels.put("admin_bombardment_sequence", 1);
        
        // 初始化技能冷却时间
        skillCooldowns.put("admin_composition_sequence", 0);
        skillCooldowns.put("admin_lockdown_sequence", 0);
        skillCooldowns.put("admin_bombardment_sequence", 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    /**
     * 获取 Molang 执行上下文
     */
    public ExecutionContext<EndshopEntity> getMolangContext() {
        return new ExecutionContext<EndshopEntity>() {
            @Override
            public @Nullable Object eval(@NotNull com.endshop.job.molang.parser.ast.Expression expression) {
                return evaluator.evaluate(expression, this);
            }

            @Override
            public @Nullable Object get(@NotNull String name) {
                return variables.get(name);
            }

            @Override
            public void set(@NotNull String name, @Nullable Object value) {
                variables.put(name, value);
            }

            @Override
            public @NotNull EndshopEntity environment() {
                return EndshopEntity.this;
            }

            @Override
            public @NotNull ExpressionEvaluatorImpl evaluator() {
                return evaluator;
            }
        };
    }

    /**
     * 执行 Molang 表达式
     */
    public @Nullable Object executeMolangExpression(com.endshop.job.molang.parser.ast.Expression expression) {
        return getMolangContext().eval(expression);
    }

    /**
     * 设置 Molang 变量
     */
    public void setMolangVariable(String name, Object value) {
        variables.put(name, value);
    }

    /**
     * 获取 Molang 变量
     */
    public @Nullable Object getMolangVariable(String name) {
        return variables.get(name);
    }

    @Override
    public void tick() {
        super.tick();
        // 每 tick 更新 Molang 变量（客户端和服务端都更新，用于渲染）
        updateMolangVariables();
        
        // AI 逻辑只在服务端执行
        if (!level().isClientSide()) {
            // 更新技能相关状态
            updateSkillState();
            // 执行任务
            executeTask();
            // 自主使用技能
            useSkillsAutomatically();
        }
    }
    
    /**
     * 更新技能相关状态
     */
    private void updateSkillState() {
        // 减少技能冷却时间
        for (Map.Entry<String, Integer> entry : skillCooldowns.entrySet()) {
            if (entry.getValue() > 0) {
                skillCooldowns.put(entry.getKey(), entry.getValue() - 1);
            }
        }
        
        // 恢复终结能量
        if (ultimateEnergy < maxUltimateEnergy) {
            ultimateEnergy += 1; // 每 tick 恢复 1 点终结能量
        }
    }
    
    /**
     * 自主使用技能
     */
    private void useSkillsAutomatically() {
        // 检查是否有敌人
        LivingEntity target = findNearestEnemy();
        if (target == null) {
            return; // 没有敌人，不使用技能
        }
        
        // 检查距离
        double distance = distanceToSqr(target);
        if (distance > 25) { // 距离超过 5 格，不使用技能
            return;
        }
        
        // 尝试使用终结技能（轰击序列）
        if (canUseUltimateSkill() && shouldUseUltimateSkill(target)) {
            useUltimateSkill();
            return;
        }
        
        // 尝试使用普通技能（构成序列）
        if (canUseCompositionSkill() && shouldUseCompositionSkill(target)) {
            useCompositionSkill();
            return;
        }
        
        // 尝试使用连携技能（锁闭序列）
        if (canUseLockdownSkill() && shouldUseLockdownSkill(target)) {
            useLockdownSkill();
            return;
        }
    }
    
    /**
     * 寻找最近的敌人
     */
    private LivingEntity findNearestEnemy() {
        Level level = level();
        if (level == null) return null;
        
        LivingEntity targetEntity = null;
        double minDistance = Double.MAX_VALUE;
        
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(16))) {
            if (entity != this && isEnemy(entity)) {
                double distance = distanceToSqr(entity);
                if (distance < minDistance) {
                    minDistance = distance;
                    targetEntity = entity;
                }
            }
        }
        
        return targetEntity;
    }

    /**
     * 执行当前任务
     */
    private void executeTask() {
        TaskType taskType = TaskDataAttachment.getTaskData(this).getTaskType();
        // 调试信息
        if (taskType != TaskType.NONE && level().getGameTime() % 20 == 0) { // 每 20 tick 输出一次
            System.out.println("EndshopEntity: 当前任务 - " + taskType.getDisplayName());
        }
        
        // 使用 AI 管理器处理任务
        aiManager.setTaskType(taskType);
        aiManager.update();
    }

    /**
     * 更新 Molang 变量
     */
    private void updateMolangVariables() {
        // 更新实体位置变量
        setMolangVariable("entity.position.x", getX());
        setMolangVariable("entity.position.y", getY());
        setMolangVariable("entity.position.z", getZ());
        
        // 更新实体旋转变量
        setMolangVariable("entity.rotation.x", getXRot());
        setMolangVariable("entity.rotation.y", getYRot());
        
        // 更新实体健康值变量
        setMolangVariable("entity.health", getHealth());
        setMolangVariable("entity.max_health", getMaxHealth());
        
        // 更新世界时间变量
        if (level() != null) {
            setMolangVariable("time.day", level().getDayTime() / 24000.0);
            setMolangVariable("time.time", level().getGameTime());
        }
        
        // 更新任务相关变量
        TaskType taskType = TaskDataAttachment.getTaskData(this).getTaskType();
        setMolangVariable("task.current", taskType.getDisplayName());
        setMolangVariable("task.has_task", taskType != TaskType.NONE);
    }

    /**
     * 执行挖矿任务
     */
    private void executeMiningTask() {
        // 搜索附近的矿石
        BlockPos center = blockPosition();
        BlockPos targetPos = null;
        double minDistance = Double.MAX_VALUE;
        int oreCount = 0;
        
        for (int x = -10; x <= 10; x++) {
            for (int y = -5; y <= 5; y++) {
                for (int z = -10; z <= 10; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState state = level().getBlockState(pos);
                    
                    if (isMineableOre(state)) {
                        oreCount++;
                        double distance = distanceToSqr(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                        if (distance < minDistance) {
                            minDistance = distance;
                            targetPos = pos;
                        }
                    }
                }
            }
        }
        
        // 调试信息
        if (level().getGameTime() % 40 == 0) { // 每 40 tick 输出一次
            System.out.println("EndshopEntity: 挖矿任务 - 发现 " + oreCount + " 个矿石");
            if (targetPos != null) {
                System.out.println("EndshopEntity: 挖矿任务 - 目标位置: " + targetPos + ", 距离: " + Math.sqrt(minDistance));
            } else {
                System.out.println("EndshopEntity: 挖矿任务 - 没有找到矿石");
            }
        }
        
        if (targetPos != null) {
            // 移动到目标位置
            moveTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, 0.4);
            
            // 检查距离
            if (distanceToSqr(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5) <= 2.5) {
                // 选择合适的工具
                BlockState state = level().getBlockState(targetPos);
                selectProperTool(state);
                
                // 面向目标
                lookAt(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
                
                // 使用工具挖掘方块
                useItemOn(targetPos, state);
                
                // 调试信息
                System.out.println("EndshopEntity: 挖矿任务 - 使用工具挖掘方块: " + targetPos);
            }
        }
    }

    /**
     * 面向指定位置
     */
    public void lookAt(double x, double y, double z) {
        double dx = x - getX();
        double dy = y - getY() - 1.0; // 调整高度，使视线水平
        double dz = z - getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);
        
        if (distance > 0.0) {
            float yaw = (float) Math.atan2(dz, dx) * 180.0F / (float) Math.PI - 90.0F;
            float pitch = (float) -Math.atan2(dy, distance) * 180.0F / (float) Math.PI;
            setYRot(yaw);
            setXRot(pitch);
        }
    }

    /**
     * 使用物品在指定位置
     */
    private void useItemOn(BlockPos pos, BlockState state) {
        // 获取主手物品
        net.minecraft.world.item.ItemStack stack = getItemInHand(InteractionHand.MAIN_HAND);
        
        // 检查物品是否有效
        if (!stack.isEmpty()) {
            // 面向方块
            lookAt(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            
            // 直接破坏方块（对于非玩家实体，这是更简单的方法）
            level().destroyBlock(pos, true);
            
            // 模拟工具损耗
            stack.hurtAndBreak(1, this, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
            
            if (stack.isEmpty()) {
                setItemInHand(InteractionHand.MAIN_HAND, net.minecraft.world.item.ItemStack.EMPTY);
            } else {
                setItemInHand(InteractionHand.MAIN_HAND, stack);
            }
        }
    }

    /**
     * 执行伐木任务
     */
    private void executeWoodcuttingTask() {
        // 调试信息 - 每次执行任务都输出
        System.out.println("EndshopEntity: 开始执行伐木任务");
        
        // 搜索附近的树木
        BlockPos center = blockPosition();
        BlockPos logTargetPos = null; // 木头目标位置
        double minLogDistance = Double.MAX_VALUE;
        int logCount = 0;
        
        System.out.println("EndshopEntity: 搜索中心位置: " + center);
        
        // 扩大搜索范围到 20x40x20，增加 Y 轴范围以覆盖树木高度
        for (int x = -20; x <= 20; x++) {
            for (int y = -10; y <= 40; y++) { // 增加 Y 轴范围，从地下10格到地上40格
                for (int z = -20; z <= 20; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState state = level().getBlockState(pos);
                    
                    if (isLog(state)) {
                        logCount++;
                        double distance = distanceToSqr(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                        if (distance < minLogDistance) {
                            minLogDistance = distance;
                            logTargetPos = pos;
                        }
                    }
                }
            }
        }
        
        // 调试信息
        System.out.println("EndshopEntity: 伐木任务 - 发现 " + logCount + " 个木头方块");
        
        // 优先处理木头
        if (logTargetPos != null) {
            System.out.println("EndshopEntity: 伐木任务 - 木头目标位置: " + logTargetPos + ", 距离: " + Math.sqrt(minLogDistance));
            processLogBlock(logTargetPos);
        } else {
            // 如果没有木头，搜索并处理树叶
            System.out.println("EndshopEntity: 伐木任务 - 没有找到木头，开始搜索树叶");
            BlockPos leafTargetPos = null; // 树叶目标位置
            double minLeafDistance = Double.MAX_VALUE;
            int leafCount = 0;
            
            for (int x = -20; x <= 20; x++) {
                for (int y = -10; y <= 40; y++) { // 增加 Y 轴范围，从地下10格到地上40格
                    for (int z = -20; z <= 20; z++) {
                        BlockPos pos = center.offset(x, y, z);
                        BlockState state = level().getBlockState(pos);
                        
                        if (isLeaf(state)) {
                            leafCount++;
                            double distance = distanceToSqr(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                            if (distance < minLeafDistance) {
                                minLeafDistance = distance;
                                leafTargetPos = pos;
                            }
                        }
                    }
                }
            }
            
            System.out.println("EndshopEntity: 伐木任务 - 发现 " + leafCount + " 个树叶方块");
            
            if (leafTargetPos != null) {
                System.out.println("EndshopEntity: 伐木任务 - 树叶目标位置: " + leafTargetPos + ", 距离: " + Math.sqrt(minLeafDistance));
                processLeafBlock(leafTargetPos);
            } else {
                System.out.println("EndshopEntity: 伐木任务 - 没有找到木头或树叶方块");
            }
        }
    }
    
    /**
     * 处理木头方块
     */
    private void processLogBlock(BlockPos pos) {
        // 检查距离（直接从当前位置到木头）
        double distance = distanceToSqr(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        System.out.println("EndshopEntity: 伐木任务 - 当前距离木头: " + Math.sqrt(distance));
        
        if (distance <= 9.0) { // 距离阈值：3格以内
            // 装备斧头
            System.out.println("EndshopEntity: 伐木任务 - 装备斧头");
            setItemInHand(InteractionHand.MAIN_HAND, Items.IRON_AXE.getDefaultInstance());
            
            // 面向目标
            lookAt(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            
            // 使用斧头砍伐方块
            System.out.println("EndshopEntity: 伐木任务 - 使用斧头砍伐木头: " + pos);
            useItemOn(pos, level().getBlockState(pos));
        } else {
            // 距离太远，移动到木头旁边（不是下方）
            BlockPos targetGroundPos = findReachableGroundNear(pos);
            System.out.println("EndshopEntity: 伐木任务 - 移动到目标地面位置: " + targetGroundPos);
            moveTo(targetGroundPos.getX() + 0.5, targetGroundPos.getY(), targetGroundPos.getZ() + 0.5, 0.6);
        }
    }
    
    /**
     * 找到方块下方的地面位置
     */
    private BlockPos findGroundBelow(BlockPos pos) {
        BlockPos groundPos = pos.below();
        
        // 向下搜索直到找到地面
        while (groundPos.getY() > 0) {
            BlockState state = level().getBlockState(groundPos);
            if (state.isSolid() && !state.is(Blocks.AIR)) {
                return groundPos.above(); // 返回地面上方的位置
            }
            groundPos = groundPos.below();
        }
        
        return pos.below(); // 如果没找到地面，返回原位置下方
    }
    
    /**
     * 在目标方块附近寻找可达的地面位置（用于伐木）
     */
    private BlockPos findReachableGroundNear(BlockPos targetPos) {
        // 在目标位置周围 3 格范围内寻找地面
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                // 跳过目标位置本身
                if (dx == 0 && dz == 0) continue;
                
                BlockPos checkPos = targetPos.offset(dx, 0, dz);
                
                // 向下查找地面
                BlockPos groundPos = checkPos;
                while (groundPos.getY() > 0) {
                    BlockState state = level().getBlockState(groundPos);
                    if (state.isSolid() && !state.is(Blocks.AIR)) {
                        return groundPos.above();
                    }
                    groundPos = groundPos.below();
                }
            }
        }
        
        // 如果没找到合适的地面，返回玩家当前位置
        return blockPosition();
    }
    
    /**
     * 处理树叶方块
     */
    private void processLeafBlock(BlockPos pos) {
        // 移动到树叶下方的地面
        BlockPos groundPos = findGroundBelow(pos);
        System.out.println("EndshopEntity: 伐木任务 - 移动到地面位置: " + groundPos);
        moveTo(groundPos.getX() + 0.5, groundPos.getY(), groundPos.getZ() + 0.5, 0.6);
        
        // 检查距离（从地面到树叶）
        double distance = distanceToSqr(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        System.out.println("EndshopEntity: 伐木任务 - 当前距离树叶: " + Math.sqrt(distance));
        
        if (distance <= 4.0) { // 增加距离阈值
            // 装备剪刀
            System.out.println("EndshopEntity: 伐木任务 - 装备剪刀");
            setItemInHand(InteractionHand.MAIN_HAND, Items.SHEARS.getDefaultInstance());
            
            // 面向目标
            lookAt(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            
            // 使用剪刀剪除树叶
            System.out.println("EndshopEntity: 伐木任务 - 使用剪刀剪除树叶: " + pos);
            useItemOn(pos, level().getBlockState(pos));
        }
    }
    
    /**
     * 检查方块是否为树叶
     */
    private boolean isLeaf(BlockState state) {
        return state.is(Blocks.OAK_LEAVES) ||
               state.is(Blocks.SPRUCE_LEAVES) ||
               state.is(Blocks.BIRCH_LEAVES) ||
               state.is(Blocks.JUNGLE_LEAVES) ||
               state.is(Blocks.ACACIA_LEAVES) ||
               state.is(Blocks.DARK_OAK_LEAVES) ||
               state.is(Blocks.MANGROVE_LEAVES) ||
               state.is(Blocks.CHERRY_LEAVES) ||
               state.is(Blocks.AZALEA_LEAVES) ||
               state.is(Blocks.FLOWERING_AZALEA_LEAVES);
    }

    /**
     * 执行钓鱼任务
     */
    private void executeFishingTask() {
        // 搜索附近的水源
        BlockPos center = blockPosition();
        BlockPos targetPos = null;
        double minDistance = Double.MAX_VALUE;
        
        for (int x = -10; x <= 10; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -10; z <= 10; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState state = level().getBlockState(pos);
                    
                    if (isWater(state)) {
                        double distance = distanceToSqr(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                        if (distance < minDistance) {
                            minDistance = distance;
                            targetPos = pos;
                        }
                    }
                }
            }
        }
        
        if (targetPos != null) {
            // 移动到目标位置
            moveTo(targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5, 0.4);
            
            // 装备钓鱼竿
            setItemInHand(InteractionHand.MAIN_HAND, Items.FISHING_ROD.getDefaultInstance());
        }
    }

    /**
     * 执行打怪任务
     */
    private void executeCombatTask() {
        // 搜索附近的敌人
        Level level = level();
        if (level == null) return;
        
        net.minecraft.world.entity.LivingEntity targetEntity = null;
        double minDistance = Double.MAX_VALUE;
        int enemyCount = 0;
        
        for (net.minecraft.world.entity.LivingEntity entity : level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, getBoundingBox().inflate(16))) {
            if (entity != this && isEnemy(entity)) {
                enemyCount++;
                double distance = distanceToSqr(entity);
                if (distance < minDistance) {
                    minDistance = distance;
                    targetEntity = entity;
                }
            }
        }
        
        // 调试信息
        if (level().getGameTime() % 40 == 0) { // 每 40 tick 输出一次
            System.out.println("EndshopEntity: 打怪任务 - 发现 " + enemyCount + " 个敌人");
            if (targetEntity != null) {
                System.out.println("EndshopEntity: 打怪任务 - 目标敌人: " + targetEntity.getName().getString() + ", 距离: " + Math.sqrt(minDistance));
            } else {
                System.out.println("EndshopEntity: 打怪任务 - 没有找到敌人");
            }
        }
        
        if (targetEntity != null) {
            // 移动到目标位置
            moveTo(targetEntity, 0.6, 3.0f);
            
            // 检查距离
            if (distanceToSqr(targetEntity) <= 2.0) {
                // 装备剑
                setItemInHand(InteractionHand.MAIN_HAND, Items.IRON_SWORD.getDefaultInstance());
                
                // 面向目标
                lookAt(targetEntity.getX(), targetEntity.getY() + 1.0, targetEntity.getZ());
                
                // 使用剑攻击敌人
                useItemOnEntity(targetEntity);
                
                // 调试信息
                System.out.println("EndshopEntity: 打怪任务 - 使用剑攻击敌人: " + targetEntity.getName().getString());
            }
        }
    }

    /**
     * 使用物品攻击实体
     */
    private void useItemOnEntity(net.minecraft.world.entity.LivingEntity entity) {
        // 获取主手物品
        net.minecraft.world.item.ItemStack stack = getItemInHand(InteractionHand.MAIN_HAND);
        
        // 检查物品是否有效
        if (!stack.isEmpty()) {
            // 面向实体
            lookAt(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ());
            
            // 直接攻击实体
            attack(entity);
            
            // 模拟工具损耗
            stack.hurtAndBreak(1, this, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
            
            if (stack.isEmpty()) {
                setItemInHand(InteractionHand.MAIN_HAND, net.minecraft.world.item.ItemStack.EMPTY);
            } else {
                setItemInHand(InteractionHand.MAIN_HAND, stack);
            }
        }
    }

    /**
     * 执行守护任务
     */
    private void executeGuardTask() {
        // 守护当前位置
        BlockPos guardPos = blockPosition();
        
        // 搜索附近的敌人
        Level level = level();
        if (level == null) return;
        
        net.minecraft.world.entity.LivingEntity targetEntity = null;
        double minDistance = Double.MAX_VALUE;
        int enemyCount = 0;
        
        for (net.minecraft.world.entity.LivingEntity entity : level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, new net.minecraft.world.phys.AABB(guardPos).inflate(10))) {
            if (entity != this && isEnemy(entity)) {
                enemyCount++;
                double distance = distanceToSqr(entity);
                if (distance < minDistance) {
                    minDistance = distance;
                    targetEntity = entity;
                }
            }
        }
        
        // 调试信息
        if (level().getGameTime() % 40 == 0) { // 每 40 tick 输出一次
            System.out.println("EndshopEntity: 守护任务 - 发现 " + enemyCount + " 个敌人");
            if (targetEntity != null) {
                System.out.println("EndshopEntity: 守护任务 - 目标敌人: " + targetEntity.getName().getString() + ", 距离: " + Math.sqrt(minDistance));
            } else {
                System.out.println("EndshopEntity: 守护任务 - 没有找到敌人，返回守护位置");
            }
        }
        
        if (targetEntity != null) {
            // 移动到目标位置
            moveTo(targetEntity, 0.6, 3.0f);
            
            // 检查距离
            if (distanceToSqr(targetEntity) <= 2.0) {
                // 装备剑
                setItemInHand(InteractionHand.MAIN_HAND, Items.IRON_SWORD.getDefaultInstance());
                
                // 面向目标
                lookAt(targetEntity.getX(), targetEntity.getY() + 1.0, targetEntity.getZ());
                
                // 使用剑攻击敌人
                useItemOnEntity(targetEntity);
                
                // 调试信息
                System.out.println("EndshopEntity: 守护任务 - 使用剑攻击敌人: " + targetEntity.getName().getString());
            }
        } else {
            // 没有敌人，返回守护位置
            if (distanceToSqr(guardPos.getX() + 0.5, guardPos.getY(), guardPos.getZ() + 0.5) > 1.0) {
                moveTo(guardPos.getX() + 0.5, guardPos.getY(), guardPos.getZ() + 0.5, 0.4);
                // 调试信息
                if (level().getGameTime() % 80 == 0) { // 每 80 tick 输出一次
                    System.out.println("EndshopEntity: 守护任务 - 返回守护位置: " + guardPos);
                }
            }
        }
    }

    /**
     * 执行建筑任务
     */
    private void executeBuildingTask() {
        // 简单的建筑逻辑：在当前位置上方放置方块
        BlockPos pos = blockPosition().above();
        BlockState state = level().getBlockState(pos);
        
        if (state.isAir()) {
            // 移动到目标位置
            moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.4);
            
            // 检查距离
            if (distanceToSqr(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5) <= 2.5) {
                // 装备方块
                setItemInHand(InteractionHand.MAIN_HAND, Items.STONE.getDefaultInstance());
                // 放置方块
                level().setBlock(pos, Blocks.STONE.defaultBlockState(), 3);
            }
        }
    }

    /**
     * 检查方块是否为可挖掘的矿石
     */
    private boolean isMineableOre(BlockState state) {
        return state.is(Blocks.COAL_ORE) ||
               state.is(Blocks.IRON_ORE) ||
               state.is(Blocks.GOLD_ORE) ||
               state.is(Blocks.DIAMOND_ORE) ||
               state.is(Blocks.EMERALD_ORE) ||
               state.is(Blocks.LAPIS_ORE) ||
               state.is(Blocks.REDSTONE_ORE);
    }

    /**
     * 检查方块是否为木头
     */
    private boolean isLog(BlockState state) {
        return state.is(Blocks.OAK_LOG) ||
               state.is(Blocks.SPRUCE_LOG) ||
               state.is(Blocks.BIRCH_LOG) ||
               state.is(Blocks.JUNGLE_LOG) ||
               state.is(Blocks.ACACIA_LOG) ||
               state.is(Blocks.DARK_OAK_LOG) ||
               state.is(Blocks.MANGROVE_LOG) ||
               state.is(Blocks.CHERRY_LOG) ||
               state.is(Blocks.BAMBOO_BLOCK) ||
               // 去皮原木
               state.is(Blocks.STRIPPED_OAK_LOG) ||
               state.is(Blocks.STRIPPED_SPRUCE_LOG) ||
               state.is(Blocks.STRIPPED_BIRCH_LOG) ||
               state.is(Blocks.STRIPPED_JUNGLE_LOG) ||
               state.is(Blocks.STRIPPED_ACACIA_LOG) ||
               state.is(Blocks.STRIPPED_DARK_OAK_LOG) ||
               state.is(Blocks.STRIPPED_MANGROVE_LOG) ||
               state.is(Blocks.STRIPPED_CHERRY_LOG) ||
               // 木头方块
               state.is(Blocks.OAK_WOOD) ||
               state.is(Blocks.SPRUCE_WOOD) ||
               state.is(Blocks.BIRCH_WOOD) ||
               state.is(Blocks.JUNGLE_WOOD) ||
               state.is(Blocks.ACACIA_WOOD) ||
               state.is(Blocks.DARK_OAK_WOOD) ||
               state.is(Blocks.MANGROVE_WOOD) ||
               state.is(Blocks.CHERRY_WOOD) ||
               // 去皮木头方块
               state.is(Blocks.STRIPPED_OAK_WOOD) ||
               state.is(Blocks.STRIPPED_SPRUCE_WOOD) ||
               state.is(Blocks.STRIPPED_BIRCH_WOOD) ||
               state.is(Blocks.STRIPPED_JUNGLE_WOOD) ||
               state.is(Blocks.STRIPPED_ACACIA_WOOD) ||
               state.is(Blocks.STRIPPED_DARK_OAK_WOOD) ||
               state.is(Blocks.STRIPPED_MANGROVE_WOOD) ||
               state.is(Blocks.STRIPPED_CHERRY_WOOD);
    }

    /**
     * 检查方块是否为水
     */
    private boolean isWater(BlockState state) {
        return state.is(Blocks.WATER) || state.is(Blocks.BUBBLE_COLUMN);
    }

    /**
     * 检查实体是否为敌人
     */
    private boolean isEnemy(net.minecraft.world.entity.LivingEntity entity) {
        return entity instanceof net.minecraft.world.entity.monster.Monster;
    }

    /**
     * 选择合适的工具
     */
    private void selectProperTool(BlockState state) {
        Item tool;
        if (state.is(Blocks.COAL_ORE) || state.is(Blocks.IRON_ORE)) {
            tool = Items.STONE_PICKAXE;
        } else if (state.is(Blocks.GOLD_ORE) || state.is(Blocks.LAPIS_ORE) || state.is(Blocks.REDSTONE_ORE)) {
            tool = Items.IRON_PICKAXE;
        } else if (state.is(Blocks.DIAMOND_ORE) || state.is(Blocks.EMERALD_ORE)) {
            tool = Items.DIAMOND_PICKAXE;
        } else {
            tool = Items.WOODEN_PICKAXE;
        }
        setItemInHand(InteractionHand.MAIN_HAND, tool.getDefaultInstance());
    }

    /**
     * 移动到指定位置
     */
    public void moveTo(double x, double y, double z, double speed) {
        PathNavigation navigation = getNavigation();
        if (navigation != null) {
            navigation.moveTo(x, y, z, speed);
        }
    }

    /**
     * 移动到指定实体
     */
    public void moveTo(net.minecraft.world.entity.Entity entity, double speed, float maxTurn) {
        PathNavigation navigation = getNavigation();
        if (navigation != null) {
            navigation.moveTo(entity, speed);
        }
    }

    /**
     * 攻击实体
     */
    private void attack(net.minecraft.world.entity.LivingEntity entity) {
        doHurtTarget(entity);
    }
    
    // ==================== 技能使用方法 ====================
    
    /**
     * 检查是否可以使用构成序列技能
     */
    private boolean canUseCompositionSkill() {
        return skillCooldowns.getOrDefault("admin_composition_sequence", 0) <= 0;
    }
    
    /**
     * 检查是否应该使用构成序列技能
     */
    private boolean shouldUseCompositionSkill(LivingEntity target) {
        // 当敌人数量较多或自身血量较低时使用
        int enemyCount = countNearbyEnemies();
        return enemyCount >= 2 || (getHealth() / getMaxHealth()) < 0.5;
    }
    
    /**
     * 使用构成序列技能
     */
    private void useCompositionSkill() {
        System.out.println("EndshopEntity: 使用技能 - 构成序列");
        
        // 设置冷却时间（10秒 = 200 tick）
        skillCooldowns.put("admin_composition_sequence", 200);
        
        // 获取技能等级
        int level = skillLevels.getOrDefault("admin_composition_sequence", 1);
        
        // 获取技能实例
        Skill skill = SkillRegistry.getSkill("admin_composition_sequence");
        if (skill instanceof AdminSkills.CompositionSequenceSkill compositionSkill) {
            // 计算伤害
            double damageMultiplier = compositionSkill.getDamageMultiplier(level);
            double damage = getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier;
            
            // 攻击周围敌人
            attackNearbyEnemies(damage, 5);
            
            // 回复技力（为终结技能积累能量）
            ultimateEnergy += 50;
            if (ultimateEnergy > maxUltimateEnergy) {
                ultimateEnergy = maxUltimateEnergy;
            }
            
            // 回复全队能量
            // 这里可以添加全队能量回复的逻辑
        }
    }
    
    /**
     * 检查是否可以使用锁闭序列技能
     */
    private boolean canUseLockdownSkill() {
        return skillCooldowns.getOrDefault("admin_lockdown_sequence", 0) <= 0;
    }
    
    /**
     * 检查是否应该使用锁闭序列技能
     */
    private boolean shouldUseLockdownSkill(LivingEntity target) {
        // 当敌人血量较高时使用
        return target.getHealth() / target.getMaxHealth() > 0.5;
    }
    
    /**
     * 使用锁闭序列技能
     */
    private void useLockdownSkill() {
        System.out.println("EndshopEntity: 使用技能 - 锁闭序列");
        
        // 设置冷却时间（16秒 = 320 tick）
        skillCooldowns.put("admin_lockdown_sequence", 320);
        
        // 获取技能等级
        int level = skillLevels.getOrDefault("admin_lockdown_sequence", 1);
        
        // 获取技能实例
        Skill skill = SkillRegistry.getSkill("admin_lockdown_sequence");
        if (skill instanceof AdminSkills.LockdownSequenceSkill lockdownSkill) {
            // 计算伤害
            double damageMultiplier = lockdownSkill.getDamageMultiplier(level);
            double damage = getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier;
            
            // 攻击最近的敌人
            LivingEntity target = findNearestEnemy();
            if (target != null) {
                target.hurt(damageSources().generic(), (float) damage);
                
                // 回复能量（为终结技能积累能量）
                ultimateEnergy += 10;
                if (ultimateEnergy > maxUltimateEnergy) {
                    ultimateEnergy = maxUltimateEnergy;
                }
            }
        }
    }
    
    /**
     * 检查是否可以使用终结技能（轰击序列）
     */
    private boolean canUseUltimateSkill() {
        return ultimateEnergy >= 80 && skillCooldowns.getOrDefault("admin_bombardment_sequence", 0) <= 0;
    }
    
    /**
     * 检查是否应该使用终结技能（轰击序列）
     */
    private boolean shouldUseUltimateSkill(LivingEntity target) {
        // 当敌人数量较多时使用
        int enemyCount = countNearbyEnemies();
        return enemyCount >= 3;
    }
    
    /**
     * 使用终结技能（轰击序列）
     */
    private void useUltimateSkill() {
        System.out.println("EndshopEntity: 使用技能 - 轰击序列");
        
        // 消耗终结能量
        ultimateEnergy -= 80;
        // 设置冷却时间（10秒 = 200 tick）
        skillCooldowns.put("admin_bombardment_sequence", 200);
        
        // 获取技能等级
        int level = skillLevels.getOrDefault("admin_bombardment_sequence", 1);
        
        // 获取技能实例
        Skill skill = SkillRegistry.getSkill("admin_bombardment_sequence");
        if (skill instanceof AdminSkills.BombardmentSequenceSkill bombardmentSkill) {
            // 计算伤害
            double damageMultiplier = bombardmentSkill.getDamageMultiplier(level);
            double damage = getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier;
            
            // 攻击前方扇形范围内的敌人
            attackForwardEnemies(damage, 8, 60); // 8格范围，60度角
            
            // 获得伤害免疫
            // 这里可以添加伤害免疫的逻辑
        }
    }
    
    /**
     * 攻击周围的敌人
     */
    private void attackNearbyEnemies(double damage, double radius) {
        Level level = level();
        if (level == null) return;
        
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(radius))) {
            if (entity != this && isEnemy(entity)) {
                entity.hurt(damageSources().generic(), (float) damage);
            }
        }
    }
    
    /**
     * 攻击前方扇形范围内的敌人
     */
    private void attackForwardEnemies(double damage, double radius, float angle) {
        Level level = level();
        if (level == null) return;
        
        double yaw = getYRot() * Math.PI / 180.0;
        Vec3 forward = new Vec3(Math.sin(yaw), 0, Math.cos(yaw));
        
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(radius))) {
            if (entity != this && isEnemy(entity)) {
                Vec3 direction = new Vec3(entity.getX() - getX(), 0, entity.getZ() - getZ()).normalize();
                double dot = forward.dot(direction);
                double angleRad = Math.acos(dot) * 180.0 / Math.PI;
                
                if (angleRad <= angle / 2) {
                    entity.hurt(damageSources().generic(), (float) damage);
                }
            }
        }
    }
    
    /**
     * 计算附近敌人的数量
     */
    private int countNearbyEnemies() {
        Level level = level();
        if (level == null) return 0;
        
        int count = 0;
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(10))) {
            if (entity != this && isEnemy(entity)) {
                count++;
            }
        }
        return count;
    }
}
