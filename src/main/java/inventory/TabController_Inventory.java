package inventory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class TabController_Inventory implements Initializable {

    @FXML
    BorderPane borderPane;

    @FXML
    Button addButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        borderPane.setLeft(new CategoriesPane());
    }

    @FXML
    private void handleAddButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");

    }
}
