package inventory.ui.tabs.home;

import inventory.model.Inventory;
import inventory.model.Reservation;
import inventory.model.ReservationManager;
import inventory.ui.CustomListCell;
import inventory.ui.PaneFactory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class ReservationCell extends CustomListCell<Reservation> {

    @FXML
    private Label labelName;
    @FXML
    private Label labelTime;

    @FXML private Label labelComment;
    @FXML
    private HBox hBox;
    @FXML
    private VBox vBox;
    @FXML
    private Pane itemPane;
    @FXML
    private Button buttonReturn;

    @Override
    protected String getFXMLRoute() {
        return "/fxml/tab_home/reservation_cell.fxml";
    }

    @Override
    protected void updateItem(Reservation reservation, boolean empty) {
        super.updateItem(reservation, empty);

        if (reservation == null) {
            setGraphic(null);
            return;
        }
        Pane pane = PaneFactory.getItemPane(Inventory.getInstance().getItem(reservation.getItemId()));
        itemPane.getChildren().clear();
        itemPane.getChildren().add(pane);

        labelName.textProperty().bind(reservation.nameProperty());
        labelComment.textProperty().bind(reservation.commentProperty());
        labelTime.setText(reservation.getTime());

        buttonReturn.setOnAction(event -> {
            ReservationManager.getInstance().returnReservation(reservation);
        });

        setGraphic(hBox);

    }
}

