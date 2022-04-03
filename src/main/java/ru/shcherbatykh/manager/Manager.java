package ru.shcherbatykh.manager;

import ru.shcherbatykh.models.Task;

import java.util.Date;
import java.util.List;

public interface Manager {
    List<Task> getListTasks();

    List<Task> getListScheduledTasks();

    boolean addTask(String title, String description, Date date, String contactDetails) throws Exception;

    boolean removeTask(int indexTask);

    boolean removeTask(Task task);

    boolean isEmptyListTasks();

    boolean isPresentTaskByNumber(int numberOfTask);

    boolean addTaskToListScheduledTasks(Task task);

    void updateNotificationDate(Task task, Date newDate);

    void saveListTaskToFile();
}
