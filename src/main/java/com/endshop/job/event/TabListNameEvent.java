package com.endshop.job.event;

import com.endshop.job.EndshopJob;
import com.endshop.job.data.JobDataAttachment;
import com.endshop.job.profession.Profession;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * 服务端事件 - 修改Tab列表中玩家显示名（加职业前缀）
 */
@EventBusSubscriber(modid = EndshopJob.MODID)
public class TabListNameEvent {

    /**
     * 在Tab列表中给玩家名字前加上【职业名】
     * 注意：PlayerEvent.NameFormat 在每次需要显示名时触发
     */
    @SubscribeEvent
    public static void onPlayerNameFormat(PlayerEvent.NameFormat event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;

        Profession profession = JobDataAttachment.getJob(serverPlayer);
        if (profession == Profession.NONE) {
            // 无职业时显示原始名字
            event.setDisplayname(event.getUsername());
            return;
        }

        // 构建带颜色职业前缀的组件：【职业名】玩家名
        Component displayName = Component.literal(
                profession.getColoredPrefix() + serverPlayer.getName().getString()
        );
        event.setDisplayname(displayName);
    }
}
