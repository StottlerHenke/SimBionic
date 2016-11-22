package com.stottlerhenke.simbionic.engine;
import java.util.*;


public class SB_RingArray extends ArrayList
{
  private int _head;
  private int _tail;

  public SB_RingArray()
  {
    super();
    _head = 0;
    _tail = 0;
  }
  public ListIterator listIterator()
  {
    return new RingListIterator(listIterator(_head), this);
  }

  public RingListIterator head()
  {
    return new RingListIterator(listIterator(_head), this);
  }

  public RingListIterator rhead()
  {
    return new RingListIterator(listIterator(_tail), this);
  }

  public RingListIterator advanceHead()
  {
    ++_head;

    if( _head == size() )
      _head = 0;

    _tail = (_head == 0) ? size() -1 : _head - 1;

    return head();
  }

  public Object GetAt(RingListIterator it)
  {
    int i = it.nextIndex();
    int currIndex = i;
    if(i == 0)
      currIndex = this.size()-1;
    else
      --currIndex;
    return this.get(currIndex);
  }
}