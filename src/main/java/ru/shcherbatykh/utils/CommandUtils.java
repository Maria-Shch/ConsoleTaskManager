package ru.shcherbatykh.utils;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

    public static int checkInt() {
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
        Date dateNow = new Date();
        if (dateNow.before(date)) {
            return date;
        } else {
            System.out.println("Вы ввели дату и время, которые уже прошли.");
            return null;
        }
    }

    public static Date getDateAfterProcessingUserInputInConsole(String dateStr, String timeStr) {
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

    public static Object readJsonFromFile(String filename) throws IOException {
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
