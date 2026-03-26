package com.endshop.job.entity;

import com.endshop.job.EndshopJob;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 终末地实体类型注册
 */
public class EndshopEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
            DeferredRegister.create(Registries.ENTITY_TYPE, EndshopJob.MODID);

    // 终末地角色实体
    public static final DeferredHolder<EntityType<?>, EntityType<EndshopEntity>> YVON = 
            ENTITY_TYPES.register("yvon", () -> EntityType.Builder.of(EndshopEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .build("yvon"));

    public static final DeferredHolder<EntityType<?>, EntityType<EndshopEntity>> PERICA = 
            ENTITY_TYPES.register("perica", () -> EntityType.Builder.of(EndshopEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .build("perica"));

    public static final DeferredHolder<EntityType<?>, EntityType<EndshopEntity>> SURTR = 
            ENTITY_TYPES.register("surtr", () -> EntityType.Builder.of(EndshopEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .build("surtr"));

    public static final DeferredHolder<EntityType<?>, EntityType<EndshopEntity>> GERPEITA = 
            ENTITY_TYPES.register("gerpeita", () -> EntityType.Builder.of(EndshopEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .build("gerpeita"));

    public static final DeferredHolder<EntityType<?>, EntityType<EndshopEntity>> ROSIE = 
            ENTITY_TYPES.register("rosie", () -> EntityType.Builder.of(EndshopEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .build("rosie"));

    public static final DeferredHolder<EntityType<?>, EntityType<EndshopEntity>> ADMIN_B = 
            ENTITY_TYPES.register("admin_b", () -> EntityType.Builder.of(EndshopEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .build("admin_b"));

    public static final DeferredHolder<EntityType<?>, EntityType<EndshopEntity>> CHEN_QIANYU = 
            ENTITY_TYPES.register("chen_qianyu", () -> EntityType.Builder.of(EndshopEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .build("chen_qianyu"));

    public static final DeferredHolder<EntityType<?>, EntityType<EndshopEntity>> BIELI = 
            ENTITY_TYPES.register("bieli", () -> EntityType.Builder.of(EndshopEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .build("bieli"));

    public static final DeferredHolder<EntityType<?>, EntityType<EndshopEntity>> ETERA = 
            ENTITY_TYPES.register("etera", () -> EntityType.Builder.of(EndshopEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .build("etera"));

    public static final DeferredHolder<EntityType<?>, EntityType<EndshopEntity>> ZHUANG_FANGYI = 
            ENTITY_TYPES.register("zhuang_fangyi", () -> EntityType.Builder.of(EndshopEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .build("zhuang_fangyi"));

    public static final DeferredHolder<EntityType<?>, EntityType<EndshopEntity>> TANGTANG = 
            ENTITY_TYPES.register("tangtang", () -> EntityType.Builder.of(EndshopEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .build("tangtang"));

    public static final DeferredHolder<EntityType<?>, EntityType<EndshopEntity>> QIULI = 
            ENTITY_TYPES.register("qiuli", () -> EntityType.Builder.of(EndshopEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .build("qiuli"));

    public static final DeferredHolder<EntityType<?>, EntityType<EndshopEntity>> ADMIN_A = 
            ENTITY_TYPES.register("admin_a", () -> EntityType.Builder.of(EndshopEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .build("admin_a"));

    public static final DeferredHolder<EntityType<?>, EntityType<EndshopEntity>> YINGSHI = 
            ENTITY_TYPES.register("yingshi", () -> EntityType.Builder.of(EndshopEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .build("yingshi"));

    public static final DeferredHolder<EntityType<?>, EntityType<EndshopEntity>> SAIXI = 
            ENTITY_TYPES.register("saixi", () -> EntityType.Builder.of(EndshopEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .build("saixi"));

    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
        modEventBus.register(new EntityAttributeEvents());
    }

    /**
     * 实体属性注册事件处理类
     */
    private static class EntityAttributeEvents {
        @net.neoforged.bus.api.SubscribeEvent
        public void onEntityAttributeCreation(net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent event) {
            // 注册所有终末地实体的属性映射表
            event.put(EndshopEntityTypes.YVON.get(), EndshopEntity.createAttributes().build());
            event.put(EndshopEntityTypes.PERICA.get(), EndshopEntity.createAttributes().build());
            event.put(EndshopEntityTypes.SURTR.get(), EndshopEntity.createAttributes().build());
            event.put(EndshopEntityTypes.GERPEITA.get(), EndshopEntity.createAttributes().build());
            event.put(EndshopEntityTypes.ROSIE.get(), EndshopEntity.createAttributes().build());
            event.put(EndshopEntityTypes.ADMIN_B.get(), EndshopEntity.createAttributes().build());
            event.put(EndshopEntityTypes.CHEN_QIANYU.get(), EndshopEntity.createAttributes().build());
            event.put(EndshopEntityTypes.BIELI.get(), EndshopEntity.createAttributes().build());
            event.put(EndshopEntityTypes.ETERA.get(), EndshopEntity.createAttributes().build());
            event.put(EndshopEntityTypes.ZHUANG_FANGYI.get(), EndshopEntity.createAttributes().build());
            event.put(EndshopEntityTypes.TANGTANG.get(), EndshopEntity.createAttributes().build());
            event.put(EndshopEntityTypes.QIULI.get(), EndshopEntity.createAttributes().build());
            event.put(EndshopEntityTypes.ADMIN_A.get(), EndshopEntity.createAttributes().build());
            event.put(EndshopEntityTypes.YINGSHI.get(), EndshopEntity.createAttributes().build());
            event.put(EndshopEntityTypes.SAIXI.get(), EndshopEntity.createAttributes().build());
        }
    }
}
