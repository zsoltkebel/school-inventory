package inventory.ui.tabs.inventory;

import inventory.model.Category;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.SplitPane;

import java.io.IOException;

public class CategoryCell extends ListCell<Category> {

    @FXML private SplitPane splitPane;

    @FXML private Label categoryNameLabel;

    @FXML private Label numOfItemsLabel;

    public CategoryCell() {
        loadFXML();
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tab_inventory/category_cell_2.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void updateItem(Category category, boolean empty) {
        super.updateItem(category, empty);

        if (empty || category == null) {
            setGraphic(null);
        } else {
            categoryNameLabel.textProperty().bind(category.nameProperty());
            numOfItemsLabel.textProperty().bind(category.numOfItemsProperty().asString());

            setGraphic(splitPane);
        }
    }
}
