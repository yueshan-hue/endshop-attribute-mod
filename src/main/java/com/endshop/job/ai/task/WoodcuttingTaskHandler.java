package com.endshop.job.ai.task;

import com.endshop.job.ai.state.AIState;
import com.endshop.job.ai.task.base.BaseTaskHandler;
import com.endshop.job.entity.EndshopEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 伐木任务处理器 - 参考 Minecolonies 的 EntityAIWorkLumberjack
 * 
 * 状态机流程:
 * IDLE -> SEARCHING_TREE -> MOVING_TO_TREE -> CHOPPING -> GATHERING -> IDLE
 */
public class WoodcuttingTaskHandler extends BaseTaskHandler {
    
    // 搜索范围
    private static final int SEARCH_RANGE = 50; // 玩家周围50格内搜索树木
    private static final int SEARCH_INCREMENT = 5;
    private static final int SEARCH_LIMIT = 100;
    
    // 工作范围（高处树木需要更大的判定范围）
    private static final int MIN_WORKING_RANGE = 5;
    
    /**
     * 砍伐范围
     */
    // 不再限制高度，追踪整棵树
    private static final int CHOP_RADIUS_XZ = 2; // XZ 方向半径2（共5格，覆盖大树）
    private static final int MAX_TREE_HEIGHT = 40; // 最大树高40格
    
    // 延迟设置
    private static final int WAIT_BEFORE_SEARCH = 400;
    private static final int GATHERING_DELAY = 60;
    
    // 当前搜索增量
    private int searchIncrement = 0;
    
    // 树木相关
    @Nullable
    private Tree currentTree;
    
    @Nullable
    private BlockPos workFrom; // 工作位置缓存
    
    /**
     * 树木数据结构
     */
    private static class Tree {
        BlockPos baseLocation; // 树的底部位置（中心点）
        List<BlockPos> blocksToChop = new ArrayList<>(); // 所有需要砍伐的方块位置（木头+树叶）
        boolean isComplete = false;
        
        public Tree(BlockPos base) {
            this.baseLocation = base;
        }
        
        public boolean hasBlocks() {
            return !blocksToChop.isEmpty();
        }
        
        @Nullable
        public BlockPos getNextBlock() {
            return hasBlocks() ? blocksToChop.get(0) : null;
        }
        
        public void removeNextBlock() {
            if (hasBlocks()) {
                blocksToChop.remove(0);
            }
        }
    }
    
    public WoodcuttingTaskHandler(@NotNull EndshopEntity entity) {
        super(entity);
        this.searchRange = SEARCH_RANGE;
    }
    
    @Override
    public void initialize() {
        super.initialize();
        currentTree = null;
        workFrom = null;
        searchIncrement = 0;
    }
    
    @Override
    public AIState handle() {
        // 状态机主循环
        switch (currentState) {
            case IDLE:
                return handleIdle();
            
            case SEARCHING:
                return handleSearching();
            
            case MOVING:
                return handleMoving();
            
            case WORKING:
                return handleWorking();
            
            case GATHERING:
                return handleGathering();
            
            case NO_TARGET_FOUND:
                // 没找到目标，等待后重新搜索
                if (hasNotDelayed(WAIT_BEFORE_SEARCH)) {
                    return AIState.NO_TARGET_FOUND;
                }
                setCurrentState(AIState.SEARCHING);
                return AIState.SEARCHING;
            
            default:
                setCurrentState(AIState.IDLE);
                return AIState.IDLE;
        }
    }
    
    /**
     * 空闲状态：搜索树木
     */
    private AIState handleIdle() {
        // 检查是否有待处理的树
        if (currentTree == null) {
            setCurrentState(AIState.SEARCHING);
            return AIState.SEARCHING;
        }
        
        // 有树，开始砍伐
        setCurrentState(AIState.MOVING);
        return AIState.MOVING;
    }
    
    /**
     * 搜索状态：寻找附近的树木
     */
    private AIState handleSearching() {
        // 调试输出
        System.out.println("WoodcuttingTaskHandler: 搜索树木 - 当前搜索增量: " + searchIncrement);
        
        // 如果之前没有找到树，等待一段时间再搜索
        if (searchIncrement > 0 && hasNotDelayed(WAIT_BEFORE_SEARCH)) {
            System.out.println("WoodcuttingTaskHandler: 等待中...");
            return AIState.SEARCHING;
        }
        
        // 执行搜索
        currentTree = findNearestTree();
        
        if (currentTree == null) {
            // 没有找到树，增加搜索范围
            System.out.println("WoodcuttingTaskHandler: 没有找到树木，增加搜索范围");
            if (searchIncrement + SEARCH_RANGE <= SEARCH_LIMIT) {
                searchIncrement += SEARCH_INCREMENT;
                setDelay(WAIT_BEFORE_SEARCH);
                return AIState.SEARCHING;
            } else {
                // 达到搜索上限，返回空闲
                System.out.println("WoodcuttingTaskHandler: 达到搜索上限，返回空闲");
                searchIncrement = 0;
                return AIState.NO_TARGET_FOUND;
            }
        }
        
        // 找到树，重置搜索增量
        searchIncrement = 0;
        System.out.println("WoodcuttingTaskHandler: 找到树木 - 位置: " + currentTree.baseLocation + 
                          ", 待砍伐方块数量: " + currentTree.blocksToChop.size());
        setCurrentState(AIState.MOVING);
        return AIState.MOVING;
    }
    
    /**
     * 移动状态：移动到树木位置
     */
    private AIState handleMoving() {
        if (currentTree == null) {
            System.out.println("WoodcuttingTaskHandler: 树木为空，重新搜索");
            setCurrentState(AIState.SEARCHING);
            return AIState.SEARCHING;
        }
        
        // 获取目标位置（第一个需要砍伐的方块）
        BlockPos targetPos = currentTree.getNextBlock();
        
        if (targetPos == null) {
            // 树已砍完，进入收集阶段
            System.out.println("WoodcuttingTaskHandler: 树已砍完，进入收集阶段");
            setCurrentState(AIState.GATHERING);
            setDelay(GATHERING_DELAY);
            return AIState.GATHERING;
        }
        
        System.out.println("WoodcuttingTaskHandler: 移动到目标 - " + targetPos);
        
        // 计算工作位置
        if (workFrom == null || !isReachable(workFrom)) {
            workFrom = findWorkingPosition(targetPos);
            System.out.println("WoodcuttingTaskHandler: 工作位置 - " + workFrom);
        }
        
        // 移动到工作位置
        if (!isAtWorkPosition()) {
            entity.moveTo(workFrom.getX() + 0.5, workFrom.getY(), workFrom.getZ() + 0.5, 0.6);
            checkIfStuck();
            return AIState.MOVING;
        }
        
        // 到达位置，开始工作
        System.out.println("WoodcuttingTaskHandler: 到达工作位置，开始砍伐");
        setCurrentState(AIState.WORKING);
        return AIState.WORKING;
    }
    
    /**
     * 工作状态：砍伐3x3x5范围内的木头和树叶
     */
    private AIState handleWorking() {
        if (currentTree == null) {
            System.out.println("WoodcuttingTaskHandler: 树木为空，重新搜索");
            setCurrentState(AIState.SEARCHING);
            return AIState.SEARCHING;
        }
        
        // 获取下一个需要砍伐的方块
        BlockPos nextBlock = currentTree.getNextBlock();
        if (nextBlock != null) {
            System.out.println("WoodcuttingTaskHandler: 砍伐方块 - " + nextBlock);
            if (mineBlock(nextBlock)) {
                // 挖掘完成，移除该方块
                System.out.println("WoodcuttingTaskHandler: 方块砍伐完成，剩余: " + (currentTree.blocksToChop.size() - 1));
                currentTree.removeNextBlock();
                workFrom = null; // 重置工作位置
                setCurrentState(AIState.MOVING);
                return AIState.MOVING;
            }
            // 仍在挖掘中
            return AIState.WORKING;
        }
        
        // 所有方块处理完毕，进入收集阶段
        System.out.println("WoodcuttingTaskHandler: 树处理完毕，进入收集阶段");
        setCurrentState(AIState.GATHERING);
        setDelay(GATHERING_DELAY);
        return AIState.GATHERING;
    }
    
    /**
     * 收集状态：收集掉落的物品
     */
    private AIState handleGathering() {
        // 延迟结束后开始收集
        if (hasNotDelayed(GATHERING_DELAY)) {
            return AIState.GATHERING;
        }
        
        // 收集物品
        gatherItems();
        
        // 收集完成后回到空闲状态
        if (itemsToPickup == null || itemsToPickup.isEmpty()) {
            currentTree = null;
            workFrom = null;
            setCurrentState(AIState.IDLE);
            return AIState.IDLE;
        }
        
        return AIState.GATHERING;
    }
    
    /**
     * 查找最近的树木（以玩家为中心）
     */
    @Nullable
    private Tree findNearestTree() {
        BlockPos playerPos = entity.blockPosition();
        double minDistance = Double.MAX_VALUE;
        Tree nearestTree = null;
        
        // 以玩家为中心，搜索50格范围内的所有木头方块
        for (int x = -SEARCH_RANGE; x <= SEARCH_RANGE; x++) {
            for (int y = -5; y <= 30; y++) {
                for (int z = -SEARCH_RANGE; z <= SEARCH_RANGE; z++) {
                    BlockPos pos = playerPos.offset(x, y, z);
                    BlockState state = entity.level().getBlockState(pos);
                    
                    if (isLog(state)) {
                        // 找到木头，构建以该木头为中心的3x3x5砍伐范围
                        Tree tree = buildTree(pos);
                        if (tree != null && tree.hasBlocks()) {
                            double distance = entity.distanceToSqr(Vec3.atCenterOf(tree.baseLocation));
                            if (distance < minDistance) {
                                minDistance = distance;
                                nearestTree = tree;
                            }
                        }
                    }
                }
            }
        }
        
        return nearestTree;
    }
    
    /**
     * 从单个木头位置构建砍伐区域（从底部追踪到树顶，包含所有连通木头和树叶）
     * 使用 BFS/DFS 沿木头方块向上扩展，确保高大树木完整砍伐
     */
    @Nullable
    private Tree buildTree(BlockPos logPos) {
        Tree tree = new Tree(logPos);
        
        // 先向下找到树根（最底部的木头）
        BlockPos root = logPos;
        while (true) {
            BlockPos below = root.below();
            if (isLog(entity.level().getBlockState(below))) {
                root = below;
            } else {
                break;
            }
        }
        
        // 从树根用BFS向上收集所有连通的木头和树叶
        java.util.Queue<BlockPos> toVisit = new java.util.LinkedList<>();
        java.util.Set<BlockPos> visited = new java.util.HashSet<>();
        toVisit.add(root);
        visited.add(root);
        
        int baseY = root.getY();
        
        while (!toVisit.isEmpty()) {
            BlockPos current = toVisit.poll();
            
            // 超过最大树高则停止
            if (current.getY() - baseY > MAX_TREE_HEIGHT) continue;
            
            BlockState curState = entity.level().getBlockState(current);
            if (isLog(curState) || isLeaf(curState)) {
                tree.blocksToChop.add(current);
            }
            
            // 如果是木头，向所有6个方向扩展（树干可能有分叉）
            if (isLog(curState)) {
                for (BlockPos neighbor : new BlockPos[] {
                    current.above(), current.north(), current.south(),
                    current.east(), current.west()
                    // 不向下扩展，避免重复收集和无限循环
                }) {
                    if (!visited.contains(neighbor)) {
                        BlockState nState = entity.level().getBlockState(neighbor);
                        if (isLog(nState) || isLeaf(nState)) {
                            visited.add(neighbor);
                            toVisit.add(neighbor);
                        }
                    }
                }
                // 在XZ范围内也查找树叶（树叶可能不紧邻木头）
                for (int dx = -CHOP_RADIUS_XZ; dx <= CHOP_RADIUS_XZ; dx++) {
                    for (int dz = -CHOP_RADIUS_XZ; dz <= CHOP_RADIUS_XZ; dz++) {
                        if (dx == 0 && dz == 0) continue;
                        BlockPos leafCheck = current.offset(dx, 0, dz);
                        if (!visited.contains(leafCheck)) {
                            BlockState lState = entity.level().getBlockState(leafCheck);
                            if (isLeaf(lState)) {
                                visited.add(leafCheck);
                                toVisit.add(leafCheck);
                            }
                        }
                        // 还要查找斜上方的树叶
                        for (int dy = 1; dy <= 3; dy++) {
                            BlockPos leafUp = current.offset(dx, dy, dz);
                            if (!visited.contains(leafUp)) {
                                BlockState luState = entity.level().getBlockState(leafUp);
                                if (isLeaf(luState)) {
                                    visited.add(leafUp);
                                    toVisit.add(leafUp);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (!tree.hasBlocks()) {
            return null;
        }
        
        // 按Y坐标排序，从下往上砍（优先挖低处，不被树叶阻挡）
        tree.blocksToChop.sort((a, b) -> {
            int yCompare = Integer.compare(a.getY(), b.getY());
            if (yCompare != 0) return yCompare;
            return Double.compare(
                entity.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(a)),
                entity.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(b))
            );
        });
        
        return tree;
    }
    
    /**
     * 找到合适的工作位置
     * 优先找目标木头正下方的地面，这样实体可以原地向上砍
     */
    @NotNull
    private BlockPos findWorkingPosition(BlockPos targetPos) {
        // 首先尝试目标方块正下方（可以原地向上砍）
        BlockPos directBelow = findGroundBelow(targetPos);
        if (directBelow != null && isReachable(directBelow)) {
            return directBelow;
        }
        
        // 找不到正下方的，在周围找
        for (int radius = 1; radius <= 4; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) != radius && Math.abs(dz) != radius) continue;
                    
                    BlockPos checkPos = targetPos.offset(dx, 0, dz);
                    BlockPos groundPos = findGroundBelow(checkPos);
                    
                    if (groundPos != null && isReachable(groundPos)) {
                        return groundPos;
                    }
                }
            }
        }
        
        // 如果找不到合适位置，返回实体当前位置下方的地面
        BlockPos fallback = findGroundBelow(targetPos);
        return fallback != null ? fallback : entity.blockPosition();
    }
    
    /**
     * 找到方块下方的地面位置
     */
    @Nullable
    private BlockPos findGroundBelow(BlockPos pos) {
        BlockPos groundPos = pos.below();
        
        // 向下搜索直到找到地面
        while (groundPos.getY() > entity.level().getMinBuildHeight()) {
            BlockState state = entity.level().getBlockState(groundPos);
            if (state.isSolid() && !state.isAir()) {
                return groundPos.above();
            }
            groundPos = groundPos.below();
        }
        
        return null;
    }
    
    /**
     * 检查工作位置是否可达（该位置应当是可以站立的空气位置，
     * 并且其下方是固体方块作为地面）
     */
    private boolean isReachable(BlockPos pos) {
        if (pos == null) return false;
        
        // 工作位置本身必须是可通行的（非固体、非基岩）
        BlockState standState = entity.level().getBlockState(pos);
        if (!standState.isAir() && standState.isSolid()) {
            return false;
        }
        
        // 脚下一格必须是固体地面（可以站立）
        BlockState groundState = entity.level().getBlockState(pos.below());
        return groundState.isSolid() && !groundState.isAir();
    }
    
    /**
     * 检查是否在工作位置
     * 只检查水平距离，忽略高度差（实体站地面，树干可能在高空）
     */
    private boolean isAtWorkPosition() {
        if (workFrom == null) return false;
        
        // 只比较XZ平面距离
        double dx = entity.getX() - (workFrom.getX() + 0.5);
        double dz = entity.getZ() - (workFrom.getZ() + 0.5);
        double horizontalDistSq = dx * dx + dz * dz;
        return horizontalDistSq <= MIN_WORKING_RANGE * MIN_WORKING_RANGE;
    }
    
    @Override
    protected Item getRequiredTool(BlockState state) {
        if (isLog(state)) {
            return Items.IRON_AXE;
        } else if (isLeaf(state)) {
            return Items.SHEARS;
        }
        return null;
    }
    
    @Override
    protected boolean isItemWorthPickingUp(net.minecraft.world.item.ItemStack stack) {
        // 只收集木头、树苗和苹果
        return stack.is(net.minecraft.tags.ItemTags.LOGS) ||
               stack.is(net.minecraft.tags.ItemTags.SAPLINGS) ||
               stack.getItem() == Items.APPLE;
    }
    
    /**
     * 检查方块是否为木头（使用标签）
     */
    private boolean isLog(BlockState state) {
        return state.is(BlockTags.LOGS);
    }
    
    /**
     * 检查方块是否为树叶（使用标签）
     */
    private boolean isLeaf(BlockState state) {
        return state.is(BlockTags.LEAVES);
    }
}
