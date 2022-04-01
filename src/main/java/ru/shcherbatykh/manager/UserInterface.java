package ru.shcherbatykh.manager;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.utils.CommandUtils;

//вывод на консоль команд
//оповещение пользователя через swing, звук, запуск другой программы-оповещения
@Component
public class UserInterface {

    Map<Integer, Action> actions;
    StringBuilder menu;

    @Autowired
    public UserInterface(Map<Integer, Action> actions, StringBuilder menu) {
        this.actions = actions;
        this.menu = menu;
    }

    public void startMenu() throws Exception {
        boolean flag = true;

        while (flag) {
            System.out.println(menu);
            int menuSelect = CommandUtils.checkInt();

            Action action = actions.get(menuSelect);

            if(action!=null) flag = action.execute();
            else System.out.println("Такой пункт в меню отсутвствует, попробуйте снова...\n");

            if (flag) CommandUtils.waitActionFromUser();
        }
    }
}
