package com.endshop.job.task;

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
 * 实体任务数据附件 - 将任务数据持久化到实体
 */
public class TaskDataAttachment {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = 
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, "endshopattribute");

    /** 实体任务数据附件类型 */
    public static final Supplier<AttachmentType<TaskData>> ENTITY_TASK = 
            ATTACHMENT_TYPES.register("entity_task", () ->
                    AttachmentType.builder(() -> new TaskData(TaskType.NONE))
                            .serialize(new IAttachmentSerializer<CompoundTag, TaskData>() {
                                @Override
                                public TaskData read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
                                    String taskTypeName = tag.getString("task_type");
                                    TaskType taskType = TaskType.fromDisplayName(taskTypeName);
                                    if (taskType == null) {
                                        taskType = TaskType.NONE;
                                    }
                                    return new TaskData(taskType);
                                }

                                @Override
                                public CompoundTag write(TaskData data, HolderLookup.Provider provider) {
                                    CompoundTag tag = new CompoundTag();
                                    tag.putString("task_type", data.getTaskType().getDisplayName());
                                    return tag;
                                }
                            })
                            .build());

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }

    /** 获取实体任务数据 */
    public static TaskData getTaskData(net.minecraft.world.entity.Entity entity) {
        return entity.getData(ENTITY_TASK);
    }

    /** 设置实体任务 */
    public static void setTask(net.minecraft.world.entity.Entity entity, TaskType taskType) {
        entity.setData(ENTITY_TASK, new TaskData(taskType));
    }

    /**
     * 任务数据类
     */
    public static class TaskData {
        private TaskType taskType;

        public TaskData(TaskType taskType) {
            this.taskType = taskType;
        }

        public TaskType getTaskType() {
            return taskType;
        }

        public void setTaskType(TaskType taskType) {
            this.taskType = taskType;
        }

        public boolean hasTask() {
            return taskType != TaskType.NONE;
        }
    }
}
