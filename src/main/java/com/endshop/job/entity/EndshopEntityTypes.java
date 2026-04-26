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

    public static final DeferredHolder<EntityType<?>, EntityType<EndshopEntity>> JUNWEI = 
            ENTITY_TYPES.register("junwei", () -> EntityType.Builder.of(EndshopEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .build("junwei"));

    public static final DeferredHolder<EntityType<?>, EntityType<EndshopEntity>> AIDERA = 
            ENTITY_TYPES.register("aidera", () -> EntityType.Builder.of(EndshopEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .build("aidera"));

    // 射流无人机实体
    public static final DeferredHolder<EntityType<?>, EntityType<JetDroneEntity>> JET_DRONE = 
            ENTITY_TYPES.register("jet_drone", () -> EntityType.Builder.of(JetDroneEntity::new, MobCategory.MISC)
                    .sized(0.8f, 0.8f)
                    .clientTrackingRange(8)
                    .updateInterval(1)
                    .build("jet_drone"));

    // 侵蚀核心实体
    public static final DeferredHolder<EntityType<?>, EntityType<ErosionCoreEntity>> EROSION_CORE = 
            ENTITY_TYPES.register("erosion_core", () -> EntityType.Builder.of(ErosionCoreEntity::new, MobCategory.MONSTER)
                    .sized(1.2f, 2.0f)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("erosion_core"));

    // 技能特效实体
    public static final DeferredHolder<EntityType<?>, EntityType<SkillEffectEntity>> SKILL_EFFECT = 
            ENTITY_TYPES.register("skill_effect", () -> EntityType.Builder.of(SkillEffectEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build("skill_effect"));

    // GeckoLib 技能特效实体 (支持基岩版模型和动画)
    public static final DeferredHolder<EntityType<?>, EntityType<GeckoSkillEffectEntity>> GECKO_SKILL_EFFECT = 
            ENTITY_TYPES.register("gecko_skill_effect", () -> EntityType.Builder.of(GeckoSkillEffectEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build("gecko_skill_effect"));

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
            event.put(EndshopEntityTypes.JUNWEI.get(), EndshopEntity.createAttributes().build());
            event.put(EndshopEntityTypes.AIDERA.get(), EndshopEntity.createAttributes().build());
            // 射流无人机实体属性
            event.put(EndshopEntityTypes.JET_DRONE.get(), EndshopEntity.createAttributes().build());
            // 侵蚀核心实体属性
            event.put(EndshopEntityTypes.EROSION_CORE.get(), ErosionCoreEntity.createAttributes().build());
        }
    }
}
