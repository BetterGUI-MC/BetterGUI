package me.hsgamer.bettergui.api.process;

import me.hsgamer.hscore.task.element.TaskProcess;

import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * A runnable applier for {@link me.hsgamer.hscore.task.BatchRunnable} with {@link UUID} as an executor
 */
public interface ProcessApplier extends BiConsumer<UUID, TaskProcess> {
}
