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

    void updateTitle(Task task, String newTitle);

    void updateDescription(Task task, String newDescription);

    void updateNotificationDate(Task task, Date newDate);

    void updateContactDetails(Task task, String newContactDetails);

    void removeAllTasksWithElapsedTime(List<Task> listTaskWithElapsedTime);
}
