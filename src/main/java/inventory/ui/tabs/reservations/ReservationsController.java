package inventory.ui.tabs.reservations;

import inventory.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.scene.input.KeyEvent.KEY_PRESSED;

public class ReservationsController implements Initializable {

    @FXML private ComboBox<Category> comboBoxCategory;
    @FXML private TextField textFieldName;
    @FXML
    private Spinner<Integer> spinnerLimit;
    @FXML
    private ListView<Reservation> reservationsListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reservationsListView.setCellFactory(param -> new ReservationCell());
        reservationsListView.setItems(ReservationManager.getInstance().filteredReservations());

        reservationsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        reservationsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ReservationManager.getInstance().setSelectedReservation(newValue);
            Reservation selected = ReservationManager.getInstance().getSelectedReservation();
            if (newValue.getId() != selected.getId()) {
                // if selected is not changed in the model class -> UI should not change selected item either
                reservationsListView.getSelectionModel().select(oldValue);
            }
        });

        ReservationManager.getInstance().selectedReservationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                reservationsListView.getSelectionModel().clearSelection();
            }
        });

        // limit of displayed reservations
        spinnerLimit.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, ReservationManager.getInstance().getLimit()));
        spinnerLimit.setEditable(true);
        spinnerLimit.valueProperty().addListener((observable, oldValue, newValue) -> ReservationManager.getInstance().setLimit(newValue));
        spinnerLimit.getEditor().addEventHandler(KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    Integer.parseInt(spinnerLimit.getEditor().textProperty().get());
                } catch (NumberFormatException e) {
                    spinnerLimit.getEditor().textProperty().set(String.valueOf(ReservationManager.getInstance().getLimit()));
                }
            }
        });

        // filtering category selector
        comboBoxCategory.setCellFactory(new Callback<ListView<Category>, ListCell<Category>>() {
            @Override
            public ListCell<Category> call(ListView<Category> param) {
                return new ListCell<Category>() {
                    @Override
                    protected void updateItem(Category item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null) {
                            this.setText(item.getName());
                        }
                    }
                };
            }
        });
        comboBoxCategory.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category object) {
                return object.getName();
            }

            @Override
            public Category fromString(String string) {
                return null;
            }
        });
        comboBoxCategory.setItems(Inventory.getInstance().getCategories());

    }

    public void onFilterClicked(ActionEvent actionEvent) {
        String name = textFieldName.getText();
        int[] selectedIds = comboBoxCategory.getSelectionModel().getSelectedItem() != null
                ? new int[]{comboBoxCategory.getSelectionModel().getSelectedItem().getId()}
                : Inventory.getInstance().getCategories()
                    .stream()
                    .mapToInt(Category::getId)
                    .toArray();

        ReservationManager.getInstance().filter(name, selectedIds);
    }

    public void onClearFilterClicked(ActionEvent actionEvent) {
        textFieldName.setText(null);
        comboBoxCategory.getSelectionModel().clearSelection();
        ReservationManager.getInstance().clearFilter();
    }

    public void onExportClicked(ActionEvent actionEvent) {
        ExportDialog dialog = new ExportDialog();
        dialog.showAndWait();
    }
}
