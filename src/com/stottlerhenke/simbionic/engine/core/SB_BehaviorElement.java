
package com.stottlerhenke.simbionic.engine.core;



/**
 * The base class for all components of a behavior.
 */
public class SB_BehaviorElement  
{
  protected String _name;
  protected int _id;
  protected SB_BehaviorElement _owner;

  public SB_BehaviorElement(SB_BehaviorElement owner) {
    _owner = owner;
  }

  /**
   * @return the ID of this element (unique among all elements sharing the same owner)
   */
  public int getId() { return _id; }

  /**
   * Sets the unique ID of this element.
   * @param id the unique ID of this element
   */
  public void setId(int id) { _id = id; }

  /**
   * @return the human-readable name of the element
   */
  public String getName() { return _name; }

  /**
   * Sets the human-readable name of the element.
   * @param name the element's new name
   */
  public void setName(String name) { _name = name; }
  

  /**
   * @return the component to which this component belongs
   */
  public SB_BehaviorElement getOwner() { return _owner; }

}