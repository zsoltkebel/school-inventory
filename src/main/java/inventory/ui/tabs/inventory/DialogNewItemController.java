package inventory.ui.tabs.inventory;

import inventory.model.Category;
import inventory.model.singleton.Inventory;
import inventory.model.Item;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class DialogNewItemController implements Initializable {
    @FXML
    private Button doneButton;
    @FXML
    private ComboBox<Category> categoryComboBox;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private TextField nameTextField;

    private Item item;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        doneButton.setText("Add");

        // it doesnt have to change dynamically as category cannot be added or removed while this dialog is visible
        categoryComboBox.setItems(Inventory.getInstance().getCategories());
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
    }


    public void setItem(Item item) {
        this.item = item;

        if (item != null) {
            nameTextField.setText(item.getName());
            descriptionTextArea.setText(item.getDescription());
            categoryComboBox.getSelectionModel().select(Inventory.getInstance().getCategory(item.getCategoryId()));

            doneButton.setText("Save");
        } else {
            doneButton.setText("Add");
        }
    }

    @FXML
    public void onCancelClicked(ActionEvent actionEvent) {
        // close dialog
        Stage stage = (Stage) ((Node) actionEvent.getTarget()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onSaveClicked(ActionEvent actionEvent) {
        if (item != null) {
            // not creating new item if category or name is missing
            if (getCategory() == null || getName() == null || getName().equals("")) return;

            Item newItem = new Item(item);
            newItem.setName(getName());
            newItem.setDescription(getDescription());
            newItem.setCategoryId(getCategory().getId());

            Inventory.getInstance().updateItem(item, newItem);
        } else {
            if (getCategory() == null) return;
            Inventory.getInstance().insertItem(getName(), getDescription(), getCategory().getId());
        }
        // close dialog
        Stage stage = (Stage) ((Node) actionEvent.getTarget()).getScene().getWindow();
        stage.close();
    }

    private String getName() {
        return nameTextField.getText();
    }

    private Category getCategory() {
        return categoryComboBox.getValue();
    }

    private String getDescription() {
        return descriptionTextArea.getText();
    }


}
