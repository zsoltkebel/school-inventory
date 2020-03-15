package inventory.model;

import org.json.simple.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Arrays;

public class Lesson {
    
    private int no;
    private LocalTime start;
    private LocalTime end;

    public Lesson(int no, LocalTime start, LocalTime end) {
        this.no = no;
        this.start = start;
        this.end = end;
    }

    public int getNo() {
        return no;
    }

    public LocalTime getStart() {
        return start;
    }

    public String getStartString() {
        return String.format("%02d:%02d", start.getHour(), start.getMinute());
    }

    public LocalTime getEnd() {
        return end;
    }

    public String getEndString() {
        return String.format("%02d:%02d", end.getHour(), end.getMinute());
    }

    public String getLessonText() {
        return getNo() + ".: " + getStartString() + " - " + getEndString();
    }

    public static Lesson newLesson(JSONObject jsonObject) {
        int id = ((Long) jsonObject.get("no")).intValue();
        Integer[] start = Arrays.stream(((String) jsonObject.get("start")).split(":"))
                .limit(2)
                .map(Integer::valueOf)
                .toArray(Integer[]::new);
        Integer[] end = Arrays.stream(((String) jsonObject.get("end")).split(":"))
                .limit(2)
                .map(Integer::valueOf)
                .toArray(Integer[]::new);

        return new Lesson(id, LocalTime.of(start[0], start[1]), LocalTime.of(end[0], end[1]));

    }
}
