package com.endshop.job.ai.statemachine;

/**
 * AI Transition Implementation - Reference Minecolonies TickingTransition
 * Supports tick-based state transitions
 */
public class AITransition<S extends IAIState> implements ITransition<S> {
    private final S targetState;
    private final Condition condition;
    private final Action action;
    private final int tickRate;
    private int tickCounter;
    
    /**
     * Create a transition
     * @param targetState Target state
     * @param condition Execution condition
     * @param action Action to execute
     * @param tickRate Tick rate (check every N ticks)
     */
    public AITransition(S targetState, Condition condition, Action action, int tickRate) {
        this.targetState = targetState;
        this.condition = condition;
        this.action = action;
        this.tickRate = tickRate;
        this.tickCounter = 0;
    }
    
    @Override
    public boolean shouldExecute() {
        tickCounter++;
        if (tickCounter < tickRate) {
            return false;
        }
        tickCounter = 0;
        return condition.test();
    }
    
    @Override
    public S execute() {
        if (action != null) {
            action.execute();
        }
        return targetState;
    }
    
    @Override
    public int getTickRate() {
        return tickRate;
    }
    
    public interface Condition {
        boolean test();
    }
    
    public interface Action {
        void execute();
    }
}
