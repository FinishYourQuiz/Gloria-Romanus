package unsw.gloriaromanus;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GloriaRomanusScreen {

    private Stage stage;
    private static GloriaRomanusController controller;
    private Scene scene;

    public GloriaRomanusScreen(Stage stage) throws IOException {
        this.stage = stage;

        controller = new GloriaRomanusController();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        // loader.setController(controller);
        // load into a Parent node called root
        Parent root = loader.load();
        controller = loader.getController();
        scene = new Scene(root);
    }

    public void start() {
        stage.setTitle("Gloria Romanus");
        stage.setWidth(800);
        stage.setHeight(700);
        stage.setScene(scene);
        stage.show();
    }

    public GloriaRomanusController getController() {
        return controller;
    }
}
