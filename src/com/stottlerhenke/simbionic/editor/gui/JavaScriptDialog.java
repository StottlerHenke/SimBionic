package com.stottlerhenke.simbionic.editor.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.stottlerhenke.simbionic.common.xmlConverters.model.JavaScript;

/**
 * A modal dialog that allows the user to edit {@link JavaScript} object.
 * 
 *
 */
public class JavaScriptDialog extends ModalDialog {
   
   /**
    * The dialog size
    */
   private static final Dimension DIALOG_SIZE = new Dimension(580, 600); 
   
   /**
    * delimiter between javaScript file names or java class names
    */
   private static final String DELIMITER = ";";
   private static final String NEW_LINE = System.getProperty("line.separator");
   
   private JTextArea _jsFileNames;
   private JTextArea _javaClassNames;

   /**
    * Constructor
    */
   public JavaScriptDialog() {
      super("JavaScript");
   }

   /**
    * Set the specified javaScript object in this dialog to display.
    * 
    * @param javaScript JavaScript object to set.
    */
   public void setJavaScript(JavaScript javaScript) {
      // update the UI
      _jsFileNames.setText(getDisplayString(javaScript.getJsFiles()));
      _javaClassNames.setText(getDisplayString(javaScript.getImportedJavaClasses()));
   }
   
   /**
    * 
    * @return A new JavaScript created from the user's input.
    */
   public JavaScript getJavaScript() {
      String jsFileNamesAsString = _jsFileNames.getText().trim();
      List<String> jsFileNames = getListFromString(jsFileNamesAsString);
      
      String javaClassNamesAsString = _javaClassNames.getText().trim();
      List<String> javaClassNames = getListFromString(javaClassNamesAsString);
      
      JavaScript javaScript = new JavaScript();
      javaScript.setJsFiles(jsFileNames);
      javaScript.setImportedJavaClasses(javaClassNames);
      return javaScript;
   }
   
   protected String getDisplayString(List<String> list) {
      String str = "";
      
      for (String s : list) {
         str += s + DELIMITER + NEW_LINE;
      }
      
      return str;
   }
   
   protected List<String> getListFromString(String str) {
      if (str == null || str.isEmpty()) {
         return new ArrayList<String>();
      }
      
      String[] strArray = str.split(DELIMITER);
      List<String> list = new ArrayList<String>();
      for (String s : strArray) {
         String trimmedString = s.trim();
         if (!trimmedString.isEmpty()) {
            list.add(trimmedString);
         }
         
      }
      return list;
   }
   
  
   /* (non-Javadoc)
    * @see com.stottlerhenke.simbionic.editor.gui.ModalDialog#initGUI()
    */
   protected void initGUI(){
      JPanel mainPanel = new JPanel();
      mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
      
      _jsFileNames = new JTextArea();
      _javaClassNames = new JTextArea();
      mainPanel.add(
            createTextAreaPanel("Load JavaScript Files:", _jsFileNames, 
            		DIALOG_SIZE.width, (int)(DIALOG_SIZE.height * 0.2), 
            		"Enter the list of Javascript files delimited by \";\""));
      mainPanel.add(
            createTextAreaPanel("Import Java Classes:", _javaClassNames, 
            		DIALOG_SIZE.width, (int)(DIALOG_SIZE.height * 0.4),
            		"Enter the list of Java classes delimited by \";\""));
      initDialog(mainPanel, null, true);
      setSize(DIALOG_SIZE);
   }
   
   
   /**
    * Create a new panel that contains a label and text area.
    * @param labelText The text for the label.
    * @param textArea JTextArea widget
    * @return The new JPanel.
    */
   private JPanel createTextAreaPanel(String labelText, JTextArea textArea, int width, int height,
		   String tooltip) {
      JPanel panel = new JPanel();
      panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      panel.setLayout(new BorderLayout());
      
      if (labelText != null){
         JLabel label = new JLabel(labelText);
         label.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
         label.setForeground(Color.black);
         panel.add(label, BorderLayout.NORTH);
      }
   
      textArea.setLineWrap(true);
      textArea.setWrapStyleWord(true);
      textArea.setToolTipText(tooltip);

      JScrollPane scrollPane
      	= new JScrollPane(textArea);

      scrollPane.setPreferredSize(new Dimension(width, height));
      panel.add(scrollPane, BorderLayout.CENTER);
      return panel;
   }
   

}
