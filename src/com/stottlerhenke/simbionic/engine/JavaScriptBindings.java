package com.stottlerhenke.simbionic.engine;

import java.util.List;
import java.util.Set;

import javax.script.Bindings;
import javax.script.SimpleBindings;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.engine.core.SB_Entity;
import com.stottlerhenke.simbionic.engine.core.SB_ExecutionFrame;
import com.stottlerhenke.simbionic.engine.parser.SB_Variable;

/**
 * Wrapper around the bindings object such that the {get(Object)
 * method defers to the variable manager to return the value of variables.
 */
public class JavaScriptBindings extends SimpleBindings {
	
	private static final String FILE_NAME_KEY = "javax.script.filename";

	private static final String ENTITY_ID = "_entityID";
	private static final String ENTITY_NAME = "_entityName";

	private SB_ExecutionFrame frame;
	private Set<String> reservedJSVariables;

	/**
	 * Wrapper around the bindings object such that the {@link #get(Object)}
	 * method defers to the variable manager to return the value of
	 * variables. Other methods in the {@link Bindings} interface are
	 * implemented by calling the method in the bindings object.
	 *
	 */
	public JavaScriptBindings(Bindings bindings, Set<String> reservedJSVariables ) {

		super();
		
		//copy the original bindings; don't change the original
		for(Entry<String, Object> entry : bindings.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
		
		this.reservedJSVariables = reservedJSVariables;
	}

	/**
	 * 'declares' the variable manager variable into the bindings object
	 * @throws SB_Exception 
	 */
	void setExecutionFrame(SB_ExecutionFrame frame) throws SB_Exception {
		
		if(frame == null)
			return;
		
		this.frame = frame;
		
		// push entity id and name into the bindings.
		SB_Entity entity = frame.GetEntity();
		put(ENTITY_ID, entity.GetId()._id);
		put(ENTITY_NAME, entity.GetName());

		// put local and global variables into the bindings
		// if variables are not pushed into the initial bindings, 'get' is not called at all
		List<String> vars = frame.GetVariableNames();
		List<String> globalVars = frame.GetEntity().GetState().GetGlobalNames();
		
		if (vars == null && globalVars == null) {
			return;
		}

		for (String varName : vars) {
			this.remove(varName);
			this.put(varName, null);
		}

		for (String varName : globalVars) {
			this.remove(varName);
			this.put(varName, null);
		}
	}

	/**
	 * if the key corresponds to a TG variable then the value is retrieved
	 * from the variable manager and added to the {@link #initialBindings}.
	 *
	 * @param key
	 */
	@Override
	public Object get(Object key) {
		
		/*try {
			System.out.println("Called get for: " + key + " " + frame.GetVariable(key.toString()).getValue() + ":" + super.get(key));
		} catch (SB_Exception e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}*/
		
		if (key instanceof String && frame != null) {
			
			String varName = (String) key;

			if (varName.equals(FILE_NAME_KEY) && !containsKey(key)) {
				// TODO: don't know why it's trying to get the value of FILE_NAME_KEY, but ignore this for now, until we have time to fix.
				//System.err.println(FILE_NAME_KEY + " is missing");
				return null;
			}

			try {
				SB_Variable var = frame.GetVariable(varName);
				if (var != null) {
					Object value = var.getValue();
					this.put(varName, value);
					return value;
				}
			} catch (SB_Exception e) {
				// TODO: syl - what do we do here?
				// Unfortunately we cannot throw the exception here as part of the 'get' implementation.
				if (!containsKey(key) && !isJSReservedVariable(key)) {
					System.err.println("Variable Initialization Error: " + e.getMessage());
				}
			}

		}

		return super.get(key);
	}

	protected boolean isJSReservedVariable(Object variableName) {
		return (variableName instanceof String) && reservedJSVariables.contains(variableName.toString());
	};

	public String toString() {
		StringBuffer buf = new StringBuffer();

		for(Entry<String, Object> entry : entrySet()) {
			buf.append("\t" + entry.getKey() + " ");
			Object value = entry.getValue();
			if(value != null) {
				String str = value.toString();
				//if(str.length() > 20) 
				//	str = str.substring(0, 20);
				buf.append(str);
			}
			else
				buf.append("null");
			
			buf.append("\r\n");
		}
		
		return buf.toString();
	}
}
