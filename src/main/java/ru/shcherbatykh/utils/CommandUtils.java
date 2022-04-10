package ru.shcherbatykh.utils;

import org.apache.log4j.Logger;
import org.json.simple.parser.JSONParser;
import ru.shcherbatykh.manager.Action;
import ru.shcherbatykh.manager.EditingAction;
import ru.shcherbatykh.manager.TaskRepo;
import ru.shcherbatykh.models.Task;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommandUtils {
    private static final Scanner in = new Scanner(System.in).useDelimiter("\n");
    private static final Logger logger = Logger.getLogger(CommandUtils.class);

    public static int readIntFromConsole(Action getMainMenu){
        logger.debug("Method 'readIntFromConsole' started working.");
        int val;
        System.out.println(" // Для возвращения к главному меню введите '0'");
        while (true) {
            if (in.hasNextInt()) {
                val = in.nextInt();
                if (Objects.equals(0,val)) getMainMenu.execute();
                break;
            } else {
                in.nextLine();
                System.out.println("Требуется значение типа int, попробуйте снова...");
            }
        }
        return val;
    }

    public static String readStringFromConsole(Action getMainMenu) {
        logger.debug("Method 'checkString' started working.");
        String str;
        System.out.println(" // Для возвращения к главному меню введите '0'");
        while (true) {
            if (in.hasNext()) {
                str = in.next();
                if (Objects.equals("0", str)) getMainMenu.execute();
                break;
            } else {
                in.nextLine();
                System.out.println("Требуется String значение, попробуйте снова...");
            }
        }
        return str;
    }

    public static int checkMenuSelect(int countActions){
        logger.debug("Method 'checkMenuSelect' started working.");
        int val;
        while (true) {
            if (in.hasNextInt()) {
                val = in.nextInt();
                if (val > 0 && val <= countActions) break;
                else System.out.println("Такой пункт в меню отсутвствует, попробуйте снова...\n");
            } else {
                in.nextLine();
                System.out.println("Требуется значение типа int, попробуйте снова...");
            }
        }
        return val;
    }

    public static Date checkDateNotPassed(Date date) {
        logger.debug("Method 'checkDateNotPassed' started working.");
        Date dateNow = new Date();
        if (dateNow.before(date)) return date;
        else {
            System.out.println("Вы ввели дату и время, которые уже прошли.");
            return null;
        }
    }

    public static Date getDateAfterProcessingUserInputInConsole(String dateStr, String timeStr) {
        logger.debug("Method 'getDateAfterProcessingUserInputInConsole' started working.");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        dateFormat.setLenient(false);
        String dateAndTimeForParse = dateStr + " " + timeStr;
        Date date = null;

        try {
            date = dateFormat.parse(dateAndTimeForParse);
        } catch (ParseException ex) {
            date = null;
            System.out.println("Дата или время были введены неверно");
        }
        return date;
    }

    public static Date getNewDateAfterUserChoiceInComboBox(Date previousNotificationDate, JComboBox box) {
        logger.debug("Method 'getNewDateAfterUserChoiceInComboBox' started working.");
        String item = (String) box.getSelectedItem();

        switch(item){
            case "Не откладывать":
                return new Date(previousNotificationDate.getTime());
            case "Отложить на 1 минуту":
                return new Date(previousNotificationDate.getTime()+(60*1000));
            case "Отложить на 5 минут":
                return new Date(previousNotificationDate.getTime()+(5*60*1000));
            case "Отложить на 10 минут":
                return new Date(previousNotificationDate.getTime()+(10*60*1000));
            case "Отложить на 30 минут":
                return new Date(previousNotificationDate.getTime()+(30*60*1000));
            case "Отложить на 45 минут":
                return new Date(previousNotificationDate.getTime()+(45*60*1000));
            case "Отложить на час":
                return new Date(previousNotificationDate.getTime()+(60*60*1000));
            default:
                throw new RuntimeException();
        }
    }

    public static Object readJsonFromFile(String path) throws IOException {
        logger.debug("Method 'readJsonFromFile' started working.");
        FileReader reader = null;
        try {
            reader = new FileReader(path);
        }
        catch (FileNotFoundException ex) {
            logger.warn("The file " + path + " was not found.", ex);
            File file = new File(path);
            if(file.length() == 0) return null;
            boolean isFileCreated = file.createNewFile();
            if(isFileCreated) logger.debug("The file " + path + " was created.");
            reader = new FileReader(path);
        }
        JSONParser jsonParser = new JSONParser();
        try {
            return jsonParser.parse(reader);
        }
        catch (org.json.simple.parser.ParseException ex) {
            logger.warn("File parsing " + path + " failed.", ex);
            return null;
        }
    }

    public static String getDateForPrint(Date notificationDate) {
        logger.debug("Method 'getDateForPrint' started working.");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy 'в' HH:mm (EEEE)");
        return dateFormat.format(notificationDate);
    }

    public static String getTitleFromUser(Action getMainMenu){
        System.out.println("Введите название задачи:");
        return readStringFromConsole(getMainMenu);
    }

    public static String getDescriptionFromUser(Action getMainMenu){
        System.out.println("Введите описание задачи:");
        return readStringFromConsole(getMainMenu);
    }

    public static Date getNotificationDateFromUser(Action getMainMenu){
        Date notificationDate = null;
        while (notificationDate == null) {
            System.out.println("Введите дату задачи в формате дд.мм.гггг:");
            String dateStr = readStringFromConsole(getMainMenu);

            System.out.println("Введите время задачи в формате чч:мм:");
            String timeStr = readStringFromConsole(getMainMenu);

            notificationDate = getDateAfterProcessingUserInputInConsole(dateStr, timeStr);
            if (notificationDate != null) {
                notificationDate = checkDateNotPassed(notificationDate);
            }
        }
        return notificationDate;
    }

    public static String getContactDetailsFromUser(Action getMainMenu){
        System.out.println("Введите контактные данные:");
        return readStringFromConsole(getMainMenu);
    }

    public static int getNumberOfTaskFromUser(Action getMainMenu){
        System.out.println("Введите номер задачи:");
        return readIntFromConsole(getMainMenu);
    }

    public static String getTextMenu(Map<Integer, Action> mapActions){
        logger.debug("Bean 'getTextMenu' was created.");
        StringBuilder textMenu = new StringBuilder();
        textMenu.append("\nМЕНЮ \n");
        Set<Integer> keys = mapActions.keySet();
        for (int i = 1; i <= keys.size(); i++) {
            textMenu.append(i + " - " + mapActions.get(i).getNameCommandOfAction() + "\n");
        }
        textMenu.append("\n");
        textMenu.append("Введите пункт меню:");
        return textMenu.toString();
    }

    public static void editTask(EditingAction editingAction, TaskRepo taskRepo, Action getMainMenu){
        int numberOfTask = getNumberOfTaskFromUser(getMainMenu);
        int indexOfTask = numberOfTask - 1;
        Task task = taskRepo.getListTasks().get(indexOfTask);
        if (taskRepo.isPresentTaskByNumber(numberOfTask)) editingAction.execute(task);
        else System.out.println("Задачи под таким номер не существует.");
    }
}
