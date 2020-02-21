package inventory.ui.tabs.home;

import inventory.model.Reservation;
import inventory.model.ReservationManager;
import inventory.ui.PaneFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML private ListView<Reservation> listViewCurrentReservations;

    private ObservableList<Reservation> activeReservations = ReservationManager.getInstance().activeReservationsObservable();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listViewCurrentReservations.setCellFactory(param -> new ReservationCell());
        listViewCurrentReservations.setItems(ReservationManager.getInstance().activeReservationsObservable());
        listViewCurrentReservations.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> listViewCurrentReservations.getSelectionModel().clearSelection());
    }

}
