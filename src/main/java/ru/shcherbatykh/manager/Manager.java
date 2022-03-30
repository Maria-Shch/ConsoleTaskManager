package ru.shcherbatykh.manager;

import java.util.Date;
import java.util.List;

public interface Manager {
    List<Task> getListTasks();

    boolean addTask(String title, String description, Date date, String contactDetails);

    boolean removeTask(int indexTask);

    boolean isEmptyListTasks();

    boolean isPresentTaskByNumber(int numberOfTask);

    void saveListTaskToFile() throws Exception;
}
