package net.herospvp.herosspawner.utils;

import com.google.common.collect.Queues;
import lombok.Setter;
import net.herospvp.herosspawner.objects.SpawnEntity;

import java.util.ArrayDeque;

public class WorkloadThread implements Runnable {

    private static final int MAX_MS_PER_TICK = 5;

    public WorkloadThread() {
        workloadDeque = Queues.newArrayDeque();
    }

    @Setter
    private ArrayDeque<SpawnEntity> workloadDeque;

    @Override
    public void run() {
        long stopTime = System.currentTimeMillis() + MAX_MS_PER_TICK;
        while (!workloadDeque.isEmpty() && System.currentTimeMillis() <= stopTime) {
            workloadDeque.poll().compute();
        }
    }

}
