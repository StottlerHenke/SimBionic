package com.stottlerhenke.simbionic.common.debug;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**S
 *
 * The answer to your quandry is available at the Peter Van der Linden's Java Programmer's
 * FAQ located at http://www.afu.com/javafaq.html. Have a look at Section 7 Question 11 (reprinted here for convenience):


Question
I used a C program to write a binary file. When I instantiate a DataInputStream on the file, and try to readInt, I do not get the correct numbers. Why is this?

Answer
The Java programming language does everything in network byte order (big-endian order), as do many computers including Motorola, and SPARC. For non-Java languages, the Intel x86 uses little-endian order in which the 4 bytes of an int are stored least significant first. Rearranging the bytes on the way in will get you the results you need. This is only necessary when the binary file was written by a non-Java program on a little-endian machine such as a PC.

 */

public class MFCSocketInputStream extends DataInputStream
{

  public MFCSocketInputStream(InputStream in)
  {
    super(in);
  }

  public float readMFCFloat() throws IOException
  {
    byte[] size = new byte[4];
    read(size);

    ByteBuffer buf = ByteBuffer.wrap(size);
    buf.position(0);
    buf.order(ByteOrder.LITTLE_ENDIAN);

    return buf.getFloat();
  }

  public long readMFCLong() throws IOException
  {
    byte[] size = new byte[4];
    read(size);
    return (size[3]<<24)|((size[2]&0xff)<<16)|((size[1]&0xff)<<8)|(size[0]&0xff);
  }

  public int readMFCInt() throws IOException
  {
    byte[] size = new byte[4];
    read(size);
    return (size[3]<<24)|((size[2]&0xff)<<16)|((size[1]&0xff)<<8)|(size[0]&0xff);
  }

  public Integer readMFCInteger() throws IOException
  {
    return new Integer(readMFCInt());
  }

  public String readPascalString() throws IOException
  {
    int size = readMFCInt();
    byte[] chars = new byte[size];
    this.read(chars);
    return new String(chars);
  }
}
