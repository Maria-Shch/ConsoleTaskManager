package ru.shcherbatykh.manager;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.utils.CommandUtils;

//вывод на консоль команд
//оповещение пользователя через swing, звук, запуск другой программы-оповещения
@Component
public class UserInterface {

    @Autowired
    Manager manager;

    @Autowired
    Map<Integer, Action> actions;

    @Autowired
    StringBuilder menu;

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
