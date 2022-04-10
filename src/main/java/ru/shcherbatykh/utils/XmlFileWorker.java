package ru.shcherbatykh.utils;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.shcherbatykh.manager.Config;
import ru.shcherbatykh.models.Task;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class XmlFileWorker implements FileWorker {

    private static final String PATH = Config.PATH + "tasks.xml";
    private static final Logger logger = Logger.getLogger(XmlFileWorker.class);

    @Override
    public List<Task> getListTasksFromFile() { //todo load from empty file
        logger.debug("Method 'getListTasksFromFile' started working.");
        List<Task> tasks = new ArrayList<>();

        File file = new File(PATH);
        if(file.length() == 0) return tasks;

        Document document = null;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        }catch (Exception ex){
            logger.warn("Document parsing " + PATH + " failed.", ex);
        }

        if(document == null) return tasks;

        NodeList listTagElement = document.getFirstChild().getChildNodes();
        for (int i = 0; i < listTagElement.getLength(); i++) {
            if(listTagElement.item(i).getNodeType() != Node.ELEMENT_NODE) continue;
            if(!listTagElement.item(i).getNodeName().equals("element")) continue;

            NodeList listTagTaskParameters = listTagElement.item(i).getChildNodes();

            String title ="";
            String description ="";
            Date notificationDate = new Date();
            String contactDetails ="";

            for (int j = 0; j < listTagTaskParameters.getLength(); j++) {
                if(listTagTaskParameters.item(j).getNodeType() != Node.ELEMENT_NODE) continue;

                switch (listTagTaskParameters.item(j).getNodeName()){
                    case "title":
                        title = listTagTaskParameters.item(j).getTextContent();
                        break;
                    case "description":
                        description = listTagTaskParameters.item(j).getTextContent();
                        break;
                    case "notificationDate":
                        notificationDate = new Date(Long.parseLong(listTagTaskParameters.item(j).getTextContent()));
                        break;
                    case "contactDetails":
                        contactDetails = listTagTaskParameters.item(j).getTextContent();
                        break;
                }
            }
            tasks.add(new Task(title,description,notificationDate,contactDetails));
        }
        return tasks;
    }

    @Override
    public void saveListTaskToFile(List<Task> tasks) {
        logger.debug("Method 'saveListTaskToFile' started working.");
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        builder.append("<taskList>");
        for(Task task : tasks) builder.append(task.ToXmlString());
        builder.append("</taskList>");
        try {
            Files.write(Paths.get(PATH), builder.toString().getBytes());
        } catch (IOException ex) {
            logger.warn("Saving of file failed.", ex);
        }
    }
}
