package com.stottlerhenke.simbionic.editor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import javax.swing.Icon;

import com.stottlerhenke.simbionic.common.classes.SB_ClassDescription;

public class SB_ClassMember extends UserObject implements Comparable 
{
    private static final long serialVersionUID = 2302585093L + 14;

	private Icon _icon;
	
	transient private boolean inUse;
	
	public SB_ClassMember() {
		super();
	}

	public SB_ClassMember(String name) {
		super(name);
	}
	
	public boolean isInUse(){
		return inUse;
	}
	
	public void setInUse(boolean inUse){
		this.inUse = inUse;
	}

	public Icon getIcon() {
        if (_icon == null)
            _icon = Util.getImageIcon("Member.gif");
        return _icon;
	}
	
	public int compareTo(Object arg0) {
		return getName().compareTo(((SB_ClassMember)arg0).getName());
	}
	
	public String getToolTipText(){
		return comment;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	private String type;
	
	private String comment;

	
	public static SB_ClassMember createMember(String className, String memberName, 
			SB_ClassDescription classDesc, HashMap commentMap)
	{
		SB_ClassMember memberSpec = new SB_ClassMember();
		
		// sets member name;
		memberSpec.setName(memberName);
		
		// sets member type;
		memberSpec.setType(((Class)classDesc.getMembers().get(memberSpec.getName())).getName());
		
		// sets member comment;
		memberSpec.setComment((String)commentMap.get(className + ":" + memberSpec.getName()));
		
		return memberSpec;
	}
	
	
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
		comment = (String) in.readObject();
	}
	
	public void writeExternal(ObjectOutput out) throws IOException
	{
		super.writeExternal(out);
		
		out.writeObject(type);
		out.writeObject(comment);
	}
	
}
