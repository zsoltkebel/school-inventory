package inventory.ui.controllers;

import inventory.model.Inventory;
import inventory.model.Reservation;
import inventory.model.ReservationManager;
import inventory.ui.PaneFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ReservationDetailsController implements Initializable {

    @FXML private VBox vBox;
    @FXML private Button buttonDelete;
    @FXML private Button buttonSaveChanges;
    @FXML private AnchorPane noSelectedReservationPane;

    @FXML private TextArea textAreaComment;

    @FXML private Label labelName;

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
        labelName.setText(reservation.getName());

        textAreaComment.setText(reservation.getComment());

        Pane pane = PaneFactory.getItemPane(Inventory.getInstance().getItem(reservation.getItemId()));
        vBox.getChildren().set(1, pane);
    }
}
