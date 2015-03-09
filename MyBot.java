import java.util.*;

public class MyBot {
   
	static int turn_counter=1;
	static int turns_wait[]=new int[15];

    public static void DoTurn(PlanetWars pw) {
   
	//FIRST TURN CODE.
//finding the planets within a particular distance.
    	if(turn_counter==1)
    	{
	ArrayList<Planet> In_Range= new ArrayList<Planet>();
	for(Planet P:pw.NeutralPlanets())
	{
		if(pw.Distance(pw.MyPlanets().get(0).PlanetID(), P.PlanetID())<18.4&&P.NumShips()<50)
			In_Range.add(P);
	}
	
	//finding the planet with the highest growth rate and lowest number of ships within that distance
	//run it twice so u get two such planets
	Planet dest[] = new Planet[2];
	double sourceScore = Double.MIN_VALUE;
	int count=2;
	for(int i=0;i<count;i++)
	{
	for(Planet P:In_Range)
	{
		 double score =( (double)P.GrowthRate()*10)/P.NumShips();
		    if (score > sourceScore&&P!=dest[0]) {
			sourceScore = score;
			dest[i] = P;		
		    }
		
	}
	
	pw.IssueOrder(pw.MyPlanets().get(0), dest[i], dest[i].NumShips()+2);
	}
    	}
    	//NOT the first turn.
    	//start by conquering planets which are close to the two already conquered planets
    	else if(pw.MyPlanets().size()>1)
    	{
    		for(Planet P:pw.MyPlanets())
    		{
    			if(P!=pw.MyPlanets().get(0)&&P.GrowthRate()>=3)
    			{
    				ArrayList<Planet> In_Range= new ArrayList<Planet>();
    				int radius=10;
    				
    				while(In_Range.size()<=2)
    	    		{
    	    		for(Planet prey:pw.NeutralPlanets())
    	    		{
    	    			if(pw.Distance(prey.PlanetID(), P.PlanetID())<radius)
    	    				In_Range.add(prey);
    	    		}
    	    		radius+=4;
    	    		}
    				Planet dest= null;
    				
    				double sourceScore = Double.MIN_VALUE;
    	    		
    	    		
    	    		for(Planet InRange:In_Range)
    	    		{
    	    			 double score =( (double)InRange.GrowthRate()*10)/InRange.NumShips();
    	    			    if (score > sourceScore) {
    	    				sourceScore = score;
    	    				dest = InRange;		
    	    			    }
    	    			
    	    		}
    	    	
    				
    	    		if(dest.NumShips()<=P.NumShips()-6)
    	    		{
    	    		pw.IssueOrder(P, dest, dest.NumShips()+1);
    	    		}
    	    		
    	    		else
    	    		{
    	    			
        	    		ArrayList<Planet> InRange_MyPlanet= new ArrayList<Planet>();
        				radius=10;
        				
        				while(InRange_MyPlanet.size()<=2)
        	    		{
        	    		for(Planet MyPlanet:pw.MyPlanets())
        	    		{
        	    			if(pw.Distance(MyPlanet.PlanetID(), dest.PlanetID())<radius)
        	    				InRange_MyPlanet.add(MyPlanet);
        	    		}
        	    		radius+=4;
        	    		}
        				
        				while(dest.Owner()!=1&&dest.NumShips()<=10)
        				{
        					for(int i=0;i<InRange_MyPlanet.size();i++)
        					{
        					pw.IssueOrder(InRange_MyPlanet.get(i), dest, InRange_MyPlanet.get(0).GrowthRate());
        					}
        				}
    	    			
    	    				pw.IssueOrder(pw.MyPlanets().get(0), dest, dest.NumShips()+1);
    	    		}
    	    				
    			}
    			
    		}
    		
    		 //attack enemy planets from front planet strategy
        	//identify the planet the enemy is conquering
        	//find MyPlanet which is closest to it and has a moderate growth rate. send growth rate ships from other planets in the range(or outside the range) to this front planet
        	//fire ships from the front planet to the enemy planet right after it has conquered it
        	//if our planet is in trouble, wait for a while till it generates enough ships to survive the attack or call in reimbursements. hence the defense.
    	//if a planet has ships less than 6, send reinforcements.
    		
    		//doubt- how to make this front planet stay constant till we are done conquering?
    		ArrayList<Integer> attacked_id=new ArrayList<Integer>();
    		ArrayList<Planet> FrontPlanet=new ArrayList<Planet>();
    	
    	for(int i=0;i<pw.EnemyFleets().size();i++)
    	{
    		if(pw.EnemyFleets().get(i).DestinationPlanet()!=2)//irrespective of whether our planet is being attacked or a neutral one, reinforcements are sent.
    		{
    			attacked_id.add(pw.EnemyFleets().get(i).DestinationPlanet());//we need a front planet for every planet that is attacked.
    			
    			int min_dist=200;
    			for(Planet P:pw.MyPlanets())
    			{
    				int distance=pw.Distance(P.PlanetID(),attacked_id.get(i));
    				if(distance<min_dist){
    					min_dist=distance;
    					FrontPlanet.set(i,P);
    				}
    			}
    		}
    		turns_wait[i]=(pw.Distance(pw.EnemyFleets().get(i).SourcePlanet(), pw.EnemyFleets().get(i).DestinationPlanet())-pw.Distance(FrontPlanet.get(i).PlanetID(),pw.EnemyFleets().get(i).DestinationPlanet()));
    		
    				}
    	for(int j=0;j<attacked_id.size();j++)
    	{
    	    	
    	if(pw.EnemyFleets().get(j).TurnsRemaining()+1==turns_wait[j])
    	{
    		pw.IssueOrder(FrontPlanet.get(j).PlanetID(), attacked_id.get(j), pw.GetPlanet(attacked_id.get(j)).GrowthRate()+2);
    			for(int i=0;(i<pw.MyPlanets().size()-1);i++)
    			{
    				if(pw.GetPlanet(attacked_id.get(j)).Owner()!=1&&pw.GetPlanet(attacked_id.get(j)).NumShips()<10)
    	    		{
    				pw.IssueOrder(pw.MyPlanets().get(i), FrontPlanet.get(j), pw.MyPlanets().get(i).GrowthRate());
    				pw.IssueOrder(FrontPlanet.get(j), pw.GetPlanet(attacked_id.get(j)), FrontPlanet.get(j).GrowthRate());
    			}
    				else
    					break;
    			
    		}
    	}
    	}
    	
    	
    	}
   
	turn_counter++;
	
    }
   
    public static void main(String[] args) {
	String line = "";
	String message = "";
	int c;
	try {
	    while ((c = System.in.read()) >= 0) {
		switch (c) {
		case '\n':
		    if (line.equals("go")) {
			PlanetWars pw = new PlanetWars(message);
			DoTurn(pw);
		        pw.FinishTurn();
			message = "";
		    } else {
			message += line + "\n";
		    }
		    line = "";
		    break;
		default:
		    line += (char)c;
		    break;
		}
	    }
	} catch (Exception e) {
	    // Owned.
	}
    }
}

