
package com.stottlerhenke.simbionic.engine.file;

import com.stottlerhenke.simbionic.common.SB_FileException;
import com.stottlerhenke.simbionic.common.SB_Util;
import com.stottlerhenke.simbionic.common.Version;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;


/**
 * This class encapsulates the actual logical format (i.e., grammar) 
 * of the different types of SimBionic behavior specifications.  Given
 * a WG_StreamReader, it provides methods to read a specification
 * from that stream.
 */
abstract public class SB_Specification 
{
	protected String _specName;

	/**
	 * Constructor
	 * @param name the name of the specification
	 */
	SB_Specification(String name)
	{
		_specName = SB_Util.stripPath(name);
	}

	/**
	 * Reads the entire specification from the supplied stream.
	 * @param book container for all engine singletons
	 * @throws SB_FileException on failure
	 */
	abstract void read(SB_SingletonBook book) throws SB_FileException;

	/**
	 * Determines whether the specification being loaded can be loaded by the engine.
	 * @param formatVersion the format version of the specification being read
	 * @return true if the format can be read by the engine
	 */
	protected boolean isCompatibleSpecFormat(int formatVersion)
	{
		// if backwards compatibility is allowed, check for supported versions here
		return (formatVersion >= Version.FILE_FORMAT_MIN_VERSION) &&
				(formatVersion <= Version.FILE_FORMAT_MAX_VERSION);		
	}
}
