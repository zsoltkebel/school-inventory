package inventory.ui.tabs.inventory;

import inventory.model.Category;
import inventory.model.Filter;
import inventory.model.Inventory;
import inventory.model.Item;
import inventory.ui.ReserveDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.IOException;

public class ItemCell extends ListCell<Item> {

    Filter filter = Filter.getInstance();

    @FXML private Pane cellPane;

    @FXML private Label nameLabel;
    @FXML private Label categoryLabel;
    @FXML private Label descriptionLabel;

    @FXML private Button reserveButton;

    private Item item;

    public ItemCell() {
        loadFXML();
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tab_inventory/item_cell.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();

            reserveButton.setOnAction(event -> {
                Stage dialog = new ReserveDialog(item);

                dialog.show();
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void updateItem(Item item, boolean empty) {
        super.updateItem(item, empty);

        this.item = item;

        if (empty || item == null) {
            setGraphic(null);
        } else {
            Category category = Inventory.getInstance().getCategory(item.getCategoryId());
            nameLabel.setText(item.getName());
            nameLabel.setTextFill(item.isAvailable() ? Paint.valueOf("#00ff00")
                    : (item.getCurrentReservations().isEmpty() ? Paint.valueOf("#ff0000") : Paint.valueOf("#ffff00")));

            categoryLabel.textProperty().bind(category.nameProperty());
            descriptionLabel.setText(item.getDescription());

            setGraphic(cellPane);

            if (item.isAvailable()) {
                reserveButton.setDisable(false);
            } else {
                reserveButton.setDisable(true);
            }
        }
    }
}
