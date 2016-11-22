package com.stottlerhenke.simbionic.common;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.stottlerhenke.simbionic.util.CalendarUtil;

/**
 * This class implements table operations on two-dimensional data.
 *
 */
public class Table implements Cloneable{
   
   public Table(){
      table = new Vector();
   }
   
   /**
    * Constructs a Table with initial data.
    * @param aTable  two-dimensional Vector with initial table data
    */
   public Table(Vector aTable){
      table = aTable;
   }
   
   public Table(ArrayList listOfRows) {
      this(new Vector(listOfRows));
   }
   
   /**
    * Constructs a Table with initial data from a database query result.
    * @param resultSet  database query result with initial table data
    * @throws SQLException any error occurs when retrieving data from result set
    */
   public Table(ResultSet resultSet) throws SQLException
   {
      this(resultSet, null);
   }
   
   public String toString() {
      if (table==null) {
         return null;
      }
      else {
         return table.toString();
      }
   }
   /**
    * Constructs a Table with initial data from a database query result
    * and converts column values to the specified column types.
    * @param resultSet  database query result with initial table data
    * @param columnTypes   target column types
    * @throws SQLException any error occurs when retrieving data from result set
    */
   public Table(ResultSet resultSet, List columnTypes) throws SQLException
   {
      this();
      
      try
      {
         ResultSetMetaData metaData = resultSet.getMetaData();
         int numberOfColumns = metaData.getColumnCount();
         
         for (int r = 0; resultSet.next(); r ++)
         {
            // adds a new row to the table;
            Vector row = new Vector();
            table.add(row);
            
            for (int c = 0; c < numberOfColumns; c ++)
            {
               Object value = getFieldValue(resultSet, metaData, c+1,   // 1-based
                     columnTypes != null && c < columnTypes.size() ? (Class) columnTypes.get(c) : null);
               
               // adds a value to the table at (r,c);
               row.add(resultSet.wasNull() ? null : value);
            }
         }
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
         if (ex instanceof SQLException)
            throw (SQLException)ex;
      }
   }
   
   //////////////////////////////////////////////////////////////////////////
   // resultset related methods
   //
   
   private Object convertBoolean(ResultSet resultSet, 
         int columnIndex, Class targetType) throws SQLException
   {
      if (targetType == null)
         return Boolean.valueOf(resultSet.getBoolean(columnIndex));
      else if (targetType.equals(Boolean.class))
         return Boolean.valueOf(resultSet.getBoolean(columnIndex));
      else if (targetType.equals(String.class))
         return Boolean.toString(resultSet.getBoolean(columnIndex));
      else
         throw new RuntimeException("Cannot convert Boolean to " + targetType.getName());
   }
   
   private Object convertString(ResultSet resultSet, 
         int columnIndex, Class targetType) throws SQLException
   {
      if (targetType == null)
         return resultSet.getString(columnIndex);
      else if (targetType.equals(String.class))
         return resultSet.getString(columnIndex);
      else if (targetType.equals(Integer.class))
         return Integer.valueOf(resultSet.getString(columnIndex));
      else if (targetType.equals(Float.class))
         return Float.valueOf(resultSet.getString(columnIndex));
      else if (targetType.equals(Double.class))
         return Double.valueOf(resultSet.getString(columnIndex));
      else if (targetType.equals(Calendar.class))
         return CalendarUtil.parse(resultSet.getString(columnIndex));
      else
         throw new RuntimeException("Cannot convert String to " + targetType.getName());
   }
   
   private Object convertTimestamp(ResultSet resultSet, 
         int columnIndex, Class targetType) throws SQLException
   {
      if (targetType == null)
         return CalendarUtil.dateToCalendar(resultSet.getTimestamp(columnIndex));
      else if (targetType.equals(Timestamp.class))
         return resultSet.getTimestamp(columnIndex);
      else if (targetType.equals(Date.class))
         return resultSet.getTimestamp(columnIndex);
      else if (targetType.equals(Calendar.class))
         return CalendarUtil.dateToCalendar(resultSet.getTimestamp(columnIndex));
      else if (targetType.equals(Long.class))
         return Long.valueOf(resultSet.getTimestamp(columnIndex).getTime());
      else if (targetType.equals(String.class))
         return DateFormat.getDateTimeInstance().format(resultSet.getTimestamp(columnIndex));
      else
         throw new RuntimeException("Cannot convert Date to " + targetType.getName());
   }
   
   private Object convertNumeric(ResultSet resultSet, ResultSetMetaData metaData,
         int columnIndex, Class targetType) throws SQLException
   {
      if (targetType == null)
      {
         if (metaData.getScale(columnIndex) == 0)
         {
            int precision = metaData.getPrecision(columnIndex);
            if(precision < 10)
               return Integer.valueOf(resultSet.getInt(columnIndex));
            else
               return Double.valueOf(resultSet.getDouble(columnIndex));
         }
         else
            return Double.valueOf(resultSet.getDouble(columnIndex));
      }
      else if (targetType.equals(Double.class))
         return Double.valueOf(resultSet.getDouble(columnIndex));
      else if (targetType.equals(Float.class))
         return Float.valueOf(resultSet.getFloat(columnIndex));
      else if (targetType.equals(Integer.class))
         return Integer.valueOf(resultSet.getInt(columnIndex));
      else if (targetType.equals(String.class))
         return resultSet.getString(columnIndex);
      else
         throw new RuntimeException("Cannot convert Numeric to " + targetType.getName());
   }
   
   private Object convertDouble(ResultSet resultSet, 
         int columnIndex, Class targetType) throws SQLException
   {
      if (targetType == null)
         return Double.valueOf(resultSet.getDouble(columnIndex));
      else if (targetType.equals(Double.class))
         return Double.valueOf(resultSet.getDouble(columnIndex));
      else if (targetType.equals(String.class))
         return Double.toString(resultSet.getDouble(columnIndex));
      else
         throw new RuntimeException("Cannot convert Double to " + targetType.getName());
   }
   
   private Object convertFloat(ResultSet resultSet, 
         int columnIndex, Class targetType) throws SQLException
   {
      if (targetType == null)
         return Float.valueOf(resultSet.getFloat(columnIndex));
      else if (targetType.equals(Float.class))
         return Float.valueOf(resultSet.getFloat(columnIndex));
      else if (targetType.equals(Double.class))
         return Double.valueOf(resultSet.getDouble(columnIndex));
      else if (targetType.equals(String.class))
         return Float.toString(resultSet.getFloat(columnIndex));
      else
         throw new RuntimeException("Cannot convert Float to " + targetType.getName());
   }
   
   private Object convertInteger(ResultSet resultSet, 
         int columnIndex, Class targetType) throws SQLException
   {
      if (targetType == null)
         return Integer.valueOf(resultSet.getInt(columnIndex));
      else if (targetType.equals(Integer.class))
         return Integer.valueOf(resultSet.getInt(columnIndex));
      else if (targetType.equals(Float.class))
         return Float.valueOf(resultSet.getFloat(columnIndex));
      else if (targetType.equals(Double.class))
         return Double.valueOf(resultSet.getDouble(columnIndex));
      else if (targetType.equals(String.class))
         return Integer.toString(resultSet.getInt(columnIndex));
      else
         throw new RuntimeException("Cannot convert Integer to " + targetType.getName());
   }
   
   private Object getFieldValue(ResultSet resultSet, 
         ResultSetMetaData metaData, 
         int columnIndex, 
         Class targetType) throws SQLException
   {
      int type = metaData.getColumnType(columnIndex);
      switch (type)
      {
      case Types.BOOLEAN:
         return convertBoolean(resultSet, columnIndex, targetType);
      
      case Types.CHAR:
      case Types.LONGVARCHAR:
      case Types.VARCHAR:
         return convertString(resultSet, columnIndex, targetType);
         
      case Types.DATE:
      case Types.TIMESTAMP:
         return convertTimestamp(resultSet, columnIndex, targetType);
         
      case Types.NUMERIC:
         return convertNumeric(resultSet, metaData, columnIndex, targetType);
      
      case Types.DOUBLE:
         return convertDouble(resultSet, columnIndex, targetType);
      
      case Types.FLOAT:
         return convertFloat(resultSet, columnIndex, targetType);
      
      case Types.INTEGER:
         return convertInteger(resultSet, columnIndex, targetType);
         
      default:
         String typeName = metaData.getColumnTypeName(columnIndex);
         if ("LOGICAL".equals(typeName))  // MS Excel type name when column cell value is TRUE or FALSE
            return convertBoolean(resultSet, columnIndex, targetType);
         
         String errString = "Unsupported type " + typeName + " at column " + columnIndex;
         throw new SQLException(errString);
      }
   }
   
   //
   // end of resultset related methods
   //////////////////////////////////////////////////////////////////////////
   
   /**
    * Returns the two-dimensional table data.
    */
   public Vector getTableData(){
      return table;
   }
   
   /**
    * Adds a new row as the last row of the table.
    * @param newRow  new row data
    */
   public void addRow(Vector newRow){
      insertRow(newRow, getRowCount());
   }
   
   /**
    * Adds a new column as the last column of the table.
    * @param newColumn  new column data
    */
   public void addColumn(Vector newColumn){
      insertColumn(newColumn, getColumnCount());
   }
   
   /**
    * Appends the given table to the end of this table.
    * @param aTable  table to be appended
    */
   public void append(Table aTable){
      int count = aTable.getRowCount();
      for (int i = 0; i < count; i ++)
         addRow(aTable.getRow(i));
   }
   
   /**
    * Inserts a new row at the specified index and moves
    * the rows at the index and after backwards.
    * @param newRow  new row data
    * @param index   position to insert the new row
    */
   public void insertRow(Vector newRow, int index){
      table.add(index, newRow);
   }
   
   /**
    * Inserts a new column at the specified index and moves
    * the columns at the index and after backwards.
    * @param newColumn  new column data
    * @param index   position to insert the new column
    */
   public void insertColumn(Vector newColumn, int index){
      int num = newColumn.size();
      if (num > getRowCount())
         num = getRowCount();
      
      for (int r = 0; r < num; r ++){
         Vector row = getRow(r);
         row.add(index, newColumn.get(r));
      }
   }
   
   /**
    * Returns the total number of rows in this table.
    */
   public int getRowCount(){
      return table.size();
   }
   
   /**
    * Returns the total number of columns in this table.
    */
   public int getColumnCount(){
      if (getRowCount() > 0)
         return getRow(0).size();
      else
         return 0;
   }
   


   /** 
    * Returns a Vector of row numbers of all rows that contain searchValue in column searchColumn
    * Returns an empty vector if the search Value does not occur in the column
    */
   public Vector findAll(Object searchValue, int searchColumn) {
      Vector indices = new Vector();
      for (int rowIndex = 0; rowIndex < getRowCount(); rowIndex ++){
         Object value = getEntry(rowIndex, searchColumn);
         if (value == null){
            if (searchValue == null) {
               indices.add(new Integer(rowIndex));
            }
         }
         else{
            if (value.equals(searchValue)) {
               indices.add(new Integer(rowIndex));
            }
         }
      }
   
      return indices;
   }
   
   /**
    * returns  index of first row found that contains searchValue in column searchColumn.
    * returns -1 if such index is not found
    * 
    * @param searchValue
    * @param searchColumn
    * @return
    */
   public int findFirst(Object searchValue, int searchColumn) {
      for (int rowIndex = 0; rowIndex < getRowCount(); rowIndex ++){
         Object value = getEntry(rowIndex, searchColumn);
         if (value == null){
            if (searchValue == null) {
               return rowIndex;
            }
         }
         else{
            if (value.equals(searchValue)) {
               return rowIndex;
            }
         }
      }
   
      return -1;
   }
   /**
    * Returns the row data at the specified row index.
    */
   public Vector getRow(int index){
      return (Vector) table.get(index);
   }

   /**
    * Returns the column data at the specified column index.
    */
   public Vector getColumn(int index){
      Vector column = new Vector();
      for (int r = 0; r < getRowCount(); r ++)
         column.add(getEntry(r, index));
      return column;
   }
   
   /**
    * Returns the entry value at the specified row index and column index.
    */
   public Object getEntry(int rowIndex, int columnIndex){
      Vector row = getRow(rowIndex);
      return (row == null ? null : row.get(columnIndex));
   }

   /**
    * Sets and replaces the row data at the specified row index.
    * @param newRow  new row data to replace the old row data
    * @param index   the position of the row to be set
    */
   public void setRow(Vector newRow, int index){
      table.set(index, newRow);
   }
   
   /**
    * Sets and replaces the column data at the specified column index.
    * @param newColumn  new column data to replace the old column data
    * @param index   the position of the column to be set
    */
   public void setColumn(Vector newColumn, int index){
      int num = newColumn.size();
      if (num > getRowCount())
         num = getRowCount();
      
      int r;
      for (r = 0; r < num; r ++)
         setEntry(newColumn.get(r), r, index);
      
      for (; r < getRowCount(); r ++)
         setEntry(null, r, index);
   }
   
   /**
    * Sets and replaces the entry value at the specified row index and column index.
    * @param obj  new entry value to replace the old entry value
    * @param rowIndex   row position of the entry to be set
    * @param columnIndex   column position of the entry to be set
    */
   public void setEntry(Object obj, int rowIndex, int columnIndex){
      Vector row = getRow(rowIndex);
      if (row != null)
         row.set(columnIndex, obj);
   }
   
   /**
    * Removes the row at the specified row index.
    */
   public void removeRow(int index){
      table.remove(index);
   }

   /**
    * removes all rows from table that are specified in the list of 0-based row indices 
    * @param rowIndices
    */
   public void removeRows(Vector rowIndices) {
      //sort the indices first
      List<Integer> sortedIndexes = new ArrayList<Integer>(rowIndices);
      Collections.sort(sortedIndexes);
      
      //remove the table indexes
      for (int i=0 , n=sortedIndexes.size() ; i < n ; i++) {
         removeRow(sortedIndexes.get(i)- i);//-i since the index changes after removing some data
      }
   }
   /**
    * Removes the column at the specified column index.
    */
   public void removeColumn(int index){
      for (int r = getRowCount()-1; r >= 0; r--){
         Vector row = getRow(r);
         row.remove(index);
      }
   }
   
   /**
    * Returns a subset of this table. The columnIndices is a list of Integer objects
    * that specify column indices of the columns to be picked to create the subset.
    * @param columnIndices column indices of the columns to be picked to create the subset
    * @return a subset of this table
    */
   public Table getSubTable(List columnIndices){
      Vector data = new Vector();
      for (int rowIndex = 0; rowIndex < getRowCount(); rowIndex ++){
         Vector row = new Vector();
         data.add(row);
         for (Iterator it = columnIndices.iterator(); it.hasNext();){
            int columnIndex = ((Integer)it.next()).intValue();
            row.add(getEntry(rowIndex, columnIndex));
         }
      }
      return (new Table(data));
   }
   
   /**
    * Returns a subset of this table. The column 0 to the column specified by the
    * lastColumnIndex are picked to create the subset.
    * @param lastColumnIndex index of the last column to be picked
    * @return a subset of this table
    */
   public Table getSubTable(int lastColumnIndex){
      Vector data = new Vector();
      if (lastColumnIndex > getColumnCount() - 1)
         lastColumnIndex = getColumnCount() - 1;
      
      for (int rowIndex = 0; rowIndex < getRowCount(); rowIndex ++){
         Vector row = new Vector();
         data.add(row);
         for (int columnIndex = 0; columnIndex <= lastColumnIndex; columnIndex ++)
            row.add(getEntry(rowIndex, columnIndex));
      }
      
      return (new Table(data));
   }
   
   public boolean isEmpty () {
      return (table == null) || (table.isEmpty());
   }
   
   public Object clone(){
      return (new Table((Vector)table.clone()));
   }

   /**
    * Returns the index of the first occurrance of the row which contains 
    * the value at the specified columnIndex that equals the match object, 
    * starting at row 0.
    * 
    * @param columnIndex column index to get the table value to compare
    * @param match the object to compare to
    * @return row index, -1 if not found
    */
   public int getRowIndex(int columnIndex, Object match){
      return getRowIndex(columnIndex, match, 0);
   }

   /**
    * Returns the index of the first occurrance of the row which contains 
    * the value at the specified columnIndex that equals the match object, 
    * starting at the specified fromRowIndex.
    * 
    * @param columnIndex column index to get the table value to compare
    * @param match the object to compare to
    * @param fromRowIndex the index from which to start the search
    * @return row index, -1 if not found
    */
   public int getRowIndex(int columnIndex, Object match, int fromRowIndex){
      for (int rowIndex = fromRowIndex; rowIndex < getRowCount(); rowIndex ++){
         Object value = getEntry(rowIndex, columnIndex);
         if (value == null){
            if (match == null)
               return rowIndex;
         }
         else{
            if (value.equals(match))
               return rowIndex;
         }
      }
      return -1;
   }
   
   /**
    * Returns the string that displays the data in this table in a HTML table.
    * @param htmlAttributes   attributes in the table HTML tag, e.g., celling, padding, width, borderWidth, etc.
    * @param columnHeaders column header strings
    * @return  a HTML table string
    */
   public String createHTMLTable(String htmlAttributes, List columnHeaders)
   {
      StringBuffer sb = new StringBuffer();
      
      sb.append("<table");
      if (htmlAttributes != null && htmlAttributes.length() > 0)
      {
         sb.append(" ");
         sb.append(htmlAttributes);
      }
      sb.append(">\n");
      
      // Append <th> tags and data for the column headers 
      if (columnHeaders != null && columnHeaders.size() > 0){
         sb.append("\t<tr class=\"TableHeader\" valign=top>\n");
         for (int c = 0; c < columnHeaders.size(); c ++){
            sb.append("\t\t<th>");
            sb.append(columnHeaders.get(c));
            sb.append("</th>\n");
         }
         sb.append("\t</tr>\n");
      }
         
      // Append <tr> and <td> tags and data for the actual table data
      for (int r = 0; r < getRowCount(); r ++){
         sb.append("\t<tr class=\"TableData\" valign=top>\n");
         for (int c = 0; c < getColumnCount(); c ++){
            sb.append("\t\t<td>");
            sb.append(getEntry(r, c));
            sb.append("</td>\n");
         }
         sb.append("\t</tr>\n");
      }
      
      sb.append("</table>");
      
      return sb.toString();
   }
   
   private Vector table;
   
   /*
   static public void main(String[] args) throws Exception
   {
      Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
      java.sql.Connection con= java.sql.DriverManager.getConnection("jdbc:odbc:Driver={Microsoft Excel Driver (*.xls)};DBQ=data.xls");
      java.sql.Statement stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery("Select * from [Sheet1$]");
      
      TaskGuideTable table = new TaskGuideTable(rs);
      for (int r = 0; r < table.getRowCount(); r ++)
      {
         for (int c = 0; c < table.getColumnCount(); c ++)
            System.out.print(table.getEntry(r, c)+"["+table.getEntry(r, c).getClass().getName()+"]\t");
         System.out.println();
      }
      
      System.out.println("**********************");
      
      List targetTypes = new Vector();
      targetTypes.add(String.class);
      targetTypes.add(String.class);
      targetTypes.add(Double.class);
      targetTypes.add(Boolean.class);
      targetTypes.add(Long.class);
      rs = stmt.executeQuery("Select * from [Sheet1$]");
      TaskGuideTable table2 = new TaskGuideTable(rs, targetTypes);
      for (int r = 0; r < table2.getRowCount(); r ++)
      {
         for (int c = 0; c < table2.getColumnCount(); c ++)
            System.out.print(table2.getEntry(r, c)+"["+table2.getEntry(r, c).getClass().getName()+"]\t");
         System.out.println();
      }
      
      System.exit(0);
   }
   */

}

