package inventory.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 *
 */
abstract public class DialogStage extends Stage {

    @FXML Pane dialogRoot;

    abstract FXMLLoader getFXMLLoader();

    public DialogStage() {
        FXMLLoader fxmlLoader = getFXMLLoader();
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        initModality(Modality.APPLICATION_MODAL);
        setTitle(getDialogTitle());
        setScene(new Scene(dialogRoot));

        // default values
        setMinWidth(200);
        setMinHeight(250);
    }

    String getDialogTitle() {
        return "Dialog";
    }
}
