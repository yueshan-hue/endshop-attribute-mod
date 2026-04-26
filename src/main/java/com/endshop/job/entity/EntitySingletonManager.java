package com.endshop.job.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 实体单例管理器 - 确保每种角色实体在服务器中只能存在一个
 */
public class EntitySingletonManager {
    // 存储已召唤的实体类型（使用 EntityType 的注册名称作为标识）
    private static final Set<String> spawnedEntityTypes = new HashSet<>();
    // 存储已召唤实体的 UUID，用于验证实体是否仍然存在
    private static final Set<UUID> spawnedEntityUUIDs = new HashSet<>();

    /**
     * 检查指定类型的实体是否已经被召唤
     * 
     * @param entityType 实体类型注册名称
     * @return 如果已存在则返回 true
     */
    public static boolean isEntityTypeSpawned(String entityType) {
        return spawnedEntityTypes.contains(entityType);
    }

    /**
     * 标记实体类型已被召唤
     * 
     * @param entityType 实体类型注册名称
     * @param entityUUID 实体 UUID
     */
    public static void markEntitySpawned(String entityType, UUID entityUUID) {
        spawnedEntityTypes.add(entityType);
        spawnedEntityUUIDs.add(entityUUID);
    }

    /**
     * 移除已召唤的实体类型标记
     * 
     * @param entityType 实体类型注册名称
     */
    public static void removeEntitySpawned(String entityType) {
        spawnedEntityTypes.remove(entityType);
        // 清理对应的 UUID
        spawnedEntityUUIDs.removeIf(uuid -> !isEntityAlive(uuid));
    }

    /**
     * 清理无效的实体 UUID（实体已不存在）
     */
    public static void cleanupDeadEntities() {
        spawnedEntityUUIDs.removeIf(uuid -> !isEntityAlive(uuid));
        // 如果没有活跃的实体了，清空类型标记
        if (spawnedEntityUUIDs.isEmpty()) {
            spawnedEntityTypes.clear();
        }
    }

    /**
     * 检查实体是否仍然存活（简化版本，实际应该查询服务器）
     * 这里暂时返回 true，实际使用时需要传入服务器引用
     */
    private static boolean isEntityAlive(UUID uuid) {
        // TODO: 实现实际的实体存活检查
        return true;
    }

    /**
     * 获取所有已召唤的实体类型
     */
    public static Set<String> getSpawnedEntityTypes() {
        return new HashSet<>(spawnedEntityTypes);
    }

    /**
     * 获取已召唤实体数量
     */
    public static int getSpawnedCount() {
        return spawnedEntityTypes.size();
    }

    /**
     * 重置所有标记（用于测试或服务器重启）
     */
    public static void reset() {
        spawnedEntityTypes.clear();
        spawnedEntityUUIDs.clear();
    }
}
