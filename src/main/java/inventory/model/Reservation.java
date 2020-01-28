package inventory.model;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class Reservation extends Record<Reservation> {

    public final Date start;
    public final Date end;
    public final int lessonId;
    public final int itemId;
    public final String name;
    public final String comment;
    public boolean returned;

    public Reservation(int id, Date start, Date end, int lessonId, int itemId, String name, String comment, boolean returned) {
        super(id);
        this.start = start;
        this.end = end;
        this.lessonId = lessonId;
        this.itemId = itemId;
        this.name = name;
        this.comment = comment;
        this.returned = returned;
    }

    public Reservation(Date start, Date end, int lessonId, int itemId, String name, String comment, boolean returned) {
        this(-1, start, end, lessonId, itemId, name, comment, returned);
    }

    public Reservation(int id, Reservation reservation) {
        this(
                id,
                reservation.getStart(),
                reservation.getEnd(),
                reservation.getLessonId(),
                reservation.getItemId(),
                reservation.getName(),
                reservation.getComment(),
                reservation.isReturned()
        );
    }

    public Reservation(Reservation reservation) {
        this(
                reservation.getId(),
                reservation.getStart(),
                reservation.getEnd(),
                reservation.getLessonId(),
                reservation.getItemId(),
                reservation.getName(),
                reservation.getComment(),
                reservation.isReturned()
        );
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd. HH:mm");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");

        String start = dateFormat.format(this.start);
        String end = timeFormat.format(this.end);
        return  start + " - " + end;
    }

    public int getLessonId() {
        return lessonId;
    }

    public int getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public boolean isReturned() {
        return returned;
    }

    public boolean isNotReturned() {
        return !returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public boolean during(Date date) {
        return !date.before(start) && !date.after(end);
    }

    public boolean before(Date date) {
        return end.before(date);
    }

    public boolean on(LocalDate date) {
        LocalDate start = Instant.ofEpochMilli(this.start.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = Instant.ofEpochMilli(this.end.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        return !date.isBefore(start) && !date.isAfter(end);
    }

    @Override
    public Reservation withId(int id) {
        return new Reservation(id, getStart(), getEnd(), getLessonId(), getItemId(), getName(), getComment(), isReturned());
    }
}
