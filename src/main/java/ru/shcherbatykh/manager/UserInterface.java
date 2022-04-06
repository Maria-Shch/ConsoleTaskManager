package ru.shcherbatykh.manager;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.utils.CommandUtils;

@Component
public class UserInterface {

    private final Map<Integer, Action> actions;
    private final StringBuilder menu;
    private final UserNotificationController userNotificationController;

    @Autowired
    public UserInterface(Map<Integer, Action> actions, StringBuilder menu, UserNotificationController userNotificationController) {
        this.actions = actions;
        this.menu = menu;
        this.userNotificationController = userNotificationController;
    }

    public void startMenu() throws Exception {
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
