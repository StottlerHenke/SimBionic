package com.stottlerhenke.simbionic.common.debug;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MFCSocketOutputStream extends DataOutputStream
{
  public MFCSocketOutputStream( OutputStream out )
  {
    super(out);
  }

  public void writePascalString(String s) throws IOException
  {
    int length = s.length();

    this.writeMFCInt(length);
    this.writeBytes(s);
  }

  public void writeMFCInt(int n) throws IOException
  {
    byte[] buffer = new byte[4];

    buffer[3] = (byte) (( n & 0xff000000) >>> 24);
    buffer[2] = (byte) (( n & 0x00ff0000) >>> 16);
    buffer[1] = (byte) (( n & 0x0000ff00) >>> 8);
    buffer[0] = (byte) (( n & 0x000000ff));

    int test = (buffer[3]<<24)|((buffer[2]&0xff)<<16)|((buffer[1]&0xff)<<8)|(buffer[0]&0xff);
    this.write(buffer);
  }

  public void writeMFCLong(long n) throws IOException
  {
    byte[] buffer = new byte[4];

    buffer[3] = (byte) (( n & 0xff000000) >>> 24);
    buffer[2] = (byte) (( n & 0x00ff0000) >>> 16);
    buffer[1] = (byte) (( n & 0x0000ff00) >>> 8);
    buffer[0] = (byte) (( n & 0x000000ff));

    int test = (buffer[3]<<24)|((buffer[2]&0xff)<<16)|((buffer[1]&0xff)<<8)|(buffer[0]&0xff);
    this.write(buffer);
  }

  public void writeMFCFloat(float f) throws IOException
  {
    byte[] buffer = new byte[4];

    ByteBuffer buf = ByteBuffer.wrap(buffer);
    buf.position(0);

    buf.putFloat(f);
    buf.order(ByteOrder.LITTLE_ENDIAN);

    this.write(buf.array());
  }
}