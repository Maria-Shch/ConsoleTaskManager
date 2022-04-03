package ru.shcherbatykh.manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.models.Task;
/*
Планировщик задач состоит из
журнала задач,
пользовательского интерфейса для добавления и удаления задачи, а также
системы оповещения пользователя о каком-то событии, т.е. в назаначенное
в планировщике время должно происходить нечто, говорящее пользователю о том,
что у него запланирована некоторая задача
    добавление задачи
    удаление задачи (после того как пользователь или удалил или завершил её)
    отложить задачу
    сохранение на диск в виде JSON
*/

@Component
@Qualifier
public class ManagerImpl implements Manager{
    @Autowired
    private List<Task> listTasks;
    private List<Task> listScheduledTasks = new ArrayList<>();

    @Override
    public List<Task> getListTasks() {
        return listTasks;
    }

    @Override
    public List<Task> getListScheduledTasks() {
        return listScheduledTasks;
    }

    @Override
    public boolean addTask(String title, String description, Date date, String contactDetails){
        boolean isSuccessful = listTasks.add(new Task(title, description, date, contactDetails));
        saveListTaskToFile();
        return isSuccessful;
    }

    @Override
    public boolean removeTask(int indexTask) {
        boolean isSuccessful = listTasks.remove(indexTask) != null;
        saveListTaskToFile();
        return isSuccessful;
    }

    @Override
    public boolean removeTask(Task task) {
        boolean isSuccessful = listTasks.remove(task);
        saveListTaskToFile();
        return isSuccessful;
    }

    @Override
    public boolean isEmptyListTasks(){
        return listTasks.isEmpty();
    }

    @Override
    public void updateNotificationDate(Task task, Date newDate){
        listTasks.remove(task);
        listTasks.add(new Task(task.getTitle(), task.getDescription(), newDate, task.getContactDetails()));
        saveListTaskToFile();
    }

    @Override
    public void saveListTaskToFile(){
        JSONObject jsonObj = new JSONObject();
        for (int i = 0; i < getListTasks().size(); i++) {
            jsonObj.put(i, getListTasks().get(i));
        }
        try {
            Files.write(Paths.get(Config.PATH), jsonObj.toJSONString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPresentTaskByNumber(int numberOfTask){
        return numberOfTask <= listTasks.size() && numberOfTask > 0;
    }

    @Override
    public boolean addTaskToListScheduledTasks(Task task) {
        return listScheduledTasks.add(task);
    }
}
