package com.endshop.job.ai.statemachine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 基础状态机实现 - 参考 Minecolonies 的 BasicStateMachine
 */
public class BasicStateMachine<T extends ITransition<S>, S extends IAIState> {
    protected S currentState;
    protected final Consumer<RuntimeException> exceptionHandler;
    protected final Map<S, List<T>> transitions = new HashMap<>();
    
    public BasicStateMachine(S initialState, Consumer<RuntimeException> exceptionHandler) {
        this.currentState = initialState;
        this.exceptionHandler = exceptionHandler;
    }
    
    public void addTransition(S state, T transition) {
        transitions.computeIfAbsent(state, k -> new java.util.ArrayList<>()).add(transition);
    }
    
    public void tick() {
        try {
            List<T> currentTransitions = transitions.get(currentState);
            if (currentTransitions != null) {
                for (T transition : currentTransitions) {
                    if (transition.shouldExecute()) {
                        S nextState = transition.execute();
                        if (nextState != null) {
                            currentState = nextState;
                        }
                        break;
                    }
                }
            }
        } catch (RuntimeException e) {
            exceptionHandler.accept(e);
        }
    }
    
    public S getCurrentState() {
        return currentState;
    }
    
    public void setCurrentState(S state) {
        this.currentState = state;
    }
}
