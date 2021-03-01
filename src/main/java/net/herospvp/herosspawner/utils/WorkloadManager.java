package net.herospvp.herosspawner.utils;

import com.google.common.collect.Lists;
import net.herospvp.herosspawner.HerosSpawner;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.MinecraftServer;

import java.util.List;

public class WorkloadManager {

    private final List<Workload> workloads;
    private final HerosSpawner plugin;

    public WorkloadManager(HerosSpawner plugin) {
        this.plugin = plugin;
        this.workloads = Lists.newArrayList();
    }

    /**
     * This method starts the Workload scheduler
     */
    public void start() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (workloads.isEmpty()) {
                return;
            }

            long tickTime = (long) (MathHelper.a(MinecraftServer.getServer().h) * 1.0E-6D);
            long executeTime = 2000 - tickTime;
            if (executeTime < 10)
                executeTime = 10;

            long stopTime = System.currentTimeMillis() + executeTime;
            while (System.currentTimeMillis() < stopTime && !workloads.isEmpty()) {
                Workload workload = workloads.get(0);
                if (workload == null) continue;

                if (workload.execute(stopTime)) {
                    workload.getCallback().run();
                    workloads.remove(workload);
                }
            }
        }, 1, 1);
    }

    /**
     * This method adds a Workload to the List
     *
     * @param workload The Workload to add
     */
    public void addWorkload(Workload workload) {
        workloads.add(workload);
    }

}
