package inventory.ui.tabs.reservations;

import inventory.model.Reservation;
import inventory.model.singleton.ReservationManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ReservationDetailsPaneController implements Initializable {

    @FXML
    private Button buttonReturn;
    @FXML
    private AnchorPane noSelectedReservationPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        noSelectedReservationPane.setVisible(ReservationManager.getInstance().getSelectedReservation() == null);

        ReservationManager.getInstance().selectedReservationProperty().addListener((observable, oldValue, newValue) -> {
            removeBind(oldValue);
            if (newValue == null) {
                noSelectedReservationPane.setVisible(true);
            } else {
                noSelectedReservationPane.setVisible(false);
                setPane(newValue);
            }
        });
    }

    private void setPane(Reservation reservation) {
        buttonReturn.disableProperty().bindBidirectional(reservation.returnedProperty());
    }

    private void removeBind(Reservation reservation) {
        try {
            buttonReturn.disableProperty().unbindBidirectional(reservation.returnedProperty());
        } catch (NullPointerException ignored) {}
    }

    public void onDeleteClicked(ActionEvent actionEvent) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setHeaderText("Delete Reservation");
        alert.setContentText("Are you sure you want to delete the selected reservation?");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.CANCEL);

        Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
        yesButton.setDefaultButton(false);

        Button noButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
        noButton.setDefaultButton(true);

        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            ReservationManager.getInstance().deleteSelected();
            ReservationManager.getInstance().unselect();
        }
    }

    public void onSaveClicked(ActionEvent actionEvent) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setHeaderText("Save Changes");
        alert.setContentText("Are you sure you want to change the details of the reservation?");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.CANCEL);

        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            ReservationManager.getInstance().updateSelected();
        }
    }

    public void onReturnClicked(ActionEvent actionEvent) {
        ReservationManager.getInstance().returnSelected();
    }
}
