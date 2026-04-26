package com.endshop.job.damage;

import com.endshop.job.EndshopJob;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

/**
 * 自定义伤害类型注册
 * 
 * 伤害类型分类:
 * - 物理伤害: 独立类型,由物理干员或部分技能造成
 * - 源石伤害: 包含四种元素
 *   - 热能(thermal)
 *   - 冰霜(frost)
 *   - 电磁(electromagnetic)
 *   - 自然(nature)
 */
public class ModDamageTypes {
    
    // ========== 物理伤害 ==========
    
    // ========== 源石伤害 ==========
    
    // 冰霜伤害(源石-冰霜元素)
    public static final ResourceKey<DamageType> FROST = 
            create("frost");
    
    // 热能伤害(源石-热能元素)
    public static final ResourceKey<DamageType> THERMAL = 
            create("thermal");
    
    // 电磁伤害(源石-电磁元素)
    public static final ResourceKey<DamageType> ELECTROMAGNETIC = 
            create("electromagnetic");
    
    // 自然伤害(源石-自然元素)
    public static final ResourceKey<DamageType> NATURE = 
            create("nature");
    
    private static ResourceKey<DamageType> create(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, 
                ResourceLocation.fromNamespaceAndPath(EndshopJob.MODID, name));
    }
}
