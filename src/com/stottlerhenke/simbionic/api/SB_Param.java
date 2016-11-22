package com.stottlerhenke.simbionic.api;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.debug.MFCSocketInputStream;
import com.stottlerhenke.simbionic.common.debug.MFCSocketOutputStream;
import com.stottlerhenke.simbionic.engine.SB_SimInterface;

/**
 * Represents a single parameter passed to an action or function
 * from the Simbionic runtime engine.
 *
 */

public class SB_Param implements Serializable, Comparable
{

  private SB_ParamType type;
  private Object data = null;
  
  public boolean _isBreakpoint = false;

  public static final long  INVALID_ID = -1;
  
  /**
   * Constructor.  Creates an SB_Param of type kSB_Invalid.
   */
  public SB_Param(){
    type = SB_ParamType.kSB_Data;
  }
 
  /**
   * Constructor.  Creates an SB_Param of type kSB_Data.
   * @param val value for the new parameter
   */
  public SB_Param(Object val){
     type = SB_ParamType.kSB_Data;
     data = val;
  }

  /**
   * Copy constructor.
   */
  public SB_Param(SB_Param param) throws SB_Exception
  {
	copy(param);
  }

  //Accessors

  /**
   * Gets the type of this parameter.
   * @return the type of this parameter
   */
  public SB_ParamType getType() { return type;}
  
  /**
   * Gets the value of this parameter. 
   * 
   * This works if the param type is float, entity, integer, string, or boolean.
   * Otherwise an exception is thrown.
   * 
   * @return the floating-point value of this parameter
   */
  public float getFloat() throws SB_Exception
  { 
	  if( data != null && data instanceof Number ) //Byte, Double, Float, Integer, Long, Short
	    return ((Number) data).floatValue();

      try {
          if( data != null && data instanceof String)
            return Float.parseFloat((String)data);    
      } catch (NumberFormatException e) {
          // logged below
      }
      
    throw new SB_Exception("Could not convert variable to float");
  }
  
  /**
   * Gets the value of this parameter.
   * @return the integer value of this parameter
   */
  public int getInteger() throws SB_Exception
  { 
    if( data != null && data instanceof Number ) //Byte, Double, Float, Integer, Long, Short
      return ((Number) data).intValue();

    try {
        if( data != null && data instanceof String)
            return Integer.parseInt((String)data);    
    } catch (NumberFormatException e) {
        // logged below
    }

    throw new SB_Exception("Could not convert variable to integer");    
  }
  
  /**
   * Gets the value of this parameter.
   * @return the entity value of this parameter
   */
  public long getEntity() throws SB_Exception
  { 
    if( data != null && data instanceof Number ) //Byte, Double, Float, Integer, Long, Short
      return ((Number) data).longValue();
    
    try {
        if( data != null && data instanceof String)
            return Long.parseLong((String)data);
    } catch (NumberFormatException e) {
        // logged below
    }
    
    throw new SB_Exception("Could not convert variable to long");   
  }
  
  /**
   * Gets the value of this parameter.
   * @return the boolean value of this parameter
   */
  public boolean getBoolean() throws SB_Exception
  { 
	  if( data != null && data instanceof Number ) //Byte, Double, Float, Integer, Long, Short
	    return ((Number) data).intValue() == 0.0 ? false : true;
	  
	  if( data != null && data instanceof Boolean)
	    return ((Boolean)data).booleanValue();
    
      if( data != null && data instanceof String)
        return Boolean.valueOf((String)data).booleanValue();
      
    throw new SB_Exception("Could not convert variable to boolean"); 
  }
  /**
   * Gets the value of this parameter.
   * @return the string value of this parameter
   */
  public String getString()
  throws SB_Exception
  { 
    if(data != null)
      return data.toString();
    
    throw new SB_Exception("Could not convert variable to string"); 
  }

  /**
   * Gets the value of this parameter.
   * 
   * @return the data value of this parameter
   */
  public Object getData()
  { 
    return data;
  }

  /**
   * Internal accessor only
   * @return the internal array for this parameter. Each object is wrapped in an SB_Param
   */
  public List getArray() throws SB_Exception
  {
    if( data != null && data instanceof List )
  	{
      return (List) data;
  	}

    throw new SB_Exception("Could not convert variable to array"); 
  }

  /**
   * Get a list of Java objects (i.e. not wrapped in SB_Param)
   * 
   * Call setData() on this SB_Param with the updated list to keep 
   * changes made to the list.
   * 
   * @return a list of the underlying java objects
   */
  public List getObjectArray() throws SB_Exception
  {
    if( data != null && data instanceof List )
	{
        return SB_SimInterface.UnwrapArrayObjects((List) data); 
	}
     
    throw new SB_Exception("Could not convert variable to array"); 
  }
  
  //Setters

  /**
   * Sets the type of this parameter.
   *
   * @param ttype the new type for this param
   */
  public void setType(SB_ParamType ttype)
  {
    type = ttype;
  }
  /**
   * Sets the value of this parameter.
   * @param value the new string value for this parameter
   */
  public void setString(String value){
    data = value;
  }
  /**
   * Sets the value of this parameter.
   * @param val the new floating-point value for this parameter
   */
  public void setFloat(float val){
    data = new Float(val);
  }
  /**
   * Sets the value of this parameter.
   * @param val the new integer value for this parameter
   */
  public void setInteger(int val){
    data = new Integer(val);
  }
  /**
   * Sets the value of this parameter.
   * @param val the new boolean value for this parameter
   */
  public void setBoolean(boolean val){
    data = new Boolean(val);
  }

  /**
   * Sets the value of this parameter.
   * @param val the new data value for this parameter
   */
  public void setData(Object val){
    type = SB_ParamType.kSB_Data;
    data = val;
  }
 

  /**
   * Makes this parameter a copy of the given SB_Param.
   * Copies of arrays made with this method are shallow - the objects in the arrays are not copied.
   * 
   * @param param the parameter to copy
   * @see deepCopy
   * @see clone
   */
  public void copy(SB_Param param) throws SB_Exception
  {
    type = param.type;
    data = param.data;
  }

  /**
   * Makes a clone of the given SB_Param.
   * Copies of SB array parameters made with this method are deep - 
   * all the objects in the arrays are copied.
   * Copies of SB_Param that are marked as data are not copied at all.
   * This includes lists of all sorts.
   * 
   * @param param the parameter to copy
   * @see deepCopy
   * @see clone
   * @return the clone
   */
  public SB_Param cloneParam() throws SB_Exception
  {
    	SB_Param retVal = new SB_Param();
   	retVal.type = type;
  	   retVal.copy(this);
    
     return retVal;
  }

  /**
   * Create a new list of the same type as the given list
   * @param toCopy
   * @return
   * @throws SB_Exception
   */
  static public List createNewList(List toCopy) throws SB_Exception
  {
	  List list = null;
	  try
	  {
		  list = (List) toCopy.getClass().newInstance();  
	  }
      catch(InstantiationException ex)
      {
          throw new SB_Exception("Null constructor missing for list type: " + toCopy.getClass().getName(), ex);
      }
      catch(IllegalAccessException ex)
      {
          throw new SB_Exception("Null constructor for list is not accessible (e.g. private)", ex);
      }
      
      return list;
  }
  
   
  public boolean equals(Object o2)
  {
	  SB_Param param2 = (SB_Param) o2;
	  try
	  {
	     if (type.equals(SB_ParamType.kSB_Data) && param2.type.equals(SB_ParamType.kSB_Data))
           return (getData() == null ? param2.getData() == null : getData().equals(param2.getData()));
		  else
			  return false;
	  }
	  catch (Exception ex)
	  {
		  return false;
	  }
  }
  
  public int compareTo(Object o2)
  {
	  SB_Param param2 = (SB_Param) o2;
	  try
	  {
	     if (type.equals(SB_ParamType.kSB_Data) && param2.type.equals(SB_ParamType.kSB_Data))
        {
           if (getData() == null && param2.getData() == null)
              return 0;
           else if (getData() == null)
              return -1;
           else if (param2.getData() == null)
              return 1;
           else if (getData() instanceof Comparable)
              return ((Comparable)getData()).compareTo(param2.getData());
           else
              throw new SB_Exception("Cannot compare two data.");
        } 
	     else
           throw new SB_Exception("Cannot compare type " + type + " and type " + param2.type);
	  }
	  catch (SB_Exception ex)
	  {
		  throw new RuntimeException(ex);
	  }
  }
  
  /**
   *
   * @return a string representation of this variable. Class name is returned on error
   */
  public String toString()
  {
    if (data == null)
      return "NULL";
    
    if( type == SB_ParamType.kSB_Invalid )
       return "<Invalid>";
    
     
    return data.toString();
    
  }

  /**
   *Return a string of an array where every element in the array is put in quotes
   *
   * @return a string representation of this variable.
   */
  public String toSerializeString() throws SB_Exception
  {
    StringBuffer buff = new StringBuffer();
    buff.append(this.toString());

     return buff.toString();
  }

  public void deserialize(MFCSocketInputStream in, SB_Logger logger) throws java.io.IOException
  {
    int type = in.readMFCInt(); //the parameter type
    if( type == SB_ParamType.kSB_Data.getState() )
    {
       // Note: see note in serialize method
       String datum = in.readPascalString();
      this.setData(datum);  
    } 
    else
      logger.log("Don't know how to deserialize parameter of type: " + type, SB_Logger.ERROR);
  }

  public void serialize(MFCSocketOutputStream out, SB_Logger logger) throws IOException, SB_Exception
  {
    int type = getType().getState();

    out.writeMFCInt(type); //Write out the type first
    
    if( type == SB_ParamType.kSB_Data.getState() )
    {
       // Note: due to SB_Variable cleanup and using SB_Param(Object) constructor,
       // type is always kSB_Data.  so we want to just call toString on the data object
      if (this.getData() == null)
      	out.writeMFCInt( 0 );
      else {
         out.writePascalString(this.getData().toString());
      }
    }
    else
      logger.log("Don't know how to serialize parameter of type: " + type, SB_Logger.ERROR);
  }

  // -------------------- Array Functionality to match that of actions/predicates ----------------------- //

  /**
   * @deprecated
   * @see GetArrayEntry
   */
  public SB_Param getParamFromArray(int index) throws SB_Exception
  {
    return getArrayEntry(index);
  }

  /**
   * The array element with the specified index.
   *
   * @param index index into the array
   * @return the SB_Param object indexed
   */
  public SB_Param getArrayEntry(int index) throws SB_Exception
  { 
      if(!SB_Config.USE_SB_PARAM_ARRAYS)
          return SB_SimInterface.ConvertObjectToSB_Param(getArray().get(index));
      else
          return (SB_Param) getArray().get(index);
  }

  /**
   * @deprecated
   * @see ArrayDelete
   */
  public void removeParamFromArray(int index) throws SB_Exception
  {
    arrayDelete(index);
  }

  /**
   * Removes the specified array element from the array, causing
   * all elements after it in the array to be shifted forward
   * (reducing their indices by one).
   *
   * @param index item to be removed from the array.
   */
  public void arrayDelete(int index) throws SB_Exception
  {
   getArray().remove(index);
  }

  /**
   * @deprecated
   * @see ArrayDAdd
   */
  public void addParamToArray(SB_Param value) throws SB_Exception
  {
    arrayAdd(value);
  }

  /**
   * Appends the specified value to this array parameter.
   * @param value the new value to append to the array
   */
  public void arrayAdd(SB_Param value) throws SB_Exception
  {
      if(!SB_Config.USE_SB_PARAM_ARRAYS)
      {
          getArray().add(value.getData());
      }
      else
      {
	    SB_Param paramCopy = new SB_Param();
	    paramCopy.copy(value);
	    getArray().add( paramCopy );
      }
  }

  /**
   * Inserts the specified value to this array parameter.
   * @param value the new value to append to the array
   */
  public void arrayInsert(int index, SB_Param value) throws SB_Exception
  {
      if(!SB_Config.USE_SB_PARAM_ARRAYS)
      {
          getArray().add(index, value.getData());
      }
      else
      {
	    SB_Param paramCopy = new SB_Param();
	    paramCopy.copy(value);
	
	    getArray().add(index, paramCopy);
      }
  }

  /**
   * @deprecated
   * @see ArraySet
   */
  public void setParamInArray(int index, SB_Param value) throws SB_Exception
  {
    arraySet(index, value);
  }

  /**
   * Sets the array element at the specified index to be
   * a copy of the given value.
   *
   * @param index index into the array
   * @param value SB_Param to put in the array
   */
  public void arraySet(int index, SB_Param value) throws SB_Exception
  {
    setMinimumArraySize(index+1);

    if(!SB_Config.USE_SB_PARAM_ARRAYS)
    {
        getArray().set(index, value.getData());
    }
    else
    {
	    SB_Param paramCopy = new SB_Param();
	    paramCopy.copy(value);
	    getArray().set(index, paramCopy);
    }
  }

  /**
   * Sets the minimum size of the array.  If the current size
   * of the array is greater than the specified minimum size,
   * this does nothing.
   *
   * @param newMinimumSize desired minimum size for the array
   */
  public void setMinimumArraySize(int newMinimumSize) throws SB_Exception
  {
    if (newMinimumSize <= getArraySize())
      return;

    int oldSize = getArraySize();
    for (int i = oldSize; i < newMinimumSize; i++)
        getArray().add(i, new SB_Param());
  }

  /**
   * Sets the size of the array.  If the new size is less
   * than the old size, elements with index >= newSize are
   * discarded.
   *
   * @param newSize the new size of the array
   */
  public void setArraySize(int newSize)throws SB_Exception
  {
      List list = getArray();
    
		int oldSize = getArraySize();
		if (oldSize < newSize) 
		{
		  for (int i = oldSize; i < newSize; i++)
		  {
		      if(!SB_Config.USE_SB_PARAM_ARRAYS)
		          list.add(i, null);
		      else
		          list.add(i, new SB_Param());
		  }
		}
		else
		if(oldSize > newSize)
		{
		  for(int i = newSize; i < oldSize; i++)
		  {
		    list.remove(list.size()-1);
		  }
		}
  }
 

  /**
   * Gets the current size of the array.
   * @return the current size of the array
   */
  public int getArraySize() throws SB_Exception
  {
      return getArray().size();
  }
  
  public int findArrayFirst(SB_Param matchValue) throws SB_Exception
  {
	  for (int i = 0; i < getArraySize(); i ++)
	  {
		  SB_Param entry = getArrayEntry(i);
		  if (entry.equals(matchValue))
			  return i;
	  }
	  return -1;
  }
  
  public List findArrayAll(SB_Param matchValue) throws SB_Exception
  {
	  List indices = new ArrayList();
	  for (int i = 0; i < getArraySize(); i ++)
	  {
		  SB_Param entry = getArrayEntry(i);
		  if (entry.equals(matchValue))
			  indices.add(new Integer(i));
	  }
	  return indices;
  }
  
  public SB_Param getArraySubset(SB_Param positions) throws SB_Exception
  {
	  SB_Param subset = new SB_Param(new ArrayList());
	  for (int i = 0; i < positions.getArraySize(); i ++)
		  subset.arrayAdd(getArrayEntry(positions.getArrayEntry(i).getInteger()));
	  return subset;
  }

  // -------------------- Table Functionality to match that of actions/predicates ----------------------- //
   /**
   * Gets the column size of a two-dimensional array (aka table).
   *
   * @return the current column size of the first row of the array
   */
  public int getTableColumnSize() throws SB_Exception
  {
    if (getArraySize() <= 0)
      return(0);
    else
      return getArrayEntry(0).getArraySize();
  }

  public int getTableRowSize() throws SB_Exception
  {
    return getArraySize();
  }

  public SB_Param getTableRow(int rowIndex) throws SB_Exception
  {
    SB_Param row = getArrayEntry(rowIndex);
    return row;
  }

  /**
   * Gets the value at the specified row and column of the array.
   * @param rowIndex the row index of the desired element
   * @param columnIndex the column index of the desired element
   * @return the value at the specified row and column
   */
  public SB_Param getTableEntry(int rowIndex, int columnIndex) throws SB_Exception
  {
    SB_Param row = getArrayEntry(rowIndex);
    return(row.getArrayEntry(columnIndex));
  }


  public void tableSetRow(int rowIndex, SB_Param rowArray) throws SB_Exception
  {
    arraySet(rowIndex, rowArray);
  }

  public void tableSetColumn(int columnIndex, SB_Param columnArray) throws SB_Exception
  {
    int nCount = getArraySize();
    for( int x = 0; x < nCount; x++ )
    {
      SB_Param row = getArrayEntry(x);
      row.arraySet( columnIndex, columnArray.getArrayEntry(x) );
    }
  }

  public void tableInsertRow(int rowIndex, SB_Param rowArray) throws SB_Exception
  {
    arrayInsert(rowIndex, rowArray);
  }

  public void tableInsertColumn(int columnIndex, SB_Param columnArray) throws SB_Exception
  {
    int nCount = getArraySize();
    for( int x = 0; x < nCount; x++ )
    {
      SB_Param row = getArrayEntry(x);
      row.arrayInsert( columnIndex, columnArray.getArrayEntry(x) );
    }
  }

  public void tableAddRow(SB_Param rowArray) throws SB_Exception
  {
    tableInsertRow(getTableRowSize(), rowArray);
  }

  public void tableAddColumn(SB_Param columnArray) throws SB_Exception
  {
    tableInsertColumn(getTableColumnSize(), columnArray);
  }

  public void tableDeleteRow(int rowIndex) throws SB_Exception
  {
    arrayDelete( rowIndex );
  }

  public void tableDeleteColumn(int columnIndex) throws SB_Exception
  {
    int nCount = getArray().size();
    for( int x = 0; x < nCount; x++ )
    {
      SB_Param row = getArrayEntry(x);
      row.arrayDelete(columnIndex);
    }
  }
   /**
   * @deprecated
   * @see GetTableEntry
   */
  public SB_Param getParamFromTable(int rowIndex, int columnIndex) throws SB_Exception
  {
    return getTableEntry(rowIndex, columnIndex);
  }


  
  public int findTableFirstMatchingRow(int selectionColumn, SB_Param matchValue) throws SB_Exception
  {
	  int rowSize = getTableRowSize();
	  for (int rowIndex = 0; rowIndex < rowSize; rowIndex ++)
	  {
		  SB_Param entry = getTableEntry(rowIndex, selectionColumn);
		  if (entry.equals(matchValue))
			  return rowIndex;
	  }
	  return -1;
  }
  
  public List findTableAllMatchingRows(int selectionColumn, SB_Param matchValue) throws SB_Exception
  {
	  List indices = new ArrayList();
	  int rowSize = getTableRowSize();
	  for (int rowIndex = 0; rowIndex < rowSize; rowIndex ++)
	  {
		  SB_Param entry = getTableEntry(rowIndex, selectionColumn);
		  if (entry.equals(matchValue))
			  indices.add(new Integer(rowIndex));
	  }
	  return indices;
  }
  
  public int findTableFirstMatchingRow2(int selectionColumn1, SB_Param matchValue1,
		  int selectionColumn2, SB_Param matchValue2) throws SB_Exception
  {
	  int rowSize = getTableRowSize();
	  for (int rowIndex = 0; rowIndex < rowSize; rowIndex ++)
	  {
		  SB_Param entry1 = getTableEntry(rowIndex, selectionColumn1);
		  SB_Param entry2 = getTableEntry(rowIndex, selectionColumn2);
		  if (entry1.equals(matchValue1) && entry2.equals(matchValue2))
			  return rowIndex;
	  }
	  return -1;
  }
  
  public List findTableAllMatchingRows2(int selectionColumn1, SB_Param matchValue1,
		  int selectionColumn2, SB_Param matchValue2) throws SB_Exception
  {
	  List indices = new ArrayList();
	  int rowSize = getTableRowSize();
	  for (int rowIndex = 0; rowIndex < rowSize; rowIndex ++)
	  {
		  SB_Param entry1 = getTableEntry(rowIndex, selectionColumn1);
		  SB_Param entry2 = getTableEntry(rowIndex, selectionColumn2);
		  if (entry1.equals(matchValue1) && entry2.equals(matchValue2))
			  indices.add(new Integer(rowIndex));
	  }
	  return indices;
  }
  
  public int findTableFirstMatchingRowN(SB_Param selectionColumns, SB_Param matchValues) throws SB_Exception
  {
	  int rowSize = getTableRowSize();
	  for (int rowIndex = 0; rowIndex < rowSize; rowIndex ++)
	  {
		  boolean allMatch = true;
		  for (int i = 0; i < selectionColumns.getArraySize(); i ++)
		  {
			  SB_Param entry = getTableEntry(rowIndex, selectionColumns.getArrayEntry(i).getInteger());
			  if (!entry.equals(matchValues.getArrayEntry(i)))
			  {
				  allMatch = false;
				  break;
			  }
		  }
		  
		  if (allMatch)
			  return rowIndex;
	  }
	  return -1;
  }
  
  public List findTableAllMatchingRowsN(SB_Param selectionColumns, SB_Param matchValues) throws SB_Exception
  {
	  List indices = new ArrayList();
	  int rowSize = getTableRowSize();
	  for (int rowIndex = 0; rowIndex < rowSize; rowIndex ++)
	  {
		  boolean allMatch = true;
		  for (int i = 0; i < selectionColumns.getArraySize(); i ++)
		  {
			  SB_Param entry = getTableEntry(rowIndex, selectionColumns.getArrayEntry(i).getInteger());
			  if (!entry.equals(matchValues.getArrayEntry(i)))
			  {
				  allMatch = false;
				  break;
			  }
		  }
		  
		  if (allMatch)
			  indices.add(new Integer(rowIndex));
	  }
	  return indices;
  }
  
  public SB_Param getTableRowSubset(SB_Param rowIndices) throws SB_Exception
  {
	  SB_Param subset = new SB_Param(new ArrayList());
	  for (int i = 0; i < rowIndices.getArraySize(); i ++)
		  subset.tableAddRow(getTableRow(rowIndices.getArrayEntry(i).getInteger()));
	  return subset;
  }
  
  public SB_Param getTableColumnSubset(SB_Param columnIndices) throws SB_Exception
  {
	  int rowSize = getTableRowSize();
	  SB_Param subset = new SB_Param(new ArrayList());
	  for (int rowIndex = 0; rowIndex < rowSize; rowIndex ++)
	  {
		  SB_Param row = new SB_Param(new ArrayList());
		  for (int i = 0; i < columnIndices.getArraySize(); i ++)
			  row.arrayAdd(getTableEntry(rowIndex, columnIndices.getArrayEntry(i).getInteger()));
		  if (row.getArraySize() > 0)
			  subset.tableAddRow(row);
	  }
	  return subset;
  }
  
  public void sortTable(int sortColumnIndex) throws SB_Exception
  {
	  final int columnIndex = sortColumnIndex;
	  Collections.sort(getArray(), new Comparator(){
		  public int compare(Object o1, Object o2)
		  {
			  try
			  {
				  SB_Param entry1 = null;
				  SB_Param entry2 = null;
				  if (SB_Config.USE_SB_PARAM_ARRAYS)
				  {
					  entry1 = ((SB_Param)o1).getArrayEntry(columnIndex);
					  entry2 = ((SB_Param)o2).getArrayEntry(columnIndex);
				  }
				  else
				  {
					  // o1, o2 should be list in this case
					  entry1 = new SB_Param(o1).getArrayEntry(columnIndex);
					  entry2 = new SB_Param(o2).getArrayEntry(columnIndex);
				  }
				  return entry1.compareTo(entry2);
			  }
			  catch (SB_Exception ex)
			  {
				  throw new RuntimeException(ex);
			  }
		  }
	  });
  }
  
}