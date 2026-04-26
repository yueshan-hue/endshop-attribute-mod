package com.endshop.job.skill;

/**
 * 技能槽位枚举 - 四个技能槽位
 */
public enum SkillSlot {
    
    /** 槽位 1 - 小键盘 1 */
    SLOT_1("slot_1", 0, "kp_1"),
    
    /** 槽位 2 - 小键盘 2 */
    SLOT_2("slot_2", 1, "kp_2"),
    
    /** 槽位 3 - 小键盘 3 */
    SLOT_3("slot_3", 2, "kp_3"),
    
    /** 槽位 4 - 小键盘 4 */
    SLOT_4("slot_4", 3, "kp_4");
    
    private final String name;
    private final int index;
    private final String keyBinding;
    
    SkillSlot(String name, int index, String keyBinding) {
        this.name = name;
        this.index = index;
        this.keyBinding = keyBinding;
    }
    
    /**
     * 获取槽位名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取槽位索引（0-3）
     */
    public int getIndex() {
        return index;
    }
    
    /**
     * 获取绑定的按键名称
     */
    public String getKeyBinding() {
        return keyBinding;
    }
    
    /**
     * 通过索引获取槽位
     */
    public static SkillSlot fromIndex(int index) {
        if (index < 0 || index >= values().length) {
            return null;
        }
        for (SkillSlot slot : values()) {
            if (slot.index == index) {
                return slot;
            }
        }
        return null;
    }
}
