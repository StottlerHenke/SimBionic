package com.stottlerhenke.simbionic.common.xmlConverters.sax;

import java.util.ArrayList;
import java.util.Hashtable;

import com.stottlerhenke.simbionic.common.xmlConverters.DocHandler;



/**
 * The stack parser relies any tag information to the {@link Parser} in
 * the top of a stack. Parsers are responsible to add other parsers to the
 * top of the stacks. However, the StackParser is the only one removing
 * elements from the stack, whenever the top element in the stack is done 
 * {@link Parser#isDone()}
 *
 */
public class StackParser implements DocHandler {
	ArrayList<Parser> stack = new ArrayList<Parser>();
	
	public StackParser () {
	}
	
	public void addParser(Parser topParser) {
		stack.add(0, topParser);
	}

	
	

	public void startElement(String tag, Hashtable h) throws Exception {
		if (stack.isEmpty()) return;//error?
		
		Parser topParser = stack.get(0);
		topParser.startElement(tag,h);
	}
	
	public void text(String str) throws Exception {
		if (stack.isEmpty()) return;//error?
		
		Parser topParser = stack.get(0);
		topParser.text(str);
	}
	
	public void endElement(String tag) throws Exception {
		if (stack.isEmpty()) return;//error?
		
		Parser topParser = stack.get(0);
		topParser.endElement(tag);
		if (topParser.isDone()) {
			stack.remove(0);
		}

	}

	public void startDocument() throws Exception {}

	public void endDocument() throws Exception {}

	

}
