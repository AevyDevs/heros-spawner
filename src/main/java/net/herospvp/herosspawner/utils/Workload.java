package net.herospvp.herosspawner.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Queue;

@RequiredArgsConstructor
public class Workload {

    private final Queue<WorkloadTask> tasks;
    @Getter
    private final Runnable callback;

    /**
     * This method executes a Workload
     *
     * @param stopTime The limit time
     * @return It is True if the execution is completed
     */
    public boolean execute(long stopTime) {
        while (!tasks.isEmpty() && System.currentTimeMillis() <= stopTime) {
            WorkloadTask workload = tasks.poll();
            if (workload == null) continue;
            workload.compute();
        }

        return tasks.isEmpty();
    }

    @Override
    public String toString() {
        return tasks.toString();
    }
}
