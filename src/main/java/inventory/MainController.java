package inventory;

import inventory.model.singleton.ReservationManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 * @author zsoltkebel
 */
public class MainController implements Initializable {

    @FXML
    private Tab tabReservations;
    @FXML
    private TabPane tabPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && oldValue.getId().equals(tabReservations.getId())) {
                if (!ReservationManager.getInstance().setSelectedReservation(null)) {
                    tabPane.getSelectionModel().select(oldValue);
                }
            }
        });
    }
}
