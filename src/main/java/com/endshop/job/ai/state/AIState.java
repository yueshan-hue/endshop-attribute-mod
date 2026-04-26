package com.endshop.job.ai.state;

import com.endshop.job.ai.statemachine.IAIState;

/**
 * AI 状态枚举 - 参考 Minecolonies 的 AIWorkerState
 * 
 * 状态设计原则：
 * 1. 工作状态 (WORKING) 不应该被打断，除非紧急事件
 * 2. 搜索、移动等状态可以被高优先级事件打断
 * 3. IDLE 是默认的空闲状态
 */
public enum AIState implements IAIState {
    IDLE("idle", true, true),              // 空闲 - 可以被中断和切换任务
    INIT("init", false, false),            // 初始化 - 不可中断
    PREPARING("preparing", true, true),    // 准备 - 可以被中断
    SEARCHING("searching", true, true),    // 搜索目标 - 可以被中断
    MOVING("moving", true, true),          // 移动到目标 - 可以被中断
    WORKING("working", false, false),      // 执行任务 - 不应被打断
    GATHERING("gathering", true, true),    // 收集物品 - 可以被中断
    RETURNING("returning", true, true),    // 返回基地 - 可以被中断
    NO_TARGET_FOUND("no_target_found", true, true), // 未找到目标 - 可以被中断
    NEEDS_ITEM("needs_item", true, true),  // 需要物品 - 可以被中断
    INVENTORY_FULL("inventory_full", true, true); // 背包已满 - 可以被中断
    
    private final String name;
    private final boolean interruptible;  // 是否可以被中断（用于紧急事件）
    private final boolean canSwitchTask;  // 是否可以切换任务
    
    AIState(String name, boolean interruptible, boolean canSwitchTask) {
        this.name = name;
        this.interruptible = interruptible;
        this.canSwitchTask = canSwitchTask;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public boolean isOkayToEat() {
        return interruptible;
    }
    
    @Override
    public boolean canSwitchTask() {
        return canSwitchTask;
    }
}
