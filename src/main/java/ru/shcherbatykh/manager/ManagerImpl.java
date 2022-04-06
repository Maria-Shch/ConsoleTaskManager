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
        logger.debug("Method 'addTask' started working.");
        boolean isSuccessful = listTasks.add(new Task(title, description, date, contactDetails));
        saveListTaskToFile();
        logger.info("New task '" + title + "' was added.");
        return isSuccessful;
    }

    @Override
    public boolean removeTask(int indexTask) {
        logger.debug("Method 'removeTask' started working.");
        Task task = listTasks.remove(indexTask);
        logger.info("Task '" + task.getTitle() + "' was removed.");
        if(scheduledTasks.containsKey(task)) cancelTimerForRemovedTask(task);
        saveListTaskToFile();
        return task!=null;
    }

    @Override
    public boolean completeTask(Task task) {
        logger.debug("Method 'completeTask' started working.");
        boolean isSuccessful = listTasks.remove(task);
        logger.info("Task '" + task.getTitle() + "' was completed.");
        saveListTaskToFile();
        return isSuccessful;
    }

    @Override
    public Map<Task, TimerTask> getScheduledTasks(){
        logger.debug("Method 'getScheduledTasks' started working.");
        return scheduledTasks;
    }

    public void cancelTimerForRemovedTask(Task task){
        logger.debug("Method 'cancelTimerForRemovedTask' started working.");
        scheduledTasks.get(task).cancel();
        logger.debug("TimerTask for the task " + task.getTitle() + " was canceled.");
    }

    @Override
    public boolean isEmptyListTasks(){
        logger.debug("Method 'isEmptyListTasks' started working.");
        return listTasks.isEmpty();
    }

    @Override
    public void  updateNotificationDate(Task task, Date newDate){
        logger.debug("Method 'updateNotificationDate' started working.");
        listTasks.remove(task);
        listTasks.add(new Task(task.getTitle(), task.getDescription(), newDate, task.getContactDetails()));
        logger.info("Time for the task " + task.getTitle() + " was updated. Previous time was " + task.getNotificationDate().toString() + ". New time is " + newDate.toString());
        saveListTaskToFile();
    }

    @Override
    public void saveListTaskToFile(){
        logger.debug("Method 'saveListTaskToFile' started working.");
        JSONObject jsonObj = new JSONObject();
        for (int i = 0; i < getListTasks().size(); i++) {
            jsonObj.put(i, getListTasks().get(i));
        }
        try {
            Files.write(Paths.get(Config.PATH), jsonObj.toJSONString().getBytes());
        } catch (IOException ex) {
            logger.warn("An exception was thrown while saving the list of tasks to a file.", ex);
        }
    }

    @Override
    public boolean isPresentTaskByNumber(int numberOfTask){
        logger.debug("Method 'isPresentTaskByNumber' started working.");
        return numberOfTask <= listTasks.size() && numberOfTask > 0;
    }
}
