package com.endshop.job;

import com.endshop.job.command.SetJobCommand;
import com.endshop.job.data.JobDataAttachment;
import com.endshop.job.entity.EndshopEntity;
import com.endshop.job.entity.EndshopEntityTypes;
import com.endshop.job.item.JobBookItem;
import com.endshop.job.skill.SkillDataAttachment;
import com.endshop.job.skill.SkillInitializer;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
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

    // 创造标签
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ENDSHOP_TAB = 
            CREATIVE_MODE_TABS.register("endshop_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + MODID))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> JOB_BOOK.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(JOB_BOOK.get());
                    })
                    .build());

    public EndshopJob(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
    
        // 注册实体类型
        EndshopEntityTypes.register(modEventBus);
    
        // 注册数据附件类型
        JobDataAttachment.register(modEventBus);
        com.endshop.job.data.AttributeDataAttachment.register(modEventBus);
            
        // 注册技能数据附件
        SkillDataAttachment.register(modEventBus);
            
        // 初始化并注册所有技能
        SkillInitializer.init();
    
        // 注册游戏事件（Tab 名显示、指令等）到 NeoForge 总线
        NeoForge.EVENT_BUS.register(new GameEvents());
    }

    /**
     * 游戏事件处理类
     */
    private static class GameEvents {
        @SubscribeEvent
        public void onRegisterCommands(RegisterCommandsEvent event) {
            SetJobCommand.register(event.getDispatcher());
        }
    }


}