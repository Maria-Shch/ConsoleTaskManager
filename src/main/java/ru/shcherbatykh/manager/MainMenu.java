package ru.shcherbatykh.manager;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import ru.shcherbatykh.utils.CommandUtils;

public class MainMenu implements Action{

    private final Map<Integer, Action> actions;
    private final String textMenu;
    private final UserNotificationController userNotificationController;

    @Autowired
    public MainMenu(Map<Integer, Action> actions, String textMenu, UserNotificationController userNotificationController) {
        this.actions = actions;
        this.textMenu = textMenu;
        this.userNotificationController = userNotificationController;
    }

    @Override
    public String getNameCommandOfAction() {
        return "Главное меню";
    }

    @Override
    public void execute(){
        while (true) {
            userNotificationController.run();
            System.out.println(textMenu);
            int menuSelect = CommandUtils.checkMenuSelect(actions.size());
            actions.get(menuSelect).execute();
        }
    }
}
