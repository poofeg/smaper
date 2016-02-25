package mip;

import DrawControls.VirtualList;
import java.io.IOException;
import java.util.TimerTask;
import javax.microedition.lcdui.*;


import java.io.DataOutputStream;
import java.io.DataInputStream;

import mip.comm.*;
import mip.util.ResourceBundle;


/* TODO: remove UI code to ChatHistory */ 
public class ContactListContactItem implements CommandListener, ContactListItem//, ActionListener
{
	
	/* No capability */
	public static final int CAP_NO_INTERNAL = 0x00000000;

	/* Message types */
	public static final int MESSAGE_PLAIN		 = 1;
	public static final int MESSAGE_URL		     = 2;
	public static final int MESSAGE_SYS_NOTICE   = 3;
	public static final int MESSAGE_AUTH_REQUEST = 4;
	
	public boolean isFlashing = false;
	
	private int    idAndGropup,
	               caps,
	               idle,
	               booleanValues,
	               messCounters;
	

	private int    typeAndClientId,
	               portAndProt,
	               intIP,
	               extIP,
				   authCookie, 
				   regdata;

	
	private int    uinLong,
				   online,
	               signOn,
	               status;
				   
	private int xStatus = -1;
	private int cliIcon = -1;
	private String name;
	private String clientVersion, lowerText, xTitle, xMessage, offlineTime;


///////////////////////////////////////////////////////////////////////////
	
	public void setStringValue(int key, String value)
	{
		switch(key)
		{
		case CONTACTITEM_UIN:        uinLong = Integer.parseInt(value); return;
		case CONTACTITEM_NAME:       name = value; lowerText = null; return;
		case CONTACTITEM_CLIVERSION: clientVersion = value; return;
		case CONTACTITEM_xTitle: xTitle = value; return;
		case CONTACTITEM_xMessage: xMessage = value; return;
		case CONTACTITEM_OFFLINETIME: offlineTime = value; return;
		}
	}
	
		// ��������� �������� ���������� ������ ��� ��������
	public Image getPrivateImage()
	{
        int index = -1;
        int ignoreIndex = (getIgnoreId() == 0) ? -1 : 2;
        int invisibleIndex = (getInvisibleId() == 0) ? -1 : 0;
        int visibleIndex = (getVisibleId() == 0) ? -1 : 1;
        
        if (Icq.isConnected()) 
		{
			if (ignoreIndex != -1)
			{
			index = ignoreIndex;
			}
			else if ((visibleIndex != -1))
			{
			index = visibleIndex;
			}
			else if (invisibleIndex != -1)
			{
			index = invisibleIndex;
			}
        }
		try
		{
			return ContactList.prIcons.elementAt(index);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	    // Privacy Lists 
	public void actionLists(int list) 
	{
            Action act = new ServerListsAction(list, ContactListContactItem.this);
            try
            {
                Icq.requestAction(act);
            } 
            catch (mipException e) 
            {
                mipException.handleException(e);
            }
            actionMMCLAct();
        }
			public void actionMMCLAct() {
                /* Active CL */
                ContactList.activate();
                }
        
	
	public String getStringValue(int key)
	{
		switch(key)
		{
		case CONTACTITEM_UIN:        return Integer.toString(uinLong);
		case CONTACTITEM_NAME:       return name;
		case CONTACTITEM_CLIVERSION: return clientVersion;
		case CONTACTITEM_xTitle: return xTitle;
		case CONTACTITEM_xMessage: return xMessage;
		case CONTACTITEM_OFFLINETIME: return offlineTime;
		}
		return null;
	}
	
///////////////////////////////////////////////////////////////////////////
		public void setIntValue(int key, int value)
	{
		switch (key)
		{
		case CONTACTITEM_ID:
			idAndGropup = (idAndGropup&0x0000FFFF)|(value << 16);
			return;
			
		case CONTACTITEM_GROUP:
			idAndGropup = (idAndGropup&0xFFFF0000)|value;
			return;
			
		case CONTACTITEM_PLAINMESSAGES:
			messCounters = (messCounters&0x00FFFFFF)|(value << 24);
			return;
			
		case CONTACTITEM_URLMESSAGES:
			messCounters = (messCounters&0xFF00FFFF)|(value << 16);
			return;
			
		case CONTACTITEM_SYSNOTICES:
			messCounters = (messCounters&0xFFFF00FF)|(value << 8);
			return;
			
		case CONTACTITEM_AUTREQUESTS:
			messCounters = (messCounters&0xFFFFFF00)|value;
			return;
		
		case CONTACTITEM_IDLE:          idle = value;          return;
		case CONTACTITEM_CAPABILITIES:  caps = value;    	   return;
		case CONTACTITEM_STATUS:        status = value;  setStatusImage();      return;
		case CONTACTITEM_XSTATUS:		xStatus = value;  setStatusImage();		return;
		case CONTACTITEM_CLIICON:		cliIcon = value; return;
		//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#

		case CONTACTITEM_DC_TYPE:       typeAndClientId = (typeAndClientId&0xff)|((value&0xff)<<8);        return;
		case CONTACTITEM_ICQ_PROT:      portAndProt = (portAndProt&0xffff0000)|(value&0xffff);       return;
		case CONTACTITEM_DC_PORT:       portAndProt = (portAndProt&0xffff)|((value&0xffff)<<16);        return;
		case CONTACTITEM_CLIENT:     	typeAndClientId = (typeAndClientId&0xff00)|(value&0xff);      return;
		case CONTACTITEM_AUTH_COOKIE:   authCookie = value; return;

		//#sijapp cond.end #
		
		case CONTACTITEM_ONLINE:        online = value; return;
		case CONTACTITEM_SIGNON:        signOn = value; return;
		case CONTACTITEM_REGDATA:       regdata = value; return;
		}
	}
	
	public int getIntValue(int key)
	{
		switch (key)
		{
		case CONTACTITEM_ID:            return ((idAndGropup&0xFFFF0000) >> 16)&0xFFFF;
		case CONTACTITEM_GROUP:         return (idAndGropup&0x0000FFFF);
		case CONTACTITEM_PLAINMESSAGES: return ((messCounters&0xFF000000) >> 24)&0xFF;
		case CONTACTITEM_URLMESSAGES:   return ((messCounters&0x00FF0000) >> 16)&0xFF;
		case CONTACTITEM_SYSNOTICES:    return ((messCounters&0x0000FF00) >> 8)&0xFF;
		case CONTACTITEM_AUTREQUESTS:   return (messCounters&0x000000FF);
		case CONTACTITEM_IDLE:          return idle;
		case CONTACTITEM_CAPABILITIES:  return caps;
		case CONTACTITEM_XSTATUS:		return xStatus;
		case CONTACTITEM_STATUS:        return status;
		case CONTACTITEM_CLIICON:	return cliIcon;
		//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#

		case CONTACTITEM_DC_TYPE:       return ((typeAndClientId&0xff00)>>8)&0xFF;
		case CONTACTITEM_ICQ_PROT:      return portAndProt&0xffff;
		case CONTACTITEM_DC_PORT:       return ((portAndProt&0xffff0000)>>16)&0xFFFF;
		case CONTACTITEM_CLIENT:        return typeAndClientId&0xff;
		case CONTACTITEM_AUTH_COOKIE:   return authCookie;

		//#sijapp cond.end #
		case CONTACTITEM_ONLINE:        return online;
		case CONTACTITEM_SIGNON:        return signOn;
		case CONTACTITEM_REGDATA:       return regdata;
		}
		return 0;
	}
	
///////////////////////////////////////////////////////////////////////////
	
	public void setBooleanValue(int key, boolean value)
	{
		booleanValues = (booleanValues & (~key)) | (value ? key : 0x00000000);
	}
	
	public boolean getBooleanValue(int key)
	{
		return (booleanValues&key) != 0;
	}
	
///////////////////////////////////////////////////////////////////////////
	
	//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
	public static byte[] longIPToByteAray(int value)
	{
		if (value == 0) return null;
		return new byte[] 
		                { 
							(byte)( value&0x000000FF),
							(byte)((value&0x0000FF00) >> 8),
							(byte)((value&0x00FF0000) >> 16),
							(byte)((value&0xFF000000) >> 24)
						}; 
	}
	
	public static int arrayToLongIP(byte[] array)
	{
		if ((array == null) || (array.length < 4)) return 0;
		return   (int)array[0] & 0xFF       | 
		       (((int)array[1] & 0xFF)<< 8) | 
		       (((int)array[2] & 0xFF)<<16) |
		       (((int)array[3] & 0xFF)<<24);
	}

	public void setIPValue(int key, byte[] value)
	{
		switch (key)
		{
		case CONTACTITEM_INTERNAL_IP: intIP = arrayToLongIP(value); break;
		case CONTACTITEM_EXTERNAL_IP: extIP = arrayToLongIP(value); break;
		}
	}
	
	public byte[] getIPValue(int key)
	{
		switch (key)
		{
		case CONTACTITEM_INTERNAL_IP: return longIPToByteAray(intIP);
		case CONTACTITEM_EXTERNAL_IP: return longIPToByteAray(extIP);
		}
		return null;
	}
	
	//#sijapp cond.end #
	
		public void saveToStream(DataOutputStream stream) throws IOException
	{
		stream.writeByte(0);
		stream.writeInt(idAndGropup);
		stream.writeByte(booleanValues&(CONTACTITEM_IS_TEMP|CONTACTITEM_NO_AUTH));
		stream.writeInt(uinLong);
		stream.writeInt(cliIcon);
		stream.writeUTF(name);
        // Privacy lists
        stream.writeInt(getVisibleId());
        stream.writeInt(getInvisibleId());
        stream.writeInt(getIgnoreId());
	}

	
		public void loadFromStream(DataInputStream stream) throws IOException
	{
		idAndGropup = stream.readInt();
		booleanValues = stream.readByte();
		uinLong = stream.readInt();
		cliIcon = stream.readInt();
		name = stream.readUTF();
        // Privacy lists
        setVisibleId(stream.readInt());
        setInvisibleId(stream.readInt());
        setIgnoreId(stream.readInt());
	}

	    // Privacy Lists
    private int ignoreId;
    public int getIgnoreId() 
    {
        //DebugLog.addText ("Ignore ID = " + ignoreId);
        return ignoreId;
    }
    
    public void setIgnoreId(int id) 
    {
        ignoreId = id;
    }
    
    private int visibleId;
    public int getVisibleId() 
    {
        //DebugLog.addText ("Visibility = " + visibleId);
        return visibleId;
    }
    public void setVisibleId(int id) 
    {
        visibleId = id;
    }
    
    private int invisibleId;
    public int getInvisibleId() 
    {
        //DebugLog.addText ("Invisibility = " + invisibleId);
        return invisibleId;
    }
    public void setInvisibleId(int id) 
    {
        invisibleId = id;
    }


	
	/* Variable keys */
	public static final int CONTACTITEM_UIN           = 0;      /* String */
	public static final int CONTACTITEM_NAME          = 1;      /* String */
	public static final int CONTACTITEM_xTitle          = 3;      /* String */
	public static final int CONTACTITEM_xMessage          = 4;      /* String */
	
	
	public static final int CONTACTITEM_ID            = 64;     /* Integer */
	public static final int CONTACTITEM_GROUP         = 65;     /* Integer */
	public static final int CONTACTITEM_PLAINMESSAGES = 67;     /* Integer */
	public static final int CONTACTITEM_URLMESSAGES   = 68;     /* Integer */
	public static final int CONTACTITEM_SYSNOTICES    = 69;     /* Integer */
	public static final int CONTACTITEM_AUTREQUESTS   = 70;     /* Integer */
	public static final int CONTACTITEM_IDLE          = 71;     /* Integer */
	public static final int CONTACTITEM_ADDED         = 1 << 0; /* Boolean */
	public static final int CONTACTITEM_NO_AUTH       = 1 << 1; /* Boolean */
	public static final int CONTACTITEM_CHAT_SHOWN    = 1 << 2; /* Boolean */
	public static final int CONTACTITEM_IS_TEMP       = 1 << 3; /* Boolean */
	public static final int CONTACTITEM_HAS_CHAT      = 1 << 4; /* Boolean */
	public static final int CONTACTITEM_STATUS        = 192;    /* Integer */
	public static final int CONTACTITEM_XSTATUS       = 197;    /* Integer */
	public static final int CONTACTITEM_CLIICON       = 199;    /* Integer */
	public static final int CONTACTITEM_SIGNON        = 194;    /* Integer */
	public static final int CONTACTITEM_ONLINE        = 195;    /* Integer */
	public static final int CONTACTITEM_REGDATA        = 191;    /* Integer */
	
	//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
	public static final int CONTACTITEM_INTERNAL_IP   = 225;    /* IP address */
	public static final int CONTACTITEM_EXTERNAL_IP   = 226;    /* IP address */
	public static final int CONTACTITEM_AUTH_COOKIE   = 193;    /* Integer */
	public static final int CONTACTITEM_DC_TYPE       = 72;     /* Integer */
	public static final int CONTACTITEM_ICQ_PROT      = 73;     /* Integer */
	public static final int CONTACTITEM_DC_PORT       = 74;     /* Integer */
	//#sijapp cond.end#
	public static final int CONTACTITEM_CAPABILITIES  = 75;     /* Integer */
	public static final int CONTACTITEM_CLIENT        = 76;     /* Integer */
	public static final int CONTACTITEM_CLIVERSION    = 2;      /* String */	
	public static final int CONTACTITEM_OFFLINETIME = 5;


	
	public static String currentUin = new String();

	public void init(int id, int group, String uin, String name, boolean noAuth, boolean added)
	{
		if (id == -1)
			setIntValue(ContactListContactItem.CONTACTITEM_ID, Util.createRandomId());
		else
			setIntValue(ContactListContactItem.CONTACTITEM_ID, id);
		setIntValue(ContactListContactItem.CONTACTITEM_GROUP, group);
		setStringValue(ContactListContactItem.CONTACTITEM_UIN, uin);
		setStringValue(ContactListContactItem.CONTACTITEM_NAME, name);
		setBooleanValue(ContactListContactItem.CONTACTITEM_NO_AUTH, noAuth);
		setBooleanValue(ContactListContactItem.CONTACTITEM_IS_TEMP, false);
		setBooleanValue(ContactListContactItem.CONTACTITEM_HAS_CHAT, false);
		setBooleanValue(ContactListContactItem.CONTACTITEM_ADDED, added);
		setIntValue(ContactListContactItem.CONTACTITEM_STATUS, ContactList.STATUS_OFFLINE);
		setIntValue(ContactListContactItem.CONTACTITEM_CAPABILITIES, Icq.CAPF_NO_INTERNAL);
		setIntValue(ContactListContactItem.CONTACTITEM_PLAINMESSAGES, 0);
		setIntValue(ContactListContactItem.CONTACTITEM_URLMESSAGES, 0);
		setIntValue(ContactListContactItem.CONTACTITEM_SYSNOTICES, 0);
		setIntValue(ContactListContactItem.CONTACTITEM_AUTREQUESTS, 0);
		//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
		setIPValue(ContactListContactItem.CONTACTITEM_INTERNAL_IP, new byte[4]);
		setIPValue(ContactListContactItem.CONTACTITEM_EXTERNAL_IP, new byte[4]);
		setIntValue(ContactListContactItem.CONTACTITEM_DC_PORT, 0);
		setIntValue(ContactListContactItem.CONTACTITEM_DC_TYPE, 0);
		setIntValue(ContactListContactItem.CONTACTITEM_ICQ_PROT, 0);
		setIntValue(ContactListContactItem.CONTACTITEM_AUTH_COOKIE, 0);
		setIntValue(ContactListContactItem.CONTACTITEM_REGDATA, -1);
		setStringValue(ContactListContactItem.CONTACTITEM_OFFLINETIME, "");
		//#sijapp cond.end#
		setIntValue(ContactListContactItem.CONTACTITEM_SIGNON, -1);
		online = -1;
		setIntValue(ContactListContactItem.CONTACTITEM_IDLE, -1);
		setIntValue(ContactListContactItem.CONTACTITEM_CLIENT, Icq.CLI_NONE);
		setStringValue(ContactListContactItem.CONTACTITEM_CLIVERSION, "");
		
		setStringValue(ContactListContactItem.CONTACTITEM_xMessage, "");
		setStringValue(ContactListContactItem.CONTACTITEM_xTitle, "");
	}
	
	/* Constructor for an existing contact item */
	public ContactListContactItem(int id, int group, String uin, String name, boolean noAuth, boolean added)
	{
	    this.init(id,group,uin,name,noAuth,added);
	}
	
	public ContactListContactItem()
	{
	}
	
	/* Returns true if client supports given capability */
	public boolean hasCapability(int capability)
	{
		return ((capability & this.caps) != 0x00000000);
	}
	
	/* Adds a capability by its CAPF value */
	public void addCapability(int capability)
	{
		this.caps |= capability;
	}
	
	public String getLowerText()
	{
		if (lowerText == null)
		{
			lowerText = name.toLowerCase();
			if (lowerText.equals(name)) lowerText = name; // to decrease memory usage 
		}
		return lowerText;
	}
	
	/* Returns color for contact name */
	public int getTextColor()
	{
		if (getBooleanValue(CONTACTITEM_IS_TEMP)) return Options.getInt(Options.OPTION_COLOR8);
		int color = getBooleanValue(ContactListContactItem.CONTACTITEM_HAS_CHAT) 
				? Options.getInt(Options.OPTION_COLOR7)
				: Options.getInt(Options.OPTION_COLOR4); 
		return color;
	}
	
	/* Returns font style for contact name */ 
	public int getFontStyle()
	{
		return getBooleanValue(ContactListContactItem.CONTACTITEM_HAS_CHAT) ? Font.STYLE_BOLD : Font.STYLE_PLAIN;
	}
	
	public int getUIN()
	{
		return uinLong;
	}

	/* Returns image index for contact */
	public int getImageIndex()
	{
		int tempIndex = -1;
//#sijapp cond.if target isnot "DEFAULT"#		
		if (typing) return 18;
//#sijapp cond.end#			
		if (isMessageAvailable(MESSAGE_PLAIN)) tempIndex = 14;
		else if (isFlashing) tempIndex = 19;
		else if (isMessageAvailable(MESSAGE_URL)) tempIndex = 15;
		else if (isMessageAvailable(MESSAGE_AUTH_REQUEST)) tempIndex = 17;
		else if (isMessageAvailable(MESSAGE_SYS_NOTICE)) tempIndex = 16;
		else tempIndex = mipUI.getStatusImageIndex(getIntValue(ContactListContactItem.CONTACTITEM_STATUS));
		return tempIndex;
	}
	
	public int getXIndex()
	{
		return getIntValue(CONTACTITEM_XSTATUS);
	}
	
	public int getCliIcon()
	{
		return (Icq.getClientImageID(getIntValue(CONTACTITEM_CLIENT)) + 1);
	}
	
	public String getText()
	{
		if (getBooleanValue(CONTACTITEM_NO_AUTH)) return "[!] "+name; 
		return name;
	}

	
	/* Returns true if contact must be shown even user offline
	   and "hide offline" is on */
	protected boolean mustBeShownAnyWay()
	{
		return (getIntValue(ContactListContactItem.CONTACTITEM_PLAINMESSAGES) > 0) ||
			   (getIntValue(ContactListContactItem.CONTACTITEM_URLMESSAGES) > 0)   || 
			   (getIntValue(ContactListContactItem.CONTACTITEM_SYSNOTICES) > 0)	||
			   (getIntValue(ContactListContactItem.CONTACTITEM_AUTREQUESTS) > 0)   || 
			   getBooleanValue(ContactListContactItem.CONTACTITEM_IS_TEMP); 
	}

	/* Returns total count of all unread messages (messages, sys notices, urls, auths) */
	protected int getUnreadMessCount()
	{
		return getIntValue(ContactListContactItem.CONTACTITEM_PLAINMESSAGES)+
			getIntValue(ContactListContactItem.CONTACTITEM_URLMESSAGES)+
			getIntValue(ContactListContactItem.CONTACTITEM_SYSNOTICES)+
			getIntValue(ContactListContactItem.CONTACTITEM_AUTREQUESTS);
	}

	/* Returns true if the next available message is a message of given type
	   Returns false if no message at all is available, or if the next available
	   message is of another type */
	protected boolean isMessageAvailable(int type)
	{
		switch (type)
		{
			case MESSAGE_PLAIN:		   return (this.getIntValue(ContactListContactItem.CONTACTITEM_PLAINMESSAGES) > 0);
			case MESSAGE_URL:		   return (this.getIntValue(ContactListContactItem.CONTACTITEM_URLMESSAGES) > 0); 
			case MESSAGE_SYS_NOTICE:   return (this.getIntValue(ContactListContactItem.CONTACTITEM_SYSNOTICES) > 0);
			case MESSAGE_AUTH_REQUEST: return (this.getIntValue(ContactListContactItem.CONTACTITEM_AUTREQUESTS) > 0); 
		}
		return (this.getIntValue(ContactListContactItem.CONTACTITEM_PLAINMESSAGES) > 0);
	}
	
	/* Increases the mesage count */
	protected void increaseMessageCount(int type)
	{ 
		switch (type)
		{
			case MESSAGE_PLAIN:		   this.setIntValue(ContactListContactItem.CONTACTITEM_PLAINMESSAGES,this.getIntValue(ContactListContactItem.CONTACTITEM_PLAINMESSAGES)+1); break;
			case MESSAGE_URL:		   this.setIntValue(ContactListContactItem.CONTACTITEM_URLMESSAGES,this.getIntValue(ContactListContactItem.CONTACTITEM_URLMESSAGES)+1); break;
			case MESSAGE_SYS_NOTICE:   this.setIntValue(ContactListContactItem.CONTACTITEM_SYSNOTICES,this.getIntValue(ContactListContactItem.CONTACTITEM_SYSNOTICES)+1); break;
			case MESSAGE_AUTH_REQUEST: this.setIntValue(ContactListContactItem.CONTACTITEM_AUTREQUESTS,this.getIntValue(ContactListContactItem.CONTACTITEM_AUTREQUESTS)+1);
		}
	}

	/* Adds a message to the message display */
	protected void addMessage(Message message)
	{
		ChatHistory.addMessage(this, message);
	}

	public void resetUnreadMessages()
	{
		setIntValue(ContactListContactItem.CONTACTITEM_PLAINMESSAGES,0);
		setIntValue(ContactListContactItem.CONTACTITEM_URLMESSAGES,0);
		setIntValue(ContactListContactItem.CONTACTITEM_SYSNOTICES,0);
	}

	/* Checks whether some other object is equal to this one */
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ContactListContactItem)) return (false);
		ContactListContactItem ci = (ContactListContactItem) obj;
		return (this.getStringValue(ContactListContactItem.CONTACTITEM_UIN).equals(ci.getStringValue(ContactListContactItem.CONTACTITEM_UIN)) && (this.getBooleanValue(ContactListContactItem.CONTACTITEM_IS_TEMP) == ci.getBooleanValue(ContactListContactItem.CONTACTITEM_IS_TEMP)));
	}
	

	//#sijapp cond.if modules_HISTORY is "true" #
	public void showHistory()
	{
		HistoryStorage.showHistoryList(getStringValue(ContactListContactItem.CONTACTITEM_UIN), name);
	}
	//#sijapp cond.end#
	
	
//#sijapp cond.if target isnot "DEFAULT"#
	private boolean typing = false;
	public void BeginTyping(boolean type)
	{
		typing = type;
		setStatusImage();
	}
	
//#sijapp cond.end#	
	/** ************************************************************************* */
	/** ************************************************************************* */
	/** ************************************************************************* */

	
	public void setOfflineStatus()
	{
//#sijapp cond.if target isnot "DEFAULT"#
		typing = false;
//#sijapp cond.end#
		setIntValue(CONTACTITEM_STATUS, ContactList.STATUS_OFFLINE);
		setIntValue(CONTACTITEM_XSTATUS, -1);
	}

	/* Shows new message form */ 
	/* Command listener */
	public void commandAction(Command c, Displayable d)
	{
		
		if ( (c == mipUI.cmdOk) && 
			 (Options.getInt(Options.OPTION_TYPING_MODE) > 0)&&((caps&Icq.CAPF_TYPING)!=0) && Options.getBoolean(Options.OPTION_SENDMTN))
		{
		try
			{
				mip.MIP.getIcqRef().beginTyping(ContactListContactItem.this.getStringValue(ContactListContactItem.CONTACTITEM_UIN),false);
			}
			catch (Exception e){}
		}
		
		/* Delete chat history -> "Current", "Others", "All" */
//		else if (mipUI.getCommandType(c, SELECTOR_DEL_CHAT) == mipUI.CMD_OK)
//		{
//			String uin = getStringValue(ContactListContactItem.CONTACTITEM_UIN);
//			int delType = -1;
//			
//			switch (mipUI.getLastSelIndex())
//			{
//				case 0: delType = ChatHistory.DEL_TYPE_CURRENT;        break;
//				case 1: delType = ChatHistory.DEL_TYPE_ALL_EXCEPT_CUR; break;
//				case 2: delType = ChatHistory.DEL_TYPE_ALL;            break;
//			}
//			
//			ChatHistory.chatHistoryDelete(uin, delType);
//			ContactList.activate();
//			return;
//		}
		else if (mipUI.getCommandType(c, SELECTOR_DEL_CHAT) == mipUI.CMD_CANCEL)
		{
			ContactList.activate();
			return;
		}
		/* user select Ok in delete contact message box */
		else if (mipUI.getCommandType(c, MSGBS_DELETECONTACT) == mipUI.CMD_OK)
		{
			mipUI.menuRemoveContactSelected();
		}

		/* user select CANCEL in delete contact message box */
		else if (mipUI.getCommandType(c, MSGBS_DELETECONTACT) == mipUI.CMD_CANCEL)
		{
			this.activate();
		}

		/* user select Ok in delete me message box */
		else if (mipUI.getCommandType(c, MSGBS_REMOVEME) == mipUI.CMD_OK)
		{
			mipUI.menuRemoveMeSelected();
		}

		/* user select CANCEL in delete contact message box */
		else if (mipUI.getCommandType(c, MSGBS_REMOVEME) == mipUI.CMD_CANCEL)
		{
			this.activate();
		}		

		
	}
final public static int MSGBS_DELETECONTACT = 1;
	final public static int MSGBS_REMOVEME      = 2;
	final public static int SELECTOR_DEL_CHAT   = 3;
	final public static int SELECTOR_SELECT_GROUP = 4;
	/*
	public void showClientInfo()
	{
		TextList tlist = mipUI.getInfoTextList(getStringValue(ContactListContactItem.CONTACTITEM_UIN), true);
		String[] clInfoData = new String[mipUI.UI_LAST_ID];
		//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA"#
		tlist.setFontSize(Font.SIZE_MEDIUM);
		//#sijapp cond.else#
		tlist.setFontSize(Font.SIZE_SMALL);
		//#sijapp cond.end#
		
		long signonTime = getIntValue(ContactListContactItem.CONTACTITEM_SIGNON); 
		if (signonTime > 0) clInfoData[mipUI.UI_SIGNON] = Util.getDateString(false, signonTime, true);
		
		long onlineTime = getIntValue(ContactListContactItem.CONTACTITEM_ONLINE);
		if (onlineTime > 0) clInfoData[mipUI.UI_ONLINETIME] = Util.longitudeToString(onlineTime);
		
		int idleTime = getIntValue(ContactListContactItem.CONTACTITEM_IDLE);
		if (idleTime > 0) clInfoData[mipUI.UI_IDLE_TIME] = Util.longitudeToString(idleTime);
		
		//#sijapp cond.if (target="MIDP2" | target="MOTOROLA" | target="SIEMENS2") #
		
		int clientVers = getIntValue(CONTACTITEM_CLIENT);
		if (clientVers != Icq.CLI_NONE) clInfoData[mipUI.UI_ICQ_CLIENT] = Icq.getClientString((byte)clientVers)+ " " + getStringValue(CONTACTITEM_CLIVERSION);
		
		clInfoData[mipUI.UI_ICQ_VERS] = Integer.toString(getIntValue(ContactListContactItem.CONTACTITEM_ICQ_PROT));
		
		clInfoData[mipUI.UI_INT_IP] = Util.ipToString(getIPValue(ContactListContactItem.CONTACTITEM_INTERNAL_IP));
		
		clInfoData[mipUI.UI_EXT_IP] = Util.ipToString(getIPValue(ContactListContactItem.CONTACTITEM_EXTERNAL_IP));
		
		int port = getIntValue(ContactListContactItem.CONTACTITEM_DC_PORT);
		if (port != 0) clInfoData[mipUI.UI_PORT] = Integer.toString(port);
		
		//#sijapp cond.end#

		//Caps checking
		if(caps != Icq.CAPF_NO_INTERNAL)
		{
			clInfoData[mipUI.UI_CLI_CAPS]	=	"";
			if ((status & 0x00080000) == 0x00080000) {clInfoData[mipUI.UI_CLI_CAPS] += "\nUser has birthday flag!";}
			//#sijapp cond.if (target="MIDP2" | target="MOTOROLA" | target="SIEMENS2") #
			if(clientVers != Icq.CLI_NONE)
			{clInfoData[mipUI.UI_CLI_CAPS] += "\n[IM is " + Icq.getClientString((byte)clientVers) + "]";}
			//#sijapp cond.end#
			if(hasCapability(Icq.CAPF_AIM_SERVERRELAY_INTERNAL))
			{clInfoData[mipUI.UI_CLI_CAPS] += "\n[AIM ServerRelay]";}
			
			if(hasCapability(Icq.CAPF_UTF8_INTERNAL)) 
			{clInfoData[mipUI.UI_CLI_CAPS] += "\n[UTF8 Support]";}
			
			
			if(hasCapability(Icq.CAPF_XTRAZ)) 
			{clInfoData[mipUI.UI_CLI_CAPS] += "\n[xTraz Support]";}
			
			
			if(hasCapability(Icq.CAPF_TYPING)) 
			{clInfoData[mipUI.UI_CLI_CAPS] += "\n[Typing Notify]";}
		}

		MIP.setDsp(MIP.DSP_DC);
		mipUI.fillUserInfo(clInfoData, tlist);
		mipUI.showInfoTextList(tlist);
	}*/
	
	/* Sets new contact name */ 
	public void rename(String newName)
	{
		if ((newName == null) || (newName.length() == 0)) return;
		name = newName;
		lowerText = null;
		try
		{
			/* Save ContactList */
			ContactList.save();

			/* Try to save ContactList to server */
			if ( !getBooleanValue(CONTACTITEM_IS_TEMP) )
			{
				UpdateContactListAction action = new UpdateContactListAction(this, UpdateContactListAction.ACTION_RENAME);
				Icq.requestAction(action);
			}
		}
		catch (mipException je)
		{
			if (je.isCritical()) return;
		}
		catch (Exception e)
		{
			/* Do nothing */
		}

		ContactList.contactChanged(this, true, true);
		ChatHistory.contactRenamed(getStringValue(ContactListContactItem.CONTACTITEM_UIN), name);
	}
		
		
	/* Activates the contact item menu */
	public void activate()
	{
		String currentUin = getStringValue(ContactListContactItem.CONTACTITEM_UIN);
		
		//#sijapp cond.if modules_HISTORY is "true" #
		ChatHistory.fillFormHistory(this, name);
		//#sijapp cond.end#
		
		/* Display chat history */
		if (getBooleanValue(ContactListContactItem.CONTACTITEM_HAS_CHAT))
		{
			ChatTextList chat = ChatHistory.getChatHistoryAt(currentUin);
			chat.setImage(MIP.SIcons.images.elementAt(typing ? 18 : mipUI.getStatusImageIndex(getIntValue(CONTACTITEM_STATUS))), getIntValue(CONTACTITEM_XSTATUS) > -1 ? MIP.SIcons.images2.elementAt(getIntValue(CONTACTITEM_XSTATUS)) : null );
			chat.activate(true, false);
			ChatHistory.UpdateCaption(currentUin);
			MIP.setDsp(MIP.DSP_CHAT);
		}
		else
		{
			 mipUI.writeMessage(this, null);
		}
		resetUnreadMessages();
		setStatusImage();
	}
	/*
	public String getURL()
	{
		ChatHistory.getChatHistoryAt(currentUin).getURL();
		return "";
	}*/
	
	public void showMenu()
	{
			setStatusImage();
	}
	
	 public String getSortText()
 	         {
	 	                 return getLowerText();
	 	         }
	 	 
	 	         public int getSortWeight()
	 	         {
	 	                 int status = getIntValue(ContactListContactItem.CONTACTITEM_STATUS);
	 	                 if (status != ContactList.STATUS_OFFLINE) return 0;
	 	                 if (getBooleanValue(ContactListContactItem.CONTACTITEM_IS_TEMP)
	 	                                 && status == ContactList.STATUS_OFFLINE) return 20;
	 	 
	 	                 return 10;
	 	         }

	/****************************************************************************/
	/****************************************************************************/
	/****************************************************************************/
    
   /* Shows popup window with text of received message */
	static public void showPopupWindow(String uin, String name, String text)
	{
	
		if (SplashCanvas.locked()) return;
		
		boolean haveToShow = false;
		boolean chatVisible = ChatHistory.chatHistoryShown(uin);
		boolean uinEquals = uin.equals(currentUin); 
		
		switch (Options.getInt(Options.OPTION_POPUP_WIN2) )
		{
		case 0: return;
		case 1:
			haveToShow = !chatVisible & uinEquals;
			break;
		case 2:
			haveToShow = !chatVisible || (chatVisible && !uinEquals);
			break;
		}
		
		if (!haveToShow) return;
                
		VirtualList.getCurrent().showMesBox(name, text, VirtualList.MB_MESSAGE);
                
//		String textToAdd = "["+name+"]\r\n"+text;
//		
//		if (MIP.display.getCurrent() instanceof Alert)
//		{
//			Alert currAlert = (Alert)MIP.display.getCurrent();
//			if (currAlert.getImage() != null) currAlert.setImage(null);
//			currAlert.setString(currAlert.getString()+"\r\n"+textToAdd);
//			return;
//		}
//		
//		//#sijapp cond.if target is "MIDP2"#
//		String oldText = mipUI.messageTextbox.isShown() ? mipUI.messageTextbox.getString() : null;
//		//#sijapp cond.end#
//	
//		Alert alert = new Alert(ResourceBundle.getString("message"), textToAdd, null, null);
//		alert.setTimeout(Alert.FOREVER);
//		
//		MIP.display.setCurrent(alert);
//		
//		//#sijapp cond.if target is "MIDP2"#
//		if (oldText != null) mipUI.messageTextbox.setString(oldText); 
//		//#sijapp cond.end#
	}


	/* flashs form caption when current contact have changed status */
	static public void statusChanged(String uin, long status)
	{
		/*if (currentUin.equals(uin))
		{
			//#sijapp cond.if target is "MIDP2"#
			if (MIP.is_phone_SE()) return;
			//#sijapp cond.end#
			showTopLine(uin, mipUI.getStatusString(status), 12, FlashCapClass.TYPE_FLASH);
		}	 */
	}
	
	public void setStatusImage()
	{
		int imgIndex;
		
		imgIndex = typing ? 18 : mipUI.getStatusImageIndex(getIntValue(CONTACTITEM_STATUS));
		
		if (SplashCanvas.locked())
		{
			SplashCanvas.setStatusToDraw(imgIndex);
			SplashCanvas.setMessage(getStringValue(CONTACTITEM_NAME));
			SplashCanvas.Repaint();
			SplashCanvas.startTimer();
			return;
		}
		
		if (!getBooleanValue(ContactListContactItem.CONTACTITEM_HAS_CHAT)) return;
		ChatTextList chat = ChatHistory.getChatHistoryAt(getStringValue(CONTACTITEM_UIN));
		try
		{
			chat.setImage( MIP.SIcons.images.elementAt(imgIndex), getIntValue(CONTACTITEM_XSTATUS) > -1 ? MIP.SIcons.images2.elementAt(getIntValue(CONTACTITEM_XSTATUS)) : null );
			chat.repaint();
		}
		catch (Exception e) {}
	}
	
	/* Timer task for flashing form caption */
	//private static FlashCapClass lastFlashTask = null;
	public void addXtraz(String name, String message)
	{
		ChatTextList chat = ChatHistory.getChatHistoryAt(getStringValue(CONTACTITEM_UIN));
		chat.addXtraz(name, message, getXIndex());
	}
	static void showTopLine(String uin, String text, int counter, int type)
	{
		/*Displayable disp = getCurrDisplayable(uin);
		if (disp != null)
		{
			if (lastFlashTask != null)
			{
				lastFlashTask.restoreCaption();
				lastFlashTask.cancel();
			}
			lastFlashTask = new FlashCapClass(disp, text, counter, type);
			mip.MIP.getTimerRef().scheduleAtFixedRate(lastFlashTask, 0, 500);
		}*/
	}
	
	/* Initializer */
	static
	{}
	
}

class FlashCapClass extends TimerTask
{
	final static public int TYPE_FLASH    = 1;
	final static public int TYPE_CREEPING = 2;
	
	private Displayable displ;
	private String text, oldText;
	private int counter, counter2;
	private int type;
	
	public FlashCapClass(Displayable displ, String text, int counter, int type)
	{
		this.displ   = displ;
		this.text    = text;
		this.oldText = mipUI.getCaption(displ);
		this.counter = counter;
		this.type    = type;
		counter2 = 0;
	}
	
	public void run()
	{
		if ((counter != 0) && displ.isShown())
		{
			switch (type)
			{
			case TYPE_FLASH:
				mipUI.setCaption(displ, ((counter&1) == 0) ? text : " ");
				break;

			case TYPE_CREEPING:
				break;
			}
			counter--;
		}
		else
		{
			mipUI.setCaption(displ, oldText);
			cancel();
		}
	}
	
	public void restoreCaption()
	{
		mipUI.setCaption(displ, oldText);
	}

}
