package com.stottlerhenke.simbionic.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFileChooser;

public class FileManager
{
	private SimBionicEditor mEditorApp = null;
	private static FileManager mInstance = null;
	
	private File mSBPFile = null;
	private File mAPDFile = null;
	private File mSimFile = null;
	private File mXMLFile = null;
	private File mHeaderFile = null;
	private File mJavaFile = null;
	
	/**
	 *  
	 */
	private FileManager(SimBionicEditor pEditorApp)
	{
		super();
		if(pEditorApp == null) throw new RuntimeException("SimBionic app cannot be null for FileManager!");
		mEditorApp = pEditorApp;
	}

	public static FileManager initialize(SimBionicEditor pEditorApp)
	{
		mInstance = new FileManager(pEditorApp);
		return mInstance;
	}
	
	public static FileManager getInstance()
	{
		if(mInstance != null) return mInstance;
		else throw new RuntimeException("FileManager not initialized!");
	}
	
	public String getProjectFileName()
	{
		if(mSBPFile == null) return new String();
		String name = mSBPFile.getName();
		int index = name.lastIndexOf('.');
		return name.substring(index);
	}
	
  public int getRevisionNumber(File pSimFile)
	{
		if (pSimFile != null && pSimFile.exists())
		{
			try
			{
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(pSimFile)));
				String line = in.readLine();
				line = in.readLine();
				in.close();
				return Integer.parseInt(line.substring(13));
			}
			catch (FileNotFoundException exception)
			{
				System.err.println("file not found");
			}
			catch (IOException exception)
			{
				System.err.println("i/o exception");
			}
		}
		return 0;
	}

	
	public JFileChooser getFileChooserXML()
	{
		if(mXMLFile == null) mXMLFile = new File(System.getProperty("user.dir"));
		JFileChooser chooser = new JFileChooser(mXMLFile);
		chooser.setFileFilter(Util.createFileFilterForExt("xml", "XML Files (*.xml)", true));
		chooser.setAcceptAllFileFilterUsed(false);

		return chooser;
	}

	public JFileChooser getFileChooserH()
	{
		if(mHeaderFile == null) mHeaderFile = new File(System.getProperty("user.dir"));
		JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));
		chooser.setFileFilter(Util.createFileFilterForExt("h", "C++ Header Files (*.h)", true));
		chooser.setAcceptAllFileFilterUsed(false);

		return chooser;
	}

	
	public JFileChooser getFileChooserJava()
	{
		if(mJavaFile == null) mJavaFile = new File(System.getProperty("user.dir"));
		JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));
		chooser.setFileFilter(Util.createFileFilterForExt("java", "Java Files (*.java)", true));
		chooser.setAcceptAllFileFilterUsed(false);

		return chooser;
	}

	/**
	 * @return Returns the simFile.
	 */
	public File getSimFile()
	{
		return this.mSimFile;
	}
	
	/**
	 * @param pSimFile The simFile to set.
	 */
	public void setSimFile(String pSimFileName)
	{
		this.mSimFile = new File(pSimFileName);
	}
	
	/**
	 * @param pSimFile The simFile to set.
	 */
	public void setSimFile(File pSimFile)
	{
		this.mSimFile = pSimFile;
	}
}