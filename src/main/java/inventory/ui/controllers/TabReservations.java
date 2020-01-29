package inventory.ui.controllers;

import inventory.model.Reservation;
import inventory.model.ReservationManager;
import inventory.ui.controllers.reservations.ReservationCell;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import java.net.URL;
import java.util.ResourceBundle;

public class TabReservations implements Initializable {

    @FXML private ListView<Reservation> reservationsListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reservationsListView.setCellFactory(param -> new ReservationCell());
        reservationsListView.setItems(ReservationManager.getInstance().reservationsObservable());

        reservationsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        reservationsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ReservationManager.getInstance().setSelectedReservation(newValue);
        });
    }
}
