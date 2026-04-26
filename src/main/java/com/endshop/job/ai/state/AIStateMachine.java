package com.endshop.job.ai.state;

import com.endshop.job.ai.statemachine.AITransition;
import com.endshop.job.entity.EndshopEntity;

/**
 * AI 状态机接口
 */
public interface AIStateMachine {
    /**
     * 初始化状态机
     */
    void initialize();
    
    /**
     * 更新状态机
     */
    void update();
    
    /**
     * 获取当前状态
     */
    AIState getCurrentState();
    
    /**
     * 切换到指定状态
     */
    void setCurrentState(AIState state);
    
    /**
     * 注册转换
     */
    void registerTransition(AITransition<AIState> transition);
    
    /**
     * 获取实体
     */
    EndshopEntity getEntity();
}
