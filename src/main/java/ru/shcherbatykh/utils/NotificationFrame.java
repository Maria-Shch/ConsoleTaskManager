package ru.shcherbatykh.utils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ru.shcherbatykh.manager.TaskRepo;
import ru.shcherbatykh.manager.UserNotificationController;
import ru.shcherbatykh.models.Task;
import java.awt.*;
import java.util.Date;
import javax.swing.*;

public class NotificationFrame extends JFrame{

    private JLabel lTitle;
    private JTextArea taDescriptionTask;
    private JTextArea taContactDetailsTask;
    private final JButton bPostpone  = new JButton("Отложить");
    private final JButton bComplete = new JButton("Завершить");
    private TaskRepo taskRepo;
    private UserNotificationController userNotificationController;
    private static final Logger logger = Logger.getLogger(NotificationFrame.class);

    @Autowired
    public void setTaskRepo(TaskRepo taskRepo) {
        this.taskRepo = taskRepo;
    }

    @Autowired
    public void setTimeNotificationController(UserNotificationController userNotificationController) {
        this.userNotificationController = userNotificationController;
    }

    public NotificationFrame(){
        super("Форма оповещения");
    }

    public void init(Task task){
        logger.debug("Method 'init' started working.");
        setBounds(200, 200, 500, 300);

        Container container = this.getContentPane();
        container.setLayout(new GridLayout(4, 1));

        Container containerForButtons = new Container();
        containerForButtons.setLayout(new GridLayout(1, 2));

        lTitle = new JLabel("У вас запланирована задача: " + task.getTitle());
        taDescriptionTask = new JTextArea(task.getDescription());
        taContactDetailsTask = new JTextArea(task.getContactDetails());

        SwingCommandUtils.setBorderElements(lTitle, taDescriptionTask, taContactDetailsTask);
        SwingCommandUtils.setFontElements(20.0f, lTitle, bPostpone, bComplete);
        SwingCommandUtils.setFontElements(18.0f, taDescriptionTask);
        SwingCommandUtils.setFontElements(16.0f, taContactDetailsTask);
        SwingCommandUtils.setWrapStyleWordTrue(taDescriptionTask, taContactDetailsTask);
        SwingCommandUtils.setLineWrapTrue(taDescriptionTask, taContactDetailsTask);
        SwingCommandUtils.setEditableFalse(taDescriptionTask, taContactDetailsTask);

        JScrollPane spDescriptionTask = new JScrollPane(taDescriptionTask);
        JScrollPane spContactDetails = new JScrollPane(taContactDetailsTask);

        bPostpone.setBackground(new java.awt.Color(240, 210, 105));
        bComplete.setBackground(new java.awt.Color(108, 220, 103));

        bComplete.addActionListener((e) -> {
            dispose();
            taskRepo.completeTask(task);
            userNotificationController.run();
        });

        bPostpone.addActionListener((e) -> {
            dispose();
            new PostponeTaskFrame(taskRepo, task);
        });

        SwingCommandUtils.addElementsToContainer(containerForButtons, bPostpone, bComplete);
        SwingCommandUtils.addElementsToContainer(container, lTitle, spDescriptionTask, spContactDetails, containerForButtons);
        SwingCommandUtils.setBasicParametersForFrame(this);
    }

    private class PostponeTaskFrame extends JFrame {
        private final String[] items = {"Не откладывать", "Отложить на 1 минуту", "Отложить на 5 минут",
                "Отложить на 10 минут", "Отложить на 30 минут", "Отложить на 45 минут", "Отложить на час"};
        private final JComboBox comboBox = new JComboBox(items);
        private final JButton bPostpone = new JButton("Отложить");
        private Date newDate;

        public PostponeTaskFrame(TaskRepo taskRepo, Task task) {
            super("Отложить задачу '" + task.getTitle() + "'");
            logger.debug("Instance of the class was created.");

            setBounds(200, 200, 400, 150);

            Container container = this.getContentPane();
            container.setLayout(new GridLayout(2, 1));

            SwingCommandUtils.setBorderElements(comboBox);
            SwingCommandUtils.setFontElements(20.0f, bPostpone, comboBox);

            bPostpone.setBackground(new java.awt.Color(240, 210, 105));

            comboBox.addActionListener((e) -> {
                newDate = CommandUtils.getNewDateAfterUserChoiceInComboBox(task.getNotificationDate(),
                        (JComboBox) e.getSource());
            });

            bPostpone.addActionListener((e) -> {
                dispose();
                if (newDate == null) taskRepo.updateNotificationDate(task, task.getNotificationDate());
                else taskRepo.updateNotificationDate(task, newDate);
                userNotificationController.run();
            });

            SwingCommandUtils.addElementsToContainer(container, comboBox, bPostpone);
            SwingCommandUtils.setBasicParametersForFrame(this);
        }
    }
}
