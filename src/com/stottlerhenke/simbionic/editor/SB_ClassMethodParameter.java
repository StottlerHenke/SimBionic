package com.stottlerhenke.simbionic.editor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.Icon;


public class SB_ClassMethodParameter extends UserObject implements Comparable 
{
    private static final long serialVersionUID = 2302585093L + 16;

	private Icon _icon;
	
	public SB_ClassMethodParameter() {
		super();
	}

	public SB_ClassMethodParameter(String name) {
		super(name);
	}

	public Icon getIcon() {
        if (_icon == null)
            _icon = Util.getImageIcon("Parameter.gif");
        return _icon;
	}
	
	public int compareTo(Object arg0) {
		return getName().compareTo(((SB_ClassMethodParameter)arg0).getName());
	}
	
	public String getName(){
		if (super.getName() == null)
			return ("arg" + parent.getParams().indexOf(this));
		else
			return super.getName();
	}
	
	public String toString(){
		return (getName() + " : " + type);
	}
	
	public Class getTypeClass() throws ClassNotFoundException{
		if (type.equals("void"))
			return null;
		else if (type.equals("boolean"))
			return boolean.class;
		else if (type.equals("byte"))
			return byte.class;
		else if (type.equals("char"))
			return char.class;
		else if (type.equals("short"))
			return short.class;
		else if (type.equals("int"))
			return int.class;
		else if (type.equals("long"))
			return long.class;
		else if (type.equals("float"))
			return float.class;
		else if (type.equals("double"))
			return double.class;
		else
			return Class.forName(type);
	}
	
	public boolean isPrimitiveType(){
		if ("void".equals(type))
			return true;
		else if ("boolean".equals(type))
			return true;
		else if ("byte".equals(type))
			return true;
		else if ("char".equals(type))
			return true;
		else if ("short".equals(type))
			return true;
		else if ("int".equals(type))
			return true;
		else if ("long".equals(type))
			return true;
		else if ("float".equals(type))
			return true;
		else if ("double".equals(type))
			return true;
		else 
			return false;
	}

	public SB_ClassMethod getParent() {
		return parent;
	}

	public void setParent(SB_ClassMethod parent) {
		this.parent = parent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	private SB_ClassMethod parent;
	
	private String type;
	
	
	
	/*private SB_VarType getTypeId(SB_TypeManager typeManager)
	{
		if ("void".equals(type))
			return SB_VarType.kVoid;
		else
			return typeManager.getClassIdByPackage(type);
	}*/
	
	/*
	 * implements Externalizable
	 */
	
	public void	readExternal(ObjectInput in) throws ClassNotFoundException, IOException
	{
		super.readExternal(in);
		
		type = (String) in.readObject();
	}
	
	public void writeExternal(ObjectOutput out) throws IOException
	{
		super.writeExternal(out);
		
		out.writeObject(type);
	}
	
}
