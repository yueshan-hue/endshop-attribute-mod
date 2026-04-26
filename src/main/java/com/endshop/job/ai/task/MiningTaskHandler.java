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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 挖矿任务处理器 - 参考 Minecolonies 的 EntityAIStructureMiner
 * 
 * 状态机流程:
 * IDLE -> SEARCHING_ORE -> MOVING_TO_ORE -> MINING -> GATHERING -> IDLE
 */
public class MiningTaskHandler extends BaseTaskHandler {
    
    // 搜索范围
    private static final int SEARCH_RANGE = 25;
    private static final int MAX_DEPTH = 60; // 最大挖掘深度
    
    // 工作范围（挖矿需要更大的范围，因为矿石可能在脚下/头顶）
    private static final int MIN_WORKING_RANGE = 4;
    
    // 延迟设置
    private static final int WAIT_BEFORE_SEARCH = 300;
    private static final int GATHERING_DELAY = 40;
    
    // 矿石列表
    @Nullable
    private List<BlockPos> orePositions;
    
    @Nullable
    private BlockPos currentOreTarget;
    
    @Nullable
    private BlockPos workFrom; // 工作位置缓存
    
    public MiningTaskHandler(@NotNull EndshopEntity entity) {
        super(entity);
        this.searchRange = SEARCH_RANGE;
        this.orePositions = new ArrayList<>();
    }
    
    @Override
    public void initialize() {
        super.initialize();
        orePositions = new ArrayList<>();
        currentOreTarget = null;
        workFrom = null;
        firstSearch = true;
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
                // 没找到矿石，等待后重新搜索
                if (hasNotDelayed(WAIT_BEFORE_SEARCH)) {
                    return AIState.NO_TARGET_FOUND;
                }
                firstSearch = true; // 重置为首次搜索
                setCurrentState(AIState.SEARCHING);
                return AIState.SEARCHING;
            
            default:
                setCurrentState(AIState.IDLE);
                return AIState.IDLE;
        }
    }
    
    /**
     * 空闲状态：检查是否需要搜索新矿石
     */
    private AIState handleIdle() {
        if (currentOreTarget == null || !hasOresNearby()) {
            setCurrentState(AIState.SEARCHING);
            return AIState.SEARCHING;
        }
        
        setCurrentState(AIState.MOVING);
        return AIState.MOVING;
    }
    
    // 是否是首次搜索（避免首次无谓等待）
    private boolean firstSearch = true;
    
    /**
     * 搜索状态：寻找附近的矿石
     */
    private AIState handleSearching() {
        // 只有在上次未找到目标时才等待，首次或重置后直接搜索
        if (!firstSearch && hasNotDelayed(WAIT_BEFORE_SEARCH)) {
            return AIState.SEARCHING;
        }
        firstSearch = false;
        
        // 执行搜索
        orePositions = findOresInArea();
        
        if (orePositions.isEmpty()) {
            System.out.println("MiningTaskHandler: 未找到矿石，等待后重试");
            return AIState.NO_TARGET_FOUND;
        }
        
        // 按距离排序，优先挖掘最近的
        orePositions.sort(Comparator.comparingDouble(pos -> 
            entity.distanceToSqr(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5)));
        
        currentOreTarget = orePositions.get(0);
        System.out.println("MiningTaskHandler: 找到矿石目标 - " + currentOreTarget + "，共 " + orePositions.size() + " 个");
        setCurrentState(AIState.MOVING);
        return AIState.MOVING;
    }
    
    /**
     * 移动状态：移动到矿石位置
     */
    private AIState handleMoving() {
        if (currentOreTarget == null) {
            setCurrentState(AIState.SEARCHING);
            return AIState.SEARCHING;
        }
        
        // 计算工作位置
        if (workFrom == null || !isReachable(workFrom)) {
            workFrom = findWorkingPosition(currentOreTarget);
        }
        
        // 移动到工作位置
        if (!isAtWorkPosition()) {
            entity.moveTo(workFrom.getX() + 0.5, workFrom.getY(), workFrom.getZ() + 0.5, 0.6);
            checkIfStuck();
            return AIState.MOVING;
        }
        
        // 到达位置，开始挖掘
        setCurrentState(AIState.WORKING);
        return AIState.WORKING;
    }
    
    /**
     * 工作状态：挖掘矿石
     */
    private AIState handleWorking() {
        if (currentOreTarget == null) {
            setCurrentState(AIState.SEARCHING);
            return AIState.SEARCHING;
        }
        
        // 检查方块是否还存在（可能已经被其他玩家挖了）
        BlockState state = entity.level().getBlockState(currentOreTarget);
        if (!isOre(state)) {
            // 矿石不存在，移除并找下一个
            orePositions.remove(currentOreTarget);
            currentOreTarget = orePositions.isEmpty() ? null : orePositions.get(0);
            workFrom = null;
            setCurrentState(AIState.MOVING);
            return AIState.MOVING;
        }
        
        // 挖掘矿石
        if (mineBlock(currentOreTarget)) {
            // 挖掘完成
            orePositions.remove(currentOreTarget);
            currentOreTarget = orePositions.isEmpty() ? null : orePositions.get(0);
            workFrom = null;
            
            if (currentOreTarget != null) {
                // 还有更多矿石，继续挖掘
                setCurrentState(AIState.MOVING);
                return AIState.MOVING;
            } else {
                // 没有更多矿石，进入收集阶段
                setCurrentState(AIState.GATHERING);
                setDelay(GATHERING_DELAY);
                return AIState.GATHERING;
            }
        }
        
        // 仍在挖掘中
        return AIState.WORKING;
    }
    
    /**
     * 收集状态：收集掉落的物品
     */
    private AIState handleGathering() {
        if (hasNotDelayed(GATHERING_DELAY)) {
            return AIState.GATHERING;
        }
        
        gatherItems();
        
        if (itemsToPickup == null || itemsToPickup.isEmpty()) {
            currentOreTarget = null;
            workFrom = null;
            setCurrentState(AIState.IDLE);
            return AIState.IDLE;
        }
        
        return AIState.GATHERING;
    }
    
    /**
     * 在区域内搜索矿石
     * 只收集"可达"的矿石：矿石周围至少有一个可以站立的空气方块
     */
    @NotNull
    private List<BlockPos> findOresInArea() {
        List<BlockPos> foundOres = new ArrayList<>();
        BlockPos center = entity.blockPosition();
        
        for (int x = -searchRange; x <= searchRange; x++) {
            for (int y = -MAX_DEPTH; y <= 10; y++) {
                for (int z = -searchRange; z <= searchRange; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState state = entity.level().getBlockState(pos);
                    
                    if (isOre(state) && hasReachableAdjacentPosition(pos)) {
                        foundOres.add(pos);
                    }
                }
            }
        }
        
        return foundOres;
    }
    
    /**
     * 检查矿石周围是否有可以站立挖掘的位置
     * （矿石必须暴露在某个可通行的空间旁边）
     */
    private boolean hasReachableAdjacentPosition(BlockPos orePos) {
        // 检查矿石六面中是否有可通行面
        BlockPos[] adjacent = {
            orePos.north(), orePos.south(), orePos.east(), orePos.west(),
            orePos.above(), orePos.below()
        };
        for (BlockPos adj : adjacent) {
            BlockState adjState = entity.level().getBlockState(adj);
            if (!adjState.isSolid() || adjState.isAir()) {
                // 该面是可通行的，检查附近能不能站
                BlockPos groundPos = findGroundBelow(adj);
                if (groundPos != null && isReachable(groundPos)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 检查附近是否有未挖掘的矿石
     */
    private boolean hasOresNearby() {
        if (orePositions == null || orePositions.isEmpty()) {
            return false;
        }
        
        // 清理已经不存在的矿石
        orePositions.removeIf(pos -> !isOre(entity.level().getBlockState(pos)));
        
        return !orePositions.isEmpty();
    }
    
    /**
     * 找到合适的工作位置
     * 矿石可能嵌在石头里，需要找矿石可见面（有空气的面）旁边能站的位置
     */
    @NotNull
    private BlockPos findWorkingPosition(BlockPos targetPos) {
        // 优先找矿石旁边暴露的可通行面
        BlockPos[] adjacent = {
            targetPos.north(), targetPos.south(),
            targetPos.east(), targetPos.west(),
            targetPos.above(), targetPos.below()
        };
        for (BlockPos adj : adjacent) {
            BlockState adjState = entity.level().getBlockState(adj);
            if (!adjState.isSolid() || adjState.isAir()) {
                // 这一面是空的，找这里的地面
                BlockPos groundPos = findGroundBelow(adj);
                if (groundPos != null && isReachable(groundPos)) {
                    return groundPos;
                }
                // 如果 adj 本身就是可站位置（比如矿石正上方的空气+下方是固体）
                if (isReachable(adj)) {
                    return adj;
                }
            }
        }
        
        // 扩大搜索：矿石周围2格范围内找可达地面
        for (int radius = 1; radius <= 3; radius++) {
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
        
        // fallback：实体当前位置
        return entity.blockPosition();
    }
    
    /**
     * 检查工作位置是否可达
     */
    private boolean isReachable(BlockPos pos) {
        if (pos == null) return false;
        
        // 工作位置本身必须是可通行的
        BlockState standState = entity.level().getBlockState(pos);
        if (!standState.isAir() && standState.isSolid()) {
            return false;
        }
        
        // 脚下必须是固体地面
        BlockState groundState = entity.level().getBlockState(pos.below());
        return groundState.isSolid() && !groundState.isAir();
    }
    
    /**
     * 检查是否在工作位置
     */
    private boolean isAtWorkPosition() {
        if (workFrom == null) return false;
        
        double distance = entity.distanceToSqr(workFrom.getX() + 0.5, workFrom.getY(), workFrom.getZ() + 0.5);
        return distance <= MIN_WORKING_RANGE * MIN_WORKING_RANGE;
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
    
    @Override
    protected Item getRequiredTool(BlockState state) {
        if (isHardOre(state)) {
            return Items.IRON_PICKAXE;
        } else {
            return Items.STONE_PICKAXE;
        }
    }
    
    @Override
    protected boolean isItemWorthPickingUp(net.minecraft.world.item.ItemStack stack) {
        // 收集所有矿物和原石
        return stack.is(net.minecraft.tags.ItemTags.COALS) ||
               stack.getItem() == Items.RAW_IRON ||
               stack.getItem() == Items.RAW_GOLD ||
               stack.getItem() == Items.DIAMOND ||
               stack.getItem() == Items.EMERALD ||
               stack.getItem() == Items.REDSTONE ||
               stack.getItem() == Items.LAPIS_LAZULI ||
               stack.getItem() == Items.COBBLESTONE;
    }
    
    /**
     * 判断是否为需要高级工具的矿石
     */
    private boolean isHardOre(BlockState state) {
        return state.is(Blocks.DIAMOND_ORE) ||
               state.is(Blocks.EMERALD_ORE) ||
               state.is(Blocks.GOLD_ORE) ||
               state.is(Blocks.REDSTONE_ORE) ||
               state.is(Blocks.LAPIS_ORE) ||
               state.is(Blocks.ANCIENT_DEBRIS);
    }
    
    /**
     * 检查方块是否为矿石（使用标签）
     */
    private boolean isOre(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE) && 
               (state.is(Blocks.COAL_ORE) ||
                state.is(Blocks.IRON_ORE) ||
                state.is(Blocks.COPPER_ORE) ||
                state.is(Blocks.GOLD_ORE) ||
                state.is(Blocks.REDSTONE_ORE) ||
                state.is(Blocks.LAPIS_ORE) ||
                state.is(Blocks.DIAMOND_ORE) ||
                state.is(Blocks.EMERALD_ORE) ||
                state.is(Blocks.NETHER_QUARTZ_ORE) ||
                state.is(Blocks.NETHER_GOLD_ORE) ||
                state.is(Blocks.ANCIENT_DEBRIS));
    }
}
