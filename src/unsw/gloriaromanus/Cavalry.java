package unsw.gloriaromanus;

public class Cavalry extends Unit{

    public Cavalry(String name, Province province, int num) {
        super(name, province, num);
        this.remain_points = 15;
        this.price = 20;
        this.range = 7;
        this.armour = 7;
        this.morale = 8;
        this.speed = 10;
        this.attack = 7;
        this.defenseSkill = 10;
        this.shieldDefense = 10;
    }
    
    // Template pattern: override methods for different types of units
    @Override
    public void endturn() {
        super.endturn();
        this.remain_points = 10;
    }

    @Override
    public boolean isRanged() {
        if(this.name.contains("horse")){
            return true;
        } else {
            return false;
        }
    }
    
}
