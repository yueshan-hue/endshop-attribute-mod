package com.endshop.job.ai;

import com.endshop.job.ai.state.AIState;
import com.endshop.job.ai.state.AIStateMachine;
import com.endshop.job.ai.state.DefaultAIStateMachine;
import com.endshop.job.ai.statemachine.AITransition;
import com.endshop.job.ai.task.TaskHandler;
import com.endshop.job.ai.task.WoodcuttingTaskHandler;
import com.endshop.job.ai.task.MiningTaskHandler;
import com.endshop.job.ai.task.FishingTaskHandler;
import com.endshop.job.ai.task.CombatTaskHandler;
import com.endshop.job.ai.task.GuardTaskHandler;
import com.endshop.job.entity.EndshopEntity;
import com.endshop.job.task.TaskType;

import java.util.HashMap;
import java.util.Map;

/**
 * AI 管理器
 */
public class AIManager {
    private final EndshopEntity entity;
    private final AIStateMachine stateMachine;
    private final Map<TaskType, TaskHandler> taskHandlers;
    private TaskType currentTaskType;
    private TaskHandler currentTaskHandler;
    
    public AIManager(EndshopEntity entity) {
        this.entity = entity;
        this.stateMachine = new DefaultAIStateMachine(entity);
        this.taskHandlers = new HashMap<>();
        this.currentTaskType = TaskType.NONE;
        this.currentTaskHandler = null;
        
        // 初始化任务处理器
        initializeTaskHandlers();
        
        // 初始化状态机转换
        initializeTransitions();
    }
    
    /**
     * 初始化任务处理器
     */
    private void initializeTaskHandlers() {
        taskHandlers.put(TaskType.WOODCUTTING, new WoodcuttingTaskHandler(entity));
        taskHandlers.put(TaskType.MINING, new MiningTaskHandler(entity));
        taskHandlers.put(TaskType.FISHING, new FishingTaskHandler(entity));
        taskHandlers.put(TaskType.COMBAT, new CombatTaskHandler(entity));
        taskHandlers.put(TaskType.GUARD, new GuardTaskHandler(entity));
        // 可以添加其他任务处理器
    }
    
    /**
     * 初始化状态机转换（作为 TaskHandler 驱动的辅助）
     * 注意：主要逻辑由 TaskHandler.handle() 直接驱动状态，
     * 此处的状态机转换仅作为全局 fallback 保障。
     */
    private void initializeTransitions() {
        // 全局高优先级转换：如果没有任务处理器，强制返回 IDLE
        ((DefaultAIStateMachine) stateMachine).registerHighPriorityTransition(
            new AITransition<>(
                AIState.IDLE,
                () -> currentTaskHandler == null,
                null,
                20  // 每20tick检查一次
            )
        );
    }
    
    /**
     * 设置当前任务类型
     */
    public void setTaskType(TaskType taskType) {
        if (currentTaskType != taskType) {
            currentTaskType = taskType;
            currentTaskHandler = taskHandlers.get(taskType);
            if (currentTaskHandler != null) {
                currentTaskHandler.initialize();
                stateMachine.setCurrentState(AIState.PREPARING);
            } else {
                stateMachine.setCurrentState(AIState.IDLE);
            }
        }
    }
    
    /**
     * 更新 AI
     */
    public void update() {
        if (currentTaskHandler != null) {
            // 处理当前任务 - 任务处理器直接执行，不使用状态机覆盖
            AIState newState = currentTaskHandler.handle();
            
            // 检查任务是否完成（处理器返回 IDLE 表示完成）
            if (newState == AIState.IDLE) {
                // 任务完成，重置处理器并重新初始化
                System.out.println("AIManager: 任务完成，重置处理器 - " + currentTaskType);
                currentTaskHandler.reset();
                currentTaskHandler.initialize();
            }
        } else {
            stateMachine.setCurrentState(AIState.IDLE);
        }
        
        // 更新状态机
        stateMachine.update();
    }
    
    /**
     * 获取当前任务类型
     */
    public TaskType getCurrentTaskType() {
        return currentTaskType;
    }
    
    /**
     * 获取当前状态
     */
    public AIState getCurrentState() {
        return stateMachine.getCurrentState();
    }
}
