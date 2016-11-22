package com.stottlerhenke.simbionic.engine.comm;
import java.io.Serializable;

import com.stottlerhenke.simbionic.engine.parser.*;
import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.*;
/**
 * Represents a single message sent by an entity.
 */

public class SB_CommMsg implements Serializable  
{
  private  SB_ID	_sender;
  private  String	_recipient;
  private  int		_type;
  private  SB_Variable  _data;

  public SB_CommMsg(SB_ID sender,String recipient,int type,SB_Variable data) throws SB_Exception
  {
    _sender = sender;
    _recipient = recipient;
    _type = type;
    _data = null;
    _data = data.Clone();
  }

  public SB_CommMsg(SB_CommMsg msg) throws SB_Exception
  {
    _sender = msg.GetSender();
    _recipient = msg.GetRecipient();
    _type = msg.GetMsgType();
    _data = msg.GetData().Clone();
  }
  /**
   * @return the unique ID of the sending entity for this message
   */
  public SB_ID GetSender() { return _sender; }

  /**
   * @return the unique ID of the recipient group for this message
   */
  public String GetRecipient()  { return _recipient; }

  /**
   * @return the data object for this message
   */
  public SB_Variable GetData() { return _data; }

  /**
   * @return the type code for this message (author-defined)
   */
  public int GetMsgType() { return _type; }

}