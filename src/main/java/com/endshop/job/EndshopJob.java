package com.endshop.job;

import com.endshop.job.command.SetJobCommand;
import com.endshop.job.command.AdminTaskCommand;
import com.endshop.job.config.ModConfig;
import com.endshop.job.data.JobDataAttachment;
import com.endshop.job.entity.EndshopEntity;
import com.endshop.job.entity.EndshopEntityTypes;
import com.endshop.job.item.JobBookItem;
import com.endshop.job.item.OperatorArtItem;
import com.endshop.job.item.ErosionBucketItem;
import com.endshop.job.skill.SkillDataAttachment;
import com.endshop.job.skill.SkillInitializer;
import com.endshop.job.operators.OperatorProfileManager;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(EndshopJob.MODID)
public class EndshopJob {

    public static final String MODID = "endshopattribute";
    public static final Logger LOGGER = LogUtils.getLogger();

    // 物品注册表
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // 创造标签注册表
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // 职业书物品
    public static final DeferredItem<Item> JOB_BOOK = 
            ITEMS.register("job_book", () -> new JobBookItem(new Item.Properties().stacksTo(1)));

    // 庄方宜美图物品
    public static final DeferredItem<Item> ZHUANG_FANGYI_ART_ZONG_XIANG_YI = 
            ITEMS.register("zhuang_fangyi_art_zong_xiang_yi", () -> new OperatorArtItem(
                    new Item.Properties().stacksTo(1), 
                    "庄方宜", 
                    "总相宜", 
                    "Momoku"));
    
    public static final DeferredItem<Item> ZHUANG_FANGYI_ART_YU_TING_DE_SHUN_JIAN = 
            ITEMS.register("zhuang_fangyi_art_yu_ting_de_shun_jian", () -> new OperatorArtItem(
                    new Item.Properties().stacksTo(1), 
                    "庄方宜", 
                    "雨停的瞬间", 
                    "KU"));
    
    public static final DeferredItem<Item> ZHUANG_FANGYI_ART_DA_JU_YI_DING = 
            ITEMS.register("zhuang_fangyi_art_da_ju_yi_ding", () -> new OperatorArtItem(
                    new Item.Properties().stacksTo(1), 
                    "庄方宜", 
                    "大局已定", 
                    "LM7"));

    // 射流无人机物品
    public static final DeferredItem<Item> JET_DRONE_ITEM = 
            ITEMS.register("jet_drone", () -> new Item(new Item.Properties().stacksTo(64)));

    // 侵蚀流体桶
    public static final DeferredItem<Item> EROSION_BUCKET = 
            ITEMS.register("erosion_bucket", ErosionBucketItem::new);

    // 侵蚀核心掉落物
    public static final DeferredItem<Item> EROSION_CORE_ITEM = 
            ITEMS.register("erosion_core_item", () -> new Item(new Item.Properties().stacksTo(64)));

    // 创造标签
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ENDSHOP_TAB = 
            CREATIVE_MODE_TABS.register("endshop_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + MODID))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> JOB_BOOK.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(JOB_BOOK.get());
                        output.accept(ZHUANG_FANGYI_ART_ZONG_XIANG_YI.get());
                        output.accept(ZHUANG_FANGYI_ART_YU_TING_DE_SHUN_JIAN.get());
                        output.accept(ZHUANG_FANGYI_ART_DA_JU_YI_DING.get());
                        output.accept(JET_DRONE_ITEM.get());
                        output.accept(EROSION_BUCKET.get());
                        output.accept(EROSION_CORE_ITEM.get());
                    })
                    .build());

    public EndshopJob(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
    
        // 注册配置
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, ModConfig.SPEC);
        EndshopJob.LOGGER.info("[终末地职业] 配置文件已加载");
    
        // 注册实体类型
        EndshopEntityTypes.register(modEventBus);
    
        // 注册自定义效果
        com.endshop.job.effect.ModEffects.register(modEventBus);
        
        // 注册流体类型（NeoForge专属，必须在Fluid之前注册）
        com.endshop.job.fluid.ModFluidTypes.register(modEventBus);
        
        // 注册流体
        com.endshop.job.fluid.ModFluids.register(modEventBus);
        
        // 注册方块
        com.endshop.job.block.ModBlocks.register(modEventBus);
    
        // 注册数据附件类型
        JobDataAttachment.register(modEventBus);
        com.endshop.job.data.AttributeDataAttachment.register(modEventBus);
            
        // 注册技能数据附件
        SkillDataAttachment.register(modEventBus);
            
        // 注册任务数据附件
        com.endshop.job.task.TaskDataAttachment.register(modEventBus);
            
        // 初始化并注册所有技能
        SkillInitializer.init();
        
        // 初始化干员档案
        OperatorProfileManager.initialize();
        
        // 注册游戏事件（Tab 名显示、指令等）到 NeoForge 总线
        NeoForge.EVENT_BUS.register(new GameEvents());
    }
    
    /**
     * 游戏事件处理类
     */
    private static class GameEvents {
        @SubscribeEvent
        public void onRegisterCommands(RegisterCommandsEvent event) {
            com.endshop.job.command.SetJobCommand.register(event.getDispatcher());
            com.endshop.job.command.AdminTaskCommand.register(event.getDispatcher());
            com.endshop.job.command.ErosionDebugCommand.register(event.getDispatcher());
        }
    }


}