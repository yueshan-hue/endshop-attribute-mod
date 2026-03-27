package com.endshop.job.event;

import com.endshop.job.data.JobDataAttachment;
import com.endshop.job.profession.Profession;
import com.endshop.job.skill.SkillDataAttachment;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * 玩家登录事件处理 - 同步职业数据到客户端
 */
@EventBusSubscriber
public class PlayerLoginHandler {
    
    /**
     * 玩家登录时同步职业数据
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            // 获取玩家的职业（这会触发数据加载）
            Profession playerJob = JobDataAttachment.getJob(serverPlayer);
            
            // 获取玩家的技能数据
            SkillDataAttachment.SkillData skillData = SkillDataAttachment.getSkillData(serverPlayer);
            
            // 输出调试信息
            System.out.println("===========================================");
            System.out.println("玩家 " + serverPlayer.getName().getString() + " 登录");
            System.out.println("职业：" + playerJob.name() + " (" + playerJob.getDisplayName() + ")");
            System.out.println("已解锁技能：" + skillData.getUnlockedSkills().size() + " 个");
            for (String skillId : skillData.getUnlockedSkills()) {
                System.out.println("  - " + skillId);
            }
            System.out.println("===========================================");
        }
    }
    
    /**
     * 玩家复制时（从存档加载）同步数据
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            // 确保数据被正确复制
            JobDataAttachment.getJob(serverPlayer);
            SkillDataAttachment.getSkillData(serverPlayer);
            
            System.out.println("玩家 " + serverPlayer.getName().getString() + " 数据已克隆");
        }
    }
}
