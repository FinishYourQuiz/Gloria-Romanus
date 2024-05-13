package unsw.gloriaromanus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class VictoryController {

    @FXML
    private Pane victoryPage;

    @FXML
    private Button back;

    //@FXML
    //private Button continu;

    private StartScreen startScreen;
    //private GameMapScreen gameMapScreen;

    @FXML
    void handleBack() {
        startScreen.start();
    }
    /*
    @FXML
    void handleContinue(ActionEvent event) {
        // back to the game scene
        // gameMapScreen.start();

    }
    */
    public void setStartScreen(StartScreen start) {
        this.startScreen = start;
    }
    /*
    public void setGameMapScreen(GameMapScreen gameMap) {
        this.gameMapScreen = gameMap;
    }
    */

}
