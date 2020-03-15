package inventory.model;

import inventory.model.singleton.Inventory;
import inventory.utils.Timetable;
import javafx.beans.property.*;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public class Reservation extends Record<Reservation> {

    private ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private IntegerProperty lessonId = new SimpleIntegerProperty();
    private IntegerProperty itemId = new SimpleIntegerProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty comment = new SimpleStringProperty();
    private BooleanProperty returned = new SimpleBooleanProperty();

    public Reservation() {
        this(
                -1,
                LocalDate.now(),
                -1,
                -1,
                "",
                "",
                false
        );
    }

    public Reservation(int id, LocalDate date, int lessonId, int itemId, String name, String comment, boolean returned) {
        super(id);
        setDate(date);
        setLessonId(lessonId);
        setItemId(itemId);
        setName(name);
        setComment(comment);
        setReturned(returned);
    }

    public Reservation(LocalDate date, int lessonId, int itemId, String name, String comment, boolean returned) {
        this(-1, date, lessonId, itemId, name, comment, returned);
    }

    public Reservation(int id, Reservation reservation) {
        this(
                id,
                reservation.getDate(),
                reservation.getLessonId(),
                reservation.getItemId(),
                reservation.getName(),
                reservation.getComment(),
                reservation.isReturned()
        );
    }

    // == date ==
    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public LocalDate getDate() {
        return date.get();
    }

    public Date getDateSQL() {
        return Date.valueOf(date.get());
    }

    public void setDate(LocalDate date) {
        this.date.setValue(date);
    }

    // == lessonIds ==
    public IntegerProperty lessonIdProperty() {
        return lessonId;
    }

    public int getLessonId() {
        return lessonId.get();
    }

    public void setLessonId(Integer lessonId) {
        this.lessonId.setValue(lessonId);
    }

    // == itemId ==
    public IntegerProperty itemIdProperty() {
        return itemId;
    }

    public int getItemId() {
        return itemId.get();
    }

    public void setItemId(int itemId) {
        this.itemId.set(itemId);
    }

    // == returned ==
    public BooleanProperty returnedProperty() {
        return returned;
    }

    public boolean isReturned() {
        return returned.get();
    }

    public void setReturned(boolean returned) {
        this.returned.set(returned);
    }

    // == name ==
    public StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    // == comment ==
    public StringProperty commentProperty() {
        return comment;
    }

    public String getComment() {
        return comment.get();
    }

    public void setComment(String comment) {
        this.comment.setValue(comment);
    }

    public boolean during(Instant instant) {
        LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime time = LocalTime.from(instant.atZone(ZoneId.systemDefault()));
        Lesson lesson = getLesson();

        return getDate().isEqual(date) &&
                !lesson.getStart().isAfter(time) &&
                !lesson.getEnd().isBefore(time);
    }

    public boolean before(Instant instant) {
        LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime time = LocalTime.from(instant.atZone(ZoneId.systemDefault()));
        Lesson lesson = getLesson();

        return !getDate().isAfter(date) &&
                lesson.getEnd().isBefore(time);
    }

    public boolean on(LocalDate date) {
        return getDate().isEqual(date);
    }

    @Override
    public Reservation withId(int id) {
        return new Reservation(id, this);
    }

    public Reservation duplicate() {
        return new Reservation(getId(), this);
    }

    public Item getItem() {
        return Inventory.getInstance().getItem(getItemId());
    }

    public Lesson getLesson() {
        return Timetable.getLesson(getLessonId());
    }

    public boolean isTheSame(Reservation other) {
        return other != null &&
                getId() == other.getId() &&
                getItemId() == other.getItemId() &&
                getLessonId() == other.getLessonId() &&
                getDate().isEqual(other.getDate()) &&
                getName().equals(other.getName()) &&
                getComment().equals(other.getComment()) &&
                isReturned() == other.isReturned();
    }

    public String getDateText() {
        return String.format("%04d.%02d.%02d", getDate().getYear(), getDate().getMonthValue(), getDate().getDayOfMonth());
    }
}
