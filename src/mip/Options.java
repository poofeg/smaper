/*******************************************************************************
Current record store format:

Record #1: VERSION               (UTF8)
Record #2: OPTION KEY            (BYTE)
OPTION VALUE          (Type depends on key)
OPTION KEY            (BYTE)
OPTION VALUE          (Type depends on key)
OPTION KEY            (BYTE)
OPTION VALUE          (Type depends on key)
...

Option key            Option value
0 -  63 (00XXXXXX)  UTF8
64 - 127 (01XXXXXX)  INTEGER
128 - 191 (10XXXXXX)  BOOLEAN (158)
192 - 224 (110XXXXX)  LONG
225 - 255 (111XXXXX)  SHORT, BYTE-ARRAY (scrambled String)
 ******************************************************************************/
package mip;

import mip.comm.Util;
import mip.comm.Icq;
import mip.util.ResourceBundle;
import mip.comm.RegisterNewUinAction;

import java.io.*;
import java.util.*;

import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import DrawControls.VirtualList;

public class Options {
    /* Option keys */

    static final int OPTION_UIN1 = 0;   /* String */

    static final int OPTION_PASSWORD1 = 228;   /* String  */

    static final int OPTION_UIN2 = 14;   /* String  */

    static final int OPTION_PASSWORD2 = 229;   /* String  */

    static final int OPTION_UIN3 = 15;   /* String  */

    static final int OPTION_PASSWORD3 = 230;   /* String  */

    static final int OPTIONS_CURR_ACCOUNT = 86;   /* int     */

    public static final int OPTION_SMAPEACC = 231;   /* String  */

    public static final int OPTION_SMAPEPASS = 232;   /* String  */

    public static final int OPTION_SMAPEID = 234;   //int
    public static final int OPTION_SMAPEPICT = 233;   //url
    public static final int OPTION_THEME = 235;   /* String  */
    // Theese two options are not stored in RMS 
    public static final int OPTION_UIN = 254;   /* String  */

    public static final int OPTION_PASSWORD = 255;   /* String  */

    public static final int OPTION_SRV_HOST = 1;   /* String  */

    public static final int OPTION_SRV_PORT = 2;   /* String  */

    public static final int OPTION_KEEP_CONN_ALIVE = 128;   /* boolean */

    public static final int OPTION_CONN_ALIVE_INVTERV = 13;   /* String  */

    public static final int OPTION_CONN_PROP = 64;   /* int     */

    public static final int OPTION_CONN_TYPE = 83;   /* int     */

    public static final int OPTION_AUTO_CONNECT = 138;   /* boolean */
    //#sijapp cond.if target isnot  "MOTOROLA"#
    public static final int OPTION_SHADOW_CON = 139;   /* boolean */
    //#sijapp cond.end#
    //!Extended MIP features
    public static final int OPTION_UTF8IN = 131;    /*int   */

    public static final int OPTION_UTF8OUT = 132;
    public static final int OPTION_CLI_ID = 94;    /*int   */

    public static final int OPTION_DCVERSION = 98;
    public static final int OPTION_XSTATUS = 95;    /*int  */

    public static final int OPTION_BFLAG = 96;
    public static final int OPTION_WEBAWARE = 174;
    public static final int OPTION_MULTIPLESERVERS = 164;
    public static final int OPTION_SHOWXTRAZMSG = 175;
    public static final int OPTION_STATUS_MESSAGE = 7;   /* String  */

    public static final int OPTION_STATUS_TITLE = 39;
    public static final int OPTION_NICKNAME = 54;
    public static final int OPTION_BACKGROUND = 163;
    public static final int OPTION_SHOWCLIICONS = 176;
    public static final int OPTION_PRIVATESTATUS = 115;
    public static final int OPTION_BRING_UP = 151;   /* boolean */

    public static final int OPTION_SENDMTN = 167;
    public static final int OPTION_SWAPSOFTS = 177;
    public static final int OPTION_SHIFT = 103;
    public static final int OPTION_HASSHIFT = 182;
    public static final int OPTION_SHOWMENUICONS = 162;
    public static final int OPTION_USERID = 26;
    public static final int OPTION_ANIMSMILES = 173;
    public static final int OPTION_SECONDS = 183;
    public static final int OPTION_LOADCL = 184;    //114, 116, 117, 118, 119 - custom user theme colors =) Yahoo)
    public static final int OPTION_COLOR1 = 114;
    public static final int OPTION_COLOR2 = 116;
    public static final int OPTION_COLOR3 = 117;
    public static final int OPTION_COLOR4 = 118;
    public static final int OPTION_COLOR5 = 119;
    public static final int OPTION_COLOR6 = 120;
    public static final int OPTION_COLOR7 = 121;
    public static final int OPTION_COLOR8 = 122;
    public static final int OPTION_COLOR9 = 123;
    public static final int OPTION_COLOR10 = 124;    //Custom client version (for MIP & mip) =) YES! Extended MIP v1.1 feature! ;)
    public static final int OPTION_CUSTOMID = 17;   /* String  */

    public static final int OPTION_RECONNECT = 149;   /* boolean */

    public static final int OPTION_RECONNECT_NUMBER = 91;   	/* int */

    public static final int OPTION_UI_LANGUAGE = 3;   /* String  */

    public static final int OPTION_DISPLAY_DATE = 129;   /* boolean */

    public static final int OPTION_CL_SORT_BY = 65;   /* int     */

    public static final int OPTION_CL_HIDE_OFFLINE = 130;   /* boolean */
    //#sijapp cond.if target isnot  "DEFAULT"#	
    public static final int OPTION_MESS_NOTIF_MODE = 66;   /* int     */

    public static final int OPTION_MESS_NOTIF_FILE = 4;   /* String  */

    public static final int OPTION_MESS_NOTIF_VOL = 67;   /* int     */

    public static final int OPTION_ONLINE_NOTIF_MODE = 68;   /* int     */

    public static final int OPTION_ONLINE_NOTIF_FILE = 5;   /* String  */

    public static final int OPTION_ONLINE_NOTIF_VOL = 69;   /* int     */

    public static final int OPTION_VIBRATOR = 75;   /* integer */

    public static final int OPTION_VIBR_SILENT = 152;   /* boolean */

    public static final int OPTION_TYPING_MODE = 88;   /* integer */

    public static final int OPTION_TYPING_FILE = 16;   /* String */

    public static final int OPTION_TYPING_VOL = 89;
    //#sijapp cond.end #	
    public static final int OPTION_CP1251_HACK = 133;   /* boolean */
    //#sijapp cond.if modules_TRAFFIC is "true" #
    public static final int OPTION_COST_PER_PACKET = 70;   /* int     */

    public static final int OPTION_COST_PER_DAY = 71;   /* int     */

    public static final int OPTION_COST_PACKET_LENGTH = 72;   /* int     */

    public static final int OPTION_CURRENCY = 6;   /* String  */
    //#sijapp cond.end #
    public static final int OPTION_ONLINE_STATUS = 192;   /* long    */

    public static final int OPTION_CHAT_SMALL_FONT = 135;   /* boolean */

    public static final int OPTION_USER_GROUPS = 136;   /* boolean */

    public static final int OPTION_HISTORY = 137;   /* boolean */

    public static final int OPTION_SHOW_LAST_MESS = 142;   /* boolean */
    //#sijapp cond.if target="SIEMENS2"#
    public static final int OPTION_CLASSIC_CHAT = 143;   /* boolean */
    //#sijapp cond.end#
    public static final int OPTION_COLOR_SCHEME = 73;
    public static final int OPTION_LIGHT_TIMEOUT = 74;   /* int     */

    public static final int OPTION_LIGHT_MANUAL = 140;   /* boolean */

    public static final int OPTION_FLASH_BACKLIGHT = 147;   /* boolean */

    public static final int OPTION_USE_SMILES = 141;   /* boolean */

    public static final int OPTION_MD5_LOGIN = 144;   /* boolean */
    //#sijapp cond.if modules_PROXY is "true" #
    public static final int OPTION_PRX_TYPE = 76;   /* int     */

    public static final int OPTION_PRX_SERV = 8;   /* String  */

    public static final int OPTION_PRX_PORT = 9;   /* String  */

    public static final int OPTION_AUTORETRY_COUNT = 10;   /* String  */

    public static final int OPTION_PRX_NAME = 11;   /* String  */

    public static final int OPTION_PRX_PASS = 12;   /* String  */
    //#sijapp cond.end#
    public static final int OPTIONS_GMT_OFFSET = 87;   /* int     */

    public static final int OPTIONS_LOCAL_OFFSET = 90;   /* int     */

    public static final int OPTION_FULL_SCREEN = 145;   /* boolean */

    public static final int OPTION_SILENT_MODE = 150;   /* boolean */

    protected static final int OPTIONS_LANG_CHANGED = 148;
    public static final int OPTION_POPUP_WIN2 = 84;   /* int     */

    public static final int OPTION_EXT_CLKEY0 = 77;   /* int     */

    public static final int OPTION_EXT_CLKEYSTAR = 78;   /* int     */

    public static final int OPTION_EXT_CLKEY4 = 79;   /* int     */

    public static final int OPTION_EXT_CLKEY6 = 80;   /* int     */

    public static final int OPTION_EXT_CLKEYCALL = 81;   /* int     */

    public static final int OPTION_EXT_CLKEYPOUND = 82;   /* int     */

    public static final int OPTION_EXT_CLKEYMENU = 92;   /* int     */

    public static final int OPTION_VISIBILITY_ID = 85;   /* int     */

    public static final int OPTION_ANTISPAM_MSG = 18;   /* String  */

    public static final int OPTION_ANTISPAM_HELLO = 19;   /* String  */

    public static final int OPTION_ANTISPAM_ANSWER = 20;   /* String  */

    public static final int OPTION_ANTISPAM_ENABLE = 153;   /* boolean */

    public static final int OPTION_ANTISPAM_INVIS = 157;   /* boolean */

    public static final int OPTION_AUTOAWAY_ENABLE = 154;   /* boolean */

    public static final int OPTION_AUTOSTATUS_DELAY = 99;   /* int     */

    public static final int OPTION_AUTONA_ENABLE = 155;   /* boolean */

    public static final int OPTION_AUTOONLINE = 156;   /* boolean */
    //Hotkey Actions
    public static final int HOTKEY_NONE = 0;
    public static final int HOTKEY_INFO = 2;
    public static final int HOTKEY_NEWMSG = 3;
    public static final int HOTKEY_ONOFF = 4;
    public static final int HOTKEY_OPTIONS = 5;
    public static final int HOTKEY_MENU = 6;
    public static final int HOTKEY_LOCK = 7;
    public static final int HOTKEY_HISTORY = 8;
    public static final int HOTKEY_MINIMIZE = 9;
    public static final int HOTKEY_CLI_INFO = 10;
    public static final int HOTKEY_FULLSCR = 11;
    public static final int HOTKEY_SOUNDOFF = 12;    //Currently implemented Client IDs
    public static final int CLI_ID_MIP = 11;
    public static final int CLI_ID_QIP = 4;
    public static final int CLI_ID_JIMM = 7;
    public static final int CLI_ID_MACICQ = 8;
    public static final int CLI_ID_LICQ = 3;
    public static final int CLI_ID_KOPETE = 2;
    public static final int CLI_ID_SIM = 6;
    public static final int CLI_ID_RQ = 9;
    public static final int CLI_ID_MIRANDA = 1;
    public static final int CLI_ID_INFIUM = 5;
    public static final int CLI_ID_RQ2 = 10;
    public static final int CLI_ID_SMAPER = 0;
    public static final int THEME_COLOR1 = 256;
    public static final int THEME_COLOR2 = 257;
    public static final int THEME_COLOR3 = 258;
    public static final int THEME_COLOR4 = 259;
    public static final int THEME_COLOR5 = 260;
    public static final int THEME_COLOR6 = 261;
    public static final int THEME_COLOR7 = 262;
    public static final int THEME_COLOR8 = 263;
    public static final int THEME_COLOR9 = 264;
    public static final int THEME_COLOR10 = 265;
    public static final int THEME_PIC_CICONS = 0;
    public static final int THEME_PIC_ICONS = 1;
    public static final int THEME_PIC_MICONS = 2;
    public static final int THEME_PIC_PRLISTS = 3;
    public static final int THEME_PIC_PSTATUS = 4;
    public static final int THEME_PIC_XSTATUS = 5;
    public static final int THEME_MP3_MESSAGE = 34;
    public static final int THEME_MP3_ONLINE = 35;
    public static final int THEME_MP3_TYPING = 36;
    public static final int THEME_ONLY_COLORS = 128;
    static int accountKeys[] = {
        Options.OPTION_UIN1, Options.OPTION_PASSWORD1,
        Options.OPTION_UIN2, Options.OPTION_PASSWORD2,
        Options.OPTION_UIN3, Options.OPTION_PASSWORD3,
    };
    /**************************************************************************/
    final public static String emptyString = new String();
    // Hashtable containing all option key-value pairs
    static private Hashtable options;    // Options form
    static OptionsForm optionsForm;
    /* Private constructor prevent to create instances of Options class */
    static public Vector themesList;
    
    public Options() {
        // Try to load option values from record store and construct options form
        try {
            options = new Hashtable();

            Options.setDefaults();

            load();

            if (getBoolean(OPTIONS_LANG_CHANGED)) {
                setBoolean(OPTIONS_LANG_CHANGED, false);
            //System.out.println("Options.resetLangDependedOpts()");
            }
        } // Use default values if loading option values from record store failed
        catch (Exception e) {
            setDefaults();
        }

        ResourceBundle.setCurrUiLanguage(getString(Options.OPTION_UI_LANGUAGE));
        VirtualList.setFullScreen(getBoolean(Options.OPTION_FULL_SCREEN));
    }

    /* Set default values
    This is done before loading because older saves may not contain all new values */
    static private void setDefaults() {
        //#sijapp cond.if target="MIDP2"#
        setBoolean(Options.OPTION_BRING_UP, true);
        //#sijapp cond.end#
        setBoolean(Options.OPTION_SWAPSOFTS, false);
        setBoolean(Options.OPTION_SHOWMENUICONS, true);
        setBoolean(Options.OPTION_HASSHIFT, false);
        setInt(Options.OPTION_SHIFT, 17);
        setString(Options.OPTION_USERID, "-1");
        setBoolean(Options.OPTION_UTF8IN, true);
        setBoolean(Options.OPTION_ANIMSMILES, true);
        setBoolean(Options.OPTION_SECONDS, false);
        setBoolean(Options.OPTION_LOADCL, true);
        setBoolean(Options.OPTION_UTF8OUT, true);
        setString(Options.OPTION_UIN1, emptyString);
        setString(Options.OPTION_PASSWORD1, emptyString);
        setString(Options.OPTION_SMAPEACC, emptyString);
        setString(Options.OPTION_SMAPEPASS, emptyString);
        setString(Options.OPTION_SMAPEID, emptyString);
        setString(Options.OPTION_SMAPEPICT, emptyString);
        setString(Options.OPTION_SRV_HOST, "login.icq.com");
        setString(Options.OPTION_SRV_PORT, "5190");
        setBoolean(Options.OPTION_KEEP_CONN_ALIVE, true);
        setBoolean(Options.OPTION_RECONNECT, true);
        setInt(Options.OPTION_RECONNECT_NUMBER, 15);
        setInt(Options.OPTION_PRIVATESTATUS, 4);
        setString(Options.OPTION_CUSTOMID, "v###VERSION###");
        setString(Options.OPTION_STATUS_MESSAGE, emptyString);

        setString(Options.OPTION_STATUS_TITLE, emptyString);
        setInt(Options.OPTION_CLI_ID, 0);
        setBoolean(Options.OPTION_WEBAWARE, false);
        setBoolean(Options.OPTION_SHOWXTRAZMSG, true);
        setInt(Options.OPTION_DCVERSION, 9);
        setInt(Options.OPTION_XSTATUS, -1);
        setInt(Options.OPTION_BFLAG, 0);
        setBoolean(Options.OPTION_MULTIPLESERVERS, true);
        setBoolean(Options.OPTION_SENDMTN, true);

        setInt(Options.OPTION_COLOR1, 0xB00000);
        setInt(Options.OPTION_COLOR2, 0x700000);
        setInt(Options.OPTION_COLOR3, 0x000000);
        setInt(Options.OPTION_COLOR4, 0xFFFFFF);
        setInt(Options.OPTION_COLOR5, 0x00DDB0);
        setInt(Options.OPTION_COLOR6, 0xFF4350);
        setInt(Options.OPTION_COLOR7, 0x00CC55);
        setInt(Options.OPTION_COLOR8, 0x808080);
        setInt(Options.OPTION_COLOR9, 0xFFFFFF);
        setInt(Options.OPTION_COLOR10, 0x000040);

        setString(Options.OPTION_CONN_ALIVE_INVTERV, "90");
        setInt(Options.OPTION_CONN_PROP, 0);
        setInt(Options.OPTION_CONN_TYPE, 0);
        //#sijapp cond.if target isnot "MOTOROLA"#
        setBoolean(Options.OPTION_SHADOW_CON, false);
        //#sijapp cond.end#
        setBoolean(Options.OPTION_MD5_LOGIN, true);
        setBoolean(Options.OPTION_AUTO_CONNECT, false);
        setString(Options.OPTION_UI_LANGUAGE, ResourceBundle.langAvailable[0]);
        setBoolean(Options.OPTION_DISPLAY_DATE, false);
        setInt(Options.OPTION_CL_SORT_BY, 0);
        setBoolean(Options.OPTION_BACKGROUND, false);
        setBoolean(Options.OPTION_CL_HIDE_OFFLINE, false);
        //#sijapp cond.if target is "SIEMENS2"#
        setInt(Options.OPTION_MESS_NOTIF_MODE, 0);
        setString(Options.OPTION_MESS_NOTIF_FILE, "message.wav");
        setInt(Options.OPTION_MESS_NOTIF_VOL, 50);
        setInt(Options.OPTION_ONLINE_NOTIF_MODE, 0);
        setString(Options.OPTION_ONLINE_NOTIF_FILE, "online.wav");
        setInt(Options.OPTION_ONLINE_NOTIF_VOL, 50);
        setInt(Options.OPTION_TYPING_VOL, 50);
        setString(Options.OPTION_TYPING_FILE, "typing.wav");
        setInt(Options.OPTION_TYPING_MODE, 0);
        //#sijapp cond.else#
        setInt(Options.OPTION_MESS_NOTIF_MODE, 0);
        setString(Options.OPTION_MESS_NOTIF_FILE, "message.mp3");
        setInt(Options.OPTION_MESS_NOTIF_VOL, 50);
        setInt(Options.OPTION_ONLINE_NOTIF_MODE, 0);
        setString(Options.OPTION_ONLINE_NOTIF_FILE, "online.mp3");
        setInt(Options.OPTION_ONLINE_NOTIF_VOL, 50);
        setInt(Options.OPTION_TYPING_VOL, 50);
        setString(Options.OPTION_TYPING_FILE, "typing.mp3");
        setInt(Options.OPTION_TYPING_MODE, 0);
        //#sijapp cond.end#

        //	setInt    (Options.OPTION_LIGHT_TIMEOUT,      5);
        //	setBoolean(Options.OPTION_LIGHT_MANUAL,       true);	

        setBoolean(Options.OPTION_CP1251_HACK, ResourceBundle.langAvailable[0].equals("RU"));
        //#sijapp cond.if target isnot "DEFAULT"#
        setInt(Options.OPTION_VIBRATOR, 1);
        setBoolean(Options.OPTION_VIBR_SILENT, false);
        //#sijapp cond.end#		
        //#sijapp cond.if modules_TRAFFIC is "true" #
        setInt(Options.OPTION_COST_PER_PACKET, 0);
        setInt(Options.OPTION_COST_PER_DAY, 0);
        setInt(Options.OPTION_COST_PACKET_LENGTH, 1024);
        setString(Options.OPTION_CURRENCY, "$");
        //#sijapp cond.end #
        setLong(Options.OPTION_ONLINE_STATUS, ContactList.STATUS_ONLINE);

        setBoolean(Options.OPTION_CHAT_SMALL_FONT, true);

        setBoolean(Options.OPTION_USER_GROUPS, false);
        setBoolean(Options.OPTION_HISTORY, false);
        setInt(Options.OPTION_COLOR_SCHEME, CLRSCHHEME_CUSTOM);
        setBoolean(Options.OPTION_USE_SMILES, true);
        setBoolean(Options.OPTION_SHOW_LAST_MESS, false);
        //#sijapp cond.if modules_PROXY is "true" #
        setInt(Options.OPTION_PRX_TYPE, 2);
        setString(Options.OPTION_PRX_SERV, emptyString);
        setString(Options.OPTION_PRX_PORT, "1080");
        setString(Options.OPTION_AUTORETRY_COUNT, "2");
        setString(Options.OPTION_PRX_NAME, emptyString);
        setString(Options.OPTION_PRX_PASS, emptyString);
        //#sijapp cond.end #
        setInt(Options.OPTION_VISIBILITY_ID, 0);
        setString(Options.OPTION_NICKNAME, ResourceBundle.getString("me"));
        setInt(Options.OPTION_EXT_CLKEY0, HOTKEY_CLI_INFO);

        //#sijapp cond.if target isnot "DEFAULT" # 
        setBoolean(Options.OPTION_SILENT_MODE, false);
        //#sijapp cond.end # 


        setInt(Options.OPTION_EXT_CLKEY4, HOTKEY_ONOFF);
        setInt(Options.OPTION_EXT_CLKEY6, HOTKEY_INFO);
        setInt(Options.OPTION_EXT_CLKEYCALL, HOTKEY_NEWMSG);
        setInt(Options.OPTION_EXT_CLKEYMENU, HOTKEY_MENU);
        setInt(Options.OPTION_EXT_CLKEYPOUND, HOTKEY_LOCK);
        setInt(Options.OPTION_POPUP_WIN2, 0);
        //#sijapp cond.if target="SIEMENS2"#
        setBoolean(Options.OPTION_CLASSIC_CHAT, false);
        //#sijapp cond.end#

        setString(Options.OPTION_UIN2, emptyString);
        setString(Options.OPTION_PASSWORD2, emptyString);
        setString(Options.OPTION_UIN3, emptyString);
        setString(Options.OPTION_PASSWORD3, emptyString);
        setInt(Options.OPTIONS_CURR_ACCOUNT, 0);

        setBoolean(Options.OPTION_FULL_SCREEN, true);

        /* Offset (in hours) between GMT time and local zone time 
        GMT_time + GMT_offset = Local_time */
        setInt(Options.OPTIONS_GMT_OFFSET, 0);

        /* Offset (in hours) between GMT time and phone clock 
        Phone_clock + Local_offset = GMT_time */
        setInt(Options.OPTIONS_LOCAL_OFFSET, 0);

        setBoolean(OPTION_FLASH_BACKLIGHT, false);

        setBoolean(OPTIONS_LANG_CHANGED, false);
        //#sijapp cond.if target isnot "DEFAULT" #	
        selectSoundType("online.", OPTION_ONLINE_NOTIF_FILE);
        selectSoundType("message.", OPTION_MESS_NOTIF_FILE);
        selectSoundType("typing.", OPTION_TYPING_FILE);
        //#sijapp cond.end#

        setString(Options.OPTION_ANTISPAM_MSG, emptyString);
        setString(Options.OPTION_ANTISPAM_HELLO, emptyString);
        setString(Options.OPTION_ANTISPAM_ANSWER, emptyString);
        setBoolean(Options.OPTION_ANTISPAM_ENABLE, false);
        setBoolean(Options.OPTION_ANTISPAM_INVIS, false);

        setBoolean(OPTION_AUTOAWAY_ENABLE, true);
        setBoolean(OPTION_AUTONA_ENABLE, true);
        setBoolean(OPTION_AUTOONLINE, true);
        setInt(OPTION_AUTOSTATUS_DELAY, 5);

        setString(OPTION_THEME, "/default.tsf");
    }

    static public byte[] loadImage() {
        try {
            RecordStore account = RecordStore.openRecordStore("image", false);

            byte[] image = account.getRecord(1);

            return image;
        } catch (Exception e) {
            return null;
        }
    }

    static public void saveImage(byte[] image) {
        try {
            /* Open record store */
            RecordStore account = RecordStore.openRecordStore("image", true);

            /* Add empty records if necessary */
            while (account.getNumRecords() < 3) {
                account.addRecord(null, 0, 0);
            }
            account.setRecord(1, image, 0, image.length);
        } catch (Exception e) {
        }
    }

    /* Load option values from record store */
    static public void load() throws IOException, RecordStoreException {
        /* Open record store */
        RecordStore account = RecordStore.openRecordStore("options", false);

        /* Temporary variables */
        byte[] buf;
        ByteArrayInputStream bais;
        DataInputStream dis;

        /* Get version info from record store */
        buf = account.getRecord(1);
        bais = new ByteArrayInputStream(buf);
        dis = new DataInputStream(bais);
        Options.setDefaults();

        /* Read all option key-value pairs */
        buf = account.getRecord(2);
        bais = new ByteArrayInputStream(buf);
        dis = new DataInputStream(bais);
        while (dis.available() > 0) {
            int optionKey = dis.readUnsignedByte();
            if (optionKey < 64) /* 0-63 = String */ {
                setString(optionKey, dis.readUTF());
            } else if (optionKey < 128) /* 64-127 = int */ {
                setInt(optionKey, dis.readInt());
            } else if (optionKey < 192) /* 128-191 = boolean */ {
                setBoolean(optionKey, dis.readBoolean());
            } else if (optionKey < 224) /* 192-223 = long */ {
                setLong(optionKey, dis.readLong());
            } else /* 226-255 = Scrambled String */ {
                byte[] optionValue = new byte[dis.readUnsignedShort()];
                dis.readFully(optionValue);
                optionValue = Util.decipherPassword(optionValue);
                setString(optionKey, Util.byteArrayToString(optionValue, 0, optionValue.length, true));
            }
        }

        /* Close record store */
        account.closeRecordStore();

        /* Hide offline? */
        if (getBoolean(Options.OPTION_CL_HIDE_OFFLINE)) {
            setInt(Options.OPTION_CL_SORT_BY, 0);
        }

    }


    /* Save option values to record store */
    static public void save() throws IOException, RecordStoreException {

        /* Open record store */
        RecordStore account = RecordStore.openRecordStore("options", true);

        /* Add empty records if necessary */
        while (account.getNumRecords() < 3) {
            account.addRecord(null, 0, 0);
        }

        /* Temporary variables */
        byte[] buf;
        ByteArrayOutputStream baos;
        DataOutputStream dos;

        /* Add version info to record store */

        baos = new ByteArrayOutputStream();
        dos = new DataOutputStream(baos);
        dos.writeUTF(MIP.VERSION);
        buf = baos.toByteArray();
        account.setRecord(1, buf, 0, buf.length);

        /* Save all option key-value pairs */
        baos = new ByteArrayOutputStream();
        dos = new DataOutputStream(baos);
        Enumeration optionKeys = options.keys();
        while (optionKeys.hasMoreElements()) {
            int optionKey = ((Integer) optionKeys.nextElement()).intValue();
            dos.writeByte(optionKey);
            if (optionKey < 64) /* 0-63 = String */ {
                dos.writeUTF(getString(optionKey));
            } else if (optionKey < 128) /* 64-127 = int */ {
                dos.writeInt(getInt(optionKey));
            } else if (optionKey < 192) /* 128-191 = boolean */ {
                dos.writeBoolean(getBoolean(optionKey));
            } else if (optionKey < 224) /* 192-223 = long */ {
                dos.writeLong(getLong(optionKey));
            } else /* 226-255 = Scrambled String */ {
                byte[] optionValue = Util.stringToByteArray(getString(optionKey), true);
                optionValue = Util.decipherPassword(optionValue);
                dos.writeShort(optionValue.length);
                dos.write(optionValue);
            }
        }
        buf = baos.toByteArray();
        account.setRecord(2, buf, 0, buf.length);

        /* Close record store */
        account.closeRecordStore();
    }

    static public void loadThemeList() throws IOException {
        DataInputStream dis = new DataInputStream(new String().getClass().getResourceAsStream("/tlist.txt"));        
        boolean eof = false;
        byte chr = 0;
        themesList = new Vector();
        StringBuffer buf;
        buf = new StringBuffer();
        buf.setLength(0);
        System.out.println("loading themes list");
        for(;;) {
            try {
                if (!eof) {
                    chr = dis.readByte();
                }
                if ((chr == '\n') || eof) {
                    themesList.addElement(buf.toString().trim());
                    System.out.println(buf.toString());
                    buf.setLength(0);
                } else {
                    buf.append((char)chr);
                }
                if (eof) {
                    break;
                }
            } catch(EOFException eofExcept) {
                eof = true;
            }
        }
        dis.close();
    }
    
    static public void loadTheme(String theme, boolean woColors) throws IOException {
        System.out.println(theme);
        System.out.println(woColors);
        DataInputStream dis = new DataInputStream(new String().getClass().getResourceAsStream(theme));
        System.out.println("dis created");
        byte[] bar = new byte[8];
        dis.readFully(bar);
        System.out.println("checking signature");
        System.out.println(Util.byteArrayToString(bar));
        if (Util.byteArrayToString(bar).equals("PFGTSF-1")) {
            bar = new byte[24];
            boolean needApply = true;
            dis.readFully(bar);
            System.out.println("theme - " + Util.byteArrayToString(bar)); //theme name
            dis.skipBytes(16);
            while (dis.available() > 0) {
                int itemKey = dis.readUnsignedShort();
                System.out.println(itemKey);
                if (itemKey < 128) /* 0-127 = Byte arrays */ {
                    bar = new byte[dis.readInt()];
                    dis.readFully(bar);
                    switch (itemKey) {
                        case Options.THEME_PIC_CICONS:
                            StatusIcons.images3.removeAllElements();
                            StatusIcons.images3.load(new ByteArrayInputStream(bar), -1, -1, -1);
                            System.out.println("loaded cicons");
                            break;
                        case Options.THEME_PIC_ICONS:
                            StatusIcons.images.removeAllElements();
                            StatusIcons.images.load(new ByteArrayInputStream(bar), -1, -1, -1);
                            System.out.println("loaded icons");
                            break;
                        case Options.THEME_PIC_MICONS:
                            ContactList.menuIcons.removeAllElements();
                            ContactList.menuIcons.load(new ByteArrayInputStream(bar), -1, -1, -1);
                            System.out.println("loaded micons");
                            break;
                        case Options.THEME_PIC_PRLISTS:
                            ContactList.prIcons.removeAllElements();
                            ContactList.prIcons.load(new ByteArrayInputStream(bar), -1, -1, -1);
                            System.out.println("loaded pricons");
                            break;
                        case Options.THEME_PIC_PSTATUS:
                            StatusIcons.images4.removeAllElements();
                            StatusIcons.images4.load(new ByteArrayInputStream(bar), -1, -1, -1);
                            break;
                        case Options.THEME_PIC_XSTATUS:
                            StatusIcons.images2.removeAllElements();
                            StatusIcons.images2.load(new ByteArrayInputStream(bar), -1, -1, -1);
                            System.out.println("loaded xicons");
                            break;
                        case Options.THEME_MP3_MESSAGE:
                            break;
                        case Options.THEME_MP3_ONLINE:
                            break;
                        case Options.THEME_MP3_TYPING:
                            break;
                    }
                } else if (itemKey < 256) { /* 128-255 = boolean */
                    boolean boolBuf = dis.readBoolean();
                    switch (itemKey) {
                        case Options.THEME_ONLY_COLORS:
                            needApply = boolBuf;
                            break;
                    }
                } else if (itemKey < 383) { /* 128-255 = integer */
                    int intBuf = dis.readInt();
                    switch (itemKey) {
                        case Options.THEME_COLOR1:
                            if (!woColors) Options.setInt(Options.OPTION_COLOR1, intBuf);
                            break;
                        case Options.THEME_COLOR2:
                            if (!woColors) Options.setInt(Options.OPTION_COLOR2, intBuf);
                            break;
                        case Options.THEME_COLOR3:
                            if (!woColors) Options.setInt(Options.OPTION_COLOR3, intBuf);
                            break;
                        case Options.THEME_COLOR4:
                            if (!woColors) Options.setInt(Options.OPTION_COLOR4, intBuf);
                            break;
                        case Options.THEME_COLOR5:
                            if (!woColors) Options.setInt(Options.OPTION_COLOR5, intBuf);
                            break;
                        case Options.THEME_COLOR6:
                            if (!woColors) Options.setInt(Options.OPTION_COLOR6, intBuf);
                            break;
                        case Options.THEME_COLOR7:
                            if (!woColors) Options.setInt(Options.OPTION_COLOR7, intBuf);
                            break;
                        case Options.THEME_COLOR8:
                            if (!woColors) Options.setInt(Options.OPTION_COLOR8, intBuf);
                            break;
                        case Options.THEME_COLOR9:
                            if (!woColors) Options.setInt(Options.OPTION_COLOR9, intBuf);
                            break;
                        case Options.THEME_COLOR10:
                            if (!woColors) Options.setInt(Options.OPTION_COLOR10, intBuf);
                            break;
                    }
                }
            }
            if (needApply) {
                Options.setString(Options.OPTION_THEME, theme);
                System.out.println("theme applied");
            } else {
                System.out.println("not applied");
            }
                   
        }
        dis.close();
    }

    static public void safe_save() {
        try {
            save();
        } catch (Exception e) {
            mipException.handleException(new mipException(172, 0, true));
        }
    }

    /* Option retrieval methods (no type checking!) */
    static public synchronized String getString(int key) {
        switch (key) {
            case OPTION_UIN:
            case OPTION_PASSWORD:
                int index = getInt(Options.OPTIONS_CURR_ACCOUNT) * 2;
                return getString(accountKeys[key == OPTION_UIN ? index : index + 1]);
        }
        return ((String) options.get(new Integer(key)));
    }

    static public synchronized int getInt(int key) {
        return (((Integer) options.get(new Integer(key))).intValue());
    }

    static public synchronized boolean getBoolean(int key) {
        return (((Boolean) options.get(new Integer(key))).booleanValue());
    }

    static public synchronized long getLong(int key) {
        return (((Long) options.get(new Integer(key))).longValue());
    }


    /* Option setting methods (no type checking!) */
    static public synchronized void setString(int key, String value) {
        options.put(new Integer(key), value);
    }

    static public synchronized void setInt(int key, int value) {
        options.put(new Integer(key), new Integer(value));
    }

    static public synchronized void setBoolean(int key, boolean value) {
        options.put(new Integer(key), new Boolean(value));
    }

    static public synchronized void setLong(int key, long value) {
        options.put(new Integer(key), new Long(value));
    }
    /**************************************************************************/
    /* Constants for color scheme */
    private static final int CLRSCHHEME_BOW = 0; // black on white
    private static final int CLRSCHHEME_WOB = 1; // white on black
    private static final int CLRSCHHEME_WOBL = 2; // white on blue
    private static final int CLRSCHHEME_CUSTOM = 3; //yeaah! custom user color theme :)
    /* Constants for method getSchemeColor to retrieving color from color scheme */
    public static final int CLRSCHHEME_BACK = 1; // retrieving background color
    public static final int CLRSCHHEME_TEXT = 2; // retrieving text color
    public static final int CLRSCHHEME_BLUE = 3; // retrieving highlight color
    public static final int CLRSCHHEME_CURS = 4; // retrieving curr mess highlight color
    public static final int CLRSCHHEME_CAP = 5; // retrieving caption background color
    public static final int CLRSCHHEME_RED = 6;
    /* Constants for connection type */
    public static final int CONN_TYPE_SOCKET = 0;
    public static final int CONN_TYPE_HTTP = 1;
    public static final int CONN_TYPE_PROXY = 2;

    /*final static private int[] colors = 
    {
    0xFFFFFF, 0x000000, 0x0000FF, 0x404040, 0xF0F0F0, 0xFF0000,
    0x000000, 0xFFFFFF, 0x00FFFF, 0x808080, 0xA00000, 0xFF0000,
    0x000080, 0xFFFFFF, 0x00FFFF, 0xFFFFFF, 0xA00000, 0xFF0000
    };*/
    /* Retrieves color value from color scheme */
    static public int getSchemeColor(int type) {
        return getCustomColor(type);
    }

    static public int getCustomColor(int type) {
        if (type == 1) {
            return (getInt(OPTION_COLOR1)); //gradient 1
        } else if (type == 2) {
            return (getInt(OPTION_COLOR2)); //gradient 2
        } else if (type == 3) {
            return (getInt(OPTION_COLOR3)); //background
        } else if (type == 4) {
            return (getInt(OPTION_COLOR4)); //font
        } else if (type == 5) {
            return (getInt(OPTION_COLOR5)); //my nick - chat
        } else if (type == 6) {
            return (getInt(OPTION_COLOR6)); //other nick - chat
        } else if (type == 7) {
            return (getInt(OPTION_COLOR7)); //contacts with chat
        } else if (type == 8) {
            return (getInt(OPTION_COLOR8)); //temp contacts
        } else if (type == 9) {
            return (getInt(OPTION_COLOR9)); //font of caption & buttons
        } else if (type == 10) {
            return (getInt(OPTION_COLOR10)); //selector
        } else {
            return 0;
        }
    }

    static public void editOptions() {
        // Construct option form
        optionsForm = new OptionsForm();
    }
    //#sijapp cond.if target isnot "DEFAULT" #
    private static void selectSoundType(String name, int option) {
        boolean ok;

        /* Test existsing option */
        ok = ContactList.testSoundFile(getString(option));
        if (ok) {
            return;

        /* Test other extensions */
        }
        String[] exts = Util.explode("wav|mp3", '|');
        for (int i = 0; i < exts.length; i++) {
            String testFile = name + exts[i];
            ok = ContactList.testSoundFile(testFile);
            if (ok) {
                setString(option, testFile);
                break;
            }
        }
    }
    //#sijapp cond.end#
    static public void setCaptchaImage(Image img) {
        int width = 9 * SplashCanvas.getAreaWidth() / 10 - 2;
        img = Util.createThumbnail(img, width, 0);
        optionsForm.addCaptchaToForm(img);
        img = null;
    }

    static public void submitNewUinPassword(String uin, String password) {
        optionsForm.addAccount(uin, password);
    }
}

/**************************************************************************/
/**************************************************************************/
/* Form for editing option values */
class OptionsForm implements CommandListener, ItemStateListener {

    private boolean lastGroupsUsed,  lastHideOffline;
    private int lastSortMethod,  lastColorScheme;
    private int currentHour;
    private String lastUILang;
    static private TextBox cliVersion;

    /* Commands */
    private Command backCommand;
    private Command saveCommand;
    private Command selectCommand;
    /* Options menu */
    private List optionsMenu;

    public void addAccount(String uin, String password) {
        readAccontsControls();
        if (checkUin((String) uins.elementAt(currAccount)).equals("---")) {
            uins.setElementAt(uin, currAccount);
            passwords.setElementAt(password, currAccount);
        } else {
            uins.addElement(uin);
            passwords.addElement(password);
        }
        optionsForm.addCommand(saveCommand);
        clearForm();
        showAccountControls();
    }
    /* Menu event list */
    private int[] eventList;

    /* Options form */
    public Form optionsForm;    // Static constants for menu actios
    private static final int OPTIONS_ACCOUNT = 0;
    private static final int OPTIONS_NETWORK = 1;
    //#sijapp cond.if modules_PROXY is "true"#       
    private static final int OPTIONS_PROXY = 2;
    //#sijapp cond.end#
    private static final int OPTIONS_INTERFACE = 3;
    private static final int OPTIONS_HOTKEYS = 4;
    private static final int OPTIONS_SIGNALING = 5;
    //#sijapp cond.if modules_TRAFFIC is "true"#
    private static final int OPTIONS_TRAFFIC = 6;
    //#sijapp cond.end#
    private static final int OPTIONS_CLI_ID = 7;
    private static final int OPTIONS_TIMEZONE = 8;    // Exit has to be biggest element cause it also marks the size
    private static final int MENU_EXIT = 9;    // Options
    private TextField[] uinTextField;
    private TextField[] passwordTextField;
    private TextField smapeAcc;
    private TextField smapePass;
    private TextField srvHostTextField;
    private TextField srvPortTextField;
    private ChoiceGroup keepConnAliveChoiceGroup;
    private ChoiceGroup cliIDChoiceGroup;
    private ChoiceGroup BirthdayFlagChoiceGroup;
    private TextField Color1TextField;
    private TextField Color2TextField;
    private TextField Color5TextField;
    private ChoiceGroup sendMTN;
    private TextField connAliveIntervTextField;
    private ChoiceGroup connPropChoiceGroup;
    private ChoiceGroup connTypeChoiceGroup;
    private ChoiceGroup autoConnectChoiceGroup;
    private TextField reconnectNumberTextField;
    private ChoiceGroup uiLanguageChoiceGroup;
    private ChoiceGroup choiceInterfaceMisc;
    private ChoiceGroup clSortByChoiceGroup;
    private ChoiceGroup chrgChat;
    private ChoiceGroup chrgPopupWin;
    private ChoiceGroup vibratorChoiceGroup;
    private ChoiceGroup vibrSilentChoiceGroup;
    private ChoiceGroup choiceCurAccount;
    private ChoiceGroup chsTimeZone;
    private ChoiceGroup chsCurrTime;
    private TextField xtrazTitle;
    private TextField xtrazMessage;
    private TextField DCVersion;
    private TextField MyNick;
    private TextField Shift;    
    //#sijapp cond.if target isnot "DEFAULT"#
    private ChoiceGroup messageNotificationModeChoiceGroup;
    private ChoiceGroup onlineNotificationModeChoiceGroup;
    private ChoiceGroup typingNotificationModeChoiceGroup;
    //#sijapp cond.if target isnot "RIM"#
    private TextField messageNotificationSoundfileTextField;
    private Gauge messageNotificationSoundVolume;
    private ChoiceGroup chsBringUp;
    private TextField onlineNotificationSoundfileTextField;
    private TextField typingNotificationSoundfileTextField;
    private Gauge onlineNotificationSoundVolume;
    private Gauge typingNotificationSoundVolume;
    //#sijapp cond.end#
    //#sijapp cond.end#
    //#sijapp cond.if modules_TRAFFIC is "true" #
    private TextField costPerPacketTextField;
    private TextField costPerDayTextField;
    private TextField costPacketLengthTextField;
    private TextField currencyTextField;
    //#sijapp cond.end#
    private ChoiceGroup choiceContactList;
    private ChoiceGroup colorScheme;
    private TextField lightTimeout;
    private ChoiceGroup lightManual;
    private ChoiceGroup flashBkltChoiceGroup;
    //#sijapp cond.if modules_PROXY is "true"#
    private ChoiceGroup srvProxyType;
    private TextField srvProxyHostTextField;
    private TextField srvProxyPortTextField;
    private TextField srvProxyLoginTextField;
    private TextField srvProxyPassTextField;
    private TextField connAutoRetryTextField;
    //#sijapp cond.end#
    private TextField antispamMsgTextField;
    private TextField antispamAnswerTextField;
    private TextField antispamHelloTextField;
    private ChoiceGroup antispamEnableChoiceGroup;
    private ChoiceGroup autoStatusChoiceGroup;
    private TextField autoStatusTextField;
    private List keysMenu;
    private List actionMenu;
    final private String[] hotkeyActionNames = {
        "ext_hotkey_action_none",
        "info",
        "send_message",
        //#sijapp cond.if modules_HISTORY is "true"#
        "history",
        //#sijapp cond.end#
        "ext_hotkey_action_onoff",
        "options_lng",
        "menu",
        "keylock",
        //#sijapp cond.if target is "MIDP2"#
        "minimize",
        //#sijapp cond.end#,
        "dc_info",
        //#sijapp cond.if target isnot "DEFAULT" # 
        "#sound_off",    //#sijapp cond.end# 
    };
    final private int[] hotkeyActions = {
        Options.HOTKEY_NONE,
        Options.HOTKEY_INFO,
        Options.HOTKEY_NEWMSG,
        //#sijapp cond.if modules_HISTORY is "true"#
        Options.HOTKEY_HISTORY,
        //#sijapp cond.end#
        Options.HOTKEY_ONOFF,
        Options.HOTKEY_OPTIONS,
        Options.HOTKEY_MENU,
        Options.HOTKEY_LOCK,
        //#sijapp cond.if target is "MIDP2"#
        Options.HOTKEY_MINIMIZE,
        //#sijapp cond.end#
        Options.HOTKEY_CLI_INFO,
        //#sijapp cond.if target isnot "DEFAULT" # 
        Options.HOTKEY_SOUNDOFF,    //#sijapp cond.end# 
    };
    private int[] hotkeyOpts;
    // Constructor
    public OptionsForm() throws NullPointerException {
        // Initialize hotkeys
        hotkeyOpts = new int[10];
        keysMenu = new List(ResourceBundle.getString("ext_listhotkeys"), List.IMPLICIT);
        keysMenu.setCommandListener(this);
        actionMenu = new List(ResourceBundle.getString("ext_actionhotkeys"), List.EXCLUSIVE);
        actionMenu.setCommandListener(this);
        lastUILang = Options.getString(Options.OPTION_UI_LANGUAGE);
        lastHideOffline = Options.getBoolean(Options.OPTION_CL_HIDE_OFFLINE);
        lastGroupsUsed = Options.getBoolean(Options.OPTION_USER_GROUPS);
        lastSortMethod = Options.getInt(Options.OPTION_CL_SORT_BY);
        lastColorScheme = Options.getInt(Options.OPTION_COLOR_SCHEME);
        /*************************************************************************/
        // Initialize commands
        backCommand = new Command(ResourceBundle.getString("back"), Command.BACK, 2);
        saveCommand = new Command(ResourceBundle.getString("save"), Command.OK, 1);

        //selectCommand=new Command(ResourceBundle.getString("select"), Command.OK, 1);


        //optionsMenu = new List(ResourceBundle.getString("options_lng"), List.IMPLICIT);

        // optionsMenu.addCommand(backCommand);
        //optionsMenu.setCommandListener(this);            

        // Initialize options form
        optionsForm = new Form(ResourceBundle.getString("options_lng"));
        optionsForm.addCommand(saveCommand);
        optionsForm.addCommand(backCommand);
        optionsForm.setCommandListener(this);
        optionsForm.setItemStateListener(this);


    //System.out.println("OPTIONS_GMT_OFFSET="+Options.getInt(Options.OPTIONS_GMT_OFFSET));
    //System.out.println("OPTIONS_LOCAL_OFFSET="+Options.getInt(Options.OPTIONS_LOCAL_OFFSET));
    }

    private String getHotKeyActName(String langStr, int option) {
        int optionValue = Options.getInt(option);
        for (int i = 0; i < hotkeyActionNames.length; i++) {
            if (hotkeyActions[i] == optionValue) {
                return ResourceBundle.getString(langStr) + ": " + ResourceBundle.getString(hotkeyActionNames[i]);
            }
        }
        return ResourceBundle.getString(langStr) + ": <???>";
    }

    private void InitHotkeyMenuUI() {
        int optIdx = 0;

        int lastItemIndex = keysMenu.getSelectedIndex();
        while (keysMenu.size() != 0) {
            keysMenu.delete(0);
        }
        keysMenu.append(getHotKeyActName("ext_clhotkey0", Options.OPTION_EXT_CLKEY0), null);
        hotkeyOpts[optIdx++] = Options.OPTION_EXT_CLKEY0;

        keysMenu.append(getHotKeyActName("ext_clhotkey4", Options.OPTION_EXT_CLKEY4), null);
        hotkeyOpts[optIdx++] = Options.OPTION_EXT_CLKEY4;

        keysMenu.append(getHotKeyActName("ext_clhotkey6", Options.OPTION_EXT_CLKEY6), null);
        hotkeyOpts[optIdx++] = Options.OPTION_EXT_CLKEY6;

        //#sijapp cond.if target is "MOTOROLA"#
        keysMenu.append(getHotKeyActName("ext_clhotkeymenu", Options.OPTION_EXT_CLKEYMENU), null);
        hotkeyOpts[optIdx++] = Options.OPTION_EXT_CLKEYMENU;
        //#sijapp cond.end#		

        keysMenu.append(getHotKeyActName("ext_clhotkeypound", Options.OPTION_EXT_CLKEYPOUND), null);
        hotkeyOpts[optIdx++] = Options.OPTION_EXT_CLKEYPOUND;

        //#sijapp cond.if target is "SIEMENS2"#
        keysMenu.append(getHotKeyActName("ext_clhotkeycall", Options.OPTION_EXT_CLKEYCALL), null);
        hotkeyOpts[optIdx++] = Options.OPTION_EXT_CLKEYCALL;
        //#sijapp cond.end#

        keysMenu.setSelectedIndex(lastItemIndex == -1 ? 0 : lastItemIndex, true);

        keysMenu.addCommand(saveCommand);
        MIP.display.setCurrent(keysMenu);
    }    ///////////////////////////////////////////////////////////////////////////
    // Accounts
    private Command cmdAddNewAccount = new Command(ResourceBundle.getString("add_new"), Command.ITEM, 3);
    private Command cmdDeleteAccount = new Command(ResourceBundle.getString("delete", ResourceBundle.FLAG_ELLIPSIS), Command.ITEM, 4);
    private int currAccount;
    private Vector uins = new Vector();
    private Vector passwords = new Vector();
    private String smynick;
    private String smapeAccount;
    private String smapePassword;
    private int maxAccountsCount = Options.accountKeys.length / 2;

    public void readAccontsData() {
        uins.removeAllElements();
        passwords.removeAllElements();
        for (int i = 0; i < maxAccountsCount; i++) {
            int index = i * 2;
            String uin = Options.getString(Options.accountKeys[index]);
            if ((i != 0) && (uin.length() == 0)) {
                continue;
            }
            uins.addElement(uin);
            passwords.addElement(Options.getString(Options.accountKeys[index + 1]));
        }
        currAccount = Options.getInt(Options.OPTIONS_CURR_ACCOUNT);
        smynick = Options.getString(Options.OPTION_NICKNAME);
        smapeAccount = Options.getString(Options.OPTION_SMAPEACC);
        smapePassword = Options.getString(Options.OPTION_SMAPEPASS);
    }

    private String checkUin(String value) {
        if ((value == null) || (value.length() == 0)) {
            return "---";
        }
        return value;
    }

    public void showAccountControls() {
        int size = uins.size();

        if (size != 1) {
            if (choiceCurAccount == null) {
                choiceCurAccount = new ChoiceGroup(ResourceBundle.getString("options_account"), Choice.EXCLUSIVE);
            //#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
            }
            choiceCurAccount.deleteAll();
            //#sijapp cond.else#
            while (choiceCurAccount.size() > 0) {
                choiceCurAccount.delete(0);
            }
            //#sijapp cond.end#

            for (int i = 0; i < size; i++) {
                choiceCurAccount.append(checkUin((String) uins.elementAt(i)), null);
            }
            optionsForm.append(choiceCurAccount);
            if (currAccount >= size) {
                currAccount = size - 1;
            }
            choiceCurAccount.setSelectedIndex(currAccount, true);
        }

        MyNick = new TextField(ResourceBundle.getString("my_nick"), smynick, 20, TextField.ANY);

        smapeAcc = new TextField(ResourceBundle.getString("smapeacc"), smapeAccount, 20, TextField.ANY);
        smapePass = new TextField(ResourceBundle.getString("password"), smapePassword, 20, TextField.PASSWORD);

        uinTextField = new TextField[size];
        passwordTextField = new TextField[size];
        for (int i = 0; i < size; i++) {
            if (size > 1) {
                optionsForm.append("---");
            }
            String add = (size == 1) ? "" : "-" + (i + 1);

            TextField uinFld = new TextField(ResourceBundle.getString("uin") + add, (String) uins.elementAt(i), 12, TextField.NUMERIC);
            TextField passFld = new TextField(ResourceBundle.getString("password") + add, (String) passwords.elementAt(i), 32, TextField.PASSWORD);

            optionsForm.append(uinFld);
            optionsForm.append(passFld);

            uinTextField[i] = uinFld;
            passwordTextField[i] = passFld;
        }
        optionsForm.append(MyNick);
        optionsForm.append(smapeAcc);
        optionsForm.append(smapePass);

        if (size != maxAccountsCount) {
            optionsForm.addCommand(cmdAddNewAccount);
        }
        if (size != 1) {
            optionsForm.addCommand(cmdDeleteAccount);
        }
        if (!Icq.isConnected()) {
            optionsForm.addCommand(cmdRegisterAccount);
        }
    }

    private void setAccountOptions() {
        int size = uins.size();
        String uin, pass;

        for (int i = 0; i < maxAccountsCount; i++) {
            if (i < size) {
                uin = (String) uins.elementAt(i);
                pass = (String) passwords.elementAt(i);
            } else {
                uin = pass = Options.emptyString;
            }
            Options.setString(Options.accountKeys[2 * i], uin);
            Options.setString(Options.accountKeys[2 * i + 1], pass);
        }

        if (currAccount >= size) {
            currAccount = size - 1;
        }
        Options.setInt(Options.OPTIONS_CURR_ACCOUNT, currAccount);
        Options.setString(Options.OPTION_NICKNAME, MyNick.getString());
        Options.setString(Options.OPTION_SMAPEACC, smapeAcc.getString());
        Options.setString(Options.OPTION_SMAPEPASS, smapePass.getString());
    }

    private void readAccontsControls() {
        uins.removeAllElements();
        passwords.removeAllElements();
        smynick = MyNick.getString();
        for (int i = 0; i < uinTextField.length; i++) {
            uins.addElement(uinTextField[i].getString());
            passwords.addElement(passwordTextField[i].getString());
        }

        currAccount = (choiceCurAccount == null) ? 0 : choiceCurAccount.getSelectedIndex();
    }    //UIN REG
    private TextField captchaCode;
    private TextField newPassword;
    private boolean registration_connected = false;
    private Command cmdRegisterAccount = new Command(ResourceBundle.getString(
            "register_new"), Command.ITEM, 3);
    private Command cmdRequestCaptchaImage = new Command(ResourceBundle.getString(
            "register_request_image"), Command.ITEM, 3);
    private Command cmdRequestRegistration = new Command(ResourceBundle.getString(
            "register_request_send"), Command.ITEM, 3);

    public void addCaptchaToForm(Image img) {
        clearForm();
        optionsForm.append(img);
        optionsForm.append(captchaCode);
        optionsForm.append(ResourceBundle.getString("register_notice"));
        optionsForm.addCommand(cmdRequestRegistration);
        registration_connected = true;
    }

    private void showRegisterControls() {
        newPassword = new TextField(ResourceBundle.getString("password"), "", 8, TextField.PASSWORD);
        captchaCode = new TextField(ResourceBundle.getString("captcha"), "", 8, TextField.ANY);
        optionsForm.removeCommand(saveCommand);
        optionsForm.append(newPassword);
        if (!Icq.isConnected()) {
            registration_connected = false;
            optionsForm.addCommand(cmdRequestCaptchaImage);
        }
    }

    public void itemStateChanged(Item item) {
        if (uinTextField != null) {
            int accCount = uinTextField.length;
            if (accCount != 1) {
                for (int i = 0; i < accCount; i++) {
                    if (uinTextField[i] != item) {
                        continue;
                    }
                    choiceCurAccount.set(i, checkUin(uinTextField[i].getString()), null);
                    return;
                }
            }
        }
    }

    public void showOptionPart(int ID) {
        clearForm();
        switch (ID) {
            case 0:
                readAccontsData();
                showAccountControls();
                break;

            case 1:
                // Initialize elements (network section)
                srvHostTextField = new TextField(ResourceBundle.getString("server_host"), Options.getString(Options.OPTION_SRV_HOST), 32, TextField.ANY);
                srvPortTextField = new TextField(ResourceBundle.getString("server_port"), Options.getString(Options.OPTION_SRV_PORT), 5, TextField.NUMERIC);

                connTypeChoiceGroup = new ChoiceGroup(ResourceBundle.getString("conn_type"), Choice.EXCLUSIVE);
                connTypeChoiceGroup.append(ResourceBundle.getString("socket"), null);
                connTypeChoiceGroup.append(ResourceBundle.getString("http_conn"), null);
                //#sijapp cond.if modules_PROXY is "true"#
                connTypeChoiceGroup.append(ResourceBundle.getString("proxy"), null);
                connTypeChoiceGroup.setSelectedIndex(Options.getInt(Options.OPTION_CONN_TYPE), true);
                //#sijapp cond.else#
                connTypeChoiceGroup.setSelectedIndex(Options.getInt(Options.OPTION_CONN_TYPE) % 2, true);
                //#sijapp cond.end#
                connPropChoiceGroup = new ChoiceGroup(ResourceBundle.getString("conn_prop"), Choice.MULTIPLE);
                connPropChoiceGroup.append(ResourceBundle.getString("md5_login"), null);
                connPropChoiceGroup.append(ResourceBundle.getString("async"), null);
                connPropChoiceGroup.append(ResourceBundle.getString("reconnect"), null);
                connPropChoiceGroup.append(ResourceBundle.getString("multserv"), null);
                //#sijapp cond.if target isnot "MOTOROLA"#
                connPropChoiceGroup.append(ResourceBundle.getString("shadow_con"), null);
                //#sijapp cond.end#
                connPropChoiceGroup.append(ResourceBundle.getString("loadcl"), null);
                connPropChoiceGroup.setSelectedIndex(0, Options.getBoolean(Options.OPTION_MD5_LOGIN));
                if (Options.getInt(Options.OPTION_CONN_PROP) == 0) {
                    connPropChoiceGroup.setSelectedIndex(1, false);
                } else {
                    connPropChoiceGroup.setSelectedIndex(1, true);
                }
                connPropChoiceGroup.setSelectedIndex(2, Options.getBoolean(Options.OPTION_RECONNECT));
                connPropChoiceGroup.setSelectedIndex(3, Options.getBoolean(Options.OPTION_MULTIPLESERVERS));
                //#sijapp cond.if target isnot "MOTOROLA"#
                connPropChoiceGroup.setSelectedIndex(4, Options.getBoolean(Options.OPTION_SHADOW_CON));
                connPropChoiceGroup.setSelectedIndex(5, Options.getBoolean(Options.OPTION_LOADCL));
                //#sijapp cond.else#
                connPropChoiceGroup.setSelectedIndex(4, Options.getBoolean(Options.OPTION_LOADCL));
                //#sijapp cond.end#
                autoConnectChoiceGroup = new ChoiceGroup(ResourceBundle.getString("auto_connect") + "?", Choice.MULTIPLE);
                autoConnectChoiceGroup.append(ResourceBundle.getString("yes"), null);
                autoConnectChoiceGroup.setSelectedIndex(0, Options.getBoolean(Options.OPTION_AUTO_CONNECT));
                reconnectNumberTextField = new TextField(ResourceBundle.getString("reconnect_number"), String.valueOf(Options.getInt(Options.OPTION_RECONNECT_NUMBER)), 2, TextField.NUMERIC);
                connAliveIntervTextField = new TextField(ResourceBundle.getString("timeout_interv"), Options.getString(Options.OPTION_CONN_ALIVE_INVTERV), 4, TextField.NUMERIC);
                optionsForm.append(srvHostTextField);
                optionsForm.append(srvPortTextField);
                optionsForm.append(connTypeChoiceGroup);
                optionsForm.append(autoConnectChoiceGroup);
                optionsForm.append(connPropChoiceGroup);
                optionsForm.append(connAliveIntervTextField);
                optionsForm.append(reconnectNumberTextField);
                break;

            case 2:
                if (ResourceBundle.langAvailable.length > 1) {
                    uiLanguageChoiceGroup = new ChoiceGroup(ResourceBundle.getString("language"), Choice.EXCLUSIVE);
                    for (int j = 0; j < ResourceBundle.langAvailable.length; j++) {
                        uiLanguageChoiceGroup.append(ResourceBundle.getString("lang_" + ResourceBundle.langAvailable[j]), null);
                        if (ResourceBundle.langAvailable[j].equals(Options.getString(Options.OPTION_UI_LANGUAGE))) {
                            uiLanguageChoiceGroup.setSelectedIndex(j, true);
                        }
                    }
                }

                int idx1 = 0;

                choiceInterfaceMisc = new ChoiceGroup(ResourceBundle.getString("misc"), Choice.MULTIPLE);

                choiceInterfaceMisc.append(ResourceBundle.getString("display_date"), null);
                choiceInterfaceMisc.setSelectedIndex(idx1++, Options.getBoolean(Options.OPTION_DISPLAY_DATE));


                /*
                choiceInterfaceMisc.append(ResourceBundle.getString("full_screen"), null);
                choiceInterfaceMisc.setSelectedIndex(idx1++, Options.getBoolean(Options.OPTION_FULL_SCREEN));
                 */
                clSortByChoiceGroup = new ChoiceGroup(ResourceBundle.getString("sort_by"), Choice.EXCLUSIVE);
                clSortByChoiceGroup.append(ResourceBundle.getString("sort_by_status"), null);
                clSortByChoiceGroup.append(ResourceBundle.getString("sort_by_name"), null);
                clSortByChoiceGroup.setSelectedIndex(Options.getInt(Options.OPTION_CL_SORT_BY), true);

                choiceContactList = new ChoiceGroup(ResourceBundle.getString("contact_list"), Choice.MULTIPLE);
                choiceContactList.append(ResourceBundle.getString("show_user_groups"), null);
                choiceContactList.append(ResourceBundle.getString("hide_offline"), null);
                choiceContactList.setSelectedIndex(0, Options.getBoolean(Options.OPTION_USER_GROUPS));
                choiceContactList.setSelectedIndex(1, Options.getBoolean(Options.OPTION_CL_HIDE_OFFLINE));
                choiceContactList.append(ResourceBundle.getString("bpicture"), null);
                choiceContactList.setSelectedIndex(2, Options.getBoolean(Options.OPTION_BACKGROUND));
                choiceContactList.append(ResourceBundle.getString("swapsofts"), null);
                choiceContactList.setSelectedIndex(3, Options.getBoolean(Options.OPTION_SWAPSOFTS));
                choiceContactList.append(ResourceBundle.getString("capshift"), null);
                choiceContactList.setSelectedIndex(4, Options.getBoolean(Options.OPTION_HASSHIFT));
                choiceContactList.append(ResourceBundle.getString("showmenuicons"), null);
                choiceContactList.setSelectedIndex(5, Options.getBoolean(Options.OPTION_SHOWMENUICONS));
                choiceContactList.append(ResourceBundle.getString("updateseconds"), null);
                choiceContactList.setSelectedIndex(6, Options.getBoolean(Options.OPTION_SECONDS));
                Shift = new TextField(ResourceBundle.getString("txtshift"), String.valueOf(Options.getInt(Options.OPTION_SHIFT)), 2, TextField.NUMERIC);

                idx1 = 0;
                chrgChat = new ChoiceGroup(ResourceBundle.getString("chat"), Choice.MULTIPLE);
                chrgChat.append(ResourceBundle.getString("chat_small_font"), null);
                chrgChat.setSelectedIndex(idx1++, Options.getBoolean(Options.OPTION_CHAT_SMALL_FONT));

                //#sijapp cond.if modules_SMILES is "true"#
                chrgChat.append(ResourceBundle.getString("use_smiles"), null);
                chrgChat.setSelectedIndex(idx1++, Options.getBoolean(Options.OPTION_USE_SMILES));
                chrgChat.append(ResourceBundle.getString("animsmiles"), null);
                chrgChat.setSelectedIndex(idx1++, Options.getBoolean(Options.OPTION_ANIMSMILES));
                //#sijapp cond.end#

                //#sijapp cond.if modules_HISTORY is "true"#
                chrgChat.append(ResourceBundle.getString("use_history"), null);
                chrgChat.setSelectedIndex(idx1++, Options.getBoolean(Options.OPTION_HISTORY));
                chrgChat.append(ResourceBundle.getString("show_prev_mess"), null);
                chrgChat.setSelectedIndex(idx1++, Options.getBoolean(Options.OPTION_SHOW_LAST_MESS));
                //#sijapp cond.end#

                //#sijapp cond.if target is "SIEMENS2"#
                chrgChat.append(ResourceBundle.getString("cl_chat"), null);
                chrgChat.setSelectedIndex(idx1++, Options.getBoolean(Options.OPTION_CLASSIC_CHAT));
                //#sijapp cond.end#

                chrgChat.append(ResourceBundle.getString("cp1251"), null);
                chrgChat.setSelectedIndex(idx1++, Options.getBoolean(Options.OPTION_CP1251_HACK));


                //	lightTimeout = new TextField(ResourceBundle.getString("backlight_timeout"), String.valueOf(Options.getInt(Options.OPTION_LIGHT_TIMEOUT)), 2, TextField.NUMERIC);
                //		lightManual = new ChoiceGroup(ResourceBundle.getString("backlight_manual"), Choice.MULTIPLE);
                //		lightManual.append(ResourceBundle.getString("yes"), null);
                //		lightManual.setSelectedIndex(0, Options.getBoolean(Options.OPTION_LIGHT_MANUAL));
                if (uiLanguageChoiceGroup != null) {
                    optionsForm.append(uiLanguageChoiceGroup);
                }
                optionsForm.append(choiceInterfaceMisc);
                optionsForm.append(choiceContactList);
                optionsForm.append(Shift);
                optionsForm.append(clSortByChoiceGroup);

                optionsForm.append(chrgChat);
                //		optionsForm.append(lightTimeout);
                //		optionsForm.append(lightManual);
					/*
                optionsForm.append(colorScheme);
                optionsForm.append(Color1TextField);
                optionsForm.append(Color2TextField);
                optionsForm.append(Color5TextField);
                 */
                break;

            case 3:
                InitHotkeyMenuUI();
                return;

            case 4:
                /* Initialize elements (cost section) */
                costPerPacketTextField = new TextField(ResourceBundle.getString("cpp"), Util.intToDecimal(Options.getInt(Options.OPTION_COST_PER_PACKET)), 6, TextField.ANY);
                costPerDayTextField = new TextField(ResourceBundle.getString("cpd"), Util.intToDecimal(Options.getInt(Options.OPTION_COST_PER_DAY)), 6, TextField.ANY);
                costPacketLengthTextField = new TextField(ResourceBundle.getString("plength"), String.valueOf(Options.getInt(Options.OPTION_COST_PACKET_LENGTH) / 1024), 4, TextField.NUMERIC);
                currencyTextField = new TextField(ResourceBundle.getString("currency"), Options.getString(Options.OPTION_CURRENCY), 4, TextField.ANY);

                optionsForm.append(costPerPacketTextField);
                optionsForm.append(costPerDayTextField);
                optionsForm.append(costPacketLengthTextField);
                optionsForm.append(currencyTextField);
                break;

            case 5:
                // Initialize elements (Signaling section)

                //#sijapp cond.if target isnot "DEFAULT"#
                onlineNotificationModeChoiceGroup = new ChoiceGroup(ResourceBundle.getString("onl_notification"), Choice.EXCLUSIVE);
                onlineNotificationModeChoiceGroup.append(ResourceBundle.getString("no"), null);
                onlineNotificationModeChoiceGroup.append(ResourceBundle.getString("beep"), null);
                //#sijapp cond.if target isnot "RIM"#        
                onlineNotificationModeChoiceGroup.append(ResourceBundle.getString("sound"), null);
                //#sijapp cond.end#                  
                onlineNotificationModeChoiceGroup.setSelectedIndex(Options.getInt(Options.OPTION_ONLINE_NOTIF_MODE), true);
                //#sijapp cond.if target isnot "RIM"#                 
                onlineNotificationSoundfileTextField = new TextField(ResourceBundle.getString("onl_sound_file_name"), Options.getString(Options.OPTION_ONLINE_NOTIF_FILE), 32, TextField.ANY);
                //#sijapp cond.end#                 
                messageNotificationModeChoiceGroup = new ChoiceGroup(ResourceBundle.getString("message_notification"), Choice.EXCLUSIVE);
                messageNotificationModeChoiceGroup.append(ResourceBundle.getString("no"), null);
                messageNotificationModeChoiceGroup.append(ResourceBundle.getString("beep"), null);
                //#sijapp cond.if target isnot "RIM"#                 
                messageNotificationModeChoiceGroup.append(ResourceBundle.getString("sound"), null);
                //#sijapp cond.end#                  
                messageNotificationModeChoiceGroup.setSelectedIndex(Options.getInt(Options.OPTION_MESS_NOTIF_MODE), true);
                //#sijapp cond.if target isnot "RIM"#                  
                messageNotificationSoundfileTextField = new TextField(ResourceBundle.getString("msg_sound_file_name"), Options.getString(Options.OPTION_MESS_NOTIF_FILE), 32, TextField.ANY);
                messageNotificationSoundVolume = new Gauge(ResourceBundle.getString("volume"), true, 10, Options.getInt(Options.OPTION_MESS_NOTIF_VOL) / 10);
                onlineNotificationSoundVolume = new Gauge(ResourceBundle.getString("volume"), true, 10, Options.getInt(Options.OPTION_ONLINE_NOTIF_VOL) / 10);
                typingNotificationSoundVolume = new Gauge(ResourceBundle.getString("volume"), true, 10, Options.getInt(Options.OPTION_TYPING_VOL) / 10);
                typingNotificationSoundfileTextField = new TextField(ResourceBundle.getString("msg_sound_file_name"), Options.getString(Options.OPTION_TYPING_FILE), 32, TextField.ANY);
                typingNotificationModeChoiceGroup = new ChoiceGroup(ResourceBundle.getString("typing_notify"), Choice.EXCLUSIVE);
                typingNotificationModeChoiceGroup.append(ResourceBundle.getString("no"), null);
                typingNotificationModeChoiceGroup.append(ResourceBundle.getString("typing_display_only"), null);
                typingNotificationModeChoiceGroup.append(ResourceBundle.getString("beep"), null);
                typingNotificationModeChoiceGroup.append(ResourceBundle.getString("sound"), null);
                typingNotificationModeChoiceGroup.setSelectedIndex(Options.getInt(Options.OPTION_TYPING_MODE), true);

                if (Options.getInt(Options.OPTION_TYPING_MODE) != 0) {
                    sendMTN = new ChoiceGroup(ResourceBundle.getString("sendmtn"), Choice.MULTIPLE);
                    sendMTN.append(ResourceBundle.getString("yes"), null);
                    if (Options.getBoolean(Options.OPTION_SENDMTN)) {
                        sendMTN.setSelectedIndex(0, true);
                    }
                }
                //#sijapp cond.end#

                vibratorChoiceGroup = new ChoiceGroup(ResourceBundle.getString("vibration"), Choice.EXCLUSIVE);
                vibratorChoiceGroup.append(ResourceBundle.getString("no"), null);
                vibratorChoiceGroup.append(ResourceBundle.getString("yes"), null);
                vibratorChoiceGroup.append(ResourceBundle.getString("when_locked"), null);
                vibratorChoiceGroup.setSelectedIndex(Options.getInt(Options.OPTION_VIBRATOR), true);
                vibrSilentChoiceGroup = new ChoiceGroup(ResourceBundle.getString("vibr_silent"), Choice.MULTIPLE);
                vibrSilentChoiceGroup.append(ResourceBundle.getString("yes"), null);
                vibrSilentChoiceGroup.setSelectedIndex(0, Options.getBoolean(Options.OPTION_VIBR_SILENT));

                //#sijapp cond.if target="MOTOROLA"#
                flashBkltChoiceGroup = new ChoiceGroup(ResourceBundle.getString("flash_backlight"), Choice.MULTIPLE);
                flashBkltChoiceGroup.append(ResourceBundle.getString("yes"), null);
                flashBkltChoiceGroup.setSelectedIndex(0, Options.getBoolean(Options.OPTION_FLASH_BACKLIGHT));
                //#sijapp cond.end#
                //#sijapp cond.end#



                chrgPopupWin = new ChoiceGroup(ResourceBundle.getString("popup_win"), Choice.EXCLUSIVE);
                chrgPopupWin.append(ResourceBundle.getString("no"), null);
                chrgPopupWin.append(ResourceBundle.getString("pw_forme"), null);
                chrgPopupWin.append(ResourceBundle.getString("pw_all"), null);
                chrgPopupWin.setSelectedIndex(Options.getInt(Options.OPTION_POPUP_WIN2), true);

                //#sijapp cond.if target isnot "DEFAULT"#     
                optionsForm.append(messageNotificationModeChoiceGroup);

                //#sijapp cond.if target isnot "RIM"#                        
                optionsForm.append(messageNotificationSoundVolume);
                optionsForm.append(messageNotificationSoundfileTextField);
                //#sijapp cond.end#

                optionsForm.append(vibratorChoiceGroup);
                optionsForm.append(vibrSilentChoiceGroup);
                //#sijapp cond.if target="MOTOROLA"#
//                if (MIP.funlight_device_type != -1) {
//                    optionsForm.append(flashBkltChoiceGroup);
//                }
                //#sijapp cond.end#
                optionsForm.append(onlineNotificationModeChoiceGroup);

                //#sijapp cond.if target isnot "RIM"#                          
                optionsForm.append(onlineNotificationSoundVolume);
                optionsForm.append(onlineNotificationSoundfileTextField);
                optionsForm.append(typingNotificationModeChoiceGroup);
                if (Options.getInt(Options.OPTION_TYPING_MODE) != 0) {
                    optionsForm.append(sendMTN);
                }
                optionsForm.append(typingNotificationSoundVolume);
                optionsForm.append(typingNotificationSoundfileTextField);
                //#sijapp cond.end#

                //#sijapp cond.end#
                optionsForm.append(chrgPopupWin);
                //#sijapp cond.if target="MIDP2"#
                chsBringUp = new ChoiceGroup(ResourceBundle.getString("bring_up"), Choice.MULTIPLE);
                chsBringUp.append(ResourceBundle.getString("yes"), null);
                chsBringUp.setSelectedIndex(0, Options.getBoolean(Options.OPTION_BRING_UP));
                optionsForm.append(chsBringUp);
                //#sijapp cond.end#
                break;

            case 6:
                int choiceType;

                //#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
                choiceType = Choice.POPUP;
                //#sijapp cond.else#
                choiceType = Choice.EXCLUSIVE;
                //#sijapp cond.end#

                chsTimeZone = new ChoiceGroup(ResourceBundle.getString("time_zone"), choiceType);
                for (int i = -12; i <= 13; i++) {
                    chsTimeZone.append("GMT" + (i < 0 ? "" : "+") + i + ":00", null);
                }
                chsTimeZone.setSelectedIndex(Options.getInt(Options.OPTIONS_GMT_OFFSET) + 12, true);

                int[] currDateTime = Util.createDate(Util.createCurrentDate(false));
                chsCurrTime = new ChoiceGroup(ResourceBundle.getString("local_time"), choiceType);
                int minutes = currDateTime[Util.TIME_MINUTE];
                int hour = currDateTime[Util.TIME_HOUR];
                for (int i = 0; i < 24; i++) {
                    chsCurrTime.append(i + ":" + minutes, null);
                }
                chsCurrTime.setSelectedIndex(hour, true);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());	                                         /* Store current hour (GMT) to calculate diff. later */
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                optionsForm.append(chsTimeZone);
                optionsForm.append(chsCurrTime);
                break;

            case 7:
                DCVersion = new TextField(ResourceBundle.getString("dcver"), String.valueOf(Options.getInt(Options.OPTION_DCVERSION)), 5, TextField.NUMERIC);

                BirthdayFlagChoiceGroup = new ChoiceGroup(ResourceBundle.getString("optoth"), Choice.MULTIPLE);
                BirthdayFlagChoiceGroup.append(ResourceBundle.getString("bflag"), null);
                BirthdayFlagChoiceGroup.append(ResourceBundle.getString("webaware"), null);
                BirthdayFlagChoiceGroup.append(ResourceBundle.getString("showxtraz"), null);
                BirthdayFlagChoiceGroup.append(ResourceBundle.getString("utf8in"), null);
                BirthdayFlagChoiceGroup.append(ResourceBundle.getString("utf8out"), null);

                optionsForm.append(DCVersion);
                optionsForm.append(BirthdayFlagChoiceGroup);

                boolean[] selectedFlags = {false, false, false, false, false};

                if (Options.getInt(Options.OPTION_BFLAG) == 1) {
                    selectedFlags[0] = true;
                }
                if (Options.getBoolean(Options.OPTION_WEBAWARE)) {
                    selectedFlags[1] = true;
                }
                if (Options.getBoolean(Options.OPTION_SHOWXTRAZMSG)) {
                    selectedFlags[2] = true;
                }
                if (Options.getBoolean(Options.OPTION_UTF8IN)) {
                    selectedFlags[3] = true;
                }
                if (Options.getBoolean(Options.OPTION_UTF8OUT)) {
                    selectedFlags[4] = true;
                }
                BirthdayFlagChoiceGroup.setSelectedFlags(selectedFlags);
                break;

            case 8:
                srvProxyType = new ChoiceGroup(ResourceBundle.getString("proxy_type"), Choice.EXCLUSIVE);
                srvProxyType.append(ResourceBundle.getString("proxy_socks4"), null);
                srvProxyType.append(ResourceBundle.getString("proxy_socks5"), null);
                srvProxyType.append(ResourceBundle.getString("proxy_guess"), null);
                // srvProxyType.append(ResourceBundle.getString("http"), null);
                srvProxyType.setSelectedIndex(Options.getInt(Options.OPTION_PRX_TYPE), true);

                srvProxyHostTextField = new TextField(ResourceBundle.getString("proxy_server_host"), Options.getString(Options.OPTION_PRX_SERV), 32, TextField.ANY);
                srvProxyPortTextField = new TextField(ResourceBundle.getString("proxy_server_port"), Options.getString(Options.OPTION_PRX_PORT), 5, TextField.NUMERIC);

                srvProxyLoginTextField = new TextField(ResourceBundle.getString("proxy_server_login"), Options.getString(Options.OPTION_PRX_NAME), 32, TextField.ANY);
                srvProxyPassTextField = new TextField(ResourceBundle.getString("proxy_server_pass"), Options.getString(Options.OPTION_PRX_PASS), 32, TextField.PASSWORD);

                connAutoRetryTextField = new TextField(ResourceBundle.getString("auto_retry_count"), Options.getString(Options.OPTION_AUTORETRY_COUNT), 5, TextField.NUMERIC);

                optionsForm.append(srvProxyType);
                optionsForm.append(srvProxyHostTextField);
                optionsForm.append(srvProxyPortTextField);
                optionsForm.append(srvProxyLoginTextField);
                optionsForm.append(srvProxyPassTextField);
                optionsForm.append(connAutoRetryTextField);
                break;
            case 9:
                antispamMsgTextField = new TextField(ResourceBundle.getString("antispam_msg"), Options.getString(Options.OPTION_ANTISPAM_MSG), 255, TextField.ANY);
                antispamAnswerTextField = new TextField(ResourceBundle.getString("antispam_answer"), Options.getString(Options.OPTION_ANTISPAM_ANSWER), 255, TextField.ANY);
                antispamHelloTextField = new TextField(ResourceBundle.getString("antispam_hello"), Options.getString(Options.OPTION_ANTISPAM_HELLO), 255, TextField.ANY);

                antispamEnableChoiceGroup = new ChoiceGroup(null, Choice.MULTIPLE);
                antispamEnableChoiceGroup.append(ResourceBundle.getString("antispam_enable"), null);
                antispamEnableChoiceGroup.append(ResourceBundle.getString("antispam_invis"), null);
                antispamEnableChoiceGroup.setSelectedIndex(0, Options.getBoolean(Options.OPTION_ANTISPAM_ENABLE));
                antispamEnableChoiceGroup.setSelectedIndex(1, Options.getBoolean(Options.OPTION_ANTISPAM_INVIS));

                optionsForm.append(antispamMsgTextField);
                optionsForm.append(antispamAnswerTextField);
                optionsForm.append(antispamHelloTextField);
                optionsForm.append(antispamEnableChoiceGroup);
                break;

            case 10: //Autostatus
                autoStatusTextField = new TextField(ResourceBundle.getString("auto_delay"), String.valueOf(Options.getInt(Options.OPTION_AUTOSTATUS_DELAY)), 3, TextField.NUMERIC);

                autoStatusChoiceGroup = new ChoiceGroup(null, Choice.MULTIPLE);
//                    autoAwayChoiceGroup.setSelectedFlags()

                autoStatusChoiceGroup.append(ResourceBundle.getString("auto_away"), null);
                autoStatusChoiceGroup.append(ResourceBundle.getString("auto_na"), null);
                autoStatusChoiceGroup.append(ResourceBundle.getString("auto_online"), null);
                autoStatusChoiceGroup.setSelectedIndex(0, Options.getBoolean(Options.OPTION_AUTOAWAY_ENABLE));
                autoStatusChoiceGroup.setSelectedIndex(1, Options.getBoolean(Options.OPTION_AUTONA_ENABLE));
                autoStatusChoiceGroup.setSelectedIndex(2, Options.getBoolean(Options.OPTION_AUTOONLINE));

                optionsForm.append(autoStatusChoiceGroup);
                optionsForm.append(autoStatusTextField);

                break;
        }
        MIP.display.setCurrent(Options.optionsForm.optionsForm);
    }
    final private static int TAG_DELETE_ACCOUNT = 1;

    /* Command listener */
    public void commandAction(Command c, Displayable d) {
        /* Command handler for hotkeys list in Options... */
        if (c == cmdRegisterAccount) {
            clearForm();
            showRegisterControls();
            return;
        } else if (c == cmdRequestCaptchaImage) {
            optionsForm.append(ResourceBundle.getString("wait"));
            Icq.connect(newPassword.getString());
            return;
        } else if (c == cmdRequestRegistration) {
            try {
                optionsForm.append(ResourceBundle.getString("wait"));
                RegisterNewUinAction.requestRegistration(newPassword.getString(), captchaCode.getString());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return;
        }

        if ((c == selectCommand) && (d == cliVersion)) {
            Options.setString(Options.OPTION_CUSTOMID, cliVersion.getString());
            Options.safe_save();
            Options.optionsForm = null;
            ContactList.activate();
            return;
        }

        if (d == keysMenu) {
            if (c == List.SELECT_COMMAND) {
                while (actionMenu.size() != 0) {
                    actionMenu.delete(0);
                }
                for (int i = 0; i < hotkeyActionNames.length; i++) {
                    actionMenu.append(ResourceBundle.getString(hotkeyActionNames[i]), null);
                }
                actionMenu.addCommand(saveCommand);
                actionMenu.addCommand(backCommand);

                int optValue = Options.getInt(hotkeyOpts[keysMenu.getSelectedIndex()]);
                for (int selIndex = 0; selIndex < hotkeyActions.length; selIndex++) {
                    if (hotkeyActions[selIndex] == optValue) {
                        actionMenu.setSelectedIndex(selIndex, true);
                        break;
                    }
                }

                MIP.display.setCurrent(actionMenu);
                return;
            } else if (c == saveCommand) {
                Options.safe_save();
                ContextMenu.build(2, ContactList.getVisibleContactListRef());
                return;
            }
        }

        //Command handler for actions list in Hotkeys...
        if (d == actionMenu) {
            if (c == saveCommand) {
                Options.setInt(
                        hotkeyOpts[keysMenu.getSelectedIndex()],
                        hotkeyActions[actionMenu.getSelectedIndex()]);
            }
            InitHotkeyMenuUI();
            return;
        } /* Look for back command */ else if (c == backCommand) {
            if (d == optionsForm || d == keysMenu) {
                ContextMenu.build(2, ContactList.getVisibleContactListRef());
            }
        } // Look for save command
        else if (c == saveCommand) {

            // Save values, depending on selected option menu item
            switch (ContextMenu.lastActionID) {
                case 26:
                    readAccontsControls();
                    setAccountOptions();
                    break;
                case 27:
                    Options.setString(Options.OPTION_SRV_HOST, srvHostTextField.getString());
                    Options.setString(Options.OPTION_SRV_PORT, srvPortTextField.getString());
                    Options.setInt(Options.OPTION_CONN_TYPE, connTypeChoiceGroup.getSelectedIndex());
                    Options.setBoolean(Options.OPTION_AUTO_CONNECT, autoConnectChoiceGroup.isSelected(0));
                    Options.setString(Options.OPTION_CONN_ALIVE_INVTERV, connAliveIntervTextField.getString());
                    Options.setBoolean(Options.OPTION_MD5_LOGIN, connPropChoiceGroup.isSelected(0));
                    if (connPropChoiceGroup.isSelected(1)) {
                        Options.setInt(Options.OPTION_CONN_PROP, 1);
                    } else {
                        Options.setInt(Options.OPTION_CONN_PROP, 0);
                    }
                    Options.setBoolean(Options.OPTION_RECONNECT, connPropChoiceGroup.isSelected(2));
                    Options.setBoolean(Options.OPTION_MULTIPLESERVERS, connPropChoiceGroup.isSelected(3));

                    //#sijapp cond.if target isnot "MOTOROLA"#
                    Options.setBoolean(Options.OPTION_SHADOW_CON, connPropChoiceGroup.isSelected(4));
                    Options.setBoolean(Options.OPTION_LOADCL, connPropChoiceGroup.isSelected(5));
                    //#sijapp cond.else#
                    Options.setBoolean(Options.OPTION_LOADCL, connPropChoiceGroup.isSelected(4));
                    //#sijapp cond.end#
                    Options.setInt(Options.OPTION_RECONNECT_NUMBER, Integer.parseInt(reconnectNumberTextField.getString()));
                    break;
                //#sijapp cond.if modules_PROXY is "true"#
                case 54:
                    Options.setInt(Options.OPTION_PRX_TYPE, srvProxyType.getSelectedIndex());
                    Options.setString(Options.OPTION_PRX_SERV, srvProxyHostTextField.getString());
                    Options.setString(Options.OPTION_PRX_PORT, srvProxyPortTextField.getString());

                    Options.setString(Options.OPTION_PRX_NAME, srvProxyLoginTextField.getString());
                    Options.setString(Options.OPTION_PRX_PASS, srvProxyPassTextField.getString());

                    Options.setString(Options.OPTION_AUTORETRY_COUNT, connAutoRetryTextField.getString());
                    break;
                //#sijapp cond.end#      
                case 28:
                    if (ResourceBundle.langAvailable.length > 1) {
                        Options.setString(Options.OPTION_UI_LANGUAGE, ResourceBundle.langAvailable[uiLanguageChoiceGroup.getSelectedIndex()]);
                    }
                    int idx = 0;

                    Options.setBoolean(Options.OPTION_DISPLAY_DATE, choiceInterfaceMisc.isSelected(idx++));


                    int newSortMethod = clSortByChoiceGroup.getSelectedIndex();
                    boolean newHideOffline = choiceContactList.isSelected(1);
                    boolean newUseGroups = choiceContactList.isSelected(0);
                    //int newColorScheme = colorScheme.getSelectedIndex();
                    Options.setInt(Options.OPTION_CL_SORT_BY, newSortMethod);
                    Options.setBoolean(Options.OPTION_CL_HIDE_OFFLINE, newHideOffline);
                    Options.setBoolean(Options.OPTION_BACKGROUND, choiceContactList.isSelected(2));
                    Options.setBoolean(Options.OPTION_SWAPSOFTS, choiceContactList.isSelected(3));
                    Options.setBoolean(Options.OPTION_HASSHIFT, choiceContactList.isSelected(4));
                    Options.setBoolean(Options.OPTION_SHOWMENUICONS, choiceContactList.isSelected(5));
                    Options.setBoolean(Options.OPTION_SECONDS, choiceContactList.isSelected(6));
                    Options.setInt(Options.OPTION_SHIFT, Integer.parseInt(Shift.getString()));
                    idx = 0;
                    Options.setBoolean(Options.OPTION_CHAT_SMALL_FONT, chrgChat.isSelected(idx++));
                    //#sijapp cond.if modules_SMILES is "true"#
                    Options.setBoolean(Options.OPTION_USE_SMILES, chrgChat.isSelected(idx++));
                    Options.setBoolean(Options.OPTION_ANIMSMILES, chrgChat.isSelected(idx++));
                    //#sijapp cond.end#
                    //#sijapp cond.if modules_HISTORY is "true"#
                    Options.setBoolean(Options.OPTION_HISTORY, chrgChat.isSelected(idx++));
                    Options.setBoolean(Options.OPTION_SHOW_LAST_MESS, chrgChat.isSelected(idx++));
                    //#sijapp cond.end#
                    //#sijapp cond.if target is "SIEMENS2"#
                    Options.setBoolean(Options.OPTION_CLASSIC_CHAT, chrgChat.isSelected(idx++));
                    //#sijapp cond.end#
                    Options.setBoolean(Options.OPTION_CP1251_HACK, chrgChat.isSelected(idx++));
                    Options.setBoolean(Options.OPTION_USER_GROUPS, newUseGroups);

                    // Set UI options for real controls
                    ContactList.optionsChanged(
                            (newUseGroups != lastGroupsUsed) || (newHideOffline != lastHideOffline),
                            (newSortMethod != lastSortMethod));

                    /*if (lastColorScheme != newColorScheme)*/ //mipUI.setColorScheme();
                    //	Options.setInt(Options.OPTION_LIGHT_TIMEOUT, Integer.parseInt(lightTimeout.getString()));
                    //		Options.setBoolean(Options.OPTION_LIGHT_MANUAL, lightManual.isSelected(0));
                    //VirtualList.setFullScreen( Options.getBoolean(Options.OPTION_FULL_SCREEN) );

                    if (!lastUILang.equals(Options.getString(Options.OPTION_UI_LANGUAGE))) {
                        Options.setBoolean(Options.OPTIONS_LANG_CHANGED, true);
                    }
                    break;

                case 46:
                    //#sijapp cond.if target isnot "DEFAULT"# ===>
                    Options.setInt(Options.OPTION_MESS_NOTIF_MODE, messageNotificationModeChoiceGroup.getSelectedIndex());
                    Options.setInt(Options.OPTION_VIBRATOR, vibratorChoiceGroup.getSelectedIndex());
                    Options.setBoolean(Options.OPTION_VIBR_SILENT, vibrSilentChoiceGroup.isSelected(0));
                    Options.setInt(Options.OPTION_ONLINE_NOTIF_MODE, onlineNotificationModeChoiceGroup.getSelectedIndex());
                    if (Options.getInt(Options.OPTION_TYPING_MODE) != 0) {
                        Options.setBoolean(Options.OPTION_SENDMTN, sendMTN.isSelected(0));
                    }
                    Options.setInt(Options.OPTION_TYPING_MODE, typingNotificationModeChoiceGroup.getSelectedIndex());

                    //#sijapp cond.if target isnot "RIM"#       
                    Options.setString(Options.OPTION_MESS_NOTIF_FILE, messageNotificationSoundfileTextField.getString());
                    Options.setInt(Options.OPTION_MESS_NOTIF_VOL, messageNotificationSoundVolume.getValue() * 10);
                    Options.setString(Options.OPTION_ONLINE_NOTIF_FILE, onlineNotificationSoundfileTextField.getString());
                    Options.setInt(Options.OPTION_ONLINE_NOTIF_VOL, onlineNotificationSoundVolume.getValue() * 10);
                    Options.setString(Options.OPTION_TYPING_FILE, typingNotificationSoundfileTextField.getString());
                    Options.setInt(Options.OPTION_TYPING_VOL, typingNotificationSoundVolume.getValue() * 10);
                    //#sijapp cond.end#
                    //#sijapp cond.end# <===
                    Options.setInt(Options.OPTION_POPUP_WIN2, chrgPopupWin.getSelectedIndex());

                    //#sijapp cond.if target="MOTOROLA"#
                    Options.setBoolean(Options.OPTION_FLASH_BACKLIGHT, flashBkltChoiceGroup.isSelected(0));
                    //#sijapp cond.end#
                    //#sijapp cond.if target="MIDP2"#
                    Options.setBoolean(Options.OPTION_BRING_UP, chsBringUp.isSelected(0));
                    //#sijapp cond.end#
                    break;

                //#sijapp cond.if modules_TRAFFIC is "true"#
                case 30:
                    Options.setInt(Options.OPTION_COST_PER_PACKET, Util.decimalToInt(costPerPacketTextField.getString()));
                    costPerPacketTextField.setString(Util.intToDecimal(Options.getInt(Options.OPTION_COST_PER_PACKET)));
                    Options.setInt(Options.OPTION_COST_PER_DAY, Util.decimalToInt(costPerDayTextField.getString()));
                    costPerDayTextField.setString(Util.intToDecimal(Options.getInt(Options.OPTION_COST_PER_DAY)));
                    Options.setInt(Options.OPTION_COST_PACKET_LENGTH, Integer.parseInt(costPacketLengthTextField.getString()) * 1024);
                    Options.setString(Options.OPTION_CURRENCY, currencyTextField.getString());
                    break;
                //#sijapp cond.end#
                case 31:
                    if (Integer.parseInt(DCVersion.getString()) > 65534) {
                        DCVersion.setString("8");
                    }
                    boolean DCChanged;
                    DCChanged = (Options.getInt(Options.OPTION_DCVERSION) == Integer.parseInt(DCVersion.getString()));
                    Options.setInt(Options.OPTION_DCVERSION, Integer.parseInt(DCVersion.getString()));
                    if (BirthdayFlagChoiceGroup.isSelected(0)) {
                        Options.setInt(Options.OPTION_BFLAG, 1);
                    } else {
                        Options.setInt(Options.OPTION_BFLAG, 0);
                    }
                    if (BirthdayFlagChoiceGroup.isSelected(1)) {
                        Options.setBoolean(Options.OPTION_WEBAWARE, true);
                    } else {
                        Options.setBoolean(Options.OPTION_WEBAWARE, false);
                    }
                    if (BirthdayFlagChoiceGroup.isSelected(2)) {
                        Options.setBoolean(Options.OPTION_SHOWXTRAZMSG, true);
                    } else {
                        Options.setBoolean(Options.OPTION_SHOWXTRAZMSG, false);
                    }
                    if (BirthdayFlagChoiceGroup.isSelected(3)) {
                        Options.setBoolean(Options.OPTION_UTF8IN, true);
                    } else {
                        Options.setBoolean(Options.OPTION_UTF8IN, false);
                    }
                    if (BirthdayFlagChoiceGroup.isSelected(4)) {
                        Options.setBoolean(Options.OPTION_UTF8OUT, true);
                    } else {
                        Options.setBoolean(Options.OPTION_UTF8OUT, false);
                    }
                    if (Icq.isConnected()) {
                        try {
                            Icq.setCliStatus(false, DCChanged);
                        } catch (mipException e) {
                            mipException.handleException(e);
                            if (e.isCritical()) {
                                return;
                            }
                        }
                    }
                    break;
                case 32: {
                    /* Set up time zone*/
                    int timeZone = chsTimeZone.getSelectedIndex() - 12;
                    Options.setInt(Options.OPTIONS_GMT_OFFSET, timeZone);

                    /* Translate selected time to GMT */
                    int selHour = chsCurrTime.getSelectedIndex() - timeZone;
                    if (selHour < 0) {
                        selHour += 24;
                    }
                    if (selHour >= 24) {
                        selHour -= 24;
                    /* Calculate diff. between selected GMT time and phone time */
                    }
                    int localOffset = selHour - currentHour;
                    while (localOffset >= 12) {
                        localOffset -= 24;
                    }
                    while (localOffset < -12) {
                        localOffset += 24;
                    }
                    Options.setInt(Options.OPTIONS_LOCAL_OFFSET, localOffset);
                    break;
                }

                case VMenuItem.VMI_ACT_ANTISPAM:
                    Options.setString(Options.OPTION_ANTISPAM_MSG, antispamMsgTextField.getString());
                    Options.setString(Options.OPTION_ANTISPAM_ANSWER, antispamAnswerTextField.getString());
                    Options.setString(Options.OPTION_ANTISPAM_HELLO, antispamHelloTextField.getString());
                    Options.setBoolean(Options.OPTION_ANTISPAM_ENABLE, antispamEnableChoiceGroup.isSelected(0));
                    Options.setBoolean(Options.OPTION_ANTISPAM_INVIS, antispamEnableChoiceGroup.isSelected(1));
                    break;

                case VMenuItem.VMI_ACT_AUTOSTATUS:
                    Options.setBoolean(Options.OPTION_AUTOAWAY_ENABLE, autoStatusChoiceGroup.isSelected(0));
                    Options.setBoolean(Options.OPTION_AUTONA_ENABLE, autoStatusChoiceGroup.isSelected(1));
                    Options.setBoolean(Options.OPTION_AUTOONLINE, autoStatusChoiceGroup.isSelected(2));
                    Options.setInt(Options.OPTION_AUTOSTATUS_DELAY, Integer.parseInt(autoStatusTextField.getString()));
                    TimerTasks.delay = Options.getInt(Options.OPTION_AUTOSTATUS_DELAY) * 60000;
                    break;
            }

            /* Save options */
            Options.safe_save();

            /* Activate MM/CL */
            if (Icq.isConnected()) {
                Options.optionsForm = null;
                ContactList.activate();
            } else {
                ContextMenu.build(2, ContactList.getVisibleContactListRef());
            }
        } /* Accounts */ else if (c == cmdAddNewAccount) {
            readAccontsControls();
            uins.addElement(Options.emptyString);
            passwords.addElement(Options.emptyString);
            clearForm();
            showAccountControls();
            return;
        } else if (c == cmdDeleteAccount) {
            readAccontsControls();
            int size = uins.size();
            String items[] = new String[size];
            for (int i = 0; i < size; i++) {
                items[i] = checkUin((String) uins.elementAt(i));
            }
            mipUI.showSelector("delete", items, this, TAG_DELETE_ACCOUNT, false);
            return;
        } else if (mipUI.getCommandType(c, TAG_DELETE_ACCOUNT) == mipUI.CMD_OK) {
            readAccontsControls();
            int index = mipUI.getLastSelIndex();
            uins.removeElementAt(index);
            passwords.removeElementAt(index);
            clearForm();
            showAccountControls();
            MIP.display.setCurrent(optionsForm);
        }

    }

    public void clearForm() {
        optionsForm.removeCommand(cmdAddNewAccount);
        optionsForm.removeCommand(cmdDeleteAccount);
        optionsForm.removeCommand(cmdRegisterAccount);
        optionsForm.removeCommand(cmdRequestCaptchaImage);
        optionsForm.removeCommand(cmdRequestRegistration);
        optionsForm.deleteAll();
    }
} // end of 'class OptionsForm'



