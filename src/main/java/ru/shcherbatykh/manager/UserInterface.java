package ru.shcherbatykh.manager;

import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.utils.CommandUtils;

@Component
public class UserInterface {

    private final Map<Integer, Action> actions;
    private final StringBuilder menu;
    private final UserNotificationController userNotificationController;
    private static final Logger logger = Logger.getLogger(UserInterface.class);

    @Autowired
    public UserInterface(Map<Integer, Action> actions, StringBuilder menu, UserNotificationController userNotificationController) {
        this.actions = actions;
        this.menu = menu;
        this.userNotificationController = userNotificationController;
    }

    public void startMenu() throws Exception {
        logger.info("==== Запуск работы приложения ====");
        while (true) {
            userNotificationController.run();

            System.out.println(menu);
            int menuSelect = CommandUtils.checkInt();

            Action action = actions.get(menuSelect);
            if(action!=null) action.execute();
            else System.out.println("Такой пункт в меню отсутвствует, попробуйте снова...\n");
        }
    }
}
