package unsw.gloriaromanus;

import java.io.Serializable;
import java.util.ArrayList;

public class Goal implements Serializable{
    private String name;
    private ArrayList<Goal> subGoals;

    public Goal(String name){
        this.name = name;
        this.subGoals = new ArrayList<>();
    }

    public void addSubGoal(Goal sub){
        this.subGoals.add(sub);
    }

    public boolean achieveAll(Faction faction){
        if(isAchieve(faction)){
            if(subGoals.size() <=0){
                return true;
            }
            for(Goal subGoal: subGoals){
                if(subGoal.achieveAll(faction) && name=="OR"){
                    return true;
                }else if(!subGoal.achieveAll(faction) &&  name=="AND"){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean checkConquest(Faction faction){
        if(faction.getProvinces().size() >= 1){
            return true;
        }
        return false;
    }

    public boolean checkTreasure(Faction faction){
        // if(faction.getTreasure() >= 100000){
        if(faction.getTreasure() >= 2000){

            return true;
        }
        return false;
    }

    public boolean checkWealth(Faction faction){
        if(faction.getWealth() >= 2500){
            return true;
        }
        return false;
    }

    public boolean isAchieve(Faction f){
        switch(name){
            case "CONQUEST":
                return checkConquest(f);
            case "TREASURY":
                return checkTreasure(f);
            case "WEALTH":
                return checkWealth(f);
        }
        return true; // root as AND/OR
    }

    @Override
    public String toString() {
        switch(name){
            case "AND":
                return  subGoals.get(0).toString()+
                        "\n    AND \n"+subGoals.get(1).toString()+"";
            case "OR":
                return  "< "+subGoals.get(0).toString()+
                        " >\n                  OR  \n< "
                +subGoals.get(1).toString()+" >";
            case "CONQUEST":
                return "Conquering all territories";
            case "WEALTH":
                return "Accumulating faction wealth of 400,000 gold";
            case "TREASURY":
                return "Accumulating a treasury balance of 100,000 gold";
        }
        return "";
    }

}
