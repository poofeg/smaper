package mip.comm;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.Vector;
import java.io.ByteArrayOutputStream;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.ContentConnection;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.io.HttpsConnection;
import javax.microedition.io.SocketConnection;

import mip.ContactListContactItem;
import mip.MIP;
import mip.mipException;
import mip.Options;
import mip.SplashCanvas;
import mip.util.ResourceBundle;
import mip.ContactList;
import mip.RunnableImpl;
import mip.TimerTasks;

//#sijapp cond.if modules_TRAFFIC is "true" #
import mip.Traffic;
//#sijapp cond.end#

public class Icq implements Runnable
{
	private static Icq _this;
	public static final byte[] CAP_AIM_SERVERRELAY = Util.explodeToBytes("09,46,13,49,4C,7F,11,D1,82,22,44,45,53,54,00,00", ',', 16);
	public static final byte[] CAP_UTF8            = Util.explodeToBytes("09,46,13,4E,4C,7F,11,D1,82,22,44,45,53,54,00,00", ',', 16);
	public static final byte[] CAP_AIM_ISICQ       = Util.explodeToBytes("09,46,13,44,4C,7F,11,D1,82,22,44,45,53,54,00,00", ',', 16);
	public static final byte[] CAP_UNKNOWN         = Util.explodeToBytes("09,46,00,00,4C,7F,11,D1,82,22,44,45,53,54,00,00", ',', 16);
	public static final byte[] CAP_MTN             = Util.explodeToBytes("56,3f,c8,09,0b,6f,41,bd,9f,79,42,26,09,df,a2,f3", ',', 16);
	public static final byte[] CAP_UTF8_GUID       = Util.explodeToBytes("7b,30,39,34,36,31,33,34,45,2D,34,43,37,46,2D,31,31,44,31,2D,38,32,32,32,2D,34,34,34,35,35,33,35,34,30,30,30,30,7D", ',', 16);
	public static final byte[] CAP_MIRANDAIM 		= { (byte) 0x4D, (byte) 0x69, (byte) 0x72, (byte) 0x61, (byte) 0x6E, (byte) 0x64, (byte) 0x61, (byte) 0x4D, (byte) 0x00, (byte) 0x07, (byte) 0x00, (byte) 0x0F, (byte) 0x00, (byte) 0x03, (byte) 0x08, (byte) 0x68};
	public static final byte[] CAP_TRILLIAN  		= { (byte) 0x97, (byte) 0xb1, (byte) 0x27, (byte) 0x51, (byte) 0x24, (byte) 0x3c, (byte) 0x43, (byte) 0x34, (byte) 0xad, (byte) 0x22, (byte) 0xd6, (byte) 0xab, (byte) 0xf7, (byte) 0x3f, (byte) 0x14, (byte) 0x09};
	public static final byte[] CAP_TRILCRYPT 		= { (byte) 0xf2, (byte) 0xe7, (byte) 0xc7, (byte) 0xf4, (byte) 0xfe, (byte) 0xad, (byte) 0x4d, (byte) 0xfb, (byte) 0xb2, (byte) 0x35, (byte) 0x36, (byte) 0x79, (byte) 0x8b, (byte) 0xdf, (byte) 0x00, (byte) 0x00};
	public static final byte[] CAP_SIM       		= {'S', 'I', 'M', ' ', 'c', 'l', 'i', 'e', 'n', 't', ' ', ' ', (byte) 0, (byte) 9, (byte) 1, (byte) 0};
	public static final byte[] CAP_SIMOLD    		= { (byte) 0x97, (byte) 0xb1, (byte) 0x27, (byte) 0x51, (byte) 0x24, (byte) 0x3c, (byte) 0x43, (byte) 0x34, (byte) 0xad, (byte) 0x22, (byte) 0xd6, (byte) 0xab, (byte) 0xf7, (byte) 0x3f, (byte) 0x14, (byte) 0x00};
	public static final byte[] CAP_LICQ      		= {'L', 'i', 'c', 'q', ' ', 'c', 'l', 'i', 'e', 'n', 't', ' ', (byte) 1, (byte) 2, (byte) 7, (byte) 0};
	public static final byte[] CAP_KOPETE    		= {'K', 'o', 'p', 'e', 't', 'e', ' ', 'I', 'C', 'Q', ' ', ' ', (byte) 0, (byte) 12, (byte) 1, (byte) 0};
	public static final byte[] CAP_MICQ      		= {'m', 'I', 'C', 'Q', ' ', (byte) 0xA9, ' ', 'R', '.', 'K', '.', ' ', (byte) 0, (byte) 0, (byte) 0, (byte) 0};
	public static final byte[] CAP_ANDRQ     		= {'&', 'R', 'Q', 'i', 'n', 's', 'i', 'd', 'e', (byte) 3, (byte) 7, (byte) 9, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
	public static final byte[] CAP_QIP       		= { (byte) 0x56, (byte) 0x3F, (byte) 0xC8, (byte) 0x09, (byte) 0x0B, (byte) 0x6F, (byte) 0x41, 'Q', 'I', 'P', ' ', '2', '0', '0', '5', 'a'};
	public static final byte[] CAP_MACICQ    		= { (byte) 0xdd, (byte) 0x16, (byte) 0xf2, (byte) 0x02, (byte) 0x84, (byte) 0xe6, (byte) 0x11, (byte) 0xd4, (byte) 0x90, (byte) 0xdb, (byte) 0x00, (byte) 0x10, (byte) 0x4b, (byte) 0x9b, (byte) 0x4b, (byte) 0x7d};
	private static final byte[] CAP_RICHTEXT = Util.explodeToBytes("97,b1,27,51,24,3c,43,34,ad,22,d6,ab,f7,3f,14,92", ',', 16);
	private static final byte[] CAP_IS2001 = Util.explodeToBytes("2e,7a,64,75,fa,df,4d,c8,88,6f,ea,35,95,fd,b6,df", ',', 16);
	private static final byte[] CAP_IS2002 = Util.explodeToBytes("10,cf,40,d1,4c,7f,11,d1,82,22,44,45,53,54,00,00", ',', 16);
	private static final byte[] CAP_STR20012 = Util.explodeToBytes("a0,e9,3f,37,4f,e9,d3,11,bc,d2,00,04,ac,96,dd,96", ',', 16);
	private static final byte[] CAP_AIMICON = Util.explodeToBytes("09,46,13,46,4c,7f,11,d1,82,22,44,45,53,54,00,00", ',', 16);
	private static final byte[] CAP_AIMIMIMAGE = Util.explodeToBytes("09,46,13,45,4c,7f,11,d1,82,22,44,45,53,54,00,00", ',', 16);
	private static final byte[] CAP_AIMCHAT = Util.explodeToBytes("74,8F,24,20,62,87,11,D1,82,22,44,45,53,54,00,00", ',', 16);
	private static final byte[] CAP_UIM = Util.explodeToBytes("A7,E4,0A,96,B3,A0,47,9A,B8,45,C9,E4,67,C5,6B,1F", ',', 16);
	private static final byte[] CAP_RAMBLER = Util.explodeToBytes("7E,11,B7,78,A3,53,49,26,A8,02,44,73,52,08,C4,2A", ',', 16);
	private static final byte[] CAP_ABV = Util.explodeToBytes("00,E7,E0,DF,A9,D0,4F,e1,91,62,C8,90,9A,13,2A,1B", ',', 16);
	private static final byte[] CAP_NETVIGATOR = Util.explodeToBytes("4C,6B,90,A3,3D,2D,48,0E,89,D6,2E,4B,2C,10,D9,9F", ',', 16);
	private static final byte[] CAP_XTRAZ = Util.explodeToBytes("1A,09,3C,6C,D7,FD,4E,C5,9D,51,A6,47,4E,34,F5,A0", ',', 16);
	private static final byte[] CAP_AIMFILE = Util.explodeToBytes("09,46,13,43,4C,7F,11,D1,82,22,44,45,53,54,00,00", ',', 16);
	private static final byte[] CAP_DIRECT = Util.explodeToBytes("09,46,13,44,4C,7F,11,D1,82,22,44,45,53,54,00,00", ',', 16);	
	public static final byte[] CAP_JIMM	  		    = {'J', 'i','m','m',' ', '0', '.', '6', '.', '0'};
	private static final byte[] CAP_AVATAR = Util.explodeToBytes("09,46,13,4C,4C,7F,11,D1,82,22,44,45,53,54,00,00", ',', 16);
	private static final byte[] CAP_TYPING = Util.explodeToBytes("56,3f,c8,09,0b,6f,41,bd,9f,79,42,26,09,df,a2,f3", ',', 16);
	private static final byte[] CAP_MCHAT	  		= { 'm','C','h','a','t',' ','i','c','q'};
	private static final byte[] CAP_MIP				= { (byte) 0x4d, (byte) 0x49, (byte) 0x50, (byte) 0x20};
	public static final byte[] CAP_RANDQ     = {'R', '&', 'Q', 'i', 'n', 's', 'i', 'd', 'e', 3, 7, 0, 1, 0, 0, 0};
	public static final byte[] CAP_INFIUM = {(byte) 0x7C, (byte) 0x73, (byte) 0x75, (byte) 0x02, (byte) 0xC3, (byte) 0xBE, (byte) 0x4F, (byte) 0x3E, (byte) 0xA6, (byte) 0x9F, (byte) 0x01, (byte) 0x53, (byte) 0x13, (byte) 0x43, (byte) 0x1E, (byte) 0x1A};
	public static final byte[] CAP_MIRANDAIM2 = {(byte) 0x69, (byte) 0x63, (byte) 0x71, (byte) 0x70, (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x0a, (byte) 0x80, (byte) 0x03, (byte) 0x08, (byte) 0x69, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
	public static final byte[] CAP_SMAPER = {'S', 'm', 'a', 'p', 'e', 'r', ' ', 'v', '1', '.', '1', '1', 'j', 0, 0, 0};
	
	// Arrays for new capability blowup
	private static final byte[] CAP_OLD_HEAD = { (byte) 0x09, (byte) 0x46 };
	private static final byte[] CAP_OLD_TAIL = Util.explodeToBytes("4C,7F,11,D1,82,22,44,45,53,54,00,00", ',', 16);	
	// No capability
	public static final int CAPF_NO_INTERNAL = 0x00000000;
	// Client unterstands type-2 messages
	public static final int CAPF_AIM_SERVERRELAY_INTERNAL = 0x00000001;
	// Client unterstands UTF-8 messages
	public static final int CAPF_UTF8_INTERNAL = 0x00000002;
	// Client capabilities for detection
	public static final int CAPF_MIRANDAIM = 0x00000004;
 	public static final int CAPF_TRILLIAN =0x00000008;
	public static final int CAPF_TRILCRYPT = 0x00000010;
	public static final int CAPF_SIM = 0x00000020;
	public static final int CAPF_SIMOLD = 0x00000040;
	public static final int CAPF_LICQ = 0x00000080;
	public static final int CAPF_KOPETE = 0x00000100;
	public static final int CAPF_MICQ = 0x00000200;
	public static final int CAPF_ANDRQ = 0x00000400;
	public static final int CAPF_QIP = 0x000000800;
	public static final int CAPF_MIP = 0x00001000;
	public static final int CAPF_MACICQ = 0x00002000;
	public static final int CAPF_RICHTEXT = 0x00004000;
	public static final int CAPF_IS2001 = 0x00008000;
	public static final int CAPF_IS2002 = 0x00010000;
	public static final int CAPF_STR20012 = 0x00020000;
	public static final int CAPF_AIMICON = 0x00040000;
	public static final int CAPF_AIMCHAT = 0x00080000;
	public static final int CAPF_UIM = 0x00100000;
	public static final int CAPF_RAMBLER = 0x00200000;
	public static final int CAPF_ABV = 0x00400000;
	public static final int CAPF_NETVIGATOR = 0x00800000;
	public static final int CAPF_XTRAZ = 0x01000000;
	public static final int CAPF_AIMFILE = 0x02000000;
	public static final int CAPF_JIMM = 0x04000000;
	public static final int CAPF_AIMIMIMAGE = 0x08000000;
	public static final int CAPF_AVATAR = 0x10000000;
	public static final int CAPF_DIRECT = 0x20000000;
	public static final int CAPF_TYPING = 0x40000000;
	public static final int CAPF_MCHAT = 0x80000000;

	// Client IDs
	public static final byte CLI_NONE = 0;
	public static final byte CLI_QIP = 1;
	public static final byte CLI_MIRANDA = 2;
	public static final byte CLI_LICQ = 3;
	public static final byte CLI_TRILLIAN = 4;
	public static final byte CLI_SIM = 5;
	public static final byte CLI_KOPETE = 6;
	public static final byte CLI_MICQ = 7;
	public static final byte CLI_ANDRQ = 8;
	public static final byte CLI_MIP = 9;
	public static final byte CLI_MACICQ = 10;
	public static final byte CLI_AIM = 11;
	public static final byte CLI_UIM = 12;
	public static final byte CLI_WEBICQ = 13;
	public static final byte CLI_GAIM = 14;
	public static final byte CLI_ALICQ = 15;
	public static final byte CLI_STRICQ = 16;
	public static final byte CLI_YSM = 17;
	public static final byte CLI_VICQ = 18;
	public static final byte CLI_LIBICQ2000 = 19;
	public static final byte CLI_JIMM = 20;
	public static final byte CLI_SMARTICQ = 21;
	public static final byte CLI_ICQLITE4 = 22;
	public static final byte CLI_ICQLITE5 = 23;
	public static final byte CLI_ICQ98 = 24;
	public static final byte CLI_ICQ99 = 25;
	public static final byte CLI_ICQ2001B = 26;
	public static final byte CLI_ICQ2002A2003A = 27;
	public static final byte CLI_ICQ2000 = 28;
	public static final byte CLI_ICQ2003B = 29;
	public static final byte CLI_ICQLITE = 30;
	public static final byte CLI_GNOMEICQ = 31;	
	public static final byte CLI_AGILE = 32;
	public static final byte CLI_SPAM = 33;
	public static final byte CLI_CENTERICQ = 34;
	public static final byte CLI_LIBICQJABBER = 35;
	public static final byte CLI_ICQ2GO = 36;
	public static final byte CLI_ICQPPC = 37;
	public static final byte CLI_STICQ = 38;
	public static final byte CLI_MCHAT = 39;
	public static final byte CLI_INFIUM = 40;
	public static final byte CLI_RANDQ = 41;
	public static final byte CLI_SMAPER = 42;
	
	private static void initClientIndData()
	{
		Vector vInd = new Vector();
		Vector vImg = new Vector();
		Vector vNames = new Vector();
		//                    name                      index              image index
		initClientIndDataItem("Not detected",           CLI_NONE,          -1, vInd, vImg, vNames);
		initClientIndDataItem("QIP",                    CLI_QIP,           4,  vInd, vImg, vNames);
		initClientIndDataItem("Miranda",                CLI_MIRANDA,       1,  vInd, vImg, vNames);
		initClientIndDataItem("LIcq",                   CLI_LICQ,          3, vInd, vImg, vNames);
		initClientIndDataItem("Trillian",               CLI_TRILLIAN,      14,  vInd, vImg, vNames);
		initClientIndDataItem("SIM",                    CLI_SIM,           6,  vInd, vImg, vNames);
		initClientIndDataItem("Kopete",                 CLI_KOPETE,        2,  vInd, vImg, vNames);
		initClientIndDataItem("MICQ",                   CLI_MICQ,          -1, vInd, vImg, vNames);
		initClientIndDataItem("&RQ",                    CLI_ANDRQ,         9,  vInd, vImg, vNames);
		initClientIndDataItem("ICQ for MAC",            CLI_MACICQ,        8, vInd, vImg, vNames);
		initClientIndDataItem("AIM",                    CLI_AIM,           -1, vInd, vImg, vNames);
		initClientIndDataItem("UIM",                    CLI_UIM,           -1, vInd, vImg, vNames);
		initClientIndDataItem("WebICQ",                 CLI_WEBICQ,        -1, vInd, vImg, vNames);
		initClientIndDataItem("Gaim",                   CLI_GAIM,          -1, vInd, vImg, vNames);
		initClientIndDataItem("Alicq",                  CLI_ALICQ,         -1, vInd, vImg, vNames);
		initClientIndDataItem("StrICQ",                 CLI_STRICQ,        -1, vInd, vImg, vNames);
		initClientIndDataItem("YSM",                    CLI_YSM,           -1, vInd, vImg, vNames);
		initClientIndDataItem("vICQ",                   CLI_VICQ,          -1, vInd, vImg, vNames);
		initClientIndDataItem("Libicq2000",             CLI_LIBICQ2000,    11, vInd, vImg, vNames);
		initClientIndDataItem("Jimm",                   CLI_JIMM,          7,  vInd, vImg, vNames);
		initClientIndDataItem("SmartICQ",               CLI_SMARTICQ,      -1, vInd, vImg, vNames);
		initClientIndDataItem("ICQ Lite v4",            CLI_ICQLITE4,      19, vInd, vImg, vNames);
		initClientIndDataItem("ICQ Lite v5",            CLI_ICQLITE5,      19, vInd, vImg, vNames);
		initClientIndDataItem("ICQ 2002a/2003a",        CLI_ICQ2002A2003A, 18, vInd, vImg, vNames);
		initClientIndDataItem("ICQ 2000",               CLI_ICQ2000,       16, vInd, vImg, vNames);
		initClientIndDataItem("ICQ 2003b",              CLI_ICQ2003B,      18, vInd, vImg, vNames);
		initClientIndDataItem("ICQ Lite",               CLI_ICQLITE,       19, vInd, vImg, vNames);
		initClientIndDataItem("Gnome ICQ",              CLI_GNOMEICQ,      -1, vInd, vImg, vNames);
		initClientIndDataItem("Agile Messenger",        CLI_AGILE,         12, vInd, vImg, vNames);
		initClientIndDataItem("SPAM:)",                 CLI_SPAM,          -1, vInd, vImg, vNames);
		initClientIndDataItem("CenterICQ",              CLI_CENTERICQ,     -1, vInd, vImg, vNames);
		initClientIndDataItem("Libicq2000 from Jabber", CLI_LIBICQJABBER,  15, vInd, vImg, vNames);
		initClientIndDataItem("ICQ2GO!",                CLI_ICQ2GO,        -1, vInd, vImg, vNames);
		initClientIndDataItem("ICQ for Pocket PC",      CLI_ICQPPC,        -1, vInd, vImg, vNames);
		initClientIndDataItem("StIcq",                  CLI_STICQ,         13,  vInd, vImg, vNames);
		initClientIndDataItem("",                  CLI_MCHAT,         -1, vInd, vImg, vNames);
		initClientIndDataItem("QIP Infium",                  CLI_INFIUM,         5, vInd, vImg, vNames);
		initClientIndDataItem("R&Q",                  CLI_RANDQ,         10, vInd, vImg, vNames);
		initClientIndDataItem("MIP",                  CLI_MIP,         11, vInd, vImg, vNames);
		initClientIndDataItem("SmapeR",                  CLI_SMAPER,         0, vInd, vImg, vNames);
		
		clientNames = new String[vNames.size()];
		vNames.copyInto(clientNames);
		
		clientIndexes = new int[vInd.size()];
		for (int i = vInd.size()-1; i >= 0; i--) clientIndexes[i] = ((Integer)vInd.elementAt(i)).intValue();
		
		clientImageIndexes = new int[vImg.size()];
		for (int i = vImg.size()-1; i >= 0; i--) clientImageIndexes[i] = ((Integer)vImg.elementAt(i)).intValue();
	}
	
	private static void initClientIndDataItem(String name, int index, int imageIndex, Vector vIndexes, Vector vImg, Vector vNames)
	{
		vNames.addElement(name);
		vIndexes.addElement(new Integer(index));
		vImg.addElement(new Integer(imageIndex-1));
	}
	public static final byte[] MTN_PACKET_BEGIN =
	{
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
		(byte) 0x00, (byte) 0x01
	};
    // State constants
	
	public static boolean cancelC = false;

    // Current state
    static private boolean connected = false;
	
	private static int iServer;
	
	// Requested actions
    static private Vector reqAction = new Vector();

    // Thread
    static volatile Thread thread;
    
    // FLAP sequence number
    static int flapSEQ;
    
    static String lastStatusChangeTime;
    
    public Icq()
    {
    	_this = this;
		p = new updateCheck();
    	// Set starting point for seq numbers (not bigger then 0x8000)
        Random rand = new Random(System.currentTimeMillis());
        flapSEQ = rand.nextInt()%0x8000;
    }
	
	
	   public static int detectXStatus(byte[] capabilities) {
        int counter;
        for (int i = 0; i < capabilities.length; i += 16) {
            for (int j = 0; j < ContactList.XS_CAPS.length; j += 17) {
                counter = 0;
                for (int k = 0; k < 16; k++, counter++) {
                    if (capabilities[i + k] != ContactList.XS_CAPS[j + k + 1]) {
                        break;
                    }
                }
                if (counter == 16) {
                    return (ContactList.XS_CAPS[j] + 1);
                }
            }
        }
        return -1;
    }
	
    // Request an action
    static public void requestAction(Action act) throws mipException
    {
        // Set reference to this ICQ object for callbacks
        act.setIcq(_this);

        // Look whether action is executable at the moment
        if (!act.isExecutable()) { throw (new mipException(140, 0)); }

        // Queue requested action
        synchronized (reqAction)
        {
            reqAction.addElement(act);
        }

        // Connect?
        if ((act instanceof ConnectAction) || (act instanceof RegisterNewUinAction))
        {
            // Create new thread and start
            thread = new Thread(_this);
            thread.start();

        }

        // Notify main loop
        /*synchronized (wait)
        {
            wait.notify();
        }*/

    }

	static
	{
		  initClientIndData();
	}
	public static void removeLocalContact(String uin) 
	{
		byte[] buf = new byte[1 + uin.length()];
		Util.putByte(buf, 0, uin.length());
		System.arraycopy(uin.getBytes(), 0, buf, 1, uin.length());
		try {
			c.sendPacket(new SnacPacket(0x0003, 0x0005, 0, new byte[0], buf));
		} catch (mipException e) {
			mipException.handleException(e);
		}
	}

    // Adds a ContactListContactItem to the server saved contact list
    static public synchronized void addToContactList(ContactListContactItem cItem)
    {
        // Request contact item adding
        UpdateContactListAction act = new UpdateContactListAction(cItem, UpdateContactListAction.ACTION_ADD);
        
        try
        {
            requestAction(act);
        } catch (mipException e)
        {
            mipException.handleException(e);
            if (e.isCritical()) return;
        }

        // Start timer
        SplashCanvas.addTimerTask("wait", act, false);
        // System.out.println("start addContact");
    }
	public static String lastVersion;
    // Connects to the ICQ network
	
	//0 - auth
	//1 - url
	static String parseResult(int type, String result)
	{
		switch (type)
		{
			case 0:
				if (result.startsWith("OK"))
				{
					return result.substring(3);
				}
				else if (result.startsWith("ERROR"))
				{
					if (result.substring(6).equals("1001")) makeAlert("wrong login or password");
					else makeAlert("unknown error");
					return null;
				}
				return null;
			case 1:
				if (result.startsWith("OK"))
				{
					return null;
				}
				else if (result.startsWith("ERROR"))
				{
					if (result.substring(6).equals("1004")) makeAlert("wrong key");
					else makeAlert("wrong url");
					return null;
				}
				return null;
		}
		return null;
	}
	
	public static void makeAlert(String msg)
	{
		Alert alert = new Alert(ResourceBundle.getString("message"), msg, null, null);
		alert.setTimeout(Alert.FOREVER);
		
		MIP.display.setCurrent(alert, MIP.display.getCurrent());
	}
	
	
	public static void uploadLink(String url) throws Exception
	{
		String query = "https://www.smape.com/smaper_con.php?opcode=101&uname=" + Util.encode(Options.getString(Options.OPTION_SMAPEACC)) + "&password=" + Util.encode(Options.getString(Options.OPTION_SMAPEPASS)) + "&smaper_id=" + Options.getString(Options.OPTION_USERID);
		//System.out.println("query - " + query);
		String result = makeHttpsQuery(query);
		String key = parseResult(0, result);
		//System.out.println("url - " + url);
		//System.out.println("encoded url - " + Util.encode(url));
		//System.out.println("auth - " + result);
		if (key != null) 
		{
			//System.out.println("authkey " + key);
			String query2 = "https://www.smape.com/smaper_con.php?opcode=103&session_id=" + key + "&url=" + Util.encode(url);
			//System.out.println("query2 " + query2);
			String result2 = makeHttpsQuery(query2);
			parseResult(1, result2);
			query2 = "https://www.smape.com/smaper_con.php?opcode=102&session_id=" + key;
			makeHttpsQuery(query2);
		}
		
	}
	
	
	public static String makeHttpsQuery(String query) 
	{
		StringBuffer b = new StringBuffer();
		HttpsConnection c = null;
		InputStream is = null;
		try 
		{
			int len = 0;
			int ch = 0;
			c = (HttpsConnection)Connector.open(query); 
			c.setRequestMethod(HttpsConnection.GET);
			is = c.openInputStream();

			// length of content to be read.
			len = (int) c.getLength();

			if (len != -1) 
			{
				// Read exactly Content-Length bytes
				for(int i=0; i<len; i++) 
				{
					if((ch = is.read()) != -1) 
					{
						b.append((char) ch);
					}
				}
			} 
			else 
			{
				// Read until connection is closed.
				while((ch = is.read()) != -1) 
				{
					len = is.available();
					b.append((char) ch);
				}
			}
		} 
		catch (Exception e) {e.printStackTrace();}
		finally 
		{
			if (is != null) 
			{
				try 
				{
					is.close();
				} catch (Exception ce) { }
			}
			if (c != null) 
			{
				try 
				{
					c.close();
				} catch (Exception ce) { }
			}
			return b.toString();
		}
	}
	
	void getViaContentConnection(String url) throws IOException {
         ContentConnection c = null;
         DataInputStream dis = null;
         try {
             c = (ContentConnection)Connector.open(url);
             int len = (int)c.getLength();
             dis = c.openDataInputStream();
             if (len > 0) {
                 byte[] data = new byte[len];
                 dis.readFully(data);
				 Options.saveImage(data);
             }
         } 
		 catch (Exception e) {e.printStackTrace();}
		 finally {
			
			//System.out.println(imageStream.toString());
			//imageStream.close();
             if (dis != null)
                 dis.close();
             if (c != null)
                 c.close();
         }
     }
	
	 private class updateCheck extends Thread {
         updateCheck() {}
		HttpConnection httemp;
        InputStream istemp2;
		String version;
         public void run() {
		 try{
				boolean needUserID = (Options.getString(Options.OPTION_USERID).equals("-1"));
				StringBuffer buffer2 = new StringBuffer();
				byte[] uinRaw = Util.stringToByteArray(Options.getString(Options.OPTION_UIN));
				byte[] hash = Util.calculateMD5(uinRaw);
				for (int i = 0; i < hash.length; i++)
				{
					String hex = Integer.toHexString(((int)hash[i])&0xFF);
					buffer2.append(hex.length() == 1 ? "0"+hex : hex);
				}
				//System.out.println(buffer2.toString());
				String userSmape = Options.getString(Options.OPTION_SMAPEACC);
				if (userSmape.equals("")) userSmape = "-1";
				userSmape = Util.encode(userSmape);
				String query = needUserID ? "http://smape.com/smaper_checkinstall1.php?os=java&version=Smaper%20v###VERSION###&resolution=8&smapeuser=" + userSmape + "&id=" + buffer2.toString(): "http://smape.com/smaper_checkinstall1.php?os=java&version=Smaper%20v###VERSION###&resolution=8&smapeuser=" + userSmape + "&nocache=" + Options.getString(Options.OPTION_USERID) + "&id=" + buffer2.toString();
				//System.out.println(query);
				httemp = (HttpConnection) Connector.open(query);
                if (httemp.getResponseCode() != HttpConnection.HTTP_OK) throw new IOException();
                istemp2 = httemp.openInputStream();
                byte[] version_ = new byte[(int)httemp.getLength()];
                istemp2.read(version_,0,version_.length);
                version = new String(version_);
				//System.out.println(version);
				String[] result = Util.explode(version, '|');
				boolean needImage = false;
				if (!needUserID)
				{
					
					lastVersion = result[0];
					if (!Options.getString(Options.OPTION_SMAPEID).equals(result[2])) needImage = true;
					Options.setString(Options.OPTION_SMAPEID, result[2]);
					Options.setString(Options.OPTION_SMAPEPICT, result[1]);
				}
				else
				{	
					lastVersion = result[1];
					if (!Options.getString(Options.OPTION_SMAPEID).equals(result[3])) needImage = true;
					Options.setString(Options.OPTION_USERID, result[0]);
					Options.setString(Options.OPTION_SMAPEID, result[3]);
					Options.setString(Options.OPTION_SMAPEPICT, result[2]);
				}
				if (needImage) getViaContentConnection(Options.getString(Options.OPTION_SMAPEPICT));
			}
				catch (Exception e) {}
         }
     }
	 
	static updateCheck p;
	
static public synchronized void connect(String newPassword)
	{
		// Connect
		RegisterNewUinAction act = new RegisterNewUinAction(newPassword, Options
					.getString(Options.OPTION_SRV_HOST), Options
					.getString(Options.OPTION_SRV_PORT));
		try
		{
			requestAction(act);

		} catch (mipException e)
		{
			mipException.handleException(e);
		}

		RegisterNewUinAction.addTimerTask (act);
		lastStatusChangeTime = Util.getDateString(true, true);
	}
	
    static public synchronized void connect()
    {
		
		Icq.connecting = true;
		Icq.cancelC	   = false;
        
            // Make the shadow connection for Nokia 6230 of other devices if
            // needed
            ContentConnection ctemp = null;
            DataInputStream istemp = null;
            try
            {		
	
			p.start();
			//#sijapp cond.if target isnot "MOTOROLA"#
				if (Options.getBoolean(Options.OPTION_SHADOW_CON))
				{
                String url = "http://smape.com/";
                ctemp = (ContentConnection) Connector.open(url);

                istemp = ctemp.openDataInputStream();
			}
        //#sijapp cond.end#
				
            } catch (Exception e)
            {
                // Do nothing
            }
        
        // Connect
        if (iServer == 3) iServer = 0;
		ConnectAction act = new ConnectAction(Options.getString(Options.OPTION_UIN), Options.getString(Options.OPTION_PASSWORD), Options.getString(Options.OPTION_SRV_HOST), Options.getString(Options.OPTION_SRV_PORT), iServer);
        try
        {
            
			requestAction(act);
			iServer += 1;

        } catch (mipException e)
        {
        	if(!reconnect(e))
        	{
                mipException.handleException(e);
        	}
        }

		SplashCanvas.setStatusToDraw(mip.mipUI.getStatusImageIndex(Options.getLong(Options.OPTION_ONLINE_STATUS)));
		// Start timer
        SplashCanvas.addTimerTask("Initialization..", act, true);
        
        lastStatusChangeTime = Util.getDateString(true, true);
    }

    
    static public synchronized void removeAllActions()
	 	         {
	 	                 if (reqAction != null)
	 	                         reqAction.removeAllElements();
	 	         }
    
    
    
    /* Disconnects from the ICQ network */
    static public synchronized void disconnect()
    {
		MIP.isFullyConnected = false;
		/* Disconnect */
        if( c != null )
        	c.close();
		resetServerCon();
		
        //#sijapp cond.if modules_TRAFFIC is "true" #
        try
        {
            Traffic.save();
        } catch (Exception e)
        { /* Do nothing */
        }
        //#sijapp cond.end#
        
        /* Reset all contacts offine */
		
        RunnableImpl.resetContactsOffline();
    }

    // Dels a ContactListContactItem to the server saved contact list
    static public synchronized boolean delFromContactList(ContactListContactItem cItem)
    {
        // Check whether contact item is temporary
        if (cItem.getBooleanValue(ContactListContactItem.CONTACTITEM_IS_TEMP))
        {
            // Remove this temporary contact item
			removeLocalContact(cItem.getStringValue(ContactListContactItem.CONTACTITEM_UIN));
            ContactList.removeContactItem(cItem);

            // Activate contact list
            ContactList.activate();

        }
        else
        {
            // Request contact item removal
            UpdateContactListAction act2 = new UpdateContactListAction(cItem, UpdateContactListAction.ACTION_DEL);
            try
            {
                Icq.requestAction(act2);
            } catch (mipException e)
            {
                mipException.handleException(e);
                if (e.isCritical()) return false;
            }

            // Start timer
            SplashCanvas.addTimerTask("wait", act2, false);
        }
		return true;
    }

    //#sijapp cond.if target isnot "DEFAULT"#
    public void beginTyping(String uin, boolean isTyping) throws mipException
    {
		byte[] uinRaw = Util.stringToByteArray(uin);
		int tempBuffLen = Icq.MTN_PACKET_BEGIN.length + 1 + uinRaw.length + 2;
		int marker = 0;
		byte[] tempBuff = new byte[tempBuffLen];
		System.arraycopy(Icq.MTN_PACKET_BEGIN, 0, tempBuff, marker, Icq.MTN_PACKET_BEGIN.length);
		marker += Icq.MTN_PACKET_BEGIN.length;
		Util.putByte(tempBuff, marker, uinRaw.length);
		marker += 1;
		System.arraycopy(uinRaw, 0, tempBuff, marker, uinRaw.length);
		marker += uinRaw.length;
		Util.putWord(tempBuff, marker, ((isTyping) ? (0x0002) : (0x0000)));
		marker += 2;
		// Send packet
		SnacPacket snacPkt = new SnacPacket(0x0004, 0x0014, 0x00000000, new byte[0], tempBuff);
		this.c.sendPacket(snacPkt);
    }
    //#sijapp cond.end#
    
    // Checks whether the comm. subsystem is in STATE_NOT_CONNECTED
    static public synchronized boolean isNotConnected()
    {
        return !connected;
    }

    // Puts the comm. subsystem into STATE_NOT_CONNECTED
    static protected synchronized void setNotConnected()
    {
        connected = false;
	}

    // Checks whether the comm. subsystem is in STATE_CONNECTED
    static public synchronized boolean isConnected()
    {
        return connected;
    }

    // Puts the comm. subsystem into STATE_CONNECTED
    static protected synchronized void setConnected()
    {
    	Icq.reconnect_attempts = Options.getInt(Options.OPTION_RECONNECT_NUMBER);
        connected = true;
    }
    
    // Returns and updates sequence nr
    static public int getFlapSequence()
    {
    	flapSEQ = ++flapSEQ%0x8000;
    	return flapSEQ;
    }

    // Resets the comm. subsystem
    static public synchronized void resetServerCon()
    {    	
    	// Stop thread
        thread = null;
        
        // Wake up thread in order to complete
		//synchronized (Icq.wait) { Icq.wait.notify(); }
		//aspro suggestion?
		//may be can help fix deadlocks problem?
	    // Reset all variables
        connected = false;
        
        // Reset all timer tasks
        mip.MIP.cancelTimer();
        
        // Delete all actions
        if (actAction != null) 
        {
        	actAction.removeAllElements();
        }
        if (reqAction != null) 
        {
        	reqAction.removeAllElements();
        }
       
    }
    
    /** *********************************************************************** */
    /** *********************************************************************** */
    /** *********************************************************************** */
    
    // Wait object
    static private Object wait = new Object();

    // Connection to the ICQ server
    public static Connection c;

    // All currently active actions
    static private Vector actAction;

    // Action listener
    static private ActionListener actListener;
    
    // Keep alive timer task
    static private TimerTasks keepAliveTimerTask;

    public static int reconnect_attempts;
    
    // Main loop
    public void run()
    {
        // Get thread object
        Thread thread = Thread.currentThread();
        // Required variables
        Action newAction = null;

        //_ToLog("run - Preparing connection");
        // Instantiate connections
        if (Options.getInt(Options.OPTION_CONN_TYPE) == Options.CONN_TYPE_SOCKET)
        	c = new SOCKETConnection();
        else if (Options.getInt(Options.OPTION_CONN_TYPE) == Options.CONN_TYPE_HTTP)
        	c = new HTTPConnection();
        //#sijapp cond.if modules_PROXY is "true"#
        else if (Options.getInt(Options.OPTION_CONN_TYPE) == Options.CONN_TYPE_PROXY)
        	c = new SOCKSConnection();
        //#sijapp cond.end#
	
        // Instantiate active actions vector
        actAction = new Vector();

        // Instantiate action listener
        actListener = new ActionListener();
        
        //_ToLog("run - Starting keepalive");
        keepAliveTimerTask = new TimerTasks(TimerTasks.ICQ_KEEPALIVE);
        long keepAliveInterv = Integer.parseInt(Options.getString(Options.OPTION_CONN_ALIVE_INVTERV))*1000;
        mip.MIP.getTimerRef().schedule(keepAliveTimerTask, keepAliveInterv, keepAliveInterv);

        // Catch mipExceptions
        try
        {
        	//_ToLog("run - Entering main loop");
            // Abort only in error state
            while (Icq.thread == thread)
            {
                // Get next action
                synchronized (reqAction)
                {
                    if (reqAction.size() > 0 )
                    {
                        if ((actAction.size() == 1) && ((Action) actAction.elementAt(0)).isExclusive())
                        {
                            newAction = null;
                        }
                        else
                        {
                        	if( reqAction != null && reqAction.size() != 0 )
                        		newAction = (Action) reqAction.elementAt(0);
                            if (((actAction.size() > 0) && newAction.isExclusive()) || (!newAction.isExecutable()))
                            {
                                newAction = null;
                            }
                            else
                            {
                            	if( reqAction != null && reqAction.size() != 0 )
                            		reqAction.removeElementAt(0);
                            }
                        }
                    }
                    else
                    {
                        newAction = null;
                    }
                }

                // Wait if a new action does not exist
                if ((newAction == null) && ((c.available() == 0)))
                {
                    try
                    {
                        /*synchronized (wait)
                        {
                            wait.wait()
                        }*/
						thread.sleep(300);
                    } catch (InterruptedException e)
                    {
                        // Do nothing
                    }
                    
                }
                // Initialize action
                else
                    if (newAction != null)
                    {
                        try
                        {
                            newAction.init();
                            actAction.addElement(newAction);
                        } catch (mipException e)
                        {
                        	if(!reconnect(e))
                                mipException.handleException(e);
                            if (e.isCritical()) throw (e);
                        }
                    }
				while ((c.available() > 0))
                {
                    // Try to get packet
                    Packet packet = null;
                    try
                    {
                        if (c.available() > 0) packet = c.getPacket();
                    } catch (mipException e)
                    {
                    	if(!reconnect(e))
                            mipException.handleException(e);
                        if (e.isCritical()) throw (e);
                    }
                    // Forward received packet to all active actions and to the
                    // action listener
                    boolean consumed = false;
                    for (int i = 0; i < actAction.size(); i++)
                    {
                        try
                        {
                            if (((Action) actAction.elementAt(i)).forward(packet))
                            {
                                consumed = true;
                                break;
                            }
                        } catch (mipException e)
                        {
                        	if(!reconnect(e))
                                mipException.handleException(e);
                            if (e.isCritical()) throw (e);
                        }
                    }
                    
                    if (!consumed)
                    {
                        try
                        {
                            actListener.forward(packet);
							packet = null;
                        } catch (mipException e)
                        {
                        	if(!reconnect(e))
                                mipException.handleException(e);
                            if (e.isCritical()) throw (e);
                        }
                    }
                }

                // Remove completed actions
                for (int i = 0; i < actAction.size(); i++)
                {
                    if (((Action) actAction.elementAt(i)).isCompleted() || ((Action) actAction.elementAt(i)).isError())
                    {
                        actAction.removeElementAt(i--);
                    }
                }
			}
        }
        // Critical mipException
        catch (mipException e)
        {
			//ContactList.activate();
            // Do nothing, already handled
        }
		catch (Exception e)
		{
			ContactList.activate();
		}
        if( !Options.getBoolean(Options.OPTION_RECONNECT) )
        {
	        // Close connection
			
	        c.close();
	        resetServerCon();
	        
	        /* Reset all contacts offine */ 
	        RunnableImpl.resetContactsOffline();
        }
    }
    
    public static volatile boolean connecting = false;
	public synchronized static boolean reconnect(mipException e) 
	{
		int errCode = e.getErrCode();
    	if (reconnect_attempts-- > 0 &&
    			Options.getBoolean(Options.OPTION_RECONNECT) && 
				e.isCritical() && 
				connecting &&
				(errCode < 115 || errCode > 117 ) &&
				errCode != 127 && errCode != 110 && errCode != 111 && errCode != 112
    		)
    	{
		//System.out.println("Exception!! Reconnect.");
		//System.out.println(e.getMessage());
    		disconnect();
                removeAllActions();
                Thread.yield();
                ContactList.beforeConnect();
    		try
			{
    			Thread.sleep(200);
			}
    		catch(Exception ex){}
    		
    		connect();
    		return true;
    	}
        else {connecting=false;}
    	return false;
	}

	/**************************************************************************/
    /**************************************************************************/
    /**************************************************************************/
    
    public abstract class Connection implements Runnable
    {
        // Disconnect flags
        protected volatile boolean inputCloseFlag;

        // Receiver thread
        protected volatile Thread rcvThread;

        // Received packets
        protected Vector rcvdPackets;
        
        // Opens a connection to the specified host and starts the receiver
        // thread
        public synchronized void connect(String hostAndPort) throws mipException
        {

        }

        // Sets the reconnect flag and closes the connection
        public synchronized void close()
        {
        }

        // Returns the number of packets available
        public synchronized int available()
        {
            if (this.rcvdPackets == null)
            {
                return (0);
            }
            else
            {
                return (this.rcvdPackets.size());
            }
        }

        // Returns the next packet, or null if no packet is available
        public Packet getPacket() throws mipException
        {

            // Request lock on packet buffer and get next packet, if available
            byte[] packet;
            synchronized (this.rcvdPackets)
            {
                if (this.rcvdPackets.size() == 0) { return (null); }
                packet = (byte[]) this.rcvdPackets.elementAt(0);
                this.rcvdPackets.removeElementAt(0);
            }

            // Parse and return packet
            return (Packet.parse(packet));

        }
        
        // Sends the specified packet always type 5 (FLAP packet)
        public void sendPacket(Packet packet) throws mipException
        {
        }
        

        // Main loop
        public void run()
        {

       

        }

    }

    /**************************************************************************/
    /**************************************************************************/
    /**************************************************************************/

    public class HTTPConnection extends Connection implements Runnable
    {

		// Connection variables
		private HttpConnection hcm; // Connection for monitor URLs (receiving)
		private HttpConnection hcd; // Connection for data URLSs (sending)
		private InputStream ism;
		private OutputStream osd;

		// URL for the monitor thread
		private String monitorURL;
		
		// HTTP Connection sequence
		private int seq;

		// HTTP Connection session ID
		private String sid;

		// IP and port of HTTP Proxy Server to connect to
		private String proxy_host;
		private int proxy_port;

		// Counter for the connections to the http proxy server
		private int connSeq;

		public HTTPConnection()
		{
			seq = 0;
			connSeq = 0;
			monitorURL = "http://http.proxy.icq.com/hello";
		}

		// Opens a connection to the specified host and starts the receiver thread
		public synchronized void connect(String hostAndPort) throws mipException
		{
			try
			{
				connSeq++;
				// If this is the first connection initialize the connection with the proxy
				if (connSeq == 1)
				{
					this.inputCloseFlag = false;
					this.rcvThread = new Thread(this);
					this.rcvThread.start();
					// Wait the the finished init will notify us
					//this.wait();
				}
				
				// Extract host and port from combined String (we need port as int value)
				String icqserver_host = hostAndPort.substring(0, hostAndPort.indexOf(":"));
				int icqserver_port = Integer.parseInt(hostAndPort.substring(hostAndPort.indexOf(":") + 1));
				// System.out.println("Connect via "+proxy_host+":"+proxy_port+" to: "+icqserver_host+" "+icqserver_port);
				// Send anser packet with connect to real server (via proxy)
				byte[] packet = new byte[icqserver_host.length() + 4];
				Util.putWord(packet, 0, icqserver_host.length());
				System.arraycopy(Util.stringToByteArray(icqserver_host), 0, packet, 2, icqserver_host.length());
				Util.putWord(packet, 2 + icqserver_host.length(), icqserver_port);

				this.sendPacket(null, packet, 0x003, connSeq);

				// If this was not the first connection to the ICQ server close the previous
				if (connSeq != 1)
				{
					DisconnectPacket reply = new DisconnectPacket();
					this.sendPacket(reply, null, 0x0005, connSeq - 1);
					this.sendPacket(null, new byte[0], 0x0006, connSeq - 1);
				}

			} catch (IllegalArgumentException e)
			{
				throw (new mipException(127, 0));
			} catch (Exception e)
			{
				// Do nothing
			}
		}

		// Sets the reconnect flag and closes the connection
		public synchronized void close()
		{
			this.inputCloseFlag = true;

			try
			{
				this.ism.close();
			} catch (Exception e)
			{ /* Do nothing */
			} finally
			{
				this.ism = null;
			}

			try
			{
				this.osd.close();
			} catch (Exception e)
			{ /* Do nothing */
			} finally
			{
				this.osd = null;
			}

			try
			{
				this.hcm.close();
				this.hcd.close();
			} catch (Exception e)
			{ /* Do nothing */
			} finally
			{
				this.hcm = null;
				this.hcd = null;
			}

			Thread.yield();
		}

		/***************************************************************************** 
		 ***************************************************************************** 
		 * 
		 * Sends and gets packets wraped in http requeste from ICQ http proxy server.
		 *  Packets to send and receive look like this:
		 * 
		 *  WORD	Size	Size of the upcoming packet
		 *  WORD	Version	Version of the ICQ Proxy Protocol (always 0x0443)
		 *  WORD	Type	Type of the upcoming packet must be one of these:
		 *  				0x0002	Reply on server hello
		 *  				0x0003	Loginrequest to ICQ server
		 *  				0x0004	Reply to login
		 *  				0x0005  FLAP packet
		 *  				0x0006  Close connection
		 *  				0x0007	Close connection reply
		 *  DWORD	Unkn	0x00000000
		 *  WORD	Unkn	0x0000
		 *  WORD	ConnSq	Number of connection the packet is for
		 *  ...		Data	Data of the packet (Size - 14 bytes)
		 * 
		 ***************************************************************************** 
		 *****************************************************************************/

		// Sends the specific packet (with the possibility of setting the packet type
		public void sendPacket(Packet packet, byte[] rawData, int type, int connCount) throws mipException
		{
			// Set the connection parameters
			try
			{
				this.hcd = (HttpConnection) Connector.open("http://" + proxy_host + ":" + proxy_port + "/data?sid=" + sid + "&seq=" + seq, Connector.READ_WRITE);
				this.hcd.setRequestProperty("User-Agent", "unknown");
				this.hcd.setRequestProperty("x-wap-profile", "unknown");
				this.hcd.setRequestProperty("Cache-Control", "no-store no-cache");
				this.hcd.setRequestProperty("Pragma", "no-cache");
				this.hcd.setRequestMethod(HttpConnection.POST);
				this.osd = this.hcd.openOutputStream();
			} catch (IOException e)
			{
				this.close();
			}

			// Throw exception if output stream is not ready
			if (this.osd == null) { throw (new mipException(128, 0, true)); }

			// Request lock on output stream
			synchronized (this.osd)
			{

				// Send packet and count the bytes
				try
				{
					byte[] outpack;

					// Add http header (it has 14 bytes)
					if (rawData == null)
					{
						rawData = packet.toByteArray();
						outpack = new byte[14 + rawData.length];
					}

					outpack = new byte[14 + rawData.length];
					Util.putWord(outpack, 0, rawData.length + 12); // Length
					Util.putWord(outpack, 2, 0x0443); // Version
					Util.putWord(outpack, 4, type);
					Util.putDWord(outpack, 6, 0x00000000); // Unknown
					Util.putDWord(outpack, 10, connCount);
					// The "real" data
					System.arraycopy(rawData, 0, outpack, 14, rawData.length);
					// System.out.println("Sent: "+outpack.length+" b");
					this.osd.write(outpack);
					// this.osd.flush();

					// Send the data
					if (hcd.getResponseCode() != HttpConnection.HTTP_OK)
						this.close();
					else
						seq++;

					try
					{
						this.osd.close();
						this.hcd.close();
					} catch (Exception e)
					{
						// Do nothing
					} finally
					{
						this.osd = null;
						this.hcd = null;
					}

					//#sijapp cond.if modules_TRAFFIC is "true" #

					// 40 is the overhead for each packet (TCP/IP)
					// 190 is the ca. overhead for the HTTP header
					// 14 bytes is the overhead for ICQ HTTP data header
					// 170 bytes is the ca. overhead of the HTTP/1.1 200 OK
					Traffic.addTraffic(outpack.length + 40 + 190 + 14 + 170);
					if (ContactList.getVisibleContactListRef().isActive())
					{
						RunnableImpl.updateContactListCaption();
					}
					//#sijapp cond.end#
					// System.out.println(" ");
				} catch (IOException e)
				{
					this.close();
				}

			}

		}

		// Sends the specified packet always type 5 (FLAP packet)
		public void sendPacket(Packet packet) throws mipException
		{
			this.sendPacket(packet, null, 0x0005, connSeq);
		}

		// Main loop
		public void run()
		{

			// Required variables
			byte[] length = new byte[2];
			byte[] httpPacket;
			byte[]packet = new byte[0];
			int flapMarker = 0;

			int bRead, bReadSum;
			int bReadSumRequest = 0;

			// Reset packet buffer
			synchronized (this)
			{
				this.rcvdPackets = new Vector();
			}

			// Try
			try
			{
				// Check abort condition
				while (!this.inputCloseFlag)
				{
					// Set connection parameters
					this.hcm = (HttpConnection) Connector.open(monitorURL, Connector.READ_WRITE);
					this.hcm.setRequestProperty("User-Agent", "unknown");
					this.hcm.setRequestProperty("x-wap-profile", "unknown");
					this.hcm.setRequestProperty("Cache-Control", "no-store no-cache");
					this.hcm.setRequestProperty("Pragma", "no-cache");
					this.hcm.setRequestMethod(HttpConnection.GET);
					this.ism = this.hcm.openInputStream();
					if (hcm.getResponseCode() != HttpConnection.HTTP_OK) throw new IOException();
					// Read flap header
					bReadSumRequest = 0;

					do
					{
						bReadSum = 0;
						// Read HTTP packet length information
						do
						{
							bRead = ism.read(length, bReadSum, length.length - bReadSum);
							if (bRead == -1) break;
							bReadSum += bRead;
							bReadSumRequest += bRead;
						} while (bReadSum < length.length);
						if (bRead == -1) break;
						// Allocate memory for packet data
						httpPacket = new byte[Util.getWord(length, 0)];
						bReadSum = 0;

						// Read HTTP packet data
						do
						{
							bRead = ism.read(httpPacket, bReadSum, httpPacket.length - bReadSum);
							if (bRead == -1) break;
							bReadSum += bRead;
							bReadSumRequest += bRead;
						} while (bReadSum < httpPacket.length);
						if (bRead == -1) break;
						
						// Only process type 5 (flap) packets
						if (Util.getWord(httpPacket, 2) == 0x0005)
						{
							// Packet has 12 bytes header and could contain more than one FLAP
							int contBytes = 12;
							while (contBytes < httpPacket.length)
							{

								// Verify flap header only if we are sure there is a start
								if (flapMarker == 0)
								{
									if (Util.getByte(httpPacket, contBytes) != 0x2A) { throw (new mipException(124, 0)); }
									// Copy flap packet data from http packet
									packet = new byte[Util.getWord(httpPacket, contBytes + 4) + 6];
								}
								// Read packet data form httpPacket to packet
								// Packet contains the end of the flap packet
								if (httpPacket.length-contBytes >= (packet.length - flapMarker))
								{
									System.arraycopy(httpPacket, contBytes, packet, flapMarker, (packet.length - flapMarker));
									contBytes += (packet.length - flapMarker);
									flapMarker = packet.length;
								}
								// Packet does not contain the end of the flap packet
								else 
								{
									System.arraycopy(httpPacket, contBytes, packet, flapMarker, httpPacket.length - contBytes);
									flapMarker += (httpPacket.length - contBytes);
									contBytes += httpPacket.length - contBytes;
								}
								// If all the bytes from a flap packet have been read add that packet to the queue
								if (flapMarker == packet.length)
								{
									// Lock object and add rcvd packet to vector
									synchronized (this.rcvdPackets)
									{
										this.rcvdPackets.addElement(packet);
									}
									flapMarker = 0;
								}
							}

							// Notify main loop
							/*synchronized (Icq.wait)
							{
								Icq.wait.notify();
							}*/
						}
						else
							if (Util.getWord(httpPacket, 2) == 0x0007)
							{
								// Construct and handle exception if we get a close rep for the connection we are
								// currently using
								if (Util.getWord(httpPacket, 10) == connSeq) throw new mipException(221, 0);
							}
							else
								if (Util.getWord(httpPacket, 2) == 0x0002)
								{
									synchronized (this)
									{
										// Init answer from proxy set sid and proxy_host and proxy_port
										byte[] temp = new byte[16];
										System.arraycopy(httpPacket, 10, temp, 0, 16);
										sid = Util.byteArrayToHexString(temp);
										// Get IP of proxy
										byte[] ip = new byte[Util.getWord(httpPacket, 26)];
										System.arraycopy(httpPacket, 28, ip, 0, ip.length);
										this.proxy_host = Util.byteArrayToString(ip);

										// Get port for proxy
										this.proxy_port = Util.getWord(httpPacket, 28 + ip.length);

										// Set monitor URL to non init value
										monitorURL = "http://" + proxy_host + ":" + proxy_port + "/monitor?sid=" + sid;

										this.notify();
									}

								}
					} while (bReadSumRequest < hcm.getLength());
					
					//#sijapp cond.if modules_TRAFFIC is "true" #
					// This is not accurate for http connection
					// 42 is the overhead for each packet (2 byte packet length) (TCP IP)
					// 185 is the overhead for each monitor packet HTTP HEADER
					// 175 is the overhead for each HTTP/1.1 200 OK answer header
					// ICQ HTTP data header is counted in bReadSum
					Traffic.addTraffic(bReadSumRequest + 42 + 185 + 175);

					if ( ContactList.getVisibleContactListRef().isActive())
					{
						RunnableImpl.updateContactListCaption();
						Traffic.trafficScreen.update(false);
					}
					//#sijapp cond.end#
					
					try
					{
						this.ism.close();
						this.hcm.close();
					} catch (Exception e)
					{
						// Do nothing
					} finally
					{
						this.ism = null;
						this.hcm = null;
					}
				}
			}
			// Catch communication exception
			catch (NullPointerException e)
			{
				if (!this.inputCloseFlag)
				{
					// Construct and handle exception
					mipException f = new mipException(125, 3);
					mipException.handleException(f);
				}
				else
				{ /* Do nothing */
				}
			}
			// Catch mipException
			catch (mipException e)
			{

				// Handle exception
				mipException.handleException(e);

			}
			// Catch IO exception
			catch (IOException e)
			{
				if (!this.inputCloseFlag)
				{
					// Construct and handle exception
					mipException f = new mipException(125, 1);
					mipException.handleException(f);
				}
				else
				{ /* Do nothing */
				}
			}
			catch (Exception e){}

		}

	}

    /**************************************************************************/
    /**************************************************************************/
    /**************************************************************************/

    
    // SOCKETConnection
    public class SOCKETConnection extends Connection implements Runnable
    {
    	
        // Connection variables
    	private SocketConnection sc;
    	private InputStream is;
    	private OutputStream os;

        // FLAP sequence number counter
    	private int nextSequence;

        // ICQ sequence number counter
    	private int nextIcqSequence;
    	

        // Opens a connection to the specified host and starts the receiver thread
		public synchronized void connect(String hostAndPort) throws mipException
		{
			try
			{
				sc = (SocketConnection) Connector.open("socket://" + hostAndPort, Connector.READ_WRITE);
				is = sc.openInputStream();
				os = sc.openOutputStream();

				inputCloseFlag = false;
				rcvThread = new Thread(this);
				rcvThread.start();
				nextSequence = (new Random()).nextInt() % 0x0FFF;
				nextIcqSequence = 2;

			} catch (ConnectionNotFoundException e)
			{
				throw (new mipException(121, 0));
			} catch (IllegalArgumentException e)
			{
				throw (new mipException(122, 0));
			} catch (IOException e)
			{
				throw (new mipException(120, 0));
			}
		}        

        // Sets the reconnect flag and closes the connection
        public synchronized void close()
        {
			inputCloseFlag = true;
			try
			{
				is.close();
			} catch (Exception e)
			{ /* Do nothing */
			} finally
			{
				is = null;
			}

			try
			{
				os.close();
			} catch (Exception e)
			{ /* Do nothing */
			} finally
			{
				os = null;
			}

			try
			{
				sc.close();
			} catch (Exception e)
			{ /* Do nothing */
			} finally
			{
				sc = null;
			}
			
			
			Thread.yield();
		}

        // Sends the specified packet
        public void sendPacket(Packet packet) throws mipException
        {

            // Throw exception if output stream is not ready
            if (os == null) 
            {
            	mipException e = new mipException(123, 0);
            	if( !Icq.reconnect(e) )
            		throw e;
            }

            // Request lock on output stream
            synchronized (os)
            {

                // Set sequence numbers
                packet.setSequence(nextSequence++);
                if (packet instanceof ToIcqSrvPacket)
                {
                    ((ToIcqSrvPacket) packet).setIcqSequence(nextIcqSequence++);
                }

                // Send packet and count the bytes
                try
                {
                    byte[] outpack = packet.toByteArray();
                    os.write(outpack);
                    os.flush();
                    //#sijapp cond.if modules_TRAFFIC is "true" #
                    Traffic.addTraffic(outpack.length + 51); // 51 is the overhead for each packet
                    if (Traffic.trafficScreen.isActive() || ContactList.getVisibleContactListRef().isActive())
                    {
                    	RunnableImpl.updateContactListCaption();
                    }
                    //#sijapp cond.end#
                } catch (IOException e)
                {
                    close();
                    mipException ex = new mipException(120, 3);
                    if( !Icq.reconnect(ex) )
                    	throw ex;
                }
				catch (Exception e){}

            }

        }
        
        //#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
        //#sijapp cond.if modules_FILES is "true"#
        
        // Retun the port this connection is running on
        public int getLocalPort()
        {
            try
            {
                return (this.sc.getLocalPort());
            } catch (IOException e)
            {
                return (0);
            }
        }

        // Retun the ip this connection is running on
        public byte[] getLocalIP()
        {
            try
            {
                return (Util.ipToByteArray(this.sc.getLocalAddress()));
            } catch (IOException e)
            {
                return (new byte[4]);
            }
        }
        
        //#sijapp cond.end#
        //#sijapp cond.end#

        // Main loop
        public void run()
        {

            // Required variables
            byte[] flapHeader = new byte[6];
            byte[] flapData;
            byte[] rcvdPacket;
            int bRead, bReadSum;

            // Reset packet buffer
            synchronized (this)
            {
                rcvdPackets = new Vector();
            }

            // Try
            try
            {

                // Check abort condition
                while (!inputCloseFlag)
                {

                    // Read flap header
                    bReadSum = 0;
                    if (Options.getInt(Options.OPTION_CONN_PROP) == 1)
                    {
                        while (is.available() == 0)
                            Thread.sleep(300);
                        if (is == null)
                        	break;
                    }
                    do
                    {
                        bRead = is.read(flapHeader, bReadSum, flapHeader.length - bReadSum);
                        if (bRead == -1) break;
                        bReadSum += bRead;
                    } while (bReadSum < flapHeader.length);
                    if (bRead == -1) break;

                    // Verify flap header
                    if (Util.getByte(flapHeader, 0) != 0x2A) { throw (new mipException(124, 0)); }

                    // Allocate memory for flap data
                    flapData = new byte[Util.getWord(flapHeader, 4)];

                    // Read flap data
                    bReadSum = 0;
                    do
                    {
                        bRead = is.read(flapData, bReadSum, flapData.length - bReadSum);
                        if (bRead == -1) break;
                        bReadSum += bRead;
                    } while (bReadSum < flapData.length);
                    if (bRead == -1) break;

                    // Merge flap header and data and count the data
                    rcvdPacket = new byte[flapHeader.length + flapData.length];
                    System.arraycopy(flapHeader, 0, rcvdPacket, 0, flapHeader.length);
                    System.arraycopy(flapData, 0, rcvdPacket, flapHeader.length, flapData.length);
                    //#sijapp cond.if modules_TRAFFIC is "true" #
                    Traffic.addTraffic(bReadSum + 57);
                    // 46 is the overhead for each packet (6 byte flap header)
                    if (ContactList.getVisibleContactListRef().isActive())
                    {
                    	RunnableImpl.updateContactListCaption();
						Traffic.trafficScreen.update(false);
                    }
                    //#sijapp cond.end#

                    // Lock object and add rcvd packet to vector
                    synchronized (rcvdPackets)
                    {
						flapData = null;
                        rcvdPackets.addElement(rcvdPacket);
						//rcvdPacket = null;
					}

                }

            }
            // Catch communication exception
            catch (NullPointerException e)
            {}
            // Catch InterruptedException
            catch (InterruptedException e)
            { /* Do nothing */
            }
            // Catch mipException
            catch (mipException e)
            {
            	if( !Icq.reconnect(e) )
	                mipException.handleException(e);
            }
            // Catch IO exception
           catch (IOException e)
            {
            	// Construct and handle exception (only if input close flag has not been set)
                if (!inputCloseFlag)
                {
                	mipException f = new mipException(120, 1);
                	if( !Icq.reconnect(f) )
	                    mipException.handleException(f);
                }
                // Reset input close flag
            }
			catch (Exception e){}
        }

    }

    /**************************************************************************/
    /**************************************************************************/
    /**************************************************************************/

    //#sijapp cond.if modules_PROXY is "true"#
    
    // SOCKSConnection
    public class SOCKSConnection extends Connection implements Runnable
    {
    	
    	private final byte[] SOCKS4_CMD_CONNECT =
        { (byte) 0x04, (byte) 0x01, (byte) 0x14, (byte) 0x46, // Port 5190
          (byte) 0x40, (byte) 0x0C, (byte) 0xA1, (byte) 0xB9, 
          (byte) 0x00 // IP 64.12.161.185 (default login.icq.com)
        };

    	private final byte[] SOCKS5_HELLO =
        { (byte) 0x05, (byte) 0x02, (byte) 0x00, (byte) 0x02};

    	private final byte[] SOCKS5_CMD_CONNECT =
        { (byte) 0x05, (byte) 0x01, (byte) 0x00, (byte) 0x03};
 

        // Connection variables
        //#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
    	private SocketConnection sc;
        //#sijapp cond.else#
    	private StreamConnection sc;
        //#sijapp cond.end#
    	private InputStream is;
    	private OutputStream os;

    	private boolean is_socks4 = false;
    	private boolean is_socks5 = false;
    	private boolean is_connected = false;

        // FLAP sequence number counter
    	private int nextSequence;

        // ICQ sequence number counter
    	private int nextIcqSequence;
    	
        // Tries to resolve given host IP
    	private synchronized String ResolveIP(String host, String port)
        {
            //#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
            if (Util.isIP(host)) return host;
            SocketConnection c;
            
            try
            {
                c = (SocketConnection) Connector.open("socket://" + host + ":" + port, Connector.READ_WRITE);
                String ip = c.getAddress();

                try
                {
                    c.close();
                } catch (Exception e)
                { /* Do nothing */
                } finally
                {
                    c = null;
                }

                return ip;
            }
            catch (Exception e)
            {
                return "0.0.0.0";
            }
            
            //#sijapp cond.else#
            if (Util.isIP(host))
            { 
            	return host;
            }
            else
            {
            	return "0.0.0.0";
            }
            //#sijapp cond.end#
        }

        // Build socks4 CONNECT request
    	private byte[] socks4_connect_request(String ip, String port)
        {
            byte[] buf = new byte[9];

            System.arraycopy(SOCKS4_CMD_CONNECT, 0, buf, 0, 9);
            Util.putWord(buf, 2, Integer.parseInt(port));
            byte[] bip = Util.ipToByteArray(ip);
            System.arraycopy(bip, 0, buf, 4, 4);

            return buf;
        }

        // Build socks5 AUTHORIZE request
    	private byte[] socks5_authorize_request(String login, String pass)
        {
            byte[] buf = new byte[3 + login.length() + pass.length()];

            Util.putByte(buf, 0, 0x01);
            Util.putByte(buf, 1, login.length());
            Util.putByte(buf, login.length() + 2, pass.length());
            byte[] blogin = Util.stringToByteArray(login);
            byte[] bpass = Util.stringToByteArray(pass);
            System.arraycopy(blogin, 0, buf, 2, blogin.length);
            System.arraycopy(bpass, 0, buf, blogin.length + 3, bpass.length);

            return buf;
        }

        // Build socks5 CONNECT request
    	private byte[] socks5_connect_request(String host, String port)
        {
            byte[] buf = new byte[7 + host.length()];

            System.arraycopy(SOCKS5_CMD_CONNECT, 0, buf, 0, 4);
            Util.putByte(buf, 4, host.length());
            byte[] bhost = Util.stringToByteArray(host);
            System.arraycopy(bhost, 0, buf, 5, bhost.length);
            Util.putWord(buf, 5 + bhost.length, Integer.parseInt(port));
            return buf;
        }

        // Opens a connection to the specified host and starts the receiver
        // thread
    	public synchronized void connect(String hostAndPort) throws mipException
        {
            int mode = Options.getInt(Options.OPTION_PRX_TYPE);
            is_connected = false;
            is_socks4 = false;
            is_socks5 = false;
            String host = "";
            String port = "";

            if (mode != 0)
            {
                int sep = 0;
                for (int i = 0; i < hostAndPort.length(); i++)
                {
                    if (hostAndPort.charAt(i) == ':')
                    {
                        sep = i;
                        break;
                    }
                }
                // Get Host and Port
                host = hostAndPort.substring(0, sep);
                port = hostAndPort.substring(sep + 1);
            }
            try
            {
                switch (mode)
                {
                case 0:
                    connect_socks4(host, port);
                    break;
                case 1:
                    connect_socks5(host, port);
                    break;
                case 2:
                    // Try better first
                    try
                    {
                        connect_socks5(host, port);
                    } catch (Exception e)
                    {
                        // Do nothing
                    }
                    // If not succeeded, then try socks4
                    if (!is_connected)
                    {
                        stream_close();
                        try
                        {
                            // Wait the given time
                            Thread.sleep(2000);
                        } catch (InterruptedException e)
                        {
                            // Do nothing
                        }
                        connect_socks4(host, port);
                    }
                    break;
                }

                inputCloseFlag = false;
                rcvThread = new Thread(this);
                rcvThread.start();
                nextSequence = (new Random()).nextInt() % 0x0FFF;
                nextIcqSequence = 2;
            } catch (mipException e)
            {
                throw (e);
            }
        }
        
        // Attempts to connect through socks4
        private synchronized void connect_socks4(String host, String port) throws mipException
        {
            is_socks4 = false;
            String proxy_host = Options.getString(Options.OPTION_PRX_SERV);
            String proxy_port = Options.getString(Options.OPTION_PRX_PORT);
            int i = 0;
            byte[] buf;

            try
            {
                //#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
                sc = (SocketConnection) Connector.open("socket://" + proxy_host + ":" + proxy_port, Connector.READ_WRITE);
                //#sijapp cond.else#
                sc = (StreamConnection) Connector.open("socket://" + proxy_host + ":" + proxy_port, Connector.READ_WRITE);
                //#sijapp cond.end#
                is = sc.openInputStream();
                os = sc.openOutputStream();

                String ip = ResolveIP(host, port);

                os.write(socks4_connect_request(ip, port));
                os.flush();

                // Wait for responce
                while (is.available() == 0 && i < 50)
                {
                    try
                    {
                        // Wait the given time
                        i++;
                        Thread.sleep(100);
                    } catch (InterruptedException e)
                    {
                        // Do nothing
                    }
                }
                // Read packet
                // If only got proxy responce packet, parse it
                if (is.available() == 8)
                {
                    // Read reply
                    buf = new byte[is.available()];
                    is.read(buf);

                    int ver = Util.getByte(buf, 0);
                    int meth = Util.getByte(buf, 1);
                    // All we need
                    if (ver == 0x00 && meth == 0x5A)
                    {
                        is_connected = true;
                        is_socks4 = true;
                    }
                    else
                    {
                        is_connected = false;
                        throw (new mipException(118, 2));
                    }
                }
                // If we got responce packet bigger than mere proxy responce,
                // we might got destination server responce in tail of proxy
                // responce
                else
                    if (is.available() > 8)
                    {
                        is_connected = true;
                        is_socks4 = true;
                    }
                    else
                    {
                        throw (new mipException(118, 2));
                    }
            } catch (ConnectionNotFoundException e)
            {
                throw (new mipException(121, 0));
            } catch (IllegalArgumentException e)
            {
                throw (new mipException(122, 0));
            } catch (IOException e)
            {
                throw (new mipException(120, 0));
            }
        }

        // Attempts to connect through socks5
        private synchronized void connect_socks5(String host, String port) throws mipException
        {
            is_socks5 = false;
            String proxy_host = Options.getString(Options.OPTION_PRX_SERV);
            String proxy_port = Options.getString(Options.OPTION_PRX_PORT);
            String proxy_login = Options.getString(Options.OPTION_PRX_NAME);
            String proxy_pass = Options.getString(Options.OPTION_PRX_PASS);
            int i = 0;
            byte[] buf;

            try
            {
                //#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
                sc = (SocketConnection) Connector.open("socket://" + proxy_host + ":" + proxy_port, Connector.READ_WRITE);
                //#sijapp cond.else#
                sc = (StreamConnection) Connector.open("socket://" + proxy_host + ":" + proxy_port, Connector.READ_WRITE);
                //#sijapp cond.end#
                is = sc.openInputStream();
                os = sc.openOutputStream();

                os.write(SOCKS5_HELLO);
                os.flush();

                // Wait for responce
                while (is.available() == 0 && i < 50)
                {
                    try
                    {
                        // Wait the given time
                        i++;
                        Thread.sleep(100);
                    } catch (InterruptedException e)
                    {
                        // Do nothing
                    }
                }

                if (is.available() == 0) { throw (new mipException(118, 2)); }

                // Read reply
                buf = new byte[is.available()];
                is.read(buf);

                int ver = Util.getByte(buf, 0);
                int meth = Util.getByte(buf, 1);

                // Plain text authorisation
                if (ver == 0x05 && meth == 0x02)
                {
                    os.write(socks5_authorize_request(proxy_login, proxy_pass));
                    os.flush();

                    // Wait for responce
                    while (is.available() == 0 && i < 50)
                    {
                        try
                        {
                            // Wait the given time
                            i++;
                            Thread.sleep(100);
                        } catch (InterruptedException e)
                        {
                            // Do nothing
                        }
                    }

                    if (is.available() == 0) { throw (new mipException(118, 2)); }

                    // Read reply
                    buf = new byte[is.available()];
                    is.read(buf);

                    meth = Util.getByte(buf, 1);

                    if (meth == 0x00)
                    {
                        is_connected = true;
                        is_socks5 = true;
                    }
                    else
                    {
                        // Unknown error (bad login or pass)
                        throw (new mipException(118, 3));
                    }
                }
                // Proxy without authorisation
                else
                    if (ver == 0x05 && meth == 0x00)
                    {
                        is_connected = true;
                        is_socks5 = true;
                    }
                    // Something bad happened :'(
                    else
                    {
                        throw (new mipException(118, 2));
                    }
                // If we got correct responce, send CONNECT
                if (is_connected == true)
                {
                    os.write(socks5_connect_request(host, port));
                    os.flush();
                }
            } catch (ConnectionNotFoundException e)
            {
                throw (new mipException(121, 0));
            } catch (IllegalArgumentException e)
            {
                throw (new mipException(122, 0));
            } catch (IOException e)
            {
                throw (new mipException(120, 0));
            }
        }

        // Sets the reconnect flag and closes the connection
        public synchronized void close()
        {
            inputCloseFlag = true;

            stream_close();

            Thread.yield();
        }

        // Close input and output streams
        private synchronized void stream_close()
        {
            try
            {
                is.close();
            } catch (Exception e)
            { /* Do nothing */
            } finally
            {
                is = null;
            }

            try
            {
                os.close();
            } catch (Exception e)
            { /* Do nothing */
            } finally
            {
                os = null;
            }

            try
            {
                sc.close();
            } catch (Exception e)
            { /* Do nothing */
            } finally
            {
                sc = null;
            }
        }

        // Sends the specified packet
        public void sendPacket(Packet packet) throws mipException
        {

            // Throw exception if output stream is not ready
            if (os == null) { throw (new mipException(123, 0)); }

            // Request lock on output stream
            synchronized (os)
            {

                // Set sequence numbers
                packet.setSequence(nextSequence++);
                if (packet instanceof ToIcqSrvPacket)
                {
                    ((ToIcqSrvPacket) packet).setIcqSequence(nextIcqSequence++);
                }

                // Send packet and count the bytes
                try
                {
                    byte[] outpack = packet.toByteArray();
                    os.write(outpack);
                    os.flush();
                    //#sijapp cond.if modules_TRAFFIC is "true" #
                    Traffic.addTraffic(outpack.length + 51); // 51 is the overhead for each packet
                    if (Traffic.trafficScreen.isActive() || ContactList.getVisibleContactListRef().isActive())
                    {
                    	RunnableImpl.updateContactListCaption();
						Traffic.trafficScreen.update(false);
                    }
                    //#sijapp cond.end#
                } catch (IOException e)
                {
                    close();
                }

            }

        }
        
        //#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
        //#sijapp cond.if modules_FILES is "true"#
        
        // Retun the port this connection is running on
        public int getLocalPort()
        {
            try
            {
                return (this.sc.getLocalPort());
            } catch (IOException e)
            {
                return (0);
            }
        }

        // Retun the ip this connection is running on
        public byte[] getLocalIP()
        {
            try
            {
                return (Util.ipToByteArray(this.sc.getLocalAddress()));
            } catch (IOException e)
            {
                return (new byte[4]);
            }
        }
        
        //#sijapp cond.end#
        //#sijapp cond.end#

        // Main loop
        public void run()
        {

            // Required variables
            byte[] flapHeader = new byte[6];
            byte[] flapData;
            byte[] rcvdPacket;
            int bRead, bReadSum;

            // Reset packet buffer
            synchronized (this)
            {
                rcvdPackets = new Vector();
            }

            // Try
            try
            {

                // Check abort condition
                while (!inputCloseFlag)
                {

                    // Read flap header
                    bReadSum = 0;
                    if (Options.getInt(Options.OPTION_CONN_PROP) == 1)
                    {
                        while (is.available() == 0)
                            Thread.sleep(250);
                        if (is == null)
                        	break;
                    }
                    do
                    {
                        bRead = is.read(flapHeader, bReadSum, flapHeader.length - bReadSum);
                        if (bRead == -1) break;
                        bReadSum += bRead;
                    } while (bReadSum < flapHeader.length);
                    if (bRead == -1) break;
                    // Verify and strip out proxy responce
                    // Socks4 first
                    if (Util.getByte(flapHeader, 0) == 0x00 && is_socks4)
                    {
                        // Strip only on first packet
                        is_socks4 = false;
                        int rep = Util.getByte(flapHeader, 1);
                        if (rep != 0x5A)
                        {
                            // Something went wrong :(
                            throw (new mipException(118, 1));
                        }

                        is.skip(2);

                        bReadSum = 0;
                        do
                        {
                            bRead = is.read(flapHeader, bReadSum, flapHeader.length - bReadSum);
                            if (bRead == -1) break;
                            bReadSum += bRead;
                        } while (bReadSum < flapHeader.length);
                    }
                    // Check for socks5
                    else
                        if (Util.getByte(flapHeader, 0) == 0x05 && is_socks5)
                        {
                            // Strip only on first packet
                            is_socks5 = false;

                            int rep = Util.getByte(flapHeader, 1);
                            if (rep != 0x00)
                            {
                                // Something went wrong :(
                                throw (new mipException(118, 1));
                            }
                            // Check ATYP and skip BND.ADDR
                            int atyp = Util.getByte(flapHeader, 3);

                            if (atyp == 0x01)
                            {
                                is.skip(4);
                            }
                            else
                                if (atyp == 0x03)
                                {
                                    int size = Util.getByte(flapHeader, 4);
                                    is.skip(size + 1);
                                }
                                else
                                {
                                    // Don't know what was that, but skip like
                                    // if it was an ip
                                    is.skip(4);
                                }

                            bReadSum = 0;
                            do
                            {
                                bRead = is.read(flapHeader, bReadSum, flapHeader.length - bReadSum);
                                if (bRead == -1) break;
                                bReadSum += bRead;
                            } while (bReadSum < flapHeader.length);
                        }

                    // Verify flap header
                    if (Util.getByte(flapHeader, 0) != 0x2A) { throw (new mipException(124, 0)); }

                    // Allocate memory for flap data
                    flapData = new byte[Util.getWord(flapHeader, 4)];

                    // Read flap data
                    bReadSum = 0;
                    do
                    {
                        bRead = is.read(flapData, bReadSum, flapData.length - bReadSum);
                        if (bRead == -1) break;
                        bReadSum += bRead;
                    } while (bReadSum < flapData.length);
                    if (bRead == -1) break;

                    // Merge flap header and data and count the data
                    rcvdPacket = new byte[flapHeader.length + flapData.length];
                    System.arraycopy(flapHeader, 0, rcvdPacket, 0, flapHeader.length);
                    System.arraycopy(flapData, 0, rcvdPacket, flapHeader.length, flapData.length);
                    //#sijapp cond.if modules_TRAFFIC is "true" #
                    Traffic.addTraffic(bReadSum + 57);
                    // 46 is the overhead for each packet (6 byte flap header)
                    if (Traffic.trafficScreen.isActive() || ContactList.getVisibleContactListRef().isActive())
                    {
                    	RunnableImpl.updateContactListCaption();
						Traffic.trafficScreen.update(false);
                    }
                    //#sijapp cond.end#

                    // Lock object and add rcvd packet to vector
                    synchronized (rcvdPackets)
                    {
                        rcvdPackets.addElement(rcvdPacket);
                    }

                    // Notify main loop
                    /*synchronized (Icq.wait)
                    {
                        Icq.wait.notify();
                    }*/
                }

            }
            // Catch communication exception
            catch (NullPointerException e)
            {

                // Construct and handle exception (only if input close flag has not been set)
                if (!inputCloseFlag)
                {
                    mipException f = new mipException(120, 3);
                    mipException.handleException(f);
                }

                // Reset input close flag
                inputCloseFlag = false;

            }
            // Catch InterruptedException
            catch (InterruptedException e)
            { /* Do nothing */
            }
            // Catch mipException
            catch (mipException e)
            {

                // Handle exception
                mipException.handleException(e);

            }
            // Catch IO exception
            catch (IOException e)
            {
                // Construct and handle exception (only if input close flag has not been set)
                if (!inputCloseFlag)
                {
                    mipException f = new mipException(120, 1);
                    mipException.handleException(f);
                }

                // Reset input close flag
                inputCloseFlag = false;

            }
        }

    }

    //#sijapp cond.end #
    
    /**************************************************************************/
    /**************************************************************************/
    /**************************************************************************/

    public static int getCurrentStatus()
    {
    	return isConnected() ? (int)Options.getLong(Options.OPTION_ONLINE_STATUS) : ContactList.STATUS_OFFLINE;
    }
	private static int[] clientIndexes;
	private static int[] clientImageIndexes;
	private static String[] clientNames;
	
		public static byte[] mergeCapabilities(byte[] capabilities_old, byte[] capabilities_new)
	{
		if (capabilities_new == null)
			return capabilities_old;
		if (capabilities_old == null)
			return capabilities_new;
		
		// Extend new capabilities to match with old ones
		byte[] extended_new = new byte[capabilities_new.length*8];
		for (int i=0;i<capabilities_new.length;i+=2)
		{
			System.arraycopy(CAP_OLD_HEAD,0,extended_new,(i*8),CAP_OLD_HEAD.length);
			System.arraycopy(capabilities_new,i,extended_new,((i*8)+CAP_OLD_HEAD.length),2);
			System.arraycopy(CAP_OLD_TAIL,0,extended_new,((i*8)+CAP_OLD_HEAD.length+2),CAP_OLD_TAIL.length);
		}
		// Check for coexisting capabilities and merge
		boolean found = false;
		for (int i=0;i<capabilities_old.length;i+=16)
		{
			byte[] tmp_old = new byte[16];
			System.arraycopy(capabilities_old,i,tmp_old,0,16);
			for (int j=0;j<extended_new.length;j+=16)
			{
				//System.out.println(j + " " + i + " " + extended_new.length);
				byte[] tmp_new = new byte[16];
				System.arraycopy(extended_new,j,tmp_new,0,16);
				if (tmp_old == tmp_new)
				{
					found = true;
					break;
				}
			}
			if (!found)
			{
				//System.out.println("Merge capability");
				byte[] merged = new byte[extended_new.length+16];
				System.arraycopy(extended_new,0,merged,0,extended_new.length);
				System.arraycopy(tmp_old,0,merged,extended_new.length,tmp_old.length);
				extended_new = merged;
				found = false;
			}
		}
	return extended_new;
	}
	
	public static String getClientString(int cli)
	{
		for (int i = clientIndexes.length-1; i >= 0; i--)
			if (clientIndexes[i] == cli) return (clientNames[i]);
		return null;
	}
	
	public static int getClientImageID(int cli)
	{
		for (int i = clientIndexes.length-1; i >= 0; i--) 
			if (clientIndexes[i] == cli) return clientImageIndexes[i]; 
		return -1; 
	}
	
	static public void setCLI_USERINFO(int cliID) throws mipException
	{
		ByteArrayOutputStream capsStream = new ByteArrayOutputStream();
		byte[] packet = null;
		boolean dontWriteVer = false;
		
			//updating client version
	 byte[] CLI_NEW = { (byte) 0x4d, (byte) 0x49, (byte) 0x50, (byte) 0x20, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, };
	  try{
	 switch(cliID)
	 {
		case Options.CLI_ID_QIP:
	 	System.arraycopy(CAP_QIP, 0, CLI_NEW, 0, 16);
		setCliStatus(false, true);
		break;
		
		case Options.CLI_ID_SMAPER:
			System.arraycopy(CAP_SMAPER, 0, CLI_NEW, 0, 16);
			break;
	  
		case Options.CLI_ID_JIMM:
		
		System.arraycopy(CAP_JIMM, 0, CLI_NEW, 0, 10);
		break;
		
		case Options.CLI_ID_INFIUM:
	 	System.arraycopy(CAP_INFIUM, 0, CLI_NEW, 0, 16);
		setCliStatus(false, true);
		break;
		
		case Options.CLI_ID_RQ2:
	 	System.arraycopy(CAP_RANDQ, 0, CLI_NEW, 0, 16);
		dontWriteVer = true;
		setCliStatus(false, true);
		break;
		
		case Options.CLI_ID_KOPETE:
		System.arraycopy(CAP_KOPETE, 0, CLI_NEW, 0, 16);
		break;
		
		case Options.CLI_ID_RQ:
		System.arraycopy(CAP_ANDRQ, 0, CLI_NEW, 0, 16);
		break;
		
		case Options.CLI_ID_MIRANDA:
		System.arraycopy(CAP_MIRANDAIM2, 0, CLI_NEW, 0, 16);
		break;		
		
		case Options.CLI_ID_SIM:
		System.arraycopy(CAP_SIM, 0, CLI_NEW, 0, 16);
		break;
		
		case Options.CLI_ID_MACICQ:
	 
		System.arraycopy(CAP_MACICQ, 0, CLI_NEW, 0, 16);
		break;
	  
		case Options.CLI_ID_LICQ:
		
		System.arraycopy(CAP_LICQ, 0, CLI_NEW, 0, 16);
		break;
	 }
			
			capsStream.write(new byte[] {(byte)0x00, (byte)0x05, (byte)0x00, (byte)0x00});
			capsStream.write(CAP_AIM_SERVERRELAY);
			capsStream.write(CAP_AIM_ISICQ);
			capsStream.write(CAP_UNKNOWN);
			if (Options.getBoolean(Options.OPTION_UTF8IN)) capsStream.write(CAP_UTF8);
			if (!dontWriteVer) capsStream.write(CLI_NEW);
			int xStatus = Options.getInt(Options.OPTION_XSTATUS);
			//#sijapp cond.if target isnot  "DEFAULT"#
			if (Options.getInt(Options.OPTION_TYPING_MODE) > 0) capsStream.write(CAP_MTN);
			//#sijapp cond.end#
			for (int i = 0; i < ContactList.XS_CAPS.length; i += 17)
			{
				if (ContactList.XS_CAPS[i] == xStatus - 1) capsStream.write(ContactList.XS_CAPS, i+1, 16);
			}
			
			packet = capsStream.toByteArray();
			Util.putWord(packet, 2, packet.length-4);
		}
		catch (Exception e) {}
		
		c.sendPacket(new SnacPacket(0x0002, 0x0004, 0, new byte[0], packet));
	}

	
		static public synchronized void setCliStatus(boolean updatePStatus, boolean updateDC) throws mipException
		{
			setOnlineStatus((int) Options.getLong(Options.OPTION_ONLINE_STATUS), updatePStatus, updateDC);
		}	
	
    static public synchronized void setOnlineStatus(int status, boolean updateVisibility, boolean updateDC) throws mipException
    {
    	byte[] CLI_SETSTATUS_DATA = ConnectAction.CLI_SETSTATUS_DATA;
    	
		// Convert online status
		int onlineStatus = Util.translateStatusSend(status);

		int visibilityItemId = Options.getInt(Options.OPTION_VISIBILITY_ID);
		byte[] buf = new byte[15];
		if(visibilityItemId != 0 && updateVisibility)
		{
			// Build packet for privacy setting changing
			int marker = 0;
			Integer bCode = new Integer(Options.getInt(Options.OPTION_PRIVATESTATUS));
			
			Util.putWord(buf, marker,    0); marker += 2; // name (null)
			Util.putWord(buf, marker,    0); marker += 2; // GroupID
			Util.putWord(buf, marker,  visibilityItemId); marker += 2; // EntryID
			Util.putWord(buf, marker,    4); marker += 2; // EntryType
			Util.putWord(buf, marker,    5); marker += 2; // Length in bytes of following TLV
			Util.putWord(buf, marker, 0xCA); marker += 2; // TLV Type
			Util.putWord(buf, marker,    1); marker += 2; // TLV Length
			Util.putByte(buf, marker, bCode.byteValue());              // TLV Value

			// Change privacy setting according to new status
				SnacPacket reply2pre = new SnacPacket(SnacPacket.CLI_ROSTERUPDATE_FAMILY,
										   SnacPacket.CLI_ROSTERUPDATE_COMMAND,
										   SnacPacket.CLI_ROSTERUPDATE_COMMAND,
										   new byte[0],
										   buf);
				c.sendPacket(reply2pre);
				reply2pre = null;
		}
		int setStatus = 0x11000000;
		// Send a CLI_SETSTATUS packet
		
		if (Options.getBoolean(Options.OPTION_WEBAWARE))
		{
			setStatus |= 0x00010000;
		}
		
		if (Options.getInt(Options.OPTION_BFLAG) != 0)
		{
			setStatus |= 0x00080000;
		}
		
		if (updateDC)
		{
			Util.putDWord(CLI_SETSTATUS_DATA, 4, setStatus | onlineStatus);
			Util.putWord(CLI_SETSTATUS_DATA, 21, Options.getInt(Options.OPTION_DCVERSION));
			switch(Options.getInt(94))
			{
				case 1:
					Util.putDWord(CLI_SETSTATUS_DATA, 35, 0x7FFFFFFF);
					Util.putDWord(CLI_SETSTATUS_DATA, 39, 0x00030869);
					break;
				case 5:
					Util.putDWord(CLI_SETSTATUS_DATA, 35, 0x00002330);
					break;
				case 4:
					Util.putDWord(CLI_SETSTATUS_DATA, 35, 0x08000600);
					break;
				case 10:
					Util.putDWord(CLI_SETSTATUS_DATA, 35, 0xFFFFF666);
					break;
			}
			
			SnacPacket packet = new SnacPacket(SnacPacket.CLI_SETSTATUS_FAMILY, SnacPacket.CLI_SETSTATUS_COMMAND, SnacPacket.CLI_SETSTATUS_COMMAND, new byte[0], CLI_SETSTATUS_DATA);						
			c.sendPacket(packet);
			packet = null;
		}
		else
		{
			byte[] StatusPacket = {(byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x04, (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00};
			Util.putDWord(StatusPacket, 4, setStatus | onlineStatus);
			SnacPacket packet = new SnacPacket(SnacPacket.CLI_SETSTATUS_FAMILY, SnacPacket.CLI_SETSTATUS_COMMAND, SnacPacket.CLI_SETSTATUS_COMMAND, new byte[0], StatusPacket);
			c.sendPacket(packet);	
			StatusPacket = null;
			packet = null;
		}
		
		Options.setLong(Options.OPTION_ONLINE_STATUS, status);
		Options.safe_save();
		
		lastStatusChangeTime = Util.getDateString(true, true); 
	}
    
    public static String getLastStatusChangeTime()
    {
    	return lastStatusChangeTime;
    }
	private static String detectClientVersion(byte[] buf1, int cli, int tlvNum)
    {
	    byte[] buf = new byte[16];
	    System.arraycopy(buf1,tlvNum*16,buf,0,16);
	    String ver = "";
	    if (cli == CAPF_MIRANDAIM )
	    {
		   if ( (buf[0xC]==0)&&(buf[0xD]==0)&&(buf[0xE]==0)&&(buf[0xF]==1) )
		    {
			    ver = "0.1.2.0";
		    }
		    //else if ( (buf[0xC]==0)&&(buf[0xD]<=3)&&(buf[0xE]<=3)&&(buf[0xF]<=1) )
		    //{
			//    ver = "0." + buf[0xD] + "." +buf[0xE] +"." + buf[0xF];
		 //   }
			//else if ( (buf[0x00]==0x69)&&(buf[0x01]==0x63)&&(buf[0x02]==0x71)&&(buf[0x03]==0x70) )
			//{
				
		//	}
		    else
		    {
			    ver = "Core " + buf[0x4] + "." +buf[0x5] +"." + buf[0x6] + "." + buf[0x7] + " ICQ 0." +buf[0x9] +"." + buf[0xa] + "." + buf[0xb];
		    }
	    }
	    else if (cli == CAPF_LICQ)
	    {
		    ver = buf[0xC] + "." + (buf[0xD]%100) +"." + buf[0xE];
	    }
		else if (cli == CLI_SMAPER)
		{
			ver = Util.byteArrayToString (buf,7,8,true);
		}
	    else if (cli == CAPF_KOPETE)
	    {
		    ver = buf[0xC] + "." +buf[0xD] + "." +buf[0xE] + "." +buf[0xF];
	    }
	    else if (cli == CAPF_ANDRQ)
	    {
		  ver = buf[0xC] + "." + buf[0xB] + "." + buf[0xA] + "." + buf[0x9];
		}
	    else if (cli == CAPF_JIMM)
		    ver = Util.byteArrayToString(buf,5,11,true);
	    else if (cli == CAPF_QIP)
		    ver = Util.byteArrayToString(buf,11,4,true);
		else if (cli == CAPF_MIP)
			ver = Util.byteArrayToString(buf,4,12,true);
	    else if ( cli == CAPF_MCHAT )
	    	ver = Util.byteArrayToString(buf,0,16,true);
	    
	    return ver;
    }
	
	public static void detectUserClient(ContactListContactItem item, int dwFP1, int dwFP2, int dwFP3, byte[] capabilities, int wVersion, boolean statusChange, int newstatus)
	{
		/*System.out.println("uin - " + uin + " found capabilities count:" + capabilities.length/16);
		PrintCapabilities("cap - ", capabilities);*/
		int client = CLI_NONE;
		String szVersion = "";
		int caps = CAPF_NO_INTERNAL;
		
			if ( capabilities!=null )
			{
			//Caps parsing
			for (int j = 0; j < capabilities.length / 16; j++)
			{
				if (Util.byteArrayEquals(capabilities, j * 16, CAP_AIM_SERVERRELAY, 0, 16))
				{
					caps |= CAPF_AIM_SERVERRELAY_INTERNAL;
				} 
				
				else if (dwFP1 == 0xFFFFF666)
				{ // this is R&Q (Rapid Edition)
					client = CLI_RANDQ;
					szVersion = "(" + (dwFP2) + ")"; 
					break;
				}
				
				else if (Util.byteArrayEquals(capabilities, j * 16, CAP_INFIUM, 0, 16))
				{
					client = CLI_INFIUM;
					szVersion = "(" + (dwFP1) + ")";
				}
				
				else if (Util.byteArrayEquals(capabilities, j*16, CAP_SMAPER, 0, 7))
				{
					client = CLI_SMAPER;
					szVersion = detectClientVersion(capabilities,CLI_SMAPER,j);
				}
				
				else if (Util.byteArrayEquals(capabilities, j * 16, CAP_RANDQ, 0, 8))
				{
					client = CLI_RANDQ;
					szVersion = detectClientVersion(capabilities,CAPF_ANDRQ,j);
				}
				
				
				else if (Util.byteArrayEquals(capabilities, j * 16, CAP_UTF8, 0, 16))
				{
					caps |= CAPF_UTF8_INTERNAL;
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_MIRANDAIM, 0, 7) || Util.byteArrayEquals(capabilities, j * 16, CAP_MIRANDAIM2, 0, 3))
				{
					client = CLI_MIRANDA;
					caps |= CAPF_MIRANDAIM;
					szVersion = detectClientVersion(capabilities,CAPF_MIRANDAIM,j);
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_TRILLIAN, 0, 16))
				{
					caps |= CAPF_TRILLIAN;
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_TRILCRYPT, 0, 16))
				{
					caps |= CAPF_TRILCRYPT;
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_SIM, 0, 0xC))
				{
					caps |= CAPF_SIM;
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_SIMOLD, 0, 16))
				{
					caps |= CAPF_SIMOLD;
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_LICQ, 0, 0xC))
				{
					caps |= CAPF_LICQ;
					szVersion = detectClientVersion(capabilities, CAPF_LICQ,j);
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_KOPETE, 0, 0xC))
				{
					caps |= CAPF_KOPETE;
					szVersion = detectClientVersion(capabilities, CAPF_KOPETE,j);
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_MICQ, 0, 16))
				{
					caps |= CAPF_MICQ;
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_ANDRQ, 0, 9))
				{
					caps |= CAPF_ANDRQ;
					szVersion = detectClientVersion(capabilities,CAPF_ANDRQ,j);
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_QIP, 0, 11))
				{
					caps |= CAPF_QIP;
					szVersion = detectClientVersion(capabilities, CAPF_QIP,j);
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_MIP, 0, 4))
				{
					caps |= CAPF_MIP;
					szVersion = detectClientVersion(capabilities, CAPF_MIP, j);
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_MACICQ, 0, 16))
				{
					caps |= CAPF_MACICQ;
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_RICHTEXT, 0, 16))
				{
					caps |= CAPF_RICHTEXT;
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_IS2001, 0, 16))
				{
					caps |= CAPF_IS2001;
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_IS2002, 0, 16))
				{
					caps |= CAPF_IS2002;
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_STR20012, 0, 16))
				{
					caps |= CAPF_STR20012;
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_AIMICON, 0, 16))
				{
					caps |= CAPF_AIMICON;
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_AIMCHAT, 0, 16))
				{
					caps |= CAPF_AIMCHAT;
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_UIM, 0, 16))
				{
					caps |= CAPF_UIM;
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_RAMBLER, 0, 16))
				{
					caps |= CAPF_RAMBLER;
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_ABV, 0, 16))
				{
					caps |= CAPF_ABV;
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_NETVIGATOR, 0, 16))
				{
					caps |= CAPF_NETVIGATOR;
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_XTRAZ, 0, 16))
				{
					caps |= CAPF_XTRAZ;
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_AIMFILE, 0, 16))
				{
					caps |= CAPF_AIMFILE;
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_JIMM, 0, 5))
				{
					caps |= CAPF_JIMM;
					szVersion = detectClientVersion(capabilities,CAPF_JIMM,j);
				} else if (Util.byteArrayEquals(capabilities, j * 16, CAP_AIMIMIMAGE, 0, 16))
					caps |= CAPF_AIMIMIMAGE;
				else if (Util.byteArrayEquals(capabilities, j * 16, CAP_AVATAR, 0, 16))
					caps |= CAPF_AVATAR;
				else if (Util.byteArrayEquals(capabilities, j * 16, CAP_DIRECT, 0, 16))
					caps |= CAPF_DIRECT;
				else if (Util.byteArrayEquals(capabilities, j * 16, CAP_TYPING, 0, 16))
					caps |= CAPF_TYPING;
				else if ( Util.byteArrayEquals(capabilities, j*16, CAP_MCHAT,0,9) )
				{
					caps |= CAPF_MCHAT;
					szVersion = detectClientVersion(capabilities, CAPF_MCHAT,j);
				}
				
			}
				item.setIntValue(ContactListContactItem.CONTACTITEM_CAPABILITIES,caps);
		}
		if (client!=CLI_NONE)
		{
				item.setIntValue(ContactListContactItem.CONTACTITEM_CLIENT,client);
				item.setStringValue(ContactListContactItem.CONTACTITEM_CLIVERSION,szVersion);
				return;
		}
		
			//Client detection
			//If this is status change we don`t need to detect client... 
			if ( !statusChange )
			{
		switch(1)
		{
		default:
			if( (caps&CAPF_MCHAT) != 0 )
			{
				client = CLI_MCHAT;
				break;
			}
			if ( (caps&CAPF_JIMM) !=0)
			{
				client = CLI_JIMM;
				break;
			}
			if ( (caps&CAPF_ANDRQ) !=0)
			{
				client = CLI_ANDRQ;
				break;
			}
			if ( (caps&CAPF_QIP) !=0)
			{
				client = CLI_QIP;
				if (((dwFP1>>24)&0xFF) != 0)
					szVersion += " (" + ((dwFP1>>24)&0xFF) + ((dwFP1>>16)&0xFF) + ((dwFP1>>8)&0xFF) + (dwFP1&0xFF) + ")";
				break;
			}
		
			if ( ((caps&(CAPF_TRILLIAN+CAPF_TRILCRYPT))!=0) && (dwFP1 == 0x3b75ac09 ) )
			{
				client = CLI_TRILLIAN;
				break;
			}
			
			if ( (caps&CAPF_MIP)!=0)
			{
				client = CLI_MIP;
				break;
			}
			
			if ( (caps&(CAPF_SIM+CAPF_SIMOLD))!=0 )
			{
				client = CLI_SIM;
				break;
			}
			
			if ( (caps&CAPF_KOPETE)!=0 )
			{
				client = CLI_KOPETE;
				break;
			}
			
			if ( (caps&CAPF_LICQ)!=0 )
			{
				client = CLI_LICQ;
				break;
			}
			
			if (((caps&CAPF_AIMICON)!=0)&&((caps&CAPF_AIMFILE)!=0)&&((caps&CAPF_AIMIMIMAGE)!=0) )
			{
				client = CLI_GAIM;
				break;
			}
						
			if ( (caps&CAPF_UTF8_INTERNAL)!=0)
			{
				switch (wVersion) {
					case 10:
					if ( ((caps&CAPF_TYPING)!=0) && ((caps&CAPF_RICHTEXT)!=0) )
					{
						client = CLI_ICQ2003B;
					}
					case 7:
					if ( ((caps&CAPF_AIM_SERVERRELAY_INTERNAL)==0)&&((caps&CAPF_DIRECT)==0) && (dwFP1==0) && (dwFP2==0) && (dwFP3==0) )
					{
						client = CLI_ICQ2GO;
					}
					break;
					default:
					if ( (dwFP1==0) && (dwFP2==0) && (dwFP3==0) )
					{
						if ( (caps&CAPF_RICHTEXT)!=0 )
						{
							client = CLI_ICQLITE;
							if ( ((caps&CAPF_AVATAR)!=0) && ((caps&CAPF_XTRAZ)!=0) )
							{
								if ( (caps&CAPF_AIMFILE)!=0 ) // TODO: add more
									{
										client = CLI_ICQLITE5;
									}
								else
									{
										client = CLI_ICQLITE4;
									}
									
							}
						}
						else
							if ( (caps&CAPF_UIM)!=0 )
								client = CLI_UIM;
							else
							{
								client = CLI_AGILE;
							}
					}
					break;
				}
			}
			
			if ( (caps&CAPF_MACICQ)!=0 )
			{
				client = CLI_MACICQ;
				break;
			}
			
			if ( (caps&CAPF_AIMCHAT)!=0 )
			{
				client = CLI_AIM;
				break;
			}
			
			if (  (dwFP1 & 0xFF7F0000) == 0x7D000000 )
			{
				client = CLI_LICQ;
				int ver = dwFP1 & 0xFFFF;
				if (ver % 10 !=0)
				{
					szVersion = ver / 1000 + "."+(ver / 10) % 100+"."+ ver % 10;
				}
				else
				{
					szVersion = ver / 1000 + "."+(ver / 10) % 100;
				}
				break;
			}
			
			switch (dwFP1) {
			case 0x7FFFFFFF:
	 	 
 if ((caps & CAPF_MIRANDAIM) != 0)
	 	                                         {
	 	                                                 client = CLI_MIRANDA;
	 	                                                 szVersion = "IM: " + szVersion + " ICQ: " +((dwFP2 >> 24) & 0x7F) + "." + ((dwFP2 >> 16) & 0xFF) + "." + ((dwFP2 >> 8) & 0xFF) + "." + (dwFP2 & 0xFF);
	 	                                         }
	 	                                         break;
				case 0xFFFFFFFF:
				if ((dwFP3 == 0xFFFFFFFF) && (dwFP2 == 0xFFFFFFFF)) 
				{
					client = CLI_GAIM;
					break;
				}
				if ( (dwFP2==0) && (dwFP3 != 0xFFFFFFFF) )
				{
					if (wVersion == 7) 
					{
						client = CLI_WEBICQ;
						break;
					}
					if ( (dwFP3 == 0x3B7248ED) && ((caps&CAPF_UTF8_INTERNAL)==0) && ((caps&CAPF_RICHTEXT)==0 ) ) 
					{
						client = CLI_SPAM;
						break;
					}
				}
				client = CLI_MIRANDA;
				
					szVersion = ((dwFP2>>24)&0x7F)+"."+((dwFP2>>16)&0xFF)+"."+((dwFP2>>8)&0xFF)+"."+(dwFP2&0xFF);
				break;
				case 0xFFFFFFFE:
				if (dwFP3==dwFP1) 
				{
					client = CLI_JIMM;
				}
				break;
				case 0xFFFFFF8F:
				client = CLI_STRICQ;
				break;
				case 0xFFFFFF42:
				client = CLI_MICQ;
				break;
				case 0xFFFFFFBE:
				client = CLI_ALICQ;
				break;
				case 0xFFFFFF7F:
				client = CLI_ANDRQ;
				szVersion = ((dwFP2>>24)&0xFF)+"."+((dwFP2>>16)&0xFF)+"."+((dwFP2>>8)&0xFF)+"."+(dwFP2&0xFF);
				break;
				case 0xFFFFFFAB:
				client = CLI_YSM;
				break;
				case 0x04031980:
				client = CLI_VICQ;
				break;
				case 0x3AA773EE:
				if ((dwFP2 == 0x3AA66380) && (dwFP3 == 0x3A877A42))
				{
					if (wVersion==7)
					{
						if ( ((caps&CAPF_AIM_SERVERRELAY_INTERNAL)!=0) && ((caps&CAPF_DIRECT)!=0) )
						{
							if ( (caps&CAPF_RICHTEXT)!=0 ) 
							{
								client = CLI_CENTERICQ;
								break;
							}
							client = CLI_LIBICQJABBER;
						}
					}
					client = CLI_LIBICQ2000;
				}
				break;
				case 0x3b75ac09:
				client = CLI_TRILLIAN;
				break;
				case 0x3BA8DBAF: // FP2: 0x3BEB5373; FP3: 0x3BEB5262;
				if (wVersion==2)
					client = CLI_STICQ;
				break;
				case 0x4201F414:
				if ( ((dwFP2 & dwFP3) == dwFP1) && (wVersion == 8) )
					client = CLI_SPAM;
				break;
				default: break;
			}
			
			if (client != CLI_NONE) break;
			
			if ( (dwFP1!=0) && (dwFP1 == dwFP3) && (dwFP3 == dwFP2) && (caps==0)) 
			{
				client = CLI_VICQ;
				break;
			}
			if ( ((caps&CAPF_AIM_SERVERRELAY_INTERNAL)!=0) && ((caps&CAPF_DIRECT)!=0) && ((caps&CAPF_UTF8_INTERNAL)!=0) && ((caps&CAPF_RICHTEXT)!=0) ) 
			{
				
				if ( (dwFP1!=0) && (dwFP2!=0) && (dwFP3!=0) )
					client = CLI_ICQ2002A2003A;
				break;
			}
			if ( ((caps&(CAPF_STR20012+CAPF_AIM_SERVERRELAY_INTERNAL))!=0) && ((caps&CAPF_IS2001)!=0) )
			{
				if ( (dwFP1==0) && (dwFP2==0) && (dwFP3==0) && (wVersion==0))
					client = CLI_ICQPPC;
				else
					client = CLI_ICQ2001B; //FP1: 1068985885; FP2:0; FP3:1068986138
				break;
			}
			if (wVersion==7) 
			{
				if( ((caps&CAPF_AIM_SERVERRELAY_INTERNAL)!=0)&&((caps&CAPF_DIRECT)!=0) )
				{
					if ( (dwFP1==0) && (dwFP2==0) && (dwFP3==0) )
						{
							client = CLI_ANDRQ;
						}
					else
						{
							client = CLI_ICQ2000;
						}
					break;
				}
				else
				if ( (caps&CAPF_RICHTEXT)!=0 ) 
				{
					client = CLI_GNOMEICQ;
					break;
				}
			}
			if (dwFP1 > 0x35000000 && dwFP1 < 0x40000000) 
			{
				switch(wVersion) 
				{
					case 6:  client = CLI_ICQ99;break;
					case 7:  client = CLI_ICQ2000;
					break;
					
					case 8:  client = CLI_ICQ2001B;
					break;
					
					case 9:  client = CLI_ICQLITE;
					break;
					
					case 10: client = CLI_ICQ2003B;
					break;
				}
				break;
			} 
		}
		
	}
		if (client!=CLI_NONE)
		{
				item.setIntValue(ContactListContactItem.CONTACTITEM_CLIENT,client);
				item.setStringValue(ContactListContactItem.CONTACTITEM_CLIVERSION,szVersion);
		}
	
	}
}
