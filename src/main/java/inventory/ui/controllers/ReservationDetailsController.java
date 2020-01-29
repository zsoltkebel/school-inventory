package inventory.ui.controllers;

import inventory.model.Inventory;
import inventory.model.Reservation;
import inventory.model.ReservationManager;
import inventory.ui.PaneFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ReservationDetailsController implements Initializable {

    @FXML private VBox vBox;
    @FXML private Button buttonSaveChanges;
    @FXML private AnchorPane noSelectedReservationPane;

    @FXML private TextField textFieldName;
    @FXML private TextArea textAreaComment;

    private ReservationManager RESERVATION_MANAGER = ReservationManager.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        noSelectedReservationPane.setVisible(ReservationManager.getInstance().getSelectedReservation() == null);

        ReservationManager.getInstance().selectedReservationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                noSelectedReservationPane.setVisible(true);
            } else {
                noSelectedReservationPane.setVisible(false);
                setPane(newValue);
            }
        });
    }

    private void setPane(Reservation reservation) {
        textFieldName.setText(reservation.getName());

        textAreaComment.setText(reservation.getComment());

        Pane pane = PaneFactory.getItemPane(Inventory.getInstance().getItem(reservation.getItemId()));
        vBox.getChildren().set(1, pane);
    }

    public void onDeleteClicked(ActionEvent actionEvent) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setHeaderText("Delete Reservation");
        alert.setContentText("Are you sure you want to delete the selected reservation?");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

        Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
        yesButton.setDefaultButton( false );

        Button noButton = (Button) alert.getDialogPane().lookupButton(ButtonType.NO);
        noButton.setDefaultButton( true );

        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            RESERVATION_MANAGER.deleteSelected();
        }
    }
}
