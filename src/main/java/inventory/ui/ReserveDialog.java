package inventory.ui;

import inventory.model.Item;
import inventory.model.Lesson;
import inventory.model.ReservationManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

public class ReserveDialog extends DialogStage {

    @FXML
    private Button cancelButton;
    @FXML
    private Button reserveButton;

    @Override
    FXMLLoader getFXMLLoader() {
        return new FXMLLoader(getClass().getResource("/fxml/dialogs/reservation_creator.fxml"));
    }

    @Override
    String getDialogTitle() {
        return "Reserve";
    }

    public ReserveDialog(Item selectedItem) {
        super();
        setMinHeight(400);
        setMinWidth(350);

        ReservationManager.getInstance().newReservation(selectedItem);

        this.setOnCloseRequest(event -> ReservationManager.getInstance().reloadSelected());

        cancelButton.setOnAction(event -> {
            ReservationManager.getInstance().reloadSelected();
            close();
        });

        reserveButton.setOnAction(event -> {
            ReservationManager.getInstance().insertNew();
            close();
        });
    }
}
