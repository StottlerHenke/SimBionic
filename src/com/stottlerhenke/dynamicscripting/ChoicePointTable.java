package com.stottlerhenke.dynamicscripting;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

/**
 * A HashMap wrapper that takes care of loading and saving.
 * 
 *
 */
public class ChoicePointTable
{
	public final String SEP = "\t";
	
	protected HashMap<String, ChoicePoint> choicePointMap = new HashMap<String, ChoicePoint>();
	
	public ChoicePointTable()
	{
		super();
	}
	
	/**
	 * Add a choice point with a set of initial values
	 * @param choicePoint
	 * @param values Double objects
	 */
	public void addChoicePoint(String choicePointName, ArrayList<DsAction> actions, int scriptSize, int minActionValue, int maxActionValue)
	{
		ChoicePoint c  = new ChoicePoint(choicePointName, actions, scriptSize, minActionValue, maxActionValue);
		choicePointMap.put(choicePointName, c);
	}
	
	public void addChoicePoint(ChoicePoint cp)
	{
		choicePointMap.put(cp.name, cp);
	}
	
	/**
	 * Get the actions for a particular choice point
	 * 
	 * @param choicePoint
	 * @return An array list of Doubles if found, null otherwise
	 */
	public ArrayList<DsAction> getChoicePointActions(String choicePoint)
	{
		return choicePointMap.get(choicePoint).getActions(0);
	}
	
	public ChoicePoint getChoicePoint(String choicePoint)
	{
		return choicePointMap.get(choicePoint);
	}
	
	/**
	 * Clear any selections that haven't been processed.
	 *
	 */
	public void clearSelections()
	{
		for(ChoicePoint c : choicePointMap.values())
		{
			c.clearSelections();
		}
	}
	
	/**
	 * Save the choice point to the specified file.
	 * 
	 * Name, <name>
	 * ScriptSize, <scriptSize>
	 * MIN, <min>
	 * MAX, <max>
	 * ActionName, Value, Priority, SBIndex
	 * <>, <>, <>, <>
	 * ...
	 */
	public void saveChoicePoint(String choicePoint, String filename) throws FileNotFoundException {
		
		ChoicePoint c = getChoicePoint(choicePoint);
		if(c == null)
			throw new RuntimeException("Can't find choice point: " + choicePoint);
		
		//Write out the header for the choicePoint
		PrintWriter pw = new PrintWriter(new File(filename));
        StringBuilder sb = new StringBuilder();
        sb.append("Name" + SEP + c.name + "\n"); 
        sb.append("ScriptSize" + SEP + c.scriptSize + "\n");
        sb.append("MIN " + SEP + c.MIN_VALUE + "\n"); 
        sb.append("MAX" + SEP + c.MAX_VALUE + "\n"); 

        //Copy and sort the actions
        ArrayList<DsAction> sortedActions = new ArrayList<>();
        sortedActions.addAll(c.getActions());
        sortedActions.sort(new Comparator<DsAction>() {
			public int compare(DsAction o1, DsAction o2) {
				return o1.sbIndex.compareTo(o2.sbIndex);
			}
        });
        
        //Write out the actions
        sb.append("ActionName" + SEP +"Value" + SEP + "Priority"  + SEP + "SBIndex\n");
        for(DsAction action : sortedActions) {
        	sb.append(action.name + SEP);
        	sb.append(action.value + SEP);
        	sb.append(action.priority + SEP);
        	sb.append(action.sbIndex + "\n");
        }

        pw.write(sb.toString());
        pw.close();
	}
	
	/**
	 * Load the choice point from the specified file
	 * @throws FileNotFoundException 
	 */
	public ChoicePoint loadChoicePoint(String filename) throws FileNotFoundException {
		
		Scanner scan = new Scanner(new File(filename));
		
		scan.next();
		String name = scan.next();
		
		scan.next();
		int scriptSize = scan.nextInt();
		
		scan.next();
		int min = scan.nextInt();
		
		scan.next();
		int max = scan.nextInt();
		
		scan.nextLine(); //Finish previous line
		scan.nextLine(); //Skip headers
		ArrayList<DsAction> actions = new ArrayList<>();
	    while (scan.hasNextLine()) {
	    	
	    	if(!scan.hasNext()) { //Escape for empty line
	    		break;
	    	}
	    	
			String actionName = scan.next();
			double value = scan.nextDouble();
			int priority = scan.nextInt();
			int sbIndex = scan.nextInt();
	    	
	    	DsAction action = new DsAction(actionName, value, priority, sbIndex);
	    	actions.add(action);
	    }
	      
	    scan.close();
	    
		return new ChoicePoint(name, actions, scriptSize, min, max);
	}
}
