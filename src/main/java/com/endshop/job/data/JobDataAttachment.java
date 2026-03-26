package com.endshop.job.data;

import com.endshop.job.EndshopJob;
import com.endshop.job.profession.Profession;
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
 * 玩家职业数据附件 - 将职业数据持久化到玩家实体
 */
public class JobDataAttachment {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = 
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, EndshopJob.MODID);

    /** 玩家职业数据附件类型 */
    public static final Supplier<AttachmentType<String>> PLAYER_JOB = 
            ATTACHMENT_TYPES.register("player_job", () ->
                    AttachmentType.builder(() -> Profession.NONE.name())
                            .serialize(new IAttachmentSerializer<CompoundTag, String>() {
                                @Override
                                public String read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
                                    return tag.getString("job");
                                }

                                @Override
                                public CompoundTag write(String attachment, HolderLookup.Provider provider) {
                                    CompoundTag tag = new CompoundTag();
                                    tag.putString("job", attachment);
                                    return tag;
                                }
                            })
                            .build());

    /** 玩家职业等级数据附件类型 */
    public static final Supplier<AttachmentType<Integer>> PLAYER_JOB_LEVEL = 
            ATTACHMENT_TYPES.register("player_job_level", () ->
                    AttachmentType.builder(() -> 1) // 默认等级1
                            .serialize(new IAttachmentSerializer<CompoundTag, Integer>() {
                                @Override
                                public Integer read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
                                    return tag.getInt("level");
                                }

                                @Override
                                public CompoundTag write(Integer attachment, HolderLookup.Provider provider) {
                                    CompoundTag tag = new CompoundTag();
                                    tag.putInt("level", attachment);
                                    return tag;
                                }
                            })
                            .build());

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }

    /** 获取玩家的职业 */
    public static Profession getJob(net.minecraft.world.entity.player.Player player) {
        if (player == null) {
            return Profession.NONE;
        }
        try {
            String name = player.getData(PLAYER_JOB.get());
            return Profession.valueOf(name);
        } catch (Exception e) {
            EndshopJob.LOGGER.warn("获取职业时出错: {}", e.getMessage());
            return Profession.NONE;
        }
    }

    /** 设置玩家的职业 */
    public static void setJob(net.minecraft.world.entity.player.Player player, Profession profession) {
        if (player == null) return;
        try {
            player.setData(PLAYER_JOB.get(), profession.name());
            // 当设置新职业时，重置等级为1
            if (profession != Profession.NONE) {
                player.setData(PLAYER_JOB_LEVEL.get(), 1);
            }
            EndshopJob.LOGGER.info("设置玩家职业: {}", profession.name());
        } catch (Exception e) {
            EndshopJob.LOGGER.warn("设置职业时出错: {}", e.getMessage());
        }
    }

    /** 获取玩家的职业等级 */
    public static int getJobLevel(net.minecraft.world.entity.player.Player player) {
        if (player == null) {
            return 1;
        }
        try {
            return player.getData(PLAYER_JOB_LEVEL.get());
        } catch (Exception e) {
            EndshopJob.LOGGER.warn("获取职业等级时出错: {}", e.getMessage());
            return 1;
        }
    }

    /** 设置玩家的职业等级 */
    public static void setJobLevel(net.minecraft.world.entity.player.Player player, int level) {
        if (player == null) return;
        try {
            player.setData(PLAYER_JOB_LEVEL.get(), level);
        } catch (Exception e) {
            EndshopJob.LOGGER.warn("设置职业等级时出错: {}", e.getMessage());
        }
    }
}
