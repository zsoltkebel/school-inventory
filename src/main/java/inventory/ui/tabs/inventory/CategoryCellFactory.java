package inventory.ui.tabs.inventory;

import inventory.model.Category;
import inventory.model.Filter;
import inventory.model.Inventory;
import inventory.utils.Database;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class CategoryCellFactory implements Callback<ListView<Category>, ListCell<Category>> {

    Inventory inventory = Inventory.getInstance();
    Filter filter = Filter.getInstance();

    @Override
    public ListCell<Category> call(ListView<Category> categoryListView) {
        CategoryCell categoryCell = new CategoryCell();

        ContextMenu contextMenu = new ContextMenu();

        ObjectProperty<Category> category = categoryCell.itemProperty();

        MenuItem editItem = new MenuItem();
        if (category.getValue() != null)
        editItem.textProperty().bind(Bindings.format("Edit \"%s\"", category.getValue().nameProperty()));
//            editItem.setText("Rename");
//                editItem.textProperty().bind(Bindings.format("Edit %s", category.getName()));

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
//                deleteItem.textProperty().bind(Bindings.format("Delete \"%s\"", ((Category) category.getValue()).getName()));
        deleteItem.setText("delete");
        deleteItem.setOnAction(event -> {
            Category categoryToDelete = categoryCell.getItem();
            // delete category
            inventory.removeCategory(categoryToDelete.getId());

            filter.clear();
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
