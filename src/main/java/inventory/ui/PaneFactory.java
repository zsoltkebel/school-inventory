package inventory.ui;

import inventory.model.Inventory;
import inventory.model.Item;
import inventory.model.Reservation;
import inventory.model.ReservationManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
            itemPane = FXMLLoader.load(PaneFactory.class.getResource("/fxml/tab_inventory/item_pane.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert itemPane != null;
        Label nameLabel = (Label) itemPane.lookup("#nameLabel");
        Label descriptionLabel = (Label) itemPane.lookup("#descriptionLabel");
        Label categoryLabel = (Label) itemPane.lookup("#categoryLabel");

//        itemPane.setBackground(new Background(new BackgroundFill(color, new CornerRadii(5), Insets.EMPTY)));

        nameLabel.textProperty().bind(item.nameProperty());
        descriptionLabel.setText(item.getDescription());
        categoryLabel.setText(Inventory.getInstance().getCategory(item.getCategoryId()).getName());

        return itemPane;
    }
}
