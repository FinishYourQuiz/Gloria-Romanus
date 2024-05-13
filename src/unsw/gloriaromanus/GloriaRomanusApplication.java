package unsw.gloriaromanus;

import java.io.IOException;

import javafx.application.Application;
// import javafx.fxml.FXMLLoader;
// import javafx.scene.Parent;
// import javafx.scene.Scene;
import javafx.stage.Stage;

public class GloriaRomanusApplication extends Application {

  private static GloriaRomanusController controller;

  @Override
  //TODO
  public void start(Stage stage) throws IOException {
    // // set up the scene
    // FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
    // Parent root = loader.load();
    // controller = loader.getController();
    // Scene scene = new Scene(root);

    // // set up the stage
    // stage.setTitle("Gloria Romanus");
    // stage.setWidth(800);
    // stage.setHeight(700);
    // stage.setScene(scene);
    // stage.show();
    StartScreen startScreen = new StartScreen(stage);
    GameMapScreen gameMapScreen = new GameMapScreen(stage);
    GloriaRomanusScreen gloriaRomanusScreen = new GloriaRomanusScreen(stage);
    VictoryScreen victoryScreen = new VictoryScreen(stage);
    DefeatScreen defeatScreen = new DefeatScreen(stage);

    startScreen.getController().setGameMapScreen(gameMapScreen);
    gameMapScreen.getController().setStartScreen(startScreen);
    // gameMapScreen.getController().setGloriaRomanusScreen(gloriaRomanusScreen);
    gloriaRomanusScreen.getController().setGameMapScreen(gameMapScreen);
    gameMapScreen.getController().setDefeatScreen(defeatScreen);
    gameMapScreen.getController().setVictoryScreen(victoryScreen);
    victoryScreen.getController().setStartScreen(startScreen);
    defeatScreen.getController().setStartScreen(startScreen);
   
   
    // gloriaRomanusScreen.start();
    startScreen.start();
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {
    controller.terminate();
  }

  /**
   * Opens and runs application.
   *
   * @param args arguments passed to this application
   */
  public static void main(String[] args) {
    Application.launch(args);
  }
}