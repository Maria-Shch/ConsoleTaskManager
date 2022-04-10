package ru.shcherbatykh.utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SwingCommandUtils {
    public static void setBorderElements(JComponent... jComponents){
        for (JComponent jcomponent: jComponents) {
            jcomponent.setBorder(new EmptyBorder(5, 5, 5, 5));
        }
    }

    public static void setFontElements(float fontSize, JComponent... jComponents){
        for (JComponent jcomponent: jComponents) {
            jcomponent.setFont(jcomponent.getFont().deriveFont(fontSize));
        }
    }

    public static void setWrapStyleWordTrue(JTextArea... jTextAreas){
        for (JTextArea jTextArea: jTextAreas) jTextArea.setWrapStyleWord(true);
    }

    public static void setLineWrapTrue(JTextArea... jTextAreas){
        for (JTextArea jTextArea: jTextAreas) jTextArea.setLineWrap(true);
    }

    public static void setEditableFalse(JTextArea... jTextAreas){
        for (JTextArea jTextArea: jTextAreas) jTextArea.setEditable(false);
    }

    public static void addElementsToContainer(Container container, Component... components){
        for (Component component: components) container.add(component);
    }

    public static void setBasicParametersForFrame(JFrame frame){
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }
}
