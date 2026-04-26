package com.endshop.job.client;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * 客户端干员列表缓存
 * 存储当前玩家已召唤的干员ID列表
 */
public class OperatorListCache {
    private static List<String> operatorIds = Collections.emptyList();
    private static final int MAX_OPERATORS = 4; // 单人上限4个干员

    /**
     * 更新干员列表（从服务端同步）
     */
    public static void update(List<String> newOperatorIds) {
        operatorIds = new ArrayList<>(newOperatorIds);
    }

    /**
     * 获取已召唤的干员列表
     */
    public static List<String> getOperatorIds() {
        return Collections.unmodifiableList(operatorIds);
    }

    /**
     * 获取已召唤干员数量
     */
    public static int getCount() {
        return operatorIds.size();
    }

    /**
     * 检查是否已达上限
     */
    public static boolean isFull() {
        return operatorIds.size() >= MAX_OPERATORS;
    }

    /**
     * 获取最大干员数量
     */
    public static int getMaxOperators() {
        return MAX_OPERATORS;
    }
}
