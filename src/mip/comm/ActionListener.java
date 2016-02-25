package mip.comm;

import java.util.*;

import mip.ContactList;
import mip.ContactListContactItem;
import mip.RunnableImpl;
import mip.mipException;
import mip.Options;

public class ActionListener
{
    /** ************************************************************************* */
    
    // Forwards received packet
    protected void forward(Packet packet) throws mipException
    {
        // Watch out for channel 4 (Disconnect) packets
        if (packet instanceof DisconnectPacket)
        {
            DisconnectPacket disconnectPacket = (DisconnectPacket) packet;

            // Throw exception
            if (disconnectPacket.getError() == 0x0001)
            { // Multiple logins
                throw (new mipException(110, 0));
            } else
            { // Unknown error
                throw (new mipException(100, 0));
            }

        }

        /** *********************************************************************** */

        // Watch out for channel 2 (SNAC) packets
        if (packet instanceof SnacPacket)
        {
            SnacPacket snacPacket = (SnacPacket) packet;

	    // Typing notify
            //#sijapp cond.if target isnot "DEFAULT"#
	    	if ((snacPacket.getFamily() == 0x0004) && (snacPacket.getCommand() == 0x0014) && Options.getInt(Options.OPTION_TYPING_MODE) > 0)
	    	{
			    byte[] p = snacPacket.getData();
			    int uin_len = Util.getByte(p, 10);
			    String uin = Util.byteArrayToString(p,11,uin_len);
			    int flag = Util.getWord(p, 11+uin_len);
			    //DebugLog.addText("Typing notify: "+flag);
			    
			    if ( flag == 0x0002)
			    	//Begin typing
				    RunnableImpl.BeginTyping(uin,true);
			    else
			    	//End typing
			    	RunnableImpl.BeginTyping(uin,false);
			}
	    	//#sijapp cond.end#
	
            // Watch out for SRV_USERONLINE packets
            if ((snacPacket.getFamily() == SnacPacket.SRV_USERONLINE_FAMILY)
                    && (snacPacket.getCommand() == SnacPacket.SRV_USERONLINE_COMMAND))
            {
                
                //#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
                //#sijapp cond.if modules_FILES is "true"#
                // DC variables
                byte[] internalIP = new byte[4];
                byte[] externalIP = new byte[4];
                int dcPort = 0;
                int dcType = -1;
                int icqProt = 0;
                int authCookie = 0;
                //#sijapp cond.end#
                //#sijapp cond.end#
                
            	boolean statusChange = true;
                int dwFT1=0, dwFT2=0, dwFT3=0;
                int wVersion=0;
                byte[] capabilities_old = null; // Buffer for old style capabilities (TLV 0x000D)
                byte[] capabilities_new = null; // Buffer for new style capabilities (TLV 0x0019)
                
                // Time variables
                int idle = -1;
                int online = -1;
                int signon = -1;
				int regdata = -1;
      
                
                // Get data
                byte[] buf = snacPacket.getData();

                // Get UIN of the contact changing status
                int uinLen = Util.getByte(buf, 0);
                String uin = Util.byteArrayToString(buf, 1, uinLen);
                // Get new status and client capabilities
                int status = ContactList.STATUS_ONLINE;
                int marker = 1 + uinLen + 2;
                int tlvNum = Util.getWord(buf, marker);
                marker += 2;
                for (int i = 0; i < tlvNum; i++)
                {
                    int tlvType = Util.getWord(buf, marker);
                    byte[] tlvData = Util.getTlv(buf, marker);
                    if (tlvType == 0x0006) // STATUS
                    {
                        status = (int)Util.getDWord(tlvData, 0);
                    } else if (tlvType == 0x000D) // Old style CAPABILITIES
                    {
                    	//DebugLog.addText("Old caps");
                    	capabilities_old = tlvData;
                    } else if (tlvType == 0x0019) // New style CAPABILITIES
                    {
                    	//DebugLog.addText("New caps");
                    	capabilities_new = tlvData;
                    }
                    //#sijapp cond.if target is "MIDP2" | target is "MOTOROLA"  | target is "SIEMENS2"#
                    //#sijapp cond.if modules_FILES is "true"#
                    
                    else if (tlvType == 0x000A) // External IP
                    {
                    	System.arraycopy(tlvData, 0, externalIP, 0 ,4);
                    }
                    else if (tlvType == 0x000c) // DC Infos
                    {                        
                        // dcMarker
                        int dcMarker = 0;
                        
                        // Get internal IP
                        System.arraycopy(tlvData,dcMarker,internalIP,0,4);
                        dcMarker += 4;
                        
                        // Get tcp port
                        dcPort = (int)Util.getDWord(tlvData,dcMarker);
                        dcMarker += 4;
                        
                        // Get DC type
                        dcType = Util.getByte(tlvData,dcMarker);
                        dcMarker ++;
                        
                        // Get protocol version
                        icqProt = Util.getWord(tlvData,dcMarker);
                        dcMarker += 2;
                        
                        // Get auth cookie
                        authCookie = (int)Util.getDWord(tlvData,dcMarker);
                        dcMarker +=12;
                        
                        // Get data for client detection
                        dwFT1 = (int) Util.getDWord(tlvData,dcMarker);
                        dcMarker += 4;
                        dwFT2 = (int) Util.getDWord(tlvData,dcMarker);
                        dcMarker += 4;
                        dwFT3 = (int) Util.getDWord(tlvData,dcMarker);
                        
                       statusChange = false;

                    }
                    //#sijapp cond.end#
                    //#sijapp cond.end#
                    else if (tlvType == 0x0003) // Signon time
                    {
                    	signon = (int)Util.gmtTimeToLocalTime( Util.byteArrayToLong(tlvData) );
                    	//System.out.println(Util.getDateString(false,signon));
                    }
                    else if (tlvType == 0x0004) // Idle time
                    {
                    	idle = (int)Util.byteArrayToLong(tlvData)/256;
                    }
                    else if (tlvType == 0x000F) // Online time
                    {
                    	online = (int)Util.byteArrayToLong(tlvData);
                    }
					else if (tlvType == 0x0005) // Reg. data time
                    {
						regdata = (int)Util.byteArrayToLong(tlvData);
                    }
                    
                    
                    marker += 2 + 2 + tlvData.length;
                    
                    
                }
				ContactListContactItem item = ContactList.getItembyUIN(uin);
				int xStatus = -1;
				if (item != null)
				{
					byte[] capsArray = Icq.mergeCapabilities(capabilities_old, capabilities_new);
					Icq.detectUserClient(item, dwFT1, dwFT2, dwFT3,capsArray,icqProt,statusChange, status);
					xStatus = Icq.detectXStatus(capsArray);
				}
                // Update contact list
				ContactList.updateCL(uin, status, internalIP, externalIP, dcPort, dcType, icqProt,authCookie, signon, online, idle, xStatus, regdata);
			}

            /** ********************************************************************* */

            // Watch out for SRV_USEROFFLINE packets
            if ((snacPacket.getFamily() == SnacPacket.SRV_USEROFFLINE_FAMILY)
                    && (snacPacket.getCommand() == SnacPacket.SRV_USEROFFLINE_COMMAND))
            {
				// Get raw data
                byte[] buf = snacPacket.getData();

                // Get UIN of the contact that goes offline
                int uinLen = Util.getByte(buf, 0);
                String uin = Util.byteArrayToString(buf, 1, uinLen);

                // Update contact list
                RunnableImpl.callSerially(RunnableImpl.TYPE_USER_OFFLINE, uin);
            }

		//Xtraz SM
		if (snacPacket.getFamily() == 4 && snacPacket.getCommand()  == 11)
        {
		 byte abyte3[];
        int j1;
        String s6;
		abyte3 = snacPacket.getData();
        long l2 = 0L;
        long l4 = 0L;
        int j5 = Util.getByte(abyte3, 10); //UIN LEN
        s6 = Util.byteArrayToString(abyte3, 11, j5); //UIN
        j1 = 11 + j5;
        j1 += 2;
        j1 += 2;
        j1++;
        j1 += 16;
        j1 += 4;
        j1 += 4;
        j1 += 2;
        j1 += 2;
        j1 += 2;
        j1 += 4;
        j1 += 4;
        j1 += 4;
        int k6 = Util.getWord(abyte3, j1, false); //00 1a - unknown...msg type?
        j1 += 2;
        j1 += 2;
        j1 += 2;
        if(k6 == 26)
        {
		j1 += 2;
        j1++;
        int j7 = Util.getWord(abyte3, j1, false); //00 4f
        j1 += 2;
        if(j7 != 79) throw (new mipException(509, 1, false));
        long l7 = 0L;
        long l9 = 0L;
        long l10 = 0L;
        long l11 = 0L;
        j1 += 16;
        j1 += 2;
        long l12 = Util.getDWord(abyte3, j1, false);
        j1 += 4;
        String s13 = Util.byteArrayToString(abyte3, j1, (int)l12);
        j1 = (int)((long)j1 + l12);
        j1 += 4;
        j1 += 4;
        j1 += 4;
        j1 += 2;
        j1++;
			if(s13 != null && s13.compareTo("Script Plug-in: Remote Notification Arrive") == 0)
			{
            j1 += 4;
            long l14 = Util.getDWord(abyte3, j1, false);
            j1 += 4;
            String s14 = Util.byteArrayToString(abyte3, j1, (int)l14, true);
			XtrazSM.b(s6, s14);
			}
		}}

            /** ********************************************************************* */
            
            // Watch out for CLI_ACKMSG_COMMAND packets
          // Watch out for CLI_ACKMSG_COMMAND packets
          /*  else if ((snacPacket.getFamily() == SnacPacket.CLI_ACKMSG_FAMILY)
                    && (snacPacket.getCommand() == SnacPacket.CLI_ACKMSG_COMMAND))
            {
                // Get raw data
                byte[] buf = snacPacket.getData();
                
                // Get length of the uin (needed for skipping)
                int uinLen = Util.getByte(buf,10);
                
                // Get message type
                int msgType = Util.getWord(buf,58+uinLen,false);
                if((msgType>=Message.MESSAGE_TYPE_AWAY) && (msgType<=Message.MESSAGE_TYPE_FFC))
                {
                    // Create an message entry
                    int textLen = Util.getWord(buf,64+uinLen,false);
                    Alert status_message = new Alert(ResourceBundle.getString("status_message"),"Status message:\r\n\r\n" + Util.byteArrayToString(buf,66+uinLen,textLen, false),null,AlertType.INFO);
                    status_message.setTimeout(Alert.FOREVER);
                    ContactList.activate(status_message);
                }
            }*/

            /** ********************************************************************* */            

            // Watch out for SRV_RECVMSG
            else if ((snacPacket.getFamily() == SnacPacket.SRV_RECVMSG_FAMILY)
                    && (snacPacket.getCommand() == SnacPacket.SRV_RECVMSG_COMMAND))
            {

                // Get raw data, initialize marker
                byte[] buf = snacPacket.getData();
                int marker = 0;
				
				long l3;
				long l5;
				
                // Check length
                if (buf.length < 11) { throw (new mipException(150, 0, false)); }
			    l3 = Util.getDWord(buf, 0, false);
				l5 = Util.getDWord(buf, 4, false);
                // Get message format
                marker += 8;
                int format = Util.getWord(buf, marker);
                marker += 2;

                // Get UIN length
                int uinLen = Util.getByte(buf, marker);
                marker += 1;

                // Check length
                if (buf.length < marker + uinLen + 4) { throw (new mipException(150, 1, false)); }

                // Get UIN
                String uin = Util.byteArrayToString(buf, marker, uinLen);
                marker += uinLen;

                // Skip WARNING
                marker += 2;

                // Skip TLVS
                int tlvCount = Util.getWord(buf, marker);
                marker += 2;
                for (int i = 0; i < tlvCount; i++)
                {
                    byte[] tlvData = Util.getTlv(buf, marker);
                    if (tlvData == null) { throw (new mipException(150, 2, false)); }
                    marker += 4 + tlvData.length;
                }

                // Get message data and initialize marker
                byte[] msgBuf;
                int tlvType;
                do
                {
                    msgBuf = Util.getTlv(buf, marker);
                    if (msgBuf == null) { throw (new mipException(150, 3, false)); }
                    tlvType = Util.getWord(buf, marker);
                    marker += 4 + msgBuf.length;
                } while ((tlvType != 0x0002) && (tlvType != 0x0005));
                int msgMarker = 0;

                //////////////////////
                // Message format 1 //
                //////////////////////
                if (format == 0x0001)
                {

                    // Variables for all possible TLVs
                    // byte[] capabilities = null;
                    byte[] message = null;

                    // Read all TLVs
                    while (msgMarker < msgBuf.length)
                    {

                        // Get next TLV
                        byte[] tlvValue = Util.getTlv(msgBuf, msgMarker);
                        if (tlvValue == null) { throw (new mipException(151, 0, false)); }

                        // Get type of next TLV
                        tlvType = Util.getWord(msgBuf, msgMarker);

                        // Update markers
                        msgMarker += 4 + tlvValue.length;

                        // Save value
                        switch (tlvType)
                        {
                        case 0x0501:
                            // capabilities
                            // capabilities = tlvValue;
                            break;
                        case 0x0101:
                            // message
                            message = tlvValue;
                            break;
                        default:
                            throw (new mipException(151, 1, false));
                        }

                    }

                    // Process packet if at least the message TLV was present
                    if (message != null)
                    {

                        // Check length of message
                        if (message.length < 4) { throw (new mipException(151, 2, false)); }

                        // Get message text
                        String text;
                        if (Util.getWord(message, 0) == 0x0002)
                        {
                            text = Util.removeCr(Util.ucs2beByteArrayToString(message, 4, message.length - 4));
                        } else
                        {
                            text = Util.removeCr(Util.byteArrayToString(message, 4, message.length - 4));
                        }

                        // Construct object which encapsulates the received
                        // plain message

                        PlainMessage plainMsg = new PlainMessage(uin, Options.getString(Options.OPTION_UIN), Util.createCurrentDate(false), text, false);
						
						RunnableImpl.addMessageSerially(plainMsg);
                    }

                }
                //////////////////////
                // Message format 2 //
                //////////////////////
                else if (format == 0x0002)
                {
        			// TLV(A): Acktype 0x0000 - normal message
        			//                 0x0001 - file request / abort request
        			//                 0x0002 - file ack
                    
                    // Check length
                    if (msgBuf.length < 10) { throw (new mipException(152, 0, false)); }

                    // Get and validate SUB_MSG_TYPE2.COMMAND
                    int command = Util.getWord(msgBuf, msgMarker);
                    if (command != 0x0000) return; // Only normal messages are
                                                   // supported yet
                    msgMarker += 2;

                    // Skip SUB_MSG_TYPE2.TIME and SUB_MSG_TYPE2.ID
                    msgMarker += 4 + 4;

                    // Check length
                    if (msgBuf.length < msgMarker + 16) { throw (new mipException(152, 1, false)); }
                    // Skip SUB_MSG_TYPE2.CAPABILITY
                    msgMarker += 16;

                    // Get message data and initialize marker
                    byte[] msg2Buf;
                    //#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
                    //#sijapp cond.if modules_FILES is "true"#
                    int ackType = -1;
                    byte[] extIP = new byte[4];
                    byte[] ip = new byte[4];
                    String port = "0";
                    int status = -1;
                    //#sijapp cond.end#
                    //#sijapp cond.end#
                    
                    do
                    {
                        msg2Buf = Util.getTlv(msgBuf, msgMarker);
                        if (msg2Buf == null) { throw (new mipException(152, 2, false)); }
                        tlvType = Util.getWord(msgBuf, msgMarker);
                        //#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
                        //#sijapp cond.if modules_FILES is "true"#
                        switch (tlvType)
                        {
                        	case 0x0003: System.arraycopy(msg2Buf,0,extIP,0,4); break;
                            case 0x0004: System.arraycopy(msg2Buf,0,ip,0,4);break;
                            case 0x0005: port = Util.byteArrayToString(msg2Buf); break;
                            case 0x000a: ackType = Util.getWord(msg2Buf,0);break;
                        }
                        //#sijapp cond.end#
                        //#sijapp cond.end#
                        msgMarker += 4 + msg2Buf.length;
                    } while (tlvType != 0x2711);
                    
                    int msg2Marker = 0;

                    // Check length
                    if (msg2Buf.length < 2 + 2 + 16 + 3 + 4 + 2 + 2 + 2 + 12 + 2 + 2 + 2 + 2) { throw (new mipException(
                            152, 3, false)); }

                    // Skip values up to (and including) SUB_MSG_TYPE2.UNKNOWN
                    // (before MSGTYPE)
                    msg2Marker += 2 + 2 + 16 + 3 + 4 + 2 + 2 + 2 + 12;

                    // Get and validate message type
                    int msgType = Util.getWord(msg2Buf, msg2Marker, false);
                    msg2Marker += 2;
                    if (!((msgType == 0x0001) || (msgType == 0x0004) || (msgType == 0x001A) || ((msgType >= 1000) && (msgType <= 1004)))) return;

                    //#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
                    //#sijapp cond.if modules_FILES is "true"#
                    status = Util.getWord(msg2Buf,msg2Marker);
                    //#sijapp cond.end#
                    //#sijapp cond.end#
                    msg2Marker += 2;
                    
                    // Skip PRIORITY
                    msg2Marker += 2;

                    // Get length of text
                    int textLen = Util.getWord(msg2Buf, msg2Marker, false);
                    msg2Marker += 2;

                    // Check length
                    if (!((msgType >= 1000) && (msgType <= 1004)))
                        if (msg2Buf.length < msg2Marker + textLen + 4 + 4) { throw (new mipException(152, 4, false)); }

                    // Get raw text
                    byte[] rawText = new byte[textLen];
                    System.arraycopy(msg2Buf, msg2Marker, rawText, 0, textLen);
                    msg2Marker += textLen;
                    // Plain message or URL message
                    if (((msgType == 0x0001) || (msgType == 0x0004)) && (rawText.length > 1))
                    {

                        // Skip FOREGROUND and BACKGROUND
                        if ((msgType == 0x0001) || (msgType == 0x0004))
                        {
                            msg2Marker += 4 + 4;
                        }

                        // Check encoding (by checking GUID)
                        boolean isUtf8 = false;
                        if (msg2Buf.length >= msg2Marker + 4)
                        {
                            int guidLen = (int) Util.getDWord(msg2Buf, msg2Marker, false);
                            if (guidLen == 38)
                            {
                                if (Util.byteArrayToString(msg2Buf, msg2Marker + 4, guidLen).equals(
                                        "{0946134E-4C7F-11D1-8222-444553540000}"))
                                {
                                    isUtf8 = true;
                                }
                            }
                            msg2Marker += 4 + guidLen;
                        }

                        // Decode text and create Message object
                        Message message;
                        if (msgType == 0x0001)
                        {

                            // Decode text
                            String text = Util.removeCr(Util.byteArrayToString(rawText, isUtf8));

                            // Instantiate message object
                            message = new PlainMessage(uin, Options.getString(Options.OPTION_UIN), Util.createCurrentDate(false), text, false);

                        } else
                        {

                            // Search for delimited
                            int delim = -1;
                            for (int i = 0; i < rawText.length; i++)
                            {
                                if (rawText[i] == 0xFE)
                                {
                                    delim = i;
                                    break;
                                }
                            }

                            // Decode text; split text first, if delimiter could
                            // be found
                            String urlText, url;
                            if (delim != -1)
                            {
                                urlText = Util.removeCr(Util.byteArrayToString(rawText, 0, delim, isUtf8));
                                url = Util.removeCr(Util.byteArrayToString(rawText, delim + 1, rawText.length - delim
                                        - 1, isUtf8));
                            } else
                            {
                                urlText = Util.removeCr(Util.byteArrayToString(rawText, isUtf8));
                                url = "";
                            }

                            // Instantiate UrlMessage object
                            message = new UrlMessage(uin, Options.getString(Options.OPTION_UIN), Util.createCurrentDate(false), url, urlText);
                        }

                        // Forward message object to contact list
                        RunnableImpl.addMessageSerially(message);

                        // Acknowledge message
						//ack turned off... for better traffic saving
						//29.02.2008
						
                      /*  byte[] ackBuf = new byte[10 + 1 + uinLen + 2 + 51 + 3];
                        int ackMarker = 0;
                        System.arraycopy(buf, 0, ackBuf, ackMarker, 10);
                        ackMarker += 10;
                        Util.putByte(ackBuf, ackMarker, uinLen);
                        ackMarker += 1;
                        byte[] uinRaw = Util.stringToByteArray(uin);
                        System.arraycopy(uinRaw, 0, ackBuf, ackMarker, uinRaw.length);
                        ackMarker += uinRaw.length;
                        Util.putWord(ackBuf, ackMarker, 0x0003);
                        ackMarker += 2;
                        System.arraycopy(msg2Buf, 0, ackBuf, ackMarker, 51);
                        ackMarker += 51;
                        Util.putWord(ackBuf, ackMarker, 0x0001, false);
                        ackMarker += 2;
                        Util.putByte(ackBuf, ackMarker, 0x00);
                        ackMarker += 1;
                        SnacPacket ackPacket = new SnacPacket(SnacPacket.CLI_ACKMSG_FAMILY,
                        SnacPacket.CLI_ACKMSG_COMMAND, 0, new byte[0], ackBuf);
                        Icq.c.sendPacket(ackPacket);*/

                    }
                    // Extended message
                    else if (msgType == 0x001A)
                    {
                        
                        // Check length
                        if (msg2Buf.length < msg2Marker + 2 + 18 + 4) { throw (new mipException(152, 5, false)); }

                        // Save current marker position
                        int extDataStart = msg2Marker;

                        // Skip EXTMSG.LEN and EXTMSG.UNKNOWN
                        msg2Marker += 2 + 18;

                        // Get length of plugin string
                        int pluginLen = (int) Util.getDWord(msg2Buf, msg2Marker, false);
                        msg2Marker += 4;

                        // Check length
                        if (msg2Buf.length < msg2Marker + pluginLen + 15 + 4 + 4) { throw (new mipException(152, 6,
                                false)); }

                        // Get plugin string
                        String plugin = Util.byteArrayToString(msg2Buf, msg2Marker, pluginLen);
                        msg2Marker += pluginLen;

                        // Skip EXTMSG.UNKNOWN and EXTMSG.LEN
                        msg2Marker += 15 + 4;

                        // Get length of text
                        textLen = (int) Util.getDWord(msg2Buf, msg2Marker, false);
                        msg2Marker += 4;

                        // Check length
                        if (msg2Buf.length < msg2Marker + textLen) { throw (new mipException(152, 7, false)); }

                        // Get text
                        String text = Util.removeCr(Util.byteArrayToString(msg2Buf, msg2Marker, textLen));
                        msg2Marker += textLen;

                       
                        if (plugin.equals("Send Web Page Address (URL)"))
                        {

                            // Search for delimiter
                            int delim = text.indexOf(0xFE);

                            // Split message, if delimiter could be found
                            String urlText;
                            String url;
                            if (delim != -1)
                            {
                                urlText = text.substring(0, delim);
                                url = text.substring(delim + 1);
                            } else
                            {
                                urlText = text;
                                url = "";
                            }

                            // Forward message message to contact list
                            UrlMessage message = new UrlMessage(uin, Options.getString(Options.OPTION_UIN), Util.createCurrentDate(false), url, urlText);
                            RunnableImpl.addMessageSerially(message);
							
							// Acknowledge message
						//ack turned off... for better traffic saving
						//29.02.2008

                            // Acknowledge message
                           /* byte[] ackBuf = new byte[10 + 1 + uinLen + 2 + 51 + 3 + 20 + 4 + (int) pluginLen + 19 + 4
                                    + textLen];
                            int ackMarker = 0;
                            System.arraycopy(buf, 0, ackBuf, ackMarker, 10);
                            ackMarker += 10;
                            Util.putByte(ackBuf, ackMarker, uinLen);
                            ackMarker += 1;
                            byte[] uinRaw = Util.stringToByteArray(uin);
                            System.arraycopy(uinRaw, 0, ackBuf, ackMarker, uinRaw.length);
                            ackMarker += uinRaw.length;
                            Util.putWord(ackBuf, ackMarker, 0x0003);
                            ackMarker += 2;
                            System.arraycopy(msgBuf, 0, ackBuf, ackMarker, 51);
                            ackMarker += 51;
                            Util.putWord(ackBuf, ackMarker, 0x0001, false);
                            ackMarker += 2;
                            Util.putByte(ackBuf, ackMarker, 0x00);
                            ackMarker += 1;
                            System.arraycopy(msg2Buf, extDataStart, ackBuf, ackMarker, 20 + 4 + (int) pluginLen + 19
                                    + 4 + textLen);
                            SnacPacket ackPacket = new SnacPacket(SnacPacket.CLI_ACKMSG_FAMILY,
                                    SnacPacket.CLI_ACKMSG_COMMAND, 0, new byte[0], ackBuf);
                            Icq.c.sendPacket(ackPacket);*/

                        }
                        // Other messages
                        else 
                        {
                             if(plugin.equals("Script Plug-in: Remote Notification Arrive"))
							 try
							 {
								XtrazSM.a(uin, text, msgType, l3, l5);
							 }
							 catch (Exception e)
							 {
								//System.out.println("Error...SM!");
							 }
                        }

                    }                    
                    // Status message requests
                    /*else if (((msgType >= 1000) && (msgType <= 1004)))
                    {
                    	String statusMess;
                    	
                    	long currStatus = Options.getLong(Options.OPTION_ONLINE_STATUS);
						if ((currStatus != ContactList.STATUS_ONLINE) && (currStatus != ContactList.STATUS_CHAT) &&
								(currStatus != ContactList.STATUS_INVISIBLE) && (currStatus != ContactList.STATUS_INVIS_ALL))
							statusMess = Util.replaceStr(Options.getString(Options.OPTION_STATUS_MESSAGE), "%TIME%", Icq.getLastStatusChangeTime());
						else
							statusMess = new String();
                   	
                        // Acknowledge message with away message
                    	final byte[] statusMessBytes = Util.stringToByteArray(statusMess, false);
                    	
						if (statusMessBytes.length < 1) return;
                    	
                        byte[] ackBuf = new byte[10 + 1 + uinLen + 2 + 51 + 2 + statusMessBytes.length +1];
                        int ackMarker = 0;
                        System.arraycopy(buf, 0, ackBuf, ackMarker, 10);
                        ackMarker += 10;
                        Util.putByte(ackBuf, ackMarker, uinLen);
                        ackMarker += 1;
                        byte[] uinRaw = Util.stringToByteArray(uin);
                        System.arraycopy(uinRaw, 0, ackBuf, ackMarker, uinRaw.length);
                        ackMarker += uinRaw.length;
                        Util.putWord(ackBuf, ackMarker, 0x0003);
                        ackMarker += 2;
                        System.arraycopy(msg2Buf, 0, ackBuf, ackMarker, 51);
                        Util.putWord(ackBuf, ackMarker+2, 0x0800);
                        ackMarker += 51;
                        Util.putWord(ackBuf, ackMarker, statusMessBytes.length+1, false);
                        ackMarker += 2;
                        System.arraycopy(statusMessBytes,0,ackBuf,ackMarker,statusMessBytes.length);
                        Util.putByte(ackBuf, ackMarker+statusMessBytes.length, 0x00);
                        SnacPacket ackPacket = new SnacPacket(SnacPacket.CLI_ACKMSG_FAMILY,
                                SnacPacket.CLI_ACKMSG_COMMAND, 0, new byte[0], ackBuf);
                        
                        Icq.c.sendPacket(ackPacket);
                    }*/

                }
                //////////////////////
                // Message format 4 //
                //////////////////////
                else if (format == 0x0004)
                {

                    // Check length
                    if (msgBuf.length < 8) { throw (new mipException(153, 0, false)); }

                    // Skip SUB_MSG_TYPE4.UIN
                    msgMarker += 4;

                    // Get SUB_MSG_TYPE4.MSGTYPE
                    int msgType = Util.getWord(msgBuf, msgMarker, false);
                    msgMarker += 2;

                    // Only plain messages and URL messagesa are supported
                    if ((msgType != 0x0001) && (msgType != 0x0004)) return;

                    // Get length of text
                    int textLen = Util.getWord(msgBuf, msgMarker, false);
                    msgMarker += 2;

                    // Check length (exact match required)
                    if (msgBuf.length != 8 + textLen) { throw (new mipException(153, 1, false)); }

                    // Get text
                    String text = Util.removeCr(Util.byteArrayToString(msgBuf, msgMarker, textLen));
                    msgMarker += textLen;

                    // Plain message
                    if (msgType == 0x0001)
                    {
                        // Forward message to contact list
                        PlainMessage plainMsg = new PlainMessage(uin, Options.getString(Options.OPTION_UIN), Util.createCurrentDate(false), text, false);
                        RunnableImpl.addMessageSerially(plainMsg);
                    }
                    // URL message
                    else if (msgType == 0x0004)
                    {

                        // Search for delimiter
                        int delim = text.indexOf(0xFE);

                        // Split message, if delimiter could be found
                        String urlText;
                        String url;
                        if (delim != -1)
                        {
                            urlText = text.substring(0, delim);
                            url = text.substring(delim + 1);
                        } else
                        {
                            urlText = text;
                            url = "";
                        }

                        // Forward message message to contact list
                        UrlMessage urlMsg = new UrlMessage(uin, Options.getString(Options.OPTION_UIN), Util.createCurrentDate(false), url, urlText);
						RunnableImpl.addMessageSerially(urlMsg);
                    }

                }

            }

            //	  Watch out for SRV_ADDEDYOU
            else if ((snacPacket.getFamily() == SnacPacket.SRV_ADDEDYOU_FAMILY)
                    && (snacPacket.getCommand() == SnacPacket.SRV_ADDEDYOU_COMMAND))
            {
                // Get data
                byte[] buf = snacPacket.getData();

                // Get UIN of the contact changing status
                int uinLen = Util.getByte(buf, 0);
                String uin = Util.byteArrayToString(buf, 1, uinLen);

                // Create a new system notice
                SystemNotice notice = new SystemNotice(SystemNotice.SYS_NOTICE_YOUWEREADDED, uin, false, null);

                // Handle the new system notice
                RunnableImpl.addMessageSerially(notice);                
            }

            //	  Watch out for SRV_AUTHREQ
            else if ((snacPacket.getFamily() == SnacPacket.SRV_AUTHREQ_FAMILY)
                    && (snacPacket.getCommand() == SnacPacket.SRV_AUTHREQ_COMMAND))
            {
                int authMarker = 0;
                
                // Get data
                byte[] buf = snacPacket.getData();

                // Get UIN of the contact changing status
                int length = Util.getByte(buf, 0);
                authMarker += 1;
                String uin = Util.byteArrayToString(buf, authMarker, length);
                authMarker += length;

                // Get reason
                length = Util.getWord(buf, authMarker);
                authMarker += 2;
                String reason = Util.byteArrayToString(buf, authMarker, length, Util.isDataUTF8(buf, authMarker, length));

                // Create a new system notice
                SystemNotice notice = new SystemNotice(SystemNotice.SYS_NOTICE_AUTHREQ, uin, false, reason);

                // Handle the new system notice
                RunnableImpl.addMessageSerially(notice);
            }

            //	  Watch out for SRV_AUTHREPLY
            else if ((snacPacket.getFamily() == SnacPacket.SRV_AUTHREPLY_FAMILY)
                    && (snacPacket.getCommand() == SnacPacket.SRV_AUTHREPLY_COMMAND))
            {

                int authMarker = 0;
                // Get data
                byte[] buf = snacPacket.getData();

                // Get UIN of the contact changing status
                int length = Util.getByte(buf, 0);
                authMarker += 1;
                String uin = Util.byteArrayToString(buf, authMarker, length);
                authMarker += length;

                // Get granted boolean
                boolean granted = false;
                if (Util.getByte(buf, authMarker) == 0x01)
                {
                    granted = true;
                }
                authMarker += 1;

                // Get reason only of not granted
                SystemNotice notice;
                if (!granted)
                {
                    length = Util.getWord(buf, authMarker);
                    String reason = Util.byteArrayToString(buf, authMarker, length + 2);
                    // Create a new system notice
                    if (length == 0)
                        notice = new SystemNotice(SystemNotice.SYS_NOTICE_AUTHREPLY, uin, granted, null);
                    else
                        notice = new SystemNotice(SystemNotice.SYS_NOTICE_AUTHREPLY, uin, granted, reason);
                } else
                {
                    // Create a new system notice
                    //System.out.println("Auth granted");
                    notice = new SystemNotice(SystemNotice.SYS_NOTICE_AUTHREPLY, uin, granted, "");
                }

                // Handle the new system notice
                RunnableImpl.addMessageSerially(notice);
            }

        }       

    }
   /********************************************** Anti-Spam *********************************************/
    private static void sendMessage(ContactListContactItem contact, String message) 
    {
        if (Options.getString(Options.OPTION_ANTISPAM_ANSWER).length() < 1)
        {
            return;
        }
        PlainMessage plainMsg = 
                new PlainMessage(contact.getStringValue(ContactListContactItem.CONTACTITEM_UIN), contact, Message.MESSAGE_TYPE_NORM, Util.createCurrentDate(false), message);

        SendMessageAction sendMsgAct = new SendMessageAction(plainMsg);
        try 
        {
            Icq.requestAction(sendMsgAct);
        } 
        catch (Exception e) {}
    }

    private static Vector uins = new Vector();

    private static boolean isChecked(String uin) 
    {
        for (int i = 0; i < uins.size(); i++) 
        {
            if (uins.elementAt(i).equals(uin)) 
            {
                return true;
            }
        }
        return false;
    }

    private static boolean stringEquals(String s1, String s2) 
    {
        if (s1.length() != s2.length()) 
        {
            return false;
        }
        if (s1 == s2) 
        {
            return true;
        }
        int size = s1.length();
        for (int i = 0; i < size; i++) 
        {
            if (toLowerCase(s1.charAt(i)) != toLowerCase(s2.charAt(i))) 
            {
                return false;
            }
        }
        return true;
    }

    private static char toLowerCase(char c) 
    {
        if (c >= 'A' && c <= 'Z' || c >= '\300' && c <= '\326' || c >= '\330' && c <= '\336' || c >= '\u0400' && c <= '\u042F') 
        {
            if (c <= 'Z' || c >= '\u0410' && c <= '\u042F') 
            {
                return (char)(c + 32);
            }
            if(c < '\u0410') 
            {
                return (char)(c + 80);
            }
            return (char)(c + 32);
        }
        return c;
    }

    public static boolean isSpam(Message message) 
    {
        if (!Options.getBoolean(Options.OPTION_ANTISPAM_ENABLE) ||
                (!Options.getBoolean(Options.OPTION_ANTISPAM_INVIS) &&
                !((Options.getInt(Options.OPTION_PRIVATESTATUS) == 1) ||
		(Options.getInt(Options.OPTION_PRIVATESTATUS) == 4)))) 
        {
            return false;
        }
        String uin = message.getSndrUin();
        ContactListContactItem contact = ContactList.getItembyUIN(uin);
        if ((null != contact) || isChecked(uin)) 
        {
            return false;
        }
        
        if (!(message instanceof PlainMessage)) 
        {
            return true;
        }

        String msg = ((PlainMessage)message).getText();

        if (message.getOffline()) 
        {
            return true;
        }
        if (stringEquals(msg, Options.getString(Options.OPTION_ANTISPAM_ANSWER))) 
        {
            uins.addElement(uin);
            ContactList.createTempContact(uin);
        }
        contact = new ContactListContactItem(0, 0, uin, uin, true, false);             
        sendMessage(contact, Options.getString(isChecked(uin) ? Options.OPTION_ANTISPAM_HELLO : Options.OPTION_ANTISPAM_MSG));       
        return true;
    }
   /*******************************************************************************************************/

}
