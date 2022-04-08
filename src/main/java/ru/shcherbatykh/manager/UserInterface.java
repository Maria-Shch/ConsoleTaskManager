package ru.shcherbatykh.manager;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import ru.shcherbatykh.utils.CommandUtils;

public class UserInterface implements Action{

    private final Map<Integer, Action> actions;
    private final StringBuilder menu;
    private final UserNotificationController userNotificationController;

    @Autowired
    public UserInterface(Map<Integer, Action> actions, StringBuilder menu, UserNotificationController userNotificationController) {
        this.actions = actions;
        this.menu = menu;
        this.userNotificationController = userNotificationController;
    }

    @Override
    public String getNameCommandOfAction() {
        return "Меню";
    }

    @Override
    public void execute(){
        while (true) {
            userNotificationController.run();
            System.out.println(menu);
            int menuSelect = CommandUtils.checkMenuSelect(actions.size());
            actions.get(menuSelect).execute();
        }
    }
}
