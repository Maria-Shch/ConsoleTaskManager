package ru.shcherbatykh.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.shcherbatykh.models.Task;

@Configuration
@ComponentScan(basePackages = "ru.shcherbatykh.manager")
public class Config {

    static final String PATH = "src/main/resources/tasks.json";

    @Bean
    public List<Task> getListTasks() throws IOException, Exception {
        JSONObject jsonObject = (JSONObject) readJsonFromFile(PATH);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Task> tasks = new ArrayList<>();
        if (jsonObject != null && !jsonObject.isEmpty()) {
            for (int i = 0; i < jsonObject.keySet().size(); i++) {
                Task task = objectMapper.readValue(jsonObject.get(String.valueOf(i)).toString(), Task.class);
                tasks.add(task);
            }
        }
        return tasks;
    }

    public static Object readJsonFromFile(String filename) throws FileNotFoundException, IOException {
        FileReader reader = null;
        try {
            reader = new FileReader(filename);
        } catch (FileNotFoundException ex) {
            File file = new File(filename);
            file.createNewFile();
        }
        reader = new FileReader(filename);
        JSONParser jsonParser = new JSONParser();
        try {
            return jsonParser.parse(reader);
        } catch (ParseException | IOException ex) {
            return null;
        }
    }
}
