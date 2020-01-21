package inventory.ui;

import inventory.model.Category;
import inventory.model.Inventory;
import inventory.model.Item;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class ItemCreatorDialog extends DialogStage {

    Inventory inventory = Inventory.getInstance();

    @FXML private TextField nameTextField;

    @FXML private TextArea descriptionTextArea;

    @FXML private ComboBox<Category> categoryComboBox;

    @FXML private Button cancelButton;
    @FXML private Button doneButton;

    @Override
    FXMLLoader getFXMLLoader() {
        return new FXMLLoader(getClass().getResource("/fxml/dialogs/item_creator.fxml"));
    }

    @Override
    String getDialogTitle() {
        return "New Item";
    }

    public ItemCreatorDialog(Item item) {
        // it doesnt have to change dynamically as category cannot be added or removed while this dialog is visible
        categoryComboBox.setItems(inventory.getCategories());
        categoryComboBox.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category o) {
                return o.getName();
            }

            @Override
            public Category fromString(String s) {
                return null;
            }
        });

        if (item != null) {
            nameTextField.setText(item.getName());
            descriptionTextArea.setText(item.getDescription());
            categoryComboBox.getSelectionModel().select(inventory.getCategory(item.getCategoryId()));

            doneButton.setText("Save");
            doneButton.setOnAction(actionEvent -> {
                if (getCategory() == null) return;

                Item newItem = new Item(item);
                newItem.setName(getName());
                newItem.setDescription(getDescription());
                newItem.setCategoryId(getCategory().getId());

                inventory.updateItem(item, newItem);
                close();
            });
        } else {
            doneButton.setText("Add");
            doneButton.setOnAction(actionEvent -> {
                //TODO dialog
                if (getCategory() == null) return;

                inventory.insertItem(getName(), getDescription(), getCategory().getId());
                close();
            });
        }

        cancelButton.setOnAction(event -> {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        });
    }

    private String getName() {
        return nameTextField.getText();
    }

    private String getDescription() {
        return descriptionTextArea.getText();
    }

    private Category getCategory() {
        return categoryComboBox.getValue();
    }


}
