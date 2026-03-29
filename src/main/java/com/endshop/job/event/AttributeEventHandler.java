package com.endshop.job.event;

import com.endshop.job.EndshopJob;
import com.endshop.job.data.AttributeDataAttachment;
import com.endshop.job.network.SyncSkillDataPacket;
import com.endshop.job.skill.ActiveSkill;
import com.endshop.job.skill.Skill;
import com.endshop.job.skill.SkillDataAttachment;
import com.endshop.job.skill.SkillRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

/**
 * 属性效果事件处理器
 */
@EventBusSubscriber
public class AttributeEventHandler {

    // 力量生命加成的修饰符ID
    private static final ResourceLocation STRENGTH_HEALTH_ID =
            ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, "strength_health_bonus");

    /**
     * 力量效果：每点力量提供 0.02 点额外生命值
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
    
        var attrs = AttributeDataAttachment.getAttributes(player);
        int strength = attrs.strength();
    
        // 获取最大生命值属性
        var maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttr != null) {
            // 移除旧的修饰符
            maxHealthAttr.removeModifier(STRENGTH_HEALTH_ID);
    
            // 添加新的修饰符（每点力量 +0.02 生命值）
            double bonusHealth = strength * 0.02;
            AttributeModifier modifier = new AttributeModifier(
                    STRENGTH_HEALTH_ID,
                    bonusHealth,
                    AttributeModifier.Operation.ADD_VALUE
            );
            maxHealthAttr.addPermanentModifier(modifier);
        }
            
        // 更新技能冷却时间
        updateSkillCooldowns(player);
    }
        
    /**
     * 更新玩家所有装备技能的冷却时间
     */
    private static void updateSkillCooldowns(Player player) {
        SkillDataAttachment.SkillData skillData = SkillDataAttachment.getSkillData(player);
        boolean needsSync = false;
            
        // 遍历所有 4 个技能槽位
        for (int slot = 0; slot < 4; slot++) {
            String skillId = skillData.getEquippedSkill(slot);
            if (skillId != null) {
                // 获取当前冷却时间
                int cooldown = skillData.getSkillCooldown(skillId);
                    
                // 减少冷却时间
                if (cooldown > 0) {
                    cooldown--;
                    skillData.setSkillCooldown(skillId, cooldown);
                    needsSync = true;
                }
            }
        }
            
        // 如果有技能冷却更新，同步到客户端
        if (needsSync) {
            // 构建冷却时间映射表
            Map<String, Integer> cooldownMap = new HashMap<>();
            for (int slot = 0; slot < 4; slot++) {
                String skillId = skillData.getEquippedSkill(slot);
                if (skillId != null) {
                    int cooldown = skillData.getSkillCooldown(skillId);
                    if (cooldown > 0) {
                        cooldownMap.put(skillId, cooldown);
                    }
                }
            }
                
            PacketDistributor.sendToPlayer(
                (ServerPlayer) player,
                new SyncSkillDataPacket(
                    skillData.getUnlockedSkillsSet(),
                    skillData.getSkillLevelsMap(),
                    skillData.getEquippedSkillsMap(),
                    cooldownMap
                )
            );
        }
    }

    /**
     * 敏捷效果：减少物理伤害
     * 智识效果：减少魔法伤害（通过伤害来源判断）
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        var attrs = AttributeDataAttachment.getAttributes(player);
        float originalDamage = event.getNewDamage();

        // 获取伤害来源
        var source = event.getSource();

        // 敏捷 - 物理伤害抗性 (每点敏捷减少0.14%物理伤害，无上限)
        // 物理伤害：不是魔法、不是摔落、不是溺水等
        if (!source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_ARMOR)
                && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_EFFECTS)) {
            int agility = attrs.agility();
            float physicalReduction = agility * 0.0014f; // 0.14%
            originalDamage *= (1.0f - physicalReduction);
        }

        // 智识 - 魔法伤害抗性 (每点智识减少0.14%魔法伤害，无上限)
        // 判断是否为魔法伤害（如药水效果、魔法攻击等）
        if (source.is(net.minecraft.tags.DamageTypeTags.WITCH_RESISTANT_TO)) {
            int wisdom = attrs.wisdom();
            float magicReduction = wisdom * 0.0014f; // 0.14%
            originalDamage *= (1.0f - magicReduction);
        }

        event.setNewDamage(originalDamage);
    }

    /**
     * 意志效果：增加治疗量 (每点意志增加0.2%治疗量，无上限)
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        var attrs = AttributeDataAttachment.getAttributes(player);
        int willpower = attrs.willpower();

        // 每点意志增加0.2%治疗量
        float healBonus = 1.0f + (willpower * 0.002f);
        float newHealAmount = event.getAmount() * healBonus;
        event.setAmount(newHealAmount);
    }
}
