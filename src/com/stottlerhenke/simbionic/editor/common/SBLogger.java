package com.stottlerhenke.simbionic.editor.common;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.logging.*;
import java.util.Iterator;


/**
 * Provides a general logging facility.  
 * It is essentially a wrapper around Java Logging.
 * 
 */
public class SBLogger
{
    /** The version information will be printed out to any LogStream upon registration */
    static String _versionInformation;
    
    
    static public SBLogger getInstance()
    {
        if( _instance == null )
            _instance = new SBLogger();
        
        return _instance;
    }
    
    private SBLogger() 
    {
        _log = Logger.getLogger("com.stottlerhenke.warcon");
        _log.setLevel(Level.ALL);
        Logger root = _log.getParent();
        root.removeHandler(root.getHandlers()[0]);  // remove the default console handler
        _handlers = new HashMap();
    }
    
    /**
     * Set the version information. This string will be the first line
     * printed upton registering a log stream.
     * 
     * @param s
     */
    public void setVersionInformation(String s)
    {
        _versionInformation = s;
    }
    
    /**
     * Called to stop logging. Flushes all of the handlers.
     *
     */
    public void shutdown()
    {
        flush();
        _handlers.clear();
    }
    
    /**
     * Flush all of the handlers
     *
     */
    public void flush()
    {
        for (Iterator i = _handlers.keySet().iterator(); i.hasNext();) 
        {
            OutputStream key = (OutputStream) i.next();
            Handler value = (Handler) _handlers.get(key);
            value.flush();
        }     
    }
    
    /**
     * Activates logging to the specified stream with the 
     * specified logging filter.
     * @return the handler for the new log stream
     */
    public Handler registerLogStream(OutputStream stream, Filter logFilter)
    {
//      Write out the version information
        PrintStream out = new PrintStream(stream, true);
        out.println(_versionInformation);
        
        Handler newHandler = new StreamHandler(stream, new SimpleFormatter());
        newHandler.setFilter(logFilter);
        _handlers.put(stream, newHandler);
        _log.addHandler(newHandler);
        return newHandler;
    }
    
    /**
     * Activates logging to the specified stream with the 
     * specified level of detail.
     * @return the handler for the new log stream
     * 
     */
    public Handler registerLogStream(OutputStream stream, Level level)
    {
        //Write out the version information
        PrintStream out = new PrintStream(stream, true);
        out.println(_versionInformation);
        
        Handler newHandler = new StreamHandler(stream, new SimpleFormatter());
        newHandler.setLevel(level);
        _handlers.put(stream, newHandler);
        _log.addHandler(newHandler);
        
        return newHandler;
    }
    
    /**
     * Deactivates logging to the specified stream.
     * 
     * @param stream
     */
    public void unregisterLogStream(OutputStream stream)
    {
        Handler tempHandler = (Handler) _handlers.get(stream);
        if( tempHandler != null )
            _log.removeHandler(tempHandler);
    }


    /**
     * Logs the specified object to all registered streams at a
     * default level (INFO), calling the objects toString() method.  
     * Content is only logged to a stream if it is of the 
     * appropriate level.
     */
    public void log(Object content)
    {
        log(content,Level.INFO);
    }
    
    /**
     * Logs the specified object to all registered streams, 
     * calling the objects toString() method.  
     * Content is only logged to a stream if it is of the 
     * appropriate level.
     * 
     */
    public void log(Object content, Level level)
    {
        //Get information on the caller (elements[1])
        Throwable t = new Throwable();
        StackTraceElement elements[] = t.getStackTrace();
        
        //Create a log record w/ proper caller class and method information
        LogRecord record = new LogRecord(level, content.toString());
        record.setSourceClassName(elements[1].getClassName());
        record.setSourceMethodName(elements[1].getMethodName());
        
        //Log
        _log.log(record);
        
        //Flush all of the handler for real-time logging
        flush();
        
    } 
    
    /**
     * Log the exception, including the stack trace
     * 
     * @param ex exception to log
     */
    public void log(Exception ex)
    {
        StackTraceElement[] trace = ex.getStackTrace();
        StringBuffer stackTraceBuffer = new StringBuffer();
        for(int i = 0 ; i < trace.length ; i++)
        {
            stackTraceBuffer.append(trace[i].toString());  
            stackTraceBuffer.append("\r\n");
        }  
        String stackTrace = stackTraceBuffer.toString();
        
        if (ex.getCause() != null)
        {
            Throwable cause = ex.getCause();
            stackTrace += "\r\nCAUSED BY: " + cause.toString() + "\r\n";
            trace = cause.getStackTrace();
            stackTraceBuffer = new StringBuffer();
            for(int i = 0 ; i < trace.length ; i++)
            {
                stackTraceBuffer.append(trace[i].toString());  
                stackTraceBuffer.append("\r\n");
            }  
            stackTrace += stackTraceBuffer.toString();
            
            if (cause.getCause() != null)
            {
                cause = cause.getCause();
                stackTrace += "\r\nCAUSED BY: " + cause.toString() + "\r\n";
                trace = cause.getStackTrace();
                stackTraceBuffer = new StringBuffer();
                for(int i = 0 ; i < trace.length ; i++)
                {
                    stackTraceBuffer.append(trace[i].toString());  
                    stackTraceBuffer.append("\r\n");
                }  
                stackTrace += stackTraceBuffer.toString();
            }
        }
        
        
        //log(ex.toString() + "\r\n" + stackTrace, LogLevel.SEVERE);
        
        //    Get information on the caller (elements[1])
        Throwable t = new Throwable();
        StackTraceElement elements[] = t.getStackTrace();
        
        //Create a log record w/ proper caller class and method information
        LogRecord record = new LogRecord(Level.SEVERE, ex.toString() + "\r\n" + stackTrace);
        record.setSourceClassName(elements[1].getClassName());
        record.setSourceMethodName(elements[1].getMethodName());
        
        //Log
        _log.log(record);
        
        //Flush all of the handler for real-time logging
        flush();
    }
    
    private static SBLogger _instance = null;
    private Logger _log;
    private HashMap _handlers; //Key: OutputStream, Value: Handler
}
