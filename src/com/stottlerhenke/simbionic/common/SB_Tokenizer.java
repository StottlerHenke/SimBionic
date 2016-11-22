package com.stottlerhenke.simbionic.common;
/**
 * This class is used to tokenize the file string when loading behaviors</p>
  */

public class SB_Tokenizer
{

  public SB_Tokenizer()
  {
  }

  /**
   * Set the string for the singleton to tokenize and the index to zero
   *
   * @param stringToTokenize
   */
  public void setString(String stringToTokenize)
  {
    _string = stringToTokenize;
    _index = 0;
  }

  /**
   *
   * @return the next token in the string, using the delimiters to find the token
   */
  public String getNextToken()
  {
    int nBeginIndex = _index;
    int nEndIndex = getNextOccurence( delimiters );

    return getString( nBeginIndex, nEndIndex );
  }

  /**
   * Changes the delimiters for tokenization.
   * @param newDelimiters the new delimiters to use
   * @return the old delimiters 
   */
  public String setDelimiters(String newDelimiters)
	{
  	String oldDelimiters = delimiters;
  	delimiters = newDelimiters;
  	return oldDelimiters;
  }

  /**
   *
   * @return read in the next token to determine the number of characters to read, read that number of characters, and return the string
   */
  public String getNextDesignatedToken()
  {
    int designatedSize = Integer.parseInt(getNextToken());

    int nBeginIndex = _index;
    int nEndIndex = _index + designatedSize;

    return getString( nBeginIndex, nEndIndex );
  }

  /**
   *
   * @return true if more tokens exist (based on the delimeters)
   */
  public boolean hasMoreTokens()
  {
    if( _index < _string.length() )
      return true;

    return false;
  }

  /**
   * Helper function for the two different types of tokenization
   *
   * @param nBeginIndex
   * @param nEndIndex
   * @return
   */
  private String getString(int nBeginIndex, int nEndIndex )
  {
    String returnString =  _string.substring(nBeginIndex, nEndIndex);

    _index = nEndIndex;
    trim();

    return returnString;
  }

  /**
   *
   * @param delimiters
   * @return the first occurence in the string of one of the characters in the delimiter string
   */
  private int getNextOccurence( String delimiters )
  {
    int returnValue = _string.length();
    int count = delimiters.length();

    int temp;
    for( int x = 0; x < count; x++ )
    {
      temp = _string.indexOf( delimiters.charAt(x), _index );

      if( temp > -1 && temp < returnValue )
        returnValue = temp;
    }

    return returnValue;
  }

  /**
   * Starting at the index, advance the pointer to the next non-tokenizing character
   */
  public void trim()
  {
    int stringLength = _string.length();

    if( _index == stringLength )
      return;

    char chIndex = _string.charAt(_index);

    while( delimiters.indexOf(chIndex) > -1 )
    {
      ++_index;

      if( _index == stringLength )
        return;

      chIndex = _string.charAt(_index);
    }
  }

  /**
   *
   * @return the current index position into the string
   */
  public int getCurrentIndexPosition()
  {
    return _index;
  }
  
  public void setCurrentIndexPosition(int newIndex)
  {
    _index = newIndex;
  }

  /**
   * Returns the next character in the string
   * @param consume if true, the index will be advanced
   */
  public char getNextChar(boolean consume)
	{
  	char c = _string.charAt( _index );
  	if (consume)
  		++_index;
  	return c;
  }
  
  
  private int    _index;
  private String _string;
  private String delimiters = " \t\n\r";
}