package org.touchhome.bundle.api;

import com.pivovarit.function.*;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.touchhome.bundle.api.model.HasEntityIdentifier;
import org.touchhome.bundle.api.model.ProgressBar;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public interface EntityContextBGP {
    EntityContext getEntityContext();

    default ThreadContext<Void> schedule(@NotNull String name, int timeout, @NotNull TimeUnit timeUnit,
                                         @NotNull ThrowingRunnable<Exception> command, boolean showOnUI) {
        return schedule(name, timeout, timeUnit, command, showOnUI, false);
    }

    ThreadContext<Void> schedule(@NotNull String name, int timeout, @NotNull TimeUnit timeUnit,
                                 @NotNull ThrowingRunnable<Exception> command,
                                 boolean showOnUI, boolean hideOnUIAfterCancel);

    default ThreadContext<Void> run(@NotNull String name, @NotNull ThrowingRunnable<Exception> command, boolean showOnUI) {
        return runAndGet(name, () -> {
            command.run();
            return null;
        }, showOnUI);
    }

    <T> ThreadContext<T> runAndGet(@NotNull String name, @NotNull ThrowingSupplier<T, Exception> command, boolean showOnUI);


    void runOnceOnInternetUp(@NotNull String name, @NotNull ThrowingRunnable<Exception> command);

    /**
     * Await processes to be done
     */
    default <T> ThreadContext<Map<String, T>> awaitProcesses(@NotNull String name, int maxTimeToWaitInSeconds,
                                                             @NotNull List<ThreadContext<T>> processes,
                                                             @NotNull Logger logger,
                                                             @Nullable Consumer<Map<String, T>> finallyBlock) {
        return runAndGet(name, () -> {
            Map<String, T> result = new HashMap<>();
            for (ThreadContext<T> process : processes) {
                try {
                    result.put(process.getName(), process.await(maxTimeToWaitInSeconds, TimeUnit.SECONDS));
                } catch (Exception ex) {
                    result.put(process.getName(), null);
                    logger.error("Error while await <{}> for finish. Fire process termination", process.getName());
                    process.cancel();
                }
            }
            if (finallyBlock != null) {
                finallyBlock.accept(result);
            }
            return result;
        }, true);
    }

    default ThreadContext<Void> runWithProgress(@NotNull String key, boolean cancellable, @NotNull ThrowingConsumer<ProgressBar, Exception> command) {
        return runWithProgress(key, cancellable, command, null, null);
    }

    default <R> ThreadContext<R> runWithProgressAndGet(@NotNull String key, boolean cancellable, @NotNull ThrowingFunction<ProgressBar, R, Exception> command) {
        return runWithProgressAndGet(key, cancellable, command, null, null);
    }

    default <R> ThreadContext<R> runWithProgressAndGet(@NotNull String key, boolean cancellable, @NotNull ThrowingFunction<ProgressBar, R, Exception> command, @NotNull Consumer<Exception> finallyBlock) {
        return runWithProgressAndGet(key, cancellable, command, finallyBlock, null);
    }

    default ThreadContext<Void> runWithProgress(@NotNull String key,
                                                boolean cancellable,
                                                @NotNull ThrowingConsumer<ProgressBar, Exception> command,
                                                @NotNull Consumer<Exception> finallyBlock) {
        return runWithProgress(key, cancellable, command, finallyBlock, null);
    }

    default ThreadContext<Void> runWithProgress(@NotNull String key, boolean cancellable, @NotNull ThrowingConsumer<ProgressBar, Exception> command,
                                                @Nullable Consumer<Exception> finallyBlock, @Nullable Supplier<RuntimeException> throwIfExists) {
        return runWithProgressAndGet(key, cancellable, progressBar -> {
            command.accept(progressBar);
            return null;
        }, finallyBlock, throwIfExists);
    }

    default <R> ThreadContext<R> runWithProgressAndGet(@NotNull String key, boolean cancellable, @NotNull ThrowingFunction<ProgressBar, R, Exception> command, @Nullable Consumer<Exception> finallyBlock, @Nullable Supplier<RuntimeException> throwIfExists) {
        if (throwIfExists != null && isThreadExists(key, true)) {
            RuntimeException exception = throwIfExists.get();
            if (exception != null) {
                throw exception;
            }
        }
        return runAndGet(key, () -> {
            ProgressBar progressBar = (progress, message) -> getEntityContext().ui().progress(key, progress, message, cancellable);
            progressBar.progress(0, key);
            Exception exception = null;
            try {
                return command.apply(progressBar);
            } catch (Exception ex) {
                exception = ex;
                throw ex;
            } finally {
                progressBar.done();
                if (finallyBlock != null) {
                    finallyBlock.accept(exception);
                }
            }
        }, true);
    }

    ThreadContext<Void> runInfinite(@NotNull String name, @NotNull ThrowingRunnable<Exception> command, boolean showOnUI, int delay, boolean stopOnException);

    boolean isThreadExists(@NotNull String name, boolean checkOnlyRunningThreads);

    void cancelThread(@NotNull String name);

    <P extends HasEntityIdentifier, T> void runInBatch(@NotNull String batchName,
                                                       int maxTerminateTimeoutInSeconds,
                                                       @NotNull Collection<P> taskItems,
                                                       @NotNull Function<P, Callable<T>> callableProducer,
                                                       @NotNull Consumer<Integer> progressConsumer,
                                                       @NotNull Consumer<List<T>> finallyProcessBlockHandler);

    <T> List<T> runInBatchAndGet(@NotNull String batchName,
                                 int maxTerminateTimeoutInSeconds,
                                 int threadsCount,
                                 @NotNull Map<String, Callable<T>> runnableTasks,
                                 @NotNull Consumer<Integer> progressConsumer);

    interface ThreadContext<T> {
        String getName();

        String getState();

        void setState(@NotNull String state);

        String getDescription();

        void setDescription(@NotNull String description);

        boolean isStopped();

        void cancel();

        T await(long timeout, @NotNull TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException;

        void onError(@NotNull Consumer<Exception> errorListener);

        /**
         * Add value listener when run/schedule finished.
         *
         * @param name          - unique name of listener
         * @param valueListener - listener: First 2 parameters is value from run/schedule and previous value.
         *                      Return boolean where is remove listener or not after execute
         * @return true if listeners successfully added
         */
        boolean addValueListener(@NotNull String name, @NotNull ThrowingBiFunction<T, T, Boolean, Exception> valueListener);
    }
}
