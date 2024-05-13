package unsw.gloriaromanus;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.GeoPackage;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol.HorizontalAlignment;
import com.esri.arcgisruntime.symbology.TextSymbol.VerticalAlignment;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.geojson.FeatureCollection;
import org.geojson.LngLatAlt;

public class GameMapController {

  @FXML
  private MapView mapView;
  @FXML
  private Pane recruitPane;
  @FXML
  private Pane movePane;
  @FXML
  private Pane troopSelections;
  @FXML
  private TextField chosenProvince;
  @FXML
  private TextArea info_terminal;
  @FXML
  private TextArea profile_terminal;
  @FXML
  private TextArea output_terminal;
  @FXML
  private ChoiceBox<String> troopTypes;
  @FXML
  private ChoiceBox<Integer> troopNums;
  @FXML
  private ChoiceBox<String> troopLocations;
  @FXML
  private ChoiceBox<String> toWhere; 
  @FXML
  private ChoiceBox<Integer> taxLevel;
  @FXML
  private TextArea fromWhere;
  @FXML
  private ChoiceBox<String> yourProvince;
  @FXML
  private ChoiceBox<String> enemyProvince;
  @FXML
  private TextArea year;
  @FXML
  private TextArea turn;
  @FXML
  private TextArea factionName;
  @FXML
  private TextArea faction_gold;
  @FXML
  private TextArea faction_wealth;
  @FXML
  private TextArea battleMessage;
  
  @FXML
  private Pane engagementPane;
  @FXML 
  private ImageView invaderImg;
  @FXML 
  private ImageView defenderImg;
  @FXML
  private Button restartBtn;


  private GloriaRomanusScreen gloriaRomanusScreen;
  private ArrayList<Unit> units_selected;
  private BattleResolver battleResolver;
  private ArcGISMap map;
  private String troop_type;
  private Integer troop_num;
  private String troop_location;
  private int color1;
  private int color2;

  // FACTION
  private FeatureLayer featureLayer_provinces;
  private String faction1;
  private String faction2;
  private String firstplayer;

  // TROOP
  private GloriaRomanusSystem system;

  private Feature currentlySelectedProvince;
  private GameMapScreen gameScreen;
  private VictoryScreen victoryScreen;
  private DefeatScreen defeatScreen;
  private StartScreen startScreen;

  private void setUp() throws IOException {
    // Build up the provinces with ownerships
    Faction curr = system.getCurrentPlayer();
    factionName.setText(curr.getName());
    faction_wealth.setText(""+curr.getWealth());
    faction_gold.setText(""+curr.getTreasure());
    year.setText(system.getYear());
    turn.setText(system.getTurn());

    List<String> l1 = system.provincesOfCurrentPlayer();
    List<String> l2 = system.provincesOfEnemyPlayer();
    Collections.sort(l1);
    Collections.sort(l2);
    yourProvince.getItems().addAll(l1);
    enemyProvince.getItems().addAll(l2);
  }

  @FXML
  private void initialize() throws JsonParseException, JsonMappingException, IOException {
    // provinceToOwningFactionMap = getProvinceToOwningFactionMap();
      color1 = 0xF00BFFF0;
      color2 = 0xFFF7F500;
      units_selected = new ArrayList<>();
      currentlySelectedProvince = null;
      initializeProvinceLayers();
  }

  @FXML
  public void handleChosenProvince(ActionEvent e) throws IOException {
    if (currentlySelectedProvince != null) {
      addAllPointGraphics(); // reset graphics
    }
  }

  // @FXML
  // public void handleBackButton(ActionEvent event) {
  //   startScreen.start();
  // }

  public void loadSystem(GloriaRomanusSystem sys) throws JsonParseException, JsonMappingException, IOException {
    this.system = sys;
    setUp();
    initializeProvinceLayers();
    addAllPointGraphics();
    alertShow("To win this game, you should: \n"+system.getGoal().toString());
  }

  @FXML
  public void clickRestart(ActionEvent event) {
      startScreen.start();
  }

  public void setStartScreen(StartScreen startScreen) {
      this.startScreen = startScreen;
  }

  public void setVictoryScreen(VictoryScreen victoryScreen) {
    this.victoryScreen = victoryScreen;
  }

  public void setDefeatScreen(DefeatScreen defeatScreen) {
    this.defeatScreen = defeatScreen;
  }

  private void initializeProvinceLayers() throws JsonParseException, JsonMappingException, IOException {

    Basemap myBasemap = Basemap.createImagery();
    // myBasemap.getReferenceLayers().remove(0);
    map = new ArcGISMap(myBasemap);
    mapView.setMap(map);

    GeoPackage gpkg_provinces = new GeoPackage("src/unsw/gloriaromanus/provinces_right_hand_fixed.gpkg");
    gpkg_provinces.loadAsync();
    gpkg_provinces.addDoneLoadingListener(() -> {
      if (gpkg_provinces.getLoadStatus() == LoadStatus.LOADED) {
        // create province border feature
        featureLayer_provinces = createFeatureLayer(gpkg_provinces);
        map.getOperationalLayers().add(featureLayer_provinces);

      } else {
        System.out.println("load failure");
      }
    });
  }

  private void addAllPointGraphics() throws JsonParseException, JsonMappingException, IOException {
    mapView.getGraphicsOverlays().clear();

    InputStream inputStream = new FileInputStream(new File("src/unsw/gloriaromanus/provinces_label.geojson"));
    FeatureCollection fc = new ObjectMapper().readValue(inputStream, FeatureCollection.class);

    GraphicsOverlay graphicsOverlay = new GraphicsOverlay();

    // add the faction upon each province
    for (org.geojson.Feature f : fc.getFeatures()) {
      if (f.getGeometry() instanceof org.geojson.Point) {
        org.geojson.Point p = (org.geojson.Point) f.getGeometry();
        LngLatAlt coor = p.getCoordinates();
        Point curPoint = new Point(coor.getLongitude(), coor.getLatitude(), SpatialReferences.getWgs84());
        PictureMarkerSymbol s = null;
        String province_name = (String) f.getProperty("name");
        String faction = system.getOwnership(province_name).getName();        
        TextSymbol t = new TextSymbol(15, province_name, system.isPlayer1(faction)? color1 : color2, HorizontalAlignment.CENTER,
            VerticalAlignment.BOTTOM);
      switch (faction) {
          case "Gauls":
            s = new PictureMarkerSymbol(new Image((new File("images/CS2511Sprites_No_Background/Flags/Gallic/GallicFlag.png")).toURI().toString()));
            break;
          case "Romans":
            s = new PictureMarkerSymbol("images/CS2511Sprites_No_Background/Flags/Roman/RomanFlag.png");
            break;
          case "Carthaginians":
            s = new PictureMarkerSymbol("images/CS2511Sprites_No_Background/Flags/Carthage/CathageFlag (1).png");
            break;
          case "Celtic Britons":
            s = new PictureMarkerSymbol("images/CS2511Sprites_No_Background/Flags/Celtic/CelticFlag.png");
            break;
          case "Spanish":
            s = new PictureMarkerSymbol("images/CS2511Sprites_No_Background/Flags/Spanish/SpanishFlag.png");
            break; 
          case "Egyptians":
            s = new PictureMarkerSymbol("images/CS2511Sprites_No_Background/Flags/Egyptian/EgyptianFlag.png");
            break; 
          case "Numidians":
            s = new PictureMarkerSymbol("images/CS2511Sprites_No_Background/Flags/NumidiansFlag.png");
            break;
          case "Seleucid Empire":
            s = new PictureMarkerSymbol("images/CS2511Sprites_No_Background/Flags/SeleucidEmpireFlag.png");
            break;
          case "Pontus":
            s = new PictureMarkerSymbol("images/CS2511Sprites_No_Background/Flags/PontusFlag.png");
            break;
          case "Amenians":
            s = new PictureMarkerSymbol("images/CS2511Sprites_No_Background/Flags/AmeniansFlag.png");
            break;
          case "Parthians":
            s = new PictureMarkerSymbol("images/CS2511Sprites_No_Background/Flags/ParthiansFlag.png");
            break;
          case "Germanics":
            s = new PictureMarkerSymbol("images/CS2511Sprites_No_Background/Flags/GermanicsFlag.png");
            break;
          case "Greek City States":
            s = new PictureMarkerSymbol("images/CS2511Sprites_No_Background/Flags/GreekCityStatesFlag.png");
            break;
          case "Macedonians":
            s = new PictureMarkerSymbol("images/CS2511Sprites_No_Background/Flags/MacedoniansFlag.png");
            break;
          case "Thracians":
            s = new PictureMarkerSymbol("images/CS2511Sprites_No_Background/Flags/ThraciansFlag.png");
            break;
          case "Dacians":
            s = new PictureMarkerSymbol("images/CS2511Sprites_No_Background/Flags/DaciansFlag.png");
            break;
        }
        t.setHaloColor(0xFFFFFFF);
        t.setHaloWidth(2);
        s.setHeight((float)22);
        s.setWidth((float)22);
        s.setOffsetY(-5);
        Graphic gPic = new Graphic(curPoint, s);
        Graphic gText = new Graphic(curPoint, t);
        graphicsOverlay.getGraphics().add(gPic);
        graphicsOverlay.getGraphics().add(gText);
      } else {
        System.out.println("Non-point geo json object in file");
      }
    }
    inputStream.close();
    mapView.getGraphicsOverlays().add(graphicsOverlay);
  }

  public void setPlayer(String fac1, String fac2, String first) throws IOException {
    this.faction1 = fac1;
    this.faction2 = fac2;
    this.firstplayer = first;
    system = new GloriaRomanusSystem(faction1, faction2, firstplayer);
    setUp();
    initializeProvinceLayers();
    addAllPointGraphics();
    alertShow("To win this game, you should: \n"+system.getGoal().toString());
  }

  private FeatureLayer createFeatureLayer(GeoPackage gpkg_provinces) {
    FeatureTable geoPackageTable_provinces = gpkg_provinces.getGeoPackageFeatureTables().get(0);

    // Make sure a feature table was found in the package
    if (geoPackageTable_provinces == null) {
      System.out.println("no geoPackageTable found");
      return null;
    }

    // Create a layer to show the feature table
    FeatureLayer flp = new FeatureLayer(geoPackageTable_provinces);

    // https://developers.arcgis.com/java/latest/guide/identify-features.htm
    // listen to the mouse clicked event on the map view
    mapView.setOnMouseClicked(e -> {
      // was the main button pressed?
      if (e.getButton() == MouseButton.PRIMARY) {
        // get the screen point where the user clicked or tapped
        Point2D screenPoint = new Point2D(e.getX(), e.getY());

        final ListenableFuture<IdentifyLayerResult> identifyFuture = mapView.identifyLayerAsync(flp, screenPoint, 0,
            false, 25);

        // add a listener to the future
        identifyFuture.addDoneListener(() -> {
          try {
            // get the identify results from the future - returns when the operation is
            // complete
            IdentifyLayerResult identifyLayerResult = identifyFuture.get();
            // a reference to the feature layer can be used, for example, to select
            // identified features
            if (identifyLayerResult.getLayerContent() instanceof FeatureLayer) {
              FeatureLayer featureLayer = (FeatureLayer) identifyLayerResult.getLayerContent();
              // select all features that were identified
              List<Feature> features = identifyLayerResult.getElements().stream().map(f -> (Feature) f)
                  .collect(Collectors.toList());

              if (features.size() > 1) {
                System.out.println("Have more than 1 element - you might have clicked on boundary!");
              } else if (features.size() == 1) {
                Feature f = features.get(0);
                String province_name = (String) f.getAttributes().get("name");
                Province province = system.getProvinceByName(province_name);
                if (province != null) {
                  // province owned by human
                  if (currentlySelectedProvince != null) {
                    featureLayer.unselectFeature(currentlySelectedProvince);
                  }
                  currentlySelectedProvince = f;
                  chosenProvince.setText(province_name);
                  printMessageToTerminal(province.toString());
                  taxInfo();
                  output_terminal.setText("");
                  if(province.getFaction().equals(system.getCurrentPlayer())){
                    yourProvince.setValue(province_name);
                    if(enemyProvince.getValue()==null && system.provincesOfEnemyPlayer().size() > 0){
                      enemyProvince.setValue(system.provincesOfEnemyPlayer().get(0));
                    }
                  }else{
                    enemyProvince.setValue(province_name);
                    if(yourProvince.getValue()==null && system.provincesOfCurrentPlayer().size() > 0){
                      yourProvince.setValue(system.provincesOfCurrentPlayer().get(0));
                    }
                    System.out.println(yourProvince.getValue());
                  }
                }

                featureLayer.selectFeature(f);
              }
            }
          } catch (InterruptedException | ExecutionException ex) {
            System.out.println("InterruptedException occurred");
          }
        });
      }
    });
    return flp;
  }

  private void printMessageToTerminal(String message) {
    info_terminal.setText(message + "\n");
  }

  private void alertShow(String info) {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("Information Dialog");
    alert.setHeaderText(null);
    alert.setContentText(info);

    alert.showAndWait();
  }

  @FXML
  public void clickRecruitPane() {
    if (currentlySelectedProvince != null) {
      String curr_pvc = (String) currentlySelectedProvince.getAttributes().get("name");
      List<String> pvc_list = system.provincesOfCurrentPlayer();
      if (!pvc_list.contains(curr_pvc)) {
        alertShow("Can't recruit any troop on the land of your enermy!");
        return;
      }
      troopTypes.getItems().addAll("melee infantry", "spearmen", "missile infantry", "heavy infantry", "melee cavalry",
          "horse archers", "elephants", "chariots", "artillery", "melee artillery");
      troopTypes.setValue("melee cavalry");
      troopNums.getItems().addAll(20, 40, 50, 60, 80, 100, 120, 140);
      troopNums.setValue(60);
      troopLocations.setValue(curr_pvc);
      troopLocations.getItems().addAll(pvc_list);
      troop_type = troopTypes.getValue();
      troop_num = troopNums.getValue();
      troop_location = troopLocations.getValue();
      recruitPane.setVisible(true);
    }
  }

  @FXML
  public void clickRecruit() throws JsonParseException, JsonMappingException, IOException {
    Province province = system.getProvinceByName(troop_location);
    String res = province.recruitUnit(troop_type, troop_num);
    alertShow(res);
    addAllPointGraphics();
    faction_gold.setText(""+system.getCurrentPlayer().getTreasure());
    recruitPane.setVisible(false);
  }

  @FXML
  public void clickCancelRecruit() {
    recruitPane.setVisible(false);
  }

  @FXML
  public void clickMovePane() {
    if (currentlySelectedProvince != null) {
      List<String> l = system.provincesOfCurrentPlayer();
      String curr_pvc = (String) currentlySelectedProvince.getAttributes().get("name");
      toWhere.getItems().addAll(l);
      toWhere.setValue(curr_pvc);
      fromWhere.setText(curr_pvc);
      VBox vbox = new VBox(); 

      ArrayList<Unit> units = system.getProvinceByName(curr_pvc).getUnits();
      for (Unit u : units) {
        CheckBox cb = new CheckBox(u.getName());
        cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
            // cb.setSelected(!new_val);
            if(cb.isSelected() && !units_selected.contains(u)){
              units_selected.add(u);
            }else if(!cb.isSelected()){
              units_selected.remove(u);
            }
          }
        });
        vbox.getChildren().add(cb);
      }
      troopSelections.getChildren().add(vbox);
      movePane.setVisible(true);
    }
  }

  @FXML 
  public void clickCancelMove(){
    movePane.setVisible(false);
    troopSelections.getChildren().clear();
    units_selected = new ArrayList<>();
  }

  @FXML
  public void clickMove() throws IOException {
    Province src = system.getProvinceByName(fromWhere.getText());
    Province target = system.getProvinceByName(toWhere.getValue());
    if(src==null || target==null){
      alertShow("Please select the provinces!");
      return;
    }
    if(units_selected.size()==0){
      alertShow("Please select at least one unit!");
      return;
    }
    String res = "";
    if(units_selected.size() == 1){
      res = src.moveUnit(target, units_selected.get(0));
    }else if(units_selected.size() > 1){
      res = src.moveUnits(target, units_selected);
    }
    if(res.equals("True")){
      alertShow("Success in move!!");
      clickCancelMove();
      info_terminal.setText(src.toString());
      return;
    }
    info_terminal.setText(src.toString());
    alertShow(res);
    return;
  }

  @FXML
  public void clickInvade() throws IOException {
      battleResolver = new BattleResolver();

      // get battle provinces 
      String invader = yourProvince.getSelectionModel().getSelectedItem();
      String defender = enemyProvince.getSelectionModel().getSelectedItem();
      Province i = system.getProvinceByName(invader);
      Province d = system.getProvinceByName(defender);

      // get gifs
      Image meleeinvadergif;
      Image rangedinvadergif;
      Image meleedefendergif;
      Image rangeddefendergif;
      if(i.getFaction().getName().equals(faction1)) {
        meleeinvadergif = new Image(new File("images/player1-melee.gif").toURI().toString());
        rangedinvadergif = new Image(new File("images/player1-ranged.gif").toURI().toString());
        meleedefendergif = new Image(new File("images/enemy2-melee.gif").toURI().toString());
        rangeddefendergif = new Image(new File("images/enemy2-ranged.gif").toURI().toString());
      } else {
        meleeinvadergif = new Image(new File("images/player2-melee.gif").toURI().toString());
        rangedinvadergif = new Image(new File("images/player2-ranged.gif").toURI().toString());
        meleedefendergif = new Image(new File("images/enemy1-melee.gif").toURI().toString());
        rangeddefendergif = new Image(new File("images/enemy1-ranged.gif").toURI().toString());
      }

      // add province list to choicebox
      List<String> l1 = system.provincesOfCurrentPlayer();
      List<String> l2 = system.provincesOfEnemyPlayer();
      Collections.sort(l1);
      Collections.sort(l2);
      yourProvince.getItems().clear();
      enemyProvince.getItems().clear();
      yourProvince.getItems().addAll(l1);
      enemyProvince.getItems().addAll(l2);
      yourProvince.setValue(l1.size()>0? l1.get(0):null);
      enemyProvince.setValue(l2.size()>0? l2.get(0):null);
      
      // display battle info
      String battleinfo ="!!  " +invader + " attemps to invade " + defender+ "  !!";
      System.out.println(battleinfo);
      printInvadeResult(battleinfo);
      
      battleResolver.setSides(d, i);

      if (i == null || d == null) printInvadeResult("Empty field: cannot invade!");
      //} else 
      //enemyProvince.getValue())

      // start battle
      if (battleResolver.isConnected(invader, defender)){
        // battleResolver.setSides(enemyProvince, yourProvince);
        ArrayList<String> battleResult = battleResolver.invade();
        // get the result of invadePlayMusic
        if(battleResult != null) {
          for(String msg:battleResult) {
            printInvadeResult(msg);
            if(msg.contains("MELEE")) {
              invaderImg.setImage(meleeinvadergif);
              defenderImg.setImage(meleedefendergif);
              battleMessage.setText(msg.replace(">", ""));
              engagementPane.setVisible(true);
            } else if (msg.contains("RANGED")) {
              invaderImg.setImage(rangedinvadergif);
              defenderImg.setImage(rangeddefendergif);
              battleMessage.setText(msg.replace(">", ""));
              engagementPane.setVisible(true);
            }
          }
        }
        addAllPointGraphics(); // reset graphics
        
    }
    else{
      printInvadeResult("Provinces not adjacent, cannot invade!");
    }
    
  }
  @FXML
  public void clickBackEnagement(){
    engagementPane.setVisible(false);
  }

  private void printInvadeResult (String message) {
    output_terminal.appendText(message + "\n");

  }

  @FXML
  public void clickEndTurn(){
    if(!system.hasShownWinner() && system.isEnd()){
      alertShow("Winner of game: "+system.getCurrentPlayer().getName()+"!\nCongraduation!");
    }
    system.endTurn();
    Faction curr = system.getCurrentPlayer();
    Faction enemy = system.getCurrentEnemyPlayer();
    factionName.setText(curr.getName());
    faction_wealth.setText(""+curr.getWealth());
    faction_gold.setText(""+curr.getTreasure());
    year.setText(system.getYear());
    turn.setText(system.getTurn());
    if (system.getGoal().achieveAll(curr)) {
      save();
      victoryScreen.start();
    } else if(system.getGoal().achieveAll(enemy)) {
      defeatScreen.start();
    }

  }

  @FXML
  public void clickSavePane(){
    save();
    alertShow("Success in save!");
  }

  @FXML
  public void backHome(){
    save();
    alertShow("Success in save!");
    startScreen.backHome();
  }

  @FXML
  public void clickTaxChange(){
    int new_tax = taxLevel.getValue();
    String curr_pvc = (String) currentlySelectedProvince.getAttributes().get("name");
    Province province = system.getProvinceByName(curr_pvc);
    province.setTax(new_tax);
    printMessageToTerminal(province.toString());
  }

  private void taxInfo(){
    if (currentlySelectedProvince != null) {
      String curr_pvc = (String) currentlySelectedProvince.getAttributes().get("name");
      Province province = system.getProvinceByName(curr_pvc);
      int tax = province.getTax();
      taxLevel.getItems().addAll(5,10,15,20,25,30,35,40);
      taxLevel.setValue(tax);
    }
  }

  @FXML
  public void clickTaxEnter(){
    String curr_pvc = (String) currentlySelectedProvince.getAttributes().get("name");
    Province province = system.getProvinceByName(curr_pvc);
    province.setTax(taxLevel.getValue());
  }
  public void save() {
    try {
      String name = new SimpleDateFormat("yyyy-MM-dd hh:mm").format(new Date());
      String path = "src/unsw/gloriaromanus/log/"+name+".json";
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(path));
      objectOutputStream.writeObject(system);
      objectOutputStream.close();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  

  void terminate() {
    if (mapView != null) {
      mapView.dispose();
    }
  }
}
