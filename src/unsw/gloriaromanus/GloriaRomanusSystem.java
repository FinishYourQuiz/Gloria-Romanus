package unsw.gloriaromanus;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class GloriaRomanusSystem implements Observer, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Faction player1;
    private Faction player2;
    private int currentPlayer; //
    private int turn;
    private int year;
    private Goal goal;
    private boolean showWon;
    private ArrayList<Province> provinces;
    private boolean isEnd;
    // private int log_count;

    public GloriaRomanusSystem(String faction1, String faction2, String firstplayer) throws IOException {
        this.provinces = setProvinces();
        ArrayList<Province> pList1 = new ArrayList<>(provinces.subList(0, provinces.size() / 2));
        ArrayList<Province> pList2 = new ArrayList<>(provinces.subList(provinces.size() / 2, provinces.size()));
        Faction player1 = new Faction(faction1);
        Faction player2 = new Faction(faction2);
        // give free units to players
        freeUnits(pList1);
        freeUnits(pList2);
        // Set provinces to faction
        player1.setProvinces(pList1);
        player2.setProvinces(pList2);

        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = firstplayer == "Player1" ? 0 : 1;
        this.isEnd = false;
        this.turn = 1;
        this.goal = gernerator();
        this.year = 200;
    }

    public GloriaRomanusSystem load(String path) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        GloriaRomanusSystem sys = objectMapper.readValue(new File("save.json"), GloriaRomanusSystem.class);
        return sys;
    }

    public boolean isPlayer1(String faction_name) {
        if (player1.getName().equals(faction_name))
            return true;
        return false;
    }

    public void freeUnits(ArrayList<Province> pList) {
        String[] unitNames = new String[] { "melee infantry", "spearmen", "missile infantry", "heavy infantry",
                "melee cavalry", "horse archers", "elephants", "chariots", "artillery", "melee artillery" };
        Random ran = new Random();
        for (Province province : pList) {
            province.setWealthOfProvince(ran.nextInt(100));
            province.setTax(10);
            province.addFreeUnit(unitNames[ran.nextInt(10)]);
        }
    }

    public Goal getGoal() {
        return goal;
    }

    public ArrayList<Province> setProvinces() throws IOException {
        List<String> pvc = Arrays.asList("Lugdunensis", "Lusitania", "Lycia et Pamphylia", "Macedonia",
                "Mauretania Caesariensis", "Mauretania Tingitana", "Moesia Inferior", "Moesia Superior", "Narbonensis",
                "Noricum", "Numidia", "Pannonia Inferior", "Pannonia Superior", "Raetia", "Sardinia et Corsica",
                "Sicilia", "Syria", "Tarraconensis", "Thracia", "V", "VI", "VII", "VIII", "X", "XI", "Achaia",
                "Aegyptus", "Africa Proconsularis", "Alpes Cottiae", "Alpes Graiae et Poeninae", "Alpes Maritimae",
                "Aquitania", "Arabia", "Armenia Mesopotamia", "Asia", "Baetica", "Belgica", "Bithynia et Pontus",
                "Britannia", "Cilicia", "Creta et Cyrene", "Cyprus", "Dacia", "Dalmatia", "Galatia et Cappadocia",
                "Germania Inferior", "Germania Superior", "I", "II", "III", "IV", "IX", "Iudaea");

        // convert the list of name to list of Province
        ArrayList<Province> provinces = new ArrayList<>();
        for (String pvc_name : pvc) {
            Province province = new Province(pvc_name);
            provinces.add(province);
        }

        // Shuffle the list
        Collections.shuffle(provinces);

        return provinces;
    }

    // update the system
    public void endTurn() {
        this.turn += 1;
        this.year += 1;
        getCurrentPlayer().endTurn();
        if (!isEnd) {
            update(getCurrentPlayer());
            if (!isEnd) {
                currentPlayer = currentPlayer == 0 ? 1 : 0;
            }
        } else {
            showWon = true;
        }
    }

    public boolean hasShownWinner() {
        return showWon;
    }

    public String getYear() {
        return "BC" + year;
    }

    public Province getProvinceByName(String pvc_name) {
        for (Province p : provinces) {
            if (p.getName().equals(pvc_name))
                return p;
        }
        return null;
    }

    public String getTurn() {
        return "" + turn;
    }

    public Faction getOwnership(String province) {
        if (player2.isOwned(province)) {
            return player2;
        }
        return player1;
    }

    public Faction getCurrentPlayer() {
        if (currentPlayer == 0) {
            return player1;
        }
        return player2;
    }

    public Faction getCurrentEnemyPlayer(){
        if(currentPlayer==0){
            return player2;
        }       
        return player1;
    }


    public List<String> provincesOfCurrentPlayer() {
        return getCurrentPlayer().getProvincesName();
    }

    public List<String> provincesOfEnemyPlayer(){
        return getCurrentEnemyPlayer().getProvincesName();
    }

    @Override
    public void update(Faction f) {
        turn++;
        if (goal.achieveAll(f)) {
            System.out.println(this.goal);
            System.out.println("Winner of the game!!\n" + f.getName());
            endgame();
        }
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void endgame() {
        isEnd = true;
    }

    public Goal gernerator() {
        // goals
        List<String> choices = Arrays.asList("CONQUEST", "TREASURY", "WEALTH");
        // indexs
        Collections.shuffle(choices);

        Goal root = new Goal(new Random().nextInt(2) == 0 ? "AND" : "OR");
        Goal g1 = new Goal(choices.get(0));
        Goal g2 = new Goal(choices.get(1));
        Goal g3 = new Goal(choices.get(2));
        Goal root2 = new Goal(new Random().nextInt(2) == 0 ? "AND" : "OR");
        root2.addSubGoal(g2);
        root2.addSubGoal(g3);
        root.addSubGoal(root2);
        root.addSubGoal(g1);

        System.out.println(root.toString());

        return root;
    }

    @Override
    public String toString() {
        return "Goal is: " + goal + "\nCurrent Turn: " + turn + "\nCurrent Player: " + getCurrentPlayer().getName()
                + "\nPlayer1: " + player1 + "\nPlayer2: " + player2;
    }
}
