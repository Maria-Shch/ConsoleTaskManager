package ru.shcherbatykh.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.shcherbatykh.models.Task;
import ru.shcherbatykh.utils.CommandUtils;

@Configuration
@ComponentScan(basePackages = "ru.shcherbatykh.manager")
public class Config {

    static final String PATH = "src/main/resources/tasks.json";

    @Bean
    public List<Task> getListTasks() throws Exception {
        JSONObject jsonObject = (JSONObject) readJsonFromFile(PATH);
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
    public Action addingTask(Manager manager){
        return new Action(){
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
                    date = getDate(dateStr, timeStr);
                    if (date != null) {
                        date = CommandUtils.checkDateNotPassed(date);
                    }
                }

                System.out.println("Введите контактные данные:");
                String contactDetails = CommandUtils.checkString();

                if (manager.addTask(title, description, date, contactDetails)) {
                    System.out.println("Задача успешно добавлена");
                }
                manager.saveListTaskToFile();
                return true;
            }
        };
    }

    @Bean
    public Action removingTask(Manager manager){
        return new Action(){
            public boolean execute() throws Exception {
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
                manager.saveListTaskToFile();
                return true;
            }
        };
    }

    @Bean
    public Action printTasks(Manager manager){
        return new Action(){
            public boolean execute() {
                getPrinter().printListTask(manager.getListTasks());
                return true;
            }
        };
    }

    @Bean
    public Action exit(){
        return new Action(){
            public boolean execute() {
                CommandUtils.exit();
                return false;
            }
        };
    }

    @Bean
    public Printer getPrinter(){
        return new PrinterImpl();
    }

    private Date getDate(String dateStr, String timeStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        dateFormat.setLenient(false);
        String dateAndTimeForParse = dateStr + " " + timeStr;
        Date date = null;

        try {
            date = dateFormat.parse(dateAndTimeForParse);
        } catch (java.text.ParseException ex) {
            date = null;
            System.out.println("Дата или время были введены неверно");
        }
        return date;
    }

    private static Object readJsonFromFile(String filename) throws FileNotFoundException, IOException {
        FileReader reader = null;
        try {
            reader = new FileReader(filename);
        } catch (FileNotFoundException ex) {
            File file = new File(filename);
            file.createNewFile();
        }
        reader = new FileReader(filename);
        JSONParser jsonParser = new JSONParser();
        try {
            return jsonParser.parse(reader);
        } catch (ParseException | IOException ex) {
            return null;
        }
    }
}
