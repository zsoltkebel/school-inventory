package inventory.ui.controllers;

import inventory.model.Reservation;
import inventory.model.ReservationManager;
import inventory.ui.PaneFactory;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML private GridPane currentReservationsGridPane;

    private ObservableList<Reservation> activeReservations = ReservationManager.getInstance().activeReservationsObservable();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        activeReservations.addListener((ListChangeListener<Reservation>) c -> {
            initGrid();
        });
        initGrid();

    }

    private void initGrid() {
        currentReservationsGridPane.getChildren().clear();
        for (int i = 0; i < activeReservations.size(); i++) {
            Pane pane = PaneFactory.getReservationPane(activeReservations.get(i));

            currentReservationsGridPane.add(pane, i, 0);

        }
    }
}
