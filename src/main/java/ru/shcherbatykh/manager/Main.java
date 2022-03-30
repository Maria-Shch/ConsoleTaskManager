package ru.shcherbatykh.manager;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        context.getBean(UserInterface.class).startMenu();
    }
}
