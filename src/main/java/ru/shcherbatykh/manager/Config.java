package ru.shcherbatykh.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.shcherbatykh.models.Task;
import ru.shcherbatykh.utils.CommandUtils;
import ru.shcherbatykh.utils.NotificationFrame;

@Configuration
@ComponentScan(basePackages = "ru.shcherbatykh.manager")
//@PropertySource("src/main/resources/taskManager.properties")
public class Config {

    static final String PATH = "src/main/resources/tasks.json";
    private static final Logger logger = Logger.getLogger(Config.class);

    @Bean
    public List<Task> getListTasks() throws Exception {
        logger.debug("Начал работу бин getListTasks");
        JSONObject jsonObject = (JSONObject) CommandUtils.readJsonFromFile(PATH);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Task> tasks = new ArrayList<>();
        if (jsonObject != null && !jsonObject.isEmpty()) {
            for (int i = 0; i < jsonObject.keySet().size(); i++) {
                Task task = objectMapper.readValue(jsonObject.get(String.valueOf(i)).toString(), Task.class);
                tasks.add(task);
            }
        }
        return tasks;
    }

    @Bean
    public Map<Integer, Action> getMapActions(Manager manager){
        logger.debug("Начал работу бин getMapActions");
        Map<Integer, Action> actions = new HashMap<>();
        actions.put(1, printTasks(manager));
        actions.put(2, addingTask(manager));
        actions.put(3, removingTask(manager));
        actions.put(4, removingAllTasks(manager));
        actions.put(5, exit());
        return actions;
    }

    @Bean
    public Action addingTask(Manager manager) {
        logger.debug("Начал работу бин addingTask");
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                return "Добавить задачу";
            }

            @Override
            public void execute() throws Exception {
                System.out.println("Добавление задачи...");

                System.out.println("Введите название новой задачи:");
                String title = CommandUtils.checkString();

                System.out.println("Введите описание новой задачи:");
                String description = CommandUtils.checkString();

                Date date = null;
                while (date == null) {
                    System.out.println("Введите дату новой задачи в формате дд.мм.гггг:");
                    String dateStr = CommandUtils.checkString();

                    System.out.println("Введите время новой задачи в формате чч:мм:");
                    String timeStr = CommandUtils.checkString();
                    date = CommandUtils.getDateAfterProcessingUserInputInConsole(dateStr, timeStr);
                    if (date != null) {
                        date = CommandUtils.checkDateNotPassed(date);
                    }
                }

                System.out.println("Введите контактные данные:");
                String contactDetails = CommandUtils.checkString();

                if (manager.addTask(title, description, date, contactDetails)) {
                    System.out.println("Задача успешно добавлена");
                }
            }
        };
    }

    @Bean
    public Action removingTask(Manager manager){
        logger.debug("Начал работу бин removingTask");
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                return "Удалить задачу";
            }
            @Override
            public void execute(){
                if (manager.isEmptyListTasks()) {
                    System.out.println("Ваш список задач пуст, вы не можете ничего удалить.");
                } else {
                    System.out.println("Удаление задачи...");
                    System.out.println("Введите номер задачи:");
                    int numberOfTask = CommandUtils.checkInt();
                    int indexOfTask = numberOfTask - 1;
                    if (manager.isPresentTaskByNumber(numberOfTask)) {
                        if (manager.removeTask(indexOfTask)) {
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
    public Action printTasks(Manager manager){
        logger.debug("Начал работу бин printTasks");
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                return "Вывести список задач";
            }
            @Override
            public void execute(){
                getPrinter().printListTask(manager.getListTasks());
            }
        };
    }

    @Bean
    public Action removingAllTasks(Manager manager){
        logger.debug("Начал работу бин removingAllTasks");
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                return "Удалить все задачи";
            }
            @Override
            public void execute(){
                if (manager.isEmptyListTasks()) {
                    System.out.println("Ваш список задач пуст, вы не можете ничего удалить.");
                }else {
                    manager.getListTasks().clear();
                    logger.info("Все задачи удалены.");
                    System.out.println("Все задачи удалены.");
                }
            }
        };
    }

    @Bean
    public Action exit(){
        logger.debug("Начал работу бин exit");
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                return "Выйти";
            }
            @Override
            public void execute(){
                logger.info("=== Завершение работы приложения ===");
                System.exit(0);
            }
        };
    }

    @Bean
    public StringBuilder getMenu(Manager manager){
        logger.debug("Начал работу бин getMenu");
        StringBuilder menu = new StringBuilder();
        menu.append("\nМЕНЮ \n");
        Set<Integer> keys = getMapActions(manager).keySet();
        for (int i = 1; i <= keys.size(); i++) {
            menu.append(i + " - " + getMapActions(manager).get(i).getNameCommandOfAction() + "\n");
        }
        menu.append("\n");
        menu.append("Введите пункт меню:");
        return menu;
    }

    @Bean
    public Printer getPrinter(){
        logger.debug("Начал работу бин getPrinter");
        return tasksList -> {
            if (tasksList.isEmpty()) System.out.println("У вас нет ни одной задачи.");
            else {
                int number = 1;
                for (int i = 0; i < tasksList.size(); i++) {
                    System.out.println(number + ". " + tasksList.get(i).getTitle() + " -- " + tasksList.get(i).getDateForPrint());
                    System.out.println(tasksList.get(i).getDescription());
                    System.out.println(tasksList.get(i).getContactDetails() + "\n");
                    number++;
                }
            }
        };
    }

    @Bean
    @Scope("prototype")
    public NotificationFrame notificationFrame(){
        logger.debug("Начал работу бин notificationFrame");
        return new NotificationFrame();
    }

    @Bean
    public Timer getTimer(){
        logger.debug("Начал работу бин getTimer");
        return new Timer();
    }
}
