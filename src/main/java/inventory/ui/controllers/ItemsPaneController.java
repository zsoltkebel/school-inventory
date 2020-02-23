package inventory.ui.controllers;

import inventory.model.Category;
import inventory.model.Inventory;
import inventory.model.Item;
import inventory.model.Filter;
import inventory.ui.tabs.inventory.ItemCreatorDialog;
import inventory.ui.tabs.inventory.ItemCellFactory;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ItemsPaneController implements Initializable {

    private Inventory inventory = Inventory.getInstance();
    private Filter filter = Filter.getInstance();

    @FXML
    private ListView<Item> itemListView;

    @FXML
    private Button addButton;

    @FXML private Pane filteringPane;

    @FXML private Label filterLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        itemListView.setCellFactory(new ItemCellFactory());

        FilteredList<Item> filteredList = new FilteredList<>(inventory.getItems(), data -> true);
        itemListView.setItems(filteredList);

        Filter.getInstance().addListener(() -> {
            if (filter.isEmpty()) {
                filteringPane.setVisible(false);
                filteredList.setPredicate(item -> true);
            } else {
                Category category = inventory.getCategory(filter.getCategoryId());

                filterLabel.textProperty().bind(category.nameProperty());
                filteringPane.setVisible(true);
                filteredList.setPredicate(item -> item.getCategoryId() == filter.getCategoryId());
            }
        });
    }

    @FXML
    private void handleAddButtonAction(ActionEvent actionEvent) {

        Stage dialog = new ItemCreatorDialog(null);
//        dialog.initModality(Modality.APPLICATION_MODAL);
//        dialog.setTitle("New Item");
//
//        ItemCreatorPane itemCreatorPane = new ItemCreatorPane(null);
//        Scene scene = new Scene(itemCreatorPane);
//
//        itemCreatorPane.getDoneButton().setOnAction(event -> {
//            Category category = itemCreatorPane.getCategory();
//
//            if (category == null) return;
//
//            inventory.addItem(itemCreatorPane.getName(), itemCreatorPane.getDescription(), category.getId());
//            dialog.close();
//        });

//        dialog.setScene(scene);
        dialog.show();

    }

    @FXML
    public void handleClearFilterButtonAction(ActionEvent actionEvent) {
        filter.clear();
    }
}
