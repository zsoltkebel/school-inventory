package inventory.model;

import inventory.utils.Database;
import inventory.utils.JSONUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.Duration;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
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
    private ObservableList<Reservation> filteredReservations = FXCollections.observableArrayList();
    private ObjectProperty<Reservation> selectedReservation = new SimpleObjectProperty<>(null);

    private String filterName = null;
    private int[] filterCategoryIds = Inventory.getInstance().getCategories()
            .stream()
            .mapToInt(Category::getId)
            .toArray();

    private IntegerProperty limit = new SimpleIntegerProperty(100);

    private Reservation temporary = null;

    private ReservationManager() {
        JSONUtil.readJSONArray("timetable.json", object -> timeTable.add(Lesson.newLesson(object)));

        loadReservations();
        loadActiveReservations();

        // every  time the original list changes update the other lists based on the original data
        reservations.addListener((ListChangeListener<Reservation>) c -> {
            loadActiveReservations();
            filter(filterName, filterCategoryIds);
        });

//        startTimeLine();

        // if the items change reload the active reservations -> display change on UI
        Inventory.getInstance().getItems().addListener((ListChangeListener<Item>) c -> {
            int selectedResId = -1;

            try {
                selectedResId = getSelectedReservation().getId();
            } catch (NullPointerException ignored) {
            }

            loadReservations();
            loadActiveReservations();

            selectedReservation.setValue(getReservation(selectedResId));
        });

        limitProperty().addListener((observable, oldValue, newValue) -> loadReservations());

        filter(filterName, filterCategoryIds);

    }

    public ObservableList<Reservation> reservationsObservable() {
        return reservations;
    }

    public List<Reservation> getActiveReservations() {
        return activeReservations;
    }

    public ObservableList<Reservation> activeReservations() {
        return activeReservations;
    }

    public ObservableList<Reservation> filteredReservations() {
        return filteredReservations;
    }

    public ObjectProperty<Reservation> selectedReservationProperty() {
        return selectedReservation;
    }

    public Reservation getSelectedReservation() {
        return selectedReservation.get();
    }

    // == limit ==
    public IntegerProperty limitProperty() {
        return limit;
    }

    public int getLimit() {
        return limit.get();
    }

    public void setLimit(int limit) {
        limitProperty().set(limit);
    }

    public void setSelectedReservation(Reservation selectedReservation) {
        Reservation previous = getSelectedReservation();
        if (previous != null &&
                selectedReservation != null &&
                selectedReservation.getId() == previous.getId()) {
            // do nothing if the same is selected again
            return;
        }

        if (previous != null && hasChanged(previous)) {
            //TODO changed
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Changes have been detected");
            alert.setContentText("There have been changes in the selected reservation. Do you want to save the changes?");
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                updateSelected();
            } else if (alert.getResult() == ButtonType.NO) {
                // do nothing
                // changes will be discarded
            } else if (alert.getResult() == ButtonType.CANCEL) {
                // abort changing selected reservation
                return;
            }
        }

        Reservation reservation = selectedReservation == null ? null : selectedReservation.duplicate();
        this.selectedReservation.set(reservation);

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
        reservations.addAll(Database.getInstance().queryAll(Reservation.class, Database.TABLE_RESERVATIONS, true, getLimit()));
    }

    private void loadActiveReservations() {
        Instant date = Instant.now();

        activeReservations.clear();
        activeReservations.addAll(reservations.stream()
                .filter(reservation -> reservation.during(date) || (reservation.before(date) && !reservation.isReturned()))
                .collect(Collectors.toList()));
    }

    private void updateReservation(Reservation oldReservation, Reservation newReservation) {
        int index = reservations.indexOf(oldReservation);

        reservations.set(index, Database.getInstance().update(Database.TABLE_RESERVATIONS, newReservation));
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

    public List<Reservation> reservationsForSelected() {
        LocalDate forDate = getSelectedReservation().getDate();

        return reservationsForSelected(forDate);
    }

    public List<Reservation> reservationsForSelected(LocalDate date) {

        return reservationsFor(getSelectedReservation().getItem(), date).stream()
                .filter(reservation -> reservation.getId() != getSelectedReservation().getId())
                .collect(Collectors.toList());
    }

    public List<Reservation> reservationsFor(Item item, LocalDate date) {
        //TODO only watch date not time
        return reservationsFor(item).stream()
                .filter(reservation -> reservation.on(date))
                .collect(Collectors.toList());
    }

    public List<Reservation> reservationsFor(Item item) {
        return reservationsObservable().stream()
                .filter(reservation -> reservation.getItemId() == item.getId())
                .collect(Collectors.toList());
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
        reservations.add(0, reservationWithId);
        while (reservations.size() > getLimit()) reservations.remove(getLimit());
    }


    public List<Reservation> activeReservations(int itemId) {
        return activeReservations.stream()
                .filter(reservation -> reservation.getItemId() == itemId).collect(Collectors.toList());
    }

    public static Date dateWithTime(LocalDate date, LocalTime time) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        DateFormat dateAndTimeFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

        String dateAndTime = date.getYear() + "."
                + String.format("%02d", date.getMonthValue()) + "."
                + String.format("%02d", date.getDayOfMonth()) + " "
                + time.getHour() + ":" + time.getMinute();
        try {
            return new Date(dateAndTimeFormat.parse(dateAndTime).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * use this method to determine the index of a reservation in the ObservableList
     *
     * @param reservation
     * @return
     */
    public int indexOf(Reservation reservation) {
        if (reservationsObservable().contains(reservation)) {
            return reservationsObservable().indexOf(reservation);
        } else {
            for (int i = 0; i < reservationsObservable().size(); i++) {
                if (reservation.getId() == reservationsObservable().get(i).getId()) {
                    return i;
                }
            }
            return -1; // to produce OutOfBoundsException
        }
    }

    public int indexOf(Lesson lesson) {
        if (timeTable.contains(lesson)) {
            return timeTable.indexOf(lesson);
        } else if (lesson != null) {
            for (int i = 0; i < timeTable.size(); i++) {
                if (lesson.getNo() == timeTable.get(i).getNo()) {
                    return i;
                }
            }
        }
        return -1; // to produce OutOfBoundsException
    }

    private void update(Reservation reservation) {
        int index = indexOf(reservation);
        if (index < 0) return;
        reservationsObservable().set(index, Database.getInstance().update(Database.TABLE_RESERVATIONS, reservation));
    }

    private void delete(Reservation reservation) {
        delete(reservation.getId());
    }

    private void delete(int id) {
        reservationsObservable().removeIf(reservation -> reservation.getId() == id);
        Database.getInstance().delete(id, Database.TABLE_RESERVATIONS);
    }

    public void returnReservation(Reservation reservation) {
        reservation.setReturned(true);
        update(reservation);
    }

    public void returnSelected() {
        returnReservation(getSelectedReservation());

    }

    public void updateSelected() {
        update(getSelectedReservation());
    }

    public void deleteSelected() {
        delete(getSelectedReservation());
    }

    public void unselect() {
        selectedReservationProperty().setValue(null);
    }

    public Lesson getLesson(int id) {
        return timeTable.stream()
                .filter(lesson -> lesson.getNo() == id)
                .findFirst()
                .orElse(null);
    }

    public Lesson[] getLessons(int... ids) {
        return Arrays.stream(ids)
                .mapToObj(this::getLesson)
                .toArray(Lesson[]::new);
    }

    public void newReservation(Item item) {
        temporary = getSelectedReservation() == null ? null : getSelectedReservation().duplicate();

        Reservation newReservation = new Reservation();
        newReservation.setItemId(item.getId());

        setSelectedReservation(newReservation);
    }

    public void reloadSelected() {
        setSelectedReservation(temporary);
        temporary = null;
    }

    public void insertNew() {
        insertReservation(getSelectedReservation());
        reloadSelected();
    }

    public boolean isPresent(Reservation reservation) {
        for (Reservation reservation1 : reservations) {
            if (reservation1.isTheSame(reservation)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasChanged(Reservation reservation) {
        for (Reservation reservation1 : reservations) {
            if (reservation1.getId() == reservation.getId() && !reservation1.isTheSame(reservation)) {
                return true;
            }
        }
        return false;
    }

    public void filter(String name, int... categoryIds) {
        filterName = name;
        filterCategoryIds = categoryIds;
        List<Integer> ids = new ArrayList<>();
        for (int categoryId : categoryIds) {
            ids.add(categoryId);
        }

        filteredReservations.clear();
        reservationsObservable().stream()
                .filter(reservation -> (name == null || reservation.getName().contains(name))
                        && ids.contains(reservation.getItem().getCategoryId()))
                .forEach(filteredReservations::add);
    }

    public void clearFilter() {
        filter(null, Inventory.getInstance().getCategories()
                .stream()
                .mapToInt(Category::getId)
                .toArray());
    }

}
