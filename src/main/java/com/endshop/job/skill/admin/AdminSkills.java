package com.endshop.job.skill.admin;

import com.endshop.job.skill.ActiveSkill;
import com.endshop.job.skill.Skill;
import com.endshop.job.skill.SkillRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * 管理员干员技能类
 */
public class AdminSkills {

    /**
     * 注册管理员干员的所有技能
     */
    public static void register() {
        // 注册毁伤序列（普通攻击）
        SkillRegistry.register(new DamageSequenceSkill());
        
        // 注册构成序列（普通技能）
        SkillRegistry.register(new CompositionSequenceSkill());
        
        // 注册锁闭序列（连携技能）
        SkillRegistry.register(new LockdownSequenceSkill());
        
        // 注册轰击序列（终结技能）
        SkillRegistry.register(new BombardmentSequenceSkill());
    }

    /**
     * 毁伤序列 - 普通攻击技能
     */
    public static class DamageSequenceSkill extends Skill {
        private static final double[] FIRST_HIT_MULTIPLIERS = {
            0.23, 0.25, 0.27, 0.29, 0.32, 0.34, 0.36, 0.39, 0.41, 0.44, 0.47, 0.51
        };
        private static final double[] SECOND_HIT_MULTIPLIERS = {
            0.27, 0.30, 0.32, 0.35, 0.38, 0.41, 0.43, 0.46, 0.49, 0.52, 0.56, 0.61
        };
        private static final double[] THIRD_HIT_MULTIPLIERS = {
            0.30, 0.33, 0.36, 0.39, 0.42, 0.45, 0.48, 0.51, 0.54, 0.58, 0.63, 0.68
        };
        private static final double[] FOURTH_HIT_MULTIPLIERS = {
            0.35, 0.38, 0.41, 0.45, 0.48, 0.52, 0.55, 0.59, 0.62, 0.67, 0.72, 0.78
        };
        private static final double[] FIFTH_HIT_MULTIPLIERS = {
            0.40, 0.44, 0.48, 0.52, 0.56, 0.60, 0.64, 0.68, 0.72, 0.77, 0.83, 0.90
        };
        private static final double[] EXECUTION_MULTIPLIERS = {
            4.00, 4.40, 4.80, 5.20, 5.60, 6.00, 6.40, 6.80, 7.20, 7.70, 8.30, 9.00
        };
        private static final double[] FALL_ATTACK_MULTIPLIERS = {
            0.80, 0.88, 0.96, 1.04, 1.12, 1.20, 1.28, 1.36, 1.44, 1.54, 1.66, 1.80
        };

        public DamageSequenceSkill() {
            super("admin_damage_sequence", "毁伤序列", "对敌人进行至多5段攻击，造成物理伤害。作为主控干员时，重击会造成18点失衡。", 12);
        }

        /**
         * 获取第一段攻击倍率
         */
        public double getFirstHitMultiplier(int level) {
            return getMultiplier(FIRST_HIT_MULTIPLIERS, level);
        }

        /**
         * 获取第二段攻击倍率
         */
        public double getSecondHitMultiplier(int level) {
            return getMultiplier(SECOND_HIT_MULTIPLIERS, level);
        }

        /**
         * 获取第三段攻击倍率
         */
        public double getThirdHitMultiplier(int level) {
            return getMultiplier(THIRD_HIT_MULTIPLIERS, level);
        }

        /**
         * 获取第四段攻击倍率
         */
        public double getFourthHitMultiplier(int level) {
            return getMultiplier(FOURTH_HIT_MULTIPLIERS, level);
        }

        /**
         * 获取第五段攻击倍率
         */
        public double getFifthHitMultiplier(int level) {
            return getMultiplier(FIFTH_HIT_MULTIPLIERS, level);
        }

        /**
         * 获取处决攻击倍率
         */
        public double getExecutionMultiplier(int level) {
            return getMultiplier(EXECUTION_MULTIPLIERS, level);
        }

        /**
         * 获取下落攻击倍率
         */
        public double getFallAttackMultiplier(int level) {
            return getMultiplier(FALL_ATTACK_MULTIPLIERS, level);
        }

        private double getMultiplier(double[] multipliers, int level) {
            if (level < 1 || level > multipliers.length) {
                return multipliers[0];
            }
            return multipliers[level - 1];
        }

        @Override
        public boolean isUnlocked() {
            return true; // 管理员技能默认解锁
        }

        @Override
        public Skill copy() {
            DamageSequenceSkill copy = new DamageSequenceSkill();
            copy.setCurrentLevel(getCurrentLevel());
            return copy;
        }
    }

    /**
     * 构成序列 - 普通技能
     */
    public static class CompositionSequenceSkill extends ActiveSkill {
        private static final double[] DAMAGE_MULTIPLIERS = {
            1.56, 1.71, 1.87, 2.02, 2.18, 2.34, 2.49, 2.65, 2.80, 3.00, 3.23, 3.50
        };

        public CompositionSequenceSkill() {
            super("admin_composition_sequence", "构成序列", "使用源石结晶冲击前方一定范围内的敌人，造成物理伤害和猛击。", 12, 10);
        }

        @Override
        public void execute(Player player) {
            // 实现技能效果
            Component message = Component.literal("§a使用了技能：构成序列！");
            player.sendSystemMessage(message);
            
            // 这里可以添加实际的技能效果代码
            // 例如：攻击周围敌人，造成伤害和失衡
        }

        /**
         * 获取伤害倍率
         */
        public double getDamageMultiplier(int level) {
            if (level < 1 || level > DAMAGE_MULTIPLIERS.length) {
                return DAMAGE_MULTIPLIERS[0];
            }
            return DAMAGE_MULTIPLIERS[level - 1];
        }

        /**
         * 获取失衡值
         */
        public int getImbalanceValue() {
            return 10;
        }

        /**
         * 获取技力回复
         */
        public int getSkillEnergyRestore() {
            return 50;
        }

        /**
         * 获取全队能量回复
         */
        public double getTeamEnergyRestore() {
            return 6.5;
        }

        @Override
        public boolean isUnlocked() {
            return true; // 管理员技能默认解锁
        }

        @Override
        public ActiveSkill copy() {
            CompositionSequenceSkill copy = new CompositionSequenceSkill();
            copy.setCurrentLevel(getCurrentLevel());
            return copy;
        }
    }

    /**
     * 锁闭序列 - 连携技能
     */
    public static class LockdownSequenceSkill extends ActiveSkill {
        private static final double[] DAMAGE_MULTIPLIERS = {
            0.45, 0.49, 0.54, 0.58, 0.62, 0.67, 0.71, 0.76, 0.80, 0.86, 0.93, 1.00
        };
        private static final double[] CRYSTAL_BREAK_MULTIPLIERS = {
            1.78, 1.96, 2.13, 2.31, 2.49, 2.67, 2.84, 3.02, 3.20, 3.42, 3.69, 4.00
        };
        private static final double[] DEFENSE_BREAK_MULTIPLIERS = {
            2.67, 2.94, 3.20, 3.47, 3.74, 4.00, 4.27, 4.54, 4.80, 5.14, 5.54, 6.00
        };
        private static final int[] SEAL_DURATIONS = {
            4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5
        };

        public LockdownSequenceSkill() {
            super("admin_lockdown_sequence", "锁闭序列", "冲到敌人身边，对其造成物理伤害，并附着源石结晶，在一段时间内将其封印。", 12, 16);
        }

        @Override
        public void execute(Player player) {
            // 实现技能效果
            Component message = Component.literal("§a使用了技能：锁闭序列！");
            player.sendSystemMessage(message);
            
            // 这里可以添加实际的技能效果代码
            // 例如：冲到敌人身边，造成伤害，附着源石结晶
        }

        /**
         * 获取伤害倍率
         */
        public double getDamageMultiplier(int level) {
            if (level < 1 || level > DAMAGE_MULTIPLIERS.length) {
                return DAMAGE_MULTIPLIERS[0];
            }
            return DAMAGE_MULTIPLIERS[level - 1];
        }

        /**
         * 获取击碎结晶伤害倍率
         */
        public double getCrystalBreakMultiplier(int level) {
            if (level < 1 || level > CRYSTAL_BREAK_MULTIPLIERS.length) {
                return CRYSTAL_BREAK_MULTIPLIERS[0];
            }
            return CRYSTAL_BREAK_MULTIPLIERS[level - 1];
        }

        /**
         * 获取破防增伤倍率
         */
        public double getDefenseBreakMultiplier(int level) {
            if (level < 1 || level > DEFENSE_BREAK_MULTIPLIERS.length) {
                return DEFENSE_BREAK_MULTIPLIERS[0];
            }
            return DEFENSE_BREAK_MULTIPLIERS[level - 1];
        }

        /**
         * 获取封印时间（秒）
         */
        public int getSealDuration(int level) {
            if (level < 1 || level > SEAL_DURATIONS.length) {
                return SEAL_DURATIONS[0];
            }
            return SEAL_DURATIONS[level - 1];
        }

        /**
         * 获取失衡值
         */
        public int getImbalanceValue() {
            return 10;
        }

        /**
         * 获取能量回复
         */
        public int getEnergyRestore() {
            return 10;
        }

        @Override
        public boolean isUnlocked() {
            return true; // 管理员技能默认解锁
        }

        @Override
        public ActiveSkill copy() {
            LockdownSequenceSkill copy = new LockdownSequenceSkill();
            copy.setCurrentLevel(getCurrentLevel());
            return copy;
        }
    }

    /**
     * 轰击序列 - 终结技能
     */
    public static class BombardmentSequenceSkill extends ActiveSkill {
        private static final double[] DAMAGE_MULTIPLIERS = {
            3.56, 3.91, 4.27, 4.62, 4.98, 5.33, 5.69, 6.04, 6.40, 6.84, 7.38, 8.00
        };
        private static final double[] EXTRA_DAMAGE_MULTIPLIERS = {
            2.67, 2.94, 3.20, 3.47, 3.74, 4.00, 4.27, 4.54, 4.80, 5.14, 5.54, 6.00
        };

        public BombardmentSequenceSkill() {
            super("admin_bombardment_sequence", "轰击序列", "使用源石结晶轰击地面，对前方扇形范围内的敌人造成大量物理伤害。", 12, 10);
        }

        @Override
        public void execute(Player player) {
            // 实现技能效果
            Component message = Component.literal("§a使用了技能：轰击序列！");
            player.sendSystemMessage(message);
            
            // 这里可以添加实际的技能效果代码
            // 例如：轰击地面，对前方扇形范围内的敌人造成伤害
        }

        /**
         * 获取伤害倍率
         */
        public double getDamageMultiplier(int level) {
            if (level < 1 || level > DAMAGE_MULTIPLIERS.length) {
                return DAMAGE_MULTIPLIERS[0];
            }
            return DAMAGE_MULTIPLIERS[level - 1];
        }

        /**
         * 获取额外伤害倍率
         */
        public double getExtraDamageMultiplier(int level) {
            if (level < 1 || level > EXTRA_DAMAGE_MULTIPLIERS.length) {
                return EXTRA_DAMAGE_MULTIPLIERS[0];
            }
            return EXTRA_DAMAGE_MULTIPLIERS[level - 1];
        }

        /**
         * 获取失衡值
         */
        public int getImbalanceValue() {
            return 25;
        }

        /**
         * 获取伤害免疫持续时间（秒）
         */
        public int getDamageImmunityDuration() {
            return 2;
        }

        @Override
        public boolean isUnlocked() {
            return true; // 管理员技能默认解锁
        }

        @Override
        public ActiveSkill copy() {
            BombardmentSequenceSkill copy = new BombardmentSequenceSkill();
            copy.setCurrentLevel(getCurrentLevel());
            return copy;
        }
    }
}
