package unsw.gloriaromanus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Represents a basic unit of soldiers
 * 
 * incomplete - should have heavy infantry, skirmishers, spearmen, lancers,
 * heavy cavalry, elephants, chariots, archers, slingers, horse-archers,
 * onagers, ballista, etc... higher classes include ranged infantry, cavalry,
 * infantry, artillery
 * 
 * current version represents a heavy infantry unit (almost no range, decent
 * armour and morale)
 */
public class Unit implements Serializable{
	private Province province;
    protected int numTroops;      // the number of troops in this unit (should reduce based on depletion)
    protected int range;          // range of the unit
    protected int armour;         // armour defense
    protected int morale;         // resistance to fleeing
    protected int speed;          // ability to disengage from disadvantageous battle
    protected int attack;         // can be either missile or melee attack to simplify. Could improve implementation by differentiating!
    protected int defenseSkill;   // skill to defend in battle. Does not protect from arrows!
	protected int shieldDefense;  // a shield
	
	private boolean used;	// check this unit is used for the invasion in this turn
	private boolean isRanged;
	private boolean isMelee;
	
	protected int price;
	protected String name;
	protected int remain_points;	// remaining points in current turn
    private int turn_remain;		// 0 means available to move and fight
	private Unit opponent;
	private double moraleDamage;
	private boolean heroicCharge;
	private int numEngagement;

	private State state;

    public Unit(String name, Province province, int num){
		this.province = province;
        this.numTroops = num;	
		this.turn_remain = 1;
		this.name = name;
		
		this.remain_points = 4;
		this.heroicCharge = false;

		this.used = false;
		this.isMelee = false;
		this.isRanged = false;

		this.moraleDamage = 0;
		this.opponent = null;
		this.numEngagement = 0;

		// check melee unit
		if(name.startsWith("melee")){
			this.isMelee = true;
		}

		// check range unit
		// artillery, horse archers, missile infantry
		if(name.contains("artillery") || name.contains("horse")
			|| name.contains("missile")){
			this.isRanged = true;
		}

		// to be changed
		this.range = 0;
        this.armour = 0;
        this.morale = 0;
        this.speed = 0;
        this.attack = 0;
        this.defenseSkill = 0;
        this.shieldDefense = 0;
	}

	public void setState(State s){
        state = s;
        state.handleState();
    }

	public boolean isRoman(){
		String n = province.getFaction().getName();
		if(n.contains("Romans"))	return true;
		return false;
	}

	public void setOppoent(Unit unit){
		this.opponent = unit;
	}

	public void endEngagement(){
		this.moraleDamage = 0;
		this.opponent = null;
		this.numEngagement = 0;
	}

	public void clearOpponent(){
		opponent = null;
		endEngagement();
	}

    public int getNumTroops(){
        return numTroops;
	}
	
	public boolean isRanged(){
		return isRanged;
	}

	public boolean isMelee(){
		return isMelee;
	}

	public int getRange() {
		return range;
	}

	public int getArmour() {
		return armour;
	}

	public int getMorale() {
		double res = (double)morale;
		// roman eagle
		if(isRoman())	{
			//System.out.println("Yes roman!");
			res += 1-moraleDamage;
		}
		// heroic charge
		if(this.heroicCharge){
			res = res*1.5;
		}
		return (int)res;
	}

	public int getRangedArmour(){
		return (int)(this.getArmour()*0.5);
	}

	public int getSpeed() {
		return speed;
	}

	public int getAttack() {
		this.numEngagement ++;
		int res = attack;
		if(this.heroicCharge){
			res *= 2;
		}
		if(isType("missile") && opponent.isType("horse")){
			return (int)(res*0.5);
		}
		if(isType("melee infantry") && numEngagement%4==0){
			return (int)(res+defenseSkill);
		}
		return res;
	}

	public int getDefenseSkill() {
		return defenseSkill;
	}

	public int getShieldDefense() {
		return shieldDefense;
	}

	public String getName() {
		return name;
	}

	// the soldier is killed in the battle
	public void removeDead(int num){
		if(isType("elephant")){
			boolean effective = new Random().nextInt(10) < 1 ? false : true;
			if(!effective){
				opponent.removeDead(num);
				return;
			}
		}
		this.numTroops -= num;
		if(isRoman()){
			moraleDamage = moraleDamage+0.2*num > 1 ? 1: moraleDamage+0.2*num;
		}
		if(this.numTroops <= 0){
			this.province.deleteUnit(this);
		}
	}
	
	public void endturn(){
		this.remain_points = 10;
		this.turn_remain = turn_remain > 0 ? turn_remain - 1 : 0;
		this.used = false;
	}

	public int getRemain_points() {
		return remain_points;
	}

	public int getPrice(){
		return price;
	}

	public String getProvinceName(){
		return province.getName();
	}

	public boolean isType(String type){
		return name.contains(type);
	}

	public void setHeroicCharge(){
		if(isType("melee cavalry")){
			this.heroicCharge = true;
		}
	}

	public boolean isAvailable(){
		return this.turn_remain == 0 && this.used == false;
	}

	// move unit 
	public void move(Province target, int points){
		this.province.deleteUnit(this);
		this.province = target;
		this.remain_points -= points;
		target.addUnit(this);
	}

	/**
	 * attampt to move the unit 
	 * Deijkstra Algorithm
	 * @param target
	 * @return false for not available within movepoint to mvoe to the target, true otherwise
	 */
	public int attamptMove(ArrayList<ArrayList<Integer>> matrix, int start, int dest){
		boolean []isVisted = new boolean[matrix.size()];
        int []d = new int[matrix.size()];
        for (int i = 0;i < matrix.size();i++){
            isVisted[i] = false; 
            d[i] = 1000;		 // the shortest path from point i
        }
        isVisted[start] = true;
        d[start] = 0;
        int unVisitNode = matrix.size() ;
        int index = start;
        while (unVisitNode > 0 && !isVisted[dest]){
            int min = 1000;
			// find out the shortest path
            for (int i = 0;i< d.length;i++){
                if (min > d[i] && !isVisted[i]){
                    index = i;
                    min = d[i];
                }
            }
            
			for (int i = 0;i<matrix.size();i++){
				// update the path
				if (d[index] + matrix.get(index).get(i) < d[i]) 
					d[i] = d[index] + matrix.get(index).get(i);
			}

			unVisitNode -- ;
			isVisted[index] = true;
		}
		return d[dest];
	}


	@Override
	public String toString() {
		return "Name : "+name+", Number of troops : "+numTroops+", Available now : "+isAvailable();
	}


}
