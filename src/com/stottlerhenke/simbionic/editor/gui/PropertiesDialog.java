package com.stottlerhenke.simbionic.editor.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.stottlerhenke.simbionic.common.xmlConverters.model.ProjectProperties;

/**
 * A modal dialog that allows the user to edit the project 
 * properties
 * 
 */
@SuppressWarnings("serial")
public class PropertiesDialog extends ModalDialog {
  
   private static final Dimension DIALOG_SIZE = new Dimension(580, 600); 
      
   private JTextField _projectName;
   private JTextArea _projectDescription;
   private JTextField _author;
   private JLabel _lastModified;
   private JLabel _simbionicVersion;
   ProjectProperties initialProperties;
   
   /**
    * Constructor
    */
   public PropertiesDialog() {
      super("Project Properties");
   }

   /**
    * Sets value of initial properties to display
    * @see #getProjectProperties()
    * @see #didPropertiesChange()
    * 
    * @param projectProperties
    */
   public void setDataModel(ProjectProperties projectProperties) {
	   initialProperties = projectProperties;
      // update the UI
	   _projectName.setText(projectProperties.getProjectName());
	   _projectDescription.setText(projectProperties.getDescription());
	   _author.setText(projectProperties.getAuthor());
	   _lastModified.setText(projectProperties.getDateLastUpdate());
	   _simbionicVersion.setText(projectProperties.getSimbionicVersion());
   }
   
   /**
    * whether there were any changes to the initial project properties passed
    * to the editor
    * @return
    */
   public boolean didPropertiesChange () {
	   String newProjectName = _projectName.getText();
	   if (newProjectName!=null && !newProjectName.equals(initialProperties.getProjectName())) {
		   return true;
	   }
	   
	   String newAuthorName = _author.getText();
	   if (newAuthorName!=null && !newAuthorName.equals(initialProperties.getAuthor())) {
		   return true;
	   }
	   
	   String newProjectDescription = _projectDescription.getText();
	   if (newProjectDescription!=null && !newProjectDescription.equals(initialProperties.getDescription())) {
		   return true;
	   }
	   return false;
   }
   
   /**
    * Updated properties reflecting the user's input
    * @see #didPropertiesChange()
    * @return A new ProjectProperties object created from the user's input.
    */
   public ProjectProperties getProjectProperties() {
      ProjectProperties projectProperties = new ProjectProperties();
      projectProperties.setAuthor(_author.getText());
      projectProperties.setDateLastUpdate(initialProperties.getDateLastUpdate());
      projectProperties.setDescription(_projectDescription.getText());
      projectProperties.setProjectName(_projectName.getText());
      projectProperties.setSimbionicVersion(initialProperties.getSimbionicVersion());
      
      return projectProperties;
   }
   
  
   
   /* (non-Javadoc)
    * @see com.stottlerhenke.simbionic.editor.gui.ModalDialog#initGUI()
    */
   protected void initGUI(){
      JPanel mainPanel = new JPanel();
      mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
      
      _author = new JTextField();
      _lastModified = new JLabel();
      _projectName = new JTextField();
      _projectDescription = new JTextArea();
      _simbionicVersion= new JLabel();
      
   
      mainPanel.add(
              createLabeledPanel("Project Name:", _projectName, 
              		DIALOG_SIZE.width, (int)(DIALOG_SIZE.height * 0.1)));
      
      mainPanel.add(
            createTextAreaPanel("Description:", _projectDescription, 
            		DIALOG_SIZE.width, (int)(DIALOG_SIZE.height * 0.6), 
            		"Description"));
      
      mainPanel.add(
              createLabeledPanel("Author:", _author, 
              		DIALOG_SIZE.width, (int)(DIALOG_SIZE.height * 0.1)));
      
      mainPanel.add(
              createLabeledPanel("Last Update:", _lastModified, 
              		DIALOG_SIZE.width, (int)(DIALOG_SIZE.height * 0.1)));
      
      mainPanel.add(
              createLabeledPanel("Simbionic Version:", _simbionicVersion, 
              		DIALOG_SIZE.width, (int)(DIALOG_SIZE.height * 0.1)));
      
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
   
   
   /**
    * Create a new panel that contains a label and the given component .
    * @param labelText The text for the label.
    * @param jComponent
    * @return The new JPanel.
    */
   private JPanel createLabeledPanel(String labelText, JComponent jComponent, int width, int height) {
      JPanel panel = new JPanel();
      panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      panel.setLayout(new BorderLayout());
      
      if (labelText != null){
         JLabel label = new JLabel(labelText);
         label.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
         label.setForeground(Color.black);
         panel.add(label, BorderLayout.NORTH);
      }
   
      panel.setPreferredSize(new Dimension(width, height));
      panel.add(jComponent, BorderLayout.CENTER);
      return panel;
   }
   

}
