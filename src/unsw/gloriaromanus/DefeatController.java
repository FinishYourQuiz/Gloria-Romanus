package unsw.gloriaromanus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DefeatController {

    @FXML
    private Button back;

    private StartScreen startScreen;

    @FXML
    void handleBack() {
        // turn to main menu 
        startScreen.start();
    }

    public void setStartscreen(StartScreen start) {
        this.startScreen = start;
    }

	public void setStartScreen(StartScreen startScreen2) {
        this.startScreen = startScreen2;
	}

}
