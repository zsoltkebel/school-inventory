package inventory.ui.tabs.inventory;

import inventory.model.Category;
import inventory.model.singleton.Filter;
import inventory.model.singleton.Inventory;
import inventory.model.Item;
import inventory.utils.Database;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CategoryCellFactory implements Callback<ListView<Category>, ListCell<Category>> {

    Inventory inventory = Inventory.getInstance();
    Filter filter = Filter.getInstance();

    @Override
    public ListCell<Category> call(ListView<Category> categoryListView) {
        CategoryCell categoryCell = new CategoryCell();

        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem();
        editItem.setText("Edit");
        editItem.setOnAction(event -> {
            // code to edit item...
            Category cat = categoryCell.getItem();

            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("New Category");

            HBox hBox = new HBox();

            TextField textField = new TextField();
            Button button = new Button("Done");

            hBox.getChildren().addAll(textField, button);

            Scene dialogScene = new Scene(hBox);
            dialogScene.getStylesheets().add("styles/styles.css");

            dialog.setScene(dialogScene);

            dialog.show();

            dialogScene.getRoot().requestFocus();

            textField.setText(cat.getName());
            textField.requestFocus();
            button.setOnAction(actionEvent -> {
                cat.setName(textField.getText());
                Database.getInstance().update(Database.TABLE_CATEGORIES, cat);
                dialog.close();
            });
        });

        MenuItem deleteItem = new MenuItem();
        deleteItem.setText("Delete");
        deleteItem.setOnAction(event -> {
            Category categoryToDelete = categoryCell.getItem();

            // confirmation dialog
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Delete category");
            alert.setHeaderText(categoryToDelete.getName());
            String content = "Are you sure you want to delete the selected category?" +
                    "\nAll items belonging to this category ar going to be deleted as well.";
            String items = Arrays.stream(inventory.getItems(categoryToDelete.getId()))
                    .map(Item::getName)
                    .collect(Collectors.joining(", "));

            if (items.length() > 0) {
                content += "\nItems affected: " + items;
            } else {
                content += "No items are belonging to this category.";
            }

            alert.setContentText(content);
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.CANCEL);

            Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
            yesButton.setDefaultButton(false);

            Button noButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
            noButton.setDefaultButton(true);

            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                // delete category
                inventory.removeCategory(categoryToDelete.getId());

                filter.clear();
            }
        });
        contextMenu.getItems().addAll(editItem, deleteItem);

        categoryCell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
            if (isNowEmpty) {
                categoryCell.setContextMenu(null);
            } else {
                categoryCell.setContextMenu(contextMenu);
            }
        });

        return categoryCell;
    }
}
