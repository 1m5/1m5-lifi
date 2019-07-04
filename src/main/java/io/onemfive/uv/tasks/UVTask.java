package io.onemfive.uv.tasks;

import io.onemfive.core.util.tasks.TaskRunner;
import io.onemfive.uv.UVSensor;

import java.util.Properties;

/**
 * A task for the Radio Sensor.
 */
abstract class UVTask {
    protected UVSensor sensor;
    protected TaskRunner taskRunner;
    protected Properties properties;
    protected long periodicity = 60 * 60 * 1000; // 1 hour as default
    protected long lastCompletionTime = 0L;
    protected boolean started = false;
    protected boolean completed = false;
    protected boolean longRunning = false;

    public UVTask(UVSensor sensor, TaskRunner taskRunner, Properties properties) {
        this.sensor = sensor;
        this.taskRunner = taskRunner;
        this.properties = properties;
        this.lastCompletionTime = System.currentTimeMillis();
    }

    public UVTask(UVSensor sensor, TaskRunner taskRunner, Properties properties, long periodicity) {
        this.sensor = sensor;
        this.taskRunner = taskRunner;
        this.properties = properties;
        this.lastCompletionTime = System.currentTimeMillis();
        this.periodicity = periodicity;
    }

    abstract boolean runTask();

    boolean isLongRunning() {return longRunning;}

    void setLastCompletionTime(long lastCompletionTime) {
        this.lastCompletionTime = lastCompletionTime;
    }

    long getLastCompletionTime() { return lastCompletionTime;}

    long getPeriodicity() {
        return periodicity;
    }
}
