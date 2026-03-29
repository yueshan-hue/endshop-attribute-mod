package com.endshop.job.command;

import com.endshop.job.EndshopJob;
import com.endshop.job.client.gui.SkillEquipScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * 打开技能装备界面指令（仅客户端）
 */
@EventBusSubscriber(modid = EndshopJob.MODID, value = Dist.CLIENT)
public class OpenSkillScreenCommand {
    
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("skillscreen")
                .executes(context -> {
                    Minecraft mc = Minecraft.getInstance();
                    
                    // 在客户端线程中打开 GUI
                    mc.execute(() -> {
                        mc.setScreen(new SkillEquipScreen());
                    });
                    
                    return 1;
                })
        );
    }
}
