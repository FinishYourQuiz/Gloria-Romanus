package unsw.gloriaromanus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Faction implements Subject, Serializable{
    ArrayList<Observer> listObservers;
    private int wealth;      // the total wealth the player has
    private ArrayList<Province> provinces;
    private int treasure;
    private String faction;

    // constructor
    public Faction(String faction){
        this.listObservers = new ArrayList<Observer>();
        this.wealth = 0;
        this.treasure = 100;    // start-up finance
        this.provinces = new ArrayList<>();
        this.faction = faction;
    }

    @Override
	public void attach(Observer o) {
		if(! listObservers.contains(o)) { 
            listObservers.add(o); 
        }
	}

	@Override
	public void detach(Observer o) {
		listObservers.remove(o);
    }
    
    @Override
	public void notifyObservers() {
		for( Observer obs : listObservers) {
			obs.update(this);
		}
    }

    

    public ArrayList<Province> getProvinces() {
        return provinces;
    }

    public List<String>getProvincesName(){
        List<String> res = new ArrayList<>();
        for(Province province: provinces){
            res.add(province.getName());
        }
        return res;
    }

    public void setProvinces(ArrayList<Province> provinces) {
        this.provinces = provinces;
        for(Province p: provinces){
            p.setFaction(this);
        }
        notifyObservers();
    }

    public int getTreasure() {
        return treasure;
    }

    public String getName() {
        return faction;
    }

    public int getWealth() {
        this.wealth = 0;
        for(Province province: provinces){
            this.wealth += province.getWealthOfProvince();
        }
        return wealth;
    }

    public void endTurn(){
        this.wealth = 0;
        for(Province province: provinces){
            province.endturn();
            // get revenue
            this.treasure += province.getRevenue();
        }
        notifyObservers();
    }

    // Spend treasure on recruiting the units
    public void pay(int amount){
        this.treasure -= amount;
        notifyObservers();
    }
    
    // Delete the province 
    public void removeProvince(Province p){
        provinces.remove(p);
        notifyObservers();
    }

    public boolean isOwned(String province_name){
        for(Province province: provinces){
            if(province.getName().equals(province_name))    return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String name = "Name of the Faction: " + getName() + "\n";
        String treasure = "The gold remained: "+ getTreasure() + "\n";
        String wealths = "The wealth of provinces: "+ getWealth() + "\n";
        String provinces = "The provinces the faction owned: \n";
        for(Province p: this.provinces){
            provinces += "-- "+p.toString();
        }
        return name+treasure+wealths+provinces;
    }

    public void addProvince(Province province){
        if(province != null)
            this.provinces.add(province);
            notifyObservers();
    }

}
