
package com.stottlerhenke.simbionic.engine.file;

import java.util.*;

import com.stottlerhenke.simbionic.common.SB_Util;

/**
 * This class keeps track of the mappings between behaviors and
 * specification files.  It also keeps track of the currently-loaded
 * project specification name and version number.
 */
public class SB_FileRegistry 
{
	private int _specVersion;
	private int _formatVersion;
	private String _projectFilename;
	private HashMap _packageMap = new HashMap();

	
//Accessors for project specification info

	/**
	 * Sets the specification version for the current-loaded project.
	 * @param specVersion the version number of the current project
	 */
	public void setSpecVersion(int version) { _specVersion = version; }

	/**
	 * @return the specification version for the current-loaded project
	 */
	public int getSpecVersion() { return _specVersion; }

	/**
	 * Sets the filename for the currently-loaded project.
	 * @param filename the project filename
	 */
	public void setProjectFilename(String filename)
	{
		_projectFilename = SB_Util.stripPath(filename);
	}

	/**
	 * @return the filename for the currently-loaded project.
	 */
	public String getProjectFilename() { return _projectFilename; }

	/**
	 * Sets the format version for the currently-loading project.
	 * @param formatVersion format version of the project file
	 */
	public void setFormatVersion(int formatVersion) { _formatVersion = formatVersion; }

	/**
	 * @return the specification format version for the currently-loading project
	 */
	public int getFormatVersion() { return _formatVersion; }


// Methods for maintaining the package registry

	/**
	 * Stores a mapping between the named package file and the given behavior.
	 * @param packageName the name of the package the behavior belongs to
	 * @param behaviorName the name of the behavior to associate
	 */
	public void associatePackage(String packageName,String behaviorName)
	{
		ArrayList assocVec = getAssociations(packageName);
		if (assocVec == null)
		{
			// no associations with this package yet, so set up a vector to hold them
			assocVec = new ArrayList();

			_packageMap.put( packageName,assocVec );
		}

		assocVec.add(behaviorName);
	}

	/**
	 * Removes all associations for the named package.
	 * @param packageName the name of the package to remove
	 */
	public void removeAssociations(String packageName)
	{
		_packageMap.remove(packageName);		
	}

	/**
	 * Removes all associations for all packages.
	 */
	public void removeAllAssociations()
	{
		_packageMap.clear();
	}

	/**
	 * Returns a list of all behaviors associated with the named package.
	 * @param packageName the name of the package to retrieve associated behaviors for
	 */
	public ArrayList getAssociations(String packageName)
	{
		return (ArrayList)_packageMap.get(packageName);
	}

}
