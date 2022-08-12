package me.hsgamer.bettergui.api.process;

import me.hsgamer.hscore.task.BatchRunnable;

import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * A runnable applier for {@link BatchRunnable} with {@link UUID} as an executor
 */
public interface ProcessApplier extends BiConsumer<UUID, BatchRunnable.Process> {
}
