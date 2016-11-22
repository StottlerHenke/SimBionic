package com.stottlerhenke.simbionic.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.stottlerhenke.simbionic.common.classes.SB_ClassMap;


public class SB_TypeManager {
	
	/**
	 * Stores the information about a single type.
	 * 
	 */
	public static class TypeInfo
	{
		String		_name;			// unique SB name of the type
		String		_package;		// unique fully-qualified type name ("" for non-classes)
		int			_typeFlags;		// what type of type is this?
		int			_validFlags;	// where can this type be used?
		boolean		_isBase;		// is this a built-in type?
	};
	
	public SB_TypeManager() {
		_types = new ArrayList();
		_typeChangeListeners = new ArrayList();
	}
	
	/**
	 * @return Java Boolean class name
	 */
	public static String getBooleanTypeName() {
	   return Boolean.class.getName(); 
	}
	
	/**
	 * @return Java String class name
	 */
	public static String getStringTypeName() {
      return String.class.getName();
   }
	
	/**
	 * @return Java Integer class name
	 */
	public static String getIntegerTypeName() {
      return Integer.class.getName(); 
   }
	
	/**
	 * @return Java Float class name.
	 */
	public static String getFloatTypeName() {
      return Float.class.getName(); 
   }
	
	/**
	 * Return true if the specified name is a base type, false otherwise. 
	 * @param name Class name to figure out if it is a base type or not.
	 * @return true if the specified name is a base type, false otherwise.
	 */
	public boolean isBaseType(String name) {
	   for (int i = 0; i < _types.size(); i++) {
	      TypeInfo typeInfo = (TypeInfo)_types.get(i);
	      if (typeInfo._name.equals(name) && typeInfo._isBase) {
	         return true;
	      }
	   }
	   return false;
	}
	
	/**
	 * Initialize with the built-in types.
	 */
	public void initialize()
	{
	   // add base java classes
	   int validFlags = ETypeValid.kForConst.getState()|ETypeValid.kForVar.getState()|ETypeValid.kForParam.getState()|ETypeValid.kForRetVal.getState();
	   for (Class javaClass : SB_ClassMap.getBaseJavaClasses()) {
	      addClass(javaClass.getSimpleName(), javaClass.getName(),
	              validFlags, ETypeType.kJavaType.getState(), true);
	   }
	   addClass(Void.class.getSimpleName(), "java.lang.Void",
	         ETypeValid.kForRetVal.getState(), ETypeType.kJavaType.getState(), true);
	   
	}
	
	public void reset() {
	   _types.clear();
	   initialize();
	   notifyTypeUpdated();
	}
	
	/*
	 * ************* Adding and Removing Types ************
	 */ 
	/**
	 * Creates a new class type.  If package matches a deleted type, 
	 * then the new class will reuse the id of the deleted class.
	 * PRECONDITION: name must be unique among existing types!
	 * @param name unique SB name of the new class
	 * @param packageName the fully-qualified package name of the class
	 * @param isBase true if class is core
	 * @return the external ID of the new class
	 */
	public void addClass(String name, String packageName, int validFlags, int typeFlags, boolean isBase)
	{
		// does a class with this name already exist?
		TypeInfo info = getTypeInfoByPackage(packageName);	
		addType(name, validFlags, typeFlags, isBase, packageName);
	}

	
	
	/**
	 * Adds the specified type.  If a type with that
	 * name already exists, it is replaced by the new type.
	 * @param name unique name of the new type
	 * @param validFlags specifies where the type may be used
	 * @param typeFlags the types of the new type
	 * @param isBase true if type is built-in
	 * @param packageName the fully-qualified package name of the type (ignored for non-classes)
	 */
	public void addType(String name, int validFlags, int typeFlags, boolean isBase, String packageName)
	{
		boolean newType = false;
		TypeInfo oldTypeInfo = null;

		// if a type with this name already exists, find and replace it
		TypeInfo info = getTypeInfoByName(name);
		if (info == null)
		{
			// no match, create a whole new type
			info = new TypeInfo();
			_types.add(info);
			newType = true;
		}

		if (!newType)
			oldTypeInfo = cloneTypeInfo(info);

		info._name = name;
		info._package = packageName;
		info._typeFlags = typeFlags;
		info._validFlags = validFlags;
		info._isBase = isBase;
		
		notifyTypeUpdated();
	}
	
	/**
	 * For non-class types.
	 * @see #addTypeWithId(String, SB_VarType, int, int, boolean, String)
	 */
	public void addType(String name, int validFlags, int typeFlags, boolean isBase){
		addType(name, validFlags, typeFlags, isBase, "");
	}

	/**
	 * Deletes the specified type so that it cannot be used in the project.
	 * @param name unique name of the type to delete
	 */
	public void removeTypeByName(String name)
	{
		for (int i = 0; i < _types.size(); i ++)
		{
			if (((TypeInfo)_types.get(i))._name.equals(name))
			{
				deleteType(i);
				break;
			}
		}
	}


	/*
	 * ************* Looking Up Types ************
	 */

	
	/**
    * Looks up the package name for the given type name
    * @param name the type name to look up
    * @return the type package name, or "" if none is found
    */
	public String getTypePackage(String name){
		TypeInfo type =  getTypeInfoByName(name);
		if (type != null)
			return type._package;
		else
			return "";
	}
	


	
	
	
	/**
	 * Returns whether the specified class exists or not.
	 * The class is identified by its full package name, e.g., 
	 * java.lang.String.
	 * @param packageName	full package name, e.g., java.lang.String
	 * @return	true if the class is registered to the type manager
	 */
	public boolean classExists(String packageName){
		return (getTypeInfoByPackage(packageName) != null);
	}
	
	
	/*
	 * ************* Combo Box Handling ************
	 */

	/**
	 * Generates the list of items for the type combo box for local and global variables,
	 * constants and behavior/action/predicate parameters.
	 * @param isConstant true if this is a combo box for constants
	 * @param isParam true if this is a combo box for a behavior parameter
	 * @return list of items for the variable combo box
	 */
	public ArrayList getVarComboItems(boolean isConstant, boolean isParam)
	{
		ArrayList items = new ArrayList();
		for (int i = 0; i < _types.size(); i ++)
		{
			TypeInfo type = (TypeInfo) _types.get(i);
			if (isVarComboItem(type, isConstant, isParam))
			{
				// valid type
				items.add(type._name);
			}
		}
		
		return items;
	}
	
	/**
	 * getVarComboItems(false, false)
	 * @see #getVarComboItems(boolean, boolean)
	 */
	public ArrayList getVarComboItems(){
		return getVarComboItems(false, false);
	}

	/**
	 * @param index index of variable combo box item 
	 * @param isConstant true if this is a combo box for constants
	 * @param isParam true if this is a combo box for a behavior parameter
	 * @return Name of the item
	 */
	public String varComboIndexToName(int index, boolean isConstant, boolean isParam)
	{
		int count=0;
		for (int i = 0; i < _types.size(); i ++)
		{
			TypeInfo type = (TypeInfo) _types.get(i);
			if (isVarComboItem(type, isConstant, isParam))
			{
				// valid type; does the index match?
				if (count == index)
					return type._name;

				count++;
			}
		}
		
		return null;
	}
	
	/**
	 * varComboIndexToId(index, false, false)
	 * @see #varComboIndexToId(int, boolean, boolean)
	 */
	public String varComboIndexToName(int index){
		return varComboIndexToName(index, false, false);
	}

	/**
	 * @param name name of the type to convert
	 * @param isConstant true if this is a combo box for constants
	 * @param isParam true if this is a combo box for a function parameter
	 * @return combo box index of the item
	 */
	public int nameToVarComboIndex(String name, boolean isConstant, boolean isParam)
	{
		int count=0;
		for (int i = 0; i < _types.size(); i ++)
		{
			TypeInfo type = (TypeInfo) _types.get(i);
			if (isVarComboItem(type, isConstant, isParam))
			{
				if (type._name.equals(name))
					return count;

				count++;
			}
		}

		return -1;
	}
	
	/**
	 * nameToVarComboIndex(name, false, false)
	 * @see #nameToVarComboIndex(name, boolean, boolean)
	 */
	public int nameToVarComboIndex(String name){
		return nameToVarComboIndex(name, false, false);
	}

	/**
	 * Generates the list of items for the type combo box for
	 * class fields and method parameters.
	 * @param isParam true if this is a combo box for a method param
	 * @return list of items for the class param/field combo box
	 */
	public ArrayList getClassComboItems(boolean isParam)
	{
		ArrayList items = new ArrayList();
		for (int i = 0; i < _types.size(); i ++)
		{
			TypeInfo type = (TypeInfo) _types.get(i);
			if (isClassComboItem(type, isParam))
			{
				// valid type
				items.add(type._name);
			}
		}

		return items;
	}
	
	/**
	 * getClassComboItems(false)
	 * @see #getClassComboItems(boolean)
	 */
	public ArrayList getClassComboItems(){
		return getClassComboItems(false);
	}

	/**
	 * @param index index of class field/param combo box item 
	 * @param isParam true if this is a combo box for a method field/param
	 * @return name of the item
	 */
	public String classComboIndexToName(int index, boolean isParam)
	{
		int count=0;
		for (int i = 0; i < _types.size(); i ++)
		{
			TypeInfo type = (TypeInfo) _types.get(i);
			if (isClassComboItem(type, isParam))
			{
				// valid type; does the index match?
				if (count == index)
					return type._name;

				count++;
			}
		}
		
		return null;
	}
	
	/**
	 * classComboIndexToName(index, false)
	 * @see #classComboIndexToName(int, boolean)
	 */
	public String classComboIndexToName(int index){
		return classComboIndexToName(index, false);
	}

	/**
	 * @param name name of the type to convert
	 * @param isParam true if this is a combo box for a method param
	 * @return combo box index of the item
	 */
	public int nameToClassComboIndex(String name, boolean isParam)
	{
		int count=0;
		for (int i = 0; i < _types.size(); i ++)
		{
			TypeInfo type = (TypeInfo) _types.get(i);
			if (isClassComboItem(type, isParam))
			{
				// valid type; does the index match?
				if (type._name.equals(name))
					return count;

				count++;
			}
		}
		
		return -1;
	}
	
	/**
	 * nameToClassComboIndex(name, false)
	 * @see #nameToClassComboIndex(String, boolean)
	 */
	public int nameToClassComboIndex(String name){
		return nameToClassComboIndex(name, false);
	}

	/**
	 * Generates the list of items for the type combo box for function or method 
	 * return types.
	 * @param isMethod true if this is a combo box for a method return value
	 * @return list of items for the function return value combo box
	 */
	public ArrayList getReturnValueComboItems(boolean isMethod)
	{
		ArrayList items = new ArrayList();
		for (int i = 0; i < _types.size(); i ++)
		{
			TypeInfo type = (TypeInfo) _types.get(i);
			if (isReturnValueComboItem(type, isMethod))
				items.add(type._name);
		}

		return items;
	}
	
	/**
	 * getReturnValueComboItems(false)
	 * @see #getReturnValueComboItems(boolean)
	 */
	public ArrayList getReturnValueComboItems(){
		return getReturnValueComboItems(false);
	}

	/**
	 * @param index index of function/method return value combo box item 
	 * @param isMethod true if this is a combo box for a method return value
	 * @return name of the item
	 */
	public String returnValueComboIndexToName(int index, boolean isMethod)
	{
		int count=0;
		for (int i = 0; i < _types.size(); i ++)
		{
			TypeInfo type = (TypeInfo) _types.get(i);
			if (isReturnValueComboItem(type, isMethod))
			{
				// valid type; does the index match?
				if (count == index)
					return type._name;

				count++;
			}
		}
		
		return null;
	}
	
	/**
	 * returnValueComboIndexToName(index, false)
	 * @see #returnValueComboIndexToName(int, boolean)
	 */
	public String returnValueComboIndexToName(int index){
		return returnValueComboIndexToName(index, false);
	}

	/**
	 * @param name name of the type to convert
	 * @param isMethod true if this is a combo box for a method return value
	 * @return combo box index of the item
	 */
	public int nameToReturnValueComboIndex(String name, boolean isMethod)
	{
		int count=0;
		for (int i = 0; i < _types.size(); i ++)
		{
			TypeInfo type = (TypeInfo) _types.get(i);
			if (isReturnValueComboItem(type, isMethod))
			{
				if (type._name.equals(name))
					return count;

				count++;
			}
		}
		
		return -1;
	}
	
	/**
	 * nameToReturnValueComboIndex(name, false)
	 * @see #nameToReturnValueComboIndex(String, boolean)
	 */
	public int nameToReturnValueComboIndex(String name){
		return nameToReturnValueComboIndex(name, false);
	}
	
	public String[] getPackageItems(){
		return getPackageItems(false, false);
	}
	
	public String[] getPackageItems(boolean isConstant, boolean isParam)
	{
		HashMap items = new HashMap();
		for (int i = 0; i < _types.size(); i ++)
		{
			TypeInfo type = (TypeInfo) _types.get(i);
			if (isVarComboItem(type, isConstant, isParam))
			{
				// valid type
				int idx = type._package.lastIndexOf('.');
				String packageName = (idx < 0 ? "(default package)" : type._package.substring(0, idx));
				items.put(packageName, packageName);
			}
		}
		String[] pItems = new String[items.values().size()];
		items.values().toArray(pItems);
		Arrays.sort(pItems);
		return pItems;
	}
	
	public String getTypeName(String fullName) {
	   for (int i = 0; i < _types.size(); i ++)
      {
         TypeInfo type = (TypeInfo) _types.get(i);
         if (type._package.equals(fullName)) {
            return type._name;
         }
      }
	   return null;
	}
	
	
	/*
	 * ************* helper methods *************
	 */
	

	
	private TypeInfo getTypeInfoByName(String name)
	{
		for (int i = 0; i < _types.size(); i ++)
		{
			TypeInfo type = (TypeInfo) _types.get(i);
			if (type._name.equals(name))
				return type;
		}
		
		return null;
	}
	
	
	private TypeInfo getTypeInfoByPackage(String packageName)
	{
		for (int i = 0; i < _types.size(); i ++)
		{
			TypeInfo type = (TypeInfo) _types.get(i);
			if (packageName.equals(type._package)) {
			   return type;
			}
		}
		
		return null;
	}
	

	private void deleteType(int index)
	{
		TypeInfo type = (TypeInfo) _types.get(index);
	  _types.remove(index);
		
		// notifies the listeners that the type was removed
	  notifyTypeUpdated();
	}
	

	private boolean isVarComboItem(TypeInfo type, boolean isConstant, boolean isParam)
	{
		if ((type._typeFlags & (ETypeType.kJavaType.getState() | ETypeType.kEnumType.getState())) > 0)
		{	
			if (isConstant)
			{
				// constant
				return ((type._validFlags & ETypeValid.kForConst.getState()) > 0);
			}
			else if (isParam)
			{
				// action/predicate/behavior param
				return ((type._validFlags & ETypeValid.kForParam.getState()) > 0);
			}
			else
			{
				// local or global variable
				return ((type._validFlags & ETypeValid.kForVar.getState()) > 0);
			}
		}
		
		return false;
	}
	
	private boolean isClassComboItem(TypeInfo type, boolean isParam)
	{
		if ((type._typeFlags & (ETypeType.kJavaType.getState())) > 0)
		{
			if (isParam)
			{
				// method param
				return ((type._validFlags & ETypeValid.kForParam.getState()) > 0);
			}
			else
			{
				// method field
				return ((type._validFlags & ETypeValid.kForVar.getState()) > 0);
			}
		}
		
		return false;
	}
	
	private boolean isReturnValueComboItem(TypeInfo type, boolean isMethod)
	{
		
		if ((type._validFlags & ETypeValid.kForRetVal.getState()) > 0)
		{
			if (isMethod)
			{
				return ((type._typeFlags & (ETypeType.kJavaType.getState())) > 0);
			}
			else
			{
				return ((type._typeFlags & (ETypeType.kJavaType.getState() | ETypeType.kEnumType.getState())) > 0);
			}
		}
		
		return false;
	}
	
	private TypeInfo cloneTypeInfo(TypeInfo info){
		TypeInfo copy = new TypeInfo();
		copy._name = info._name;
		copy._package = info._package;
		copy._typeFlags = info._typeFlags;
		copy._validFlags = info._validFlags;
		copy._isBase = info._isBase;
		return copy;
	}
	
	
	/*
	 * ************* type change listener methods *************
	 */
	
	public void addTypeChangeListener(SB_TypeChangeListener l){
		_typeChangeListeners.add(l);
	}
	
	public void removeTypeChangeListener(SB_TypeChangeListener l){
		_typeChangeListeners.remove(l);
	}
	
	public void removeAllTypeChangeListeners(){
		_typeChangeListeners.clear();
	}
	
	protected void notifyTypeUpdated(){
		for (int i = 0; i < _typeChangeListeners.size(); i ++)
			((SB_TypeChangeListener)_typeChangeListeners.get(i)).typeUpdated(this);
	}
	
	
	private ArrayList _types;	// ArrayList<TypeInfo>
	
	private ArrayList _typeChangeListeners;
}
