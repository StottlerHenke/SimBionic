package com.stottlerhenke.simbionic.editor;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Icon;

import com.stottlerhenke.simbionic.common.classes.SB_ClassDescription;
import com.stottlerhenke.simbionic.common.classes.SB_ClassMap;

public class SB_Class extends UserObject implements Comparable 
{
    private static final long serialVersionUID = 2302585093L + 13;

	private Icon _icon;
	
	transient private boolean inUse;
	
	public SB_Class() {
		super();
		members = new Vector();
		methods = new Vector();
	}

	public SB_Class(String name) {
		super(name);
		members = new Vector();
		methods = new Vector();
	}
	
	public boolean isInUse(){
		return inUse;
	}
	
	public void setInUse(boolean inUse){
		this.inUse = inUse;
	}
	
	/**
	 * Resets the inUse flags in members, methods to false.
	 */
	protected void resetInUseFlags()
	{
		setInUse(false);
		
		for (Iterator it = members.iterator(); it.hasNext();)
			((SB_ClassMember)it.next()).setInUse(false);
		
		for (Iterator it = methods.iterator(); it.hasNext();)
			((SB_ClassMethod)it.next()).setInUse(false);
	}
	
	public SB_ClassMember lookupMember(String memberName){
		for (Iterator it = members.iterator(); it.hasNext();){
			SB_ClassMember member = (SB_ClassMember) it.next();
			if (member.getName().equals(memberName))
				return member;
		}
		return null;
	}
	
	public Vector lookupMethods(String methodName){
		Vector ret = new Vector();
		for (Iterator it = methods.iterator(); it.hasNext();){
			SB_ClassMethod method = (SB_ClassMethod) it.next();
			if (method.getName().equals(methodName))
				ret.add(method);
		}
		return ret;
	}
	
	public SB_ClassMethod lookupMethod(String methodName,int overload)
	{
		int overloadCount = 0;
		int numMethods = methods.size();
		for (int i=0; i < numMethods; ++i)
		{
			SB_ClassMethod method = (SB_ClassMethod) methods.get(i);
			if (method.getName().equals(methodName))
			{
				if (overloadCount == overload)
					return method;

				// found a matching method, but not the right overload
				++overloadCount;
			}
		}
		return null;
	}
	
	public int getInUseMemberCount(){
		int count = 0;
		for (Iterator it = members.iterator(); it.hasNext();){
			if (((SB_ClassMember)it.next()).isInUse())
				count ++;
		}
		return count;
	}
	
	public int getInUseMethodCount(){
		int count = 0;
		for (Iterator it = methods.iterator(); it.hasNext();){
			if (((SB_ClassMethod)it.next()).isInUse())
				count ++;
		}
		return count;
	}

	public Icon getIcon() {
        if (_icon == null)
            _icon = Util.getImageIcon("Class.gif");
        return _icon;
	}

	public int compareTo(Object arg0) {
		if (arg0 instanceof SB_Package)
			return 1;
		else if (arg0 instanceof SB_Class)
			return getName().compareTo(((SB_Class)arg0).getName());
		else
			throw new ClassCastException();
	}
	
	public boolean equals(Object arg0){
		if (arg0 instanceof SB_Package)
			return false;
		else
			return getName().equals(((SB_Class)arg0).getName());
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
		return getAlias();
	}
	
	public boolean isEmpty(){
		return (!(members.size() > 0 || methods.size() > 0));
	}
	
	public String getType(){
		return type;
	}
	
	public void setType(String type){
		this.type = type;
	}

	/**
	 * Returns the alias of the class. By default, it is the
	 * class name without package, for example, "ClassSpecGroup".
	 * @return alias of the class
	 */
	public String getAlias() {
		if (alias != null)
			return alias;
		else if (getName() != null)
			return getName().substring(getName().lastIndexOf('.')+1);
		else
			return null;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Vector getMembers() {
		return members;
	}

	public void setMembers(Vector members) {
		this.members = members;
	}

	public Vector getMethods() {
		return methods;
	}

	public void setMethods(Vector methods) {
		this.methods = methods;
	}
	
	private String type;
	
	private String alias;
	
	private Vector members;
	
	private Vector methods;
	
	private String comment;
	
	
	public void toClassMap(SB_ClassMap classMap, SB_TypeManager typeManager) throws ClassNotFoundException
	{
	    SB_ClassDescription classDesc = classMap.getClassDescription(getName());
	    
	   // int classId = typeManager.getClassIdByPackage(getName()).getState();
	    
	    if (classDesc == null){
	    	//classMap.addSBClass(new Integer(classId), getAlias());  
	    	classMap.addJavaClass(getAlias(), getName());
	    	classDesc = classMap.getClassDescription(getName());
	    }
	    
	    for (Iterator it = getMembers().iterator(); it.hasNext();){
	    	SB_ClassMember memberSpec = (SB_ClassMember) it.next();
    		classDesc.addMemberDescription(memberSpec.getName(), 
    				memberSpec.getTypeClass());
	    }
	    
	    for (Iterator it = getMethods().iterator(); it.hasNext();){
	    	SB_ClassMethod methodSpec = (SB_ClassMethod) it.next();
	    	com.stottlerhenke.simbionic.common.classes.SB_ClassMethod methodDesc = 
	    		classDesc.addMethodDescription(methodSpec.getName(), 
	    				methodSpec.getReturnTypeClass(), 
	    				methodSpec.getParamTypeClasses());
	    	methodDesc.overloadIndex = methodSpec.overloadIndex;
	    }	    	
	}
	
	public static SB_Class createClass(String className, 
			SB_ClassDescription classDesc, HashMap commentMap)
	{
		SB_Class classSpec = new SB_Class();
		
		// sets class name;
		classSpec.setName(className);
		
		// sets class comment;
		classSpec.setComment((String)commentMap.get(className));
		
		// adds member variables;
		for (Iterator it = classDesc.getMembers().keySet().iterator(); it.hasNext();)
		{
			SB_ClassMember memberSpec = SB_ClassMember.createMember(className,
					(String) it.next(), classDesc, commentMap);
			classSpec.getMembers().add(memberSpec);
		}
		
		// adds methods;
		for (Iterator it = classDesc.getMethods().iterator(); it.hasNext();){
			SB_ClassMethod methodSpec = SB_ClassMethod.createMethod(className, 
					(com.stottlerhenke.simbionic.common.classes.SB_ClassMethod) it.next(), commentMap);
			classSpec.getMethods().add(methodSpec);
		}
		
		return classSpec;
	}
	
	/**
	 * Create a new SB_Class from the specified class name
	 * @param className The class name
	 * @return a new SB_Class created from the class name.
	 */
	public static SB_Class createClass(String className) {
	   Class javaClass = null;
      try {
         javaClass = Class.forName(className);
      }
      catch (Exception e) {
         e.printStackTrace();
         return null;
      }

   
      if (javaClass == null) {
         return null;
      }
      
      SB_Class sbClass = new SB_Class(className);
      //only the public fields //fixme. Does this include superclass members?
      Field[] members = javaClass.getFields();
      Vector classMembers = new Vector();
      classMembers.setSize(members.length);
      for (Field member : members) {
         SB_ClassMember classMember = new SB_ClassMember();
         
         // sets member name;
         classMember.setName(member.getName());
         
         // sets member type;
         classMember.setType(member.getType().getName());
         
         // sets member comment
         classMember.setComment(member.toString());
         
         classMembers.add(classMember);
      }
      sbClass.setMembers(classMembers);
      
      Vector classMethods = new Vector();
      Method[] methods = javaClass.getMethods();//public methods
      for (Method method : methods) {
         SB_ClassMethod classMethod = new SB_ClassMethod();
         classMethod.setName(method.getName());
         // sets method return type;
         classMethod.setReturnType(method.getReturnType().getName());
         classMethods.add(classMethod);
      }
      sbClass.setMethods(classMethods);
      
      return sbClass;

	}
	
	

	/*
	 * implements Externalizable
	 */
	public void	readExternal(ObjectInput in) throws ClassNotFoundException, IOException
	{
		super.readExternal(in);
		
	    type = (String)in.readObject();
		
		alias = (String) in.readObject();
		comment = (String) in.readObject();
		
		int memberSize = in.readInt();
		members = new Vector(memberSize);
		for (int i = 0; i < memberSize; i ++){
			SB_ClassMember member = new SB_ClassMember();
			member.readExternal(in);
			members.add(member);
		}
		
		int methodSize = in.readInt();
		methods = new Vector(methodSize);
		for (int i = 0; i < methodSize; i ++){
			SB_ClassMethod method = new SB_ClassMethod();
			method.readExternal(in);
			method.overloadIndex = getOverloadIndex(method);
			methods.add(method);
		}
	}
	
	public void writeExternal(ObjectOutput out) throws IOException
	{
		super.writeExternal(out);
		
		out.writeObject(type);
		
		out.writeObject(alias);
		out.writeObject(comment);
		
		// writes the number of members;
		int memberSize = (members == null ? 0 : members.size());
		out.writeInt(memberSize);
		
		// writes out members;
		for (int i = 0; i < memberSize; i ++)
			((SB_ClassMember)members.get(i)).writeExternal(out);
		
		// writes the number of methods;
		int methodSize = (methods == null ? 0 : methods.size());
		out.writeInt(methodSize);
		
		// writes out methods;
		for (int i = 0; i < methodSize; i ++)
			((SB_ClassMethod)methods.get(i)).writeExternal(out);
	}
	
	private int getOverloadIndex(SB_ClassMethod method)
	{
		int count = 0;
		for (Iterator it = methods.iterator(); it.hasNext();){
			if (((SB_ClassMethod)it.next()).getName().equals(method.getName()))
				count ++;
		}
		return count;
	}
	
}
