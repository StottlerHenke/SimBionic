package com.stottlerhenke.simbionic.engine;
import java.util.*;

public class RingListIterator implements ListIterator
{
    private ListIterator _it;
    private ArrayList _array;

    public RingListIterator(ListIterator it, ArrayList array)
    {
      _it = it;
      _array = array;
    }
    public void add(Object o)
    {
      _it.add(o);
    }
    public boolean hasNext()
    {
      return true;
    }

    public boolean hasPrevious()
    {
      return true;
    }

    public int nextIndex()
    {
      if(_it.nextIndex() == _array.size())
        return 0;

      return _it.nextIndex();
    }

    public int previousIndex()
    {
      if(_it.previousIndex() == -1)
        return _array.size() - 1;
      else
        return _it.previousIndex();
    }

    public Object next()
    {
      if(_it.nextIndex() == _array.size())
        _it = _array.listIterator();

      return _it.next();
    }

    public Object previous()
    {
      if(_it.previousIndex() == -1)
      {
        _it = _array.listIterator(_array.size() - 1);
      }

      return _it.previous();
    }

    public void remove()
    {
      _it.remove();
    }

    public void set(Object o)
    {
      _it.set(o);
    }
/*
    public Object getCurrObj()
    {
      return _array.get(_offset);
    }

    public Object getNext(int incr)
    {
      this._offset = (this._offset + incr) % _array.size();
      return getCurrObj();
    }

    public Object getNext(long incr)
    {
      this._offset = (int) (((long) this._offset + incr) % (long) _array.size());
      return getCurrObj();
    }
    */
/*
    public Object getPrevious(int incr)
    {
      this._offset = (this._offset - incr) % _array.size();
      this._offset = (this._offset < 0) ? _array.size() + this._offset : this._offset;
      return getCurrObj();
    }

    public Object getPrevious()
    {
      _Dec();
      return getCurrObj();
    }
*/
/*
    public boolean equals(RingListIterator _X)
    { return (_array == _X._array) && (_offset == _X._offset); }

    public boolean opNEQ(RingListIterator _X)

    { return (_array != _X._array) || (_offset != _X._offset); }

    public Object opASSIGN(RingListIterator _X)
    { _offset = _X._offset; _array = _X._array; return getCurrObj(); }
*/

/*
    public int offset(){ return _offset; }
/*
    private void _Inc()
    {
      _it.next();
      //_offset = (_offset < _array.size()-1) ? ++_offset : 0;
    }

    private void _Dec()
    {
      _it.previous();
      //_offset = (_offset > 0) ? --_offset : _array.size()-1;
    }
*/
  }
