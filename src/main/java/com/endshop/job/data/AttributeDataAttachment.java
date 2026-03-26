package com.endshop.job.data;

import com.endshop.job.EndshopJob;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * 玩家属性数据附件 - 存储智识、力量、敏捷、意志
 */
public class AttributeDataAttachment {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, EndshopJob.MODID);

    // 默认属性值
    public static final int DEFAULT_WISDOM = 10;
    public static final int DEFAULT_STRENGTH = 10;
    public static final int DEFAULT_AGILITY = 10;
    public static final int DEFAULT_WILLPOWER = 10;

    /** 玩家属性数据附件类型 */
    public static final Supplier<AttachmentType<PlayerAttributes>> PLAYER_ATTRIBUTES =
            ATTACHMENT_TYPES.register("player_attributes", () ->
                    AttachmentType.builder(() -> new PlayerAttributes(
                            DEFAULT_WISDOM,
                            DEFAULT_STRENGTH,
                            DEFAULT_AGILITY,
                            DEFAULT_WILLPOWER
                    ))
                            .serialize(new IAttachmentSerializer<CompoundTag, PlayerAttributes>() {
                                @Override
                                public PlayerAttributes read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
                                    return new PlayerAttributes(
                                            tag.getInt("wisdom"),
                                            tag.getInt("strength"),
                                            tag.getInt("agility"),
                                            tag.getInt("willpower")
                                    );
                                }

                                @Override
                                public CompoundTag write(PlayerAttributes attributes, HolderLookup.Provider provider) {
                                    CompoundTag tag = new CompoundTag();
                                    tag.putInt("wisdom", attributes.wisdom());
                                    tag.putInt("strength", attributes.strength());
                                    tag.putInt("agility", attributes.agility());
                                    tag.putInt("willpower", attributes.willpower());
                                    return tag;
                                }
                            })
                            .build());

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }

    /** 获取玩家属性 */
    public static PlayerAttributes getAttributes(net.minecraft.world.entity.player.Player player) {
        return player.getData(PLAYER_ATTRIBUTES);
    }

    /** 设置玩家属性 */
    public static void setAttributes(net.minecraft.world.entity.player.Player player, PlayerAttributes attributes) {
        player.setData(PLAYER_ATTRIBUTES, attributes);
    }

    /** 设置智识 */
    public static void setWisdom(net.minecraft.world.entity.player.Player player, int value) {
        PlayerAttributes current = getAttributes(player);
        setAttributes(player, new PlayerAttributes(value, current.strength(), current.agility(), current.willpower()));
    }

    /** 设置力量 */
    public static void setStrength(net.minecraft.world.entity.player.Player player, int value) {
        PlayerAttributes current = getAttributes(player);
        setAttributes(player, new PlayerAttributes(current.wisdom(), value, current.agility(), current.willpower()));
    }

    /** 设置敏捷 */
    public static void setAgility(net.minecraft.world.entity.player.Player player, int value) {
        PlayerAttributes current = getAttributes(player);
        setAttributes(player, new PlayerAttributes(current.wisdom(), current.strength(), value, current.willpower()));
    }

    /** 设置意志 */
    public static void setWillpower(net.minecraft.world.entity.player.Player player, int value) {
        PlayerAttributes current = getAttributes(player);
        setAttributes(player, new PlayerAttributes(current.wisdom(), current.strength(), current.agility(), value));
    }

    /**
     * 玩家属性记录类
     */
    public record PlayerAttributes(int wisdom, int strength, int agility, int willpower) {

        public int getTotal() {
            return wisdom + strength + agility + willpower;
        }
    }
}
