package inventory.ui.controllers;

import inventory.model.Inventory;
import inventory.model.Lesson;
import inventory.model.Reservation;
import inventory.model.ReservationManager;
import inventory.ui.PaneFactory;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class ReservationDetailsController implements Initializable {

    @FXML
    private ComboBox<Lesson> lessonComboBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Pane itemPane;
    @FXML
    private VBox vBox;
    @FXML
    private TextField textFieldName;
    @FXML
    private TextArea textAreaComment;

    private int indexOfItemPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldName.requestFocus();
        indexOfItemPane = vBox.getChildren().indexOf(itemPane);

        ReservationManager.getInstance().selectedReservationProperty().addListener((observable, oldValue, newValue) -> {
            removeBind(oldValue);
            if (newValue != null) {
                setPane(newValue);
            }
        });

        lessonComboBox.setItems(ReservationManager.getInstance().getClassesObservableList());
        lessonComboBox.setConverter(new StringConverter<Lesson>() {
            @Override
            public String toString(Lesson object) {
                return ofLesson(object);
            }

            @Override
            public Lesson fromString(String string) {
                return null;
            }
        });

        lessonComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ReservationManager.getInstance().getSelectedReservation().setLessonId(newValue.getNo());
            }
        });

        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            // refresh UI
            lessonComboBox.setItems(FXCollections.observableArrayList());
            lessonComboBox.setItems(ReservationManager.getInstance().getClassesObservableList());
        });
    }

    private void setPane(Reservation reservation) {
        textFieldName.textProperty().bindBidirectional(reservation.nameProperty());
        textAreaComment.textProperty().bindBidirectional(reservation.commentProperty());
        datePicker.valueProperty().bindBidirectional(reservation.dateProperty());
        lessonComboBox.getSelectionModel().select(ReservationManager.getInstance().indexOf(reservation.getLesson()));

        Pane pane = PaneFactory.getItemPane(Inventory.getInstance().getItem(reservation.getItemId()));
        vBox.getChildren().set(indexOfItemPane, pane);

        // setting already reserved dates to be unavailable to select
        lessonComboBox.setCellFactory(new Callback<ListView<Lesson>, ListCell<Lesson>>() {
            @Override
            public ListCell<Lesson> call(ListView<Lesson> param) {
                return new ListCell<Lesson>() {
                    @Override
                    protected void updateItem(Lesson item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null) {
                            this.setText(ofLesson(item));

                            if (ReservationManager.getInstance().reservationsForSelected(datePicker.getValue()).stream()
                                    .anyMatch(reservation -> reservation.getLessonId() == item.getNo())) {
                                this.setDisable(true);
                                this.setTextFill(Paint.valueOf("#C0C0C0"));
                            } else {
                                this.setDisable(false);
                                this.setTextFill(Paint.valueOf("#000000"));
                            }
                        }
                    }
                };
            }
        });
    }

    private void removeBind(Reservation reservation) {
        try {
            textFieldName.textProperty().unbindBidirectional(reservation.nameProperty());
            textAreaComment.setText(reservation.getComment());
            datePicker.setValue(reservation.getDate());
        } catch (NullPointerException ignored) {}
    }

    private String ofLesson(Lesson lesson) {
        return lesson.getNo() + ". class: " + lesson.getStartString() + " - " + lesson.getEndString();
    }
}
