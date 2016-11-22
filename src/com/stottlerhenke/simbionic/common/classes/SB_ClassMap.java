package com.stottlerhenke.simbionic.common.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.Table;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;

/**
 * This class provides access to class aliases (SB Names), fully-qualified class names,
 * and class descriptions (used to access methods/member/constructors).
 */
public class SB_ClassMap
{

  //private HashMap _classMap = new HashMap(); //Key = int identifier, Value = string of class name
  private HashMap _classPackageMap = new HashMap(); //Key = string of class name, Value = fully specified class name (with package)
  private HashMap _classDescriptionMap = new HashMap(); //Key = fully specified class name, Value = SB_ClassDescription

  public SB_ClassMap()
  {
  }
  
  /**
   * @return The list of basic java classes.
   */
  public static List<Class> getBaseJavaClasses() {
     
     List<Class> classes = new ArrayList<Class>();
     classes.add(String.class);
     classes.add(Boolean.class);
     classes.add(Integer.class);
     classes.add(Float.class);
     classes.add(Vector.class);
     classes.add(Table.class);
     classes.add(Object.class);
     classes.add(ArrayList.class);
     return classes;
  }
  
  /**
   * @param id a number that corresponds to a class alias type
   * @return a fully qualified java class name; null if undefined
   */
  public String getJavaClassName(int id)
  {
     System.err.println("getJavaClassName " + id);
     return "";
    /*  if(id > SB_VarType.kCLASS_START.getState())
      {
          String alias = (String) _classMap.get( new Integer(id) );
          return (String) _classPackageMap.get(alias);
      }
      else
      {
          String className = null;
          if( id == SB_VarType.kAny.getState() ) className = Object.class.getName();
          return className;
      } */
  }
  
  
  /**
   * @param sbClassname the alias for a class used within SimBionic
   * @return the fully qualified class name
   */
  public String getJavaClassName(String sbClassname)
  {
    return (String) _classPackageMap.get(sbClassname);    
  }
  
  /**
   * 
   * @param javaClassName the fully qualified class name
   * @return the corresponding class description
   */
  public SB_ClassDescription getClassDescription(String javaClassName)
  {
    return (SB_ClassDescription) _classDescriptionMap.get(javaClassName);
  }
  
  /**
   * Convert the class representations from the initial integer represetations
   * to actual Class objects. This should only be called once after class loading
   * is finished.
   * 
   * @param book
   */
  public void convertClassDescriptions(SB_SingletonBook book) throws SB_Exception
  {
    for (Iterator i = _classDescriptionMap.keySet().iterator(); i.hasNext();) 
    {
      String key = (String) i.next();
      SB_ClassDescription sb = (SB_ClassDescription) _classDescriptionMap.get(key);
      sb.convertClassDescription(book);
    }    
  }
  
  /**
   * 
   * @param s
   * @return true if s if a defined class alias, false otherwise
   */
  public boolean isDefinedSBClass(String s)
  {
    return _classPackageMap.containsKey(s);
  }
  
  /**
   * 
   * @param c
   * @return true if c is a defined java class
   */
  public boolean isDefinedJavaClass(Class c)
  {
    return _classPackageMap.containsValue(c.getName());
  }

  /**
   * Define an SB class alias. 
   * NOTE: addJavaClass must also be called for this class.
   * 
   * @param id SB type identifier
   * @param sbClassName SB alias
   */
 /* public void addSBClass(Integer id, String sbClassName)
  {
    _classMap.put( id, sbClassName);
  } */
  
  /**
   * Define an SB fully qualified class name.
   * NOTE: addSBClass must also be called for this class.
   * 
   * @param sbClassName
   * @param javaClassName
   */
  public void addJavaClass(String sbClassName, String javaClassName)
  {
    
    _classPackageMap.put(sbClassName, javaClassName);
    _classDescriptionMap.put(javaClassName, new SB_ClassDescription());
  }
  
  /**
   * Enter an additional mapping from an SB class name to a java class name
   * @param sbClassName
   * @param javaClassName
   */
  public void addAdditionalSBClassName(String sbClassName, String javaClassName)
  {
    _classPackageMap.put(sbClassName, javaClassName);
  }
  
  /**
   * 
   * @return a list of strings that represent the fully specified class names defined in this map
   */
  public ArrayList getClassList()
  {
    ArrayList classList = new ArrayList();
    
    for (Iterator i = _classPackageMap.keySet().iterator(); i.hasNext();) 
    {
      String key = (String) i.next();
      String value = (String) _classPackageMap.get(key);
      classList.add(value);
    }     
    
    return classList;
  }
}
