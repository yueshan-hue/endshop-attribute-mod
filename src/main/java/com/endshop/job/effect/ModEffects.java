package com.endshop.job.effect;

import com.endshop.job.EndshopJob;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 自定义效果注册表
 */
public class ModEffects {
    
    public static final DeferredRegister<MobEffect> EFFECTS = 
            DeferredRegister.create(Registries.MOB_EFFECT, EndshopJob.MODID);
    
    // 寒冷附着效果
    public static final DeferredHolder<MobEffect, MobEffect> COLD_ATTACHMENT = 
            EFFECTS.register("cold_attachment", ColdAttachmentEffect::new);
    
    // 灼燃附着效果
    public static final DeferredHolder<MobEffect, MobEffect> BURNING_ATTACHMENT = 
            EFFECTS.register("burning_attachment", BurningAttachmentEffect::new);
    
    // 电磁附着效果
    public static final DeferredHolder<MobEffect, MobEffect> ELECTROMAGNETIC_ATTACHMENT = 
            EFFECTS.register("electromagnetic_attachment", ElectromagneticAttachmentEffect::new);
    
    // 自然附着效果
    public static final DeferredHolder<MobEffect, MobEffect> NATURE_ATTACHMENT = 
            EFFECTS.register("nature_attachment", NatureAttachmentEffect::new);
    
    // 侵蚀效果
    public static final DeferredHolder<MobEffect, MobEffect> EROSION = 
            EFFECTS.register("erosion", ErosionEffect::new);
    
    /**
     * 注册所有效果
     */
    public static void register(IEventBus modEventBus) {
        EFFECTS.register(modEventBus);
    }
}
