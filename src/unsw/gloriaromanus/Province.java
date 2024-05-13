package unsw.gloriaromanus;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONObject;

public class Province implements Serializable{
    private Faction faction;
    private String name;
    private ArrayList<Unit> units;
    private int wealthOfProvince;   
    private int tax;
    private int unit_code;

    // Set the province at the begining of the game
    public Province(String name){
        this.name = name;
        this.unit_code = 1;
        this.faction = null;
        this.units = new ArrayList<>();
        this.wealthOfProvince = 100;     // start-up wealth of province
        this.tax = 10;
    }

    public String getName(){
        return this.name;
    }

    public Faction getFaction(){
        return faction;
    }
    // set faction or update the faction it belongs to
    public void setFaction(Faction new_faction) {
        if(this.faction != null){
            this.faction.removeProvince(this);
        }
        this.faction = new_faction;
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

    public ArrayList<Unit> getAvailableUnits(){
        ArrayList<Unit> l = new ArrayList<>();
        for(Unit i: units){
            if(i.isAvailable()){
                l.add(i);
            }
        }
        return l;
    }

    public void setUnits(ArrayList<Unit> units) {
        this.units = units;
    }

    public int getWealthOfProvince() {
        return wealthOfProvince;
    }

    public void setWealthOfProvince(int wealthOfProvince) {
        this.wealthOfProvince = wealthOfProvince;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public void endturn(){
        // refresh the point of unit 
        for(Unit unit: units){
            unit.endturn();
        }
        // update wealth
        addWealth();
    }

    // TOWN WEALTH 
    // add the wealth by the corresponding tax

    public void addWealth(){
        if(tax <= 10){
            this.wealthOfProvince += 10;
        }else if(tax <= 15){
            this.wealthOfProvince -= 10;
        }else if(tax <= 20){
        }else{
            this.wealthOfProvince -= 30;
        }
    }

    public int getRevenue(){
        return this.wealthOfProvince * tax / 100;
    }

    // UNIT
    // create a new unit of soldiers
    // the name of the unit :: headvy infantry 1:: chariots 4

    public String recruitUnit(String name, int troopNum){
        int remain = faction.getTreasure();
        Unit newUnit = null;
        switch (name) {
            case "melee cavalry":
            case "heavy cavalry":
            case "lancers":
            case "elephants":
            case "chariots":
                newUnit = new Cavalry(name+" "+unit_code, this, troopNum);
                break;
            case "missile infantry":
            case "heavy infantry":
            case "spearmen":
            case "melee infantry":
                newUnit = new Infantry(name+" "+unit_code,this, troopNum);
                break;
            case "melee artillery":
            case "artillery":
            case "horse archers":
                newUnit = new Artillery(name+" "+unit_code, this, troopNum);
                break;
        }

        if(newUnit == null){
            return "Please enter the correct name you wanna recruit\n";
        }else if(remain >= newUnit.getPrice()){
            this.units.add(newUnit);
            this.faction.pay(newUnit.getPrice());
            this.unit_code ++;
        }else{
            return "You dont have enough money to recruit!\n";
        }
        return "Success in recruit!\n";
    }

    // delete a unit of the units
    public void deleteUnit(Unit u){
        // String name = u.getName();
        this.units.remove(u);
    }

    public void addUnit(Unit unit){
        if(unit!=null){
            this.units.add(unit);
        }
    }

    public void emptyProvince(){
        this.units = new ArrayList<>();
    }
    // give the player free units to player with
    public void addFreeUnit(String name){
        Unit newUnit = null;
        switch (name) {
            //"melee infantry", "spearmen", "missile infantry", "heavy infantry", "melee cavalry", "horse archers", "elephants", "chariots", "artillery","melee artillery"
            case "melee cavalry":
            case "heavy cavalry":
            case "lancers":
            case "elephants":
            case "chariots":
                newUnit = new Cavalry(name+" "+unit_code, this, 20);
                break;
            case "missile infantry":
            case "heavy infantry":
            case "spearmen":
            case "melee infantry":
                newUnit = new Infantry(name+" "+unit_code,this, 20);
                break;
            case "melee artillery":
            case "artillery":
            case "horse archers":
                newUnit = new Artillery(name+" "+unit_code, this, 20);
                break;
        }
        if(newUnit != null){
            newUnit.endturn();
            this.units.add(newUnit);
            unit_code ++;
        }
    }

    public boolean confirmIfProvincesConnected(String province1, String province2) throws IOException {
        String content = Files.readString(Paths.get("src/unsw/gloriaromanus/province_adjacency_matrix_fully_connected.json"));
        JSONObject provinceAdjacencyMatrix = new JSONObject(content);
        return provinceAdjacencyMatrix.getJSONObject(province1).getBoolean(province2);
    }

    // get the matrix of the provinces of the user owned
    public ArrayList<ArrayList<Integer>> getMatrix(ArrayList<Province>provinces_list) throws IOException {
        ArrayList<ArrayList<Integer>> matrix = new ArrayList<>();
        for(Province p1: provinces_list){
            ArrayList<Integer> line = new ArrayList<>();
            for(Province p2: provinces_list){
                if(p2.getName() == p1.getName()){
                    line.add(0);
                }else if(confirmIfProvincesConnected(p1.getName(), p2.getName())){
                    line.add(4);
                }else{
                    line.add(1000);
                }
            }
            matrix.add(line);
        }
        return matrix;
    }

    // attampt move a unit to province
    public String moveUnit(Province target, Unit unit) throws IOException {
        // get the matrix of provinces of faction
        ArrayList<Province> provinces_list = this.faction.getProvinces();
        int src = provinces_list.indexOf(this);
        int dest = provinces_list.indexOf(target);
        if(provinces_list.indexOf(target) == -1){
            return "Please select your provinces only!";
        }
        
        ArrayList<ArrayList<Integer>> matrix = getMatrix(provinces_list);

        if(this.units.indexOf(unit)!=-1 && unit.isAvailable()){
            // available in this turn, attamp to move
            int shortest = unit.attamptMove(matrix, src, dest);
            if(src == dest){
                return "You are in the target province!\n";
            }
            if(shortest <= unit.getRemain_points()){
                unit.move(target, shortest);
                deleteUnit(unit);
                return "True";
            }else{
                return "Cannot move to this province within movement point!\n";
            }
        }
        return "This unit is not available untill next turn!\n";
    }

    public String moveUnits(Province target, ArrayList<Unit> move_units) throws IOException {
        // get the matrix of provinces of faction
        ArrayList<Province> provinces_list = this.faction.getProvinces();
        int src = provinces_list.indexOf(this);
        int dest = provinces_list.indexOf(target);
        if(provinces_list.indexOf(target) == -1){
            return "Please select your provinces only!";
        }
        
        ArrayList<ArrayList<Integer>> matrix = getMatrix(provinces_list);

        // get the fewest movement point in the list
        int movepoint = 1000;
        for(Unit unit: move_units){
            int curr = unit.getRemain_points();
            movepoint = curr < movepoint ? curr : movepoint;
        }
        
        ArrayList<Integer> mpList =new ArrayList<>();
        // Check each unit in the list is available to move in this turn
        for(Unit unit: move_units){
            if(!unit.isAvailable()){
                return unit.getName()+" is not available untill next turn!\n";
            }
            int curr = unit.attamptMove(matrix, src, dest);
            if(curr > movepoint){
                return unit.getName()+"cannot move to "+this.getName();
            }
            mpList.add(curr);
        }

        // if all of the unit in the unit list can move within this fewest movement point
        for(int i = 0; i < move_units.size(); i++){
            Unit unit = move_units.get(i);
            unit.move(target, mpList.get(i));
            deleteUnit(unit);
        }
        return "True";
    }

    @Override
    public String toString() {
        String unitName = "";
        for(Unit unit: units){
            unitName += "\n   -- "+ unit.toString();
        }
        return "Owner : "+faction.getName()+"\nWealth of province : "+this.wealthOfProvince+"\nTax of province : "+this.tax+"% \nUnits : "+ unitName;
    }
}
