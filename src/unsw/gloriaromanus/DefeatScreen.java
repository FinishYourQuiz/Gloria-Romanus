package unsw.gloriaromanus;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DefeatScreen {

    private Stage stage;
    private static DefeatController controller;
    private Scene scene;

    public DefeatScreen(Stage stage) throws IOException {
        this.stage = stage;

        controller = new DefeatController();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("defeat.fxml"));
        // loader.setController(controller);
        // load into a Parent node called root
        Parent root = loader.load();
        controller = loader.getController();
        scene = new Scene(root);
    }

    public void start() {
        stage.setTitle("Defeat");
        stage.setWidth(800);
        stage.setHeight(700);
        stage.setScene(scene);
        stage.show();
        new Play0("music/Lose.mp3").start();
    }

    public DefeatController getController() {
        return controller;
    }
}
