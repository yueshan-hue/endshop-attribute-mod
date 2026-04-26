package com.endshop.job.skill.skills;

import com.endshop.job.entity.EndshopEntityTypes;
import com.endshop.job.entity.GeckoSkillEffectEntity;
import com.endshop.job.skill.ActiveSkill;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

/**
 * 冰晶技能 - 使用基岩版模型显示冰晶特效
 */
public class IceCrystalSkill extends ActiveSkill {
    
    public IceCrystalSkill() {
        super("ice_crystal", "冰晶", "召唤冰晶环绕，对敌人造成冰冻伤害", 10, 15, "ice");
    }
    
    @Override
    public void execute(Player player) {
        if (player == null) return;
        
        var level = player.level();
        
        // 计算伤害
        float damage = 15.0f * getCurrentLevel();
        
        // ========== 特效部分 ==========
        
        // 1. 播放冰霜音效
        level.playSound(
            null,
            player.getX(), player.getY(), player.getZ(),
            SoundEvents.GLASS_BREAK,
            SoundSource.PLAYERS,
            1.0f,
            1.2f
        );
        
        // 2. 生成冰晶特效实体（使用 GeckoLib 基岩版模型）
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(i * 60);
            double radius = 2.5;
            
            double x = player.getX() + Math.cos(angle) * radius;
            double y = player.getY() + 1.5;
            double z = player.getZ() + Math.sin(angle) * radius;
            
            // 创建冰晶特效实体
            GeckoSkillEffectEntity iceCrystal = new GeckoSkillEffectEntity(
                EndshopEntityTypes.GECKO_SKILL_EFFECT.get(), 
                level
            );
            
            iceCrystal.setPos(x, y, z);
            iceCrystal.setEffectType("ice_crystal"); // 对应 ice_crystal.geo.json
            iceCrystal.setLifetime(100); // 生存 5 秒
            
            level.addFreshEntity(iceCrystal);
            
            // 添加冰霜粒子效果
            for (int j = 0; j < 8; j++) {
                level.addParticle(
                    ParticleTypes.SNOWFLAKE,
                    x + (Math.random() - 0.5) * 0.8,
                    y + Math.random() * 1.5,
                    z + (Math.random() - 0.5) * 0.8,
                    0.0, 0.05, 0.0
                );
            }
        }
        
        // 3. 中心位置生成额外的冰晶爆发效果
        GeckoSkillEffectEntity centerCrystal = new GeckoSkillEffectEntity(
            EndshopEntityTypes.GECKO_SKILL_EFFECT.get(), 
            level
        );
        centerCrystal.setPos(player.getX(), player.getY() + 0.5, player.getZ());
        centerCrystal.setEffectType("ice_crystal");
        centerCrystal.setLifetime(60); // 生存 3 秒
        level.addFreshEntity(centerCrystal);
        
        // ========== 逻辑部分 ==========
        
        // 对范围内敌人造成伤害并施加减速效果
        level.getEntitiesOfClass(net.minecraft.world.entity.Mob.class, 
            player.getBoundingBox().inflate(5.0)).forEach(entity -> {
            
            entity.hurt(player.damageSources().playerAttack(player), damage);
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3));
        });
        
        player.sendSystemMessage(Component.literal("§b§l❄️ 冰晶技能 §7- 造成 §c" + damage + "§7 点冰冻伤害"));
    }
    
    @Override
    public boolean isUnlocked() {
        return true;
    }
    
    @Override
    public ActiveSkill copy() {
        return new IceCrystalSkill();
    }
}
