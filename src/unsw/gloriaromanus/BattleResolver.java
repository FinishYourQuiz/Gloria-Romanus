package unsw.gloriaromanus;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONObject;

public class BattleResolver implements Serializable{
    private Province defend;
    private Province invade;

    public boolean isConnected(String province1, String province2) throws IOException {
        if(province1 == null || province2 == null) {
            return false;
        }
        String content = Files.readString(Paths.get("src/unsw/gloriaromanus/province_adjacency_matrix_fully_connected.json"));
        JSONObject provinceAdjacencyMatrix = new JSONObject(content);
        return provinceAdjacencyMatrix.getJSONObject(province1).getBoolean(province2);
    }

    public BattleResolver() {
        this.defend = null;
        this.invade = null;
    }

    public void setSides(Province defend, Province invade) throws IOException {
        if(!isConnected(defend.getName(), invade.getName())){
            System.out.println("They are not connected!!\n");
            return;
        }
        this.defend = defend;
        this.invade = invade;
    }

    public Province getDefend() {
        return defend;
    }

    public Province getInvade() {
        return invade;
    }

    private boolean rangeAndmelee(Unit unit1, Unit unit2){
        if(unit1.isMelee() && unit2.isRanged())  return true;
        if(unit2.isMelee() && unit1.isRanged())  return true;
        return false;
    }

    /**
     *  // If a battle lasts longer than 200 engagements, the outcome should be a draw. 
     * 
     * @return victory province
     */
    
    public ArrayList<String> invade(){

        ArrayList<String> engagementResult = new ArrayList<String>();
        if( invade == null || defend == null){
            System.out.println("Can't invade!");
            engagementResult.add("Can't invade!");
            return engagementResult;

        } 
        int count = 0;
        ArrayList<Unit> defendUnitList = defend.getAvailableUnits();
        ArrayList<Unit> invadeUnitList = invade.getAvailableUnits();
        if(invadeUnitList.size() == 0) {
            engagementResult.add("Current province has no avaliable units!");
            return engagementResult;
        }
        // a unit is randomly chosen from each of the armies (uniformly random)
        Random r = new Random();
        while (count <= 200 && defendUnitList.size() > 0 && invadeUnitList.size()> 0){
            int defendChoiceIndex = r.nextInt(defendUnitList.size());
            int invadeChoiceIndex = r.nextInt(invadeUnitList.size());
            Unit defendChoice = defendUnitList.get(defendChoiceIndex);
            Unit invadeChoice = invadeUnitList.get(invadeChoiceIndex);
            // set the opponents
            defendChoice.setOppoent(invadeChoice);
            invadeChoice.setOppoent(defendChoice);
            int[] result;
            if(invadeChoice.getNumTroops() <= 0 || defendChoice.getNumTroops() <= 0)  break;
            // System.out.println("\n--  Current Unit of defendChoice: "+defendChoice.toString());
            // System.out.println("    Current Unit of invadeChoice: "+invadeChoice.toString());
            // 2 melee units engagement, there is a 100% chance of a melee engagement
            if(defendChoice.isMelee() && invadeChoice.isMelee()) {
                result = meleeEngagement(defendChoice, invadeChoice);
                engagementResult = addMeleeResult(result[0], engagementResult);
                
            // 2 ranged unit engagement
            } else if (defendChoice.isRanged() && invadeChoice.isRanged()) {
                result = rangedEngagement(defendChoice,invadeChoice);
                engagementResult= addRangedResult(result[0], engagementResult);
            // 1 ranged unit and 1 melee unit
            } else if (rangeAndmelee(defendChoice, invadeChoice)){
                // chance of engagement to be a melee engagement 
                // (where the engagement has both a melee and missile unit) 
                // is increased by 10% x (speed of melee unit - speed of missile unit)
                double meleeEngagementChance = 0.5 + 0.1*(defendChoice.getSpeed() - invadeChoice.getSpeed());
                // the maximum chance for an engagement to be either a ranged or melee engagement is 95% in either case
                if (meleeEngagementChance > 0.95) {
                    meleeEngagementChance = 0.95;
                } else if (meleeEngagementChance < 0.05) {
                    meleeEngagementChance = 0.05;
                }
                if (randomChooser(meleeEngagementChance)) {
                    System.out.println("->  meleeEngagement ");
                    result = meleeEngagement(defendChoice, invadeChoice);
                    engagementResult = addMeleeResult(result[0], engagementResult);
                } else {
                    System.out.println("->  rangeEngagement ");
                    result = rangedEngagement(defendChoice, invadeChoice);
                    engagementResult = addRangedResult(result[0], engagementResult);
                }  
            } else {
                // denfender is melee & invader is ranged
                double meleeEngagementChance = 0.5 + 0.1*(invadeChoice.getSpeed() - defendChoice.getSpeed());
                if (meleeEngagementChance > 0.95) {
                    meleeEngagementChance = 0.95;
                } else if (meleeEngagementChance < 0.05) {
                    meleeEngagementChance = 0.05;
                }
                if (randomChooser(meleeEngagementChance)) {
                    result = meleeEngagement(defendChoice, invadeChoice);
                    engagementResult = addMeleeResult(result[0], engagementResult);
                } else {
                    result = rangedEngagement(defendChoice, invadeChoice);
                    engagementResult = addRangedResult(result[0], engagementResult);
                }  
            }
            if(result[0] == 2 || result[0]==3){
                engagementResult.add(">>> Draw");
                return engagementResult; //return null
            } else if (result[0] == 0) {
                // if the defender wins
                (invade.getUnits()).remove(invadeChoice);
            } else if (result[0] == 1) {
                // if the invader wins
                (defend.getUnits()).remove(defendChoice);
            }
            // draw is result[0] == 2, do nothing
            count += result[1];

            defendChoice.clearOpponent();
            invadeChoice.clearOpponent();
        }  


        // all engagement ends, procceed on battle results
        if (count >= 200) {
            System.out.println("Ahh, the battle is over 200 turn!");
            Province winner =  r.nextInt(2) == 1 ? invade : defend;
            Province loser = winner == invade? defend : invade;
            if(winner.getName().equals(invade.getName())) {
                cleanResult(winner, loser);
                engagementResult.add(">>> The invader wins!");
                return engagementResult;
            }
            engagementResult.add(">>> The defender wins!");
            return engagementResult;
        } else if(defend.getUnits().size()== 0 && invade.getUnits().size()== 0){
            engagementResult.add(">>> Both defeated!");
            return engagementResult;
        } else if (defend.getUnits().size()== 0) {
            cleanResult(invade, defend);
            engagementResult.add(">>> The invader wins!");
            return engagementResult;
            //return invade;
        } else if (invade.getUnits().size()== 0){
            engagementResult.add(">>> The defender wins!");
            return engagementResult;
            //return defend;
        } else {
            engagementResult.add(">>> Draw!");
            return engagementResult; 
        }
        
    }

    public void cleanResult(Province winner, Province loser){
        System.out.println(winner);
        System.out.println(loser);
        // unlink the connect
        loser.emptyProvince();
        loser.setFaction(winner.getFaction());
        loser.setUnits(winner.getUnits());
        ArrayList<Unit> l = winner.getUnits();
        winner.emptyProvince(); // move to new province
        loser.setUnits(l);
        loser.getFaction().addProvince(loser);
    }

    public ArrayList<String> addMeleeResult(int result, ArrayList<String> list) {
        if (result == 0) {
            // the defender wins
            list.add("MELEE Engagement: the defender unit wins");
        } else if (result == 1) {
            // the defender wins
            list.add("MELEE Engagement: the invader unit wins");
        } else if (result == 2) {
            list.add("MELEE Engagement: both side breaks");
        } else {
            list.add("MELEE Engagement: sucessfully routes");
        }
        System.out.println("[MELEE]-added engagement result!" + result);
        return list;
    }

    public ArrayList<String> addRangedResult(int result, ArrayList<String> list) {
        if (result == 0) {
            // the defender wins
            list.add("RANGED Engagement: the defender unit wins");
        } else if (result == 1) {
            // the defender wins
            list.add("RANGED Engagement: the invader unit wins");
        } else if (result == 2) {
            list.add("RANGED Engagement: both side breaks");
        } else {
            list.add("RANGED Engagement: sucessfully routes");
        }
    
        System.out.println("[RANGED]-added engagement result!" + result);
        return list;
    }


    // The resolver for a  melee engagement
    public int[] meleeEngagement(Unit defend, Unit invade) {
        int[] result = new int[2];
        int count = 1;
        
        boolean invadeBreakingResult = false;
        boolean defendBreakingResult = false;
        boolean invadeRoutingResult = false;
        boolean defendRoutingResult = false;

        // BEGIN engagement
        while(invade.getNumTroops() > 0 && defend.getNumTroops() > 0 && count < 200) {
            if(invadeBreakingResult && defendBreakingResult){
                break;
            }
            invadeBreakingResult = false;
            defendBreakingResult = false;
            double n = new Random().nextGaussian();
            
            int defendDamage = (int)(defend.getNumTroops()* 0.1 * defend.getAttack()/(defend.getArmour() + defend.getShieldDefense() + defend.getDefenseSkill()) * (n+1));
            int invadeDamage = (int)(invade.getNumTroops()* 0.1 * invade.getAttack()/(invade.getArmour() + invade.getShieldDefense() + invade.getDefenseSkill()) * (n+1));

            defendDamage = defendDamage <= 0 ? 0 : defendDamage;
            invadeDamage = invadeDamage <= 0 ? 0 : invadeDamage;

            // System.out.println("melee defendDamage: "+defendDamage);
            // System.out.println("melee invadeDamage: "+invadeDamage);

            // Update the troops in the engagement
            defend.removeDead(invadeDamage);
            invade.removeDead(defendDamage);

            // System.out.printf("invadeNumTroops: %d\n",invade.getNumTroops());
            // System.out.printf("defendNumTroops: %d\n",defend.getNumTroops());
           
            if(invade.getNumTroops() <= 0 || defend.getNumTroops() <= 0)  break;

            // BREAK AND ROUTE
            
            if(defendDamage > 0 && invadeDamage > 0) { 
                // attemp to break
                invadeBreakingResult = breaking(invade, (double)((double)(defendDamage/invade.getNumTroops())/(double)(invadeDamage/defend.getNumTroops()))); 
                // if invade unit successfully breaks, try routing
                if (invadeBreakingResult) {
                    int res = 0;
                    invadeRoutingResult = routing(invade.getSpeed() - defend.getSpeed());
                    if(invadeRoutingResult) { 
                        System.out.println("Success Break\n");
                        res = 1;
                        result[0] = 2;
                        System.out.println("Success in route");
                        return result; 
                    }
                    System.out.printf("res is: %d\n", res);
                }
                // attempt to break
                defendBreakingResult = breaking(defend, (double)((double)(invadeDamage/defend.getNumTroops())/(double)(defendDamage/invade.getNumTroops())));
                // if defend unit successfully breaks, try routing
                if (defendBreakingResult) {
                    defendRoutingResult = routing(defend.getSpeed() - invade.getSpeed());
                    if(defendRoutingResult) {
                        System.out.println("Success in route");
                        result[0] = 3;
                        return result; 
                    }
                }
            // only defend unit got damaged
            } else if(invadeDamage > 0) { 
                defendBreakingResult = breaking(defend, 0.0);
                if (defendBreakingResult) {
                    defendRoutingResult = routing(defend.getSpeed() - invade.getSpeed());
                    if(defendRoutingResult) {
                        System.out.println("Success in route");
                        result[0] = 3;
                        return result; 
                    }
                }
            // only invade unit got damaged
            } else {
                invadeBreakingResult = breaking(invade, 0.0);
                if (invadeBreakingResult) {
                    invadeRoutingResult = routing(invade.getSpeed() - defend.getSpeed());
                    if(invadeRoutingResult) { 
                        result[0] = 3;
                        System.out.println("Success in route");
                        return result; 
                    }
                }
            }
            count ++;
        }
        // result[0] is result of skirmish, 0 is defender win, 1 is invader win
        if (defend.getNumTroops() <= 0) {
            result[0] = 1;
        } else if (invade.getNumTroops() <= 0){
            result[0] = 0;
        } else {
            result[0] = 2; // draw
        }
        // result[1] is the count of engagements in the skirmish
        result[1] = count;
        return result;
    }

    // The resolver for a range engagement
    public int[] rangedEngagement(Unit defend, Unit invade) {
        int[] result = new int[2];
        int count = 1;
        
        boolean invadeBreakingResult = false;
        boolean defendBreakingResult = false;
        boolean invadeRoutingResult = false;
        boolean defendRoutingResult = false;
        
        while(invade.getNumTroops() >  0 && defend.getNumTroops() > 0 && count < 200) {
            if(invadeBreakingResult && defendBreakingResult){
                break;
            }
            double n = new Random().nextGaussian();
            int defendDamage = (int)( (defend.getNumTroops()*0.1) * (defend.getAttack()/ (defend.getRangedArmour() + defend.getShieldDefense()) )*(n+1));
            int invadeDamage = (int)( (invade.getNumTroops()*0.1) * (invade.getAttack()/ (invade.getRangedArmour() + invade.getShieldDefense()) )*(n+1));
            defendDamage = defendDamage <= 0 ? 0 : defendDamage;
            invadeDamage = invadeDamage <= 0 ? 0 : invadeDamage;

            // System.out.printf("Defend %d %d %d %d %f \n",defend.getNumTroops(), defend.getAttack(),invade.getArmour(), invade.getShieldDefense(), (n+1));
            // System.out.printf("Invade %d %d %d %d %f \n",invade.getNumTroops(), invade.getAttack(),invade.getArmour(), invade.getShieldDefense(), (n+1));
            // System.out.println("defendDamage: "+defendDamage);
            // System.out.println("invadeDamage: "+invadeDamage);

            // System.out.printf("invade.getNumTroops(): %d\n",invade.getNumTroops());
            // System.out.printf("defend.getNumTroops(): %d\n",defend.getNumTroops());
            invadeBreakingResult = false;
            defendBreakingResult = false;

            if(invade.isMelee()) {
                // Melee units cannot inflict damage in a ranged engagement
                invade.removeDead(defendDamage);
                if(invade.getNumTroops() <= 0 || defend.getNumTroops() <= 0)  break;
                if(defendDamage > 0) { 
                    invadeBreakingResult = breaking(invade, 0); // Zero Division 
                    if (invadeBreakingResult) {
                        invadeRoutingResult = routing(invade.getSpeed() - defend.getSpeed());
                        if(invadeRoutingResult) { 
                            result[0] = 2;
                            return result; 
                        }
                    }
                }
            } else if (defend.isMelee()){
                defend.removeDead(invadeDamage);
                if(invade.getNumTroops() <= 0 || defend.getNumTroops() <= 0)  break;
                if(invadeDamage > 0) { 
                    defendBreakingResult = breaking(defend, 0); //  Zero Division 
                    // if sucessfully breaks, calculate routing
                    if (defendBreakingResult) {
                        defendRoutingResult = routing(defend.getSpeed() - invade.getSpeed());
                        if(defendRoutingResult) {
                            result[0] = 3;
                            return result;
                        }
                    }
                }
            } else {
                // (size of enemy unit at start of engagement x 10%) x 
                // (Missile attack damage of unit/(effective armor of enemy unit + effective shield of enemy unit)) x (N+1)
                defend.removeDead(invadeDamage);
                invade.removeDead(defendDamage);

                if(invade.getNumTroops() <= 0 || defend.getNumTroops() <= 0)  break;

                // BREAK AND ROUTE

                if(defendDamage > 0 && invadeDamage > 0) { 
                    // (casualties suffered by the unit during the engagement/
                    // number of troops in the unit at the start of the engagement)/
                    // (casualties suffered by the opposing unit during the engagement/
                    // number of troops in the opposing unit at the start of the engagement)
                    // attemp to break
                    invadeBreakingResult = breaking(invade, (double)(defendDamage/invade.getNumTroops())/(invadeDamage/defend.getNumTroops())); 
                    // if invade unit successfully breaks, try routing
                    if (invadeBreakingResult) {
                        invadeRoutingResult = routing(invade.getSpeed() - defend.getSpeed());
                        if(invadeRoutingResult) { 
                            System.out.println("Success in route");
                            result[0] = 3;
                            return result; 
                        }
                    }
                    // attempt to break
                    defendBreakingResult = breaking(defend, (double)(invadeDamage/defend.getNumTroops())/(defendDamage/invade.getNumTroops()));
                    // if defend unit successfully breaks, try routing
                    if (defendBreakingResult) {
                        defendRoutingResult = routing(defend.getSpeed() - invade.getSpeed());
                        if(defendRoutingResult) {
                            result[0] = 3;
                            System.out.println("Success in route");
                            return result; 
                        }
                    }
                } 
                else if(invadeDamage > 0) { 
                    defendBreakingResult = breaking(defend, 0);
                    // if defend unit successfully breaks, try routing
                    if (defendBreakingResult) {
                        defendRoutingResult = routing(defend.getSpeed() - invade.getSpeed());
                        if(defendRoutingResult) {
                            System.out.println("Success in route");
                            result[0] = 3;
                            return result; 
                        }
                    }
                } 
                else {
                    invadeBreakingResult = breaking(invade, 0); 
                    // if invade unit successfully breaks, try routing
                    if (invadeBreakingResult) {
                        invadeRoutingResult = routing(invade.getSpeed() - defend.getSpeed());
                        if(invadeRoutingResult) { 
                            System.out.println("Success in route");
                            result[0] = 3;
                            return result; 
                        }
                    }   
                }
            }
            count ++;
        }
        // result[0] is result of skirmish, 0 is defender win, 1 is invader win 
        if (defend.getNumTroops() <= 0) {
            result[0] = 1;
        } else if (invade.getNumTroops() <= 0){
            result[0] = 0;
        } else {
            result[0] = 2; // draw
        }
        // result[1] is the count of engagements in the skirmish
        result[1] = count;
        return result;
    }

    public boolean randomChooser(double chance) {
        return new Random().nextInt(100) < Math.round(chance/0.01);
    }

    public boolean breaking(Unit unit, double increase) {
        double chance = 1 - (unit.getMorale()*0.1);

        // (casualties suffered by the unit during the engagement/
        // number of troops in the unit at the start of the engagement)/
        // (casualties suffered by the opposing unit during the engagement/
        // number of troops in the opposing unit at the start of the engagement) x 10%
        // 0.1 * (a/b)/(c/d)
        chance += 0.1*increase;
        return randomChooser(chance);
    }

    public boolean routing(int speedDiff) {
        // chance of routing successfully = 50% + 10% x (speed of routing unit - speed of pursuing unit)
        double chance  = 0.5 + (0.1 * speedDiff);
        // The minimum chance a unit can have to successfully route is 10%, 
        // and the maximum chance it can have is 100%.
        if (chance <= 0.1) {
            chance = 0.1;
        } else if (chance > 1) {
            chance = 1;
        }

        boolean result = randomChooser(chance);
        return result;
    }
    
}
