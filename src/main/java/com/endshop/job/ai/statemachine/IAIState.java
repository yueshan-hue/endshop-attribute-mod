package com.endshop.job.ai.statemachine;

/**
 * AI 状态接口 - 参考 Minecolonies 的 IAIState
 */
public interface IAIState {
    /**
     * 获取状态名称
     */
    String getName();
    
    /**
     * 是否允许在此状态下进行其他活动（如吃饭、休息）
     * 返回 true 表示可以被中断
     */
    default boolean isOkayToEat() {
        return false;
    }
    
    /**
     * 是否允许在此状态下切换任务
     */
    default boolean canSwitchTask() {
        return false;
    }
}
