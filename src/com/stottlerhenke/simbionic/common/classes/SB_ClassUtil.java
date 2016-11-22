package com.stottlerhenke.simbionic.common.classes;

import java.util.ArrayList;

/**
 * Class utility methods
 * 
 */
public class SB_ClassUtil
{


    /**
     * Create a debug String from the method name and parameters
     * @return
     */
    public static String getMethodString(String methodName, ArrayList args)
    {
        StringBuffer buf = new StringBuffer();
        buf.append(methodName + "(");
        
        for(int x = 0; x < args.size(); x++)
        {
            Object obj = args.get(x);
            if(obj != null)
                buf.append(obj.getClass().getName());
            else
                buf.append("null");
            
            if(x < args.size() -1)
                buf.append(", ");
        }
        
        buf.append(")");
        return buf.toString();
    }
    
    /**
     * Create a debug String from the class name, method name, and parameters
     * @return
     */
    public static String getMethodString(String className, String methodName, ArrayList args)
    {
        return className + "." + getMethodString(methodName, args);
    }

}
