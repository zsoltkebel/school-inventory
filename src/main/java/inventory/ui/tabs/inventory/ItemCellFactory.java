package inventory.ui.tabs.inventory;

import inventory.model.Inventory;
import inventory.model.Item;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ItemCellFactory implements Callback<ListView<Item>, ListCell<Item>> {
    @Override
    public ListCell<Item> call(ListView<Item> itemListView) {
        ItemCell itemCell = new ItemCell();

        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem();
        editItem.setText("Edit");
        editItem.setOnAction(actionEvent -> {
            Stage dialog = new ItemCreatorDialog(itemCell.getItem());
            dialog.show();
        });

        MenuItem deleteItem = new MenuItem();
        deleteItem.setText("Delete");
        deleteItem.setOnAction(event -> {
            Item itemToDelete = itemCell.getItem();

            // confirmation dialog
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Delete item");
            alert.setHeaderText(itemToDelete.getName());
            alert.setContentText("Are you sure you want to delete the selected item?");
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.CANCEL);

            Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
            yesButton.setDefaultButton(false);

            Button noButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
            noButton.setDefaultButton(true);

            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                // delete item
                Inventory.getInstance().removeItem(itemToDelete);
            }

        });


        contextMenu.getItems().addAll(editItem, deleteItem);

        itemCell.setContextMenu(contextMenu);

        return itemCell;
    }
}
