package ru.shcherbatykh.manager;

public interface Action {
    String getNameCommandOfAction();
    void execute() throws Exception;
}
