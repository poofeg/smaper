package mip;

import mip.comm.Message;
import mip.comm.Util;
import mip.comm.Icq;
import mip.util.ResourceBundle;

import java.util.Hashtable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.TimerTask;

import javax.microedition.lcdui.*;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;

import javax.microedition.media.PlayerListener;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.ToneControl;
import javax.microedition.media.control.VolumeControl;
import java.io.InputStream;

import DrawControls.*;

//////////////////////////////////////////////////////////////////////////////////
public class ContactList implements CommandListener, VirtualTreeCommands, VirtualListCommands, PlayerListener

{
    /* Status (all are mutual exclusive) TODO: move status to ContactListContactItem */
    public static final int STATUS_AWAY      = 0x00000001;
    public static final int STATUS_CHAT      = 0x00000020;
    public static final int STATUS_DND       = 0x00000002;
    public static final int STATUS_INVISIBLE = 0x00000100;
    public static final int STATUS_INVIS_ALL = 0x00000200;
    public static final int STATUS_NA        = 0x00000004;
    public static final int STATUS_OCCUPIED  = 0x00000010;
    public static final int STATUS_OFFLINE   = 0xFFFFFFFF;
    public static final int STATUS_NONE      = 0x10000000;
    public static final int STATUS_DEPRESS   = 0x00004000;
    public static final int STATUS_EVIL      = 0x00003000;
    public static final int STATUS_HOME      = 0x00005000;
    public static final int STATUS_ONLINE    = 0x00000000;
    public static final int STATUS_WORK      = 0x00006000;
    public static final int STATUS_LUNCH     = 0x00002001;
	
	/* XStatus constants... */
public static final byte[] XS_CAPS = Util.explodeToBytes(
	 	                 "00,01,D8,D7,EE,AC,3B,49,2A,A5,8D,D3,D8,77,E6,6B,92,"+ // ANGRY
	 	                 "01,5A,58,1E,A1,E5,80,43,0C,A0,6F,61,22,98,B7,E4,C7,"+ // DUCK
	 	                 "02,83,C9,B7,8E,77,E7,43,78,B2,C5,FB,6C,FC,C3,5B,EC,"+ // TIRED
	 	                 "03,E6,01,E4,1C,33,73,4B,D1,BC,06,81,1D,6C,32,3D,81,"+ // PARTY
	 	                 "04,8C,50,DB,AE,81,ED,47,86,AC,CA,16,CC,32,13,C7,B7,"+ // BEER
	 	                 "05,3F,B0,BD,36,AF,3B,4A,60,9E,EF,CF,19,0F,6A,5A,7F,"+ // THINKING
	 	                 "06,F8,E8,D7,B2,82,C4,41,42,90,F8,10,C6,CE,0A,89,A6,"+ // EATING
	 	                 "07,80,53,7D,E2,A4,67,4A,76,B3,54,6D,FD,07,5F,5E,C6,"+ // TV
	 	                 "08,F1,8A,B5,2E,DC,57,49,1D,99,DC,64,44,50,24,57,AF,"+ // FRIENDS
	 	                 "09,1B,78,AE,31,FA,0B,4D,38,93,D1,99,7E,EE,AF,B2,18,"+ // COFFEE
	 	                 "0A,61,BE,E0,DD,8B,DD,47,5D,8D,EE,5F,4B,AA,CF,19,A7,"+ // MUSIC
	 	                 "0B,48,8E,14,89,8A,CA,4A,08,82,AA,77,CE,7A,16,52,08,"+ // BUSINESS
	 	                 "0C,10,7A,9A,18,12,32,4D,A4,B6,CD,08,79,DB,78,0F,09,"+ // CAMERA
	 	                 "0D,6F,49,30,98,4F,7C,4A,FF,A2,76,34,A0,3B,CE,AE,A7,"+ // FUNNY
	 	                 "0E,12,92,E5,50,1B,64,4F,66,B2,06,B2,9A,F3,78,E4,8D,"+ // PHONE
	 	                 "0F,D4,A6,11,D0,8F,01,4E,C0,92,23,C5,B6,BE,C6,CC,F0,"+ // GAMES
	 	                 "10,60,9D,52,F8,A2,9A,49,A6,B2,A0,25,24,C5,E9,D2,60,"+ // COLLEGE
	 	                 "11,63,62,73,37,A0,3F,49,FF,80,E5,F7,09,CD,E0,A4,EE,"+ // SHOPPING
	 	                 "12,1F,7A,40,71,BF,3B,4E,60,BC,32,4C,57,87,B0,4C,F1,"+ // SICK
	 	                 "13,78,5E,8C,48,40,D3,4C,65,88,6F,04,CF,3F,3F,43,DF,"+ // SLEEPING
	 	                 "14,A6,ED,55,7E,6B,F7,44,D4,A5,D4,D2,E7,D9,5C,E8,1F,"+ // SURFING
	 	                 "15,12,D0,7E,3E,F8,85,48,9E,8E,97,A7,2A,65,51,E5,8D,"+ // INTERNET
	 	                 "16,BA,74,DB,3E,9E,24,43,4B,87,B6,2F,6B,8D,FE,E5,0F,"+ // ENGINEERING
	 	                 "17,63,4F,6B,D8,AD,D2,4A,A1,AA,B9,11,5B,C2,6D,05,A1,"+ // TYPING
	 	                 "18,2C,E0,E4,E5,7C,64,43,70,9C,3A,7A,1C,E8,78,A7,DC,"+ // UNK
	 	                 "19,10,11,17,C9,A3,B0,40,F9,81,AC,49,E1,59,FB,D5,D4,"+ // PPC
	 	                 "1A,16,0C,60,BB,DD,44,43,F3,91,40,05,0F,00,E6,C0,09,"+ // MOBILE
	 	                 "1B,64,43,C6,AF,22,60,45,17,B5,8C,D7,DF,8E,29,03,52,"+ // MAN
	 	                 "1C,16,F5,B7,6F,A9,D2,40,35,8C,C5,C0,84,70,3C,98,FA,"+ // WC
	 	                 "1D,63,14,36,FF,3F,8A,40,D0,A5,CB,7B,66,E0,51,B3,64,"+ // QUESTION
	 	                 "1E,B7,08,67,F5,38,25,43,27,A1,FF,CF,4C,C1,93,97,97,"+ // WAY
	 	                 "1F,DD,CF,0E,A9,71,95,40,48,A9,C6,41,32,06,D6,F2,80,"+ // HEART
	 	                 "20,D4,E2,B0,BA,33,4E,4F,A5,98,D0,11,7D,BF,4D,3C,C8,"+ // SEARCH 35
	 	                 "21,CD,56,43,A2,C9,4C,47,24,B5,2C,DC,01,24,A1,D0,CD,"+ // LOVE 36
	 	                 "22,00,72,D9,08,4A,D1,43,DD,91,99,6F,02,69,66,02,6F," +  // DIARY 37
						 "23,E6,01,E4,1C,33,73,4B,D1,BC,06,81,1D,6C,32,3D,82,"+ // SEX 34
						 "24,3F,B0,BD,36,AF,3B,4A,60,9E,EF,CF,19,0F,6A,5A,7E", // CIGARETTE 33
	 	                 ',', 16);
	

    /* Sound notification typs */
    public static final int SOUND_TYPE_MESSAGE = 1;
    public static final int SOUND_TYPE_ONLINE  = 2;
    public static final int SOUND_TYPE_TYPING  = 3;
	
	private static FlashStatus lastFlashTask = null;
	
	public static Image statusChatImg;
	public static Image eventPlainMessageImg;
    public static Image eventUrlMessageImg;
    public static Image eventSystemNoticeImg;
    public static Image eventSysActionImg;
       // Privacy Lists
    static public void update(ContactListContactItem item) 
    {
        contactChanged(item, false, false);
    }


    public static boolean playerFree	= true;
    
    private static boolean needPlayOnlineNotif = false; 
    private static boolean needPlayMessNotif = false;
    private static ContactList _this;
    
	
    /** ************************************************************************* */

    /* Version id numbers */
    static private int ssiListLastChangeTime = -1;
    static private int ssiNumberOfItems = 0;

    /* Update help variable */
    private static boolean haveToBeCleared;

    /* Contact items */
    private static Vector cItems;

    /* Group items */
    private static Vector gItems;
	
	private static Vector wItems;
    
    public static boolean treeBuilt = false;//, treeSorted = false;

    /* Contains tree nodes by groip ids */
	private static Hashtable gNodes = new Hashtable();
	
	/* Tree object */
	public static VirtualTree tree;

	/* Images for icons */
	final public static ImageList menuIcons;
    //final public static ImageList cliIcons;
	final public static ImageList prIcons;
	//
	private static int onlineCounter;

	public static void repaintTree()
	{
		tree.setCapImage(MIP.SIcons.images.elementAt(mipUI.getStatusImageIndex(Icq.getCurrentStatus())), Options.getInt(Options.OPTION_XSTATUS) == -1 ? null : MainMenu.getXImage());
		tree.repaint();
	}
	
    /* Initializer */
    static
    {
        /* Construct image objects */
        menuIcons = new ImageList();
		prIcons = new ImageList();
		//cliIcons = new ImageList();
        try
        {
        	/* reads and divides image "icons.png" to several icons */
            //cliIcons.load("/cicons.png", -1, -1, -1);
			prIcons.load("/prlists.png", -1, -1, -1);
			
			
			menuIcons.load("/micons.png", -1, -1, -1);
        } 
        catch (IOException e)
        {
            /* Do nothing */
        }
    }
	static Command okCommand;
	public static void updateCheck()
	{	
		String curr = "###VERSION###";
		if (!Icq.lastVersion.trim().endsWith(curr)) {
                    VirtualList.getCurrent().showMesBox(
                            ResourceBundle.getString("new_available"),
                            ResourceBundle.getString("current_ver") + " - " + curr +"\n" + ResourceBundle.getString("available_ver") + " " + Icq.lastVersion,
                            VirtualList.MB_OK);
//			okCommand = new Command("OK", Command.OK, 1);
////			Alert alert = new Alert
////			(
////				ResourceBundle.getString("new_available"), 
////				ResourceBundle.getString("current_ver") + " - " + curr +"\n" + ResourceBundle.getString("available_ver") + " " + Icq.lastVersion,
////				null,
////				AlertType.INFO 
////			);
////			alert.addCommand(okCommand);
////			alert.setCommandListener(_this);
////			alert.setTimeout(Alert.FOREVER);
////			MIP.display.setCurrent(alert);
		}
	}
	
    /* Constructor */
    public ContactList()
    {
    	_this = this;
        try
        {
            load();
        } catch (Exception e)
        {
            haveToBeCleared = false;
            cItems = new Vector();
            gItems = new Vector();
			wItems = new Vector();
        }
		ContactList.statusChatImg        = MIP.SIcons.images.elementAt(1);
		ContactList.eventPlainMessageImg = MIP.SIcons.images.elementAt(14);
		ContactList.eventUrlMessageImg   = MIP.SIcons.images.elementAt(15);
		ContactList.eventSystemNoticeImg = MIP.SIcons.images.elementAt(16);
		ContactList.eventSysActionImg    = MIP.SIcons.images.elementAt(17);
     
		tree = new VirtualTree(null, false);
		tree.setSoftNames("menu", "contact");
                
        tree.setImageList(getImageList());
		tree.setVTCommands(this);
		tree.setVLCommands(this);

		
//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
		tree.setFullScreenMode(true);
//#sijapp cond.end#
		tree.setFontSize(Options.getBoolean(Options.OPTION_CHAT_SMALL_FONT) ? VirtualList.SMALL_FONT : VirtualList.MEDIUM_FONT);
		tree.setStepSize( -tree.getFontHeight()/2 );
		
//#sijapp cond.if modules_TRAFFIC is "true" #
		updateTitle(Traffic.getSessionTraffic());
//#sijapp cond.else #
        updateTitle(0);
//#sijapp cond.end#
       }
    
    /* *********************************************************** */
	final static public int SORT_BY_NAME   = 1;
	final static public int SORT_BY_STATUS = 0;
	static private int sortType;
	

	public int compareNodes(TreeNode node1, TreeNode node2)
	{
		Object obj1 = node1.getData();
		Object obj2 = node2.getData();
		ContactListItem item1 = (ContactListItem) obj1;
		ContactListItem item2 = (ContactListItem) obj2;
		int result = 0;
	
		switch (sortType)
		{
		case SORT_BY_NAME: 
			result = item1.getSortText().compareTo(item2.getSortText());
			break;
		case SORT_BY_STATUS:
			int weight1 = item1.getSortWeight();
			int weight2 = item2.getSortWeight();
			if (weight1 == weight2) result = item1.getSortText().compareTo(item2.getSortText());
			else result = (weight1 < weight2) ? -1 : 1; 
			break;
		}
		
		return result;
	}
	
	/* *********************************************************** */
    
    /* Returns reference to tree */ 
     static public VirtualList getVisibleContactListRef()
    {
        return tree;
    }
	
	static public ImageList getImageList()
	{
		return MIP.SIcons.images;
	}
	
	static public ImageList getXList()
	{
		return MIP.SIcons.images2;
	}
	

    /* Returns the id number #1 which identifies (together with id number #2)
       the saved contact list version */
	static public int getSsiListLastChangeTime()
    {
        return ssiListLastChangeTime;
    }

    /* Returns the id number #2 which identifies (together with id number #1)
       the saved contact list version */
	static public int getSsiNumberOfItems()
    {
        return (ssiNumberOfItems);
    }
    
    // Returns number of contact items
    static public int getSize()
    {
    	return cItems.size();
    }
    
    static private ContactListContactItem getCItem(int index)
    {
    	return (ContactListContactItem)cItems.elementAt(index);
    }
	
    
    // Returns all contact items as array
    static public ContactListContactItem[] getContactItems()
    {
        ContactListContactItem[] cItems_ = new ContactListContactItem[cItems.size()];
        ContactList.cItems.copyInto(cItems_);
        return (cItems_);
    }
    
    // Returns all group items as array
    static public /*synchronized */ContactListGroupItem[] getGroupItems()
    {
        ContactListGroupItem[] gItems_ = new ContactListGroupItem[gItems.size()];
        ContactList.gItems.copyInto(gItems_);
        return (gItems_);
    }

    // Request display of the given alert and the main menu afterwards
    static public void activate(Alert alert)
    {
		MIP.setDsp(MIP.DSP_CL);
		tree.setUIState(0);
                ContactList.tree.activate(MIP.display, alert);
    }

    static public void activate()
	{
		MIP.setDsp(MIP.DSP_CL);
		tree.setUIState(0);
    	tree.setCapImage(MIP.SIcons.images.elementAt(mipUI.getStatusImageIndex(Icq.getCurrentStatus())), Options.getInt(Options.OPTION_XSTATUS) == -1 ? null : MainMenu.getXImage());
		//#sijapp cond.if modules_TRAFFIC is "true" #
		updateTitle(Traffic.getSessionTraffic());
		//#sijapp cond.else #
		updateTitle(0);
		//#sijapp cond.end#
		// show contact list
		tree.lock();
		buildTree();
		sortAll();
		tree.unlock();
                
		ContactList.tree.activate(MIP.display);
		mipUI.setLastScreen(ContactList.tree);

		// play sound notifications after connecting 
		if (needPlayOnlineNotif)
		{
			needPlayOnlineNotif = false;
			playSoundNotification(SOUND_TYPE_ONLINE);
		}

		if (needPlayMessNotif)
		{
			needPlayMessNotif = false;
			playSoundNotification(SOUND_TYPE_MESSAGE);
		}
	}
    
    // is called by options form when options changed
    static public void optionsChanged(boolean needToRebuildTree, boolean needToSortContacts)
    {
    	if (needToRebuildTree) treeBuilt = false;
    	//if (needToSortContacts) treeSorted = false;
    }
    
    // Tries to load contact list from record store
    static private void load() throws Exception, IOException, RecordStoreException
    {
        // Initialize vectors
    	ContactList.cItems = new Vector();
    	ContactList.gItems = new Vector();
		ContactList.wItems = new Vector();

        // Check whether record store exists
        String[] recordStores = RecordStore.listRecordStores();
        boolean exist = false;
        for (int i = 0; i < recordStores.length; i++)
        {
            if (recordStores[i].equals("contactlist"))
            {
                exist = true;
                break;
            }
        }
        if (!exist) throw (new Exception());
        
        // Open record store
        RecordStore cl = RecordStore.openRecordStore("contactlist", false);

        try
		{
            // Temporary variables
            byte[] buf;
            ByteArrayInputStream bais;
            DataInputStream dis;

            // Get version info from record store
            buf = cl.getRecord(1);
            bais = new ByteArrayInputStream(buf);
            dis = new DataInputStream(bais);
            if (!(dis.readUTF().equals(MIP.VERSION))) throw (new IOException());

            // Get version ids from the record store
            buf = cl.getRecord(2);
            bais = new ByteArrayInputStream(buf);
            dis = new DataInputStream(bais);
            ssiListLastChangeTime = dis.readInt();
            ssiNumberOfItems = dis.readUnsignedShort();
            
            // Read all remaining items from the record store
            int marker = 3;
            
			while (marker <= cl.getNumRecords())
            {

                // Get type of the next item
                buf = cl.getRecord(marker++);
                bais = new ByteArrayInputStream(buf);
                dis = new DataInputStream(bais);

                // Loop until no more items are available
                //int load = 0;
                while (dis.available() > 0)
                {
                    // Get item type
                    byte type = dis.readByte();
                    
                    // Normal contact
                    if (type == 0)
                    {
                    	
                        // Instantiate ContactListContactItem object and add to vector
                        ContactListContactItem ci = new ContactListContactItem();
                        ci.loadFromStream(dis);
                        ContactList.cItems.addElement(ci);
                    }
                    // Group of contacts
                    else if (type == 1)
                    {
                        // Instantiate ContactListGroupItem object and add to vector
                        ContactListGroupItem gi = new ContactListGroupItem();
                        gi.loadFromStream(dis);
                        ContactList.gItems.addElement(gi);
                    }
					
                }
            }
            
            
            buf = null;
            dis = null;
            bais = null;
            
            
		}
        finally
		{
        	// Close record store
        	cl.closeRecordStore(); 
			cl = null;
		}
    }

    // Save contact list to record store
    static protected void save() throws IOException, RecordStoreException
    {
        // Try to delete the record store
        try
        {
            RecordStore.deleteRecordStore("contactlist");
        } catch (RecordStoreNotFoundException e)
        {
            // Do nothing
        }

        // Create new record store
        RecordStore cl = RecordStore.openRecordStore("contactlist", true);

        // Temporary variables
        ByteArrayOutputStream baos;
        DataOutputStream dos;
        byte[] buf;

        // Add version info to record store
        baos = new ByteArrayOutputStream();
        dos = new DataOutputStream(baos);
        dos.writeUTF(MIP.VERSION);
        buf = baos.toByteArray();
        cl.addRecord(buf, 0, buf.length);

        // Add version ids to the record store
        baos.reset();
        dos.writeInt(ssiListLastChangeTime);
        dos.writeShort((short)ssiNumberOfItems);
        buf = baos.toByteArray();
        cl.addRecord(buf, 0, buf.length);

        // Initialize buffer
        baos.reset();

        // Iterate through all contact items
        int cItemsCount = cItems.size();
		int gItemsCount = gItems.size();
        int totalCount = cItemsCount+gItemsCount+wItems.size();
        for (int i = 0; i < totalCount; i++)
        {
        	if (i < cItemsCount) getCItem(i).saveToStream(dos);
        	else if (i < gItemsCount + cItemsCount)
        	{
        		ContactListGroupItem gItem = (ContactListGroupItem)gItems.elementAt(i-cItemsCount);
        		gItem.saveToStream(dos);
        	}
		
        	
            // Start new record if it exceeds 4000 bytes
            if ((baos.size() >= 4000) || (i == totalCount-1))
            {
                // Save record
                buf = baos.toByteArray();
                cl.addRecord(buf, 0, buf.length);

                // Initialize buffer
                baos.reset();
            }
        }

        // Close record store
        cl.closeRecordStore();
		cl = null;
		dos = null;
		buf = null;
		baos = null;
    }
    
    // called before mip start to connect to server
    public static void beforeConnect()
    {
    	treeBuilt  =/*= treeSorted =*/ false;
    	haveToBeCleared = true;
    	tree.clear();
		setStatusesOffline();
    }
    
    static public void setStatusesOffline()
    {
    	onlineCounter = 0;
    	for (int i = cItems.size()-1; i >= 0; i--)
    	{
    	    ContactListContactItem item = getCItem(i); 
    	    item.setOfflineStatus();
			item.setIntValue(ContactListContactItem.CONTACTITEM_XSTATUS, -1);	
    	}
    	
    	for (int i = gItems.size()-1; i >= 0; i--)
    		((ContactListGroupItem)gItems.elementAt(i)).setCounters(0, 0);
    }
    
	// Returns array of uins of unuthorized and temporary contacts
	public static String[] getUnauthAndTempContacts() 
	{
		Vector data = new Vector(); 
		for (int i = cItems.size()-1; i >= 0; i--)
		{
			if (getCItem(i).getBooleanValue(ContactListContactItem.CONTACTITEM_NO_AUTH) ||
					getCItem(i).getBooleanValue(ContactListContactItem.CONTACTITEM_IS_TEMP))
				data.addElement( getCItem(i).getStringValue(ContactListContactItem.CONTACTITEM_UIN) );
		}
		String result[] = new String[data.size()];
		data.copyInto(result);
		return result;
	}
    
    // Updates the client-side conact list (called when a new roster has been
    // received)
    static public /*synchronized*/ void update(int flags, int versionId1_, int versionId2_, ContactListItem[] items)
    {
    	
        // Remove all Elemente form the old ContactList
        if (haveToBeCleared)
        {
            cItems.removeAllElements();
            gItems.removeAllElements();
            haveToBeCleared = false;
            ssiNumberOfItems = 0;
        }

        // Add new contact items and group items
        for (int i = 0; i < items.length; i++)
        {
            if (items[i] instanceof ContactListContactItem)
            {
            	cItems.addElement(items[i]);
            } else if (items[i] instanceof ContactListGroupItem)
            {
            	gItems.addElement(items[i]);
            }
        }
        ssiNumberOfItems += versionId2_;
        
        // Save new contact list
        if (versionId1_ != 0)
        {
        	ssiListLastChangeTime = versionId1_;
        	safeSave();
        	treeBuilt = false;
        	
        	// Which contacts already have chats?
        	for (int i = getSize()-1; i >= 0; i--)
        	{
        		ContactListContactItem cItem = getCItem(i); 
        		cItem.setBooleanValue
        		(
        			ContactListContactItem.CONTACTITEM_HAS_CHAT,
        			ChatHistory.chatHistoryExists(cItem.getStringValue(ContactListContactItem.CONTACTITEM_UIN))
        		);
        	}
        }	
    }
    
    public static void safeSave()
    {
    	try
    	{
    		save();
    	}
    	catch (Exception e) 
    	{
    	}
    }
    
    //==================================//
    //                                  //
    //    WORKING WITH CONTACTS TREE    //
    //                                  //  
    //==================================//
    
    // Sorts the contacts and calc online counters
    static public void sortAll()
    {
    	//if (treeSorted) return; //new... trying to fix
    	sortType = Options.getInt(Options.OPTION_CL_SORT_BY);
    	if (Options.getBoolean(Options.OPTION_USER_GROUPS))
    	{
            for (int i = 0; i < gItems.size(); i++)
    		{
    		    ContactListGroupItem gItem = (ContactListGroupItem)gItems.elementAt(i);
    		    TreeNode groupNode = (TreeNode)gNodes.get( new Integer(gItem.getId()) );
    		    tree.sortNode(groupNode);
    		    calcGroupData(groupNode, gItem);
    		}
    	}
    	else tree.sortNode(null);
    	//treeSorted = true;
    }
    
    // Builds contacts tree (without sorting) 
    static public void buildTree()
	{
	    try{
		int i, gCount, cCount;
	    boolean use_groups  = Options.getBoolean(Options.OPTION_USER_GROUPS),
		        only_online = Options.getBoolean(Options.OPTION_CL_HIDE_OFFLINE);
			    
		cCount = cItems.size();
		gCount = gItems.size();
		if (treeBuilt || ((cCount == 0) && (gCount == 0))) return;
		
		tree.clear();
		tree.setShowButtons(use_groups);
		
		// add group nodes
		gNodes.clear();
		
		if (use_groups)
		{
			
			for (i = 0; i < gCount; i++)
			{
				ContactListGroupItem item = (ContactListGroupItem)gItems.elementAt(i);
				TreeNode groupNode = tree.addNode(null, item);
				gNodes.put(new Integer(item.getId()), groupNode);
			}
		}
		
		// add contacts
		for (i = 0; i < cCount; i++)
		{
			ContactListContactItem cItem = getCItem(i);
			
			if (only_online && 
			    (cItem.getIntValue(ContactListContactItem.CONTACTITEM_STATUS) == STATUS_OFFLINE) &&
				 !cItem.mustBeShownAnyWay()) continue;
			
			if (use_groups)
			{
			    ContactListGroupItem group = getGroupById( cItem.getIntValue(ContactListContactItem.CONTACTITEM_GROUP) );
		  	    TreeNode groupNode = (TreeNode)gNodes.get( new Integer( cItem.getIntValue(ContactListContactItem.CONTACTITEM_GROUP) ) );
		  		tree.addNode(groupNode, cItem);
			}
			else
			{
				tree.addNode(null, cItem);
			}
		}    
		//treeSorted = false;
		treeBuilt = true;}
		catch (Exception e) {}
	}

	// Returns reference to group with id or null if group not found
	public static ContactListGroupItem getGroupById(int id)
	{
		for (int i = gItems.size()-1; i >= 0; i--)
		{
			ContactListGroupItem group = (ContactListGroupItem) gItems.elementAt(i);
			if (group.getId() == id) return group;
		}
		return null;
	}
   
	// Returns reference to contact item with uin or null if not found  
	static public ContactListContactItem getItembyUIN(String uin)
    {
		int uinInt = Integer.parseInt(uin);
    	for (int i = cItems.size()-1; i >= 0; i--)
    	{
    		ContactListContactItem citem = getCItem(i); 
    	    if (citem.getUIN() == uinInt) return citem;
    	}
    	return null;
    }
	
	static public ContactListContactItem[] getGroupItems(int groupId)
	{
		Vector vect = new Vector(); 
		for (int i = 0; i < cItems.size(); i++)
		{
			ContactListContactItem cItem = getCItem(i); 
			if (cItem.getIntValue(ContactListContactItem.CONTACTITEM_GROUP) == groupId) vect.addElement(cItem);
		}
		
		ContactListContactItem[] result = new ContactListContactItem[vect.size()];
		vect.copyInto(result);
		
		return result;
	}
    
    // Calculates online/total values for group
    static private void calcGroupData(TreeNode groupNode, ContactListGroupItem group)
    {
        if ((group == null) || (groupNode == null)) return;
        
        ContactListContactItem cItem;
        int onlineCount = 0;
        
        int count = groupNode.size();
        for (int i = 0; i < count; i++)
        {
        	if (!(groupNode.elementAt(i).getData() instanceof ContactListContactItem)) continue; // TODO: must be removed
            cItem = (ContactListContactItem)groupNode.elementAt(i).getData();
            if (cItem.getIntValue(ContactListContactItem.CONTACTITEM_STATUS) != STATUS_OFFLINE) onlineCount++;
        }
        group.setCounters(onlineCount, count);
    }
    
    // Must be called after any changes in contacts
    public static void contactChanged
    (
    	ContactListContactItem item, 
		boolean setCurrent,
		boolean needSorting
    )
    {
    	if (!treeBuilt) return;
    	
    	boolean contactExistInTree = false,
		        contactExistsInList,
				wasDeleted = false,
				haveToAdd = false,
				haveToDelete = false;
    	TreeNode cItemNode = null;
    	int i, count, groupId;
    	
    	int status = item.getIntValue(ContactListContactItem.CONTACTITEM_STATUS);
    	
    	String uin = item.getStringValue(ContactListContactItem.CONTACTITEM_UIN);
    	
    	// which group id ?
    	groupId = item.getIntValue(ContactListContactItem.CONTACTITEM_GROUP);
    	
	    // which group ?
	    ContactListGroupItem group = getGroupById(groupId);
	    
		boolean only_online = Options.getBoolean(Options.OPTION_CL_HIDE_OFFLINE);
    	
    	// Whitch group node?
    	TreeNode groupNode = (TreeNode)gNodes.get( new Integer(groupId) );
    	if (groupNode == null) groupNode = tree.getRoot();
    	
    	// Does contact exists in tree?
  		count = groupNode.size();
   		for (i = 0; i < count; i++)
   		{
   			cItemNode = groupNode.elementAt(i);
   			Object data = cItemNode.getData();
   			if ( !(data instanceof ContactListContactItem) ) continue; 
   			if ( !((ContactListContactItem)data).getStringValue(ContactListContactItem.CONTACTITEM_UIN).equals(uin) ) continue;
   			contactExistInTree = true;
   			break;
   		}
    	
    	// Does contact exists in internal list?
    	contactExistsInList = (cItems.indexOf(item) != -1);
    	
    	// Lock tree repainting
    	tree.lock();
    	
    	haveToAdd = contactExistsInList && !contactExistInTree;
    	if (only_online && !contactExistInTree) 
    		haveToAdd |= ((status != STATUS_OFFLINE) | item.mustBeShownAnyWay()); 
    	
    	haveToDelete = !contactExistsInList && contactExistInTree;
    	if (only_online && contactExistInTree) 
    		haveToDelete |= ((status == STATUS_OFFLINE) && !item.mustBeShownAnyWay());
    	
    	// if have to add new contact
    	if (haveToAdd && !contactExistInTree)
    	{
    		cItemNode = tree.addNode(groupNode, item);
    	}
    	
    	// if have to delete contact
    	else if (haveToDelete)
    	{
    		tree.removeNode(cItemNode);
    		wasDeleted = true;
    	}
    	
    	// sort group
    	if (needSorting && !wasDeleted)
    	{
    		boolean isCurrent = (tree.getCurrentItem() == cItemNode),
			        inserted = false;
    		
    		tree.deleteChild( groupNode, tree.getIndexOfChild(groupNode, cItemNode) );
    		
    		int contCount = groupNode.size();
    		sortType = Options.getInt(Options.OPTION_CL_SORT_BY);
    		
    		// TODO: Make binary search instead of linear before child insertion!!!
    		for (int j = 0; j < contCount; j++)
    		{
    			TreeNode testNode = groupNode.elementAt(j);
    			if ( !(testNode.getData() instanceof ContactListContactItem) ) continue;
    			if (_this.compareNodes(cItemNode, testNode) < 0)
    			{
    				tree.insertChild(groupNode, cItemNode, j);
    				inserted = true;
    				break;
    			}
    		}
    		if (!inserted) tree.insertChild(groupNode, cItemNode, contCount);
    		if (isCurrent) tree.setCurrentItem(cItemNode);
    	}
    	
    	// if set current
    	if (setCurrent) tree.setCurrentItem(cItemNode);
    	
    	// unlock tree and repaint
    	tree.unlock();
    	
    	// change status for chat (if exists)
    	item.setStatusImage();
    }
    
    private static int lastUnknownStatus = STATUS_NONE;
    
	static public void updateCL(String uin, int status, byte[] internalIP, byte[] externalIP, int dcPort, int dcType, int icqProt, int authCookie, int signon, int online, int idle, int xStatus, int regdata)
	{
		ContactListContactItem cItem = getItembyUIN(uin);
        int trueStatus = Util.translateStatusReceived(status);
        
        if (cItem == null)
        {
        	lastUnknownStatus = trueStatus;
        	return;
        }
        
        long oldStatus = cItem.getIntValue(ContactListContactItem.CONTACTITEM_STATUS);
    	
    	boolean statusChanged  = (oldStatus != trueStatus);
        boolean wasOnline  = (oldStatus != STATUS_OFFLINE);
        boolean nowOnline  = (trueStatus != STATUS_OFFLINE);
		if (nowOnline) 
		{
			cItem.setIntValue (ContactListContactItem.CONTACTITEM_SIGNON,signon);
			cItem.setIntValue (ContactListContactItem.CONTACTITEM_REGDATA,regdata);
			cItem.setIntValue (ContactListContactItem.CONTACTITEM_ONLINE,online);
			cItem.setIntValue (ContactListContactItem.CONTACTITEM_IDLE,idle);
			if (dcType != -1) cItem.setIntValue (ContactListContactItem.CONTACTITEM_ICQ_PROT,    icqProt);
		}
		boolean xStatusChanged = (cItem.getIntValue(ContactListContactItem.CONTACTITEM_STATUS) != xStatus);
		cItem.setIntValue(ContactListContactItem.CONTACTITEM_XSTATUS, xStatus);
		cItem.setIntValue(ContactListContactItem.CONTACTITEM_STATUS, trueStatus);
		statusChanged(cItem, wasOnline, nowOnline, 0);
		//#sijapp cond.if (target="MIDP2" | target="MOTOROLA" | target="SIEMENS2") & modules_FILES="true"#
        if (dcType != -1)
        {
            cItem.setIPValue  (ContactListContactItem.CONTACTITEM_INTERNAL_IP, internalIP);
            cItem.setIPValue  (ContactListContactItem.CONTACTITEM_EXTERNAL_IP, externalIP); 
            cItem.setIntValue (ContactListContactItem.CONTACTITEM_DC_PORT,     (int)dcPort);
            cItem.setIntValue (ContactListContactItem.CONTACTITEM_DC_TYPE,     dcType);
            cItem.setIntValue(ContactListContactItem.CONTACTITEM_AUTH_COOKIE, authCookie);
        }
        //#sijapp cond.end#
		RunnableImpl.updateContactList(uin, statusChanged, wasOnline, nowOnline, trueStatus, xStatusChanged);
	}


	// Updates the client-side contact list (called when a contact changes status)
    // DO NOT CALL THIS DIRECTLY FROM OTHER THREAND THAN MAIN!
    // USE RunnableImpl.updateContactList INSTEAD!
    static public synchronized void update(String uin, boolean statusChanged, boolean wasOnline, boolean nowOnline, int trueStatus, boolean xStatusChanged)
    {
        ContactListContactItem cItem = getItembyUIN(uin);
        
		if ((wasOnline) && (!nowOnline))
        	{
        		//#sijapp cond.if target isnot "DEFAULT"#
        		cItem.BeginTyping(false);
        		//#sijapp cond.end#
				cItem.setIntValue(ContactListContactItem.CONTACTITEM_XSTATUS, -1);
				cItem.setStringValue(ContactListContactItem.CONTACTITEM_OFFLINETIME,Util.getDateString(false, true));
        		cItem.setIntValue(ContactListContactItem.CONTACTITEM_CAPABILITIES, 0);
        	}
		if (statusChanged) contactChanged(cItem, false, (wasOnline && !nowOnline) || (!wasOnline && nowOnline));
        if (treeBuilt && statusChanged) ContactListContactItem.statusChanged(uin, trueStatus);
		// Play sound notice if selected
        if ((!wasOnline) && nowOnline && statusChanged && !treeBuilt) needPlayOnlineNotif |= true;
    }


    static public void checkAndPlayOnlineSound(String uin, int trueStatus)
    {
        ContactListContactItem cItem = getItembyUIN(uin);
        if (cItem == null) return;
        if ((trueStatus != STATUS_OFFLINE) && 
            (cItem.getIntValue(ContactListContactItem.CONTACTITEM_STATUS) == STATUS_OFFLINE))
        		playSoundNotification(SOUND_TYPE_ONLINE);  
    }
    

    // Updates the client-side contact list (called when a contact changes status)
    static public void update(String uin, int status)
    {
        updateCL(uin, status, null, null, 0,0,-1,0,-1,-1,-1, -1, -1);
    }
    
    static public void statusChanged(ContactListContactItem cItem, boolean wasOnline, boolean nowOnline, int tolalChanges)
    {
    	boolean changed = false;
    	
    	// which group id ?
    	int groupId = cItem.getIntValue(ContactListContactItem.CONTACTITEM_GROUP);
    	
	    // which group ?
	    ContactListGroupItem group = getGroupById(groupId);
        
        // Calc online counters
        if (wasOnline && !nowOnline)
        {
        	onlineCounter--;
        	if (group != null) group.updateCounters(-1, 0);
        	changed = true;
        }
        
        if (!wasOnline && nowOnline)
        {
        	onlineCounter++;
        	if (group != null) group.updateCounters(1, 0);
        	changed = true;
			if (MIP.isFullyConnected)
			{
				lastFlashTask = new FlashStatus(cItem, 6);
				mip.MIP.getTimerRef().scheduleAtFixedRate(lastFlashTask, 0, 700);
			}
		}
        
        if (group != null)
        {
        	group.updateCounters(0, tolalChanges);
        	changed |= (tolalChanges != 0);
        }	
        
        if (changed) RunnableImpl.updateContactListCaption();
    }
    
    //Updates the title of the list
	static public void updateTitle(int traffic)
	{
		String text = onlineCounter + "/" + cItems.size();
		String sep = "-";
		if (traffic != 0)
		{
			text += /*sep + Util.getDateString(true, false)+ */sep + traffic + ResourceBundle.getString("kb");
		}
		/*else
		{
			text += sep + Util.getDateString(true, false);
		}*/
		text += sep + (Runtime.getRuntime().freeMemory()/1024) + " kb";
		tree.setCaption(text);
	}

	// Removes a contact list item
	static public /*synchronized*/void removeContactItem(
		ContactListContactItem cItem)
	{
		// Remove given contact item
		ContactList.cItems.removeElement(cItem);

		// Update visual list
		contactChanged(cItem, false, false);

		// Update online counters
		statusChanged(cItem, cItem.getIntValue(ContactListContactItem.CONTACTITEM_STATUS) != STATUS_OFFLINE, false, -1);

		// Save list
		safeSave();
	}

	// Adds a contact list item
	static public /*synchronized */void addContactItem(ContactListContactItem cItem)
	{
		if (!cItem.getBooleanValue(ContactListContactItem.CONTACTITEM_ADDED))
		{
			// does contact already exists or temporary ?
			ContactListContactItem oldItem = getItembyUIN(cItem.getStringValue(ContactListContactItem.CONTACTITEM_UIN));
			if (oldItem != null)
			{
				removeContactItem(oldItem);
				lastUnknownStatus = oldItem.getIntValue(ContactListContactItem.CONTACTITEM_STATUS);
			}

			// Add given contact item
			cItems.addElement(cItem);
			cItem.setBooleanValue(ContactListContactItem.CONTACTITEM_ADDED, true);

			// Check is chat availible 
    		cItem.setBooleanValue
    		(
    			ContactListContactItem.CONTACTITEM_HAS_CHAT,
    			ChatHistory.chatHistoryExists(cItem.getStringValue(ContactListContactItem.CONTACTITEM_UIN))
    		);
    		
    		// Set contact status (if already received)
    		if (lastUnknownStatus != STATUS_NONE)
    		{
    			cItem.setIntValue(ContactListContactItem.CONTACTITEM_STATUS, lastUnknownStatus);
    			lastUnknownStatus = STATUS_NONE;
    		}
    		
    		
			// Update visual list
			contactChanged(cItem, true, true);
    		
			// Update online counters
			statusChanged(cItem, false, cItem.getIntValue(ContactListContactItem.CONTACTITEM_STATUS) != STATUS_OFFLINE, 1);

			// Save list
			safeSave();
		}
	}

	// Adds new group
	static public /*synchronized */void addGroup(ContactListGroupItem gItem)
	{
		gItems.addElement(gItem);
		if (!Options.getBoolean(Options.OPTION_USER_GROUPS)) return;
		TreeNode groupNode = tree.addNode(null, gItem);
		gNodes.put(new Integer(gItem.getId()), groupNode);
		safeSave();
	}

	// removes existing group 
	static public /*synchronized*/ void removeGroup(ContactListGroupItem gItem)
	{
		for (int i = cItems.size()-1; i >= 0; i--) {
			ContactListContactItem cItem = getCItem(i);
			if (cItem.getIntValue(ContactListContactItem.CONTACTITEM_GROUP) == gItem.getId()) {
				if (cItem.getIntValue(ContactListContactItem.CONTACTITEM_STATUS) != STATUS_OFFLINE)
					onlineCounter--;
				cItems.removeElementAt(i);
			}
		}
		Integer groupId = new Integer(gItem.getId());
		if (Options.getBoolean(Options.OPTION_USER_GROUPS))
		{
			TreeNode node = (TreeNode) gNodes.get(groupId);
			tree.deleteChild(tree.getRoot(), tree.getIndexOfChild(tree.getRoot(), node));
			gNodes.remove(groupId);
		}
		gItems.removeElement(gItem);
		safeSave();
	}

    static public ContactListContactItem createTempContact(String uin)
    {
    	ContactListContactItem cItem = getItembyUIN(uin);
    	
    	if (cItem != null) return cItem;  
    	
    	try
    	{
			cItem = new ContactListContactItem(0, 0, uin, uin, false, true);
    	}
    	catch (Exception e)
    	{
    		// Message from non-icq contact
    		return null;
    	}
        cItems.addElement(cItem);
        cItem.setBooleanValue(ContactListContactItem.CONTACTITEM_IS_TEMP,true);
        return cItem;
    }
    
    // Adds the given message to the message queue of the contact item
    // identified by the given UIN
    static public /*synchronized*/ void addMessage(Message message, boolean haveToBeep)
    {
        // Notify user
		
        if ( !treeBuilt ) needPlayMessNotif |= true;
         
		ContactListContactItem cItem = getItembyUIN(message.getSndrUin());
        
        // Add message to contact
        
        if (cItem != null) ChatHistory.addMessage(cItem, message);
        
        
		// Create a temporary contact entry if no contact entry could be found
        // do we have a new temp contact
        else
        {
        	cItem = createTempContact( message.getSndrUin() );
            if (cItem != null) ChatHistory.addMessage(cItem, message);
        }
		cItem.setBooleanValue(ContactListContactItem.CONTACTITEM_HAS_CHAT,true);
        // Notify splash canvas
        SplashCanvas.messageAvailable();
        if (haveToBeep) playSoundNotification(SOUND_TYPE_MESSAGE);       
        // Update tree
        contactChanged(cItem, true, false);
		    }

    //#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#    
    
    // Reaction to player events. (Thanks to Alexander Barannik for idea!)
    public void playerUpdate(final Player player, final String event, Object eventData)
    {
    	if ( event.equals(PlayerListener.END_OF_MEDIA) )
    	{
        	player.close();
        	playerFree = true;
			
    	}
    }

    public static boolean testSoundFile(String source)
    {
    	playerFree = true;
    	Player player = createPlayer(source);
    	boolean ok = (player != null);
    	if (player != null) player.close();
		playerFree = true;
    	return ok;
    }

	/* Creates player for file 'source' */
    static private Player createPlayer(String source)
	{
    	if (!playerFree) return null;
    	
		String ext, mediaType;
		Player p;
		
		/* What is file extention? */
		int point = source.lastIndexOf('.');
		if (point != -1) ext = source.substring(point+1, source.length()).toLowerCase();
		else ext = "mp3";
		
		/* What is media type? */
		if (ext.equals("mp3")) mediaType = "audio/mpeg";
		else if (ext.equals("mid") || ext.equals("midi")) mediaType = "audio/midi";
		else if (ext.equals("amr"))  mediaType = "audio/amr";
		else mediaType = "audio/X-wav";
	
		try
		{
			Class cls = new Object().getClass();
			InputStream is = cls.getResourceAsStream(source);
			if (is == null) is = cls.getResourceAsStream("/"+source);
			if (is == null) return null;
				p = Manager.createPlayer(is, mediaType);
				playerFree = false;
				p.addPlayerListener(_this);
			}
		catch (Exception e)
		{
			return null;
		}
		return p;
	}
	
	//#sijapp cond.end#
	
	
	
	//#sijapp cond.if target is "MIDP2" | target is"SIEMENS1" | target is "MOTOROLA" | target is "SIEMENS2"#
	// sets volume for player
	static private void setVolume(Player p, int value)
	{
		try
		{
			p.realize();
			VolumeControl c = (VolumeControl) p.getControl("VolumeControl");
			if (c != null)
			{
				c.setLevel(value);
				p.prefetch();
			}
		}
		catch (Exception e)
		{
		}
	}
	
	//#sijapp cond.end#

	//#sijapp cond.if target isnot "DEFAULT"#
	static private void TypingHelper(String uin, boolean type)
	{
		if (type) playSoundNotification(ContactList.SOUND_TYPE_TYPING);
		if (ChatHistory.chatHistoryShown(uin))
		{
			ChatHistory.getChatHistoryAt(uin).BeginTyping(type);
		}
		else
			tree.repaint();
	}
	
	static public void BeginTyping(String uin, boolean type)
	{
		ContactListContactItem item = getItembyUIN(uin);
		if( item == null )
			if (!Options.getBoolean(Options.OPTION_ANTISPAM_ENABLE))
			{
				item = createTempContact(uin);
			}
		
		if( item == null )
		{
			System.out.println("Unable to create temp ContactItem!");
			return;
		}
			
	    // If the user does not have it add the typing capability
		if( !item.hasCapability(Icq.CAPF_TYPING) )
			item.addCapability(Icq.CAPF_TYPING);
		item.BeginTyping(type);
		TypingHelper(uin,type);
	}
	//#sijapp cond.end#
	
    // Play a sound notification
    static public void playSoundNotification(int notType)
    {
	
	
    		if (!treeBuilt) return;
    	
//#sijapp cond.if target is "SIEMENS1" | target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
        
        
    		int vibraKind = Options.getInt(Options.OPTION_VIBRATOR);
    		if(vibraKind == 2) vibraKind = SplashCanvas.locked()?1:0;
    		if((Options.getBoolean(Options.OPTION_VIBR_SILENT) == true) && 
    		   (Options.getBoolean(Options.OPTION_SILENT_MODE) == false)) vibraKind = 0;
    		if ((vibraKind > 0) && (notType == SOUND_TYPE_MESSAGE))
    		{

    			MIP.display.vibrate(500);
    		}
				//#sijapp cond.if target isnot "DEFAULT"# 
				if (Options.getBoolean(Options.OPTION_SILENT_MODE) == true) return; 
				//#sijapp cond.end# 

			
			int not_mode = 0;
        
    		switch (notType)
    		{
    		case SOUND_TYPE_MESSAGE:
    			not_mode = Options.getInt(Options.OPTION_MESS_NOTIF_MODE);
    			break;
			
    		case SOUND_TYPE_ONLINE:
    			not_mode = Options.getInt(Options.OPTION_ONLINE_NOTIF_MODE);
    			break;
			
//#sijapp cond.if target isnot "DEFAULT"#
    		case SOUND_TYPE_TYPING:
    			not_mode = Options.getInt(Options.OPTION_TYPING_MODE) - 1;
    			break;
//#sijapp cond.end#
    		}
            
    		switch (not_mode)
    		{
    		case 1:
    			try
    			{
    				switch(notType)
    				{
    				case SOUND_TYPE_MESSAGE:
    					Manager.playTone(ToneControl.C4, 500, Options.getInt(Options.OPTION_MESS_NOTIF_VOL));
    					break;
    				case SOUND_TYPE_ONLINE:
                	
//#sijapp cond.if target isnot "DEFAULT"#
    				case SOUND_TYPE_TYPING:
//#sijapp cond.end#
    					Manager.playTone(ToneControl.C4+7, 500, Options.getInt(Options.OPTION_ONLINE_NOTIF_VOL));
    				}

    			} catch (Exception e)
    			{
    				//Do nothing
    			}
    			break;
    			
    		case 2:
    			try
    			{
    				Player p;
                
    				if (notType == SOUND_TYPE_MESSAGE)
    				{
                	//Siemens 65-75 bugfix
//#sijapp cond.if target is "SIEMENS2"#
	        			Player p1 = createPlayer("silence.wav");
        				setVolume(p1,100);
        				p1.start();
        				p1.close();
        				playerFree = true;
//#sijapp cond.end#
	                	p = createPlayer( Options.getString(Options.OPTION_MESS_NOTIF_FILE) );
    	            	if (p == null) return;
        	            setVolume(p, Options.getInt(Options.OPTION_MESS_NOTIF_VOL));
            	    }
	                else if (notType == SOUND_TYPE_ONLINE)
                	{
	                	// Siemens 65-75 bugfix
//#sijapp cond.if target is "SIEMENS2"#
        				Player p1 = createPlayer("silence.wav");
        				setVolume(p1,100);
        				p1.start();
	        			p1.close();
    	    			playerFree = true;
//#sijapp cond.end#
	                	p = createPlayer( Options.getString(Options.OPTION_ONLINE_NOTIF_FILE) );
    	            	if (p == null) return;
        	            setVolume(p, Options.getInt(Options.OPTION_ONLINE_NOTIF_VOL)); 
            	    }
//#sijapp cond.if target isnot "DEFAULT"#
	                else
    	            {
        	        	// Siemens 65-75 bugfix
//#sijapp cond.if target is "SIEMENS2"#
        				Player p1 = createPlayer("silence.wav");
        				setVolume(p1,100);
        				p1.start();
        				p1.close();
        				playerFree = true;
//#sijapp cond.end#
                		p = createPlayer( Options.getString(Options.OPTION_TYPING_FILE) );
                		if (p == null) return;
                    	setVolume(p, Options.getInt(Options.OPTION_TYPING_VOL)); 
                	}
//#sijapp cond.end#
                
                	p.start();
            	}
            	catch (Exception me)
            	{
	                // Do nothing
            	}

            	break;

        	}
        
//#sijapp cond.end#
    	}

    
    //#sijapp cond.if target isnot "DEFAULT"# 
        static public boolean changeSoundMode(boolean activate) 
        { 
            boolean newValue = !Options.getBoolean(Options.OPTION_SILENT_MODE); 
            Options.setBoolean(Options.OPTION_SILENT_MODE, newValue); 
            Options.safe_save(); 
            Alert alert = new Alert(null, ResourceBundle.getString(newValue ? "#sound_is_off" : "#sound_is_on"), null, null); 
            alert.setTimeout(2000); 
             if (activate) tree.activate(MIP.display, alert);
                else  MIP.display.setCurrent(alert, MIP.display.getCurrent());
            return newValue; 
        } 
     
    //#sijapp cond.end# 

	
	
    static ContactListContactItem lastChatItem = null;
    
    public void VTGetItemDrawData(TreeNode src, ListItem dst)
	{
		ContactListItem item = (ContactListItem)src.getData();
		dst.text       = item.getText();
		dst.imageIndex = item.getImageIndex();
		dst.color      = item.getTextColor();
		dst.fontStyle  = item.getFontStyle(); 
		dst.xIndex 	   = item.getXIndex();	
		dst.cliIcon	   = item.getCliIcon();
		dst.usedIL = 1;
		dst.PrivateImg = null;
		if(item instanceof ContactListContactItem)
		{
		ContactListContactItem cItem = (ContactListContactItem)item;
		dst.PrivateImg = cItem.getPrivateImage();
		}
		
		
	}
	
	public static void makeClick()
	{
		_this.VTnodeClicked(tree.getCurrentItem());
	}
	
	public void VTnodeClicked(TreeNode node)
	{
		if (node == null) return;
		ContactListItem item = (ContactListItem)node.getData();
		if (item instanceof ContactListContactItem)
		{
			// Activate the contact item menu
			
			lastChatItem = (ContactListContactItem)item; 
			lastChatItem.activate();
		}
		else if (item instanceof ContactListGroupItem)
		{
			tree.setExpandFlag(node, !node.getExpanded());
		}
	}
	
	public void onCursorMove(VirtualList sender) {}
	public void onItemSelected(VirtualList sender) {}
	public void onKeyPress(VirtualList sender, int keyCode,int type)
	{
		TreeNode node = tree.getCurrentItem();
		ContactListContactItem item = 
			((node != null) && (node.getData() instanceof ContactListContactItem))
				?
			(ContactListContactItem) node.getData()
				:
			null;
		mipUI.execHotKey(item, keyCode, type);
	}

	// shows next or previos chat 
	static public String showNextPrevChat(boolean next, boolean activate)
	{
		int index = cItems.indexOf(lastChatItem);
		if (index == -1) return null;
		int di = next ? 1 : -1;
		int maxSize = cItems.size();
		
		for (int i = index+di;; i += di)
		{
			if (i < 0) i = maxSize-1;
			if (i >= maxSize) i = 0;
			if (i == index) break;
			
			ContactListContactItem cItem = getCItem(i); 
			if ( cItem.getBooleanValue(ContactListContactItem.CONTACTITEM_HAS_CHAT) )
			{
				if (activate) 
				{
					lastChatItem = cItem;
					cItem.activate();
					MIP.uin = lastChatItem.getStringValue(ContactListContactItem.CONTACTITEM_UIN);
					MIP.setDsp(MIP.DSP_CHAT);
				}
				return cItem.getStringValue(ContactListContactItem.CONTACTITEM_UIN);
			}
		}
		return null;
	}
	
	// Returns number of unread messages 
	static protected int getUnreadMessCount()
	{
		int count = cItems.size();
		int result = 0;
		for (int i = 0; i < count; i++) result += getCItem(i).getUnreadMessCount();
		return result;
	}
	
	static public ContactListContactItem[] getItems(ContactListGroupItem group)
	{
		Vector data = new Vector();
		int gid = group.getId();
		int size = getSize();
		for (int i = 0; i < size; i++)
		{
			ContactListContactItem item = getCItem(i);
			if (item.getIntValue(ContactListContactItem.CONTACTITEM_GROUP) == gid)
				data.addElement(item);
		}
		ContactListContactItem[] result = new ContactListContactItem[data.size()];
		data.copyInto(result);
		return result;
	}
	
    // Command listener
    public void commandAction(Command c, Displayable d)
    {
        // Activate main menu
      if (c == okCommand) ContactList.activate();
        // Contact item has been selected
      else if (c == mipUI.cmdSelect)
		{
			TreeNode node = tree.getCurrentItem();
			if (node == null) return;
			ContactListItem item = (ContactListItem) node.getData();
			
			if (item instanceof ContactListContactItem)
			{
				lastChatItem = (ContactListContactItem) item;
				lastChatItem.activate();
			}
			
			if (item instanceof ContactListGroupItem)
			{
				boolean newExpanded = !node.getExpanded();
				if (!newExpanded)
				{
					ContactListGroupItem gItem = (ContactListGroupItem)item;
					ContactListContactItem[] cItems = getItems(gItem);
					int unreadCounter = 0;
					for (int i = 0; i < cItems.length; i++) unreadCounter += cItems[i].getUnreadMessCount();
					gItem.setMessCount(unreadCounter);
				}
				tree.setExpandFlag(node, newExpanded);
			}
		}

    }

}

class FlashStatus extends TimerTask
{
	private ContactListContactItem displ;
	private int counter;
	
	public FlashStatus(ContactListContactItem displ, int counter)
	{
		this.displ   = displ;
		this.counter = counter;
	}
	
	public void run()
	{
		if (counter != 0)
		{
				ContactList.tree.lock();
				if ((counter & 1) == 0) 
				{
					displ.isFlashing = false;
				}
				else
				{
					displ.isFlashing = true;
				}
				ContactList.tree.unlock();
				counter--;
		}
		else
		{
			displ.isFlashing = false;
			ContactList.repaintTree();
			cancel();
		}
	}
	
	public void restoreCaption()
	{
		displ.isFlashing= false;
	}

}

