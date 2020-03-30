package inventory.model.singleton;

import inventory.model.*;
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

    // instance variables
    private ObservableList<Lesson> lessons = FXCollections.observableArrayList();
    private ObservableList<Reservation> reservations = FXCollections.observableArrayList();
    private ObservableList<Reservation> activeReservations = FXCollections.observableArrayList();
    private ObservableList<Reservation> filteredReservations = FXCollections.observableArrayList();
    private ObjectProperty<Reservation> selectedReservation = new SimpleObjectProperty<>(null);

    // variables for filtering
    private String filterName = null;
    private int[] filterCategoryIds = Inventory.getInstance().getCategories()
            .stream()
            .mapToInt(Category::getId)
            .toArray();

    private IntegerProperty limit = new SimpleIntegerProperty(100);

    private ReservationManager() {
        // load lessons
        JSONUtil.readJSONArray("timetable.json", object -> lessons.add(Lesson.newLesson(object)));

        loadReservations();
        loadActiveReservations();
        filter(filterName, filterCategoryIds);

        // every time the original list changes update the other lists based on the original data
        reservations.addListener((ListChangeListener<Reservation>) c -> {
            loadActiveReservations();
            filter(filterName, filterCategoryIds);
        });

        // starting a timer that is going to load the active reservations in every 1 minute
        startTimeLine();

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

        // if the limit of displayed reservations change, reload the list
        limitProperty().addListener((observable, oldValue, newValue) -> loadReservations());
    }

    public ObservableList<Reservation> reservationsObservable() {
        return reservations;
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

    /**
     *
     * @param reservationToBeSelected the reservation to be selected (it has to be in the reservations list!!!)
     * @return true if the user decided to finish editing (clicked "Yes" or "No" or nothing changed in the
     * currently selected reservation. false if user clicks "Cancel".
     */
    public boolean setSelectedReservation(Reservation reservationToBeSelected) {
        Reservation previous = getSelectedReservation();
        if (previous != null &&
                reservationToBeSelected != null &&
                reservationToBeSelected.getId() == previous.getId()) {
            // do nothing if the same is selected again
            return true;
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
                return false;
            }
        }

        // creating a copy of the reservation that is going to be selected
        // and setting that to be the selected reservation
        Reservation reservation = reservationToBeSelected == null ? null : reservationToBeSelected.duplicate();
        this.selectedReservation.set(reservation);

        return true;
    }

    private void startTimeLine() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.minutes(1), event -> {
            loadActiveReservations();
            System.out.println("Reloaded active reservations");
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
                .filter(reservation -> (reservation.during(date) || reservation.isTerminatedBy(date)) && !reservation.isReturned())
                .collect(Collectors.toList()));
    }

    public ObservableList<Lesson> lessonsObservable() {
        return lessons;
    }

    public Reservation getReservation(int id) {
        return reservations.stream()
                .filter(reservation -> reservation.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Reservation> reservationsForSelected(LocalDate date) {
        return reservationsFor(getSelectedReservation().getItem(), date).stream()
                .filter(reservation -> reservation.getId() != getSelectedReservation().getId())
                .collect(Collectors.toList());
    }

    public List<Reservation> reservationsFor(Item item, LocalDate date) {
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

        return lessons.stream()
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
        if (lessons.contains(lesson)) {
            return lessons.indexOf(lesson);
        } else if (lesson != null) {
            for (int i = 0; i < lessons.size(); i++) {
                if (lesson.getNo() == lessons.get(i).getNo()) {
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

    public void remove(Reservation reservation) {
        remove(reservation.getId());
    }

    public void remove(int id) {
        reservationsObservable().removeIf(reservation -> reservation.getId() == id);
        Database.getInstance().remove(id, Database.TABLE_RESERVATIONS);
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
        remove(getSelectedReservation());
    }

    public void unselect() {
        selectedReservationProperty().setValue(null);
    }

    public Lesson getLesson(int id) {
        return lessons.stream()
                .filter(lesson -> lesson.getNo() == id)
                .findFirst()
                .orElse(null);
    }

    public Lesson[] getLessons(int... ids) {
        return Arrays.stream(ids)
                .mapToObj(this::getLesson)
                .toArray(Lesson[]::new);
    }

    /**
     *
     * @param item for which the reservation takes place
     */
    public void newReservation(Item item) {
        Reservation newReservation = new Reservation();
        newReservation.setItemId(item.getId());

        setSelectedReservation(newReservation);
    }

    /**
     * Inserts the selected reservation
     * When this is called the selected reservation is a new one that is entered in the inventory tab
     */
    public void insertNew() {
        insertReservation(getSelectedReservation());
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
