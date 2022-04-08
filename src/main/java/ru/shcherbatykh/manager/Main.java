package ru.shcherbatykh.manager;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("========= Application was started =========");

        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        context.getBean(UserInterface.class).execute();
    }
}
