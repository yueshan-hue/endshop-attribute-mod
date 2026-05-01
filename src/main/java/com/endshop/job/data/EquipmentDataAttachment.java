package com.endshop.job.data;

import com.endshop.job.EndshopJob;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 装备数据附件 - 管理玩家的装备槽位
 */
public class EquipmentDataAttachment {
    
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = 
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, EndshopJob.MODID);
    
    // 装备槽位数量：护甲(1) + 护手(1) + 配件(2) = 4
    private static final int EQUIPMENT_SLOTS = 4;
    
    // 槽位类型枚举
    public enum EquipmentSlot {
        ARMOR(0, "armor"),      // 护甲
        GAUNTLET(1, "gauntlet"), // 护手
        ACCESSORY_1(2, "accessory_1"), // 配件1
        ACCESSORY_2(3, "accessory_2"); // 配件2
        
        private final int index;
        private final String name;
        
        EquipmentSlot(int index, String name) {
            this.index = index;
            this.name = name;
        }
        
        public int getIndex() {
            return index;
        }
        
        public String getName() {
            return name;
        }
    }
    
    // 注册附件类型
    public static final Supplier<AttachmentType<EquipmentData>> EQUIPMENT_DATA = 
            ATTACHMENT_TYPES.register("equipment_data", () -> 
                    AttachmentType.builder(() -> new EquipmentData())
                            .serialize(new IAttachmentSerializer<CompoundTag, EquipmentData>() {
                                @Override
                                public EquipmentData read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
                                    EquipmentData data = new EquipmentData();
                                    data.deserializeNBT(tag, provider);
                                    return data;
                                }
                                
                                @Override
                                public CompoundTag write(EquipmentData attachment, HolderLookup.Provider provider) {
                                    return attachment.serializeNBT(provider);
                                }
                            })
                            .build());
    
    /**
     * 装备数据类
     */
    public static class EquipmentData {
        private List<String> equippedItems; // 已装备的物品ID列表
        
        public EquipmentData() {
            this.equippedItems = new ArrayList<>();
            // 初始化4个空槽位
            for (int i = 0; i < EQUIPMENT_SLOTS; i++) {
                this.equippedItems.add(null);
            }
        }
        
        /**
         * 获取指定槽位的装备物品ID
         */
        public String getEquippedItem(EquipmentSlot slot) {
            if (slot.getIndex() >= 0 && slot.getIndex() < equippedItems.size()) {
                return equippedItems.get(slot.getIndex());
            }
            return null;
        }
        
        /**
         * 设置指定槽位的装备物品ID
         */
        public void setEquippedItem(EquipmentSlot slot, String itemId) {
            if (slot.getIndex() >= 0 && slot.getIndex() < equippedItems.size()) {
                equippedItems.set(slot.getIndex(), itemId);
            }
        }
        
        /**
         * 卸下指定槽位的装备
         */
        public void unequipItem(EquipmentSlot slot) {
            setEquippedItem(slot, null);
        }
        
        /**
         * 检查槽位是否为空
         */
        public boolean isSlotEmpty(EquipmentSlot slot) {
            return getEquippedItem(slot) == null;
        }
        
        /**
         * 序列化到NBT
         */
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            CompoundTag tag = new CompoundTag();
            ListTag itemsList = new ListTag();
            
            for (String itemId : equippedItems) {
                if (itemId != null) {
                    itemsList.add(StringTag.valueOf(itemId));
                } else {
                    itemsList.add(StringTag.valueOf(""));
                }
            }
            
            tag.put("equipped_items", itemsList);
            return tag;
        }
        
        /**
         * 从NBT反序列化
         */
        public void deserializeNBT(CompoundTag tag, HolderLookup.Provider provider) {
            equippedItems.clear();
            
            if (tag.contains("equipped_items")) {
                ListTag itemsList = tag.getList("equipped_items", Tag.TAG_STRING);
                for (int i = 0; i < itemsList.size(); i++) {
                    String itemId = itemsList.getString(i);
                    if (itemId.isEmpty()) {
                        equippedItems.add(null);
                    } else {
                        equippedItems.add(itemId);
                    }
                }
            }
            
            // 确保有4个槽位
            while (equippedItems.size() < EQUIPMENT_SLOTS) {
                equippedItems.add(null);
            }
        }
        
        /**
         * 复制数据
         */
        public EquipmentData copy() {
            EquipmentData copy = new EquipmentData();
            copy.equippedItems = new ArrayList<>(this.equippedItems);
            return copy;
        }
    }
    
    /**
     * 获取实体的装备数据
     */
    public static EquipmentData getEquipmentData(Entity entity) {
        return entity.getData(EQUIPMENT_DATA);
    }
    
    /**
     * 注册附件类型
     */
    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
