package inventory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static final int MIN_WIDTH = 1000;
    private static final int MIN_HEIGHT = 700;

    private static final int PREF_WIDTH = MIN_WIDTH;
    private static final int PREF_HEIGHT = MIN_HEIGHT;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        primaryStage.setTitle("Inventory App");

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);

        primaryStage.setWidth(PREF_WIDTH);
        primaryStage.setHeight(PREF_HEIGHT);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
