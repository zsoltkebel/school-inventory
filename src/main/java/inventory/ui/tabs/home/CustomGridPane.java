package inventory.ui.tabs.home;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.util.Comparator;
import java.util.List;

public class CustomGridPane extends GridPane {

    private ObservableList<Pane> panes = FXCollections.observableArrayList();
    private IntegerProperty rowsProperty = new SimpleIntegerProperty(1);
    private DoubleProperty rowHeightProperty = new SimpleDoubleProperty(0);

    public CustomGridPane(List<Pane> panes) {
        panes.stream().max(Comparator.comparingDouble(Region::getHeight)).ifPresent(pane -> {
            double paneHeight = pane.getHeight();

            rowHeightProperty.set(paneHeight);
            rowsProperty.set((int) (this.getHeight() / paneHeight));
        });

        heightProperty().addListener((observable, oldValue, newValue) -> {

        });


    }

}
