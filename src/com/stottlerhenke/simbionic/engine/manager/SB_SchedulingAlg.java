package com.stottlerhenke.simbionic.engine.manager;
import java.io.Serializable;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;


public abstract class SB_SchedulingAlg implements Serializable  
{
  public SB_SchedulingAlg() {
  }
  /**
   * Prepares the scheduler for use.  Must be called before any
   * other method.
   * @return the maximum number of ticks that can pass between
   * updates for a single entity.
   */
  public abstract void Initialize(long maxPeriod) ;



// Entity Management

   /**
    * Adds the given entity to the update schedule.
    */
   public abstract void AddEntity(SB_EntityRecord entity)throws SB_Exception  ;

   /**
    * Removes the given entity from the update schedule.
    */
   public abstract void RemoveEntity(SB_EntityRecord entity) ;

   /**
    * Reschedules the given entity, possibly precipitating
    * a recomputation of the whole schedule.  Generally called
    * when the update frequency or priority of the entity has
    * been changed.
    * @param record the entity to be rescheduled
    */
   public abstract void RescheduleEntity(SB_EntityRecord record)throws SB_Exception ;


// Update Methods
  /**
   * Advances the scheduler's clock.  Must be called before
   * GetFirstEntityToUpdate() on each update.
   * @return the scheduler's current clock time (total ticks)
   */
   public abstract long Tick() ;

   /**
    * @return the scheduler's current clock time (total ticks)
    */
    public abstract long GetTicks() ;

   /**
    * Returns the first entity in the list of entities to be updated
    * on this clock tick.  Entities *are* ordered within a given tick
    * by update priority.
    */
   public abstract SB_EntityRecord GetFirstEntityToUpdate() throws SB_Exception;

   /**
    * Returns the next entity from the list of entities to be
    * updated on this clock tick, or NULL if no entities remain to
    * be updated.  FirstEntityToUpdate() must be called to get the
    * first entity in the update list.
    */
   public abstract SB_EntityRecord GetNextEntityToUpdate()throws SB_Exception ;

   /**
 	 * This gives the scheduler access to the singleton book after it has been deserialized
 	 * and before it is used.
 	 * 
 	 * @param book
 	 * @throws SB_Exception
 	 */
 	public abstract void finishDeserialization(SB_SingletonBook book) throws SB_Exception;

}