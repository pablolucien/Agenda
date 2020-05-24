// Este es un paquete ad hoc para poner cosas que he obtenido de otras fuentes
package org.pclg.xtras;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
/**
 * This class wraps a standard java.util.StringTokenizer, but provides
 * an additional mode of operation (NO_CONSECUTIVE_DELIMS).
 *
 */
// Sacado de un foro de JDC
// http://forum.java.sun.com/thread.jsp?forum=31&thread=158421
// post de Jeff Schiller (schillj)
public class EnhancedStringTokenizer implements Enumeration
{
  private static final int NO_RETURN_DELIMS = 0;
  private static final int RETURN_DELIMS = 1;
  public static final int NO_CONSECUTIVE_DELIMS = 2;
  private int Mode = NO_CONSECUTIVE_DELIMS;
  void setMode(final int mode)
  {
    Mode = mode;
  }
  int getMode()
  {
    return Mode;
  }
  // default delimiter in the StringTokenizer
  private String Delimiter = " \t\n\r\f";
  void setDelimiter(final String delim)
  {
    Delimiter = delim;
  }
  String getDelimiter()
  {
    return Delimiter;
  }
  private String Remainder = "";
  void setRemainder(final String str)
  {
    Remainder = str;
  }
  String getRemainder()
  {
    return Remainder;
  }
  private java.util.StringTokenizer st;
  /**
   * Same as the StringTokenizer constructor.  No added functionality.
   */
  public EnhancedStringTokenizer(final String str)
  {
    setMode(NO_RETURN_DELIMS);
    st = new StringTokenizer(str);
  }
  /**
   * Same as the StringTokenizer constructor.  No added functionality.
   */
  public EnhancedStringTokenizer(final String str, final String delim)
  {
    setMode(NO_RETURN_DELIMS);
    setDelimiter(delim);
    st = new StringTokenizer(str, delim);
  }
  /**
   * Same as the StringTokenizer constructor.  No added functionality.
   */
  public EnhancedStringTokenizer(final String str, final String delim, final boolean returnDelims)
  {
    if(returnDelims)
    {
      setMode(RETURN_DELIMS);
    }
    else
    {
      setMode(NO_RETURN_DELIMS);
    }
    setDelimiter(delim);
    st = new StringTokenizer(str, delim, returnDelims);
  }
  /**
   * Using this constructor allows use of the NO_CONSECUTIVE_DELIMS mode
   * of operation.
   */
  public EnhancedStringTokenizer(final String str, final String delim, final int mode)
  {
    setMode(mode);
    setDelimiter(delim);
    switch(getMode())
    {
      case NO_RETURN_DELIMS:
        st = new StringTokenizer(str, delim, false);
        break;
      case RETURN_DELIMS:
        st = new StringTokenizer(str, delim, true);
        break;
      case NO_CONSECUTIVE_DELIMS:
      default:
        init(str);
        break;
    }
  }
  void init(final String str)
  {
    setRemainder(str);
  }
  public int countTokens()
  {
    switch(getMode())
    {
      case NO_CONSECUTIVE_DELIMS:
		  final String oldRem = getRemainder();
		  int count = 0;
		  try
		  {
			String temp;
			while(true)
			{
			  temp = nextToken();
			  count++;
			}
		  }
		  catch(final NoSuchElementException nsee)
		  {
			setRemainder(oldRem);
			return count;
		  }
	default:
        return st.countTokens();
    }
  }
  public boolean hasMoreElements()
  {
    switch(getMode())
    {
      case NO_CONSECUTIVE_DELIMS:
        int intIndex = 0;
        intIndex = getRemainder().indexOf(getDelimiter(), intIndex);
        if(intIndex != -1)
        {
          return true;
        }
        else
        {
          if(getRemainder().length() > 0)
          {
            return true;
          }
          else
          {
            return false;
          }
        }
      default:
        return st.hasMoreElements();
    }
  }
  public Object nextElement() throws NoSuchElementException
  {
    switch(getMode())
    {
      case NO_CONSECUTIVE_DELIMS:
        int intIndex = 0;
        intIndex = getRemainder().indexOf(getDelimiter(), intIndex);
        if(intIndex != -1)
        {
          final String retValue = getRemainder().substring(0, intIndex);
          setRemainder(getRemainder().substring(intIndex+1));
          return retValue;
        }
        else
        {
          if(getRemainder().length() > 0)
          {
            final String retValue = getRemainder();
            setRemainder("");
            return retValue;
          }
          else
          {
            throw new NoSuchElementException();
          }
        }
      default:
        return st.nextElement();
    }
  }
  public boolean hasMoreTokens()
  {
    switch(getMode())
    {
      case NO_CONSECUTIVE_DELIMS:
        int intIndex = 0;
        intIndex = getRemainder().indexOf(getDelimiter(), intIndex);
        if(intIndex != -1)
        {
          return true;
        }
        else
        {
          if(getRemainder().length() > 0)
          {
            return true;
          }
          else
          {
            return false;
          }
        }
      default:
        return st.hasMoreElements();
    }
  }
  public String nextToken() throws NoSuchElementException
  {
    switch(getMode())
    {
      case NO_CONSECUTIVE_DELIMS:
        int intIndex = 0;
        intIndex = getRemainder().indexOf(getDelimiter(), intIndex);
        if(intIndex != -1)
        {
          final String retValue = getRemainder().substring(0, intIndex);
          setRemainder(getRemainder().substring(intIndex+1));
          return retValue;
        }
        else
        {
          if(getRemainder().length() > 0)
          {
            final String retValue = getRemainder();
            setRemainder("");
            return retValue;
          }
          else
          {
            throw new NoSuchElementException();
          }
        }
      default:
        return st.nextToken();
    }
  }
  public String nextToken(final String delim) throws NoSuchElementException
  {
    switch(getMode())
    {
      case NO_CONSECUTIVE_DELIMS:
        setDelimiter(delim);
        return nextToken();
      default:
        return st.nextToken(delim);
    }
  }
}
