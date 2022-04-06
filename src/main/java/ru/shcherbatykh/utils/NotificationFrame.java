package ru.shcherbatykh.utils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.manager.Manager;
import ru.shcherbatykh.manager.UserNotificationController;
import ru.shcherbatykh.models.Task;
import java.awt.*;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

@Component
public class NotificationFrame extends JFrame{

    private JLabel lTitle;
    private JTextArea taDescriptionTask;
    private JTextArea taContactDetailsTask;
    private final JButton bPostpone  = new JButton("Отложить");
    private final JButton bComplete = new JButton("Завершить");
    private Manager manager;
    private UserNotificationController userNotificationController;
    private static final Logger logger = Logger.getLogger(NotificationFrame.class);

    @Autowired
    public void setManager(Manager manager) {
        this.manager = manager;
    }

    @Autowired
    public void setTimeNotificationController(UserNotificationController userNotificationController) {
        this.userNotificationController = userNotificationController;
    }

    public NotificationFrame(){
        super("Форма оповещения");
    }

    public void init(Task task){
        logger.debug("Начал работу метод init");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(200, 200, 500, 300);
        setLocationRelativeTo(null);

        Container container = this.getContentPane();
        container.setLayout(new GridLayout(4, 1));

        Container containerForButtons = new Container();
        containerForButtons.setLayout(new GridLayout(1, 2));
        containerForButtons.setVisible(true);

        lTitle = new JLabel("У вас запланирована задача: " + task.getTitle());
        taDescriptionTask = new JTextArea(task.getDescription());
        taContactDetailsTask = new JTextArea(task.getContactDetails());

        lTitle.setBorder(new EmptyBorder(5, 5, 5, 5));
        taDescriptionTask.setBorder(new EmptyBorder(5, 5, 5, 5));
        taContactDetailsTask.setBorder(new EmptyBorder(5, 5, 5, 5));

        lTitle.setFont(lTitle.getFont().deriveFont(20.0f));
        taDescriptionTask.setFont(taDescriptionTask.getFont().deriveFont(18.0f));
        taContactDetailsTask.setFont(taDescriptionTask.getFont().deriveFont(16.0f));
        bPostpone.setFont(bPostpone.getFont().deriveFont(20.0f));
        bComplete.setFont(bComplete.getFont().deriveFont(20.0f));

        taDescriptionTask.setWrapStyleWord(true);
        taDescriptionTask.setLineWrap(true);
        taContactDetailsTask.setWrapStyleWord(true);
        taContactDetailsTask.setLineWrap(true);

        taDescriptionTask.setEditable(false);
        taContactDetailsTask.setEditable(false);

        JScrollPane spDescriptionTask = new JScrollPane(taDescriptionTask);
        JScrollPane spContactDetails = new JScrollPane(taContactDetailsTask);

        bPostpone.setBackground(new java.awt.Color(240, 210, 105));
        bComplete.setBackground(new java.awt.Color(108, 220, 103));

        bComplete.addActionListener((e) -> {
            dispose();
            manager.completeTask(task);
            userNotificationController.run();
        });

        bPostpone.addActionListener((e) -> {
            dispose();
            new PostponeTaskFrame(manager, task);
        });

        containerForButtons.add(bPostpone);
        containerForButtons.add(bComplete);

        container.add(lTitle);
        container.add(spDescriptionTask);
        container.add(spContactDetails);
        container.add(containerForButtons);

        setVisible(true);
    }

    private class PostponeTaskFrame extends JFrame {
        private final String[] items = {"Не откладывать", "Отложить на 1 минуту", "Отложить на 5 минут",
                "Отложить на 10 минут", "Отложить на 30 минут", "Отложить на 45 минут", "Отложить на час"};
        private final JComboBox comboBox = new JComboBox(items);
        private final JButton bPostpone = new JButton("Отложить");
        private Date newDate;

        public PostponeTaskFrame(Manager manager, Task task) {
            super("Отложить задачу '" + task.getTitle() + "'");
            logger.debug("Создан экземпляр класса PostponeTaskFrame");
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setBounds(200, 200, 400, 150);
            setLocationRelativeTo(null);

            Container container = this.getContentPane();
            container.setLayout(new GridLayout(2, 1));

            comboBox.setBorder(new EmptyBorder(5, 5, 5, 5));
            bPostpone.setFont(bPostpone.getFont().deriveFont(20.0f));
            bPostpone.setBackground(new java.awt.Color(240, 210, 105));

            comboBox.setFont(comboBox.getFont().deriveFont(20.0f));

            comboBox.addActionListener((e) -> {
                newDate = CommandUtils.getNewDateAfterUserChoiceInComboBox(task.getNotificationDate(), (JComboBox) e.getSource());
            });

            bPostpone.addActionListener((e) -> {
                dispose();
                if (newDate == null) manager.updateNotificationDate(task, task.getNotificationDate());
                else manager.updateNotificationDate(task, newDate);
                userNotificationController.run();
            });

            container.add(comboBox);
            container.add(bPostpone);

            setVisible(true);
        }
    }
}
