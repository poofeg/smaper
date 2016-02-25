package mip;

import java.util.*;
import javax.microedition.lcdui.*;

import mip.comm.Message;
import mip.comm.PlainMessage;
import mip.comm.SystemNotice;
import mip.comm.UrlMessage;
import mip.comm.Util;
import mip.util.ResourceBundle;
//#sijapp cond.if modules_HISTORY is "true" #
import mip.HistoryStorage;
//#sijapp cond.end#
import DrawControls.TextList;
import DrawControls.VirtualList;
import DrawControls.VirtualListCommands;

class MessData
{
	private long time;
	private int rowData;
	
	public MessData(boolean incoming, long time, int textOffset, boolean contains_url)
	{
		this.time    = time;
		this.rowData = (textOffset&0xFFFFFF)|(contains_url ? 0x8000000 : 0)|(incoming ? 0x4000000 : 0);
	}
		
	public boolean getIncoming() { return (rowData&0x4000000) != 0; }
	public long getTime() { return time; }
	public int getOffset() { return (rowData&0xFFFFFF); }
	//#sijapp cond.if target is "MIDP2" | target is "SIEMENS2" | target is "MOTOROLA"#
	public boolean isURL() { return (rowData&0x8000000) != 0; }
	//#sijapp cond.end#
}


class ChatTextList implements VirtualListCommands
{
	// UI modes
	 	         final public static int UI_MODE_NONE = 0;
	 	         final public static int UI_MODE_DEL_CHAT = 1;
	
	public TextList textList;
	
	public String ChatName;
	ContactListContactItem contact;
	private Vector messData = new Vector();
	private int messTotalCounter = 0;
	private static int currentUiMode;
	public boolean isVisible()
	{
		if (textList != null) return textList.isActive();
		return false;
	}

	public void repaint()
	{
		textList.repaint();
	}
		
	public String getURL()
	{
		try
		{
			return ((String) Util.parseMessageForURL(textList.getCurrText(0, false)).elementAt(0));
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	ChatTextList(String name, ContactListContactItem contact)
	{

		
		textList = new TextList(null);
                
		textList.setSoftNames("backcl", "comlist");
		textList.setCursorMode(TextList.SEL_NONE);
		textList.setFontSize
		(
			Options.getBoolean(Options.OPTION_CHAT_SMALL_FONT)
				   ? TextList.SMALL_FONT : TextList.MEDIUM_FONT
		);
		
		this.contact = contact;
		ChatName = name;
		mipUI.setColorScheme(textList);
		textList.setVLCommands(this);
	}
	  public void setImage(Image img, Image img2)
	 	         {
 	                 textList.setCapImage(img, img2);
					 textList.repaint();
	 	         }
	
	static int getInOutColor(boolean incoming)
	{
		return incoming ? 
		       Options.getInt(Options.OPTION_COLOR6) : 
		       Options.getInt(Options.OPTION_COLOR5);
	}

	Vector getMessData()
	{
		return messData; 
	}

	public void onCursorMove(VirtualList sender) 
	{
	}
	
	public void onItemSelected(VirtualList sender) {}
	public void onKeyPress(VirtualList sender, int keyCode,int type)
	{
		if (textList.getUIState() == 1) return;
		try // getGameAction can raise exception
		{
			if (type == VirtualList.KEY_PRESSED)
			{
				String currUin;
				switch (sender.getGameAction(keyCode))
				{
				case Canvas.LEFT:
					contact.resetUnreadMessages();
					currUin = ContactList.showNextPrevChat(false, true);
					ChatHistory.calcCounter(currUin);
					return;
				
				case Canvas.RIGHT:
					contact.resetUnreadMessages();
					currUin = ContactList.showNextPrevChat(true, true);
					ChatHistory.calcCounter(currUin);
					return;
				case Canvas.FIRE:
					contact.resetUnreadMessages();
					mipUI.writeMessage(contact, null);
					return;
				}
			}
			
			mipUI.execHotKey(contact, keyCode, type);
		}
		catch(Exception e)
		{
			// do nothing
		}
	}
	
	//#sijapp cond.if target isnot "DEFAULT"#
	private boolean typing = false;
	public void BeginTyping(boolean type)
	{
		typing = type;
		textList.repaint();
	}
	//#sijapp cond.end#
	// Add text to message form
	
	void addTextToForm(String from, String message, String url, long time, boolean red, boolean offline)
	{
		int texOffset;
		
		textList.lock();
		int lastSize = textList.getSize();
		textList.addBigText
		(
			from + " (" + Util.getDateString(!offline, time, true) + "): ",
			getInOutColor(red), 
			Font.STYLE_BOLD,
			messTotalCounter
		);
		// no new line after time!!!!
		textList.doCRLF(messTotalCounter);

		/*if (url.length() > 0)
		{
			textList.addBigText
			(
				ResourceBundle.getString("url")+": "+url, 
				0x00FF00, 
				Font.STYLE_PLAIN, messTotalCounter
			);
		}*/
		
		texOffset = textList.getSize()-lastSize;
		
		mipUI.addMessageText(textList, message, messTotalCounter, Integer.parseInt(contact.getStringValue(0)));
		boolean contains_url = false;
		if (Util.parseMessageForURL(message) != null)
   		{
   			contains_url = true;
   		}
		
		getMessData().addElement( new MessData(red, time, texOffset, contains_url) );
		messTotalCounter++;
		
		textList.setTopItem(lastSize);		
		textList.unlock();
	}
	
	public void addXtraz(String name, String message, int index)
	{
		int texOffset;
		textList.lock();
		int lastSize = textList.getSize();
		
		textList.addImage
		(
			StatusIcons.images2.elementAt(index),
			"",
			StatusIcons.images2.elementAt(index).getWidth(),
			StatusIcons.images2.elementAt(index).getHeight(),
			0
		);
		textList.addBigText
		(
			" " + name,
			getInOutColor(true), 
			Font.STYLE_BOLD,
			messTotalCounter
		);
		textList.doCRLF(messTotalCounter);
		textList.addBigText
		(
			message,
			getInOutColor(true), 
			Font.STYLE_BOLD,
			messTotalCounter
		);
		texOffset = textList.getSize()-lastSize;
		textList.doCRLF(messTotalCounter);
		getMessData().addElement( new MessData(true, 0, texOffset, false) );
		messTotalCounter++;
		
		textList.setTopItem(lastSize);		
		textList.unlock();
	}
	
	public void activate(boolean initChat, boolean resetText)
	{
		MIP.setDsp(MIP.DSP_CHAT);
		try{contact.setStatusImage();}
		catch (Exception e){}
		currentUiMode = UI_MODE_NONE;
                //textList.setSoftNames("backcl", "comlist");
		textList.activate(MIP.display);
		textList.setUIState(0);
		mipUI.setLastScreen(textList);
	}
	
	//#sijapp cond.if target is "SIEMENS2"#
	private static int lastHeight = -1;
	public void itemStateChanged(Item item)
	{
		if (item == textLine) updateChatHeight(false);
	}
	
	static void updateChatHeight(boolean force)
	{
		int height = form.getHeight()-textLine.getPreferredHeight()-4;
		if (lastHeight != height)
		{
			chatItem.setHeight(height);
			_this.textList.setForcedSize(_this.textList.getWidth(), height);
			int size = _this.textList.getSize();
			if (size != 0)
			_this.textList.setCurrentItem(size-1);
			lastHeight = height; 
		}
	}
	
	private static int caretPos;
	public void commandAction(Command c, Item item)
	{
		if (c == cmdSend)
		{
			ContactListContactItem cItem = ContactList.getItembyUIN(uin);
			String text = textLine.getString();
			textLine.setString(new String());
			updateChatHeight(true);
			cItem.sendMessage(text);
		}
		
		
		//#sijapp cond.if modules_SMILES is "true" #
		if (c == insertEmotionCommand)
		{
			caretPos = textLine.getCaretPosition();
			Emotions.selectEmotion(_this, form);
		}
		//#sijapp cond.end#
	}
	
	public void commandAction(Command c, Displayable d)
	{
		//#sijapp cond.if modules_SMILES is "true" #
		if (Emotions.isMyOkCommand(c))
		{
			textLine.insert(" " + Emotions.getSelectedEmotion() + " ", caretPos);
			//System.gc();
		}
		//#sijapp cond.end#
	}
	
	//#sijapp cond.end#
}

public class ChatHistory
{
	static public Hashtable historyTable;
	static private int counter;
	// Adds selected message to history
	//#sijapp cond.if modules_HISTORY is "true" #
	static public void addTextToHistory(String uin, String from)
	{
		ChatTextList list = getChatHistoryAt(uin);
		int textIndex = list.textList.getCurrTextIndex();
		
		MessData data = (MessData)list.getMessData().elementAt(textIndex);
		
		String text = list.textList.getCurrText(data.getOffset(), false);
		if (text == null) return;
		
		HistoryStorage.addText
		(
			uin, 
			text, 
			data.getIncoming() ? (byte)0 : (byte)1, 
			data.getIncoming() ? from : Options.getString(Options.OPTION_NICKNAME),
			data.getTime()
		);
	}
	//#sijapp cond.end#

	static
	{
		historyTable = new Hashtable();
		counter = 1;
	}
	// Adds a message to the message display
	static protected/* synchronized */ void addMessage(ContactListContactItem contact, Message message)
	{
		String uin = contact.getStringValue(ContactListContactItem.CONTACTITEM_UIN);
		if (!historyTable.containsKey(uin))
			newChatForm(contact,contact.getStringValue(ContactListContactItem.CONTACTITEM_NAME));
		
		ChatTextList chat = (ChatTextList)historyTable.get(uin);

		boolean offline = message.getOffline();
		boolean visible = chat.isVisible();

		if (message instanceof PlainMessage)
		{
			PlainMessage plainMsg = (PlainMessage) message;
			
			if (!visible) contact.increaseMessageCount(ContactListContactItem.MESSAGE_PLAIN);
			
			addTextToForm(uin,contact.getStringValue(ContactListContactItem.CONTACTITEM_NAME), plainMsg.getText(), "", plainMsg.getNewDate(), true, offline);
			
			// #sijapp cond.if modules_HISTORY is "true" #
			if ( Options.getBoolean(Options.OPTION_HISTORY) )
				HistoryStorage.addText(uin, plainMsg.getText(), (byte)0, contact.getStringValue(ContactListContactItem.CONTACTITEM_NAME), plainMsg.getNewDate());
			// #sijapp cond.end#
			
			if ( !message.getOffline() ) ContactListContactItem.showPopupWindow(uin, contact.getStringValue(ContactListContactItem.CONTACTITEM_NAME), plainMsg.getText());
			//popup fix - todo
		}
		if (message instanceof UrlMessage)
		{
			UrlMessage urlMsg = (UrlMessage) message;
			 if (!visible) contact.increaseMessageCount(ContactListContactItem.MESSAGE_URL);
			addTextToForm(uin,contact.getStringValue(ContactListContactItem.CONTACTITEM_NAME), urlMsg.getText(), urlMsg.getUrl(), urlMsg.getNewDate(), false, offline);
		}
		if (message instanceof SystemNotice)
		{
			SystemNotice notice = (SystemNotice) message;
			 if (!visible) contact.increaseMessageCount(ContactListContactItem.MESSAGE_SYS_NOTICE);

			if (notice.getSysnotetype() == SystemNotice.SYS_NOTICE_YOUWEREADDED)
			{
				addTextToForm(uin,ResourceBundle.getString("sysnotice"), ResourceBundle.getString("youwereadded")
						+ notice.getSndrUin(), "", notice.getNewDate(), false, offline);
			} else if (notice.getSysnotetype() == SystemNotice.SYS_NOTICE_AUTHREQ)
			{
				contact.increaseMessageCount(ContactListContactItem.MESSAGE_AUTH_REQUEST);
				addTextToForm(uin,ResourceBundle.getString("sysnotice"), notice.getSndrUin()
						+ ResourceBundle.getString("wantsyourauth") + notice.getReason(), "", notice.getNewDate(), false, offline);
			} else if (notice.getSysnotetype() == SystemNotice.SYS_NOTICE_AUTHREPLY)
			{
				if (notice.isAUTH_granted())
				{
					contact.setBooleanValue(ContactListContactItem.CONTACTITEM_NO_AUTH,false);
					addTextToForm(uin,ResourceBundle.getString("sysnotice"), ResourceBundle.getString("grantedby")
							+ notice.getSndrUin() + ".", "", notice.getNewDate(), false, offline);
				} else if (notice.getReason() != null)
					addTextToForm(uin,ResourceBundle.getString("sysnotice"), ResourceBundle.getString("denyedby")
							+ notice.getSndrUin() + ". " + ResourceBundle.getString("reason") + ": " + notice.getReason(),
							"", notice.getNewDate(), false, offline);
				else
					addTextToForm(uin,ResourceBundle.getString("sysnotice"), ResourceBundle.getString("denyedby")
							+ notice.getSndrUin() + ". " + ResourceBundle.getString("noreason"), "", notice.getNewDate(),
							false, offline);
			}
		}
	}
	
	public static boolean activateIfExists(ContactListContactItem item)
	{
		if (item == null) return false;
		
		ChatTextList chat = getChatHistoryAt(item.getStringValue(ContactListContactItem.CONTACTITEM_UIN));
		if (chat != null)
		{
			chat.activate(false, false);
			item.setStatusImage();
		}
		return (chat != null);
	}
	
	static protected void addMyMessage(ContactListContactItem contact, String message, long time, String ChatName)
	{
		String uin = contact.getStringValue(ContactListContactItem.CONTACTITEM_UIN);
		if (!historyTable.containsKey(uin))
			newChatForm(contact,ChatName);
		

		addTextToForm(uin,Options.getString(Options.OPTION_NICKNAME),message,"",time, false, false);
	}
	
	// Add text to message form
	static private void addTextToForm(String uin, String from,
			String message, String url, long time, boolean red, boolean offline)
	{
		ChatTextList msgDisplay = (ChatTextList) historyTable.get(uin);

		msgDisplay.addTextToForm(from, message, url, time, red, offline);
		//ContactListContactItem cItemt = ContactList.getItembyUIN(uin);
		//if ((cItemt.getBooleanValue(ContactListContactItem.CONTACTITEM_HAS_CHAT))!=true) cItemt.setBooleanValue(ContactListContactItem.CONTACTITEM_HAS_CHAT, true);
		
	}
	
	static private MessData getCurrentMessData(String uin)
	{
		ChatTextList list = getChatHistoryAt(uin);
		int messIndex = list.textList.getCurrTextIndex();
		if (messIndex == -1) return null;
		MessData md = (MessData)list.getMessData().elementAt(messIndex);
		return md;
	}
	
	static public String getCurrentMessage(String uin)
	{
		return getChatHistoryAt(uin).textList.getCurrText(getCurrentMessData(uin).getOffset(), false);
	}
	
	static public String getURL(String uin)
	{
		ChatTextList list = getChatHistoryAt(uin);
		return list.getURL();
	}
	
	static public void copyText(String uin, String from)
	{
		ChatTextList list = getChatHistoryAt(uin);
		int messIndex = list.textList.getCurrTextIndex();
		if (messIndex == -1) return;
		MessData md = (MessData)list.getMessData().elementAt(messIndex);
		
		mipUI.setClipBoardText
		(
			md.getIncoming(),
			Util.getDateString(false, md.getTime(), true),
			md.getIncoming() ? from : Options.getString(Options.OPTION_NICKNAME),
			getCurrentMessage(uin)
		);
	}

	// Returns the chat history form at the given uin
	static public ChatTextList getChatHistoryAt(String uin)
	{
		try
		{
			if (historyTable.containsKey(uin))
			{
				return (ChatTextList) historyTable.get(uin);
			}
			else
			{
				ContactListContactItem contact = ContactList.getItembyUIN(uin);
				if (contact == null) return null;
				newChatForm(contact,contact.getStringValue(ContactListContactItem.CONTACTITEM_NAME));
				return (ChatTextList) historyTable.get(uin);
			}
		}
		catch (Exception e){return null;}
	}
	
	final static public int DEL_TYPE_CURRENT        = 1;
	final static public int DEL_TYPE_ALL_EXCEPT_CUR = 2;
	final static public int DEL_TYPE_ALL            = 3;
	
	static public void chatHistoryDelete(String uin)
	{
		ContactListContactItem cItem = ContactList.getItembyUIN(uin);
		historyTable.remove(uin);
		cItem.setBooleanValue(ContactListContactItem.CONTACTITEM_HAS_CHAT, false);
		cItem.setIntValue(ContactListContactItem.CONTACTITEM_PLAINMESSAGES, 0);
		cItem.setIntValue(ContactListContactItem.CONTACTITEM_URLMESSAGES, 0);
		cItem.setIntValue(ContactListContactItem.CONTACTITEM_SYSNOTICES, 0);
	}

	// Delete the chat history for uin
	static public void chatHistoryDelete(String uin, int delType)
	{
		switch (delType)
		{
		case DEL_TYPE_CURRENT:
			chatHistoryDelete(uin);
			break;
			
		case DEL_TYPE_ALL_EXCEPT_CUR:
		case DEL_TYPE_ALL:
			Enumeration AllChats = historyTable.keys();
			while (AllChats.hasMoreElements())
			{
				String key = (String)AllChats.nextElement();
				if ((delType == DEL_TYPE_ALL_EXCEPT_CUR) && (key.equals(uin))) continue;
				chatHistoryDelete(key);
			}
			break;
		}
		System.gc();
	}

	// Returns if the chat history at the given number is shown
	static public boolean chatHistoryShown(String uin)
	{
		if (historyTable.containsKey(uin))
		{
			ChatTextList temp = (ChatTextList)historyTable.get(uin);
			 return temp.isVisible();
		}
		else
			return false;
	}

	// Returns true if chat history exists for this uin
	static public boolean chatHistoryExists(String uin)
	{
		return historyTable.containsKey(uin);
	}

	
	// Creates a new chat form
	static private void newChatForm(ContactListContactItem contact, String name)
	{
		ChatTextList chatForm = new ChatTextList(name, contact);
		String uin = contact.getStringValue(ContactListContactItem.CONTACTITEM_UIN);
		historyTable.put(uin,chatForm);
		UpdateCaption(uin);
		ContactList.getItembyUIN(uin).setBooleanValue(ContactListContactItem.CONTACTITEM_HAS_CHAT,true); ///
		//#sijapp cond.if modules_HISTORY is "true" #
		fillFormHistory(contact, name);
		//#sijapp cond.end#
	}
	
	// fill chat with last history lines
	//#sijapp cond.if modules_HISTORY is "true" #
	final static private int MAX_HIST_LAST_MESS = 5;
	static public void fillFormHistory(ContactListContactItem contact, String name)
	{
		String uin = contact.getStringValue(ContactListContactItem.CONTACTITEM_UIN);
		if (Options.getBoolean(Options.OPTION_SHOW_LAST_MESS))
		{
			int recCount = HistoryStorage.getRecordCount(uin);
			if (recCount == 0) return;
			 
			if (!chatHistoryExists(uin)) newChatForm(contact, name);
			ChatTextList chatForm = (ChatTextList) historyTable.get(uin);
			if (chatForm.textList.getSize() != 0) return;
			
			int insSize = (recCount > MAX_HIST_LAST_MESS) ? MAX_HIST_LAST_MESS : recCount;  
			for (int i = recCount-insSize; i < recCount; i++)
			{
				CachedRecord rec = HistoryStorage.getRecord(uin, i);
				chatForm.textList.addBigText
				(
					"["+rec.from+" "+rec.date+"]", 
					ChatTextList.getInOutColor(rec.type == 0),
					Font.STYLE_PLAIN,
					-1
				);
				chatForm.textList.doCRLF(-1);
				
				//#sijapp cond.if modules_SMILES is "true" #
				Emotions.addTextWithEmotions(chatForm.textList, rec.text, Font.STYLE_PLAIN, 0x808080, -1, Integer.parseInt(contact.getStringValue(0)));
				//#sijapp cond.else#
				chatForm.textList.addBigText(rec.text, 0x808080, Font.STYLE_PLAIN, -1);
				//#sijapp cond.end#
				chatForm.textList.doCRLF(-1);
			}
		}
	}
	//#sijapp cond.end#
	
	static public void contactRenamed(String uin, String newName)
	{
		ChatTextList temp = (ChatTextList) historyTable.get(uin);
		if (temp == null) return;
		temp.ChatName = newName;
		UpdateCaption(uin);
	}

	static public void UpdateCaption(String uin)
	{
		calcCounter(uin);
		ChatTextList temp = (ChatTextList) historyTable.get(uin);
		// Calculate the title for the chatdisplay.
		String Title = ContactList.getItembyUIN(uin).getStringValue(ContactListContactItem.CONTACTITEM_NAME)+" ("+counter+"/"+historyTable.size()+")";
		temp.textList.setCaption(Title);
	}

	static public void setColorScheme()
	{
		Enumeration AllChats = historyTable.elements();
		while (AllChats.hasMoreElements())
			mipUI.setColorScheme(((ChatTextList)AllChats.nextElement()).textList);
	}
	
	// Sets the counter for the ChatHistory
	static public void calcCounter(String curUin)
    {
		if (curUin == null) return;
		Enumeration AllChats = historyTable.elements();
		Object chat = historyTable.get(curUin);
		counter = 1;
		while (AllChats.hasMoreElements())
		{
			if (AllChats.nextElement() == chat) break;
			counter++;
		}
    }
}
