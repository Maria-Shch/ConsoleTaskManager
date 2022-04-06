package ru.shcherbatykh.utils;

import org.apache.log4j.Logger;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.shcherbatykh.manager.UserNotificationController;
import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class CommandUtils {
    private static final Scanner in = new Scanner(System.in).useDelimiter("\n");
    private static final Logger logger = Logger.getLogger(CommandUtils.class);

    public static int checkInt() {
        logger.debug("Начал работу метод checkInt");
        int val;
        while (true) {
            if (in.hasNextInt()) {
                val = in.nextInt();
                break;
            } else {
                in.nextLine();
                System.out.println("Требуется значение типа int, попробуйте снова...");
            }
        }
        return val;
    }

    public static String checkString() {
        logger.debug("Начал работу метод checkString");
        String str;
        while (true) {
            if (in.hasNext()) {
                str = in.next();
                break;
            } else {
                in.nextLine();
                System.out.println("Требуется String значение, попробуйте снова...");
            }
        }
        return str;
    }

    public static Date checkDateNotPassed(Date date) {
        logger.debug("Начал работу метод checkDateNotPassed");
        Date dateNow = new Date();
        if (dateNow.before(date)) {
            return date;
        } else {
            System.out.println("Вы ввели дату и время, которые уже прошли.");
            return null;
        }
    }

    public static Date getDateAfterProcessingUserInputInConsole(String dateStr, String timeStr) {
        logger.debug("Начал работу метод getDateAfterProcessingUserInputInConsole");
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
        logger.debug("Начал работу метод getNewDateAfterUserChoiceInComboBox");
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
        logger.debug("Начал работу метод readJsonFromFile");
        FileReader reader = null;
        try {
            reader = new FileReader(path);
        } catch (FileNotFoundException ex) {
            logger.warn("Файл " + path + " не найден. ", ex);
            File file = new File(path);
            boolean isFileCreated = file.createNewFile();
            if(isFileCreated) logger.debug("Файл " + path + " создан.");
        }
        reader = new FileReader(path);
        JSONParser jsonParser = new JSONParser();
        try {
            return jsonParser.parse(reader);
        } catch (ParseException ex) {
            logger.warn("Парсинг файла " + path + " завершился неудачей. ", ex);
            return null;
        }
    }
}
