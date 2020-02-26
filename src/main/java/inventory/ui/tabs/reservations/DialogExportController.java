package inventory.ui.tabs.reservations;

import inventory.utils.ExcelGenerator;
import inventory.utils.StageHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DialogExportController implements Initializable {
    @FXML
    private Button buttonExport;
    @FXML
    private DatePicker datePickerStart;
    @FXML
    public DatePicker datePickerEnd;
    @FXML
    public Label labelPath;

    private File destinationFile;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buttonExport.setDisable(true);
        datePickerStart.setValue(LocalDate.now().minusWeeks(1));
        datePickerEnd.setValue(LocalDate.now());

        datePickerStart.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isAfter(datePickerEnd.getValue())) {
                datePickerEnd.setValue(newValue);
            }
        });

        highlightInterval(datePickerStart, true);
        highlightInterval(datePickerEnd, false);

        datePickerStart.setOnAction(event -> {
            datePickerStart.hide();
            highlightInterval(datePickerStart, true);
            highlightInterval(datePickerEnd, false);
        });
        datePickerEnd.setOnAction(event -> {
            datePickerEnd.hide();
            highlightInterval(datePickerStart, true);
            highlightInterval(datePickerEnd, false);
        });
    }

    private void highlightInterval(DatePicker datePicker, boolean isBeginning) {
        datePicker.setDayCellFactory(datePicker1 -> {
            LocalDate start = datePickerStart.getValue();
            LocalDate end = datePickerEnd.getValue();

            return new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);

                    if (!empty && item != null) {
                        if (!isBeginning && item.isBefore(start)) {
                            setDisable(true);
                        } else if (item.isAfter(start) && item.isBefore(end)) {
                            setBackground(new Background(new BackgroundFill(Color.ALICEBLUE, null, null)));
                        } else if (isBeginning && item.isEqual(end)) {
                            setBackground(new Background(new BackgroundFill(Color.ALICEBLUE, null, null)));
                        } else if (!isBeginning && item.isEqual(start)) {
                            setBackground(new Background(new BackgroundFill(Color.ALICEBLUE, null, null)));
                        }
                    }
                }
            };
        });
    }

    @FXML
    public void onSelectPathClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        //Set extension filter for *.xlsx files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Save to");
        fileChooser.setInitialFileName("Export");

        destinationFile = fileChooser.showSaveDialog(((Node) actionEvent.getTarget()).getScene().getWindow());

        if (destinationFile != null) {
            labelPath.setText(destinationFile.getPath());
            buttonExport.setDisable(false);
        }
    }

    @FXML
    public void onExportClicked(ActionEvent actionEvent) {
        if (destinationFile != null) {
            ExcelGenerator.generateTableReservations(destinationFile, datePickerStart.getValue(), datePickerEnd.getValue());
            // close dialog
            StageHelper.close(actionEvent);
        }
    }

    public void onCancelClicked(ActionEvent actionEvent) {
        // close dialog
        StageHelper.close(actionEvent);
    }
}
