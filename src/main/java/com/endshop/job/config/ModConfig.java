package com.endshop.job.config;

import com.endshop.job.EndshopJob;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 模组配置类
 */
public class ModConfig {
    
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    
    // 侵蚀效果开关
    public static final ModConfigSpec.BooleanValue ENABLE_EROSION;
    
    // 侵蚀流体开关
    public static final ModConfigSpec.BooleanValue ENABLE_EROSION_FLUID;
    
    // 侵蚀核心生成开关
    public static final ModConfigSpec.BooleanValue ENABLE_EROSION_CORE_SPAWN;
    
    static {
        BUILDER.comment("终末地职业模组配置").push("general");
        
        BUILDER.comment("是否启用侵蚀效果系统").push("erosion");
        ENABLE_EROSION = BUILDER
                .comment("启用后，侵蚀效果和侵蚀流体会正常工作")
                .translation("config.endshopattribute.enable_erosion")
                .define("enable_erosion", true);
        
        ENABLE_EROSION_FLUID = BUILDER
                .comment("启用后，侵蚀流体会对进入的生物施加侵蚀效果")
                .translation("config.endshopattribute.enable_erosion_fluid")
                .define("enable_erosion_fluid", true);
        
        ENABLE_EROSION_CORE_SPAWN = BUILDER
                .comment("启用后，侵蚀核心会在侵蚀流体中自然生成")
                .translation("config.endshopattribute.enable_erosion_core_spawn")
                .define("enable_erosion_core_spawn", true);
        
        BUILDER.pop();
        BUILDER.pop();
    }
    
    public static final ModConfigSpec SPEC = BUILDER.build();
}
