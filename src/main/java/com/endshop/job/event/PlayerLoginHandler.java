package com.endshop.job.event;

import com.endshop.job.EndshopJob;
import com.endshop.job.data.JobDataAttachment;
import com.endshop.job.item.JobBookItem;
import com.endshop.job.network.SyncSkillDataPacket;
import com.endshop.job.profession.Profession;
import com.endshop.job.skill.SkillDataAttachment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;

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
            
            // 如果玩家还没有选择职业，给予职业书
            if (playerJob == Profession.NONE) {
                giveJobBook(serverPlayer);
            }
            
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
            
            // 同步技能数据到客户端
            sendSkillDataToClient(serverPlayer, skillData);
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
            SkillDataAttachment.SkillData skillData = SkillDataAttachment.getSkillData(serverPlayer);
            
            // 同步技能数据到客户端
            sendSkillDataToClient(serverPlayer, skillData);
            
            System.out.println("玩家 " + serverPlayer.getName().getString() + " 数据已克隆");
        }
    }
    
    /**
     * 发送技能数据到客户端
     */
    private static void sendSkillDataToClient(ServerPlayer player, SkillDataAttachment.SkillData skillData) {
        SyncSkillDataPacket packet = new SyncSkillDataPacket(
            skillData.getUnlockedSkillsSet(),
            skillData.getSkillLevelsMap(),
            skillData.getEquippedSkillsMap(),
            new HashMap<>()  // 空的冷却时间映射
        );
        
        PacketDistributor.sendToPlayer(player, packet);
        System.out.println("[Network] 已发送技能同步包到客户端");
    }
    
    /**
     * 给予玩家职业书
     */
    private static void giveJobBook(ServerPlayer player) {
        // 检查玩家背包中是否已有职业书
        for (ItemStack stack : player.getInventory().items) {
            if (!stack.isEmpty() && stack.getItem() instanceof JobBookItem) {
                return; // 已有职业书，不重复给予
            }
        }
        
        // 给予职业书
        ItemStack jobBook = new ItemStack(EndshopJob.JOB_BOOK.get());
        boolean added = player.getInventory().add(jobBook);
        
        if (added) {
            System.out.println("已给新玩家 " + player.getName().getString() + " 职业书");
        }
    }
}
