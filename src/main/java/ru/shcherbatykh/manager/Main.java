package ru.shcherbatykh.manager;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        logger.info("==== Запуск работы приложения ====");

        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        context.getBean(UserInterface.class).startMenu();
    }
}
