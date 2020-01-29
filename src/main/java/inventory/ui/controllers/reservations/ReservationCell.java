package inventory.ui.controllers.reservations;

import inventory.model.Reservation;
import inventory.ui.CustomListCell;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ReservationCell extends CustomListCell<Reservation> {

    @FXML
    private Label labelName;
    @FXML
    private Label labelTime;

    @FXML private Label labelComment;
    @FXML
    private VBox splitPane;

    @Override
    protected String getFXMLRoute() {
        return "/fxml/tab_reservations/reservation_cell.fxml";
    }

    @Override
    protected void updateItem(Reservation item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null) {
            setGraphic(null);
            return;
        }

        labelName.setText(item.getName() + item.getId());
        labelComment.setText(item.getComment());
        labelTime.setText(item.getTime());

        setGraphic(splitPane);
    }
}
