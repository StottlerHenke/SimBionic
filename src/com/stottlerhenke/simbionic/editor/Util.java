package com.stottlerhenke.simbionic.editor;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;


/**
 * Util is a class of static utility methods, especially for useful jdk1.4+
 * methods not found in jdk1.3. If you are writing a util to emulate 1.4+
 * functionality, please document the equivalent 1.4+ code, either by a javadoc
 * see reference to the function, or by giving code examples in jdk1.4.
 */
public class Util {

	static {
		
	}

	/**
	 * Hash of Files to SoftReferences of ImageIcons.
	 * 
	 * @see #getImageIcon(String, String)
	 */
  private static HashMap softImages = new HashMap();

    /**
		 * UtilActionEvent is a class that emulates jdk1.4 ActionEvents by having a
		 * setSource method. Usually setSource is used to pass events on to other
		 * listeners, with a different source. This class simplifies conversion of
		 * 1.4 code into 1.3 code, and can be used as follows: <br>
		 * UtilActionEvent event=new UtilActionEvent(actionEvent); <br>
		 * event.setSource(newSource); <br>
		 * listener.actionPerformed(event);
		 */
    public static class UtilActionEvent extends ActionEvent {

        public UtilActionEvent(ActionEvent e) {
            super(e.getSource(), e.getID(), e.getActionCommand(), e
                    .getModifiers());
        }

        public void setSource(Object newSource) {
            source = newSource;
        }
    }

    /**
		 * Utility function to return the lower-cased file extension - the part of
		 * the filename after the last period.
		 * <p>
		 * For example, getFileExtension("myPicture.is.cool.JPEG") would return
		 * "jpeg".
		 * 
		 * @param f
		 *          The file
		 * @return The file extension in lower case, or null if none exists.
		 */
    public static String getFileExtension(File f) 
    {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    static class ExtFileFilter extends FileFilter
		{
    	private String mDescription = null;
    	private String mExt = null;
    	private boolean mAcceptDir = true;

    	public ExtFileFilter(String pExt)
    	{
    		this(pExt, "");
    	}
    	
    	public ExtFileFilter(String pExt, String pDescription)
    	{
    		this(pExt, pDescription, true);
    	}
    	
    	public ExtFileFilter(String pExt, boolean pAcceptDir)
    	{
    		this(pExt, "", true);
    	}
    	
    	public ExtFileFilter(String pExt, String pDescription, boolean pAcceptDir)
    	{
    		mExt = pExt;
    		mDescription = pDescription;
    		mAcceptDir = pAcceptDir;
    	}
    	
    	public String getDescription()
			{
				return mDescription;
			}

			public boolean accept(File f)
			{
				if(f.isDirectory())
				{
					return mAcceptDir;
				}

				String ext = Util.getFileExtension(f);
				if(ext != null)
				{
					return ext.equals(mExt);
				}
				return false;
			}
		}

  	public static FileFilter createFileFilterForExt(String pExt, String pDescription)
  	{
  		return new ExtFileFilter(pExt, pDescription);
  	}

  	public static FileFilter createFileFilterForExt(String pExt, boolean pAcceptDir)
  	{
  		return new ExtFileFilter(pExt, pAcceptDir);
  	}

  	public static FileFilter createFileFilterForExt(String pExt, String pDescription, boolean pAcceptDir)
  	{
  		return new ExtFileFilter(pExt, pDescription, pAcceptDir);
  	}

    
    /**
		 * Same as getImageIcon(String, String) with null description. Uses soft
		 * references to avoid creating more than one copy of the same image, and
		 * help with memory management.
		 * 
		 * @see #getImageIcon(String, String)
		 * @param filename
		 *          Image filename (relative to IMAGE_DIR) to get
		 * @return ImageIcon for that filename, or null if no file exists
		 */
    public static ImageIcon getImageIcon(String filename) {
        return getImageIcon(filename, null);
    }

    /**
		 * Keeps a hashmap of SoftReferences to images, hashed by filename, so that
		 * the same image icon will be used for multiple versions of the same image.
		 * The SoftReferences ensure that if the images are no longer directly
		 * reachable, they will be garbage collected before an OutOfMemoryError is
		 * thrown.
		 * 
		 * @deprecated Use ImageBundle instead...
		 * 
		 * @param filename
		 *          Image filename (relative to IMAGE_DIR) to get
		 * @param description
		 *          String description of icon, or null
		 * @return
		 */
    public static ImageIcon getImageIcon(String filename, String description) {
    	
    	ResourceBundle bundle = ResourceBundle.getBundle("com.stottlerhenke.simbionic.editor.gui.ImageBundle");
    	try{
    	    ImageIcon icon= (ImageIcon) bundle.getObject(filename);
    	
    	    if (icon==null) return new ImageIcon();
    	    return icon;
    	}catch(Exception e){
    	    return new ImageIcon();
    	}
//    	File f = new File("images" + filename);
//
//    	System.gc();
//        if (softImages.containsKey(f)) {
//            SoftReference sr = (SoftReference) softImages.get(f);
//            ImageIcon icon = (ImageIcon) sr.get();
//            if (icon != null)
//                return icon;
//        }
//        ImageIcon icon;
//        if (f.exists()) {
//            if (description != null)
//                icon = new ImageIcon(f.toString(), description);
//            else
//                icon = new ImageIcon(f.toString());
//            softImages.put(f, new SoftReference(icon));
//            return icon;
//        }
//        return null;
    }

    /**
		 * Returns a String equivalent to input with all parentheses, curly braces,
		 * and square brackets escaped with backslashes. Used for regular
		 * expressions. For jdk1.3, this uses ORO regular expressions.
		 * 
		 * @param input
		 *          String to escape.
		 * @return String with all parentheses, curly braces and backslashes
		 *         escaped.
		 */
    public static String escapeParens(String input) {
        // in jdk1.4+
        //		String strFind = input.replaceAll("\\(", "\\\\(");// strFind =
        // strFind.replaceAll("\\)", "\\\\)");
        //		strFind = strFind.replaceAll("\\[", "\\\\[");
        //		strFind = strFind.replaceAll("\\]", "\\\\]");
        //		strFind = strFind.replaceAll("\\{", "\\\\{");
        //		strFind = strFind.replaceAll("\\}", "\\\\{");
        //		return strFind;

        // in ORO:
        String result = replaceAll(input, "\\(", "\\\\(");
        result = replaceAll(result, "\\)", "\\\\)");
        result = replaceAll(result, "\\[", "\\\\[");
        result = replaceAll(result, "\\]", "\\\\]");
        result = replaceAll(result, "\\{", "\\\\{");
        result = replaceAll(result, "\\}", "\\\\{");
        return result;

    }

    /**
		 * Escapes the ampersand and left angle bracket into HTML entities. Useful
		 * for HTML/XML documents. More characters can be added to this function if
		 * desired.
		 * 
		 * @param input
		 *          Input to escape
		 * @return Input with &amp; and &lt; changed to HTML entities
		 */
    public static String escapeSomeChars(String input) {
        //in jdk1.4+:
        //		String result = input.replaceAll("&", "&amp;");
        //		result = result.replaceAll("<", "&lt;");
        //		return result;

        //in ORO:
        String result = replaceAll(input, "&", "&amp;");
        result = replaceAll(result, "<", "&lt;");
        return result;
    }

    // ---------- Regular Expressions with ORO ----------------------
    // WARNING: ORO regular expressions use Perl syntax, so things that
    // work with Java regexps may not work here, and vice versa. When
    // in doubt, uncomment and run the main() method below to get a quick test
    // app.
    // (main requires jdk1.4 *and* jakarta oro for comparison).

    /**
		 * Replaces the first substring of this string that matches the given
		 * regular expression with the given replacement. see jdk1.4
		 * String.replaceFirst(String, String)
		 * 
		 * @param origString
		 *          Input string
		 * @param regexString
		 *          Regular expression string
		 * @param replacement
		 *          Replacement String
		 * @return The input string, with the first substring that matches the
		 *         regular expression replaced with the replacement.
		 */
    public static String replaceFirst(String origString, String regexString,
            String replacement) {
        //Perl5Util util = new Perl5Util();
//        StringBuffer result = new StringBuffer();
//        String perlexp = "s/" + regexString + "/" + replacement + "/";
        //util.substitute(result, perlexp, origString);
        return origString.replaceFirst(regexString,replacement);
    }

    /**
		 * Replaces each substring of this string that matches the given regular
		 * expression with the given replacement. see jdk1.4
		 * String.replaceAll(String, String)
		 * 
		 * @param origString
		 *          Input string
		 * @param regexString
		 *          Regular expression string
		 * @param replacement
		 *          Replacement String
		 * @return The input string, with all substrings that matches the regular
		 *         expression replaced with the replacement.
		 */
    public static String replaceAll(String origString, String regexString,
            String replacement) {
    	//TODO Uncomment this [mjc]
        //Perl5Util util = new Perl5Util();
        StringBuffer result = new StringBuffer();
        String perlexp = "s/" + regexString + "/" + replacement + "/g";
        //util.substitute(result, perlexp, origString);
        return result.toString();
    }

    /**
		 * Whether the origString matches the regular expression in regexString. see
		 * jdk1.4 String.matches(String)
		 * 
		 * @param origString
		 *          Input string
		 * @param regexString
		 *          Regular expression string
		 * @return true if origString matches regexString
		 */
    public static boolean matches(String origString, String regexString) {
        //TODO UNcomment [mjc]
    	//Perl5Util util = new Perl5Util();
        return true; //TODO uncomment [mjc] util.match("/" + regexString + "/",
										 // origString);
    }

	public static JPanel test() {
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Original String");
		final JTextField originaltf = new JTextField();
		JLabel label2 = new JLabel("Regular Expression");
		final JTextField regtf = new JTextField();
		JLabel label3 = new JLabel("Replacement");
		final JTextField replacetf = new JTextField();
		JButton submit = new JButton("Matches/Replace All");
		JButton submit2 = new JButton("Matches/Replace First");
		JLabel label4 = new JLabel("Jakarta Result");
		final JTextField jakresulttf = new JTextField();
		jakresulttf.setEditable(false);
		JLabel label5 = new JLabel("Java Result");
		final JTextField javresulttf = new JTextField();
		javresulttf.setEditable(false);
		JLabel label6 = new JLabel("Equal?");
		final JTextField equaltf = new JTextField("");
		equaltf.setEditable(false);

		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String original = originaltf.getText();
				String replace = replacetf.getText();
				String reg = regtf.getText();
				String jakresult, javresult;
				if (replace.trim().length() == 0) {
					boolean jak = Util.matches(original, reg);
					jakresult = new Boolean(jak).toString();
					javresult = new Boolean(original.matches(reg)).toString();
				} else {
					jakresult = Util.replaceAll(original, reg, replace);
					javresult = original.replaceAll(reg, replace);
				}
				javresulttf.setText(javresult);
				jakresulttf.setText(jakresult);
				equaltf.setText(new Boolean(jakresult.equals(javresult))
						.toString());
			}
		});

		submit2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String original = originaltf.getText();
				String replace = replacetf.getText();
				String reg = regtf.getText();
				String jakresult, javresult;
				if (replace.trim().length() == 0) {
					boolean jak = Util.matches(original, reg);
					jakresult = new Boolean(jak).toString();
					javresult = new Boolean(original.matches(reg)).toString();
				} else {
					jakresult = Util.replaceFirst(original, reg, replace);
					javresult = original.replaceFirst(reg, replace);
				}
				javresulttf.setText(javresult);
				jakresulttf.setText(jakresult);
				equaltf.setText(new Boolean(jakresult.equals(javresult))
						.toString());
			}
		});

		panel.setLayout(new GridLayout(7, 2));
		panel.add(label);
		panel.add(originaltf);
		panel.add(label2);
		panel.add(regtf);
		panel.add(label3);
		panel.add(replacetf);
		panel.add(submit2);
		panel.add(submit);
		panel.add(label4);
		panel.add(jakresulttf);
		panel.add(label5);
		panel.add(javresulttf);
		panel.add(label6);
		panel.add(equaltf);
		return panel;
	}

	public static void main(String argv[]) {
		JPanel panel = Util.test();
		JFrame main = new JFrame();
		main.getContentPane().add(panel);
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.pack();
		main.setVisible(true);
	}
}