package com.endshop.job.fluid;

import com.endshop.job.EndshopJob;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 流体注册表
 */
public class ModFluids {
    
    public static final DeferredRegister<Fluid> FLUIDS = 
            DeferredRegister.create(Registries.FLUID, EndshopJob.MODID);
    
    // 侵蚀流体源方块
    public static final DeferredHolder<Fluid, Fluid> EROSION_SOURCE = 
            FLUIDS.register("erosion", ErosionFluid.Source::new);
    
    // 侵蚀流体流动方块
    public static final DeferredHolder<Fluid, Fluid> EROSION_FLOWING = 
            FLUIDS.register("flowing_erosion", ErosionFluid.Flowing::new);
    
    /**
     * 注册所有流体
     */
    public static void register(IEventBus modEventBus) {
        FLUIDS.register(modEventBus);
    }
}
