package com.endshop.job.ai.state;

import com.endshop.job.ai.statemachine.AITransition;
import com.endshop.job.ai.statemachine.TickRateStateMachine;
import com.endshop.job.entity.EndshopEntity;

/**
 * 增强的 AI 状态机实现 - 参考 Minecolonies 的 TickRateStateMachine
 */
public class DefaultAIStateMachine implements AIStateMachine {
    private final EndshopEntity entity;
    private final TickRateStateMachine<AIState> stateMachine;
    
    public DefaultAIStateMachine(EndshopEntity entity) {
        this.entity = entity;
        // 使用 TickRateStateMachine，默认 tick rate 为 20 (1秒)
        this.stateMachine = new TickRateStateMachine<>(AIState.IDLE, e -> e.printStackTrace(), 20);
    }
    
    @Override
    public void initialize() {
        // 初始化状态机
    }
    
    @Override
    public void update() {
        stateMachine.tick();
    }
    
    @Override
    public AIState getCurrentState() {
        return stateMachine.getCurrentState();
    }
    
    @Override
    public void setCurrentState(AIState state) {
        stateMachine.setCurrentState(state);
    }
    
    @Override
    public void registerTransition(AITransition<AIState> transition) {
        // 注册到当前状态的转换
        stateMachine.registerTransition(stateMachine.getCurrentState(), transition);
    }
    
    /**
     * 注册从指定状态出发的转换
     */
    public void registerTransition(AIState fromState, AITransition<AIState> transition) {
        stateMachine.registerTransition(fromState, transition);
    }
    
    /**
     * 注册全局转换（在所有状态下都检查）
     */
    public void registerGlobalTransition(AITransition<AIState> transition) {
        stateMachine.registerGlobalTransition(transition);
    }
    
    /**
     * 注册高优先级转换（每 tick 都检查，可打断当前状态）
     */
    public void registerHighPriorityTransition(AITransition<AIState> transition) {
        stateMachine.registerHighPriorityTransition(transition);
    }
    
    @Override
    public EndshopEntity getEntity() {
        return entity;
    }
}
