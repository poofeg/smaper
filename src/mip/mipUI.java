package mip;

import java.util.*;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import mip.comm.*;
import mip.util.ResourceBundle;
import DrawControls.TextList;
import DrawControls.VirtualList;

public class mipUI implements CommandListener
{

// Last screen constants
	 	         static private Object lastScreen;
	 	 
	 	         public static void setLastScreen(Object screen)
	 	         {
	 	                 lastScreen = screen;
	 	         }
	 	 
	 	         public static void backToLastScreen()
	 	         {
	 	                 selectScreen(lastScreen);
	 	         }
	 	 
	 	         public static void selectScreen(Object screen)
	 	         {
	 	                 if (screen instanceof VirtualList)
	 	                 {
	 	                         VirtualList vl = (VirtualList)screen;
	 	                         vl.activate(MIP.display);
	 	                 }
	 	                 else if (screen instanceof Displayable)
	 	                 {
	 	                         MIP.display.setCurrent((Displayable)screen);
	 	                 }
	 	                 else
	 	                 {
	 	                         ContactList.activate();
	 	                 }
	 	         }
	// Commands codes
	final public static int CMD_OK     = 1;
	final public static int CMD_CANCEL = 2;
	final public static int CMD_YES    = 3;
	final public static int CMD_NO     = 4;
	final public static int CMD_FIND   = 5;
	final public static int CMD_BACK   = 6;

        
	
	// Commands
	final public static Command cmdOk       = new Command(ResourceBundle.getString("ok"),            Command.OK,     1);
	final public static Command cmdCancel   = new Command(ResourceBundle.getString("cancel"),        Command.BACK,   2);
	final public static Command cmdYes      = new Command(ResourceBundle.getString("yes"),           Command.OK,     1);
	final public static Command cmdNo       = new Command(ResourceBundle.getString("no"),            Command.CANCEL, 2);
	final public static Command cmdFind     = new Command(ResourceBundle.getString("find"),          Command.OK,     1);
	final public static Command cmdBack     = new Command(ResourceBundle.getString("back"),          Command.BACK,   2);
	final public static Command cmdCopyText = new Command(ResourceBundle.getString("copy_text"),     Command.ITEM,   3);
	final public static Command cmdCopyAll  = new Command(ResourceBundle.getString("copy_all_text"), Command.ITEM,   4);  
	final public static Command cmdEdit  	 = new Command("Edit", Command.ITEM,   1);
	final public static Command cmdSelect = new Command(ResourceBundle.getString("select"), Command.OK, 2);
	final public static Command cmdPaste = new Command(ResourceBundle.getString("paste"), Command.ITEM, 3);
	
	private static Command cmdSend = new Command(ResourceBundle.getString("send"), Command.OK, 1);
	
	 //#sijapp cond.if modules_SMILES is "true" #
	 	         private static Command cmdInsertEmo = new Command(ResourceBundle.getString("insert_emotion"), Command.ITEM, 2);
	 	         //#sijapp cond.end#
	
	 private static Command cmdClearText = new Command(ResourceBundle.getString("clear"), Command.ITEM, 5);
	 static private CommandListener listener;
	 	         static private Hashtable commands = new Hashtable();
	static private mipUI _this;
	// Associate commands and commands codes 
	static 
	{
		commands.put(cmdOk,               new Integer(CMD_OK)    );
		commands.put(List.SELECT_COMMAND, new Integer(CMD_OK)    );
		commands.put(cmdCancel,           new Integer(CMD_CANCEL));
		commands.put(cmdYes,              new Integer(CMD_YES)   );
		commands.put(cmdNo,               new Integer(CMD_NO)    );
		commands.put(cmdFind,             new Integer(CMD_FIND)  );
		commands.put(cmdBack,             new Integer(CMD_BACK)  );
	}
	
	mipUI()
	{
		_this = this;
	}
	
	// Returns commands index of command
	public static int getCommandIdx(Command command)
	{
		Object result = commands.get(command);
		return (result == null) ? -1 : ((Integer)result).intValue();  
	}
	
	private static int caretPos;
	//#sijapp cond.if modules_SMILES is "true" # 
	public static void insertSmile(String str)
	{
		MIP.setDsp(MIP.DSP_CHAT);
		
		// #sijapp cond.if target is "MOTOROLA"#
		caretPos = messageTextbox.getString().length();
		// #sijapp cond.end#
		mipUI.messageTextbox.insert(str + " ", caretPos);
		
	}
	// #sijapp cond.end#
	
	// Place "object = null;" code here:
	private static void clearAll()
	{
		msgForm = null;
		aboutTextList = null;
		System.gc();
	}
	
	public void commandAction(Command c, Displayable d)
	{
		if (isControlActive(removeContactMessageBox))
		{
			if (c == cmdOk) menuRemoveContactSelected();
			else backToLastScreen();
			removeContactMessageBox = null;
		}
		
		else if ((renameTextbox != null) && (d == renameTextbox))
		{
			if (c == cmdOk) menuRenameSelected();
			else backToLastScreen();
			renameTextbox = null;
		}
		
		else if (isControlActive(removeMeMessageBox))
		{
			if (c == cmdOk) menuRemoveMeSelected();
			else backToLastScreen();
			removeMeMessageBox = null;
		}
		
		else if (c == cmdPaste)
		{
			int caretPos = mipUI.messageTextbox.getCaretPosition();
			mipUI.messageTextbox.insert(mipUI.getClipBoardText(), caretPos);
		}
		
		
		else if ((authTextbox != null) && (d == authTextbox))
		{
			if (c == cmdSend)
			{
				SystemNotice notice = null;

				/* If or if not a reason was entered
				 Though this box is used twice (reason for auth request and auth repley)
				 we have to distinguish what we wanna do requReason is used for that */
				String textBoxText = authTextbox.getString();
				String reasonText = (textBoxText == null || textBoxText.length() < 1) ? "" : textBoxText;
				
				switch (authType)
				{
				case AUTH_TYPE_DENY:
					notice = new SystemNotice(
							SystemNotice.SYS_NOTICE_AUTHORISE,
							authContactItem.getStringValue(ContactListContactItem.CONTACTITEM_UIN),
							false, reasonText);
					break;
				case AUTH_TYPE_REQ_AUTH:
					notice = new SystemNotice(
							SystemNotice.SYS_NOTICE_REQUAUTH,
							authContactItem.getStringValue(ContactListContactItem.CONTACTITEM_UIN),
							false, reasonText);
							//authContactItem.setBoolean();
					break;
				}
				
				/* Assemble the sysNotAction and request it */
				SysNoticeAction sysNotAct = new SysNoticeAction(notice);
				UpdateContactListAction updateAct = new UpdateContactListAction(
						authContactItem, UpdateContactListAction.ACTION_REQ_AUTH);

				try
				{
					Icq.requestAction(sysNotAct);
					if (authContactItem.getBooleanValue(ContactListContactItem.CONTACTITEM_IS_TEMP))
						Icq.requestAction(updateAct);
				} catch (mipException e)
				{
					mipException.handleException(e);
					if (e.isCritical())
						return;
				}
				
				authContactItem.setIntValue(ContactListContactItem.CONTACTITEM_AUTREQUESTS, 0);
				
			}

			boolean activated = ChatHistory.activateIfExists(authContactItem);
			if (!activated) ContactList.activate();

			authTextbox = null;
			authContactItem = null;
			
			return;
		}
		
		
		else if ((messageTextbox != null) && (d == messageTextbox))
		{
			if (c == cmdCancel) backToLastScreen();
			else if (c == cmdSend)
			{
				switch (textMessCurMode)
				{
				case EDITOR_MODE_MESSAGE:
					String messText = messageTextbox.getString();

					if (messText.length() != 0)
					{
						sendMessage(messText, textMessReceiver);
						messageTextbox.setString(null);
					}
					textMessReceiver.activate();	
					break;
				}
			}
			
			//#sijapp cond.if modules_SMILES is "true" #
			else if (c == cmdInsertEmo)
			{
				caretPos = messageTextbox.getCaretPosition();
				Emotions.selectEmotion(messageTextbox);
			}
			//#sijapp cond.end#
			
			else if (c == cmdClearText)
			{
				messageTextbox.setString(new String());
			}
		}
		
		
    else if ((msgForm != null) && (d == msgForm))
		{
			listener.commandAction(c, d);
			msgForm = null;
			actionTag = -1;
		}
			/*
			// "User info" -> "Copy text, Copy all"
			else if ((c == cmdCopyText) || (c == cmdCopyAll))
			{
				mipUI.setClipBoardText
				(
					"["+getCaption(infoTextList)+"]\n"
					+infoTextList.getCurrText(0, (c == cmdCopyAll))
				);
			}*/
		
		
		// "Selector"
//		if (d == lstSelector)
//		{
//			lastSelectedItemIndex = lstSelector.getSelectedIndex();
//						
//			// "Selector" -> "Ok"
//			if ((c == cmdOk) || (c == List.SELECT_COMMAND) || (c == cmdCancel)) listener.commandAction(c, d);
//			
//			lstSelector = null;
//			
//			actionTag = -1;
//		}
		
		// Message box
		
	}
	
	public static void setCaption(Object ctrl, String caption)
	{
		if (ctrl instanceof VirtualList)
		{
			VirtualList vl = (VirtualList)ctrl;
			vl.setCaption(caption);
		}
		// #sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
		else if (ctrl instanceof Displayable)
{
	 	                         ((Displayable)ctrl).setTitle(caption);
	 	                 }
		// #sijapp cond.end#
	}
	
	public static String getCaption(Object ctrl)
	{
		if (ctrl == null) return null;
		String result = null;
		if (ctrl instanceof VirtualList)
		{
			VirtualList vl = (VirtualList)ctrl;
			result = vl.getCaption();
		}
		// #sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
		else if (ctrl instanceof Displayable)
{
	 	                         result = ((Displayable)ctrl).getTitle();
	 	                 }
		// #sijapp cond.end#
		
		return result;
	}
	
	
	/////////////////////////
	//                     // 
	//     Message Box     //
	//                     //
	/////////////////////////
	static private Form msgForm;
	static private int actionTag = -1;

	public static int getCommandType(Command testCommand, int testTag)
	{
		return (actionTag == testTag) ? getCommandIdx(testCommand) : -1;
	}

	final public static int MESBOX_YESNO    = 1;
	final public static int MESBOX_OKCANCEL = 2;
	
	
	static public void messageBox(String cap, String text, int type, CommandListener listener, int tag)
	{
		clearAll();
		
		actionTag = tag;
		msgForm = new Form(cap);
		msgForm.append(text);
		
		switch (type)
		{
		case MESBOX_YESNO:
			msgForm.addCommand(cmdYes);
			msgForm.addCommand(cmdNo);
			break;
			
		case MESBOX_OKCANCEL:
			msgForm.addCommand(cmdOk);
			msgForm.addCommand(cmdCancel);
			break;
		}

		mipUI.listener = listener;
		msgForm.setCommandListener(_this);
		MIP.display.setCurrent(msgForm);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	private static TextList aboutTextList;
    
    // String for recent version
    static public String version;
    
	
	static public void about(Displayable lastDisplayable_)
	{
		if (aboutTextList == null) aboutTextList = new TextList(null);
		
		aboutTextList.setSoftNames("back", "");
		aboutTextList.lock();
		aboutTextList.clear();
		aboutTextList.setCursorMode(TextList.SEL_NONE);
		setColorScheme(aboutTextList);
	
		
		aboutTextList.setFontSize(Font.SIZE_SMALL);
		
		aboutTextList.setCaption(ResourceBundle.getString("about"));
		
		String commaAndSpace = ", "; 
	    
		StringBuffer str = new StringBuffer();
		str.append(ResourceBundle.getString("about_info"))
		
			
			.append("\n")
		    .append(ResourceBundle.getString("cell_phone")).append(": ")
		    .append(MIP.microeditionPlatform);
		
		if (MIP.microeditionProfiles != null) str.append(commaAndSpace).append(MIP.microeditionProfiles);
		
		String locale = System.getProperty("microedition.locale");
		if (locale != null) str.append(commaAndSpace).append(locale);
		
		System.gc();
		long freeMem = Runtime.getRuntime().freeMemory()/1024;
				
		str.append("\n")
		   .append(ResourceBundle.getString("free_heap")).append(": ")
		   .append(freeMem).append("kb\n")
		   .append(ResourceBundle.getString("total_mem")).append(": ")
		   .append(Runtime.getRuntime().totalMemory()/1024)
		   .append("kb\n");
		
		/*if (version != null) str.append(' ').append(version);
		else */str.append(" ...");
		
		
			aboutTextList.addBigText(str.toString(), Options.getInt(Options.OPTION_COLOR4), Font.STYLE_PLAIN, -1);
			aboutTextList.activate(MIP.display);
		
		aboutTextList.unlock();
		
	}
    	
	//////////////////////
	//                  //
	//    Clipboard     //
	//                  //
	//////////////////////
	
	static private String clipBoardText;
	
	static private String insertQuotingChars(String text, String qChars)
	{
		StringBuffer result = new StringBuffer();
		int size = text.length();
		boolean wasNewLine = true;
		for (int i = 0; i < size; i++)
		{
			char chr = text.charAt(i);
			if (wasNewLine) result.append(qChars);
			result.append(chr);
			wasNewLine = (chr == '\n');
		}
		
		return result.toString();
	}
	
	static public String getClipBoardText()
	{
		return clipBoardText;
	}
	
	static public void setClipBoardText(String text)
	{
		clipBoardText = text;
	}
	
	static public void setClipBoardText(boolean incoming, String date, String from, String text)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(from).append(' ').append('(').append(date).append(')').append(CRLFstr)
		  .append(text);
		clipBoardText = insertQuotingChars(sb.toString(), "> ");
	}
	
	static public void clearClipBoardText()
	{
		clipBoardText = null;	
	}
	
	final public static String CRLFstr = "\n";
	
	////////////////////////
	//                    //
	//    Color scheme    //
	//                    //
	////////////////////////
	
	static public void setColorScheme(VirtualList vl)
	{
		if (vl == null) return;
		
		vl.setColors
		(
			Options.getInt(Options.OPTION_COLOR9),
			Options.getInt(Options.OPTION_COLOR10),
			Options.getInt(Options.OPTION_COLOR3),
			Options.getInt(Options.OPTION_COLOR1),
			Options.getInt(Options.OPTION_COLOR9)
		);
	}
	
	static public void setColorScheme()
	{
		// #sijapp cond.if modules_HISTORY is "true" #
		HistoryStorage.setColorScheme();
		// #sijapp cond.end#
		ChatHistory.setColorScheme();
		setColorScheme((VirtualList)ContactList.getVisibleContactListRef());
	}
    
    /*****************************************************************************/
    /*****************************************************************************/
    /*****************************************************************************/


    /************************************************************************/
    /************************************************************************/
    /************************************************************************/
    
    ///////////////////
    //               //
    //    Hotkeys    //
    //               //
    ///////////////////

	static public void execHotKey(ContactListContactItem cItem, int keyCode, int type)
	{
		
		
		switch (keyCode)
		{
		case Canvas.KEY_NUM0:
			execHotKeyAction(Options.getInt(Options.OPTION_EXT_CLKEY0), cItem, type);
			break;
		case Canvas.KEY_NUM4:
			execHotKeyAction(Options.getInt(Options.OPTION_EXT_CLKEY4), cItem, type);
			break;
			
		case Canvas.KEY_NUM6:
			execHotKeyAction(Options.getInt(Options.OPTION_EXT_CLKEY6), cItem, type);
			break;

		case Canvas.KEY_POUND:
			execHotKeyAction(Options.getInt(Options.OPTION_EXT_CLKEYPOUND), cItem, type);
			break;

		// #sijapp cond.if target is "MOTOROLA"#
		case -23:
			// This means the MENU button was pressed...
			execHotKeyAction(Options.getInt(Options.OPTION_EXT_CLKEYMENU), cItem, type);
			break;
		// #sijapp cond.end#	
			
		// #sijapp cond.if target is "SIEMENS2"#
		case -11:
			// This means the CALL button was pressed...
			execHotKeyAction(Options.getInt(Options.OPTION_EXT_CLKEYCALL), cItem, type);
			break;
		// #sijapp cond.end#
		}		
	}
	
	private static long lockPressedTime = -1;
	static private void execHotKeyAction(int actionNum, ContactListContactItem item, int keyType)
	{
		if (keyType == VirtualList.KEY_PRESSED)
		{
			lockPressedTime = System.currentTimeMillis();
			
			switch (actionNum)
			{

			// #sijapp cond.if modules_HISTORY is "true" #
			case Options.HOTKEY_HISTORY:
				if (item != null) item.showHistory();
				break;
			// #sijapp cond.end#

			case Options.HOTKEY_INFO:
				if (item != null){
					mipUI.setLastScreen(VirtualList.getCurrent());
					requiestUserInfo
					(
						item.getStringValue(ContactListContactItem.CONTACTITEM_UIN),
						item.getStringValue(ContactListContactItem.CONTACTITEM_NAME)
					);
					}
				break;

			case Options.HOTKEY_NEWMSG:
				if (item != null) writeMessage(item, null);
				break;

			case Options.HOTKEY_ONOFF:
				if (Options.getBoolean(Options.OPTION_CL_HIDE_OFFLINE)) 
					Options.setBoolean(Options.OPTION_CL_HIDE_OFFLINE, false);
				else 
					Options.setBoolean(Options.OPTION_CL_HIDE_OFFLINE, true);
				Options.safe_save();
				ContactList.optionsChanged(true, false);
				ContactList.activate();
				break;

			case Options.HOTKEY_OPTIONS:
				Options.editOptions();
				break;

				
			// #sijapp cond.if target is "MIDP2"#
			case Options.HOTKEY_MINIMIZE:
				MIP.setMinimized(true);
				break;
			// #sijapp cond.end#
				
			case Options.HOTKEY_CLI_INFO:
				if (item != null) {
					ContextMenu.saveDisplayable();
					showClientInfo(item);
				}
				break;
				
				
			// #sijapp cond.if target isnot "DEFAULT" # 
                           case Options.HOTKEY_SOUNDOFF: 
                                    ContactList.changeSoundMode(false); 
                                    break; 
                            //#sijapp cond.end# 
                         

				
			//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
			case Options.HOTKEY_FULLSCR:
				boolean fsValue = !Options.getBoolean(Options.OPTION_FULL_SCREEN);
				VirtualList.setFullScreen(fsValue);
				Options.setBoolean(Options.OPTION_FULL_SCREEN, fsValue);
				Options.safe_save();
				break;
			//#sijapp cond.end#
			}
		}

		else if ((keyType == VirtualList.KEY_REPEATED) || (keyType == VirtualList.KEY_RELEASED))
		{
			if (lockPressedTime == -1) return;
			long diff = System.currentTimeMillis()-lockPressedTime;
			if ((actionNum == Options.HOTKEY_LOCK) && (diff > 900))
			{
				lockPressedTime = -1;
				SplashCanvas.lock();
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	//                                                                       //
	//               U S E R   A N D   C L I E N T   I N F O                 //
	//                                                                       //
	///////////////////////////////////////////////////////////////////////////
	
	// Information about the user
	final public static int UI_UIN        = 0;
	final public static int UI_NICK       = 1;
	final public static int UI_NAME       = 2;
	final public static int UI_EMAIL      = 3;
	final public static int UI_CITY       = 4;
	final public static int UI_STATE      = 5;
	final public static int UI_PHONE      = 6;
	final public static int UI_FAX        = 7;
	final public static int UI_ADDR       = 8;
	final public static int UI_CPHONE     = 9;
	final public static int UI_AGE        = 10;
	final public static int UI_GENDER     = 11;
	final public static int UI_HOME_PAGE  = 12;
	final public static int UI_BDAY       = 13;
	final public static int UI_W_CITY     = 14;
	final public static int UI_W_STATE    = 15;
	final public static int UI_W_PHONE    = 16;
	final public static int UI_W_FAX      = 17;
	final public static int UI_W_ADDR     = 18;
	final public static int UI_W_NAME     = 19;
	final public static int UI_W_DEP      = 20;
	final public static int UI_W_POS      = 21;
	final public static int UI_ABOUT      = 22;
	final public static int UI_INETRESTS  = 23;
	final public static int UI_AUTH       = 24;
	final public static int UI_STATUS     = 25;
	final public static int UI_ICQ_CLIENT = 26;
	final public static int UI_SIGNON     = 27;
	final public static int UI_ONLINETIME = 28;
	final public static int UI_IDLE_TIME  = 29;
	final public static int UI_REGDATA      = 30;
	final public static int UI_OFFLINE_TIME = 31;
	final public static int UI_ICQ_VERS   = 32;
	final public static int UI_INT_IP     = 33;
	final public static int UI_EXT_IP     = 34;
	final public static int UI_PORT       = 35;
	final public static int UI_UIN_LIST   = 36;
	
	//////
	final public static int UI_FIRST_NAME = 37;
	final public static int UI_LAST_NAME  = 38;
	final public static int UI_CLI_CAPS	  = 39;
	
	final public static int UI_LAST_ID    = 40;
	
	static private int uiBigTextIndex;
	static private String uiSectName = null;
	
	static private void addToTextList(String str, String langStr, TextList list)
	{
		if (uiSectName != null)
		{
			list.addBigText
			(
				ResourceBundle.getString(uiSectName),
				list.getTextColor(),
				Font.STYLE_BOLD,
				-1
			).doCRLF(-1);
			uiSectName = null;
		}
		
		list.addBigText(ResourceBundle.getString(langStr)+": ", Options.getInt(Options.OPTION_COLOR6), Font.STYLE_PLAIN, uiBigTextIndex)
		  .addBigText(str, Options.getInt(Options.OPTION_COLOR4), Font.STYLE_PLAIN, uiBigTextIndex)
		  .doCRLF(uiBigTextIndex);
		uiBigTextIndex++;
	}
	
	static private void addToTextList(int index, String[] data, String langStr, TextList list)
	{
		String str = data[index];
		if (str == null) return;
		if (str.length() == 0) return;

		addToTextList(str, langStr, list);
	}
	
	static public void fillUserInfo(String[] data, TextList list)
	{
		MIP.setDsp(MIP.DSP_PROFILE);
		uiSectName = "main_info";
		addToTextList(UI_UIN_LIST,  data, "uin",        list);
		addToTextList(UI_NICK,      data, "nick",       list);
		addToTextList(UI_NAME,      data, "name",       list);
		addToTextList(UI_GENDER,    data, "gender",     list);
		addToTextList(UI_AGE,       data, "age",        list);
		addToTextList(UI_EMAIL,     data, "email",      list);
		if (data[UI_AUTH] != null) addToTextList
		(
			data[UI_AUTH].equals("1") ? ResourceBundle.getString("yes") : ResourceBundle.getString("no"),
			"auth", list
		);
		addToTextList(UI_BDAY,      data, "birth_day",  list);
		addToTextList(UI_CPHONE,    data, "cell_phone_info", list);
		addToTextList(UI_HOME_PAGE, data, "home_page",  list);
		addToTextList(UI_ABOUT,     data, "notes",      list);
		addToTextList(UI_INETRESTS, data, "interests",  list);
		
		if (data[UI_STATUS] != null)
		{
	        int stat = Integer.parseInt(data[UI_STATUS]);
	        int imgIndex = 0;
	        if (stat == 0) imgIndex = 13; //offline
	        else if (stat == 1) imgIndex = 0; //online
	        else if (stat == 2) imgIndex = 11; //invisible
	        list
				.addBigText(ResourceBundle.getString("status") + ": ",list.getTextColor(),Font.STYLE_PLAIN, uiBigTextIndex)
				.addImage
				(
					MIP.SIcons.images.elementAt(imgIndex),
					null,
					MIP.SIcons.images.getWidth(),
					MIP.SIcons.images.getHeight(),
					uiBigTextIndex
				)
				.doCRLF(uiBigTextIndex);
	        uiBigTextIndex++;
		}
		
		uiSectName = "home_info";
		addToTextList(UI_CITY,      data, "city",  list);
		addToTextList(UI_STATE,     data, "state", list);
		addToTextList(UI_ADDR,      data, "addr",  list);
		addToTextList(UI_PHONE,     data, "phone", list);
		addToTextList(UI_FAX,       data, "fax",   list);
		
		uiSectName = "work_info";
		addToTextList(UI_W_NAME,    data, "title",    list);
		addToTextList(UI_W_DEP,     data, "depart",   list);
		addToTextList(UI_W_POS,     data, "position", list);
		addToTextList(UI_W_CITY,    data, "city",     list);
		addToTextList(UI_W_STATE,   data, "state",    list);
		addToTextList(UI_W_ADDR,    data, "addr",     list);
		addToTextList(UI_W_PHONE,   data, "phone",    list);
		addToTextList(UI_W_FAX,     data, "fax",      list);
		
		uiSectName = "dc_info";
		addToTextList(UI_ICQ_CLIENT, data, "icq_client",     list);
		addToTextList(UI_SIGNON,     data, "li_signon_time", list);
		addToTextList(UI_ONLINETIME, data, "li_online_time", list);
		addToTextList(UI_IDLE_TIME,    data, "li_idle_time",    list);
		addToTextList(UI_OFFLINE_TIME, data, "li_offline_time", list);
		addToTextList(UI_REGDATA,      data, "li_regdata_time", list);
		
		uiSectName = "DC Information";
		addToTextList(UI_ICQ_VERS, data, "ICQ version", list);
		addToTextList(UI_INT_IP,   data, "Int IP",      list);
		addToTextList(UI_EXT_IP,   data, "Ext IP",      list);
		addToTextList(UI_PORT,     data, "Port",        list);
		addToTextList(UI_CLI_CAPS, data, "Capabilities", 	list);
}
	
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	
	static private TextList infoTextList = null;
	
	static public void requiestUserInfo(String uin, String name)
	{
		
		infoTextList = getInfoTextList(uin, false);
		                  
	 	                 if (Icq.isConnected())
	 	                 {
	 	                       //  if( uin == Options.getString(Options.OPTION_UIN) ) infoTextList.addCommand(cmdEdit);
	 	 //todo fix
	 	                         RequestInfoAction act = new RequestInfoAction(uin, name);
		
		try
		{
			Icq.requestAction(act);
		}
		catch (mipException e)
		{
			mipException.handleException(e);
			if (e.isCritical()) return;
		}
		
		infoTextList.add(ResourceBundle.getString("wait"));
		showInfoTextList(infoTextList);
	 	}
	 	                 else
	 	                 {
	 	                         String[] data = new String[mipUI.UI_LAST_ID];
	 	                         data[mipUI.UI_NICK] = name;
	 	                         data[mipUI.UI_UIN_LIST] = uin;
	 	                         showUserInfo(data);
		showInfoTextList(infoTextList);
		}
		MIP.setDsp(MIP.DSP_PROFILE);
	}
	
	
	static public void showUserInfo(String[] data)
	{
		if (infoTextList == null) return;
		infoTextList.clear();
		mipUI.fillUserInfo(data, infoTextList);
	}
	
	static public TextList getInfoTextList(String caption, boolean addCommands)
	{
		infoTextList = new TextList(null);
                infoTextList.setSoftNames("back", "comlist");
		
		infoTextList.setFontSize(Font.SIZE_SMALL);
		
		infoTextList.setCaption(caption);
		
		mipUI.setColorScheme(infoTextList);
		infoTextList.setCursorMode(TextList.SEL_NONE);
		
		return infoTextList;
	}
	
	static public void showInfoTextList(TextList list)
	{
		   list.activate(MIP.display);
	}
	
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	
//	static public String[] stdSelector = {"currect_contact", "all_contact_except_this", "all_contacts" };
	
	static private List lstSelector;
	
	static private int lastSelectedItemIndex;
	
	static public void showSelector(String caption, String[] elements, CommandListener listener, int tag, boolean translateWords)
	{
		if (translateWords) for (int i = 0; i < elements.length; i++) elements[i] = ResourceBundle.getString(elements[i]);
		actionTag = tag;
		lstSelector = new List(ResourceBundle.getString(caption), List.IMPLICIT, elements, null);
		lstSelector.addCommand(cmdOk);
		lstSelector.addCommand(cmdCancel);
		lstSelector.setCommandListener(_this);
		mipUI.listener = listener;
		MIP.display.setCurrent(lstSelector);
	}
	
	static public int getLastSelIndex()
	{
		return lastSelectedItemIndex;
	}
	
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	
	static public void addMessageText(TextList textList, String text, int messTotalCounter, int uin)
	{
		//#sijapp cond.if modules_SMILES is "true" #
		Emotions.addTextWithEmotions(textList, text, Font.STYLE_PLAIN, textList.getTextColor(), messTotalCounter, uin);
		//#sijapp cond.else#
		textList.addBigText(text, textList.getTextColor(), Font.STYLE_PLAIN, messTotalCounter);
		//#sijapp cond.end#
		textList.doCRLF(messTotalCounter);
	}
	
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	
	private final static long[] statuses = 
	{
		ContactList.STATUS_AWAY,
		ContactList.STATUS_CHAT,
		ContactList.STATUS_DND,
		ContactList.STATUS_INVISIBLE,
		ContactList.STATUS_NA,
		ContactList.STATUS_OCCUPIED,
		ContactList.STATUS_OFFLINE,
		ContactList.STATUS_ONLINE,
		ContactList.STATUS_DEPRESS,
		ContactList.STATUS_EVIL,
		ContactList.STATUS_HOME,
		ContactList.STATUS_WORK,
		ContactList.STATUS_LUNCH,
		ContactList.STATUS_INVIS_ALL,
	};
	
	//#sijapp cond.if target="MOTOROLA"#
	public final static int[] st_colors =
	{
		0x00AACC,
		0xFFFF00,
		0xFF8888,
		0xBBBBBB,
		0xAA0088,
		0xFFCC66,
		0xFF0000,
		0x00FF00,
		0x888888,
		0x0000FF,
		0x00AACC,
		0xFFFF00,
		0xFF8888,
		0xBBBBBB,
		0xAA0088,
		0xFFCC66,
		0xFF0000,
		0x00FF00,
		0x888888,
		0x0000FF
	
	};
	//#sijapp cond.end#
	
	public final static int[] imageIndexes = { 5, 1, 2, 11, 3, 4, 13, 0, 6, 7, 8, 9, 10, 12 };
	
	
	public final static String[] statusStrings = 
	{
		"status_away",
		"status_chat",
		"status_dnd",
		"status_invisible",
		"status_na",	
		"status_occupied",
		"status_offline",
		"status_online",
		"status_depress",
		"status_evil",
		"status_home",
		"status_work",
		"status_lunch",
		"status_invis_all"
	};
	
	public static int getStatusIndexByImage(int imageIndex)
	{
		for (int i = 0; i < imageIndexes.length; i++) if (imageIndexes[i] == imageIndex) return i;
		return -1;
	}
	
	public static String getStatusStringByImage(int imageIndex)
	{
		int index = getStatusIndexByImage(imageIndex);
		return (index == -1) ? null : ResourceBundle.getString(statusStrings[index]);
	}
	
	public static String getXStatusStringByImage(int imageIndex)
	{
		return (ResourceBundle.getString(xs[imageIndex]));
	}
	public static String getPStatusStringByImage(int imageIndex)
	{
		return (ResourceBundle.getString(pr[imageIndex]));
	}
	private static String xs[] = {"0zz", "1zz", "2zz", "3zz", "4zz", "5zz", "6zz", "7zz", "8zz", "9zz", "10zz", "11zz", "12zz", "13zz", "14zz", "15zz", "16zz", "17zz", "18zz", "19zz", "20zz", "21zz", "22zz", "23zz", "24zz", "25zz", "26zz", "27zz", "28zz", "29zz", "30zz", "31zz", "32zz", "35zz", "36zz", "37zz", "34zz", "33zz"};
	private static String pr[] = {"pr1", "pr2", "pr3", "pr4", "pr5"};
	private static String cl[] = {"SmapeR", "Miranda IM", "Kopete", "LIcq", "QIP 2005", "QIP Infium", "SIM", "Jimm", "ICQ for Mac", "&RQ", "R&Q", "MIP IM"};
	
	public static String getCLIndex(int index)
	{
		return (cl[index]);
	}
	public static int getStatusIndex(long status)
	{
		for (int i = 0; i < statuses.length; i++) if (statuses[i] == status) return i;
		return -1;
	}
	
	public static int getStatusImageIndex(long status)
	{
		int index = getStatusIndex(status);
		return (index == -1) ? -1 : imageIndexes[index];
	}

	public static String getStatusString(long status)
	{
		int index = getStatusIndex(status);
		return (index == -1) ? null : ResourceBundle.getString(statusStrings[index]);
	}

	public static void addTextListItem(TextList list, String text, Image image, int value, boolean translate)
	{
		//if (image != null) list.addImage(image, null, value);
		String textToAdd = translate ? ResourceBundle.getString(text) : text; 
		list.addBigText((image != null) ? (" "+textToAdd) : textToAdd, list.getTextColor(), Font.STYLE_PLAIN, value);
		list.doCRLF(value);
	}
	
	public static final int SHS_TYPE_ALL   = 1;
	public static final int SHS_TYPE_EMPTY = 2;
	
	public static int[] showGroupSelector(String caption, int tag, CommandListener listener, int type, int excludeGroupId)
	{
		ContactListGroupItem[] groups = ContactList.getGroupItems();
		String[] groupNamesTmp = new String[groups.length];
		int[] groupIdsTmp = new int[groups.length];

		int index = 0;
		for (int i = 0; i < groups.length; i++)
		{
			int groupId = groups[i].getId();
			if (groupId == excludeGroupId) continue;
			switch (type)
			{
			case SHS_TYPE_EMPTY:
				ContactListContactItem[] cItems = ContactList.getGroupItems(groupId);
				if (cItems.length != 0) continue;
				break;
			}
			
			groupIdsTmp[index] = groupId;
			groupNamesTmp[index] = groups[i].getName();
			index++;
		}
		
		if (index == 0)
		{
			Alert alert = new Alert("", ResourceBundle.getString("no_availible_groups"), null, AlertType.INFO );
			alert.setTimeout(Alert.FOREVER);
			MIP.display.setCurrent(alert);
			return null;
		}
		
		String[] groupNames = new String[index];
		int[] groupIds = new int[index];

		System.arraycopy(groupIdsTmp, 0, groupIds, 0, index);
		System.arraycopy(groupNamesTmp, 0, groupNames, 0, index);
			
		showSelector(ResourceBundle.getString(caption), groupNames, listener, tag, false);
		
		return groupIds;
	}
	public static void menuRenameSelected()
	{
		String newName = renameTextbox.getString();
		if ((newName == null) || (newName.length() == 0)) return;
		ContextMenu.lastCCI.rename(newName);
		messageTextbox.setString(null);
	}
	
	////////////////////////////////
	
	public static Object getCurrentScreen()
	{
		if (VirtualList.getCurrent() != null) return VirtualList.getCurrent();
		Displayable disp = MIP.display.getCurrent();
		if ((disp == null) || (disp instanceof Canvas)) return null;
		return disp;
	}
	
	public static void menuRemoveContactSelected()
	{
		String uin = ContextMenu.lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_UIN);
		ChatHistory.chatHistoryDelete(uin);
		boolean ok = Icq.delFromContactList(ContextMenu.lastCCI);
		if (ok)
		{
			//#sijapp cond.if modules_HISTORY is "true" #
			HistoryStorage.clearHistory(uin);
			//#sijapp cond.end#
		}
	}
	
	public static void menuRemoveMeSelected()
	{
		RemoveMeAction remAct = new RemoveMeAction(ContextMenu.lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_UIN));

		try
		{
			Icq.requestAction(remAct);
		} 
		catch (mipException e)
		{
			mipException.handleException(e);
			if (e.isCritical()) return;
		}
		ContactList.activate();
	}

	public static void showClientInfo(ContactListContactItem cItem)
	{
		//ContextMenu.saveDisplayable();
		TextList tlist = mipUI.getInfoTextList(
				cItem.getStringValue(ContactListContactItem.CONTACTITEM_UIN), true);
		String[] clInfoData = new String[mipUI.UI_LAST_ID];

		/* sign on time */
		long signonTime = cItem.getIntValue(ContactListContactItem.CONTACTITEM_SIGNON);
		if (signonTime > 0)
			clInfoData[mipUI.UI_SIGNON] = Util
					.getDateString(false, signonTime, true);

		/* online time */
		long onlineTime = cItem.getIntValue(ContactListContactItem.CONTACTITEM_ONLINE);
		if (onlineTime > 0)
			clInfoData[mipUI.UI_ONLINETIME] = Util
					.longitudeToString(onlineTime);

		/* idle time */
		int idleTime = cItem.getIntValue(ContactListContactItem.CONTACTITEM_IDLE);
		if (idleTime > 0)
			clInfoData[mipUI.UI_IDLE_TIME] = Util.longitudeToString(idleTime);
			
		int regdata = cItem.getIntValue(ContactListContactItem.CONTACTITEM_REGDATA);
		if (regdata > 0)
			clInfoData[mipUI.UI_REGDATA] = Util.getDateString(false, regdata, true);	
		
		//#sijapp cond.if (target="MIDP2" | target="MOTOROLA" | target="SIEMENS2") & modules_FILES="true"#

		/* Client version */
		int clientVers = cItem.getIntValue(ContactListContactItem.CONTACTITEM_CLIENT);
		if (clientVers != Icq.CLI_NONE)
			clInfoData[mipUI.UI_ICQ_CLIENT] = Icq.getClientString((byte) clientVers)
					+ " " + cItem.getStringValue(ContactListContactItem.CONTACTITEM_CLIVERSION);
		
		String offlineTime = cItem.getStringValue(ContactListContactItem.CONTACTITEM_OFFLINETIME);
		clInfoData[mipUI.UI_OFFLINE_TIME] = offlineTime;

		/* ICQ protocol version */
		clInfoData[mipUI.UI_ICQ_VERS] = Integer
				.toString(cItem.getIntValue(ContactListContactItem.CONTACTITEM_ICQ_PROT));

		/* Internal IP */
		clInfoData[mipUI.UI_INT_IP] = Util
				.ipToString(cItem.getIPValue(ContactListContactItem.CONTACTITEM_INTERNAL_IP));

		/* External IP */
		clInfoData[mipUI.UI_EXT_IP] = Util
				.ipToString(cItem.getIPValue(ContactListContactItem.CONTACTITEM_EXTERNAL_IP));

		/* Port */
		int port = cItem.getIntValue(ContactListContactItem.CONTACTITEM_DC_PORT);
		if (port != 0)
			clInfoData[mipUI.UI_PORT] = Integer.toString(port);
		//#sijapp cond.end#

		mipUI.fillUserInfo(clInfoData, tlist);
		mipUI.showInfoTextList(tlist);
	}
	
	public static void addTextListItem(TextList list, String text, Image image, int value)
	{
		//if (image != null) list.addImage(image, null, value);
		//TODO: ADD IMAGE FIX
		list.addBigText(" "+ResourceBundle.getString(text), list.getTextColor(), Font.STYLE_PLAIN, value);
		list.doCRLF(value);
	}
	
	//////
	
	static public boolean isControlActive(VirtualList list)
	{
		if (list == null) return false;
		return list.isActive();
	}
	
	//
	// Text editor for messages
	//
	
	/* Size of text area for entering mesages */
	final public static int MAX_EDITOR_TEXT_SIZE = 2000;
	
	/* Textbox for entering messages */
	public static TextBox messageTextbox;
	
	/* receiver for text message */
	private static ContactListContactItem textMessReceiver;
	
	/* Modes constant for text editor */
	final private static int EDITOR_MODE_MESSAGE = 200001;
	
	/* Current text editor mode */
	private static int textMessCurMode; 
	
	private static void removeTextMessageCommands()
	{
		messageTextbox.removeCommand(cmdSend);
		messageTextbox.removeCommand(cmdPaste);
		messageTextbox.removeCommand(cmdCancel);
	}
	
	/* Write message */
	public static void writeMessage(ContactListContactItem receiver, String initText)
	{
		if (messageTextbox == null) messageTextbox = new TextBox(ResourceBundle.getString("message") + " - " + receiver.getStringValue(1), null, MAX_EDITOR_TEXT_SIZE, TextField.ANY | TextField.INITIAL_CAPS_SENTENCE);
		else messageTextbox.setTitle(ResourceBundle.getString("message") + " - " + receiver.getStringValue(1));
		
		textMessReceiver = receiver;
		textMessCurMode = EDITOR_MODE_MESSAGE;
		
		removeTextMessageCommands();
		messageTextbox.addCommand(cmdSend);
		if (mipUI.getClipBoardText() != null) messageTextbox.addCommand(cmdPaste);
		messageTextbox.addCommand(cmdCancel);
		messageTextbox.addCommand(cmdClearText);
		
		//#sijapp cond.if modules_SMILES is "true" #
                if (MIP.animationData != null)
		messageTextbox.addCommand(cmdInsertEmo);
		//#sijapp cond.end#
		
		//messageTextbox.addCommand(cmdInsTemplate);
		
		if (initText != null) messageTextbox.setString(initText);
		messageTextbox.setCommandListener(_this);
		MIP.display.setCurrent(messageTextbox);
	}

	public static void sendMessage(String text, ContactListContactItem textMessReceiver)
	{
		/* Construct plain message object and request new SendMessageAction
		 Add the new message to the chat history */

		if (text == null)
			return;
		if (text.length() == 0)
			return;

		PlainMessage plainMsg = new PlainMessage(Options.getString(Options.OPTION_UIN), textMessReceiver, Message.MESSAGE_TYPE_NORM, Util.createCurrentDate(false), text);

		SendMessageAction sendMsgAct = new SendMessageAction(plainMsg);
		try
		{
			Icq.requestAction(sendMsgAct);
		} catch (mipException e)
		{
			mipException.handleException(e);
			if (e.isCritical())
				return;
		}
		ChatHistory.addMyMessage(textMessReceiver, text, plainMsg.getNewDate(), textMessReceiver.getStringValue(ContactListContactItem.CONTACTITEM_NAME));

		//#sijapp cond.if modules_HISTORY is "true" #
		if (Options.getBoolean(Options.OPTION_HISTORY))
			HistoryStorage.addText(textMessReceiver.getStringValue(ContactListContactItem.CONTACTITEM_UIN),
					text, (byte) 1, ResourceBundle.getString("me"), plainMsg
							.getNewDate());
		//#sijapp cond.end#
	}
        
        public static int mesBoxCount(VirtualList vl) {
            return vl.getMBCount();
        }
        
	/////////////////////////////////////////////////////

	//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
	private static TextList URLList;
	private static Object lastScreenBeforeUrlSelect;
/*	
	public static void gotoURL(String msg, Object lastScreen)
	{
		lastScreenBeforeUrlSelect = lastScreen;
		Vector v = Util.parseMessageForURL(msg);
		if (v.size() == 1)
		{
			try
			{
				Jimm.jimm.platformRequest((String) v.elementAt(0));
			} catch (Exception e) {}
		}
		else
		{
			URLList = mipUI.getInfoTextList(ResourceBundle.getString("goto_url"), false);
			URLList.addCommandEx(cmdSelect, VirtualList.MENU_TYPE_RIGHT);
			URLList.addCommandEx(cmdBack, VirtualList.MENU_TYPE_RIGHT);
			URLList.setCommandListener(_this);
			for (int i = 0; i < v.size(); i++)
			{
				URLList.addBigText((String) v.elementAt(i), URLList.getTextColor(),
						Font.STYLE_PLAIN, i).doCRLF(i);
			}
			mipUI.showInfoTextList(URLList);
		}
	}
	//#sijapp cond.end#
	*/
	///////////////////////////////////////////////////////////
	
	private static int authType;
	private static TextBox authTextbox;
	private static ContactListContactItem authContactItem;

	public static final int AUTH_TYPE_DENY = 10001;
	public static final int AUTH_TYPE_REQ_AUTH = 10002;
	
	public static void authMessage(int authType, ContactListContactItem contactItem, String caption, String text)
	{
		mipUI.authType = authType;
		authContactItem = contactItem;
		//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
		authTextbox = new TextBox(ResourceBundle.getString(caption), ResourceBundle.getString(text), 500, TextField.ANY | TextField.INITIAL_CAPS_SENTENCE);
		//#sijapp cond.else#
		authTextbox = new TextBox(ResourceBundle.getString(caption), ResourceBundle.getString(text), 500, TextField.ANY);
		//#sijapp cond.end#

		
		authTextbox.addCommand(cmdSend);
		authTextbox.addCommand(cmdCancel);
		authTextbox.setCommandListener(_this);
		MIP.display.setCurrent(authTextbox);
	}
	
	/////////////////////////////////////////////////////////////
	
	private static TextList removeContactMessageBox;
	private static TextList removeMeMessageBox;
	private static TextBox renameTextbox;
	
	
	
}
