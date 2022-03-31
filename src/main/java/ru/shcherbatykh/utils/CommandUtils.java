package ru.shcherbatykh.utils;

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

    public static void waitActionFromUser() {
        System.out.println("Нажмите Enter чтобы продолжить...");
        in.next();
        in.nextLine();
    }

    public static void exit() {
        in.close();
    }
}
