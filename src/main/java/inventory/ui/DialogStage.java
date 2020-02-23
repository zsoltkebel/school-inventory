package inventory.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 *
 */
abstract public class DialogStage extends Stage {

    private FXMLLoader fxmlLoader;

    protected abstract String getFXMLName();

    public DialogStage() {
        fxmlLoader = new FXMLLoader(getClass().getResource(getFXMLName()));

        try {
            Parent parent = fxmlLoader.load();

            Scene scene = new Scene(parent, 300, 200);
            this.initModality(Modality.APPLICATION_MODAL);
            this.setScene(scene);

            this.setTitle(getDialogTitle());

            // default min values
            setMinWidth(200);
            setMinHeight(250);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public<T> T getController() {
        return fxmlLoader.getController();
    }

    public void openDialog() {
        this.showAndWait();
    }

    public String getDialogTitle() {
        return "Dialog";
    }
}
