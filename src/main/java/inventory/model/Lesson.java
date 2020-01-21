package inventory.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Lesson {

    DateFormat sdf = new SimpleDateFormat("HH:mm");

    private int no;
    private Date start;
    private Date end;

    public Lesson(int no, Date start, Date end) {
        this.no = no;
        this.start = start;
        this.end = end;
    }

    public int getNo() {
        return no;
    }

    public Date getStart() {
        return start;
    }

    public String getStartString() {
        return sdf.format(start);
    }

    public Date getEnd() {
        return end;
    }

    public String getEndString() {
        return sdf.format(end);
    }
}
