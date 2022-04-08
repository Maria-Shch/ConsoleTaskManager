package ru.shcherbatykh.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import ru.shcherbatykh.models.Task;
import ru.shcherbatykh.utils.CommandUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonFileWorker implements FileWorker{

    private final static String PATH = Config.PATH + "tasks.json";
    private static final Logger logger = Logger.getLogger(JsonFileWorker.class);

    @Override
    public List<Task> getListTasksFromFile() {
        logger.debug("Method 'getListTasksFromFile' started working.");
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) CommandUtils.readJsonFromFile(PATH);
        } catch (IOException ex) {
            logger.warn("File reading " + PATH + " failed.", ex);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        List<Task> tasks = new ArrayList<>();
        if (jsonObject != null && !jsonObject.isEmpty()) {
            for (int i = 0; i < jsonObject.keySet().size(); i++) {
                Task task = null;
                try {
                    task = objectMapper.readValue(jsonObject.get(String.valueOf(i)).toString(), Task.class);
                } catch (JsonProcessingException ex) {
                    logger.warn("Creating new task failed.", ex);
                }
                tasks.add(task);
            }
        }
        return tasks;
    }

    @Override
    public void saveListTaskToFile(List<Task> tasks) {
        logger.debug("Method 'saveListTaskToFile' started working.");
        JSONObject jsonObj = new JSONObject();
        for (int i = 0; i < tasks.size(); i++) {
            jsonObj.put(i, tasks.get(i));
        }
        try {
            Files.write(Paths.get(PATH), jsonObj.toJSONString().getBytes());
        } catch (IOException ex) {
            logger.warn("Saving of file failed.", ex);
        }
    }
}
