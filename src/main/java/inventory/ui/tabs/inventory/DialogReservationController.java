package inventory.ui.tabs.inventory;

import inventory.model.ReservationManager;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class DialogReservationController {

    public void onCancelClicked(ActionEvent actionEvent) {
        ReservationManager.getInstance().reloadSelected();
        // close dialog
        Stage stage = (Stage) ((Node) actionEvent.getTarget()).getScene().getWindow();
        stage.close();
    }

    public void onReserveClicked(ActionEvent actionEvent) {
        ReservationManager.getInstance().insertNew();
        // close dialog
        Stage stage = (Stage) ((Node) actionEvent.getTarget()).getScene().getWindow();
        stage.close();
    }
}
