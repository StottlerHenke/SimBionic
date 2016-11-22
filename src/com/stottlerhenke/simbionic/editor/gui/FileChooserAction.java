package com.stottlerhenke.simbionic.editor.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.text.JTextComponent;

/**
 * FileChooserAction that works together with a TextField
 *
 */
public class FileChooserAction implements ActionListener {
	
	protected JFileChooser _fc;
	protected JTextComponent _textComponent;
	protected String _fileDescription; // e.g. "SIM File (*.sim)"
	protected String _fileExt; // e,g, "sim" , "txt"
	protected String _dialogTitle; //"Output file listing pathname");
	protected String DEFAULT_BUTTON_LABEL = "Set";
	
	/**
	 * Constructor
	 * @param dialogTitle
	 * @param fileDescription if null, will not set any file filter
	 * @param fileExt
	 * @param textComponent
	 * @param isFileOnly if true the dialog will only accept files. if false, both files and directories will be accepted.
	 */
	public FileChooserAction(String dialogTitle, String fileDescription, String fileExt, JTextComponent textComponent){
		_dialogTitle = dialogTitle;
		_fileDescription = fileDescription;
		_fileExt = fileExt;
		_textComponent = textComponent;
	}
	
	/**
	 * set the textComponent on actionPerformed
	 */
	public void actionPerformed(ActionEvent event)
	{
        String text = _textComponent.getText();
        if (text.length() > 0)
            getFileChooser().setSelectedFile(new File(text));
        getFileChooser().setDialogTitle(_dialogTitle); 
        int returnVal = getFileChooser().showDialog(ComponentRegistry.getFrame(),
        		DEFAULT_BUTTON_LABEL);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = getFileChooser().getSelectedFile();
            _textComponent.setText(file.getAbsolutePath());
        }
	}
	
	/**
	 * returns the file chooser
	 * @return JFileChooser 
	 */
	public JFileChooser getFileChooser()
	{
		if (_fc == null)
		{
			_fc = new JFileChooser();
		}
		if(_fileDescription!=null){
			_fc.setFileFilter(new javax.swing.filechooser.FileFilter()
			{

				public String getDescription()
				{
					return _fileDescription;
				}

				public boolean accept(File f)
				{
					if (f.isDirectory())
					{
						return true;
					}

					if(_fileExt!=null && !"".equals(_fileExt)){
						String ext = getExt(f);
    	                if (ext != null)
    	                {
    	                	if (ext.equals(_fileExt))
    	                	{
    	                		return true;
    	                	} else
    	                	{
    	                		return false;
    	                    }
    	                }
					}
	                return true;
				}
			});
		}
		
		_fc.setAcceptAllFileFilterUsed(false);
		_fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
		
	    return _fc;
	 }

	/**
	 * a utility function for getting the file extension
	 * @param f file
	 * @return fileExtension file extension
	 */
    protected String getExt(File f)
    {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1)
        {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
}