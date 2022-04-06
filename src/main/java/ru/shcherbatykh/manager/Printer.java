package ru.shcherbatykh.manager;

import ru.shcherbatykh.models.Task;

import java.util.List;

public interface Printer {
    void printListTask(List<Task> tasksList);
    void printTitleTasks(List<Task> tasksList);
}
