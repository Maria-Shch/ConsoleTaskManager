package ru.shcherbatykh.manager;

import ru.shcherbatykh.models.Task;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public interface Manager {
    List<Task> getListTasks();

    boolean addTask(String title, String description, Date date, String contactDetails) throws Exception;

    boolean removeTask(int indexTask);

    void removeAllTasks();

    Map<Task, TimerTask> getScheduledTasks();

    boolean completeTask(Task task);

    boolean isEmptyListTasks();

    boolean isPresentTaskByNumber(int numberOfTask);

    void updateNotificationDate(Task task, Date newDate);

    void saveListTaskToFile();

    void removeAllTasksWithElapsedTime(List<Task> listTaskWithElapsedTime);
}
