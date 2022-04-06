package ru.shcherbatykh.manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.models.Task;

@Component
@Qualifier
public class ManagerImpl implements Manager{
    @Autowired
    private List<Task> listTasks;
    private Map<Task, TimerTask> scheduledTasks = new HashMap<>();
    private static final Logger logger = Logger.getLogger(ManagerImpl.class);

    @Override
    public List<Task> getListTasks() {
        return listTasks;
    }

    @Override
    public boolean addTask(String title, String description, Date date, String contactDetails){
        logger.debug("Начал работу метод addTask");
        boolean isSuccessful = listTasks.add(new Task(title, description, date, contactDetails));
        saveListTaskToFile();
        logger.info("Добавлена новая задача: " + title);
        return isSuccessful;
    }

    @Override
    public boolean removeTask(int indexTask) {
        logger.debug("Начал работу метод removeTask");
        Task task = listTasks.remove(indexTask);
        logger.info("Удалена задача: " + task.getTitle());
        if(scheduledTasks.containsKey(task)) cancelTimerForRemovedTask(task);
        saveListTaskToFile();
        return task!=null;
    }

    @Override
    public boolean completeTask(Task task) {
        logger.debug("Начал работу метод completeTask");
        boolean isSuccessful = listTasks.remove(task);
        logger.info("Завершена задача: " + task.getTitle());
        saveListTaskToFile();
        return isSuccessful;
    }

    @Override
    public Map<Task, TimerTask> getScheduledTasks(){
        logger.debug("Начал работу метод getScheduledTasks");
        return scheduledTasks;
    }

    public void cancelTimerForRemovedTask(Task task){
        logger.debug("Начал работу метод cancelTimerForRemovedTask");
        scheduledTasks.get(task).cancel();
        logger.debug("Отменён timerTask для задачи " + task.getTitle());
    }

    @Override
    public boolean isEmptyListTasks(){
        logger.debug("Начал работу метод isEmptyListTasks");
        return listTasks.isEmpty();
    }

    @Override
    public void  updateNotificationDate(Task task, Date newDate){
        logger.debug("Начал работу метод updateNotificationDate");
        listTasks.remove(task);
        listTasks.add(new Task(task.getTitle(), task.getDescription(), newDate, task.getContactDetails()));
        logger.info("Обновлено время для задачи " + task.getTitle() + ". Предыдущее время " + task.getDateForPrint() + ". Новое время " + newDate.toString());
        saveListTaskToFile();
    }

    @Override
    public void saveListTaskToFile(){
        logger.debug("Начал работу метод saveListTaskToFile");
        JSONObject jsonObj = new JSONObject();
        for (int i = 0; i < getListTasks().size(); i++) {
            jsonObj.put(i, getListTasks().get(i));
        }
        try {
            Files.write(Paths.get(Config.PATH), jsonObj.toJSONString().getBytes());
        } catch (IOException ex) {
            logger.warn("Было выброшено исключение при сохранении списка задач в файл", ex);
        }
    }

    @Override
    public boolean isPresentTaskByNumber(int numberOfTask){
        logger.debug("Начал работу метод isPresentTaskByNumber");
        return numberOfTask <= listTasks.size() && numberOfTask > 0;
    }
}
