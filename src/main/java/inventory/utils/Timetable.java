package inventory.utils;

import inventory.model.Lesson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Timetable {

    public static List<Lesson> getLessons() {
        List<Lesson> lessons = new ArrayList<>();
        final String fileName = "timetable.json";
        try {
            File file = new File(fileName);
            if (file.createNewFile());

            FileReader reader = new FileReader(fileName);

            //JSON parser object to parse read file
            JSONParser jsonParser = new JSONParser();
            //Read JSON file
            JSONArray jsonArray = (JSONArray) jsonParser.parse(reader);

            for (Object o : jsonArray) {
                if (o instanceof JSONObject) {
                    lessons.add(Lesson.newLesson((JSONObject) o));
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {

        }

        return lessons;
    }

    public static Lesson getLesson(int id) {
        return getLessons().stream()
                .filter(lesson -> lesson.getNo() == id)
                .findFirst()
                .orElse(null);
    }


}
