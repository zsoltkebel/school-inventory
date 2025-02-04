package inventory.ui.tabs.inventory;

import inventory.model.Category;
import inventory.model.Item;
import inventory.model.singleton.Inventory;
import inventory.ui.dialogs.ReserveDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ItemCell extends ListCell<Item> {

    @FXML private GridPane root;

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
            categoryLabel.textProperty().bind(category.nameProperty());
            descriptionLabel.setText(item.getDescription());

            setGraphic(root);
        }
    }
}
