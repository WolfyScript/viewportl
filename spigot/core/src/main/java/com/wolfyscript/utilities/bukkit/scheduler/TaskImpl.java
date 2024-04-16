package com.wolfyscript.utilities.bukkit.scheduler;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.platform.scheduler.Scheduler;
import com.wolfyscript.utilities.platform.scheduler.Task;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class TaskImpl implements Task {

    private final BukkitTask task;
    private final WolfyUtils api;

    public TaskImpl(BukkitTask task, WolfyUtils wolfyUtils) {
        this.task = task;
        this.api = wolfyUtils;
    }

    @Override
    public void cancel() {
        task.cancel();
    }

    @Override
    public WolfyUtils api() {
        return api;
    }

    static class BuilderImpl implements Task.Builder {

        private final WolfyUtils api;
        private boolean async = false;
        private long delay = 0;
        private long interval = -1;
        private Runnable taskRunnable;
        private Consumer<Task> executor;

        BuilderImpl(WolfyUtils wolfyUtils) {
            this.api = wolfyUtils;
        }

        @Override
        public Builder async() {
            this.async = true;
            return this;
        }

        @Override
        public Builder delay(long ticks) {
            this.delay = Math.max(ticks, 0);
            return this;
        }

        @Override
        public Builder interval(long ticks) {
            this.interval = Math.max(ticks, 1);
            return this;
        }

        @Override
        public Builder execute(Runnable runnable) {
            this.taskRunnable = runnable;
            return this;
        }

        @Override
        public Builder execute(Consumer<Task> consumer) {
            this.executor = consumer;
            return this;
        }

        @Override
        public Task build() {
            BukkitScheduler scheduler = Bukkit.getScheduler();
            Plugin plugin = ((WolfyUtilsBukkit) api).getPlugin();

            // An interval of -1 causes the following tasks to act as a non-timer task
            if (executor != null) {
                // If we use an executor we cannot use the Bukkit scheduler methods, that also use executors, because they do not return the task!
                // Instead, we use our own Runnable wrapper that provides the task to the executor.
                SelfSupplyRunnable selfSupplyRunnable = new SelfSupplyRunnable(executor);
                Task task = new TaskImpl(constructTaskFromSettings(scheduler, selfSupplyRunnable, plugin), api);
                selfSupplyRunnable.setTask(task);
                return task;
            }

            return new TaskImpl(constructTaskFromSettings(scheduler, taskRunnable, plugin), api);
        }

        private BukkitTask constructTaskFromSettings(BukkitScheduler scheduler, Runnable runnable, Plugin plugin) {
            if (async) {
                return scheduler.runTaskTimerAsynchronously(plugin, runnable, delay, interval);
            }
            return scheduler.runTaskTimer(plugin, runnable, delay, interval);
        }

    }

    private static class SelfSupplyRunnable implements Runnable {

        private Task task;
        private final Consumer<Task> executor;

        public SelfSupplyRunnable(Consumer<Task> executor) {
            Preconditions.checkArgument(executor != null, "Invalid Runnable: Must provide a executor!");
            this.executor = executor;
        }

        public void setTask(Task task) {
            this.task = task;
        }

        public Task getTask() {
            return task;
        }

        @Override
        public void run() {
            if (task == null) return;
            executor.accept(task);
        }
    }

}
