package unsw.gloriaromanus;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameMapScreen {

    private Stage stage;
    private String title;
    private GameMapController controller;
    private Scene scene;

    public GameMapScreen(Stage stage) throws IOException {
        this.stage = stage;
        title = "Game Map";
        controller = new GameMapController();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gameMap.fxml"));
        loader.setController(controller);

        // load into a Parent node called root
        Parent root = loader.load();
        // controller = loader.getController();
        scene = new Scene(root, 800, 700);
    }

    public void startLoad(GloriaRomanusSystem sys) throws JsonParseException, JsonMappingException, IOException {
        controller.loadSystem(sys);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public void start(String fac1, String fac2, String first) throws IOException {
        controller.setPlayer(fac1, fac2, first);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public void close(){
        stage.close();
    }

    public GameMapController getController() {
        return controller;
    }


}
