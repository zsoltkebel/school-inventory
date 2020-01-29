package inventory.model;

import inventory.utils.Database;
import inventory.utils.JSONUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Duration;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationManager {

    private static ReservationManager reservationManager = null;

    public static ReservationManager getInstance() {
        if (reservationManager == null) {
            reservationManager = new ReservationManager();
        }
        return reservationManager;
    }

    // ================================

    public static final DateFormat sdf = new SimpleDateFormat("hh:mm");

    private ObservableList<Lesson> timeTable = FXCollections.observableArrayList();
    private ObservableList<Reservation> reservations = FXCollections.observableArrayList();
    private ObservableList<Reservation> activeReservations = FXCollections.observableArrayList();
    private ObjectProperty<Reservation> selectedReservation = new SimpleObjectProperty<>(null);

    private ReservationManager() {
        JSONUtil.readJSONArray("timetable.json", object -> timeTable.add(Lesson.newLesson(object)));

        reservations.addListener((ListChangeListener<Reservation>) c -> {
            loadActiveReservations();
        });

        loadReservations();
        loadActiveReservations();

//        startTimeLine();

        // if the items change reload the active reservations -> display change on UI
        Inventory.getInstance().getItems().addListener((ListChangeListener<Item>) c -> {
            int selectedResId = -1;

            try {
                selectedResId = getSelectedReservation().getId();
            } catch (NullPointerException ignored) {}

            loadReservations();
            loadActiveReservations();

            selectedReservation.setValue(getReservation(selectedResId));
        });
    }

    public ObservableList<Reservation> reservationsObservable() {
        return reservations;
    }

    public List<Reservation> getActiveReservations() {
        return activeReservations;
    }

    public ObservableList<Reservation> activeReservationsObservable() {
        return activeReservations;
    }

    public Reservation getSelectedReservation() {
        return selectedReservation.get();
    }

    public ObjectProperty<Reservation> selectedReservationProperty() {
        return selectedReservation;
    }

    public void setSelectedReservation(Reservation selectedReservation) {
        this.selectedReservation.set(selectedReservation);
    }

    private void startTimeLine() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.minutes(1), event -> {
            System.out.println("this is called every 5 seconds on UI thread");
            loadActiveReservations();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void loadReservations() {
        reservations.clear();
        reservations.addAll(Database.getInstance().queryAll(Reservation.class, Database.TABLE_RESERVATIONS));
    }

    private void loadActiveReservations() {
        Date date = new Date(new java.util.Date().getTime());

        activeReservations.clear();
        activeReservations.addAll(reservations.stream()
                .filter(reservation -> reservation.during(date) || (reservation.before(date) && !reservation.isReturned()))
                .collect(Collectors.toList()));
    }

    private void updateReservation(Reservation oldReservation, Reservation newReservation) {
        int index = reservations.indexOf(oldReservation);

        reservations.set(index, Database.getInstance().update(Database.TABLE_RESERVATIONS, newReservation));
    }

    public void returnReservation(int reservationId, boolean returned) {
        Reservation oldReservation = getReservation(reservationId);

        Reservation newReservation = new Reservation(oldReservation);

        newReservation.setReturned(returned);

        updateReservation(oldReservation, newReservation);
    }

    public ObservableList<Lesson> getClassesObservableList() {
        return timeTable;
    }

    public List<Lesson> getClasses() {
        return timeTable;
    }



    public List<Reservation> reservationsObservable(Item item) {
        return reservations.stream()
                .filter(reservation -> reservation.getItemId() == item.getId())
                .collect(Collectors.toList());
    }

    public Reservation getReservation(int id) {
        return reservations.stream()
                .filter(reservation -> reservation.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Reservation> reservationsObservable(Item item, LocalDate date) {
        //TODO only watch date not time
        return reservationsObservable(item).stream()
                .filter(reservation -> reservation.on(date))
                .collect(Collectors.toList());
    }

    public Lesson getLesson(int id) {
        return timeTable.stream()
                .filter(lesson -> lesson.getNo() == id)
                .findFirst()
                .orElse(null);
    }

    public Lesson getCurrentLesson() {
        final LocalTime now = LocalTime.now();

        return timeTable.stream()
                .filter(lesson -> now.isAfter(lesson.getStart()) && now.isBefore(lesson.getEnd()))
                .findFirst()
                .orElse(null);
    }

    public void insertReservation(Reservation reservation) {
        Reservation reservationWithId = Database.getInstance().insert(Database.TABLE_RESERVATIONS, reservation);
        reservations.add(reservationWithId);
    }



    public List<Reservation> activeReservationsObservable(int itemId) {
        return activeReservations.stream()
                .filter(reservation -> reservation.getItemId() == itemId).collect(Collectors.toList());
    }

    public static Date dateWithTime(LocalDate date, LocalTime time) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        DateFormat dateAndTimeFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

        String dateAndTime = date.getYear() + "."
                + String.format("%02d", date.getMonthValue()) + "."
                + String.format("%02d", date.getDayOfMonth()) + " "
                + time.getHour() + ":" +time.getMinute();
        try {
            return new Date(dateAndTimeFormat.parse(dateAndTime).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteReservation(Reservation reservation) {
        deleteReservation(reservation.getId());
    }

    private void deleteReservation(int id) {
        reservationsObservable().removeIf(reservation -> reservation.getId() == id);
        Database.getInstance().delete(id, Database.TABLE_RESERVATIONS);
    }

    public void deleteSelected() {
        deleteReservation(getSelectedReservation());
    }
}
