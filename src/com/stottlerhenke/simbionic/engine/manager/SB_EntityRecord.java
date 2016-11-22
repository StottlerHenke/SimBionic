package com.stottlerhenke.simbionic.engine.manager;
import java.io.Serializable;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;
import com.stottlerhenke.simbionic.engine.core.SB_Entity;
import com.stottlerhenke.simbionic.engine.debug.SB_Debugger;


public class SB_EntityRecord implements Serializable  
{
  private SB_Entity _entity;
  private int _updatePriority;
  private int _updateFreq;
  private int _maxUpdatePeriod;

  private long _nextUpdate;
  private long _lastUpdate;
  private long _totalUpdates;

  private float _avgUpdateDur;
  private long _timeBorn;
  private double _timeDebugging;
  private double _timeUpdating;

  public SB_EntityRecord(SB_Entity entity, int updateFreq, int updatePriority) {
    _entity = entity;
    _updatePriority = updatePriority;
    _updateFreq = updateFreq;
    _maxUpdatePeriod = 0;
    _nextUpdate = 0;
    _totalUpdates = 0;
    _avgUpdateDur = 0;
    _timeBorn = Math.round(System.currentTimeMillis()/1000);
    //_timeDebugging = 0;
    _timeUpdating = 0;
  }
  public void SetUpdatePriority(int priority){
    _updatePriority = priority;
  }

  public void SetUpdateFreq(int freq) throws SB_Exception{
    if(freq > SB_DefaultScheduler.LOWEST_UPDATE_FREQ)
      throw new SB_Exception("Entity " + _entity.GetId() + " update frequency exceeds the value of "
                             + SB_DefaultScheduler.LOWEST_UPDATE_FREQ);
    _updateFreq = freq;
  }

  public void Update(long currentTick, SB_Debugger debugger, SB_Logger logger) throws SB_Exception {
    long startTick = Math.round(System.currentTimeMillis()/1000);
    _entity.Update(debugger, logger);
    long endTick = Math.round(System.currentTimeMillis()/1000);

    double secsSpentUpdating = (endTick - startTick);
    _timeUpdating += secsSpentUpdating;
    _lastUpdate = currentTick;
    ++_totalUpdates;

  }
  /**
   * Sets the time of next update for this entity.
   * @param nextUpdate the tick upon which this entity will next be updated
   */
  public void SetNextUpdate(long nextUpdate) { _nextUpdate = nextUpdate; }

  /**
   * Sets the maximum number of ticks that can pass between updates
   * for this entity.
   * @param maxUpdatePeriod max number of ticks between updates of this entity
   */
  public void SetMaxUpdatePeriod(int maxUpdatePeriod) { _maxUpdatePeriod = maxUpdatePeriod; }

// Accessors

  /**
   * @return the entity associated with this record
   */
   public SB_Entity GetEntity() { return _entity; }

   /**
    * @return the entity's current update priority
    */
   public int GetUpdatePriority()  { return _updatePriority; }

   /**
    * @return the entity's current update frequency
    */
   public int GetUpdateFreq()  { return _updateFreq; }

   /*
    * @return the tick upon which this entity will next be updated
    *
    */
   public long GetNextUpdate()  { return _nextUpdate; }

   /**
    * @return the tick upon which this entity was last updated
    */
   public long GetLastUpdate()  { return _lastUpdate; }

   /**
    * @return max number of ticks between updates of this entity
    */
   public int GetMaxUpdatePeriod() { return _maxUpdatePeriod; }


 // Accessors for update statistics

   /**
    * @return the number of updates this entity has experienced
    */
   public long GetTotalUpdates() { return _totalUpdates; }
   public long GetRealTimeAlive()
   {
     return (Math.round(System.currentTimeMillis()/1000) - _timeBorn);
   }

   public double GetTimeSpentUpdating() {
     return _timeUpdating;
   }

   public double GetTimeSpentDebugging() {
     return _timeDebugging;
   }

   public double GetAverageUpdateDuration()  {
     return ((double)_timeUpdating) / _totalUpdates;
   }

   public String toString(){
     return new String (GetEntity().toString() + " [next " + _nextUpdate + ", last " + _lastUpdate
                        + ", freq " + _updateFreq  +  ", pty " + _updatePriority + "]");
   }

   /**
 	 * Trickle down finishing
 	 * 
 	 * @param book
 	 * @throws SB_Exception
 	 */
 	public void finishDeserialization(SB_SingletonBook book) throws SB_Exception
   {
 	    _entity.finishDeserialization(book);
   }
}