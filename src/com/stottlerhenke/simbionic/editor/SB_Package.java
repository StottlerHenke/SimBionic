package com.stottlerhenke.simbionic.editor;

import java.awt.Component;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import com.stottlerhenke.simbionic.common.classes.SB_ClassDescription;
import com.stottlerhenke.simbionic.common.classes.SB_ClassMap;

public class SB_Package extends UserObject implements Comparable 
{
    private static final long serialVersionUID = 2302585093L + 12;
    
	//A class index variable valid across different groups
//	static private int classIndex;
	
	// a child can be either a SB_Package or a SB_Class
	private Vector children;
	
	private Icon _icon;
	
	public SB_Package() {
		super();
		children = new Vector();
	}

	public SB_Package(String name) {
		super(name);
		children = new Vector();
	}

	public Icon getIcon() {
        if (_icon == null)
            _icon = Util.getImageIcon("Package.gif");
        return _icon;
	}

	public int compareTo(Object arg0) {
		if (arg0 instanceof SB_Class)
			return -1;
		else if (arg0 instanceof SB_Package)
			return getName().compareTo(((SB_Package)arg0).getName());
		else
			throw new ClassCastException();
	}
	
	public boolean equals(Object arg0) {
		if (arg0 instanceof SB_Class)
			return false;
		else
			return getName().equals(((SB_Package)arg0).getName());
	}
	
	public Vector getChildren() {
		return children;
	}

	public void setChildren(Vector children) {
		this.children = children;
	}
	
	/**
	 * Resets the inUse flags in classes, members and methods to false.
	 * Call this method before compiling.
	 */
	public void resetInUseFlags(){
		for (Iterator it = children.iterator(); it.hasNext();){
			Object child = it.next();
			if (child instanceof SB_Package)
				((SB_Package)child).resetInUseFlags();
			else if (child instanceof SB_Class)
				((SB_Class)child).resetInUseFlags();
		}
	}
	
	/**
	 * Returns the SB_Class object which has the given className.
	 */
	public SB_Class lookupClass(String className){
		for (Iterator it = children.iterator(); it.hasNext();){
			Object child = it.next();
			if (child instanceof SB_Package){
				SB_Class cls = ((SB_Package)child).lookupClass(className);
				if (cls != null)
					return cls;
			}
			else if (child instanceof SB_Class && ((SB_Class)child).getName().equals(className))
				return (SB_Class) child;
		}
		return null;
	}
	
	/**
	 * Returns the SB_Class object which has the given classAlias.
	 */
	public SB_Class lookupClassByAlias(String classAlias){
		for (Iterator it = children.iterator(); it.hasNext();){
			Object child = it.next();
			if (child instanceof SB_Package){
				SB_Class cls = ((SB_Package)child).lookupClassByAlias(classAlias);
				if (cls != null)
					return cls;
			}
			else if (child instanceof SB_Class && ((SB_Class)child).getAlias().equals(classAlias))
				return (SB_Class) child;
		}
		return null;
	}
	
	/**
	 * @return the number of classes in-use in this package.
	 */
	public int getInUseClassCount(){
		int count = 0;
		for (Iterator it = children.iterator(); it.hasNext();){
			Object child = it.next();
			if (child instanceof SB_Package)
				count += ((SB_Package)child).getInUseClassCount();
			else if (child instanceof SB_Class && ((SB_Class)child).isInUse())
				count ++;
		}
		return count;
	}

	/**
	 * Adds to this SB_Package class specifications stored in the
	 * given SB_ClassMap. commentMap stores class, member comments.
	 * 
	 * @param classMap	stores class specifications
	 * @param commentMap	stores comments of classes and their memebers,
	 * 	comments are only necessary at the authoring time, they aren't stored in SB_ClassMap
	 * @param parentUI	the parent UI component which invokes the method, can be null
	 */
	public void addClasses(SB_ClassMap classMap, 
			HashMap commentMap, Component parentUI)
	{
		for (Iterator it = classMap.getClassList().iterator(); it.hasNext();)
		{
			String className = (String) it.next();
			SB_Package parent = getPackage(className);
			if (parent == null)
				parent = this;
			
			SB_ClassDescription classDesc = classMap.getClassDescription(className);
			SB_Class classSpec = SB_Class.createClass(className, classDesc, commentMap);
			
			int index = parent.getChildren().indexOf(classSpec);
			
			if (index < 0)
			{
				// add if does not exist;
				parent.getChildren().add(classSpec);
			}
			else if (!classSpec.isEmpty())
			{
				// replace the empty one with the non-empty one silently;
				if (((SB_Class)parent.getChildren().get(index)).isEmpty())
					parent.getChildren().set(index, classSpec);
				// or replace the non-empty one with the new non-empty one if user agrees;
				else if (JOptionPane.showConfirmDialog(parentUI, classSpec.getName() + " is already imported. Overwrite?", "Duplicate Class", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
					parent.getChildren().set(index, classSpec);
			}
		}
	}

//	/**
//	 * Reset the class index before calling toClassMap
//	 */
//	public void resetClassMapIndex() {
//		classIndex = 128;
//	}
	
	/**
	 * Puts the class specs in this SB_Package into the given SB_ClassMap.
	 * 
	 * @param classMap output parameter stores class specifications
	 * @param typeManager  lookup class IDs from type manager
	 * @throws ClassNotFoundException if there're class names of member variables or 
	 * 	method parameters are not names of defined classes
	 */
	public void toClassMap(SB_ClassMap classMap, SB_TypeManager typeManager) throws ClassNotFoundException
	{
		for (Iterator it = getChildren().iterator(); it.hasNext();)
		{
			Object child = it.next();
			
			if (child instanceof SB_Package)
				((SB_Package)child).toClassMap(classMap, typeManager);
			else if (child instanceof SB_Class)
				((SB_Class)child).toClassMap(classMap, typeManager);
		}
	}
	
	/*
	 * implements Externalizable
	 */
	
	public void	readExternal(ObjectInput in) throws ClassNotFoundException, IOException
	{
        super.readExternal(in);

        int size = in.readInt();
        children = new Vector(size);
        
        for (int i = 0; i < size; i ++){
        	switch (in.readChar()){
        	case 'P':
        		SB_Package pack = new SB_Package();
        		pack.readExternal(in);
        		children.add(pack);
        		break;
        	case 'C':
        		SB_Class cls = new SB_Class();
        		cls.readExternal(in);
        		children.add(cls);
        	}
        }
	}
	
	public void writeExternal(ObjectOutput out) throws IOException
	{
		super.writeExternal(out);
		
		// writes the number of children;
		int size = (children == null ? 0 : children.size());
		out.writeInt(size);
		
		// writes out children;
		for (int i = 0; i < size; i ++){
			Object child = children.get(i);
			if (child instanceof SB_Package){
				out.writeChar('P');
				((SB_Package)child).writeExternal(out);
			}
			else if (child instanceof SB_Class){
				out.writeChar('C');
				((SB_Class)child).writeExternal(out);
			}
		}
	}

	/*
	 * helper methods
	 */

	private SB_Package getPackage(String className)
	{
		int idx = className.lastIndexOf('.');
		
		if (idx <= 0)
			return null;
		else
		{
			String packageName = className.substring(0, idx);
			SB_Package pack = getSubPackage(packageName);
			if (pack == null){
				pack = new SB_Package();
				pack.setName(packageName);
				getChildren().add(pack);
			}
			return pack;
		}		
	}
	
	private SB_Package getSubPackage(String name){
		for (Iterator it = getChildren().iterator(); it.hasNext();){
			Object child = it.next();
			if (child instanceof SB_Package && name.equals(((SB_Package)child).getName()))
				return (SB_Package) child;
		}
		return null;
	}

}
