package com.endshop.job.item;

import com.endshop.job.fluid.ModFluids;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;

/**
 * 侵蚀流体桶
 */
public class ErosionBucketItem extends BucketItem {
    
    public ErosionBucketItem() {
        super(ModFluids.EROSION_SOURCE.get(), new Item.Properties().stacksTo(1));
    }
}
