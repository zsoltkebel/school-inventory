package inventory.ui.tabs.inventory;

import inventory.model.Item;
import inventory.ui.ItemCreatorDialog;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ItemCellFactory implements Callback<ListView<Item>, ListCell<Item>> {
    @Override
    public ListCell<Item> call(ListView<Item> itemListView) {
        ItemCell itemCell = new ItemCell();

        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem();
        editItem.setText("Edit");
        editItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Stage dialog = new ItemCreatorDialog(itemCell.getItem());
                dialog.show();
            }
        });

        contextMenu.getItems().addAll(editItem);

        itemCell.setContextMenu(contextMenu);

        return itemCell;
    }
}
