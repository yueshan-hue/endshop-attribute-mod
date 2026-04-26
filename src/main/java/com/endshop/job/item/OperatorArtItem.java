package com.endshop.job.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 干员美图物品
 */
public class OperatorArtItem extends Item {
    
    private final String operatorName;
    private final String artName;
    private final String artist;
    
    public OperatorArtItem(Properties properties, String operatorName, String artName, String artist) {
        super(properties);
        this.operatorName = operatorName;
        this.artName = artName;
        this.artist = artist;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.endshopattribute.operator_art.operator").append(": ").append(Component.literal(operatorName)));
        tooltipComponents.add(Component.translatable("item.endshopattribute.operator_art.title").append(": ").append(Component.literal(artName)));
        tooltipComponents.add(Component.translatable("item.endshopattribute.operator_art.artist").append(": ").append(Component.literal(artist)));
    }
}
