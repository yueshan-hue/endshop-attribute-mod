package com.endshop.job.ai.statemachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 增强的 Tick Rate 状态机 - 参考 Minecolonies 的 TickRateStateMachine
 * 支持：
 * 1. 基于 Tick Rate 的状态转换
 * 2. 全局转换（在所有状态下都检查）
 * 3. 高优先级事件处理（可打断当前状态）
 * 4. 状态组转换
 */
public class TickRateStateMachine<S extends IAIState> {
    
    // 当前状态
    private S currentState;
    
    // 异常处理器
    private final Consumer<RuntimeException> exceptionHandler;
    
    // 默认 Tick Rate
    private final int defaultTickRate;
    
    // 状态转换映射（从某个状态到其他状态的转换）
    private final Map<S, List<AITransition<S>>> stateTransitions = new HashMap<>();
    
    // 全局转换（在所有状态下都检查）
    private final List<AITransition<S>> globalTransitions = new ArrayList<>();
    
    // 高优先级转换（每 tick 都检查，可以中断当前状态）
    private final List<AITransition<S>> highPriorityTransitions = new ArrayList<>();
    
    // 当前 tick 计数器
    private int currentTick = 0;
    
    public TickRateStateMachine(S initialState, Consumer<RuntimeException> exceptionHandler, int defaultTickRate) {
        this.currentState = initialState;
        this.exceptionHandler = exceptionHandler;
        this.defaultTickRate = defaultTickRate;
    }
    
    /**
     * 注册状态转换
     */
    public void registerTransition(S fromState, AITransition<S> transition) {
        stateTransitions.computeIfAbsent(fromState, k -> new ArrayList<>()).add(transition);
    }
    
    /**
     * 注册全局转换（在所有状态下都检查）
     */
    public void registerGlobalTransition(AITransition<S> transition) {
        globalTransitions.add(transition);
    }
    
    /**
     * 注册高优先级转换（每 tick 都检查，可以中断当前状态）
     */
    public void registerHighPriorityTransition(AITransition<S> transition) {
        highPriorityTransitions.add(transition);
    }
    
    /**
     * 执行一个 tick
     */
    public void tick() {
        currentTick++;
        
        try {
            // 1. 首先检查高优先级转换（每 tick 都执行）
            for (AITransition<S> transition : highPriorityTransitions) {
                if (transition.shouldExecute()) {
                    S nextState = transition.execute();
                    if (nextState != null) {
                        currentState = nextState;
                        return; // 高优先级转换执行后直接返回
                    }
                }
            }
            
            // 2. 检查全局转换
            for (AITransition<S> transition : globalTransitions) {
                if (transition.shouldExecute()) {
                    S nextState = transition.execute();
                    if (nextState != null) {
                        currentState = nextState;
                        return;
                    }
                }
            }
            
            // 3. 检查当前状态的转换
            List<AITransition<S>> transitions = stateTransitions.get(currentState);
            if (transitions != null) {
                for (AITransition<S> transition : transitions) {
                    if (transition.shouldExecute()) {
                        S nextState = transition.execute();
                        if (nextState != null) {
                            currentState = nextState;
                        }
                        break; // 执行第一个匹配的转换后退出
                    }
                }
            }
        } catch (RuntimeException e) {
            exceptionHandler.accept(e);
        }
    }
    
    /**
     * 获取当前状态
     */
    public S getCurrentState() {
        return currentState;
    }
    
    /**
     * 切换到指定状态
     */
    public void setCurrentState(S state) {
        this.currentState = state;
    }
    
    /**
     * 重置状态机
     */
    public void reset() {
        currentTick = 0;
    }
    
    /**
     * 获取默认 Tick Rate
     */
    public int getDefaultTickRate() {
        return defaultTickRate;
    }
}
