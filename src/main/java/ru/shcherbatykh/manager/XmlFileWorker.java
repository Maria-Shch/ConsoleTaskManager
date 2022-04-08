package ru.shcherbatykh.manager;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.shcherbatykh.models.Task;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class XmlFileWorker implements FileWorker{

    private final static String PATH = Config.PATH + "tasks.xml";
    private static final Logger logger = Logger.getLogger(XmlFileWorker.class);

    @Override
    public List<Task> getListTasksFromFile() {
        logger.debug("Method 'getListTasksFromFile' started working.");
        List<Task> tasks = new ArrayList<>();

        File file = new File(PATH);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            doc = dbf.newDocumentBuilder().parse(file);
        }catch (Exception ex){
            logger.warn("Document parsing " + PATH + " failed.", ex);
        }

        NodeList tasksChilds = doc.getFirstChild().getChildNodes();
        for (int i = 0; i < tasksChilds.getLength(); i++) {
            if(tasksChilds.item(i).getNodeType() != Node.ELEMENT_NODE) continue;
            if(!tasksChilds.item(i).getNodeName().equals("element")) continue;

            NodeList elementChilds = tasksChilds.item(i).getChildNodes();

            String title ="";
            String description ="";
            Date notificationDate = new Date();
            String contactDetails ="";

            for (int j = 0; j < elementChilds.getLength(); j++) {
                if(elementChilds.item(j).getNodeType() != Node.ELEMENT_NODE) continue;

                switch (elementChilds.item(j).getNodeName()){
                    case "title":{
                        title = elementChilds.item(j).getTextContent();
                        break;
                    }
                    case "description":{
                        description = elementChilds.item(j).getTextContent();
                        break;
                    }
                    case "notificationDate":{
                        notificationDate = new Date(Long.parseLong(elementChilds.item(j).getTextContent()));
                        break;
                    }
                    case "contactDetails":{
                        contactDetails = elementChilds.item(j).getTextContent();
                        break;
                    }
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
        builder.append("<task>");
        for(Task task : tasks) builder.append(task.ToXmlString());
        builder.append("</task>");
        try {
            Files.write(Paths.get(PATH), builder.toString().getBytes());
        } catch (IOException ex) {
            logger.warn("Saving of file failed.", ex);
        }
    }
}
