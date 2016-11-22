package com.stottlerhenke.simbionic.common;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Logger is used to log information to a disk file, console or any registered
 * print stream. Logger has a content control flag on each individual registered
 * print stream. Only log messages whose type matches the content control flag
 * will be written to the log stream.
 * <p>
 * The following is an example of how to use this class:
 * <br>...
 * <br>Logger.timestamp = true;
 * <br>Logger.register(System.out, 2);
 * <br>Logger.register(new PrintStream(new FileOutputStream("log.txt")), 3);
 * <br>...
 * <br>Logger.log("This is a plain message.");
 * <br>Logger.log("Warning: this is a warning.", Logger.WARNING);
 * <br>Logger.log("Error: this is an error.", Logger.ERROR);
 * <br>...
 */
public class SB_Logger {

	/**
	 * Flag for logging messages about action execution.
	 */
	public final static int ACTION		= 0x00001;
	
	/**
	 * Flag for logging messages about predicate execution.
	 */
	public final static int PREDICATE	= 0x00002;
	
	/**
	 * Flag for logging system milestone messages.
	 */
	public final static int MILESTONE	= 0x00004;

	/**
	 * Flag for logging detailed initialization messages.
	 */
	public final static int INIT			= 0x00008;

	/**
	 * Flag for logging warning messages.
	 */
	public final static int WARNING		= 0x00010;

	/**
	 * Flag for logging messages from the interactive debugger.
	 */
	public final static int DEBUGGER	= 0x00020;
	
	/**
	 * Flag for logging messages about variable binding evaluations.
	 */
	public final static int BINDING		= 0x00040;
	
	/**
	 * Flag for logging messages about condition evaluations.
	 */
	public final static int CONDITION	= 0x00080;

	/**
	 * Flag for logging messages about behavior invocation and termination.
	 */
	public final static int BEHAVIOR	= 0x00100;
	
	/**
	 * Flag for logging informational messages.
	 */
	public final static int TICK			= 0x00200;
	
	/**
	 * Flag for logging messages from the entity scheduler.
	 */
	public final static int SCHEDULE	= 0x00400;
	
	/**
	 * Flag for logging messages about entity creation and destruction.
	 */
	public final static int ENTITY		= 0x00800;
	
	/**
	 * Flag for logging error messages.  (Errors are always logged.)
	 */
	public final static int ERROR			= 0x01000;
	
	/**
	 * Flag for logging internal engine messages.
	 */
	public final static int INTERN		= 0x02000;	
	
	/**
	 * Flag for logging all messages.
	 */
	public final static int ALL				= 0xFFFFF;

  /**
   * @deprecated
   */
  public final static int FATAL   	= 0x04000;

  /**
   * @deprecated
   */
  public final static int MESSAGE 	= 0x04000;

  /**
   * If this value is <code>true</code>, the output information will follow its
   * timestamp. No timestamp otherwise. Default value is <code>false</code>.
   */
  public static boolean timestamp = false;

  /**
   * Key is the print stream object and value is its verbosity threshold.
   */
  private Dictionary streams = new Hashtable();

  /**
   * Registers a print stream so that the log information will be sent to it.
   * The stream has a set of bit flags associated with it that control
   * what types of messages get logged.
   * <p>
   * If the caller register the same stream again, the previous registered
   * stream will be replaced with the new one with the new content flags.
   *
   * @param stream the print stream that will receive the log information
   * @param contentType the content flags of this stream
   */
  public void register(PrintStream stream, int contentType) {
    streams.put(stream, new Integer(contentType));
    
		if (timestamp){
		  stream.println(new SimpleDateFormat("[EEE MMM dd hh:mm:ss.SSS z yyyy]").format(new Date()));
		}
		stream.println("** Initializing " + Version.SYSTEM_NAME + " log stream.");
		stream.println("** System info: " + Version.SYSTEM_INFO);
		stream.flush();
  }

  /**
   * Unregisters a print stream so that it stops receiving the log information.
   *
   * @param stream the print stream that will stop receiving the log information
   */
  public void unregister(PrintStream stream) {
    streams.remove(stream);

		if (timestamp){
		  stream.println(new SimpleDateFormat("[EEE MMM dd hh:mm:ss.SSS z yyyy]").format(new Date()));
		}
		stream.println("** Unregistering log stream.");
		stream.flush();
  }

  /**
   * Unregisters all of the streams.
   */
  public void unregisterAll()
  {
    streams = new Hashtable();
  }


  /**
   * Logs the content to the print streams regardless of their content flags.
   * <p>
   * This method is thread-safe.
   *
   * @param content the content to be logged
   */
  public synchronized void log(String content) {
    log(content, MESSAGE);
  }

  /**
   * Logs the content to the print streams. The content has an associated content type. 
   * The content will be really logged if its type matches the content flag set for
   * that print stream.
   * <p>
   * This method is thread-safe.
   *
   * @param content the content to be logged
   * @param type the content type for the content
   */
  public synchronized void log(Object content, int type) {
    for (Enumeration it = streams.keys(); it.hasMoreElements(); ) {
      PrintStream stream = (PrintStream)it.nextElement();
      int contentFlags = ((Integer)streams.get(stream)).intValue();
      if ((type & contentFlags) == 0) continue;
      if (timestamp){
        stream.println(new SimpleDateFormat("[EEE MMM dd hh:mm:ss.SSS z yyyy]").format(new Date()));
      }
      stream.println(content.toString());
      stream.flush();
    }
  }
  
  /**
   * Logs the exception, and the accompanying stack trace, to the log stream
   * with the ERROR flag.
   * 
   * @param ex exception to be logged.
   */
  public synchronized void logException(Exception ex)
  {
     	//Get the stack trace as a string
    	StackTraceElement[] trace = ex.getStackTrace();
    	StringBuffer stackTraceBuffer = new StringBuffer();
    	for(int i = 0 ; i < trace.length ; i++)
    	{
    	    stackTraceBuffer.append("\t" + trace[i].toString());	 
    	    stackTraceBuffer.append("\r\n");
    	}
    	String stackTrace = stackTraceBuffer.toString();
    	log("Exception: " + ex.toString(), SB_Logger.ERROR);
    	log("\tStack Trace:\r\n" + stackTrace, SB_Logger.ERROR);
    	
    	Throwable cause = getCause(ex);
    	while(cause != null)
    	{
        	trace = cause.getStackTrace();
        	stackTraceBuffer = new StringBuffer();
        	for(int i = 0 ; i < trace.length ; i++)
        	{
        	    stackTraceBuffer.append("\t" + trace[i].toString());	 
        	    stackTraceBuffer.append("\r\n");
        	}
        	stackTrace = stackTraceBuffer.toString();
        	log("\tCaused by:\r\n\t" + cause.toString() +"\r\n" + stackTrace, SB_Logger.ERROR);
        	
        	cause = getCause(cause);
    	}
  }
  
  /**
   * Return the cause of the given exception. Normally, this is just 
   * ex.getCause but in some cases it will be ex.getTargetException
   *
   * @return the cause of the exception, if any, null otherwise
   */
  public static Throwable getCause(Throwable ex)
  {
	  if(ex instanceof InvocationTargetException)
		  return ((InvocationTargetException) ex).getTargetException();
	  else
		  return ex.getCause();
  }
}