package com.stottlerhenke.simbionic.engine.manager;
import java.util.ArrayList;
import java.util.ListIterator;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.SIM_Constants;
import com.stottlerhenke.simbionic.engine.RingListIterator;
import com.stottlerhenke.simbionic.engine.SB_RingArray;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;


public class SB_DefaultScheduler extends SB_SchedulingAlg 
{

  public static final int LOWEST_UPDATE_FREQ = 100;
  public static final int HIGHEST_UPDATE_FREQ = 0;
  public static final float FLT_MAX = 3.402823466e+38F;

  protected ArrayList _schedule = new ArrayList();
  protected SB_RingArray _schedData = new SB_RingArray();
  protected SB_EntityRecord _nextEntity;
  transient protected SB_Logger _logger;

  protected long _ticks;
  protected long _maxUpdatePeriod;
  protected float _updateFactor;

  public SB_DefaultScheduler(SB_Logger logger) {
    _ticks = 0;
    _maxUpdatePeriod = 1;
    _updateFactor = 1.0f/LOWEST_UPDATE_FREQ;
    _logger = logger;
  }

  protected void Reschedule(){

  }
  protected void ScheduleEntity(SB_EntityRecord entity)throws SB_Exception{
    if (entity.GetUpdateFreq() < 0)
    {
            // entity is on "pause" and should not be updated
            // until its update frequency becomes non-negative again
            return;
    }

    // compute the update window for this entity
    int maxPeriod = Math.round(_updateFactor * entity.GetUpdateFreq());
    entity.SetMaxUpdatePeriod( maxPeriod );

    // entity can be scheduled at most maxPeriod ticks from now,
    // so check each of those possible ticks and pick the best
    float bestWeight = -FLT_MAX;
    int bestTick = 0;
    RingListIterator tickItor = _schedData.head();	// start with the *next* tick
    TickData tickIt = (TickData)tickItor.next();
    for (int t=0; t < maxPeriod; t++)
    {
            // we prefer to schedule as far into the future as possible,
            // but we also want to distribute the update load evenly,
            float weight = (t/(float)maxPeriod) - tickIt._numUpdates;

            if (weight >= bestWeight)
            {
                    bestWeight = weight;
                    bestTick = t;
            }

            tickIt = (TickData)tickItor.next();
    }

    if(SIM_Constants.DEBUG_INFO_ON)
      _logger.log(entity.toString() + "scheduled at tick " + (bestTick + _ticks+1));

    // update the histogram info for the chosen tick

    RingListIterator ringIt = _schedData.head();
    TickData tick = null;
    for( int roll = 0; roll < bestTick + 1; roll++ )
      tick = (TickData) ringIt.next();

    tick._numUpdates++;

    // update the entity record itself
    long nextUpdate = _ticks + bestTick + 1;
    entity.SetNextUpdate( nextUpdate );

    if (tick._numUpdates == 1)
    {
       // this is the first update scheduled for this tick,
       // so walk down the schedule to find the correct spot
       SB_EntityRecord it;
       int i;
       for ( i = 0; i < _schedule.size(); i++)
       {
         it = (SB_EntityRecord)_schedule.get(i);
         if ((it).GetNextUpdate() > nextUpdate)
         {
            // the entity should go before this iterator
            _schedule.add(i,entity);
            tick._firstScheduledEntity = entity;
            break;
          }
          else if (it.GetNextUpdate() < nextUpdate)
          {
            // the entity should go after this iterator
            continue;
          }
          else
            throw new SB_Exception("Error in schedule tick " + nextUpdate + ".");
        }
        if (i == _schedule.size())
        {
          // insert the entity at the end of the schedule
          _schedule.add(entity);
          if(_schedule.size() > 0)
            tick._firstScheduledEntity = (SB_EntityRecord)_schedule.get(_schedule.size() - 1);
        }
    }
    else
    {
      // other updates are already scheduled at this tick,
      // so walk down the list to find where this entity
      // should fit given its update priority
      //
      // #OPT: we could walk backwards through the _schedData
      // list to find an iterator to the first scheduled entity
      // for a *previous* tick, then walk forward from there.
      SB_EntityRecord it = tick._firstScheduledEntity;
      int j = _schedule.indexOf(it);
      boolean bAdded = false;  //JRL - added this (and related code below)
      if( j >= 0)              //JRL - added this
      {
        do
        {
          if(j >= 0)
            it = (SB_EntityRecord)_schedule.get(j);

          if ((it).GetNextUpdate() != nextUpdate)
          {
            // reached the end of the update list for the desired tick,
            // so attach this entity at the end
            _schedule.add(j,entity);
            bAdded = true;
            break;
          }
          else if ((it).GetUpdatePriority() >= entity.GetUpdatePriority())
          {
            // the entity is higher priority, insert it
            _schedule.add(j,entity);
            bAdded = true;
            SB_EntityRecord newIt = entity;

            // if this is the first entity for this tick, update
            // the first-update-on-tick pointer
            if (it == tick._firstScheduledEntity)
            {
              tick._firstScheduledEntity = newIt;
            }
            break;
          }
          // the entity is lower priority, keep looking
          ++j;
        } while (j != _schedule.size());
      }

      if (!bAdded)
      {
        // insert the entity at the very end of the schedule
        _schedule.add(entity);
      }
    }

  }

  /**
   * RemoveEntity() needs to do two things:
   *
   *   1) remove the entity from _schedule (and hence from the update schedule)
   *   2) remove the entity from _schedData.
   *
   *   Only the first entity to be updated on each tick is pointed to by _schedData,
   *   though, so we only have to check the _firstScheduledEntity iterator for the tick.
   *   If the entity being removed *is* the first on the tick, move the iterator to point
   *   to the next entity to update in that tick (safely since we know from the numUpdates
   *   check that at least one other entity is scheduled for that tick).
   *
   *   Note that there is an apparent bug here in the C++ code where if a) the entity
   *   being removed *is* the first entity scheduled for the tick and b) it is the
   *   *only* entity scheduled for that tick, tick->_firstScheduledEntity is not altered.
   *   This is ok, though, because _numUpdates is decremented, and it is _numUpdates that
   *   the ScheduleEntity code checks first.
   *
   * @param entity
   */
  public void RemoveEntity(SB_EntityRecord entity) {
    SB_EntityRecord it = null;
    int i;
    for(i = 0; i < _schedule.size(); i++){
      it = (SB_EntityRecord)_schedule.get(i);
      if(it == entity) break;
    }
    if(i == _schedule.size())
      return;

    int tickOffset = Math.round(entity.GetNextUpdate() - _ticks);
    ListIterator tmpIt = _schedData.head();
    for(int k = 0; k < tickOffset - 1; k++)
    {
      tmpIt.next();
    }
    TickData tick = (TickData)tmpIt.next();
    tick._numUpdates--;

    if(tick._firstScheduledEntity == it)
    {
      if(tick._numUpdates > 0)
      {
        if( i + 1 < _schedule.size()) //JRL - added if
          tick._firstScheduledEntity = (SB_EntityRecord)_schedule.get(i + 1); //JRL - replaced ++i with i + 1
        else
          tick._firstScheduledEntity = (SB_EntityRecord)_schedule.get(0); //JRL - added this case
      }
    }
    _schedule.remove(i);
  }

  public void RescheduleEntity(SB_EntityRecord record)throws SB_Exception {
    RemoveEntity(record);
    AddEntity(record);
  }
  public void AddEntity(SB_EntityRecord entity)throws SB_Exception {
      ScheduleEntity(entity);
  }
  public void Initialize(long maxPeriod) {
    _maxUpdatePeriod = maxPeriod;
    _updateFactor = _maxUpdatePeriod / (float)LOWEST_UPDATE_FREQ;

    for(int i = 0; i < maxPeriod + 1; i++){
      TickData it = new TickData();
      it._numUpdates = 0;
      _schedData.add(it);
    }

  }
  public long GetTicks() {
    return _ticks;
  }
  public SB_EntityRecord GetFirstEntityToUpdate()throws SB_Exception {
    if(_schedule.size() == 0)
      return null;
    //int index = _schedule.indexOf(_nextEntity);
    //SB_EntityRecord entity = (SB_EntityRecord)_schedule.get(++index);
    _nextEntity = (SB_EntityRecord)_schedule.get(0);

    SB_EntityRecord entity = _nextEntity;
    if(entity.GetNextUpdate() > _ticks)
      return null;
    //int i = _schedule.indexOf(entity);
    int i = 0; //JRL - removed _schedule.indexOf(_nextEntity) and replaced with 0 since we get it in the above line!
    _schedule.remove(i);
    if(i >= 0 && _schedule.size()>0)
      _nextEntity = (SB_EntityRecord)_schedule.get(i);

    AddEntity(entity);
    return entity;
  }
  public SB_EntityRecord GetNextEntityToUpdate()throws SB_Exception {
    if(_nextEntity == null)
      return null;

    SB_EntityRecord entity = _nextEntity;

    if (entity.GetNextUpdate() > _ticks)
      return null;

    // remove this entity from the schedule
    int nextIndex = 0;
    _schedule.remove(nextIndex);

    //choose the next entity
    if(nextIndex >= 0 && _schedule.size()>0)
      _nextEntity = (SB_EntityRecord)_schedule.get(nextIndex);

    // reschedule this entity
    AddEntity(entity);

    return entity;
	}
	
  public long Tick() {
   RingListIterator it = _schedData.head();
   // advance the pointer on the histogram ring array
   _schedData.advanceHead();

   // zero out the histo data for the tail of the ring
   ((TickData)_schedData.GetAt(it))._numUpdates = 0;

   if(SIM_Constants.DEBUG_INFO_ON)
     _logger.log(toString(),SB_Logger.SCHEDULE);

   return ++_ticks;
  }
  
  public String toString()
  {
    String out = new String();
    SB_EntityRecord record;
    for (int i = 0; i < _schedule.size(); i++)
    {
      record = (SB_EntityRecord)_schedule.get(i);
      out = out +  "\r\nSCHED:\t" + record.toString();
    }
    return out;
  }
  
	public void finishDeserialization(SB_SingletonBook book) throws SB_Exception
  {
	    _logger = book.getLogger();
  }  
}