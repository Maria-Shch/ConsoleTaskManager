package ru.shcherbatykh.utils;

import org.apache.log4j.Logger;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.shcherbatykh.manager.Action;
import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Scanner;

public class CommandUtils {
    private static final Scanner in = new Scanner(System.in).useDelimiter("\n");
    private static final Logger logger = Logger.getLogger(CommandUtils.class);

    public static int checkInt(Action actionToExit){
        logger.debug("Method 'checkInt' started working.");
        int val;
        System.out.println(" // Для возвращения к главному меню введите '0'");
        while (true) {
            if (in.hasNextInt()) {
                val = in.nextInt();
                if (Objects.equals(0,val)) actionToExit.execute();
                break;
            } else {
                in.nextLine();
                System.out.println("Требуется значение типа int, попробуйте снова...");
            }
        }
        return val;
    }

    public static int checkMenuSelect(int countActions){
        logger.debug("Method 'checkMenuSelect' started working.");
        int val;
        while (true) {
            if (in.hasNextInt()) {
                val = in.nextInt();
                if(val > 0 && val <= countActions) break;
                else{
                    System.out.println("Такой пункт в меню отсутвствует, попробуйте снова...\n");
                }
            } else {
                in.nextLine();
                System.out.println("Требуется значение типа int, попробуйте снова...");
            }
        }
        return val;
    }

    public static String checkString(Action actionToExit){
        logger.debug("Method 'checkString' started working.");
        String str;
        System.out.println(" // Для возвращения к главному меню введите '0'");
        while (true) {
            if (in.hasNext()) {
                str = in.next();
                if(Objects.equals("0", str)) actionToExit.execute();
                break;
            } else {
                in.nextLine();
                System.out.println("Требуется String значение, попробуйте снова...");
            }
        }
        return str;
    }

    public static Date checkDateNotPassed(Date date) {
        logger.debug("Method 'checkDateNotPassed' started working.");
        Date dateNow = new Date();
        if (dateNow.before(date)) {
            return date;
        } else {
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
        } catch (java.text.ParseException ex) {
            date = null;
            System.out.println("Дата или время были введены неверно");
        }
        return date;
    }

    public static Date getNewDateAfterUserChoiceInComboBox(Date previousNotificationDate, JComboBox box) {
        logger.debug("Method 'getNewDateAfterUserChoiceInComboBox' started working.");
        String item = (String)box.getSelectedItem();

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
        } catch (FileNotFoundException ex) {
            logger.warn("The file " + path + " was not found.", ex);
            File file = new File(path);
            boolean isFileCreated = file.createNewFile();
            if(isFileCreated) logger.debug("The file " + path + " was created.");
        }
        reader = new FileReader(path);
        JSONParser jsonParser = new JSONParser();
        try {
            return jsonParser.parse(reader);
        } catch (ParseException ex) {
            logger.warn("File parsing " + path + " failed.", ex);
            return null;
        }
    }

    public static String getDateForPrint(Date notificationDate) {
        logger.debug("Method 'getDateForPrint' started working.");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy 'в' HH:mm (EEEE)");
        return dateFormat.format(notificationDate);
    }
}
