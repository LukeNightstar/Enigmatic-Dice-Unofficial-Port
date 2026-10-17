package net.jrdemiurge.enigmaticdice.scheduler;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class Scheduler {
    private static final List<SchedulerTask> tasks = new ArrayList<>();
    private static final List<SchedulerTask> newTasks = new ArrayList<>();

    public static void schedule(Runnable task, int delay) {
        schedule(task, delay, 0, 0);
    }

    public static void schedule(Runnable task, int delay, int period, int repeatCount) {
        synchronized (tasks) {
            newTasks.add(new SchedulerTask(task, delay, period, repeatCount));
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        synchronized (newTasks) {
            if (!newTasks.isEmpty()) {
                tasks.addAll(newTasks);
                newTasks.clear();
            }
        }
        synchronized (tasks) {
            Iterator<SchedulerTask> iterator = tasks.iterator();
            while (iterator.hasNext()) {
                SchedulerTask st = iterator.next();
                int newTicks = st.getTicksRemaining() - 1;
                st.setTicksRemaining(newTicks);

                if (newTicks <= 0) {
                    st.getTask().run();

                    if (st.isRepeating()) {
                        st.decrementRepeatCount();
                        if (st.getRepeatCount() < 1) {
                            iterator.remove();
                            continue;
                        }
                        st.setTicksRemaining(st.getPeriod());
                    } else {
                        iterator.remove();
                    }
                }
            }
        }
    }
}
