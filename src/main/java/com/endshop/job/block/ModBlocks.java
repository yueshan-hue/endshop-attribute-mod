package com.endshop.job.block;

import com.endshop.job.EndshopJob;
import com.endshop.job.fluid.ModFluids;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 方块注册表
 */
public class ModBlocks {
    
    public static final DeferredRegister<Block> BLOCKS = 
            DeferredRegister.create(Registries.BLOCK, EndshopJob.MODID);
    
    // 侵蚀流体方块
    public static final DeferredHolder<Block, LiquidBlock> EROSION_FLUID = 
            registerBlock("erosion_fluid", () -> new ErosionFluidBlock(
                    (FlowingFluid) ModFluids.EROSION_SOURCE.get(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
                            .mapColor(MapColor.COLOR_PURPLE)
                            .replaceable()
                            .noCollission()
                            .strength(100.0F)
                            .noLootTable()
                            .liquid()
                            .pushReaction(PushReaction.DESTROY)
            ));
    
    private static <T extends Block> DeferredHolder<Block, T> registerBlock(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }
    
    /**
     * 注册所有方块
     */
    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
