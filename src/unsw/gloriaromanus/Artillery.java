package unsw.gloriaromanus;

public class Artillery extends Unit{

    /**
    protected int numTroops;      // the number of troops in this unit (should reduce based on depletion)
    protected int range;          // range of the unit
    protected int armour;         // armour defense
    protected int morale;         // resistance to fleeing
    protected int speed;          // ability to disengage from disadvantageous battle
    protected int attack;         // can be either missile or melee attack to simplify. Could improve implementation by differentiating!
    protected int defenseSkill;   // skill to defend in battle. Does not protect from arrows!
    protected int shieldDefense;  // a shield
     */
    public Artillery(String name, Province province, int num) {
        super(name, province, num);
        this.remain_points = 4;
        this.price = 30;
        this.range = 10;
        this.armour = 3;
        this.morale = 10;
        this.speed = 2;
        this.attack = 10;
        this.defenseSkill = 5;
        this.shieldDefense = 2;
    }
    
    // Template pattern: override methods for different types of units
    @Override
    public void endturn() {
        // Auto-generated method stub
        super.endturn();
        this.remain_points = 4;
    }

    @Override
    public boolean isRanged() {
        return true;
    }
}
