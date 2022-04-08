package ru.shcherbatykh.manager;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ru.shcherbatykh.models.Task;

public class ManagerImpl implements Manager{

    private List<Task> listTasks;
    private FileWorker fileWorker;
    private Map<Task, TimerTask> scheduledTasks = new HashMap<>();
    private static final Logger logger = Logger.getLogger(ManagerImpl.class);

    @Autowired
    public ManagerImpl(List<Task> listTasks, FileWorker fileWorker) {
        this.listTasks = listTasks;
        this.fileWorker = fileWorker;
    }

    @Override
    public List<Task> getListTasks() {
        return listTasks;
    }

    @Override
    public boolean addTask(String title, String description, Date date, String contactDetails){
        logger.debug("Method 'addTask' started working.");
        boolean isSuccessful = listTasks.add(new Task(title, description, date, contactDetails));
        fileWorker.saveListTaskToFile(listTasks);
        logger.info("New task '" + title + "' was added.");
        return isSuccessful;
    }

    @Override
    public boolean removeTask(int indexTask) {
        logger.debug("Method 'removeTask' started working.");
        Task task = listTasks.remove(indexTask);
        logger.info("Task '" + task.getTitle() + "' was removed.");
        if(scheduledTasks.containsKey(task)) cancelTimerForRemovedTask(task);
        fileWorker.saveListTaskToFile(listTasks);
        return task!=null;
    }

    @Override
    public void removeAllTasks() {
        logger.debug("Method 'removeAllTasks' started working.");
        for(Task task: listTasks){
            if(scheduledTasks.containsKey(task)) cancelTimerForRemovedTask(task);
        }
        listTasks.clear();
        logger.debug("List tasks was cleared.");
        fileWorker.saveListTaskToFile(listTasks);
    }

    @Override
    public boolean completeTask(Task task) {
        logger.debug("Method 'completeTask' started working.");
        boolean isSuccessful = listTasks.remove(task);
        logger.info("Task '" + task.getTitle() + "' was completed.");
        fileWorker.saveListTaskToFile(listTasks);
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
        fileWorker.saveListTaskToFile(listTasks);
    }

    @Override
    public void removeAllTasksWithElapsedTime(List<Task> listTaskWithElapsedTime) {
        logger.debug("Method 'removeAllTasksWithElapsedTime' started working.");
        listTasks.removeAll(listTaskWithElapsedTime);
        logger.info("All tasks with elapsed time was removed.");
        fileWorker.saveListTaskToFile(listTasks);
    }

    @Override
    public boolean isPresentTaskByNumber(int numberOfTask){
        logger.debug("Method 'isPresentTaskByNumber' started working.");
        return numberOfTask <= listTasks.size() && numberOfTask > 0;
    }
}
