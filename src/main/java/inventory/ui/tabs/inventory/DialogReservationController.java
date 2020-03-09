package inventory.ui.tabs.inventory;

import inventory.model.ReservationManager;
import inventory.utils.StageHelper;
import javafx.event.ActionEvent;

public class DialogReservationController {

    public void onCancelClicked(ActionEvent actionEvent) {
        ReservationManager.getInstance().setSelectedReservation(null);
        // close dialog
        StageHelper.close(actionEvent);
    }

    public void onReserveClicked(ActionEvent actionEvent) {
        ReservationManager.getInstance().insertNew();
        ReservationManager.getInstance().setSelectedReservation(null);
        // close dialog
        StageHelper.close(actionEvent);
    }
}
