package com.endshop.job.ai.task;

import com.endshop.job.ai.state.AIState;
import com.endshop.job.entity.EndshopEntity;

/**
 * 任务处理器接口
 */
public interface TaskHandler {
    /**
     * 初始化任务
     */
    void initialize();
    
    /**
     * 处理任务
     * @return 下一个状态
     */
    AIState handle();
    
    /**
     * 重置任务
     */
    void reset();
    
    /**
     * 获取实体
     */
    EndshopEntity getEntity();
}
