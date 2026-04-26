package com.endshop.job.tool;

import com.endshop.job.EndshopJob;
import com.endshop.job.entity.EndshopEntityTypes;
import com.endshop.job.entity.JetDroneEntity;
import com.endshop.job.network.SummonDronePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 工具管理器 - 统一管理各种工具的召唤逻辑
 */
public class ToolManager {
    
    private static long lastSummonTime = 0;
    private static final long COOLDOWN_MS = 30000; // 30秒冷却
    
    /**
     * 召唤射流无人机
     */
    public static void summonJetDrone() {
        Minecraft mc = Minecraft.getInstance();
        
        if (mc.player == null || mc.level == null) {
            return;
        }
        
        var player = mc.player;
        var level = mc.level;
        
        // 发送数据包到服务端，由服务端创建无人机
        PacketDistributor.sendToServer(new SummonDronePayload());
        
        EndshopJob.LOGGER.info("[终末地职业] 已发送召唤无人机指令到服务端");
        
        // 播放召唤音效（客户端）
        level.playSound(
            player,
            player.getX(), player.getY(), player.getZ(),
            SoundEvents.IRON_DOOR_OPEN,
            SoundSource.PLAYERS,
            0.8f,
            1.5f
        );
        
        // 生成召唤粒子效果（客户端）
        for (int i = 0; i < 20; i++) {
            double angle = Math.toRadians(i * 18);
            double radius = 1.5;
            
            double x = player.getX() + Math.cos(angle) * radius;
            double y = player.getY() + 1.0 + Math.random() * 0.5;
            double z = player.getZ() + Math.sin(angle) * radius;
            
            level.addParticle(
                ParticleTypes.END_ROD,
                x, y, z,
                0.0, 0.1, 0.0
            );
        }
        
        // ========== 提示信息 ==========
        
        player.sendSystemMessage(Component.literal(
            "§e§l🔧 召唤工具 §7- 已召唤 §b射流无人机§7，持续 §e30§7 秒"
        ));
    }
    
    /**
     * 检查是否在冷却中
     */
    public static boolean isOnCooldown() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastSummonTime) < COOLDOWN_MS;
    }
    
    /**
     * 获取剩余冷却时间（秒）
     */
    public static long getRemainingCooldown() {
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lastSummonTime;
        return Math.max(0, (COOLDOWN_MS - elapsed) / 1000);
    }
    
    /**
     * 记录召唤时间
     */
    public static void recordSummon() {
        lastSummonTime = System.currentTimeMillis();
    }
}
