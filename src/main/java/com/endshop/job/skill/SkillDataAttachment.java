package com.endshop.job.skill;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.*;
import java.util.function.Supplier;

/**
 * 玩家技能数据附件 - 管理玩家的技能数据
 */
public class SkillDataAttachment {
    
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = 
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, "endshopattribute");
    
    /** 玩家技能数据附件类型 */
    public static final Supplier<AttachmentType<SkillData>> PLAYER_SKILL_DATA = 
            ATTACHMENT_TYPES.register("player_skill_data", () ->
                    AttachmentType.builder(() -> new SkillData())
                            .serialize(new IAttachmentSerializer<ListTag, SkillData>() {
                                @Override
                                public SkillData read(IAttachmentHolder holder, ListTag tag, HolderLookup.Provider provider) {
                                    return new SkillData(tag);
                                }
                                
                                @Override
                                public ListTag write(SkillData attachment, HolderLookup.Provider provider) {
                                    return attachment.toNBT();
                                }
                            })
                            .build());
    
    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
    
    /**
     * 获取玩家的技能数据
     */
    public static SkillData getSkillData(net.minecraft.world.entity.player.Player player) {
        if (player == null) {
            return new SkillData();
        }
        return player.getData(PLAYER_SKILL_DATA.get());
    }
    
    /**
     * 技能数据存储类
     */
    public static class SkillData {
        /** 已解锁的技能 ID 列表 */
        private Set<String> unlockedSkills;
        
        /** 技能等级映射表 */
        private Map<String, Integer> skillLevels;
        
        /** 装备的技能槽位 <槽位索引，技能 ID> */
        private Map<Integer, String> equippedSkills;
        
        /** 技能冷却时间映射表 <技能 ID, 冷却时间> */
        private Map<String, Integer> skillCooldowns;
        
        public SkillData() {
            this.unlockedSkills = new HashSet<>();
            this.skillLevels = new HashMap<>();
            this.equippedSkills = new HashMap<>();
            this.skillCooldowns = new HashMap<>();
        }
        
        public SkillData(ListTag nbt) {
            this();
            fromNBT(nbt);
        }
        
        /**
         * 获取未加密的技能数据（用于网络同步）
         */
        public Set<String> getUnlockedSkillsSet() {
            return unlockedSkills;
        }
        
        public Map<String, Integer> getSkillLevelsMap() {
            return skillLevels;
        }
        
        public Map<Integer, String> getEquippedSkillsMap() {
            return equippedSkills;
        }
        
        /**
         * 设置技能数据（用于网络同步）
         */
        public void setSkillData(Set<String> unlockedSkills, Map<String, Integer> skillLevels, Map<Integer, String> equippedSkills) {
            this.unlockedSkills.clear();
            this.unlockedSkills.addAll(unlockedSkills);
            this.skillLevels.clear();
            this.skillLevels.putAll(skillLevels);
            this.equippedSkills.clear();
            this.equippedSkills.putAll(equippedSkills);
        }
        
        /**
         * 设置技能数据（包含冷却时间，用于网络同步）
         */
        public void setSkillData(Set<String> unlockedSkills, Map<String, Integer> skillLevels, 
                                 Map<Integer, String> equippedSkills, Map<String, Integer> skillCooldowns) {
            this.unlockedSkills.clear();
            this.unlockedSkills.addAll(unlockedSkills);
            this.skillLevels.clear();
            this.skillLevels.putAll(skillLevels);
            this.equippedSkills.clear();
            this.equippedSkills.putAll(equippedSkills);
            this.skillCooldowns.clear();
            this.skillCooldowns.putAll(skillCooldowns);
        }
        
        /**
         * 获取技能冷却时间
         */
        public int getSkillCooldown(String skillId) {
            return skillCooldowns.getOrDefault(skillId, 0);
        }
        
        /**
         * 设置技能冷却时间
         */
        public void setSkillCooldown(String skillId, int cooldown) {
            if (cooldown <= 0) {
                skillCooldowns.remove(skillId);
            } else {
                skillCooldowns.put(skillId, cooldown);
            }
        }
        
        /**
         * 解锁技能
         */
        public void unlockSkill(String skillId) {
            unlockedSkills.add(skillId);
            if (!skillLevels.containsKey(skillId)) {
                skillLevels.put(skillId, 1);
            }
        }
        
        /**
         * 锁定技能
         */
        public void lockSkill(String skillId) {
            unlockedSkills.remove(skillId);
        }
        
        /**
         * 检查技能是否已解锁
         */
        public boolean isUnlocked(String skillId) {
            return unlockedSkills.contains(skillId);
        }
        
        /**
         * 设置技能等级
         */
        public void setSkillLevel(String skillId, int level) {
            if (isUnlocked(skillId)) {
                skillLevels.put(skillId, level);
            }
        }
        
        /**
         * 获取技能等级
         */
        public int getSkillLevel(String skillId) {
            return skillLevels.getOrDefault(skillId, 1);
        }
        
        /**
         * 获取所有已解锁的技能
         */
        public Set<String> getUnlockedSkills() {
            return Collections.unmodifiableSet(unlockedSkills);
        }
        
        /**
         * 装备技能到指定槽位
         */
        public boolean equipSkill(String skillId, int slot) {
            if (slot < 0 || slot > 3) {
                return false; // 槽位无效
            }
            
            if (!isUnlocked(skillId)) {
                return false; // 未解锁该技能
            }
            
            // 移除该槽位原有的技能
            equippedSkills.remove(slot);
            
            // 装备新技能
            equippedSkills.put(slot, skillId);
            return true;
        }
        
        /**
         * 从指定槽位卸下技能
         */
        public void unequipSkill(int slot) {
            equippedSkills.remove(slot);
        }
        
        /**
         * 获取指定槽位的技能 ID
         */
        public String getEquippedSkill(int slot) {
            return equippedSkills.get(slot);
        }
        
        /**
         * 检查槽位是否已装备技能
         */
        public boolean isSlotOccupied(int slot) {
            return equippedSkills.containsKey(slot);
        }
        
        /**
         * 获取所有装备的技能
         */
        public Map<Integer, String> getEquippedSkills() {
            return Collections.unmodifiableMap(equippedSkills);
        }
        
        /**
         * 转换为 NBT
         */
        public ListTag toNBT() {
            ListTag listTag = new ListTag();
            
            for (String skillId : unlockedSkills) {
                CompoundTag skillTag = new CompoundTag();
                skillTag.putString("id", skillId);
                skillTag.putInt("level", skillLevels.getOrDefault(skillId, 1));
                
                // 写入装备信息（如果已装备）
                for (Map.Entry<Integer, String> entry : equippedSkills.entrySet()) {
                    if (entry.getValue().equals(skillId)) {
                        skillTag.putInt("slot", entry.getKey());
                        break;
                    }
                }
                
                // 写入冷却时间（如果存在）
                int cooldown = skillCooldowns.getOrDefault(skillId, 0);
                if (cooldown > 0) {
                    skillTag.putInt("cooldown", cooldown);
                }
                
                listTag.add(skillTag);
            }
            
            return listTag;
        }
        
        /**
         * 从 NBT 加载
         */
        public void fromNBT(ListTag nbt) {
            unlockedSkills.clear();
            skillLevels.clear();
            equippedSkills.clear();
            skillCooldowns.clear();
            
            for (int i = 0; i < nbt.size(); i++) {
                CompoundTag tag = nbt.getCompound(i);
                String skillId = tag.getString("id");
                int level = tag.getInt("level");
                
                unlockedSkills.add(skillId);
                skillLevels.put(skillId, level);
                
                // 读取装备信息（如果存在）
                if (tag.contains("slot")) {
                    int slot = tag.getInt("slot");
                    equippedSkills.put(slot, skillId);
                }
                
                // 读取冷却时间（如果存在）
                if (tag.contains("cooldown")) {
                    int cooldown = tag.getInt("cooldown");
                    skillCooldowns.put(skillId, cooldown);
                }
            }
        }
    }
}
