package inventory.ui.tabs.reservations;

import inventory.model.Reservation;
import inventory.ui.CustomListCell;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class ReservationCell extends CustomListCell<Reservation> {

    @FXML
    private Label labelName;
    @FXML
    private Label labelDate;
    @FXML
    private Label labelLesson;
    @FXML
    private Label labelComment;
    @FXML
    private GridPane root;

    @Override
    protected String getFXMLRoute() {
        return "/fxml/tab_reservations/reservation_cell.fxml";
    }

    @Override
    protected void updateItem(Reservation reservation, boolean empty) {
        super.updateItem(reservation, empty);

        if (reservation == null) {
            setGraphic(null);
            return;
        }

        labelName.textProperty().bind(reservation.nameProperty());
        labelComment.textProperty().bind(reservation.commentProperty());
        labelDate.setText(reservation.getDateText());
        labelLesson.setText(reservation.getLesson().getNo() + ". lesson");

        if (!reservation.isReturned()) {
            labelName.setTextFill(Color.RED);
            labelComment.setTextFill(Color.RED);
        } else {
            labelName.setTextFill(Color.BLACK);
            labelComment.setTextFill(Color.BLACK);
        }
        setGraphic(root);
    }
}
