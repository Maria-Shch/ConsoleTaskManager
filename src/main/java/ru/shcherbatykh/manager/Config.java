package ru.shcherbatykh.manager;

import java.util.*;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import ru.shcherbatykh.models.Task;
import ru.shcherbatykh.utils.CommandUtils;
import ru.shcherbatykh.utils.NotificationFrame;

@Configuration
@ComponentScan(basePackages = "ru.shcherbatykh.manager")
@PropertySource("classpath:taskManager.properties")
public class Config {
    @Autowired
    private Environment environment;
    static final String PATH = "src/main/resources/";
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
    public Action getUserInterface(){
        logger.debug("Bean 'getUserInterface' was created.");
        return new UserInterface(getMapActions(), getMenu(),getUserNotificationController());
    }

    @Bean
    public UserNotificationController getUserNotificationController(){
        logger.debug("Bean 'getUserNotificationController' was created.");
        return new UserNotificationController(getTimer(), getManager()) {
            @Override
            protected NotificationFrame getNotificationFrame() {
                return notificationFrame();
            }
        };
    }

    @Bean
    public Manager getManager(){
        return new ManagerImpl(getListTasks(),getFileWorker());
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

                System.out.println("Введите название новой задачи:");
                String title = CommandUtils.checkString(getUserInterface());

                System.out.println("Введите описание новой задачи:");
                String description = CommandUtils.checkString(getUserInterface());

                Date date = null;
                while (date == null) {
                    System.out.println("Введите дату новой задачи в формате дд.мм.гггг:");
                    String dateStr = CommandUtils.checkString(getUserInterface());

                    System.out.println("Введите время новой задачи в формате чч:мм:");
                    String timeStr = CommandUtils.checkString(getUserInterface());
                    date = CommandUtils.getDateAfterProcessingUserInputInConsole(dateStr, timeStr);
                    if (date != null) {
                        date = CommandUtils.checkDateNotPassed(date);
                    }
                }

                System.out.println("Введите контактные данные:");
                String contactDetails = CommandUtils.checkString(getUserInterface());

                try {
                if (getManager().addTask(title, description, date, contactDetails)) {
                    System.out.println("Задача успешно добавлена");
                }
                } catch (Exception ex) {
                    logger.warn("Adding a new task caused an exception", ex);
                }
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

                if (getManager().isEmptyListTasks()) {
                    System.out.println("Ваш список задач пуст, вы не можете ничего редактировать.");
                } else {
                    System.out.println("Редактирование задачи...\n");
                    getPrinter().printTitleTasks(getManager().getListTasks());
                    System.out.println("\n" + getEditingMenu().toString());
                    int menuSelect = CommandUtils.checkMenuSelect(getMapEditActions().size());
                    getMapEditActions().get(menuSelect).execute();
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
                if (getManager().isEmptyListTasks()) {
                    System.out.println("Ваш список задач пуст, вы не можете ничего удалить.");
                } else {
                    System.out.println("Удаление задачи...");
                    getPrinter().printTitleTasks(getManager().getListTasks());
                    System.out.println("Введите номер задачи:");
                    int numberOfTask = CommandUtils.checkInt(getUserInterface());
                    int indexOfTask = numberOfTask - 1;
                    if (getManager().isPresentTaskByNumber(numberOfTask)) {
                        if (getManager().removeTask(indexOfTask)) {
                            System.out.println("Задача под номером " + numberOfTask + " успешно удалена.");
                        } else {
                            System.out.println("Что-то пошло не так.");
                        }
                    } else {
                        System.out.println("Задачи под таким номер не существует.");
                    }
                }
            }
        };
    }

    @Bean
    public Action printAllTasks(){
        logger.debug("Bean 'printTasks' was created.");
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                logger.debug("Method 'getNameCommandOfAction' of 'printAllTasks' bean started working.");
                return "Вывести список задач";
            }

            @Override
            public void execute(){
                logger.debug("Method 'execute' of 'printAllTasks' bean started working.");
                getPrinter().printListTask(getManager().getListTasks());
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
                getPrinter().printListTask(getManager()
                        .getListTasks()
                        .stream()
                        .filter(x -> x.getNotificationDate().after(new Date()))
                        .collect(Collectors.toList()));
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
                if (getManager().isEmptyListTasks()) {
                    System.out.println("Ваш список задач пуст, вы не можете ничего удалить.");
                }else {
                    getManager().removeAllTasks();
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
                if (getManager().isEmptyListTasks()) {
                    System.out.println("Ваш список задач пуст, вы не можете ничего удалить.");
                }else {
                    List<Task> listTaskWithElapsedTime = getManager()
                                    .getListTasks()
                                    .stream()
                                    .filter(x -> x.getNotificationDate().before(new Date()))
                                    .collect(Collectors.toList());

                    getManager().removeAllTasksWithElapsedTime(listTaskWithElapsedTime);
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

                System.out.println("Введите номер задачи:");
                int numberOfTask = CommandUtils.checkInt(getUserInterface());
                int indexOfTask = numberOfTask - 1;

                if (getManager().isPresentTaskByNumber(numberOfTask)) {
                    System.out.println("Введите новое название задачи:");
                    String newTitle = CommandUtils.checkString(getUserInterface());
                    Task task = getManager().getListTasks().get(indexOfTask);
                    getManager().updateTitle(task, newTitle);
                } else {
                    System.out.println("Задачи под таким номер не существует.");
                }
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

                System.out.println("Введите номер задачи:");
                int numberOfTask = CommandUtils.checkInt(getUserInterface());
                int indexOfTask = numberOfTask - 1;

                if (getManager().isPresentTaskByNumber(numberOfTask)) {
                    System.out.println("Введите новое описание задачи:");
                    String newDescription = CommandUtils.checkString(getUserInterface());
                    Task task = getManager().getListTasks().get(indexOfTask);
                    getManager().updateDescription(task, newDescription);
                } else {
                    System.out.println("Задачи под таким номер не существует.");
                }
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

                System.out.println("Введите номер задачи:");
                int numberOfTask = CommandUtils.checkInt(getUserInterface());
                int indexOfTask = numberOfTask - 1;

                if (getManager().isPresentTaskByNumber(numberOfTask)) {
                    Date newDate = null;
                    while (newDate == null) {
                        System.out.println("Введите новую дату задачи в формате дд.мм.гггг:");
                        String dateStr = CommandUtils.checkString(getUserInterface());

                        System.out.println("Введите новое время задачи в формате чч:мм:");
                        String timeStr = CommandUtils.checkString(getUserInterface());
                        newDate = CommandUtils.getDateAfterProcessingUserInputInConsole(dateStr, timeStr);
                        if (newDate != null) {
                            newDate = CommandUtils.checkDateNotPassed(newDate);
                        }
                    }

                    Task task = getManager().getListTasks().get(indexOfTask);
                    getManager().updateNotificationDate(task, newDate);
                } else {
                    System.out.println("Задачи под таким номер не существует.");
                }
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

                System.out.println("Введите номер задачи:");
                int numberOfTask = CommandUtils.checkInt(getUserInterface());
                int indexOfTask = numberOfTask - 1;

                if (getManager().isPresentTaskByNumber(numberOfTask)) {
                    System.out.println("Введите новые контактные данные задачи:");
                    String newContactDetails = CommandUtils.checkString(getUserInterface());
                    Task task = getManager().getListTasks().get(indexOfTask);
                    getManager().updateContactDetails(task, newContactDetails);
                } else {
                    System.out.println("Задачи под таким номер не существует.");
                }
            }
        };
    }

    @Bean
    public Map<Integer, Action> getMapEditActions(){
        logger.debug("Bean 'getMapEditActions' was created.");
        Map<Integer, Action> editActions = new HashMap<>();
        editActions.put(1, editTitleTask());
        editActions.put(2, editDescriptionTask());
        editActions.put(3, editNotificationDateTask());
        editActions.put(4, editContactDetailsTask());
        editActions.put(5, getUserInterface());
        return editActions;
    }

    @Bean
    public StringBuilder getMenu(){
        logger.debug("Bean 'getMenu' was created.");
        StringBuilder menu = new StringBuilder();
        menu.append("\nМЕНЮ \n");
        Set<Integer> keys = getMapActions().keySet();
        for (int i = 1; i <= keys.size(); i++) {
            menu.append(i + " - " + getMapActions().get(i).getNameCommandOfAction() + "\n");
        }
        menu.append("\n");
        menu.append("Введите пункт меню:");
        return menu;
    }

    @Bean
    public StringBuilder getEditingMenu(){
        logger.debug("Bean 'getEditingMenu' was created.");
        StringBuilder editingMenu = new StringBuilder();
        Set<Integer> keys = getMapEditActions().keySet();
        for (int i = 1; i <= keys.size(); i++) {
            editingMenu.append(i + " - " + getMapEditActions().get(i).getNameCommandOfAction() + "\n");
        }
        editingMenu.append("\n");
        editingMenu.append("Введите пункт меню:");
        return editingMenu;
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
                    System.out.println(number + ". " + tasksList.get(i).getTitle() + " -- " + CommandUtils.getDateForPrint(tasksList.get(i).getNotificationDate()));
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
                    System.out.println(number + ". " + tasksList.get(i).getTitle() + " -- " + CommandUtils.getDateForPrint(tasksList.get(i).getNotificationDate()));
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
