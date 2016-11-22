package com.stottlerhenke.simbionic.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarUtil {
   
     /**
      * Returns a Calendar object holding the calendar information represented by
      * the argument string s. The format of the string can be either one of the
      * following:<br>
      * YYYY-MM-DD<br>
      * YYYY-MM-DDTHH:MM<br>
      * YYYY-MM-DDTHH:MM(-/+)XX:XX<br>
      * YYYY-MM-DDTHH:MM:SS<br>
      * YYYY-MM-DDTHH:MM:SS(-/+)XX:XX
      * 
      * @param s the string representation of a calendar
      * @return a Calendar object if s complies to the formats above, returns
      *  null otherwise.
      */
     public static Calendar valueOf(String s){
       // This implementation assumes the argument is good formatted.

       int year = Integer.parseInt(s.substring(0, 4));
       int month = Integer.parseInt(s.substring(5, 7)) - 1;// Note: month is zero-based
       int date = Integer.parseInt(s.substring(8, 10));

       if (s.length() == 10)
         return new GregorianCalendar(year, month, date);

       int hour = Integer.parseInt(s.substring(11, 13));
       int minute = Integer.parseInt(s.substring(14, 16));

       if (s.length() == 16)
         return new GregorianCalendar(year, month, date, hour, minute);

       if (s.length() == 19){
         int second = Integer.parseInt(s.substring(17));
         return new GregorianCalendar(year, month, date, hour, minute, second);
       }

       if (s.length() == 22){
         Calendar c = new GregorianCalendar(year, month, date, hour, minute);
         int offsetHour = Integer.parseInt(s.substring(16, 19));
         int offsetMinute = Integer.parseInt(s.substring(20));
         if (offsetHour < 0) offsetMinute = -offsetMinute;
         c.setTimeZone(TimeZone.getTimeZone(TimeZone.getAvailableIDs((offsetHour*60+offsetMinute)*60*1000)[0]));
         return c;
       }

       if (s.length() == 25){
         int second = Integer.parseInt(s.substring(17, 19));
         Calendar c = new GregorianCalendar(year, month, date, hour, minute, second);
         int offsetHour = Integer.parseInt(s.substring(19, 22));
         int offsetMinute = Integer.parseInt(s.substring(23));
         if (offsetHour < 0) offsetMinute = -offsetMinute;
         c.setTimeZone(TimeZone.getTimeZone(TimeZone.getAvailableIDs((offsetHour*60+offsetMinute)*60*1000)[0]));
         return c;
       }

       return null;// Should not reach here.
     }
     
     /**
      * Parses the give string to a Calendar object in the following steps:
      * 1. Try to call {@link #valueOf(String)} to see if the string is in that format;
      * 2. If step 1 failed, try to parse the string using the current locale's date format;
      * 3. If step 2 still failed, try to parse the string using other avaiable locale's date format;
      * 4. If step 1 through 3 failed, return null.
      * 
      * @param s the string representation of a calendar
      * @return a Calendar object if s can be successfully parsed, returns null otherwise.
      * @since 1.3
      */
     public static Calendar parse(String s)
     {
        Calendar cal;
        
        // step 1
        try
        {
           cal = valueOf(s);
           if (cal != null)
              return cal;
        }
        catch (Exception ex)
        {
        }
        
        // step 2
        cal = parse(s, Locale.getDefault());
        if (cal != null)
           return cal;
        
        // step 3
        Locale[] locales = Locale.getAvailableLocales();
        for (int i = 0; i < locales.length; i ++)
        {
           if (!locales[i].equals(Locale.getDefault()))
           {
              cal = parse(s, locales[i]);
              if (cal != null)
                 return cal;
           }
        }
        
        // step 4
        return null;
     }
     
     /**
      * Parses the given string to a Calendar object using the give locale's date format.
      * It tries all possible format combinations including: 
      * FULL date FULL time, FULL date LONG time, FULL date MEDIUM time, FULL date SHORT time,
      * LONG date FULL time, LONG date LONG time, LONG date MEDIUM time, LONG date SHORT time,
      * MEDIUM date FULL time, MEDIUM date LONG time, MEDIUM date MEDIUM time, MEDIUM date SHORT time,
      * SHORT date FULL time, SHORT date LONG time, SHORT date MEDIUM time, SHORT date SHORT time,
      * FULL date only, LONG date only, MEDIUM date only, SHORT date only,
      * FULL time only, LONG time only, MEDIUM time only, and SHORT time only.
      * Returns null if all format fail.
      * 
      * @param s the string representation of a calendar
      * @param locale a locale that defines its own date format
      * @return a Calendar object if s can be successfully parsed, returns null otherwise.
      * @since 1.3
      */
     public static Calendar parse(String s, Locale locale)
     {
        int[] dateStyles = new int[]{DateFormat.FULL, DateFormat.LONG, DateFormat.MEDIUM, DateFormat.SHORT};
        int[] timeStyles = new int[]{DateFormat.FULL, DateFormat.LONG, DateFormat.MEDIUM, DateFormat.SHORT};
        
        // try datetime
        for (int d = 0; d < dateStyles.length; d ++)
        {
           for (int t = 0; t < timeStyles.length; t ++)
           {
              try
              {
                 Date date = DateFormat.getDateTimeInstance(dateStyles[d], timeStyles[t], locale).parse(s);
                 return dateToCalendar(date, locale);
              }
              catch (ParseException ex){
              }
           }
        }
        
        // try date only
        for (int d = 0; d < dateStyles.length; d ++)
        {
           try
           {
              Date date = DateFormat.getDateInstance(dateStyles[d], locale).parse(s);
              return dateToCalendar(date, locale);
           }
           catch (ParseException ex){
           }
        }
        
        // try time only
        for (int t = 0; t < timeStyles.length; t ++)
        {
           try
           {
              Date date = DateFormat.getTimeInstance(timeStyles[t], locale).parse(s);
              return dateToCalendar(date, locale);
           }
           catch (ParseException ex){
           }
        }
        
        return null;
     }
     
     public static Calendar dateToCalendar(Date date)
     {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      return cal;
     }
     
     public static Calendar dateToCalendar(Date date, Locale locale)
     {
      Calendar cal = Calendar.getInstance(locale);
      cal.setTime(date);
      return cal;
     }

}

