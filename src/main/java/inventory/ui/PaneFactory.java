package inventory.ui;

import inventory.model.Inventory;
import inventory.model.Item;
import inventory.model.Reservation;
import inventory.model.ReservationManager;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.IOException;

public class PaneFactory {

    public static Pane getItemPane(Item item) {
        return getItemPane(item, Color.WHITE);
    }

    public static Pane getItemPane(Item item, Color color) {
        if (item == null) return new Pane();

        Pane itemPane = null;
        try {
            itemPane = FXMLLoader.load(PaneFactory.class.getResource("/fxml/item_pane.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert itemPane != null;
        Label nameLabel = (Label) itemPane.lookup("#nameLabel");
        Label descriptionLabel = (Label) itemPane.lookup("#descriptionLabel");
        Label categoryLabel = (Label) itemPane.lookup("#categoryLabel");

        itemPane.setBackground(new Background(new BackgroundFill(color, new CornerRadii(5), Insets.EMPTY)));

        nameLabel.textProperty().bind(item.nameProperty());
        descriptionLabel.setText(item.getDescription());
        categoryLabel.setText(Inventory.getInstance().getCategory(item.getCategoryId()).getName());

        return itemPane;
    }

    public static Pane getReservationPane(Reservation reservation) {
        Pane reservationPane = null;
        try {
            reservationPane = FXMLLoader.load(PaneFactory.class.getResource("/fxml/reservation_pane.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert reservationPane != null;
        Label nameLabel = (Label) reservationPane.lookup("#nameLabel");
        Button returnButton = (Button) reservationPane.lookup("#returnButton");
        //TODO display date of the reservation
        returnButton.setOnAction(event -> {
            ReservationManager.getInstance().returnReservation(reservation);
//            Inventory.getInstance().changeItemAvailability(Inventory.getInstance().getItem(reservation.getItemId()).getId(), true);
        });

        nameLabel.setText(reservation.getName());

        Item item = Inventory.getInstance().getItem(reservation.getItemId());
        Pane pane = reservation.isReturned()
                ? getItemPane(item, Color.valueOf("#c7ffc4"))
                : getItemPane(item, Color.valueOf("#ffc4c4"));
        reservationPane.getChildren().add(pane);

        return reservationPane;
    }
}
