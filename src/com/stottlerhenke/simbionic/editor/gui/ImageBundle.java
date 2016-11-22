package com.stottlerhenke.simbionic.editor.gui;

import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;


public class ImageBundle extends ResourceBundle
{
	  private String _fileSuffix;
	  private static Hashtable _table;

	  static 
	  {
	     _table = new Hashtable();
	  }

	  protected ImageBundle(String suffix) 
	  {
	    _fileSuffix = suffix;
	  }

	  public ImageBundle() 
	  {
	    this("");
	  }

	  private ImageIcon loadImageIcon(String basename, String extension) 
	  {
	    String imageName = basename;// + _fileSuffix + extension;

        if (_table.containsKey(imageName)) 
        {
            SoftReference sr = (SoftReference) _table.get(imageName);
            ImageIcon icon = (ImageIcon) sr.get();
            if (icon != null)
                return icon;
        }

	    URL url = ImageBundle.class.getResource("images/" + imageName);
	    if(url == null)
	    	return null;
        
        ImageIcon icon = new ImageIcon(url);
        if (icon != null)
        {
        	_table.put(imageName, new SoftReference(icon));
            return icon;
        }

        return null;
	  }
	  	  
	  
	  /* (non-Javadoc)
	 * @see java.util.ResourceBundle#getKeys()
	 */
	  public Enumeration getKeys() 
	  {
	    return null; 
	  }


	/* (non-Javadoc)
	 * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
	 */
	  protected final Object handleGetObject(String key) {
	    // All of our files are GIFs named by key
	    return loadImageIcon(key, ".gif");
	  }

}

