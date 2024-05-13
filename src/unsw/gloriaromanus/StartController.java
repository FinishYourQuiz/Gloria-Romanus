package unsw.gloriaromanus;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Random;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.util.ArrayList;
import java.util.List;

public class StartController {
    @FXML
    private Button playBtn;
    @FXML
    private Button onlineBtn;
    @FXML
    private Button historyBtn;
    @FXML
    private Button settingBtn;
    @FXML
    private Button gameMapButton;
    @FXML
    private Pane homePage;
    @FXML
    private Pane startPage;
    @FXML
    private Pane historyPage;
    @FXML
    private ChoiceBox<String> faction1;
    @FXML
    private ChoiceBox<String> faction2;
    @FXML
    private ChoiceBox<String> firstPlayer;
    @FXML
    private ChoiceBox<String> historyList;
    @FXML
    private ImageView start_img;    

    private GameMapScreen gameMapScreen;

    private GloriaRomanusSystem system;
    @FXML
    public void initialize() {
        // choices of factions
        faction1.getItems().addAll("Romans", "Carthaginians", "Gauls", "Celtic Britons", "Spanish", "Numidians",
                "Egyptians", "Seleucid Empire", "Pontus", "Amenians", "Parthians", "Germanics", "Greek City States",
                "Macedonians", "Thracians", "Dacians");
        faction1.setValue("Romans");
        faction2.getItems().addAll("Romans", "Carthaginians", "Gauls", "Celtic Britons", "Spanish", "Numidians",
                "Egyptians", "Seleucid Empire", "Pontus", "Amenians", "Parthians", "Germanics", "Greek City States",
                "Macedonians", "Thracians", "Dacians");
        faction2.setValue("Gauls");
        // choices of first player
        firstPlayer.getItems().addAll("Random", "Player1", "Player2");
        firstPlayer.setValue("Random");
    }

    @FXML
    public void clickPlay() {
        homePage.setOpacity(0.5);
        startPage.setVisible(true);
    }

    @FXML
    public void clickCancel() {
        homePage.setOpacity(1);
        startPage.setVisible(false);
    }

    @FXML
    public void clickStart() throws IOException {
        String fac1 = faction1.getValue();
        String fac2 = faction2.getValue();
        String first = firstPlayer.getValue();
        if (fac1.equals(fac2)) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setContentText("Please choose different factions!");
            alert.showAndWait();
        } else {
            if (first.equals("Random")) {
                int rand = new Random().nextInt(2);
                first = rand == 0 ? "Player1" : "Player2";
            }
            gameMapScreen.start(fac1, fac2, first);
        }
    }

    @FXML
    public void clickHistoryPane() {
        List<String> historyfile = new ArrayList<String>();
        File folder = new File("src/unsw/gloriaromanus/log");
        File[] listOfFiles = folder.listFiles();
        // get the filename in the log folder
        for (File file : listOfFiles) {
            if (file.isFile()) {
                historyfile.add(file.getName().substring(0, file.getName().lastIndexOf(".")));
            }
        }
        historyList.getItems().addAll(historyfile);
        historyList.setValue("");

        homePage.setOpacity(0.5);
        historyPage.setVisible(true);
    }

    @FXML
    public void clickCancelHistoy() {
        homePage.setOpacity(1);
        historyPage.setVisible(false);
    }

    public void load(String path) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void clickStartHistoy() throws JsonParseException, JsonMappingException, IOException,
            ClassNotFoundException {

        String path = historyList.getValue();
        path = "src/unsw/gloriaromanus/log/"+path+".json";
        System.out.println(path);
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(path));
        GloriaRomanusSystem sys = (GloriaRomanusSystem) inputStream.readObject();
        System.out.println(sys.toString());

        homePage.setOpacity(1);
        gameMapScreen.startLoad(sys);
        inputStream.close();
        
    }

    public void setGameMapScreen(GameMapScreen gameMapScreen) {
        this.gameMapScreen = gameMapScreen;
    }

    void terminate() {
        return; 
    }

}
