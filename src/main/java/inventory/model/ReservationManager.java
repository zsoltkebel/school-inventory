package inventory.model;

import inventory.utils.Database;
import inventory.utils.JSONUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Duration;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.time.LocalDate;
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

    private ReservationManager() {
        JSONUtil.readJSONArray("timetable.json", object -> {
            try {
                Date start = new Date(sdf.parse((String) object.get("start")).getTime());
                Date end = new Date(sdf.parse((String) object.get("end")).getTime());

                timeTable.add(new Lesson(((Long) object.get("no")).intValue(), start, end));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        reservations.addListener((ListChangeListener<Reservation>) c -> {
            loadActiveReservations();
        });

        loadReservations();
        loadActiveReservations();

//        startTimeLine();

        // if the items change reload the active reservations -> display change on UI
        Inventory.getInstance().getItems().addListener((ListChangeListener<Item>) c -> loadActiveReservations());
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
        reservations.addAll(Database.getInstance().queryAll(Reservation.class, Database.TABLE_RESERVATIONS));
    }

    private void loadActiveReservations() {
        Date date = new Date(new java.util.Date().getTime());

        activeReservations.clear();
        activeReservations.addAll(reservations.stream()
                .filter(reservation -> reservation.during(date) || (reservation.before(date) && !reservation.isReturned()))
                .collect(Collectors.toList()));

        activeReservations.forEach(reservation -> {
            if (!reservation.isReturned()) {
                Item item = Inventory.getInstance().getItem(reservation.getItemId());

                Inventory.getInstance().changeItemAvailability(reservation.itemId, false);
            }
        });
    }

    private void updateReservation(Reservation oldReservation, Reservation newReservation) {
        int index = reservations.indexOf(oldReservation);

        reservations.set(index, Database.getInstance().update(Database.TABLE_RESERVATIONS, newReservation));
    }

    public void changeAvailability(int reservationId, boolean available) {
        Reservation oldReservation = getReservation(reservationId);

        Reservation newReservation = new Reservation(oldReservation);

        newReservation.setReturned(available);

        updateReservation(oldReservation, newReservation);
    }

    public ObservableList<Lesson> getClassesObservableList() {
        return timeTable;
    }

    public List<Lesson> getClasses() {
        return timeTable;
    }


    public List<Reservation> getReservations() {
        return reservations;
    }

    public List<Reservation> getReservations(Item item) {
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

    public List<Reservation> getReservations(Item item, LocalDate date) {
        //TODO only watch date not time
        return getReservations(item).stream()
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
        Date date = new Date(new java.util.Date().getTime());

        return timeTable.stream()
                .filter(lesson -> {
                    Date startDate = changeTime(new Date(new java.util.Date().getTime()), lesson.getStartString());
                    Date endDate = changeTime(new Date(new java.util.Date().getTime()), lesson.getStartString());

                    return date.after(startDate) && date.before(endDate);
                })
                .findFirst()
                .orElse(null);
    }

    public void insertReservation(Reservation reservation) {
        Reservation reservationWithId = Database.getInstance().insert(Database.TABLE_RESERVATIONS, reservation);
        reservations.add(reservationWithId);
    }

    public ObservableList<Reservation> getActiveReservationsObservableList() {
        return activeReservations;
    }

    public ObservableList<Reservation> getActiveReservations() {
        return activeReservations;
    }

    public List<Reservation> getActiveReservations(int itemId) {
        return activeReservations.stream()
                .filter(reservation -> reservation.getItemId() == itemId).collect(Collectors.toList());
    }

    public static Date changeTime(Date date, String time) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        DateFormat dateAndTimeFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

        try {
            return new Date(dateAndTimeFormat.parse(dateFormat.format(date) + " " + time).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }
}
