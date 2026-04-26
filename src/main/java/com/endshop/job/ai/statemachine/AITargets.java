package com.endshop.job.ai.statemachine;

/**
 * AI 转换工厂 - 参考 Minecolonies 的 AITarget 创建方式
 * 提供便捷的方法来创建 AI 转换
 */
public class AITargets {
    
    /**
     * 创建一个简单的状态转换
     * @param targetState 目标状态
     * @param condition 执行条件
     * @param tickRate tick 频率
     * @return AITransition
     */
    public static AITransition<IAIState> create(Supplier<IAIState> targetState, 
                                                  AITransition.Condition condition, 
                                                  int tickRate) {
        return new AITransition<>(targetState.get(), condition, null, tickRate);
    }
    
    /**
     * 创建一个带动作的状态转换
     * @param targetState 目标状态
     * @param condition 执行条件
     * @param action 执行的动作
     * @param tickRate tick 频率
     * @return AITransition
     */
    public static AITransition<IAIState> create(Supplier<IAIState> targetState, 
                                                  AITransition.Condition condition, 
                                                  AITransition.Action action, 
                                                  int tickRate) {
        return new AITransition<>(targetState.get(), condition, action, tickRate);
    }
    
    /**
     * 创建一个无条件转换（总是执行）
     * @param targetState 目标状态
     * @param tickRate tick 频率
     * @return AITransition
     */
    public static AITransition<IAIState> always(IAIState targetState, int tickRate) {
        return new AITransition<>(targetState, () -> true, null, tickRate);
    }
    
    /**
     * 创建一个带动作的无条件转换
     * @param targetState 目标状态
     * @param action 执行的动作
     * @param tickRate tick 频率
     * @return AITransition
     */
    public static AITransition<IAIState> always(IAIState targetState, 
                                                  AITransition.Action action, 
                                                  int tickRate) {
        return new AITransition<>(targetState, () -> true, action, tickRate);
    }
    
    /**
     * 创建一个高优先级转换（用于紧急事件）
     * @param targetState 目标状态
     * @param condition 执行条件
     * @return AITransition (tickRate = 1，每 tick 都检查)
     */
    public static AITransition<IAIState> highPriority(IAIState targetState, 
                                                        AITransition.Condition condition) {
        return new AITransition<>(targetState, condition, null, 1);
    }
    
    /**
     * 函数式接口，用于延迟获取目标状态
     */
    @FunctionalInterface
    public interface Supplier<T> {
        T get();
    }
}
