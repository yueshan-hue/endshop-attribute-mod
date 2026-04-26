package com.endshop.job.ai.statemachine;

/**
 * AI 转换接口 - 参考 Minecolonies 的 TickingTransition
 */
public interface ITransition<S extends IAIState> {
    /**
     * 检查是否应该执行此转换
     */
    boolean shouldExecute();
    
    /**
     * 执行转换，返回下一个状态
     */
    S execute();
    
    /**
     * 获取 Tick 频率（每多少 tick 检查一次）
     */
    default int getTickRate() {
        return 20;
    }
    
    /**
     * 条件接口
     */
    interface Condition {
        boolean test();
    }
    
    /**
     * 动作接口
     */
    interface Action {
        void execute();
    }
}
