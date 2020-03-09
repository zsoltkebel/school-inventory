package inventory.ui.tabs.home;

import inventory.model.Reservation;
import inventory.model.singleton.ReservationManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML private ListView<Reservation> listViewCurrentReservations;

    private ObservableList<Reservation> activeReservations = ReservationManager.getInstance().activeReservations();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listViewCurrentReservations.setCellFactory(param -> new ReservationCell());
        listViewCurrentReservations.setItems(ReservationManager.getInstance().activeReservations());
    }

}
