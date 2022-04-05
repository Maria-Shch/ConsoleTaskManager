package ru.shcherbatykh.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.models.Task;
import ru.shcherbatykh.utils.NotificationFrame;
import java.util.*;
import java.util.stream.Collectors;

@Component
public abstract class UserNotificationController {
    private Timer timer;
    private Manager manager;

    @Autowired
    public UserNotificationController(Timer timer, Manager manager) {
        this.timer = timer;
        this.manager = manager;
    }

    public void run(){
        List<Task> unscheduledTasks = manager.getListTasks()
                .stream()
                .filter(x -> (x.getNotificationDate().after(new Date())
                        && !manager.getScheduledTasks().keySet().contains(x)))
                .collect(Collectors.toList());

        if(!unscheduledTasks.isEmpty()){
            for (Task task : unscheduledTasks) {
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        getNotificationFrame().init(task);
                    }
                };
                timer.schedule(timerTask, task.getNotificationDate());
                manager.getScheduledTasks().put(task, timerTask);
            }
        }
    }

    @Lookup
    protected abstract NotificationFrame getNotificationFrame();
}
