package com.endshop.job.skill.skills;

import com.endshop.job.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * 突击干员 - 莱万汀（Laevatain）
 * 战技：焚灭 - 熔核碎片持续攻击
 */
public class InfernoSkill extends ActiveSkill {
    
    public InfernoSkill() {
        super("inferno", "Inferno", "Molten core fragments continuous attack", 10, 8, "inferno");
    }
    
    @Override
    public void execute(Player player) {
        if (player == null) return;
        
        // 计算伤害
        float damage = 8.0f * getCurrentLevel();
        
        // 对周围敌人造成持续灼热伤害
        player.level().getEntitiesOfClass(net.minecraft.world.entity.Mob.class, 
            player.getBoundingBox().inflate(6.0)).forEach(entity -> {
            entity.hurt(player.damageSources().playerAttack(player), damage);
            entity.setRemainingFireTicks(entity.getRemainingFireTicks() + 100);
            // 施加虚弱效果
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1));
        });
        
        player.sendSystemMessage(Component.literal("§4§l焚灭 §7- 造成 §c" + damage + "§7 点熔核伤害并点燃敌人"));
    }
    
    @Override
    public boolean isUnlocked() {
        return true;
    }
    
    @Override
    public ActiveSkill copy() {
        return new InfernoSkill();
    }
}
