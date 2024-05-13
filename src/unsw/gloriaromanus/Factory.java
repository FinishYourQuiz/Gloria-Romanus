package unsw.gloriaromanus;

public class Factory {
    public Unit getUnitType(String type,Province province){
        if(type.equalsIgnoreCase("horse archers")){
           return new Artillery(type,province, 20);
        } else if(type.equalsIgnoreCase("chariotss")){
           return new Cavalry(type, province, 20);
        } else if(type.equalsIgnoreCase("melee infantry")){
           return new Infantry(type, province, 20);
        }
        return null;
     }
}
