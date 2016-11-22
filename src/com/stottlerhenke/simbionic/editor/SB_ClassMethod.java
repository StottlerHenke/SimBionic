package com.stottlerhenke.simbionic.editor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Icon;


public class SB_ClassMethod extends UserObject implements Comparable 
{
    private static final long serialVersionUID = 2302585093L + 15;

	private Icon _icon;
	
	transient private boolean inUse;
	
	/**
	 * For overload methods (methods that have the same method name but different
	 * argument types and/or names), this is the index of this method among the
	 * overload methods. The index is zero-based.
	 */
	transient protected int overloadIndex = -1;
	
	public SB_ClassMethod() {
		super();
		params = new Vector();
	}

	public SB_ClassMethod(String name) {
		super(name);
		params = new Vector();
	}
	
	public boolean isInUse(){
		return inUse;
	}
	
	public void setInUse(boolean inUse){
		this.inUse = inUse;
	}

	public Icon getIcon() {
        if (_icon == null)
            _icon = Util.getImageIcon("Method.gif");
        return _icon;
	}
	
	public String getToolTipText(){
		if (comment == null)
			return null;
		else if (comment.trim().length() <= 0)
			return null;
		else{
			StringBuffer sb = new StringBuffer();
			sb.append("<html><table width=\"390\"><tr><td>");
			sb.append(comment);
			sb.append("</td></tr></table></html>");
			return sb.toString();
		}
	}
	
	public String toString(){
		return (getName() + " : " + returnType);
	}
	
	public int compareTo(Object arg0) {
		return getName().compareTo(((SB_ClassMethod)arg0).getName());
	}
	
	public Class getReturnTypeClass() throws ClassNotFoundException{
		if (returnType.equals("void"))
			return null;
		else if (returnType.equals("boolean"))
			return boolean.class;
		else if (returnType.equals("byte"))
			return byte.class;
		else if (returnType.equals("char"))
			return char.class;
		else if (returnType.equals("short"))
			return short.class;
		else if (returnType.equals("int"))
			return int.class;
		else if (returnType.equals("long"))
			return long.class;
		else if (returnType.equals("float"))
			return float.class;
		else if (returnType.equals("double"))
			return double.class;
		else
			return Class.forName(returnType);
	}
	
	public boolean isPrimitiveReturnType(){
		if ("void".equals(returnType))
			return true;
		else if ("boolean".equals(returnType))
			return true;
		else if ("byte".equals(returnType))
			return true;
		else if ("char".equals(returnType))
			return true;
		else if ("short".equals(returnType))
			return true;
		else if ("int".equals(returnType))
			return true;
		else if ("long".equals(returnType))
			return true;
		else if ("float".equals(returnType))
			return true;
		else if ("double".equals(returnType))
			return true;
		else 
			return false;
	}
	
	public ArrayList getParamTypeClasses() throws ClassNotFoundException
	{
		ArrayList classes = new ArrayList();
		for (Iterator it = params.iterator(); it.hasNext();)
			classes.add(((SB_ClassMethodParameter)it.next()).getTypeClass());
		return classes;
	}
	
	public Class[] getParamTypeClassArray() throws ClassNotFoundException
	{
		Class[] classes = new Class[params.size()];
		for (int i = 0; i < classes.length; i ++)
			classes[i] = ((SB_ClassMethodParameter)params.get(i)).getTypeClass();
		return classes;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Vector getParams() {
		return params;
	}

	public void setParams(Vector params) {
		this.params = params;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	
	private String returnType;
	
	private Vector params;
	
	private String comment;
	
	
	public static SB_ClassMethod createMethod(String className,
			com.stottlerhenke.simbionic.common.classes.SB_ClassMethod methodDesc, 
			HashMap commentMap)
	{
		SB_ClassMethod methodSpec = new SB_ClassMethod();
		
		// sets method name;
		methodSpec.setName(methodDesc._name);
		
		// sets method return type;
		methodSpec.setReturnType(methodDesc._returnValue == null ? 
				"void" : methodDesc._returnValue.getName());
		
		String signature="";
		
		// adds parameters and constructs signature string;
		for (int p = 0; p < methodDesc._params.size(); p ++){
			SB_ClassMethodParameter paramSpec = new SB_ClassMethodParameter();
			paramSpec.setParent(methodSpec);
			paramSpec.setName((String)methodDesc.getParamNames().get(p));
			paramSpec.setType(((Class)methodDesc._params.get(p)).getName());
			methodSpec.getParams().add(paramSpec);
			
			if (p>0)
				signature += ", ";
			signature += ((Class)methodDesc._params.get(p)).getName();
		}
		
		signature = "(" + signature +")";
		
		// sets method comment;
		methodSpec.setComment((String)commentMap.get(className + ":" + methodSpec.getName() + signature));
		
		methodSpec.overloadIndex = methodDesc.overloadIndex;

		return methodSpec;
	}
	
	
	/*private SB_VarType getReturnTypeId(SB_TypeManager typeManager)
	{
		if ("void".equals(returnType))
			return SB_VarType.kVoid;
		else
			return typeManager.getClassIdByPackage(returnType);
	}*/
	
	/*
	 * implements Externalizable
	 */
	
	public void	readExternal(ObjectInput in) throws ClassNotFoundException, IOException
	{
		super.readExternal(in);
		
		returnType = (String) in.readObject();
		comment = (String) in.readObject();
		
		int size = in.readInt();
		params = new Vector(size);
		for (int i = 0; i < size; i ++){
			SB_ClassMethodParameter param = new SB_ClassMethodParameter();
			param.readExternal(in);
			param.setParent(this);
			params.add(param);
		}
	}
	
	public void writeExternal(ObjectOutput out) throws IOException
	{
		super.writeExternal(out);
		
		out.writeObject(returnType);
		out.writeObject(comment);
		
		// writes the number of parameters;
		int size = (params == null ? 0 : params.size());
		out.writeInt(size);
		
		// writes out parameters;
		for (int i = 0; i < size; i ++)
			((SB_ClassMethodParameter)params.get(i)).writeExternal(out);
	}
	
}
