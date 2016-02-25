//Class for VirtualTree-based\popup menu items...VMenuItem.
//Copyright (C) D@rkNeo, 2007.
//A part of MIP.

package mip;

import mip.util.ResourceBundle;

public class VMenuItem
{
	private String itemName;
	private int imgIndex;
	private int itemAction;
	
	//Item action types....
	
	//ContactListContactItem Actions
	public static final int VMI_ACT_MSG = 0;
	public static final int VMI_ACT_PROFILE = 1;
	public static final int VMI_ACT_SENDFILE = 2;
	public static final int VMI_ACT_SENDPHOTO = 3;
	public static final int VMI_ACT_AUTH = 4;
	public static final int VMI_ACT_READSTATUS = 5;
	public static final int VMI_ACT_READXTRAZ = 6;
	public static final int VMI_ACT_PRLISTS = 7;
	public static final int VMI_ACT_DCINFO = 8;
	public static final int VMI_ACT_DELETE = 9;
	public static final int VMI_ACT_DELETEME = 10;
	public static final int VMI_ACT_RENAME = 11;
	public static final int VMI_ACT_HISTORY = 12;
	
	//MainMenu Actions
	public static final int VMI_ACT_CONNECT = 13;
	public static final int VMI_ACT_DISCONNECT = 14;
	public static final int VMI_ACT_LOCK = 15;
	public static final int VMI_ACT_STATUS = 16;
	public static final int VMI_ACT_XTRAZ = 17;
	public static final int VMI_ACT_PRSTATUS = 18;
	public static final int VMI_ACT_LISTCTRL = 19;
	public static final int VMI_ACT_EDITPROFILE = 20;
	public static final int VMI_ACT_OPTIONS = 21;
	public static final int VMI_ACT_SOUND = 22;
	public static final int VMI_ACT_STATS = 23;
	public static final int VMI_ACT_ABOUT = 24;
	public static final int VMI_ACT_EXIT = 25;
	
	//Options Menu Actions
	public static final int VMI_ACT_ACCOUNT = 26;
	public static final int VMI_ACT_NETWORK = 27;
	public static final int VMI_ACT_INTERFACE = 28;
	public static final int VMI_ACT_HOTKEYS = 29;
	public static final int VMI_ACT_COST = 30;
	public static final int VMI_ACT_ADVSETS = 31;
	public static final int VMI_ACT_TIMEZONE = 32;
	public static final int VMI_ACT_COLORSCHEME = 33;
	
	//Common Actions
	public static final int VMI_ACT_COPY = 34;
	public static final int VMI_ACT_COPYALL = 35;
	public static final int VMI_ACT_COPYNOQUOTE = 36;
	public static final int VMI_ACT_SAVETOHISTORY = 37;
	public static final int VMI_ACT_DELETECHAT = 38;
	
	//start editing own profile
	public static final int VMI_ACT_EDITPROFILE_START = 39;
	
	public static final int VMI_ACT_SEARCHCONTACT = 40;
	public static final int VMI_ACT_ADDGROUP = 41;
	public static final int VMI_ACT_RENAMEGROUP = 42;
	public static final int VMI_ACT_DELGROUP = 43;
	
	public static final int VMI_ACT_SHOWCL = 44;
	//public static final int VMI_ACT_MINIMIZE = 45;
	public static final int VMI_ACT_NOTIFY = 46;
	public static final int VMI_ACT_STATUSMENU = 47;
	public static final int VMI_ACT_CLIENTiDMENU = 48;
	public static final int VMI_ACT_QUOTE = 49;
	public static final int VMI_ACT_COPYUIN = 50;
	public static final int VMI_WEATHERSEARCH = 51;
	public static final int VMI_ACT_ANTISPAM = 53;
	public static final int VMI_ACT_PROXY = 54;
	public static final int VMI_COLOR_1 = 60;
	public static final int VMI_COLOR_2 = 61;
	public static final int VMI_COLOR_3 = 62;
	public static final int VMI_COLOR_4 = 63;
	public static final int VMI_COLOR_5 = 64;
	public static final int VMI_COLOR_6 = 65;
	public static final int VMI_COLOR_7 = 66;
	public static final int VMI_COLOR_8 = 67;
	public static final int VMI_COLOR_9 = 68;
 	public static final int VMI_COLOR_10 = 69;
        public static final int VMI_ACT_AUTOSTATUS = 70;        
 	public static final int VMI_ACT_ADDUSER = 71;
 	public static final int VMI_ACT_GRANT = 72;     
 	public static final int VMI_ACT_MINIMIZE = 73;   
        public static final int VMI_ACT_COPYTEXT = 74;
       // History messages list 
        public static final int VMI_HIST_CLEAR = 75;
        public static final int VMI_HIST_FIND = 76;
        public static final int VMI_HIST_INFO = 77;
        public static final int VMI_HIST_EXPORT = 78;
        public static final int VMI_HIST_EXPORTALL = 79;
        public static final int VMI_HIST_COPY = 80;

        public static final int VMI_LISTS_VISIBLE = 81; 
	public static final int VMI_LISTS_INVISIBLE = 82;
        public static final int VMI_LISTS_IGNORE = 83;
        
        public static final int VMI_DELCHAT_ALL = 84;
        public static final int VMI_DELCHAT_NOTCURRENT = 85;
        public static final int VMI_DELCHAT_CURRENT = 86;        
        
        public static final int VMI_DELHIST_ALL = 87;
        public static final int VMI_DELHIST_NOTCURRENT = 88;
        public static final int VMI_DELHIST_CURRENT = 89;  

	public static final int VMI_ADDCONTACT = 90;
        public static final int VMI_SRCHSENDMSG = 91;
        public static final int VMI_SRCHINFO = 92;
        
        public static final int VMI_DIRBR_SELECT = 93;
        public static final int VMI_DIRBR_NEWDIR = 94;
        public static final int VMI_DIRBR_OPEN   = 95;
		public static final int VMI_UPLOADURL   = 97;
                
        public static final int VMI_STATUS_ONLINE     = 98;
        public static final int VMI_STATUS_CHAT       = 99;
        public static final int VMI_STATUS_OCCUPIED  = 100;
        public static final int VMI_STATUS_DND       = 101;
        public static final int VMI_STATUS_AWAY      = 102;
        public static final int VMI_STATUS_NA        = 103;
        public static final int VMI_STATUS_EVIL      = 104;
        public static final int VMI_STATUS_DEPRESS   = 105;
        public static final int VMI_STATUS_HOME      = 106;
        public static final int VMI_STATUS_WORK      = 107;
        public static final int VMI_STATUS_LUNCH     = 108;
        public static final int VMI_STATUS_INVISIBLE = 109;
        public static final int VMI_STATUS_INVIS_ALL = 110;
        
        
        public static final int VMI_ACT_THEMES       = 116;
        public static final int VMI_TEST             = 117;
        
        public static final int VMI_LIST_THEMES      = 256; // vmi list 256-319, 64 items max
	public int usedIL;
	//0 - standart IL
	//1 - status IL
	//2 - xStatus IL
	//3 - pStatus IL
	//4 - Client iD IL
	
	public String getText()
	{
		return ResourceBundle.getString(itemName);
	}
	
	public void setText(String newName)
	{
		itemName = newName;
	}
	
	public int getImageIndex()
	{
		return imgIndex;
	}
	
	public VMenuItem(String name, int index, int imageIndex, int IL)
	{
		itemName = name;
		itemAction = index;
		imgIndex = imageIndex;
		usedIL = IL;
	}
	
	public void performAction()
	{
		ContextMenu.performAction(itemAction);
	}
	
	/*
	public void performAction()
	{
		switch(itemAction)
		{
			case VMI_ACT_EDITPROFILE:
				mipUI.requiestUserInfo(Options.getString(Options.OPTION_UIN), ""); 
				break; 
			case VMI_ACT_SOUND: //Needs to be remaked
				//#sijapp cond.if target isnot "DEFAULT" # 
                boolean soundValue = ContactList.changeSoundMode( Icq.isConnected() ); 
                MainMenu.list.set(selectedIndex, getSoundValue(soundValue), ContactList.MenuItemSignaling);  
                //#sijapp cond.end# 
				break; 
			case VMI_ACT_MINIMIZE:
				//#sijapp cond.if target is "MIDP2"#
				mip.setMinimized(true);
				//#sijapp cond.end#
				break;
			case VMI_ACT_LISTCTRL:
				groupActList = new List(ResourceBundle.getString("manage_contact_list"), List.IMPLICIT);
				groupActList.append(ResourceBundle.getString("add_user"), ContactList.MenuItemAdd);
				groupActList.append(ResourceBundle.getString("search_user"), ContactList.MenuItemSearch);
				groupActList.append(ResourceBundle.getString("add_group"), ContactList.MenuItemAddGroup);
				groupActList.append(ResourceBundle.getString("rename_group"), ContactList.MenuItemRename);
				groupActList.append(ResourceBundle.getString("del_group"), ContactList.MenuItemDelete);
				groupActList.setCommandListener(_this);
				groupActList.addCommand(backCommand);
				groupActList.addCommand(selectCommand);
				MIP.display.setCurrent(groupActList);
				break;
		}
	}*/
}
