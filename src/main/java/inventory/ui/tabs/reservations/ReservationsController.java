package inventory.ui.tabs.reservations;

import inventory.model.Reservation;
import inventory.model.ReservationManager;
import inventory.ui.controllers.reservations.ReservationCell;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.scene.input.KeyEvent.KEY_PRESSED;

public class ReservationsController implements Initializable {

    @FXML
    private Spinner<Integer> spinnerLimit;
    @FXML
    private ListView<Reservation> reservationsListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reservationsListView.setCellFactory(param -> new ReservationCell());
        reservationsListView.setItems(ReservationManager.getInstance().reservationsObservable());

        reservationsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        reservationsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ReservationManager.getInstance().setSelectedReservation(newValue);
            Reservation selected = ReservationManager.getInstance().getSelectedReservation();
            if (newValue.getId() != selected.getId()) {
                // if selected is not changed in the model class -> UI should not change selected item either
                reservationsListView.getSelectionModel().select(oldValue);
            }
        });

        ReservationManager.getInstance().selectedReservationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                reservationsListView.getSelectionModel().clearSelection();
            }
        });

        spinnerLimit.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, ReservationManager.getInstance().getLimit()));
        spinnerLimit.setEditable(true);
        spinnerLimit.valueProperty().addListener((observable, oldValue, newValue) -> ReservationManager.getInstance().setLimit(newValue));
        spinnerLimit.getEditor().addEventHandler(KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    Integer.parseInt(spinnerLimit.getEditor().textProperty().get());
                } catch (NumberFormatException e) {
                    spinnerLimit.getEditor().textProperty().set(String.valueOf(ReservationManager.getInstance().getLimit()));
                }
            }
        });
    }
}
