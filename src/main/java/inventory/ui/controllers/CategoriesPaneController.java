package inventory.ui.controllers;

import inventory.model.Category;
import inventory.model.Filter;
import inventory.model.Inventory;
import inventory.ui.tabs.inventory.CategoryCellFactory;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CategoriesPaneController implements Initializable {

    private Inventory inventory = Inventory.getInstance();
    private Filter filter = Filter.getInstance();

    @FXML
    private ListView<Category> listView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listView.setCellFactory(new CategoryCellFactory());

        listView.setItems(inventory.getCategories());

        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Category>) change -> {
            change.next();
            if (change.wasAdded()) {
                int id = listView.getSelectionModel().getSelectedItem().getId();
                Filter.getInstance().setCategoryId(id);
            }
        });

        Filter.getInstance().addListener(() -> {
            if (filter.isEmpty()) listView.getSelectionModel().clearSelection();
        });
    }

    @FXML
    private void handleButtonAction(ActionEvent actionEvent) {
        onAddPressed();
    }

    private void onAddPressed() {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("New Category");

        HBox hBox = new HBox();

        TextField textField = new TextField();
        Button button = new Button("Add");

        hBox.getChildren().addAll(textField, button);

        Scene dialogScene = new Scene(hBox);
        dialogScene.getStylesheets().add("styles/styles.css");

        dialog.setScene(dialogScene);

        dialog.show();

        dialogScene.getRoot().requestFocus();

        textField.requestFocus();
        button.setOnAction(actionEvent -> {
            inventory.insertCategory(textField.getText());
            dialog.close();
        });

    }
}
