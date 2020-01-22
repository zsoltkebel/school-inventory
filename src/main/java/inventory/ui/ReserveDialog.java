package inventory.ui;

import inventory.model.Item;
import inventory.model.Lesson;
import inventory.model.Reservation;
import inventory.model.ReservationManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.sql.Date;
import java.time.LocalDate;

import static inventory.model.ReservationManager.dateWithTime;

public class ReserveDialog extends DialogStage {

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField nameTextField;
    @FXML
    private ComboBox<Lesson> lessonComboBox;
    @FXML
    private TextArea commentTextArea;

    @FXML
    private Button cancelButton;
    @FXML
    private Button reserveButton;

    private Item selectedItem;

    @Override
    FXMLLoader getFXMLLoader() {
        return new FXMLLoader(getClass().getResource("/fxml/dialogs/reservation_creator.fxml"));
    }

    @Override
    String getDialogTitle() {
        return "Reserve";
    }

    public ReserveDialog(Item selectedItem) {
        super();

        this.selectedItem = selectedItem;

        datePicker.setValue(LocalDate.now());

        datePicker.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                }
            }
        });

        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            lessonComboBox.setItems(FXCollections.observableArrayList());
            lessonComboBox.setItems(ReservationManager.getInstance().getClassesObservableList());
        });

        lessonComboBox.setItems(ReservationManager.getInstance().getClassesObservableList());
        lessonComboBox.setConverter(new StringConverter<Lesson>() {
            @Override
            public String toString(Lesson object) {
                return object.getNo() + ".: " + object.getStartString() + "-" + object.getEndString();
            }

            @Override
            public Lesson fromString(String string) {
                return null;
            }
        });

        lessonComboBox.setCellFactory(new Callback<ListView<Lesson>, ListCell<Lesson>>() {
            @Override
            public ListCell<Lesson> call(ListView<Lesson> param) {
                return new ListCell<Lesson>() {
                    @Override
                    protected void updateItem(Lesson item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null) {
                            this.setText(item.getNo() + ".: " + item.getStartString() + "-" + item.getEndString());

                            if (ReservationManager.getInstance().getReservations(selectedItem, datePicker.getValue())
                                    .stream()
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

        cancelButton.setOnAction(event -> {
            close();
        });

        reserveButton.setOnAction(event -> {
            Lesson lesson = lessonComboBox.getValue();
            Reservation reservation = new Reservation(
                    dateWithTime(datePicker.getValue(), lesson.getStart()),
                    dateWithTime(datePicker.getValue(), lesson.getEnd()),
                    lesson.getNo(),
                    selectedItem.getId(),
                    nameTextField.getText(),
                    commentTextArea.getText(),
                    false);

            ReservationManager.getInstance().insertReservation(reservation);

            close();
        });

        nameTextField.requestFocus();
    }
}
