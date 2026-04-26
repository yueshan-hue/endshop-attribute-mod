package com.endshop.job.task;

/**
 * 任务类型枚举
 */
public enum TaskType {
    NONE("无任务"),
    MINING("挖矿"),
    WOODCUTTING("伐木"),
    FISHING("钓鱼"),
    BUILDING("建筑"),
    COMBAT("打怪"),
    GUARD("守护");
    
    private final String displayName;
    
    TaskType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 从显示名称获取任务类型
     */
    public static TaskType fromDisplayName(String name) {
        for (TaskType type : values()) {
            if (type.displayName.equals(name) || type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
