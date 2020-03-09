package inventory.ui.dialogs;

import inventory.model.Item;
import inventory.model.ReservationManager;
import inventory.ui.DialogStage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;

public class ReserveDialog extends DialogStage {

    @Override
    public String getFXMLName() {
        return "/fxml/dialogs/reservation_creator.fxml";
    }

    @Override
    public String getDialogTitle() {
        return "Reserve";
    }

    public ReserveDialog(Item selectedItem) {
        super();
        setMinHeight(400);
        setMinWidth(350);

        ReservationManager.getInstance().newReservation(selectedItem);

        this.setOnCloseRequest(event -> ReservationManager.getInstance().setSelectedReservation(null));
    }
}
