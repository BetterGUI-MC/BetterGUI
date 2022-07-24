package me.hsgamer.bettergui.api;

import me.hsgamer.hscore.task.BatchRunnable;

import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * A runnable applier for {@link BatchRunnable} with {@link UUID} as an executor
 */
public interface RunnableApplier extends BiConsumer<UUID, BatchRunnable> {
}
