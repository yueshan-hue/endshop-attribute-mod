package com.endshop.job.operators;

import com.endshop.job.EndshopJob;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 干员档案管理器 - 从JSON文件加载和管理干员档案
 */
public class OperatorProfileManager {
    
    private static final Map<String, JsonObject> OPERATOR_PROFILES = new HashMap<>();
    private static boolean initialized = false;
    
    /**
     * 初始化并加载所有干员档案
     */
    public static void initialize() {
        if (initialized) return;
        
        System.out.println("[干员档案] 开始加载档案...");
        
        // 干员档案ID列表
        String[] operatorIds = {
            "zhuang_fangyi", "gerpeita", "bieli", "rosie", 
            "junwei", "aidera", "admin_a", "admin_b",
            "levante", "tangtang", "chen_qianyu", "fluorite",
            "etra", "etera", "perlica", "sehy", "yvon", "yingshi",
            "qiuli", "surtr"
        };
        
        for (String operatorId : operatorIds) {
            try {
                // 使用类路径加载JSON文件
                String resourcePath = "/data/endshopattribute/operators/" + operatorId + ".json";
                java.net.URL url = OperatorProfileManager.class.getResource(resourcePath);
                
                if (url != null) {
                    try (java.io.BufferedReader reader = new java.io.BufferedReader(
                            new java.io.InputStreamReader(url.openStream(), java.nio.charset.StandardCharsets.UTF_8))) {
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                        
                        // 检查是否为废弃档案
                        if (json.has("_deprecated") && json.get("_deprecated").getAsBoolean()) {
                            String note = json.has("_deprecation_note") ? json.get("_deprecation_note").getAsString() : "";
                            String migrateTo = json.has("_migrate_to") ? json.get("_migrate_to").getAsString() : "";
                            System.out.println("[干员档案] 跳过废弃档案: " + operatorId + " - " + note + (migrateTo.isEmpty() ? "" : " (请迁移至: " + migrateTo + ")"));
                            continue;
                        }
                        
                        OPERATOR_PROFILES.put(operatorId, json);
                        System.out.println("[干员档案] 成功加载: " + operatorId + " - " + json.get("name").getAsString());
                    }
                } else {
                    System.err.println("[干员档案] 未找到档案: " + operatorId);
                }
            } catch (Exception e) {
                System.err.println("[干员档案] 加载失败: " + operatorId);
                e.printStackTrace();
            }
        }
        
        System.out.println("[干员档案] 档案加载完成，共加载 " + OPERATOR_PROFILES.size() + " 个干员档案");
        initialized = true;
    }
    
    /**
     * 根据ID获取干员档案
     */
    public static JsonObject getProfile(String operatorId) {
        return OPERATOR_PROFILES.get(operatorId);
    }
    
    /**
     * 获取干员名称
     */
    public static String getOperatorName(String operatorId) {
        JsonObject profile = getProfile(operatorId);
        if (profile != null && profile.has("name")) {
            return profile.get("name").getAsString();
        }
        return operatorId;
    }
    
    /**
     * 获取干员职业
     */
    public static String getOperatorProfession(String operatorId) {
        JsonObject profile = getProfile(operatorId);
        if (profile != null && profile.has("profession")) {
            return profile.get("profession").getAsString();
        }
        return "UNKNOWN";
    }
    
    /**
     * 获取干员描述
     */
    public static String getOperatorDescription(String operatorId) {
        JsonObject profile = getProfile(operatorId);
        if (profile != null && profile.has("description")) {
            return profile.get("description").getAsString();
        }
        return "";
    }
    
    /**
     * 检查档案是否存在
     */
    public static boolean hasProfile(String operatorId) {
        return OPERATOR_PROFILES.containsKey(operatorId);
    }
}
