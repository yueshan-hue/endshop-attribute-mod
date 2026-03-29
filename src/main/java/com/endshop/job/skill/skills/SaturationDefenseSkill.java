package com.endshop.job.skill.skills;

import com.endshop.job.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * 重装干员 - 昼雪
 * 战技：饱和性防御 - 架盾格挡 + 友方庇护
 */
public class SaturationDefenseSkill extends ActiveSkill {
    
    public SaturationDefenseSkill() {
        super("saturation_defense", "Saturation Defense", "Shield block and protect allies", 10, 6, "saturation_defense");
    }
    
    @Override
    public void execute(Player player) {
        if (player == null) return;
        
        // 给予自身和周围友方庇护效果
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 2));
        
        // 对周围敌人造成寒冷伤害
        float damage = 6.0f * getCurrentLevel();
        player.level().getEntitiesOfClass(net.minecraft.world.entity.Mob.class, 
            player.getBoundingBox().inflate(5.0)).forEach(entity -> {
            entity.hurt(player.damageSources().playerAttack(player), damage);
            // 施加寒冷效果
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 2));
        });
        
        player.sendSystemMessage(Component.literal("§e§l饱和性防御 §7- 获得§a庇护 §7并造成 §c" + damage + "§7 点寒冷伤害"));
    }
    
    @Override
    public boolean isUnlocked() {
        return true;
    }
    
    @Override
    public ActiveSkill copy() {
        return new SaturationDefenseSkill();
    }
}
