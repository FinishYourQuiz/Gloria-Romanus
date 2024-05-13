package unsw.gloriaromanus;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;

public class StartScreen {
    private Stage stage;
    private String title;
    private StartController controller;
    private Scene scene;

    public StartScreen(Stage stage) throws IOException {
        this.stage = stage;
        title = "Start Screen";

        controller = new StartController();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("start.fxml"));      
        // loader.setController(controller);

        // load into a Parent node called root
        Parent root = loader.load();
        controller = loader.getController();
        scene = new Scene(root, 800, 700);

    }

    public void start() {
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
        new Play0("music/Start.mp3").start();
    }

    public void backHome(){
        controller.clickCancel();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public StartController getController() {
        return controller;
    }
}
