package ru.shcherbatykh.manager;

public interface Action {
    String getNameCommandOfAction();
    boolean execute() throws Exception;
}
