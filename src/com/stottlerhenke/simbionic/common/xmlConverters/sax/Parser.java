package com.stottlerhenke.simbionic.common.xmlConverters.sax;

import java.util.Hashtable;

import com.stottlerhenke.simbionic.common.xmlConverters.DocHandler;
import com.stottlerhenke.simbionic.common.xmlConverters.QDParser;


/**
 * Superclass for all parser for the different datamontage
 * classes.
 * <br>usage
 * <ol>
 * <li>A "Parser" consumes some tags and creates some object. 
 * The class provides a do nothing implementation of the {@link DocHandler}
 * interface used by the quick-and-dirty parser {@link QDParser}
 * 
 * <li>The method {@link #isDone()} is used to indicate that the parser
 * should not process any more tags. Subclasses should set the 
 * variable {@link #isDone} to true when this is the case. 
 * <li>The first time the parser is done, the method {@link #when_done()} will be
 * run. This is where subclasses will do something with the object read by the parser
 *
 *
 */
public abstract class Parser implements DocHandler {

	/**
	 * 
	 * @param stackParser
	 * @param client - optional client to which send the parsing result
	 * @param property - he id used by the client (if any) to identify the parsing property
	 */
	public Parser(StackParser stackParser, Parser client, int property) {
		this.stackParser = stackParser;
		this.clientParser = client;
		this.clientParserPropertyID = property;
	}
	
	/**
	 * 
	 */
	final public boolean isDone() {
		if (isDone && !when_done_has_run) {
			when_done_has_run = true;
			informParsingResult();
		}
		return isDone;
	}
	
	
	/**
	 * subclasses should override this method indicating what to do
	 * with a parsing result the parser requested. The usual code is to map the property
	 * id to some field setter and call that setter with the result
	 * 
	 * @param property -- id used to identify what the result is about
	 * @param result
	 */
	protected  void receiveParsingResult(int property, Object result) {}
	
	private void informParsingResult() {
		if (clientParser != null) {
			clientParser.receiveParsingResult(clientParserPropertyID,getValue());
		}
	}
	
	/**
	 * returns the object read by the parser.
	 * @return
	 */
	abstract public Object getValue () ;
	
    //---------------------------------------------------------------------
	// DocHanndle empty implementation
	//---------------------------------------------------------------------

	public void endDocument() throws Exception {}


	public void endElement(String tag) throws Exception {}


	public void startDocument() throws Exception {}


	public void startElement(String tag, Hashtable h) throws Exception {}

	
	public void text(String str) throws Exception {}

	//
	// class fields
	// 
	
	/** the stackParsing controlling the parsers **/
	protected StackParser stackParser;
	
	/** optional client to which send the parsing result**/
	protected Parser clientParser;
	
	/** the id used by the client (if any) to identify the parsing property**/
	protected int clientParserPropertyID;
	
	/** flag indicating whether the parser is done **/
	protected boolean isDone = false;
	
	/** flag indicating whether the method when_done has been run**/
	private boolean when_done_has_run = false;
}
