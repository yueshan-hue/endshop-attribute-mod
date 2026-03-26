package com.endshop.job.profession;

/**
 * 职业枚举 - 明日方舟八大职业
 */
public enum Profession {

    // ─── 明日方舟八大职业 ─────────────────────────
    VANGUARD("先锋", "§a"),
    GUARD("近卫", "§c"),
    DEFENDER("重装", "§e"),
    SNIPER("狙击", "§b"),
    SPECIALIST("特种", "§5"),
    MEDIC("医疗", "§f"),
    CASTER("术师", "§9"),
    SUPPORTER("辅助", "§7"),

    // ─── 无职业（默认）────────────────────────────
    NONE("无职业", "§7");

    /** 中文显示名 */
    private final String displayName;
    /** 颜色代码前缀 */
    private final String colorCode;

    Profession(String displayName, String colorCode) {
        this.displayName = displayName;
        this.colorCode = colorCode;
    }

    /** 获取中文名称 */
    public String getDisplayName() {
        return displayName;
    }

    /** 获取带颜色的【职业名】前缀，用于Tab列表 */
    public String getColoredPrefix() {
        if (this == NONE) return "";
        return colorCode + "【" + displayName + "】§r";
    }

    /** 通过中文名查找职业（忽略大小写） */
    public static Profession fromDisplayName(String name) {
        for (Profession p : values()) {
            if (p.displayName.equals(name) || p.name().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    /** 获取所有可选职业（排除 NONE） */
    public static Profession[] selectableValues() {
        Profession[] all = values();
        Profession[] result = new Profession[all.length - 1];
        int idx = 0;
        for (Profession p : all) {
            if (p != NONE) result[idx++] = p;
        }
        return result;
    }
}
