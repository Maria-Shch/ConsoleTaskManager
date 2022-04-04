package ru.shcherbatykh.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
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

    @Bean
    public List<Task> getListTasks() throws Exception {
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
        Map<Integer, Action> actions = new HashMap<>();
        actions.put(1, printTasks(manager));
        actions.put(2, addingTask(manager));
        actions.put(3, removingTask(manager));
        actions.put(4, exit());
        return actions;
    }

    @Bean
    public Action addingTask(Manager manager) {
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                return "Добавить задачу";
            }

            @Override
            public boolean execute() throws Exception {
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
                return true;
            }
        };
    }

    @Bean
    public Action removingTask(Manager manager){
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                return "Удалить задачу";
            }
            @Override
            public boolean execute(){
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
                return true;
            }
        };
    }

    @Bean
    public Action printTasks(Manager manager){
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                return "Вывести список задач";
            }
            @Override
            public boolean execute(){
                getPrinter().printListTask(manager.getListTasks());
                return true;
            }
        };
    }

    @Bean
    public Action exit(){
        return new Action() {
            @Override
            public String getNameCommandOfAction() {
                return "Выйти";
            }
            @Override
            public boolean execute(){
                System.exit(0);
                return false;
            }
        };
    }

    @Bean
    public StringBuilder getMenu(Manager manager){
        StringBuilder menu = new StringBuilder();
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
        return new NotificationFrame();
    }

    @Bean
    public Timer getTimer(){
        return new Timer();
    }
}
