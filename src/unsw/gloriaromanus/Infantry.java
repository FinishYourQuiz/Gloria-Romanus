package unsw.gloriaromanus;

public class Infantry extends Unit{

    public Infantry(String name, Province province, int num){
        super(name, province, num);
        this.remain_points = 10;
        this.price = 15;
        this.range = 3;
        this.armour = 3;
        this.morale = 3;
        this.speed = 3;
        this.attack = 8;
        this.defenseSkill = 5;
        this.shieldDefense = 3;
    }
    
    // Template pattern: override methods for different types of units
    @Override
    public void endturn() {
        super.endturn();
        this.remain_points = 10;

    }

    @Override
    public boolean isRanged() {
        if(this.name.contains("missle")){
            return true;
        } else {
            return false;
        }
    }
}
