package ru.shcherbatykh.manager;

public abstract class ActionImpl implements Action{
    private String nameCommandOfAction;

    public ActionImpl(String nameCommandOfAction) {
        this.nameCommandOfAction = nameCommandOfAction;
    }

    public String getNameCommandOfAction() {
        return nameCommandOfAction;
    }
}
