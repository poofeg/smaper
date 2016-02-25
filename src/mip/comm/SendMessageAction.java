package mip.comm;

import mip.ContactList;
import mip.ContactListContactItem;
import mip.MIP;
import mip.mipException;
import mip.Options;


public class SendMessageAction extends Action
{
  // Plain message
  private PlainMessage plainMsg;
  
  private int SEQ1 = 0xffff;


	// Constructor
	public SendMessageAction(Message msg)
	{
		super(false, true);
		if (msg instanceof PlainMessage)
		{
			this.plainMsg = (PlainMessage) msg;
			
		}
		
	}


  // Init action
  protected void init() throws mipException
  {
    // Forward init request depending on message type
    SEQ1--; 
    initPlainMsg();
  }


  // Init action for plain messages
  private void initPlainMsg() throws mipException
  {

        // Get receiver object
        ContactListContactItem rcvr;

        rcvr = this.plainMsg.getRcvr();
        // What message format/encoding should we use?
        int type = 1;
		boolean utf8;
		try
		{
			if ((Options.getBoolean(Options.OPTION_UTF8OUT)) && (rcvr.getIntValue(ContactListContactItem.CONTACTITEM_STATUS) != ContactList.STATUS_OFFLINE)) utf8 = rcvr.hasCapability(Icq.CAPF_UTF8_INTERNAL);
			else utf8 = false;
		}
		catch (Exception e) 
		{
			utf8=false;
		}
		
        
        if((this.plainMsg != null) && ((this.plainMsg.getMessageType() >= Message.MESSAGE_TYPE_AWAY) && (this.plainMsg.getMessageType() <= Message.MESSAGE_TYPE_FFC)))
        {
            type = 2;
        }
        //////////////////////
        // Message format 1 //
        //////////////////////

        if (type == 1)
        {
            
            // Get UIN
            byte[] uinRaw = Util.stringToByteArray(rcvr.getStringValue(ContactListContactItem.CONTACTITEM_UIN));

            // Get text
            byte[] textRaw;
            if (utf8)
            {
                textRaw = Util.stringToUcs2beByteArray( Util.restoreCrLf(this.plainMsg.getText()) );
            }
            else
            {
                textRaw = Util.stringToByteArray( Util.restoreCrLf(this.plainMsg.getText()) );
            }

            // Pack data
            byte[] buf = new byte[10 + 1 + uinRaw.length + 4 + (utf8 ? 6 : 5) + 4 + 4 + textRaw.length + 4];
            int marker = 0;
            Util.putDWord(buf, marker, 0x00000000); // CLI_SENDMSG.TIME
            marker += 4;
            Util.putDWord(buf, marker, 0x00000000); // CLI_SENDMSG.ID
            marker += 4;
            Util.putWord(buf, marker, 0x0001); // CLI_SENDMSG.FORMAT
            marker += 2;
            Util.putByte(buf, marker, uinRaw.length); // CLI_SENDMSG.UIN
            System.arraycopy(uinRaw, 0, buf, marker + 1, uinRaw.length);
            marker += 1 + uinRaw.length;
            Util.putWord(buf, marker, 0x0002); // CLI_SENDMSG.SUB_MSG_TYPE1
            Util.putWord(buf, marker + 2, (utf8 ? 6 : 5) + 4 + 4 + textRaw.length);
            marker += 4;
            Util.putWord(buf, marker, 0x0501); // SUB_MSG_TYPE1.CAPABILITIES
            if (utf8)
            {
                Util.putWord(buf, marker + 2, 0x0002);
                Util.putWord(buf, marker + 4, 0x0106);
                marker += 6;
            }
            else
            {
                Util.putWord(buf, marker + 2, 0x0001);
                Util.putByte(buf, marker + 4, 0x01);
                marker += 5;
            }
            Util.putWord(buf, marker, 0x0101); // SUB_MSG_TYPE1.MESSAGE
            Util.putWord(buf, marker + 2, 4 + textRaw.length);
            marker += 4;
            if (utf8)
            {
                Util.putDWord(buf, marker, 0x00020000); // MESSAGE.ENCODING
            }
            else
            {
                Util.putDWord(buf, marker, 0x00000000); // MESSAGE.ENCODING
            }
            marker += 4;
            System.arraycopy(textRaw, 0, buf, marker, textRaw.length); // MESSAGE.MESSAGE
            marker += textRaw.length;
            Util.putWord(buf, marker, 0x0006); // CLI_SENDMSG.UNKNOWN
            Util.putWord(buf, marker + 2, 0x0000);
            marker += 4;

            // Send packet
            SnacPacket snacPkt = new SnacPacket(SnacPacket.CLI_SENDMSG_FAMILY, SnacPacket.CLI_SENDMSG_COMMAND, 0, new byte[0], buf);
            Icq.c.sendPacket(snacPkt);

        }
                
        //////////////////////
        // Message format 2 //
        //////////////////////

        else
            if (type == 2)
            {
                // System.out.println("Send TYPE 2");
                // Get UIN
                byte[] uinRaw = Util.stringToByteArray(rcvr.getStringValue(ContactListContactItem.CONTACTITEM_UIN));

                // Get text
                byte[] textRaw;

                // Get filename if file transfer
                byte[] filenameRaw;

             
                textRaw = Util.stringToByteArray( Util.restoreCrLf(this.plainMsg.getText()) );
                filenameRaw = new byte[0];

                // Set length
                // file request: 192 + UIN len + file description (no null) +
                // file name (null included)
                // normal msg: 163 + UIN len + message length;

                int p_sz = 0;

              
                p_sz = 163 + uinRaw.length + textRaw.length;
                

                //int tlv5len = 148;
                //int tlv11len = 108;

                // Build the packet
                byte[] buf = new byte[p_sz];
                int marker = 0;
                long tim = System.currentTimeMillis();
                Util.putDWord(buf, marker, tim); // CLI_SENDMSG.TIME
                marker += 4;
                Util.putDWord(buf, marker, 0x00000000); // CLI_SENDMSG.ID
                marker += 4;
                Util.putWord(buf, marker, 0x0002); // CLI_SENDMSG.FORMAT
                marker += 2;
                Util.putByte(buf, marker, uinRaw.length); // CLI_SENDMSG.UIN
                System.arraycopy(uinRaw, 0, buf, marker + 1, uinRaw.length);
                marker += 1 + uinRaw.length;

                //-----------------TYPE2 Specific Data-------------------
                Util.putWord(buf, marker, 0x0005);
                marker += 2;

                // Length of TLV5 differs betweeen normal message and file requst
              
                Util.putWord(buf, marker, 144 + textRaw.length, true);
                marker += 2;

                Util.putWord(buf, marker, 0x0000);
                marker += 2;

                Util.putDWord(buf, marker, tim);
                marker += 4;

                Util.putDWord(buf, marker, 0x00000000);
                marker += 4;

                System.arraycopy(Icq.CAP_AIM_SERVERRELAY, 0, buf, marker, 16);
                // SUB_MSG_TYPE2.CAPABILITY
                marker += 16;

                // Set TLV 0x0a to 0x0001
                Util.putDWord(buf, marker, 0x000a0002);
                marker += 4;
                Util.putWord(buf, marker, 0x0001);
                marker += 2;

                // Set emtpy TLV 0x0f
                Util.putDWord(buf, marker, 0x000f0000);
                marker += 4;

           
                // Set TLV 0x2711
                Util.putWord(buf, marker, 0x2711);
                marker += 2;

                // Length of TLV2711 differs betweeen normal message and file requst
              
                Util.putWord(buf, marker, 104 + textRaw.length, true);
                marker += 2;
                // Put 0x1b00 (unknown)
                Util.putWord(buf, marker, 0x1B00);
                marker += 2;

                // Put ICQ protocol version in LE
                Util.putWord(buf, marker, 0x0800);
                marker += 2;

                // Put capablilty (16 zero bytes)
                Util.putDWord(buf, marker, 0x00000000);
                marker += 4;
                Util.putDWord(buf, marker, 0x00000000);
                marker += 4;
                Util.putDWord(buf, marker, 0x00000000);
                marker += 4;
                Util.putDWord(buf, marker, 0x00000000);
                marker += 4;

                // Put some unknown stuff
                Util.putWord(buf, marker, 0x0000);
                marker += 2;
                Util.putByte(buf, marker, 0x03);
                marker += 1;

                // Set the DC_TYPE to "normal" if we send a file transfer request
              
                Util.putDWord(buf, marker, 0x00000000);
              
                marker += 4;
                // Put cookie, unkown 0x0e00 and cookie again
                Util.putWord(buf, marker, SEQ1, false);
                marker += 2;
                Util.putWord(buf, marker, 0x0e, false);
                marker += 2;
                Util.putWord(buf, marker, SEQ1, false);
                marker += 2;

                // Put 12 unknown zero bytes
                Util.putDWord(buf, marker, 0x00000000);
                marker += 4;
                Util.putDWord(buf, marker, 0x00000000);
                marker += 4;
                Util.putDWord(buf, marker, 0x00000000);
                marker += 4;

                // Put message type 0x0001 if normal message else 0x001a for file request
                Util.putWord(buf, marker, this.plainMsg.getMessageType(),false);
                marker += 2;

                // Put contact status
                Util.putWord(buf, marker, Util.translateStatusSend((int)Options.getLong(Options.OPTION_ONLINE_STATUS)), false);
                marker += 2;

                // Put priority
                Util.putWord(buf, marker, 0x01, false);
                marker += 2;
                // Put message
                //#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
                // Put message length
                Util.putWord(buf, marker, textRaw.length + 1, false);
                marker += 2;

                // Put message
                System.arraycopy(textRaw, 0, buf, marker, textRaw.length); // TLV.MESSAGE
                marker += textRaw.length;
                Util.putByte(buf, marker, 0x00);
                marker++;
                // Put foreground, background color and guidlength
                Util.putDWord(buf, marker, 0x00000000);
                marker += 4;
                Util.putDWord(buf, marker, 0x00FFFFFF);
                marker += 4;
                Util.putDWord(buf, marker, 0x26000000);
                marker += 4;
                System.arraycopy(Icq.CAP_UTF8_GUID, 0, buf, marker, 38);
                // SUB_MSG_TYPE2.CAPABILITY
                marker += 38;                
                //#sijapp cond.else#
                // Put message length
                Util.putWord(buf, marker, textRaw.length + 1, false);
                marker += 2;

                // Put message
                System.arraycopy(textRaw, 0, buf, marker, textRaw.length); // TLV.MESSAGE
                marker += textRaw.length;
                Util.putByte(buf, marker, 0x00);
                marker++;
                // Put foreground, background color and guidlength
                Util.putDWord(buf, marker, 0x00000000);
                marker += 4;
                Util.putDWord(buf, marker, 0x00FFFFFF);
                marker += 4;
                Util.putDWord(buf, marker, 0x26000000);
                marker += 4;
                System.arraycopy(Icq.CAP_UTF8_GUID, 0, buf, marker, 38);
                // SUB_MSG_TYPE2.CAPABILITY
                marker += 38;
                //#sijapp cond.end#

                // Put TLV 0x03
                Util.putWord(buf, marker, 0x0003, true); // CLI_SENDMSG.UNKNOWN
                marker += 2;
                Util.putWord(buf, marker, 0x0000);
                marker += 2;
                // Send packet
                SnacPacket snacPkt = new SnacPacket(SnacPacket.CLI_SENDMSG_FAMILY, SnacPacket.CLI_SENDMSG_COMMAND, 0, new byte[0], buf);
                mip.MIP.getIcqRef().c.sendPacket(snacPkt);
                // System.out.println("SendMessageAction: Sent the packet");
            }
           
        SEQ1--;

    }


	// Forwards received packet, returns true if packet was consumed
	protected boolean forward(Packet packet) throws mipException
	{
		return (false);
	}


  // Returns true if the action is completed
  public boolean isCompleted()
  {
    return (true);
  }


  // Returns true if an error has occured
  public boolean isError()
  {
    return (false);
  }


}
