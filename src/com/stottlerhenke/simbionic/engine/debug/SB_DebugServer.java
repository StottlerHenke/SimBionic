package com.stottlerhenke.simbionic.engine.debug;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.Version;
import com.stottlerhenke.simbionic.common.debug.DMFieldMap;
import com.stottlerhenke.simbionic.common.debug.MFCSocketInputStream;
import com.stottlerhenke.simbionic.common.debug.MFCSocketOutputStream;
import com.stottlerhenke.simbionic.common.debug.SB_DebugMessage;
import com.stottlerhenke.simbionic.engine.SB_SingletonBook;

/**
 * The DebugServer acts as the conduit between the Java debugging implementation and the C++ network implementation</p>
 */

public class SB_DebugServer
{
  public static final int NO_TIMEOUT = -1;
  public static final int RESPONSE_WAIT_SECS = 10;
  public static final int PORT_NUMBER = 7242;

  protected SB_MessageHandler _msgHandler;
  protected SB_SingletonBook _book;

  private ServerSocket serverSocket = null;
  private Socket clientSocket = null;
  private MFCSocketInputStream in = null;
  private MFCSocketOutputStream out = null;

  
  public SB_DebugServer(SB_SingletonBook book)
  {
    _book = book;
  }

  public boolean EstablishSession(int timeout) throws SB_Exception
  {
    // wait for the opening handshake message
    SB_DebugMessage initMsg;
    if ((initMsg = _msgHandler.WaitForMessage(SB_DebugMessage.kDBG_GUI_INIT, -1, false, RESPONSE_WAIT_SECS, _book.getDebugger())) == null)
      return false;

    // check the engine and simfile version
    if (!DoVersionsMatch(initMsg))
    {
      // abort connect attempt with client
      DMFieldMap fields = new DMFieldMap();
      fields.ADD_STR_FIELD( "simfileName", _book.getFileRegistry().getProjectFilename() );
      fields.ADD_INT_FIELD( "simfileVersion", _book.getFileRegistry().getSpecVersion() );
      SendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_ENG_INIT_FAILED, fields) );

      return false;
    }

    // send the return handshake message
    DMFieldMap fields = new DMFieldMap();
    fields.ADD_STR_FIELD("engineVersion", Version.SIMBIONIC_INTERFACE_VERSION);
    SB_DebugMessage msg = new SB_DebugMessage(SB_DebugMessage.kDBG_ENG_INIT_OK, fields);
    SendMsg(msg);

    _book.getLogger().log(" [[ Handshake with client complete, debugging session established (" + initMsg.GetStringField("simfileName") + ") ]]",SB_Logger.DEBUGGER);
    return true;
  }

  boolean DoVersionsMatch(SB_DebugMessage initMsg)
  {
  	
  	//return true; //hack for jim
  	
    // check that the simfile is the same
    if (_book.getFileRegistry().getProjectFilename().compareToIgnoreCase(initMsg.GetStringField("simfileName")) != 0)
    {
      _book.getLogger().log("!! ERROR: engine and client sim-file names do not match ("
                                    + _book.getFileRegistry().getProjectFilename() + " vs "
                                    + initMsg.GetStringField("simfileName") + ")" , SB_Logger.ERROR);
      //return false;	// strict name-checking causes too many false positives
    }

    // check that the simfile version is the same
    if (_book.getFileRegistry().getSpecVersion() != initMsg.GetIntField("simfileVersion"))
    {
      _book.getLogger().log( "!! ERROR: engine and client sim-file versions do not match ("
                                    + _book.getFileRegistry().getSpecVersion() + " vs "
                                    + initMsg.GetIntField("simfileVersion") + ")", SB_Logger.ERROR);
      return false;
    }

    return true;
  }

  public boolean EndSessionServer() throws SB_Exception
  {
    // send the opening handshake message
    SendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_ENG_SHUTDOWN) );

    _book.getLogger().log(" [[ Sent shutdown message to external debugging client. ]]",SB_Logger.DEBUGGER);

    if (_msgHandler.WaitForMessage(SB_DebugMessage.kDBG_GUI_SHUTDOWN_OK, -1, false, RESPONSE_WAIT_SECS, _book.getDebugger()) == null)
    {
      // client failed to shut down gracefully
      return false;
    }

    return true;
  }

  public void EndSessionClient()
  {
    // send the return handshake message
    SendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_ENG_SHUTDOWN_OK) );

    _book.getLogger().log(" [[ Sent shutdown acknowledgement to external debugging client. ]]",SB_Logger.DEBUGGER);
  }

  public void SetMessageHandler(SB_MessageHandler msgHandler)
  {
    _msgHandler = msgHandler;
  }

  // Actual server functions

  public boolean Initialize()
  {
    return true;
  }

  /**
   *
   * @param timeout Number of seconds to wait
   * @return
   */
  public boolean WaitForConnection(int timeout)
  {
		_book.getLogger().log(" [[ Initializing TCP/IP Interactive Debugging Server... ]]",SB_Logger.DEBUGGER);
  	
    try
    {
      serverSocket = new ServerSocket(PORT_NUMBER);
    }
    catch (IOException ex)
    {
      _book.getLogger().log("!! " + ex.toString(), SB_Logger.ERROR);
      return false;
    }

		_book.getLogger().log(" [[ Interactive Debugging server waiting for client connections ]]", SB_Logger.DEBUGGER);
		
    try
    {
      serverSocket.setSoTimeout(timeout *  1000);
      clientSocket = serverSocket.accept();
    }
    catch (IOException ex)
    {
      _book.getLogger().log("!! " + ex.toString(), SB_Logger.ERROR);
      return false;
    }

    try
    {
      out = new MFCSocketOutputStream(clientSocket.getOutputStream());
      //out = new PrintWriter(clientSocket.getOutputStream(), true);
      //in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
      in = new MFCSocketInputStream( clientSocket.getInputStream() );
    }
    catch( IOException ex)
    {
      _book.getLogger().log("!! " + ex.toString(), SB_Logger.ERROR);
      return false;
    }

		_book.getLogger().log(" [[ Accepted Interactive Debugging client connection from ???",SB_Logger.DEBUGGER);
		
    return true;
  }

  public void Shutdown()
  {
    try
    {
     if( out != null )
          out.close();

      if( in != null )
        in.close();

      if( serverSocket != null )
        serverSocket.close();

      if( clientSocket != null )
        clientSocket.close();
    }
    catch( IOException ex )
    {
      _book.getLogger().log("!! " + ex.toString(), SB_Logger.ERROR);
    }
  }

  public boolean IsConnected()
  {
    return clientSocket.isBound();
  }

  public void SendMsg(SB_DebugMessage msg)
  {
    byte[] msgBytes = msg.serialize(_book.getLogger());

    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    MFCSocketOutputStream tempOut = new MFCSocketOutputStream(byteOut);
    try
    {
      tempOut.writeByte(0); //Packet type
      tempOut.writeMFCInt( msgBytes.length ); //PacketSize
      tempOut.write(msgBytes);

      out.write(byteOut.toByteArray());
      _book.getLogger().log(" [[ sent message " + msg.GetTypeName() + " to client ]]",SB_Logger.DEBUGGER);
    }
    catch( IOException ex)
    {
      _book.getLogger().log("!! " + ex.toString(), SB_Logger.ERROR);
    }
  }

  public SB_DebugMessage ReceiveMsg()
  {
    SB_DebugMessage msg = null;

    try
    {
      if(in.available() > 0 )
      {
        byte packetType = in.readByte();
        int dataSize = in.readMFCInt();

        int msgType = in.readByte();
        msg = new SB_DebugMessage(msgType);
        msg.deserialize(in, _book.getLogger());
      }
    }
    catch( IOException ex )
    {
      _book.getLogger().log("!!" + ex.toString(), SB_Logger.ERROR);
    }

		if (msg != null)
			_book.getLogger().log(" [[ received message " + msg.GetTypeName() + " from client. ]]",SB_Logger.DEBUGGER);

    return msg;
  }

}


