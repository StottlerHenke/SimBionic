package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Dimension;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 * Collects a set of useful static methods that perform
 * common Swing GUI tasks.
 *
 */
public class UIUtil
{
	  /** default border around components */
    public static final int DEFAULT_BORDER = 6;

    /** default spacing between buttons */
    public static final int DEFAULT_STRUT = 6;
    
    /**
     * Takes the given list of buttons and creates a horizontal
     * panel containing them, with standard spacing and size.
     * @param buttons the list of buttons in the row
     * @return the button row
     */
    public static JComponent createButtonRow(List buttons)
    {
        equalizeSizes(buttons);
        Box row = new Box(BoxLayout.X_AXIS);
        row.setBorder(BorderFactory.createEmptyBorder(DEFAULT_BORDER,0,DEFAULT_BORDER,0));
        
        Iterator buttonIt = buttons.iterator();
        while (buttonIt.hasNext())
        {
            row.add((JComponent)buttonIt.next());
            if (buttonIt.hasNext())
                row.add(Box.createRigidArea(new Dimension(DEFAULT_STRUT,0)));  // leave space between buttons
        }
        return row;
    }
    
    /**
     * Takes the given list of buttons and creates a vertical
     * panel containing them, with standard spacing and size.
     * @param buttons the list of buttons in the column
     * @return the button column
     */
    public static JComponent createButtonColumn(List buttons)
    {
        equalizeSizes(buttons);
        Box col = new Box(BoxLayout.Y_AXIS);
        col.setBorder(BorderFactory.createEmptyBorder(0,DEFAULT_BORDER,0,DEFAULT_BORDER));
        
        Iterator buttonIt = buttons.iterator();
        while (buttonIt.hasNext())
        {
            col.add((JComponent)buttonIt.next());
            if (buttonIt.hasNext())
                col.add(Box.createRigidArea(new Dimension(0,DEFAULT_STRUT)));  // leave space between buttons
        }
        col.add(Box.createVerticalGlue());
        return col;
    }
    
    /**
     * Takes a list of JComponents and sets them all to the
     * size of the largest component.
     * @param items the list of items to equalize
     */
    public static void equalizeSizes(List items)
    {
        Dimension maxDim = new Dimension(0,0);
        Iterator itemIt = items.iterator();
        while (itemIt.hasNext())
        {
            Dimension dim = ((JComponent)itemIt.next()).getPreferredSize();
            double height = Math.max(maxDim.getHeight(),dim.getHeight());
            double width = Math.max(maxDim.getWidth(),dim.getWidth());
            maxDim.setSize(width,height);
        }
        
        itemIt = items.iterator();
        while (itemIt.hasNext())
        {
            JComponent c = (JComponent)itemIt.next(); 
            c.setPreferredSize(maxDim);
            c.setMaximumSize(maxDim);
        }
    }
    
    
    
    /**
     * wrap the text within a single cell HTML
     * table, and set the width of the table column to a default value.
     * This method is used to wrap tooltip text.
     * We do NOT integrate the method into Slot.setTooltip
     * to allow finer control of the tooltip line break.
     * Also, adding a 'width' to Slot is not very meaningful
     * as width is a rendering parameter.
     * @param message text to be wrapped
     * @param maxNumChar max number of char before the wrapping occurs
     * @param width max width of the tooltip
     * @return wrappedText
     */
    public static String wrapText(String message, int maxNumChar, int width){    	
    	String tooltip = null;
    	if(message==null || "".equals(message)){
    		return "";
    	}
    	// we still need the wrapping because the message could have been escaped,
    	// which would show up as funny character if you do not put them within <html>
    	if(message.length()<maxNumChar){
        	tooltip = "<html><table><tr><td>"
        	+message
        	+"</td></tr></table></html>";
    		return tooltip; // no wrapping is needed
    	}
    	// we should NOT do escaping here
    	// because some tooltip already has the html tag
    	// escaping them would cause error
    	// instead they should call escapeHTMLTag separately 
    	tooltip = "<html><table WIDTH=\""+width+"\"><tr><td>"
    	+message
    	+"</td></tr></table></html>";
    	return tooltip;
    }
    
    /**
     * Replace characters having special meaning inside HTML tags
     * with their escaped equivalents, using character entities such as '&amp;'.
     *
     * The escaped characters are :
     *  < > " ' \ &
     *
     * This method ensures that arbitrary text appearing inside a tag does not "confuse"
     * the tag. For example, HREF='Blah.do?Page=1&Sort=ASC'
     * does not comply with strict HTML because of the ampersand, and should be changed to
     * HREF='Blah.do?Page=1&amp;Sort=ASC'.
     * This method always returns a new String
     * @param text input text to be escaped
     * @return result the escaped text
     */
    public static String escapeHTMLTag(String text){
    	if(hasHTMLTag(text)){
    		return escapeHTMLTagCopy(text);
    	}
    	return text;
    }
    
    /**
     * Returns true if the input string contains any escape character
     * The escaped characters are :
     *  < > " ' \ &

     * @param text input text to be checked
     * @return hasEscape character
     */
     public static boolean hasHTMLTag(String text){
    	 if(text.indexOf('<')>=0){
    		 return true;
    	 }
    	 if(text.indexOf('>')>=0){
    		 return true;
    	 }
    	 if(text.indexOf('\"')>=0){
    		 return true;
    	 }
    	 if(text.indexOf('\\')>=0){
    		 return true;
    	 }
    	 if(text.indexOf('&')>=0){
    		 return true;
    	 }
    	 return false;
     }
     
     
     /**
      * Replace characters having special meaning inside HTML tags
      * with their escaped equivalents, using character entities such as '&amp;'.
      *
      * The escaped characters are :
      *  < > " ' \ &
      *
      * This method ensures that arbitrary text appearing inside a tag does not "confuse"
      * the tag. For example, HREF='Blah.do?Page=1&Sort=ASC'
      * does not comply with strict HTML because of the ampersand, and should be changed to
      * HREF='Blah.do?Page=1&amp;Sort=ASC'.
      * This method always returns a new String
      * @param text input text to be escaped
      * @return result the escaped text
      */
      public static String escapeHTMLTagCopy(String text){
        final StringBuffer result = new StringBuffer();

        final StringCharacterIterator iterator = new StringCharacterIterator(text);
        char character =  iterator.current();
        while (character != CharacterIterator.DONE ){
          if (character == '<') {
            result.append("&lt;");
          }
          else if (character == '>') {
            result.append("&gt;");
          }
          else if (character == '\"') {
            result.append("&quot;");
          }
          else if (character == '\'') {
            result.append("&#039;");
          }
          else if (character == '\\') {
             result.append("&#092;");
          }
          else if (character == '&') {
             result.append("&amp;");
          }
          else {
            //the char is not a special one
            //add it to the result as is
            result.append(character);
          }
          character = iterator.next();
        }
        return result.toString();
      }
      
      /**
  	 * Repaints the cells for all the tree nodes in the tree represented by the given tree model. This
  	 * is particularly useful when the renderer for a tree has changed leaving us open to the
  	 * possibility that the new renderer will leave the nodes' text cut off and an ellipsis at the
  	 * end. Note: The root node must be a TreeNode.
  	 * 
  	 * @param treeModel
  	 */
  	public static void redrawAllTreeCells(DefaultTreeModel treeModel) {
  		Object root = treeModel.getRoot();
  		if(root instanceof TreeNode) {
  			redrawAllTreeCellsRec(treeModel, (TreeNode) root);
  		}
  		else {
  			System.err.println("Error: Tree root is not a TreeNode");
  		}
  	}
  	
  	private static void redrawAllTreeCellsRec(DefaultTreeModel treeModel, TreeNode parent) {
  		treeModel.nodeChanged(parent);
  		for(int i = 0; i < parent.getChildCount(); i++) {
  			redrawAllTreeCellsRec(treeModel, parent.getChildAt(i));
  		}
  	}
}
