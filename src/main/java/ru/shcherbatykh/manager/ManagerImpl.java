package ru.shcherbatykh.manager;

import java.nio.file.Files;
import java.nio.file.Paths;
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

    @Override
    public List<Task> getListTasks() {
        return listTasks;
    }

    @Override
    public boolean addTask(String title, String description, Date date, String contactDetails){
        return listTasks.add(new Task(title, description, date, contactDetails));
    }

    @Override
    public boolean removeTask(int indexTask){
        return listTasks.remove(indexTask) != null;
    }

    @Override
    public boolean isEmptyListTasks(){
        return listTasks.isEmpty();
    }

    @Override
    public void saveListTaskToFile() throws Exception {
        JSONObject jsonObj = new JSONObject();
        for (int i = 0; i < getListTasks().size(); i++) {
            jsonObj.put(i, getListTasks().get(i));
        }
        Files.write(Paths.get(Config.PATH), jsonObj.toJSONString().getBytes());
    }

    @Override
    public boolean isPresentTaskByNumber(int numberOfTask){
        return numberOfTask <= listTasks.size() && numberOfTask > 0;
    }
}
