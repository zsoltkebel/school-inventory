package inventory.ui.tabs.inventory;

import inventory.model.Category;
import inventory.model.Item;
import inventory.model.singleton.Inventory;
import inventory.ui.dialogs.ItemCreatorDialog;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ItemsController implements Initializable {

    @FXML
    private ListView<Item> itemListView;
    @FXML
    private Pane filteringPane;
    @FXML
    private Label filterLabel;

    private Inventory inventory = Inventory.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        itemListView.setCellFactory(new ItemCellFactory());

        FilteredList<Item> filteredList = new FilteredList<>(inventory.getItems(), data -> true);
        itemListView.setItems(filteredList);

        inventory.filterCategoryIdProperty().addListener((observable, oldValue, newValue) -> {
            if (inventory.isEmptyFilter()) {
                filteringPane.setVisible(false);
                filteredList.setPredicate(item -> true);
            } else {
                Category category = inventory.getFilterCategory();

                filterLabel.textProperty().bind(category.nameProperty());
                filteringPane.setVisible(true);
                filteredList.setPredicate(item -> item.getCategoryId() == inventory.getFilterCategoryId());
            }
        });
    }

    @FXML
    private void handleAddButtonAction(ActionEvent actionEvent) {
        Stage dialog = new ItemCreatorDialog(null);
        dialog.show();
    }

    @FXML
    public void handleClearFilterButtonAction(ActionEvent actionEvent) {
        inventory.clearFilter();
    }
}
