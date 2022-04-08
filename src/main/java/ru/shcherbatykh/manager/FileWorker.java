package ru.shcherbatykh.manager;

import ru.shcherbatykh.models.Task;
import java.util.List;

public interface FileWorker {
    List<Task> getListTasksFromFile();
    void saveListTaskToFile(List<Task> tasks);
}
