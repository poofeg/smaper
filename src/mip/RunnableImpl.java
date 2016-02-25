package mip;

import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.*;

//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
import javax.microedition.media.PlayerListener;
import javax.microedition.media.Player;
//#sijapp cond.end#

import mip.comm.Message;
import mip.comm.ActionListener;
import mip.ContactListContactItem;


public class RunnableImpl implements Runnable
{
	private int type;
	private Object[] data;
	private static MIDlet midlet;
	
	final static private int TYPE_ADD_MSG             = 1;
	final static public  int TYPE_SET_CAPTION         = 3;
	final static public  int TYPE_USER_OFFLINE        = 4;
	final static public  int TYPE_UPDATE_CONTACT_LIST = 5;
	final static public  int TYPE_SHOW_USER_INFO      = 6;
	final static public  int TYPE_UPDATE_CL_CAPTION   = 7;
	final static public  int TYPE_ADDCONTACT          = 8;
	final static public  int TYPE_USER_IS_TYPING      = 9;
	final static public  int TYPE_RESET_CONTACTS      = 10;
	final static public  int TYPE_SORT      = 12;
	
	RunnableImpl(int type, Object[] data)
	{
		this.type = type;
		this.data = data;
	}
	
	/* Method run contains operations which have to be synchronized
	   with main events queue (in main thread)
	   If you want your code run in main thread, make new constant 
	   beginning of TYPE_ and write your source to switch block of 
	   RunnableImpl.run method.
	   To run you source call RunnableImpl.callSerially()
	   Note RunnableImpl.callSerially NEVER blocks calling thread */
	public void run()
	{
		switch (type)
		{
		//#sijapp cond.if target isnot "DEFAULT"#
		case TYPE_USER_IS_TYPING:
			ContactList.BeginTyping((String)data[0], getBoolean(data,1));
			break;
			//#sijapp cond.end#
			
		case TYPE_ADD_MSG:
			ContactList.addMessage((Message)data[0], getBoolean(data, 1));
			break;
			
		case TYPE_SORT:
			try{
			ContactList.contactChanged((ContactListContactItem)data[0], false, getBoolean(data, 1));}
			catch (Exception e) {}
			break;
		
		case TYPE_USER_OFFLINE:
			ContactList.update((String)data[0], ContactList.STATUS_OFFLINE);
			break;
			
		case TYPE_SHOW_USER_INFO:
			mipUI.showUserInfo((String[])data[0]);
			break;
			
		case TYPE_UPDATE_CL_CAPTION:
			//#sijapp cond.if modules_TRAFFIC="true"#
			ContactList.updateTitle(Traffic.getSessionTraffic());
			//#sijapp cond.else#
			ContactList.updateTitle(0);
			//#sijapp cond.end#
			break;
			
		case TYPE_UPDATE_CONTACT_LIST:
			ContactList.update((String)data[0], getBoolean(data, 1), getBoolean(data, 2), getBoolean(data, 3), getInt(data, 4), getBoolean(data, 5));
			
			break;
			
		case TYPE_ADDCONTACT:
			ContactList.addContactItem((ContactListContactItem)data[0]);
			break;
		
		case TYPE_RESET_CONTACTS:
			ContactList.setStatusesOffline();
			break;
		}
	}
	
	static public void setMidlet(MIDlet midlet_)
	{
		midlet = midlet_;
	}
	
	synchronized static public void callSerially(int type, Object[] data)
	{
		Display.getDisplay(midlet).callSerially(new RunnableImpl(type, data));
	}
	
	static public void callSerially(int type, Object obj1)
	{
		callSerially(type, new Object[] {obj1});
	}
	
	static public void callSerially(int type)
	{
		callSerially(type, null);
	}
	
	static public void callSerially(int type, Object obj1, Object obj2)
	{
		callSerially(type, new Object[] {obj1, obj2});
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	static public void updateContactListCaption()
	{
		callSerially(TYPE_UPDATE_CL_CAPTION);
	}
	
	static public void addMessageSerially(Message message)
	{
		if (!ActionListener.isSpam(message))
        	{	
	boolean haveToBeepNow;
		
//#sijapp cond.if target is "MIDP2" #
		haveToBeepNow = MIP.is_phone_SE();
//#sijapp cond.elseif target="MOTOROLA"#
		haveToBeepNow = true;
//#sijapp cond.else#
		haveToBeepNow = false;
//#sijapp cond.end #
		
		callSerially(TYPE_ADD_MSG, message, new Boolean(!haveToBeepNow));
		if (haveToBeepNow) ContactList.playSoundNotification(ContactList.SOUND_TYPE_MESSAGE);
//#sijapp cond.if target is "MIDP2" #
		if (Options.getBoolean(Options.OPTION_BRING_UP)) MIP.setMinimized(false);
//#sijapp cond.end #
		}
	}
	
	static public void updateContactList (String uin, boolean statusChanged, boolean wasOnline, boolean nowOnline, int trueStatus, boolean xStatusChanged)
	{
		Object[] arguments = new Object[7];
		
		arguments[0] = uin;
		setBoolean(arguments,  1, statusChanged);
		setBoolean(arguments,  2, wasOnline);
		setBoolean(arguments,  3, nowOnline);
		setInt(arguments,  4, trueStatus);
		setBoolean(arguments,  5, xStatusChanged);
		ContactList.checkAndPlayOnlineSound(uin, trueStatus);
		callSerially(TYPE_UPDATE_CONTACT_LIST, arguments);
		
	}
	
	//#sijapp cond.if target isnot "DEFAULT"#
	static public void BeginTyping(String uin, boolean type)
	{
		Object[] args = new Object[2];
		args[0] = uin;
		setBoolean(args,1,type);
		callSerially(TYPE_USER_IS_TYPING, args);
	}
	//#sijapp cond.end#
	
	static public void resetContactsOffline()
	{
		callSerially(TYPE_RESET_CONTACTS);
	}
	
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	
	static public void setBoolean(Object[] data, int index, boolean value)
	{
		data[index] = new Boolean(value);
	}
	
	static public boolean getBoolean(Object[] data, int index)
	{
		return (data[index] == null) ? false : ((Boolean)data[index]).booleanValue();
	}

	static public void setLong(Object[] data, int index, long value)
	{
		data[index] = new Long(value);
	}
	
	static public long getLong(Object[] data, int index)
	{
		return (data[index] == null) ? 0 : ((Long)data[index]).longValue();
	}

	static public void setInt(Object[] data, int index, int value)
	{
		data[index] = new Integer(value);
	}
	
	static public int getInt(Object[] data, int index)
	{
		return (data[index] == null) ? 0 : ((Integer)data[index]).intValue();
	}
	
	
}
