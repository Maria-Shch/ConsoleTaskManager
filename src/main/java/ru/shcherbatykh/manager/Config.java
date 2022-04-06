package ru.shcherbatykh.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
        logger.debug("Bean 'getListTasks' was created.");
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
    public Map<Integer, Action> getMapActions(){
        logger.debug("Bean 'getMapActions' was created.");
        Map<Integer, Action> actions = new HashMap<>();
        actions.put(1, printTasks());
        actions.put(2, addingTask());
        actions.put(3, removingTask());
        actions.put(4, removingAllTasks());
        actions.put(5, exit());
        return actions;
    }

    @Bean
    public Manager getManager(){
        return new ManagerImpl();
    }

    @Bean
    public Action addingTask() {
        logger.debug("Bean 'addingTask' was created.");
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

                if (getManager().addTask(title, description, date, contactDetails)) {
                    System.out.println("Задача успешно добавлена");
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
                return "Удалить задачу";
            }

            @Override
            public void execute(){
                if (getManager().isEmptyListTasks()) {
                    System.out.println("Ваш список задач пуст, вы не можете ничего удалить.");
                } else {
                    System.out.println("Удаление задачи...");
                    System.out.println("Введите номер задачи:");
                    int numberOfTask = CommandUtils.checkInt();
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
    public Action printTasks(){
        logger.debug("Bean 'printTasks' was created.");
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                return "Вывести список задач";
            }

            @Override
            public void execute(){
                getPrinter().printListTask(getManager().getListTasks());
            }
        };
    }

    @Bean
    public Action removingAllTasks(){
        logger.debug("Bean 'removingAllTasks' was created.");
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                return "Удалить все задачи";
            }

            @Override
            public void execute(){
                if (getManager().isEmptyListTasks()) {
                    System.out.println("Ваш список задач пуст, вы не можете ничего удалить.");
                }else {
                    getManager().getListTasks().clear();
                    logger.info("All tasks was removed.");
                    System.out.println("Все задачи удалены.");
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
                return "Выйти";
            }
            @Override
            public void execute(){
                logger.info("======== Application shutdown ========");
                System.exit(0);
            }
        };
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
    public Printer getPrinter(){
        logger.debug("Bean 'getPrinter' was created.");
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
        logger.debug("Bean 'notificationFrame' was created.");
        return new NotificationFrame();
    }

    @Bean
    public Timer getTimer(){
        logger.debug("Bean 'getTimer' was created.");
        return new Timer();
    }
}
