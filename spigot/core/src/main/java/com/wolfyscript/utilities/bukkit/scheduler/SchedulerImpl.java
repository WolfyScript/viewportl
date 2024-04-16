package com.wolfyscript.utilities.bukkit.scheduler;

import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.platform.scheduler.Task;
import org.bukkit.Bukkit;

public class SchedulerImpl implements com.wolfyscript.utilities.platform.scheduler.Scheduler {

    @Override
    public Task.Builder task(WolfyUtils wolfyUtils) {
        return new TaskImpl.BuilderImpl(wolfyUtils);
    }

    @Override
    public Task syncTask(WolfyUtils wolfyUtils, Runnable runnable, int delay) {
        return new TaskImpl(Bukkit.getScheduler().runTaskLater(((WolfyUtilsBukkit) wolfyUtils).getPlugin(), runnable, delay), wolfyUtils);
    }

    @Override
    public Task asyncTask(WolfyUtils wolfyUtils, Runnable runnable, int delay) {
        return new TaskImpl(Bukkit.getScheduler().runTaskLaterAsynchronously(((WolfyUtilsBukkit) wolfyUtils).getPlugin(), runnable, delay), wolfyUtils);
    }

    @Override
    public Task syncTimerTask(WolfyUtils wolfyUtils, Runnable runnable, int delay, int interval) {
        return new TaskImpl(Bukkit.getScheduler().runTaskTimer(((WolfyUtilsBukkit) wolfyUtils).getPlugin(), runnable, delay, interval), wolfyUtils);
    }

    @Override
    public Task asyncTimerTask(WolfyUtils wolfyUtils, Runnable runnable, int delay, int interval) {
        return new TaskImpl(Bukkit.getScheduler().runTaskTimerAsynchronously(((WolfyUtilsBukkit) wolfyUtils).getPlugin(), runnable, delay, interval), wolfyUtils);
    }

}
