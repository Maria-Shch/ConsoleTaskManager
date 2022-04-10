package ru.shcherbatykh.manager;

import java.util.*;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import ru.shcherbatykh.models.Task;
import ru.shcherbatykh.utils.*;

@Configuration
@ComponentScan(basePackages = "ru.shcherbatykh.manager")
@PropertySource("classpath:taskManager.properties")
public class Config {

    @Autowired
    private Environment environment;
    public static final String PATH = "src/main/resources/";
    private static final Logger logger = Logger.getLogger(Config.class);

    @Bean
    public FileWorker getFileWorker(){
        logger.debug("Bean 'getFileWorker' was created.");
        if(Objects.equals("json", environment.getProperty("data.source.type")))
            return new JsonFileWorker();
        else
            return new XmlFileWorker();
    }

    @Bean
    public List<Task> getListTasks(){
        logger.debug("Bean 'getListTasks' was created.");
        return getFileWorker().getListTasksFromFile();
    }

    @Bean
    public Map<Integer, Action> getMapActions(){
        logger.debug("Bean 'getMapActions' was created.");
        Map<Integer, Action> actions = new HashMap<>();
        actions.put(1, printAllTasks());
        actions.put(2, printActualTasks());
        actions.put(3, addingTask());
        actions.put(4, editingTask());
        actions.put(5, removingTask());
        actions.put(6, removingAllTasksWithElapsedTime());
        actions.put(7, removingAllTasks());
        actions.put(8, exit());
        return actions;
    }

    @Bean
    public Map<Integer, Action> getMapEditingActions(){
        logger.debug("Bean 'getMapEditingActions' was created.");
        Map<Integer, Action> editingActions = new HashMap<>();
        editingActions.put(1, editTitleTask());
        editingActions.put(2, editDescriptionTask());
        editingActions.put(3, editNotificationDateTask());
        editingActions.put(4, editContactDetailsTask());
        editingActions.put(5, getMainMenu());
        return editingActions;
    }

    @Bean
    public Action getMainMenu(){
        logger.debug("Bean 'getMainMenu' was created.");
        return new MainMenu(getMapActions(), getTextMenu(), getUserNotificationController());
    }

    @Bean
    public UserNotificationController getUserNotificationController(){
        logger.debug("Bean 'getUserNotificationController' was created.");
        return new UserNotificationController(getTimer(), getTaskRepo()) {
            @Override
            protected NotificationFrame getNotificationFrame() {
                return notificationFrame();
            }
        };
    }

    @Bean
    public TaskRepo getTaskRepo(){
        logger.debug("Bean 'getTaskRepo' was created.");
        return new TaskRepoImpl(getListTasks(), getFileWorker());
    }

    @Bean
    public Action addingTask() {
        logger.debug("Bean 'addingTask' was created.");
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                logger.debug("Method 'getNameCommandOfAction' of 'addingTask' bean started working.");
                return "Добавить задачу";
            }

            @Override
            public void execute(){
                logger.debug("Method 'execute' of 'addingTask' bean started working.");

                System.out.println("Добавление задачи...");
                String title = CommandUtils.getTitleFromUser(getMainMenu());
                String description = CommandUtils.getDescriptionFromUser(getMainMenu());
                Date notificationDate = CommandUtils.getNotificationDateFromUser((getMainMenu()));
                String contactDetails = CommandUtils.getContactDetailsFromUser(getMainMenu());

                getTaskRepo().addTask(title, description, notificationDate, contactDetails);
                System.out.println("Задача успешно добавлена");
            }
        };
    }

    @Bean
    public Action editingTask() {
        logger.debug("Bean 'editingTask' was created.");
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                logger.debug("Method 'getNameCommandOfAction' of 'editingTask' bean started working.");
                return "Редактировать задачу";
            }

            @Override
            public void execute(){
                logger.debug("Method 'execute' of 'editingTask' bean started working.");

                if (getTaskRepo().isEmptyListTasks()) {
                    System.out.println("Ваш список задач пуст, вы не можете ничего редактировать.");
                }
                else {
                    System.out.println("Редактирование задачи...\n" + "Список задач:");
                    getPrinter().printTitleTasks(getTaskRepo().getListTasks());
                    System.out.println(getTextEditingMenu());
                    int menuSelect = CommandUtils.checkMenuSelect(getMapEditingActions().size());
                    getMapEditingActions().get(menuSelect).execute();
                }
            }
        };
    }

    @Bean
    public Action removingTask(){
        logger.debug("Bean 'removingTask' was created.");
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                logger.debug("Method 'getNameCommandOfAction' of 'removingTask' bean started working.");
                return "Удалить задачу";
            }

            @Override
            public void execute(){
                logger.debug("Method 'execute' of 'removingTask' bean started working.");
                if (getTaskRepo().isEmptyListTasks()) {
                    System.out.println("Ваш список задач пуст, вы не можете ничего удалить.");
                } else {
                    System.out.println("Удаление задачи...");
                    getPrinter().printTitleTasks(getTaskRepo().getListTasks());
                    int numberOfTask = CommandUtils.getNumberOfTaskFromUser(getMainMenu());
                    int indexOfTask = numberOfTask - 1;

                    if (getTaskRepo().isPresentTaskByNumber(numberOfTask)) {
                        getTaskRepo().removeTask(indexOfTask);
                        System.out.println("Задача под номером " + numberOfTask + " успешно удалена.");
                    } else {
                        System.out.println("Задачи под таким номер не существует.");
                    }
                }
            }
        };
    }

    @Bean
    public Action printAllTasks(){
        logger.debug("Bean 'printAllTasks ' was created.");
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                logger.debug("Method 'getNameCommandOfAction' of 'printAllTasks' bean started working.");
                return "Вывести список задач";
            }

            @Override
            public void execute(){
                logger.debug("Method 'execute' of 'printAllTasks' bean started working.");
                getPrinter().printListTask(getTaskRepo().getListTasks());
            }
        };
    }

    @Bean
    public Action printActualTasks(){
        logger.debug("Bean 'printActualTasks' was created.");
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                logger.debug("Method 'getNameCommandOfAction' of 'printActualTasks' bean started working.");
                return "Вывести список актуальных задач";
            }

            @Override
            public void execute(){
                logger.debug("Method 'execute' of 'printActualTasks' bean started working.");
                getPrinter().printListTask(
                        getTaskRepo()
                        .getListTasks()
                        .stream()
                        .filter(x -> x.getNotificationDate().after(new Date()))
                        .collect(Collectors.toList())
                );
            }
        };
    }

    @Bean
    public Action removingAllTasks(){
        logger.debug("Bean 'removingAllTasks' was created.");
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                logger.debug("Method 'getNameCommandOfAction' of 'removingAllTasks' bean started working.");
                return "Удалить все задачи";
            }

            @Override
            public void execute(){
                logger.debug("Method 'execute' of 'removingAllTasks' bean started working.");
                if (getTaskRepo().isEmptyListTasks()) {
                    System.out.println("Ваш список задач пуст, вы не можете ничего удалить.");
                } else {
                    getTaskRepo().removeAllTasks();
                    logger.info("All tasks was removed.");
                    System.out.println("Все задачи удалены.");
                }
            }
        };
    }

    @Bean
    public Action removingAllTasksWithElapsedTime(){
        logger.debug("Bean 'removingAllTasksWithElapsedTime' was created.");
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                logger.debug("Method 'getNameCommandOfAction' of 'removingAllTasksWithElapsedTime' bean started working.");
                return "Удалить все задачи с прошедшим временем оповещения";
            }

            @Override
            public void execute(){
                logger.debug("Method 'execute' of 'removingAllTasksWithElapsedTime' bean started working.");
                if (getTaskRepo().isEmptyListTasks()) {
                    System.out.println("Ваш список задач пуст, вы не можете ничего удалить.");
                } else {
                    List<Task> listTaskWithElapsedTime = getTaskRepo()
                                    .getListTasks()
                                    .stream()
                                    .filter(x -> x.getNotificationDate().before(new Date()))
                                    .collect(Collectors.toList());

                    getTaskRepo().removeAllTasksWithElapsedTime(listTaskWithElapsedTime);
                    System.out.println("Все задачи с прошедшим временем оповещения удалены.");
                }
            }
        };
    }

    @Bean
    public Action exit(){
        logger.debug("Bean 'exit' was created.");
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                logger.debug("Method 'getNameCommandOfAction' of 'exit' bean started working.");
                return "Выйти";
            }
            @Override
            public void execute(){
                logger.debug("Method 'execute' of 'exit' bean started working.");
                logger.info("======== Application shutdown ========");
                System.exit(0);
            }
        };
    }

    @Bean
    public Action editTitleTask(){
        logger.debug("Bean 'editTitleTask' was created.");
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                logger.debug("Method 'getNameCommandOfAction' of 'editTitleTask' bean started working.");
                return "Редактировать название";
            }
            @Override
            public void execute(){
                logger.debug("Method 'execute' of 'editTitleTask' bean started working.");
                EditingAction editingAction = (Task task) -> {
                    String newTitle = CommandUtils.getTitleFromUser(getMainMenu());
                    getTaskRepo().updateTitle(task, newTitle);
                    System.out.println("Название задачи успешно изменено с '" + task.getTitle() + "' на '" + newTitle + "'." );
                };
                CommandUtils.editTask(editingAction, getTaskRepo(), getMainMenu());
            }
        };
    }

    @Bean
    public Action editDescriptionTask(){
        logger.debug("Bean 'editDescriptionTask' was created.");
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                logger.debug("Method 'getNameCommandOfAction' of 'editDescriptionTask' bean started working.");
                return "Редактировать описание";
            }
            @Override
            public void execute(){
                logger.debug("Method 'execute' of 'editDescriptionTask' bean started working.");
                EditingAction editingAction = (Task task) -> {
                    String newDescription = CommandUtils.getDescriptionFromUser(getMainMenu());
                    getTaskRepo().updateDescription(task, newDescription);
                    System.out.println("Описание задачи успешно изменено с '" + task.getDescription() + "' на '" + newDescription + "'." );
                };
                CommandUtils.editTask(editingAction, getTaskRepo(), getMainMenu());
            }
        };
    }

    @Bean
    public Action editNotificationDateTask(){
        logger.debug("Bean 'editNotificationDateTask' was created.");
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                logger.debug("Method 'getNameCommandOfAction' of 'editNotificationDateTask' bean started working.");
                return "Редактировать дату и время";
            }
            @Override
            public void execute(){
                logger.debug("Method 'execute' of 'editNotificationDateTask' bean started working.");
                EditingAction editingAction = (Task task) -> {
                    Date newDate = CommandUtils.getNotificationDateFromUser(getMainMenu());
                    getTaskRepo().updateNotificationDate(task, newDate);
                    System.out.println("дата и время задачи успешно изменено с '"
                            + CommandUtils.getDateForPrint(task.getNotificationDate())
                            + "' на '"
                            + CommandUtils.getDateForPrint(newDate) + "'." );
                };
                CommandUtils.editTask(editingAction, getTaskRepo(), getMainMenu());
            }
        };
    }

    @Bean
    public Action editContactDetailsTask(){
        logger.debug("Bean 'editContactDetailsTask' was created.");
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                logger.debug("Method 'getNameCommandOfAction' of 'editContactDetailsTask' bean started working.");
                return "Редактировать контактные данные";
            }
            @Override
            public void execute(){
                logger.debug("Method 'execute' of 'editContactDetailsTask' bean started working.");
                EditingAction editingAction = (Task task) -> {
                    String newContactDetails = CommandUtils.getContactDetailsFromUser(getMainMenu());
                    getTaskRepo().updateContactDetails(task, newContactDetails);
                    System.out.println("Контактные данные задачи успешно изменено с '" +
                            task.getContactDetails() + "' на '" + newContactDetails + "'." );
                };
                CommandUtils.editTask(editingAction, getTaskRepo(), getMainMenu());
            }
        };
    }

    @Bean
    public String getTextMenu(){
        logger.debug("Bean 'getTextMenu' was created.");
        return CommandUtils.getTextMenu(getMapActions());
    }

    @Bean
    public String getTextEditingMenu(){
        logger.debug("Bean 'getTextEditingMenu' was created.");
        return CommandUtils.getTextMenu(getMapEditingActions());
    }

    @Bean
    public Printer getPrinter(){
        logger.debug("Bean 'getPrinter' was created.");

        return new Printer() {
            @Override
            public void printListTask(List<Task> tasksList) {
                logger.debug("Method 'printListTask' of 'getPrinter' bean started working.");
                if (tasksList.isEmpty()) System.out.println("У вас нет ни одной задачи.");
                else {
                    int number = 1;
                    for (int i = 0; i < tasksList.size(); i++) {
                        System.out.println(number + ". " + tasksList.get(i).getTitle() + " -- "
                                + CommandUtils.getDateForPrint(tasksList.get(i).getNotificationDate()));
                        System.out.println(tasksList.get(i).getDescription());
                        System.out.println(tasksList.get(i).getContactDetails() + "\n");
                        number++;
                    }
                }
            }
            @Override
            public void printTitleTasks(List<Task> tasksList) {
                logger.debug("Method 'printTitleTasks' of 'getPrinter' started working.");
                int number = 1;
                for (int i = 0; i < tasksList.size(); i++) {
                    System.out.println(number + ". " + tasksList.get(i).getTitle() + " -- "
                            + CommandUtils.getDateForPrint(tasksList.get(i).getNotificationDate()));
                    number++;
                }
            }
        };
    }

    @Bean
    @Scope("prototype")
    public NotificationFrame notificationFrame(){
        logger.debug("Bean 'notificationFrame' was created.");
        return new NotificationFrame();
    }

    @Bean
    public Timer getTimer(){
        logger.debug("Bean 'getTimer' was created.");
        return new Timer();
    }
}