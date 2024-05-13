package unsw.gloriaromanus;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VictoryScreen {

    private Stage stage;
    private static VictoryController controller;
    private Scene scene;

    public VictoryScreen(Stage stage) throws IOException {
        this.stage = stage;

        controller = new VictoryController();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("victory.fxml"));
        // loader.setController(controller);
        // load into a Parent node called root
        Parent root = loader.load();
        controller = loader.getController();
        scene = new Scene(root);
    }

    public void start() {
        stage.setTitle("Victory");
        stage.setWidth(800);
        stage.setHeight(700);
        stage.setScene(scene);
        stage.show();
        new Play0("music/Win.mp3").start();
    }

    public VictoryController getController() {
        return controller;
    }
}
