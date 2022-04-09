package ru.shcherbatykh.manager;

import ru.shcherbatykh.models.Task;

public interface EditingAction {
    void execute(Task task);
}
