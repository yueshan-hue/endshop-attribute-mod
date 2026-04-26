package com.endshop.job.fluid;

import com.endshop.job.EndshopJob;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * 流体类型注册表 - NeoForge专属
 * 定义流体的物理属性（密度、粘度、声音等）
 */
public class ModFluidTypes {
    
    public static final DeferredRegister<FluidType> FLUID_TYPES = 
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, EndshopJob.MODID);
    
    // 侵蚀流体类型
    public static final DeferredHolder<FluidType, FluidType> EROSION_FLUID_TYPE = 
            FLUID_TYPES.register("erosion", () -> new FluidType(
                    FluidType.Properties.create()
                            .descriptionId("block." + EndshopJob.MODID + ".erosion_fluid")
                            .canSwim(false)           // 不能在流体内游泳
                            .canDrown(true)           // 会溺水
                            .supportsBoating(false)   // 不支持船
                            .canHydrate(false)        // 不能浇灌作物
                            .lightLevel(8)            // 发光等级（紫色发光）
                            .density(1200)            // 密度（水为1000，侵蚀流体更稠密）
                            .viscosity(1500)          // 粘度（水为1000，侵蚀流体更粘稠）
                            .temperature(350)         // 温度（开尔文，略高于室温）
            ));
    
    /**
     * 注册所有流体类型
     */
    public static void register(IEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
    }
}
