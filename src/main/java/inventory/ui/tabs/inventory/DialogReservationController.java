package inventory.ui.tabs.inventory;

import inventory.model.Reservation;
import inventory.model.singleton.ReservationManager;
import inventory.utils.StageHelper;
import javafx.event.ActionEvent;

public class DialogReservationController {

    public void onCancelClicked(ActionEvent actionEvent) {
        ReservationManager.getInstance().setSelectedReservation(null);
        // close dialog
        StageHelper.close(actionEvent);
    }

    public void onReserveClicked(ActionEvent actionEvent) {
        // if name or lesson is missing do not add new reservation
        Reservation reservation = ReservationManager.getInstance().getSelectedReservation();
        if (reservation.getName() == null || reservation.getName().equals("")
                || reservation.getLesson() == null) return;

        ReservationManager.getInstance().insertNew();
        ReservationManager.getInstance().setSelectedReservation(null);
        // close dialog
        StageHelper.close(actionEvent);
    }
}
