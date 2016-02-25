//Context Menu handler... (C) D@rkNeo, 2007.
//A part of MIP.
package mip;

import javax.microedition.lcdui.*;
import mip.comm.*;
import java.util.Vector;
import mip.util.ResourceBundle;
import DrawControls.*;
import javax.microedition.midlet.MIDletStateChangeException;

import mip.comm.SearchAction;
import mip.comm.UpdateContactListAction;

public class ContextMenu implements CommandListener {

    public static ContextMenu _this;
    public static Form xForm;
    public static String smapeURL;
    public static Form cityForm;
    static private boolean isConnected;
    public static int lastActionID;
    private static final int TAG_EXIT = 1;
    private static final int TAG_RENAME_GROUPS = 2;
    private static final int TAG_DELETE_GROUPS = 3;
    private static final int TAG_CL = 4;
    private static Displayable currDisp;
    private static int savedDsp = 1;
    private static int currType;
    private static final int STATUS_NONE = 0;
    private static final int STATUS_ADD_CONTACT = 1;
    private static final int STATUS_ADD_GROUP = 2;
    private static final int STATUS_RENAME_GROUP = 3;
    private static int status = STATUS_NONE;
    public static ContactListContactItem lastCCI;
    private Command saveCommand;
    /* Abort command */
    private static Command backCommand = new Command(ResourceBundle.getString("back"), Command.BACK, 1);
    /* Send command */
    private static Command sendCommand = new Command(ResourceBundle.getString("send"), Command.OK, 1);
    public static TextField xTextField;
    public static TextField xTextField2;
    public static ChoiceGroup xOptionsChoiceGroup;
    public static int actionType = 1;
    public String actionData;
    public static String actionResult;
    public static volatile Thread thread;
    private static Vector mItems;
    public static boolean isCompleted = false;
    /* Groups list */
    static private int[] groupIds;
    /* Text box for adding users to the contact list */
    static private TextField uinTextField;

    static public void showTextBoxForm(String caption, String label, String text, int fieldType) {
        cityForm = new Form(ResourceBundle.getString(caption));
        uinTextField = new TextField(ResourceBundle.getString(label), text, 16, fieldType);
        cityForm.append(uinTextField);

        cityForm.addCommand(sendCommand);
        cityForm.addCommand(backCommand);
        cityForm.setCommandListener(_this);
        MIP.display.setCurrent(cityForm);
    }

    public void commandAction(Command c, Displayable d) {
        /* User selects OK in exit question message box */
        if (c == backCommand) {
            ContactList.activate();
        } else if (c == saveCommand) {
            Options.setString(Options.OPTION_STATUS_TITLE, xTextField.getString());
            Options.setString(Options.OPTION_STATUS_MESSAGE, xTextField2.getString());
            if (xOptionsChoiceGroup.isSelected(0)) {
                Options.setBoolean(Options.OPTION_SHOWXTRAZMSG, true);
            } else {
                Options.setBoolean(Options.OPTION_SHOWXTRAZMSG, false);
            }
            int bf = Options.getInt(Options.OPTION_BFLAG);
            if (xOptionsChoiceGroup.isSelected(1)) {
                Options.setInt(Options.OPTION_BFLAG, 1);
            } else {
                Options.setInt(Options.OPTION_BFLAG, 0);
            }
            Options.safe_save();
            if (Icq.isConnected()) {
                try {
                    Icq.setCLI_USERINFO(Options.getInt(Options.OPTION_CLI_ID));
                    if (bf != Options.getInt(Options.OPTION_BFLAG)) {
                        Icq.setCliStatus(false, false);
                    }
                } catch (mipException e) {
                    mipException.handleException(e);
                    if (e.isCritical()) {
                        return;
                    }
                }
            }
            ContactList.getVisibleContactListRef().setCapImage(MIP.SIcons.images.elementAt(mipUI.getStatusImageIndex(Icq.getCurrentStatus())), MainMenu.getXImage());
            ContextMenu.build(6, ContactList.getVisibleContactListRef());
        } else if (mipUI.getCommandType(c, TAG_RENAME_GROUPS) == mipUI.CMD_OK) {
            String groupName = ContactList.getGroupById(groupIds[mipUI.getLastSelIndex()]).getName();
            showTextBoxForm("rename_group", "group_name", groupName, TextField.ANY);
        } else if (mipUI.getCommandType(c, TAG_DELETE_GROUPS) == mipUI.CMD_OK) {
            UpdateContactListAction deleteGroupAct = new UpdateContactListAction(ContactList.getGroupById(groupIds[mipUI.getLastSelIndex()]), UpdateContactListAction.ACTION_DEL);
            try {
                Icq.requestAction(deleteGroupAct);
                SplashCanvas.addTimerTask("wait", deleteGroupAct, false);
            } catch (mipException e) {
                mipException.handleException(e);
            }
        } else if (MIP.clSelect.isMyOkCommand(c)) {
            switch (lastActionID) {
                case 60:
                    Options.setInt(Options.OPTION_COLOR1, MIP.clSelect.curcolor);
                    break;
                case 61:
                    Options.setInt(Options.OPTION_COLOR2, MIP.clSelect.curcolor);
                    break;
                case 62:
                    Options.setInt(Options.OPTION_COLOR3, MIP.clSelect.curcolor);
                    break;
                case 63:
                    Options.setInt(Options.OPTION_COLOR4, MIP.clSelect.curcolor);
                    break;
                case 64:
                    Options.setInt(Options.OPTION_COLOR5, MIP.clSelect.curcolor);
                    break;
                case 65:
                    Options.setInt(Options.OPTION_COLOR6, MIP.clSelect.curcolor);
                    break;
                case 66:
                    Options.setInt(Options.OPTION_COLOR7, MIP.clSelect.curcolor);
                    break;
                case 67:
                    Options.setInt(Options.OPTION_COLOR8, MIP.clSelect.curcolor);
                    break;
                case 68:
                    Options.setInt(Options.OPTION_COLOR9, MIP.clSelect.curcolor);
                    break;
                case 69:
                    Options.setInt(Options.OPTION_COLOR10, MIP.clSelect.curcolor);
                    break;
            }
            mipUI.setColorScheme();
            Options.safe_save();
            ContextMenu.build(4, ContactList.getVisibleContactListRef());
        } else if (mipUI.getCommandType(c, TAG_EXIT) == mipUI.CMD_YES) {
            doExit(true);
        } else if (mipUI.getCommandType(c, TAG_EXIT) == mipUI.CMD_NO) {
            ContactList.activate();
        } else if (mipUI.getCommandType(c, 15) == mipUI.CMD_YES) {
            actionType = 1;
            actionData = actionResult;
            isCompleted = false;
        //thread = new Thread(this);
        //thread.start();
        } else if (mipUI.getCommandType(c, 15) == mipUI.CMD_NO) {
            ContextMenu.build(0, ContactList.getVisibleContactListRef());
        }
    }

    public ContextMenu() {
        _this = this;
        mItems = new Vector();
        saveCommand = new Command(ResourceBundle.getString("save"), Command.OK, 1);
        xForm = new Form("Xtraz");
        xTextField = new TextField(ResourceBundle.getString("xtraz_t"), "", 100, TextField.ANY);
        xTextField2 = new TextField(ResourceBundle.getString("xtraz_m"), "", 500, TextField.ANY);
        xOptionsChoiceGroup = new ChoiceGroup("", Choice.MULTIPLE);
        xOptionsChoiceGroup.append(ResourceBundle.getString("showxtraz"), null);
        xOptionsChoiceGroup.setSelectedIndex(0, Options.getBoolean(Options.OPTION_SHOWXTRAZMSG));
        xOptionsChoiceGroup.append(ResourceBundle.getString("bflag"), null);
        if (Options.getInt(Options.OPTION_BFLAG) == 1) {
            xOptionsChoiceGroup.setSelectedIndex(1, true);
        }
        xForm.append(xTextField);
        xForm.append(xTextField2);
        xForm.append(xOptionsChoiceGroup);
        xForm.addCommand(saveCommand);
        xForm.setCommandListener(this);
    }

    public static void doExit(boolean anyway) {
        if (!anyway) {
            String msg = ResourceBundle.getString("really_exit") + "\n";
            if (ContactList.getUnreadMessCount() > 0) {
                msg += ResourceBundle.getString("humess");
            }
            mipUI.messageBox(ResourceBundle.getString("attention"), msg, mipUI.MESBOX_YESNO, _this, 1);
        } else {
            Icq.disconnect();
            Options.safe_save();
            try {
                ContactList.save();
                Thread.sleep(400);
            } catch (Exception e1) {
                /* Do nothing */
            }

            /* Exit app */
            try {
                MIP.MIP.destroyApp(true);
            } catch (MIDletStateChangeException e) { /* Do nothing */

            }
        }
    }

    static public int getXiD() {
        int xStatusIndex = Options.getInt(Options.OPTION_XSTATUS);
        if (xStatusIndex == -1) {
            return 0;
        }
        return xStatusIndex;
    }
    /* int type - type of ContextMenu, that will be created. 	
    0 - Main Menu.
    1 - Contact context menu
    2 - Options menu
    3 - CL Control submenu (main menu)
    4 - color options
    5 - prlists
    6 - statuses menu
    7 - copy submenu
     */
    
    static public void build(int type, VirtualList vl) {
        mItems.removeAllElements();
        currType = type;
        switch (type) {
            case 7: //copy submenu
                //VirtualList.setSoftNames("cancel", "select");
                mItems.addElement(new VMenuItem("copy_text", VMenuItem.VMI_ACT_COPYTEXT, 38, 0));
                break;

            case 3:
                //VirtualList.setSoftNames("back", "select");
                mItems.addElement(new VMenuItem("search_user", VMenuItem.VMI_ACT_SEARCHCONTACT, 2, 0));
                mItems.addElement(new VMenuItem("add_group", VMenuItem.VMI_ACT_ADDGROUP, 28, 0));
                //mItems.addElement(new VMenuItem("rename_group", 42, 11, 0));
                //mItems.addElement(new VMenuItem("del_group", 43, 6, 0));
                break;
            case 4:
                //VirtualList.setSoftNames("back", "select");
                MIP.setDsp(MIP.DSP_COLORLIST);
                mItems.addElement(new VMenuItem("options_color1", VMenuItem.VMI_COLOR_1, 29, 0));
                mItems.addElement(new VMenuItem("options_color2", VMenuItem.VMI_COLOR_2, 29, 0));
                mItems.addElement(new VMenuItem("options_color3", VMenuItem.VMI_COLOR_3, 29, 0));
                mItems.addElement(new VMenuItem("options_color4", VMenuItem.VMI_COLOR_4, 29, 0));
                mItems.addElement(new VMenuItem("options_color5", VMenuItem.VMI_COLOR_5, 29, 0));
                mItems.addElement(new VMenuItem("options_color6", VMenuItem.VMI_COLOR_6, 29, 0));
                mItems.addElement(new VMenuItem("options_color7", VMenuItem.VMI_COLOR_7, 29, 0));
                mItems.addElement(new VMenuItem("options_color8", VMenuItem.VMI_COLOR_8, 29, 0));
                mItems.addElement(new VMenuItem("options_color9", VMenuItem.VMI_COLOR_9, 29, 0));
                mItems.addElement(new VMenuItem("options_color10", VMenuItem.VMI_COLOR_10, 29, 0));
                break;

            case 1:
                //VirtualList.setSoftNames("cancel", "select");
                ContactListContactItem cItem = getCCI();
                lastCCI = cItem;
                mItems.addElement(new VMenuItem("send_message", VMenuItem.VMI_ACT_MSG, 14, 1));
                if (cItem.getBooleanValue(ContactListContactItem.CONTACTITEM_IS_TEMP)) {
                    mItems.addElement(new VMenuItem("add_user", VMenuItem.VMI_ACT_ADDUSER, 28, 0));
                }
                if (cItem.getBooleanValue(ContactListContactItem.CONTACTITEM_NO_AUTH)) {
                    mItems.addElement(new VMenuItem("requauth", VMenuItem.VMI_ACT_AUTH, 16, 1));
                }
                if (cItem.isMessageAvailable(ContactListContactItem.MESSAGE_AUTH_REQUEST)) {
                    mItems.addElement(new VMenuItem("grant", VMenuItem.VMI_ACT_GRANT, 16, 1));
                /*
                try{System.out.println((String) (Util.parseMessageForURL(textList.getCurrText(0, false)).elementAt(0)));}
                catch(Exception e) {}
                 */
                }
                if (MIP.lastDsp != 1) {
                    /*smapeURL = null;
                    try{smapeURL = ChatHistory.getURL(lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_UIN));}
                    catch (Exception e) {}
                    if (smapeURL != null ) mItems.addElement(new VMenuItem("uploadurl", VMenuItem.VMI_UPLOADURL, 16, 1));*/

                    mItems.addElement(new VMenuItem("copy_noquota", VMenuItem.VMI_ACT_COPYNOQUOTE, 39, 0));
                    mItems.addElement(new VMenuItem("copy_text", VMenuItem.VMI_ACT_COPY, 38, 0));
                }
                if (cItem.getIntValue(ContactListContactItem.CONTACTITEM_XSTATUS) != -1) {
                    mItems.addElement(new VMenuItem("reqstatmsg", VMenuItem.VMI_ACT_READXTRAZ, cItem.getIntValue(ContactListContactItem.CONTACTITEM_XSTATUS), 2));
                }
                if (mipUI.getClipBoardText() != null) {
                    mItems.addElement(new VMenuItem("paste", VMenuItem.VMI_ACT_QUOTE, 18, 0));
                }
                mItems.addElement(new VMenuItem("copyuin", VMenuItem.VMI_ACT_COPYUIN, 35, 0));
                mItems.addElement(new VMenuItem("info", VMenuItem.VMI_ACT_PROFILE, 9, 0));
                mItems.addElement(new VMenuItem("prlists", VMenuItem.VMI_ACT_PRLISTS, 33, 0));
                if ((MIP.lastDsp != 1) && (!Options.getBoolean(Options.OPTION_HISTORY))) {
                    mItems.addElement(new VMenuItem("add_to_history", VMenuItem.VMI_ACT_SAVETOHISTORY, 37, 0));
                }
                mItems.addElement(new VMenuItem("history", VMenuItem.VMI_ACT_HISTORY, 7, 0));
                mItems.addElement(new VMenuItem("rename", VMenuItem.VMI_ACT_RENAME, 11, 0));

                mItems.addElement(new VMenuItem("remove", VMenuItem.VMI_ACT_DELETE, 34, 0));
                mItems.addElement(new VMenuItem("remove_me", VMenuItem.VMI_ACT_DELETEME, 40, 0));
                mItems.addElement(new VMenuItem("dc_info", VMenuItem.VMI_ACT_DCINFO, 10, 0));
                if (MIP.lastDsp != 1) {
                    mItems.addElement(new VMenuItem("delete_chat", VMenuItem.VMI_ACT_DELETECHAT, 36, 0));
                }
                MIP.setDsp(MIP.DSP_CONTACTMENU);
                break;

            case 6:
                VirtualList.setMenuSoftNames("back", "select");
                MIP.setDsp(MIP.DSP_STATUSES);
//                mItems.addElement(new VMenuItem("set_status", VMenuItem.VMI_ACT_STATUS, mipUI.getStatusImageIndex(Options.getLong(Options.OPTION_ONLINE_STATUS)), 1));
                mItems.addElement(new VMenuItem("set_xstatus", VMenuItem.VMI_ACT_XTRAZ, getXiD(), 2));
                mItems.addElement(new VMenuItem("set_pstatus", VMenuItem.VMI_ACT_PRSTATUS, Options.getInt(Options.OPTION_PRIVATESTATUS) - 1, 3));
                mItems.addElement(new VMenuItem("status_online", VMenuItem.VMI_STATUS_ONLINE, mipUI.getStatusImageIndex(ContactList.STATUS_ONLINE), 1));
                mItems.addElement(new VMenuItem("status_chat", VMenuItem.VMI_STATUS_CHAT, mipUI.getStatusImageIndex(ContactList.STATUS_CHAT), 1));
                mItems.addElement(new VMenuItem("status_occupied", VMenuItem.VMI_STATUS_OCCUPIED, mipUI.getStatusImageIndex(ContactList.STATUS_OCCUPIED), 1));
                mItems.addElement(new VMenuItem("status_dnd", VMenuItem.VMI_STATUS_DND, mipUI.getStatusImageIndex(ContactList.STATUS_DND), 1));
                mItems.addElement(new VMenuItem("status_away", VMenuItem.VMI_STATUS_NA, mipUI.getStatusImageIndex(ContactList.STATUS_AWAY), 1));
                mItems.addElement(new VMenuItem("status_na", VMenuItem.VMI_STATUS_NA, mipUI.getStatusImageIndex(ContactList.STATUS_NA), 1));
                mItems.addElement(new VMenuItem("status_evil", VMenuItem.VMI_STATUS_EVIL, mipUI.getStatusImageIndex(ContactList.STATUS_EVIL), 1));
                mItems.addElement(new VMenuItem("status_depress", VMenuItem.VMI_STATUS_DEPRESS, mipUI.getStatusImageIndex(ContactList.STATUS_DEPRESS), 1));
                mItems.addElement(new VMenuItem("status_home", VMenuItem.VMI_STATUS_HOME, mipUI.getStatusImageIndex(ContactList.STATUS_HOME), 1));
                mItems.addElement(new VMenuItem("status_work", VMenuItem.VMI_STATUS_WORK, mipUI.getStatusImageIndex(ContactList.STATUS_WORK), 1));
                mItems.addElement(new VMenuItem("status_lunch", VMenuItem.VMI_STATUS_LUNCH, mipUI.getStatusImageIndex(ContactList.STATUS_LUNCH), 1));
                mItems.addElement(new VMenuItem("status_invisible", VMenuItem.VMI_STATUS_INVISIBLE, mipUI.getStatusImageIndex(ContactList.STATUS_INVISIBLE), 1));
                mItems.addElement(new VMenuItem("status_invis_all", VMenuItem.VMI_STATUS_INVIS_ALL, mipUI.getStatusImageIndex(ContactList.STATUS_INVIS_ALL), 1));
                break;

            case 0:
                VirtualList.setMenuSoftNames("cancel", "select");
                if (Icq.isNotConnected()) {
                    mItems.addElement(new VMenuItem("connect", VMenuItem.VMI_ACT_CONNECT, 16, 0));
                } else {
                    mItems.addElement(new VMenuItem("keylock_enable", VMenuItem.VMI_ACT_LOCK, 0, 0));
                    mItems.addElement(new VMenuItem("disconnect", VMenuItem.VMI_ACT_DISCONNECT, 17, 0));
                }

                mItems.addElement(new VMenuItem("set_status", VMenuItem.VMI_ACT_STATUSMENU, mipUI.getStatusImageIndex(Options.getLong(Options.OPTION_ONLINE_STATUS)), 1));
                mItems.addElement(new VMenuItem("cliID", VMenuItem.VMI_ACT_CLIENTiDMENU, Options.getInt(Options.OPTION_CLI_ID), 4));
                if (Icq.isConnected()) {
                    mItems.addElement(new VMenuItem("manage_contact_list", VMenuItem.VMI_ACT_LISTCTRL, 30, 0));
                    mItems.addElement(new VMenuItem("editprofile", VMenuItem.VMI_ACT_EDITPROFILE, 31, 0));
                }
                mItems.addElement(new VMenuItem("options_lng", VMenuItem.VMI_ACT_OPTIONS, 3, 0));
                mItems.addElement(new VMenuItem("traffic_lng", VMenuItem.VMI_ACT_STATS, 22, 0));
                //#sijapp cond.if target is "MIDP2" #
                mItems.addElement(new VMenuItem("minimize", VMenuItem.VMI_ACT_MINIMIZE, 25, 0));
                //#sijapp cond.end #
                mItems.addElement(new VMenuItem("about", VMenuItem.VMI_ACT_ABOUT, 4, 0));
                mItems.addElement(new VMenuItem("test", VMenuItem.VMI_TEST, 3, 0));
                mItems.addElement(new VMenuItem("exit", VMenuItem.VMI_ACT_EXIT, 26, 0));
                MIP.setDsp(MIP.DSP_MAINMENU);
                break;

            case 2:
                VirtualList.setMenuSoftNames("back", "select");
                mItems.addElement(new VMenuItem("options_account", VMenuItem.VMI_ACT_ACCOUNT, 12, 0));
                mItems.addElement(new VMenuItem("options_network", VMenuItem.VMI_ACT_NETWORK, 13, 0));
                if (Options.getInt(Options.OPTION_CONN_TYPE) == Options.CONN_TYPE_PROXY) {
                    mItems.addElement(new VMenuItem("proxy", VMenuItem.VMI_ACT_PROXY, 19, 0));
                }
                mItems.addElement(new VMenuItem("options_interface", VMenuItem.VMI_ACT_INTERFACE, 14, 0));
                mItems.addElement(new VMenuItem("options_hotkeys", VMenuItem.VMI_ACT_HOTKEYS, 20, 0));
                mItems.addElement(new VMenuItem("options_signaling", VMenuItem.VMI_ACT_NOTIFY, 15, 0));
                mItems.addElement(new VMenuItem("options_cost", VMenuItem.VMI_ACT_COST, 23, 0));
                mItems.addElement(new VMenuItem("time_zone", VMenuItem.VMI_ACT_TIMEZONE, 24, 0));
                mItems.addElement(new VMenuItem("options_cliID", VMenuItem.VMI_ACT_ADVSETS, 9, 0));
                mItems.addElement(new VMenuItem("options_themes", VMenuItem.VMI_ACT_THEMES, 29, 0));
                mItems.addElement(new VMenuItem("options_spam", VMenuItem.VMI_ACT_ANTISPAM, 32, 0));
                mItems.addElement(new VMenuItem("options_autostatus", VMenuItem.VMI_ACT_AUTOSTATUS, mipUI.getStatusImageIndex(ContactList.STATUS_AWAY), 1));
                MIP.setDsp(MIP.DSP_OPTIONS);
                break;

            case 8:
                VirtualList.setMenuSoftNames("cancel", "select");
                mItems.addElement(new VMenuItem("copy_text", VMenuItem.VMI_HIST_COPY, -1, 0));
                mItems.addElement(new VMenuItem("find", VMenuItem.VMI_HIST_FIND, -1, 0));
                mItems.addElement(new VMenuItem("history_info", VMenuItem.VMI_HIST_INFO, -1, 0));
                mItems.addElement(new VMenuItem("clear", VMenuItem.VMI_HIST_CLEAR, -1, 0));
                mItems.addElement(new VMenuItem("export", VMenuItem.VMI_HIST_EXPORT, -1, 0));
                mItems.addElement(new VMenuItem("exportall", VMenuItem.VMI_HIST_EXPORTALL, -1, 0));

                MIP.setDsp(MIP.DSP_HISTORY);
                break;
            case 9:
               VirtualList.setMenuSoftNames("cancel", "select");
                mItems.addElement(new VMenuItem("copy_text", VMenuItem.VMI_HIST_COPY, -1, 0));
                MIP.setDsp(MIP.DSP_HISTORYMSG);
                break;

            case 10:
                VirtualList.setMenuSoftNames("back", "select");
                MIP.setDsp(MIP.DSP_PRLISTS);
                if (lastCCI.getVisibleId() == 0) {
                    mItems.addElement(new VMenuItem("add_visible_list", VMenuItem.VMI_LISTS_VISIBLE, 5, 0));
                } else {
                    mItems.addElement(new VMenuItem("rem_visible_list", VMenuItem.VMI_LISTS_VISIBLE, 6, 0));
                }
                if (lastCCI.getInvisibleId() == 0) {
                    mItems.addElement(new VMenuItem("add_invisible_list", VMenuItem.VMI_LISTS_INVISIBLE, 5, 0));
                } else {
                    mItems.addElement(new VMenuItem("rem_invisible_list", VMenuItem.VMI_LISTS_INVISIBLE, 6, 0));
                }
                if (lastCCI.getIgnoreId() == 0) {
                    mItems.addElement(new VMenuItem("add_ignore_list", VMenuItem.VMI_LISTS_IGNORE, 5, 0));
                } else {
                    mItems.addElement(new VMenuItem("rem_ignore_list", VMenuItem.VMI_LISTS_IGNORE, 6, 0));
                }
                break;

            case 11: //Chat
                MIP.setDsp(MIP.DSP_DELCHAT);
                VirtualList.setMenuSoftNames("cancel", "delete");
                mItems.addElement(new VMenuItem("currect_contact", VMenuItem.VMI_DELCHAT_CURRENT, -1, 0));
                mItems.addElement(new VMenuItem("all_contact_except_this", VMenuItem.VMI_DELCHAT_NOTCURRENT, -1, 0));
                mItems.addElement(new VMenuItem("all_contacts", VMenuItem.VMI_DELCHAT_ALL, -1, 0));
                break;

            case 12: //Hist
                MIP.setDsp(MIP.DSP_DELHIST);
                VirtualList.setMenuSoftNames("cancel", "delete");
                mItems.addElement(new VMenuItem("all_contacts", VMenuItem.VMI_DELHIST_ALL, -1, 0));
                mItems.addElement(new VMenuItem("all_contact_except_this", VMenuItem.VMI_DELHIST_NOTCURRENT, -1, 0));
                mItems.addElement(new VMenuItem("currect_contact", VMenuItem.VMI_DELHIST_CURRENT, -1, 0));
                break;

            case 14: // profile
                //screen.addCommand(this.addCommand);

                mItems.addElement(new VMenuItem("add_to_list", VMenuItem.VMI_ADDCONTACT, 28, 0));
                mItems.addElement(new VMenuItem("send_message", VMenuItem.VMI_SRCHSENDMSG, 14, 1));
                mItems.addElement(new VMenuItem("info", VMenuItem.VMI_SRCHINFO, 9, 0));
                //screen.addCommand(this.cmdSendMessage);
                //screen.addCommand(this.cmdShowInfo);
                break;

            case 15: //DirMenu
                MIP.setDsp(MIP.DSP_DIRMENU);
                VirtualList.setMenuSoftNames("cancel", "select");
//                            mItems.addElement(new VMenuItem("open", VMenuItem.VMI_DIRBR_OPEN, -1, 0));        
                mItems.addElement(new VMenuItem("savehere", VMenuItem.VMI_DIRBR_SELECT, -1, 0));
//                            mItems.addElement(new VMenuItem("new_dir", VMenuItem.VMI_DIRBR_NEWDIR, -1, 0));
                break;
                
            case 16: //Themes
                MIP.setDsp(MIP.DSP_THEMES);
                VirtualList.setMenuSoftNames("back", "select");
                mItems.addElement(new VMenuItem("options_color", VMenuItem.VMI_ACT_COLORSCHEME, 29, 0));
                int curVMI = VMenuItem.VMI_LIST_THEMES;
                for (int i = 0; i < Options.themesList.size(); i++) {
                    mItems.addElement(new VMenuItem((String)Options.themesList.elementAt(i), curVMI++, 29, 0));
                }
        }
        buildTree(vl);
    }
    
    static private void buildTree(VirtualList vl) {
        vl.menuItems = mItems;
        vl.setUIState(1);
        vl.curMenuItemIndex = 0;
        vl.initMenu();
        vl.justShow(MIP.display);
    }

    public void VTnodeClicked(TreeNode node) {
        if (node == null) {
            return;
        }
        VMenuItem item = (VMenuItem) node.getData();
        item.performAction();
    }

    public int compareNodes(TreeNode node1, TreeNode node2) {
        return 1;
    }
    ;

    public static ContactListContactItem getCCI() {
        return ContactList.getItembyUIN(MIP.uin);
    }
    final public static int MSGBS_DELETECONTACT = 1;
    final public static int MSGBS_REMOVEME = 2;
    final public static int SELECTOR_DEL_CHAT = 3;
    final public static int SELECTOR_SELECT_GROUP = 4;

    public static void saveDisplayable() {
        System.out.println("saving dsp! " + MIP.lastDsp);
        currDisp = MIP.display.getCurrent();
        savedDsp = MIP.lastDsp;
    }

    public static void activateLast() {
        mipUI.backToLastScreen();
    }

    public static void restoreDisplayable() {
        System.out.println("restoring dsp! " + savedDsp);
        //MIP.display.setCurrent(currDisp);
        MIP.setDsp(savedDsp);
    }

    public static void performAction(int itemAction) {
        lastActionID = itemAction;
        switch (itemAction) {
            case VMenuItem.VMI_ACT_COPYTEXT:
                if (VirtualList.getCurrent() instanceof TextList) {
                    mipUI.setClipBoardText(((TextList) VirtualList.getCurrent()).copyText());
                    VirtualList.getCurrent().setUIState(0);
                }
                break;

            case VMenuItem.VMI_ACT_SAVETOHISTORY:
                ChatHistory.addTextToHistory(lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_UIN), lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_NAME));
                ContextMenu.restoreDisplayable();
                break;

            case VMenuItem.VMI_ACT_AUTH:
                mipUI.authMessage(10002, lastCCI, "auth", "plsauthme");
                break;

            case VMenuItem.VMI_ACT_ADDUSER:
                Search search = new Search(true);
                String data[] = new String[Search.LAST_INDEX];
                data[Search.UIN] = lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_UIN);

                SearchAction act = new SearchAction(search, data, SearchAction.CALLED_BY_ADDUSER);

                try {
                    Icq.requestAction(act);
                } catch (mipException e) {
                    mipException.handleException(e);
                }

                SplashCanvas.addTimerTask("wait", act, false);
                break;
            //#sijapp cond.if target is "MIDP2" #
            case VMenuItem.VMI_ACT_MINIMIZE:
                MIP.setMinimized(true);
                break;
            //#sijapp cond.end #
            case VMenuItem.VMI_ACT_GRANT:
                lastCCI.setIntValue(ContactListContactItem.CONTACTITEM_AUTREQUESTS, lastCCI.getIntValue(ContactListContactItem.CONTACTITEM_AUTREQUESTS) - 1);
                SystemNotice notice = new SystemNotice(SystemNotice.SYS_NOTICE_AUTHORISE, lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_UIN), true, "");
                SysNoticeAction sysNotAct = new SysNoticeAction(notice);
                try {
                    Icq.requestAction(sysNotAct);
                } catch (mipException e) {
                    mipException.handleException(e);
                    if (e.isCritical()) {
                        return;
                    }
                }
                ContactList.activate();
                break;

            case VMenuItem.VMI_ACT_MSG:
                mipUI.writeMessage(lastCCI, null);
                break;

            case VMenuItem.VMI_ACT_COPYUIN:
                mipUI.setClipBoardText(lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_UIN));
                ContextMenu.restoreDisplayable();
                break;

            case VMenuItem.VMI_ACT_DELETE:
                mipUI.messageBox(
                        ResourceBundle.getString("remove") + "?",
                        ResourceBundle.getString("remove") + " " + lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_NAME) + "?",
                        mipUI.MESBOX_OKCANCEL,
                        lastCCI,
                        MSGBS_DELETECONTACT);
                break;

            case VMenuItem.VMI_ACT_DELETEME:
                mipUI.messageBox(
                        ResourceBundle.getString("remove_me") + "?",
                        ResourceBundle.getString("remove_me_from") + lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_NAME) + "?",
                        mipUI.MESBOX_OKCANCEL,
                        lastCCI,
                        MSGBS_REMOVEME);
                break;

            case VMenuItem.VMI_ACT_COPY:
                ChatHistory.copyText(lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_UIN), lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_NAME));
                ContextMenu.restoreDisplayable();
                break;

            case VMenuItem.VMI_ACT_COPYNOQUOTE:
                mipUI.setClipBoardText(ChatHistory.getCurrentMessage(lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_UIN)));
                ContextMenu.restoreDisplayable();
                break;

            case VMenuItem.VMI_ACT_QUOTE:
                mipUI.writeMessage(lastCCI, mipUI.getClipBoardText());
                break;

            case VMenuItem.VMI_ACT_PROFILE:
                //saveDisplayable();
                mipUI.setLastScreen(VirtualList.getCurrent());
                MIP.setDsp(MIP.DSP_PROFILE);
                mipUI.requiestUserInfo(lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_UIN), lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_NAME));
                break;

            case VMenuItem.VMI_ACT_HISTORY:
                HistoryStorage.showHistoryList(lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_UIN), lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_NAME));
                break;

            case VMenuItem.VMI_ACT_RENAME:
                /////////////
                break;

            case VMenuItem.VMI_ACT_READXTRAZ:
                try {
                    XtrazSM.a(lastCCI.getStringValue(0), lastCCI.getIntValue(ContactListContactItem.CONTACTITEM_XSTATUS));
                    ContextMenu.restoreDisplayable();
                } catch (Exception e) {
                }
                break;

            case VMenuItem.VMI_ACT_PRLISTS:
                MIP.setDsp(MIP.DSP_PRLISTS);
                build(10, ContactList.getVisibleContactListRef());
                break;

            case VMenuItem.VMI_ACT_DCINFO:
                mipUI.setLastScreen(VirtualList.getCurrent());
                MIP.setDsp(MIP.DSP_DC);
                //saveDisplayable();
                mipUI.showClientInfo(lastCCI);
                break;

            case VMenuItem.VMI_ACT_DELETECHAT:
                build(11, VirtualList.getCurrent());
                break;

            case VMenuItem.VMI_ACT_ABOUT:
                // Display an info
                saveDisplayable();
                MIP.setDsp(MIP.DSP_ABOUT);
                mipUI.about(MIP.display.getCurrent());
                break;
            case VMenuItem.VMI_ACT_STATUS:
                MIP.setDsp(MIP.DSP_STATUS);
                MIP.SIcons.selectIcon(0);
                break;
            case VMenuItem.VMI_ACT_XTRAZ:
                MIP.setDsp(MIP.DSP_XSTATUS);
                MIP.SIcons.selectIcon(1);
                break;
            case VMenuItem.VMI_ACT_CLIENTiDMENU:
                MIP.setDsp(MIP.DSP_CLIENTID);
                MIP.SIcons.selectIcon(2);
                break;
            case VMenuItem.VMI_ACT_OPTIONS:
                MIP.setDsp(MIP.DSP_OPTIONS);
                Options.editOptions();
                build(2, ContactList.getVisibleContactListRef());
                break;
            case VMenuItem.VMI_ACT_EXIT:
                doExit(false);
                break;
            case VMenuItem.VMI_ACT_PRSTATUS:
                MIP.setDsp(MIP.DSP_PSTATUS);
                MIP.SIcons.selectIcon(3);
                break;
            //#sijapp cond.if modules_TRAFFIC is "true" #
            case VMenuItem.VMI_ACT_STATS:
                MIP.setDsp(MIP.DSP_ABOUT);    //???????????
                Traffic.trafficScreen.activate();
                break;
            //#sijapp cond.end #
            case VMenuItem.VMI_ACT_CONNECT:
                Icq.reconnect_attempts = Options.getInt(Options.OPTION_RECONNECT_NUMBER);
                ContactList.beforeConnect();
                Icq.connect();
                break;
            case VMenuItem.VMI_ACT_DISCONNECT:
                Icq.disconnect();
                Thread.yield();
                ContactList.setStatusesOffline();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                }
                build(0, ContactList.getVisibleContactListRef());
                break;
            case VMenuItem.VMI_ACT_LISTCTRL:
                build(3, ContactList.getVisibleContactListRef());
                break;
            case VMenuItem.VMI_ACT_SEARCHCONTACT:
                Search searchf = new Search(false);
                searchf.getSearchForm().activate(SearchForm.ACTIV_JUST_SHOW);
                break;
            case VMenuItem.VMI_ACT_ADDGROUP:
                status = STATUS_ADD_GROUP;
                showTextBoxForm("add_group", "group_name", null, TextField.ANY);
                break;
            case VMenuItem.VMI_ACT_RENAMEGROUP:
                status = STATUS_RENAME_GROUP;
                groupIds = mipUI.showGroupSelector("rename_group", TAG_RENAME_GROUPS, _this, mipUI.SHS_TYPE_ALL, -1);
                break;
            case VMenuItem.VMI_ACT_DELGROUP:
                groupIds = mipUI.showGroupSelector("del_group", TAG_DELETE_GROUPS, _this, mipUI.SHS_TYPE_EMPTY, -1);
                break;
            case VMenuItem.VMI_ACT_SHOWCL:
                MIP.setDsp(MIP.DSP_CL);
                ContactList.activate();
                break;
            case VMenuItem.VMI_ACT_STATUSMENU:
                MIP.setDsp(MIP.DSP_STATUSES);
                build(6, ContactList.getVisibleContactListRef());
                break;
            case VMenuItem.VMI_ACT_LOCK:
                MIP.setDsp(MIP.DSP_SPLASH);
                SplashCanvas.lock();
                break;
            case VMenuItem.VMI_ACT_ACCOUNT:
                Options.optionsForm.showOptionPart(0);
                break;
            case VMenuItem.VMI_ACT_NETWORK:
                Options.optionsForm.showOptionPart(1);
                break;
            case VMenuItem.VMI_ACT_INTERFACE:
                Options.optionsForm.showOptionPart(2);
                break;
            case VMenuItem.VMI_ACT_HOTKEYS:
                Options.optionsForm.showOptionPart(3);
                break;
            case VMenuItem.VMI_ACT_COST:
                Options.optionsForm.showOptionPart(4);
                break;
            case VMenuItem.VMI_ACT_ADVSETS:
                Options.optionsForm.showOptionPart(7);
                break;
            case VMenuItem.VMI_ACT_TIMEZONE:
                Options.optionsForm.showOptionPart(6);
                break;
            case VMenuItem.VMI_ACT_COLORSCHEME:
                build(4, ContactList.getVisibleContactListRef());
                break;
            case VMenuItem.VMI_ACT_NOTIFY:
                Options.optionsForm.showOptionPart(5);
                break;
            case VMenuItem.VMI_ACT_PROXY:
                Options.optionsForm.showOptionPart(8);
                break;
            case VMenuItem.VMI_ACT_ANTISPAM:
                Options.optionsForm.showOptionPart(9);
                break;
            case VMenuItem.VMI_ACT_AUTOSTATUS:
                Options.optionsForm.showOptionPart(10);
                break;

            case VMenuItem.VMI_COLOR_1:
                MIP.clSelect.selectColor(MIP.display.getCurrent(), _this, Options.getInt(Options.OPTION_COLOR1));
                break;
            case VMenuItem.VMI_COLOR_2:
                MIP.clSelect.selectColor(MIP.display.getCurrent(), _this, Options.getInt(Options.OPTION_COLOR2));
                break;
            case VMenuItem.VMI_COLOR_3:
                MIP.clSelect.selectColor(MIP.display.getCurrent(), _this, Options.getInt(Options.OPTION_COLOR3));
                break;
            case VMenuItem.VMI_COLOR_4:
                MIP.clSelect.selectColor(MIP.display.getCurrent(), _this, Options.getInt(Options.OPTION_COLOR4));
                break;
            case VMenuItem.VMI_COLOR_5:
                MIP.clSelect.selectColor(MIP.display.getCurrent(), _this, Options.getInt(Options.OPTION_COLOR5));
                break;
            case VMenuItem.VMI_COLOR_6:
                MIP.clSelect.selectColor(MIP.display.getCurrent(), _this, Options.getInt(Options.OPTION_COLOR6));
                break;
            case VMenuItem.VMI_COLOR_7:
                MIP.clSelect.selectColor(MIP.display.getCurrent(), _this, Options.getInt(Options.OPTION_COLOR7));
                break;
            case VMenuItem.VMI_COLOR_8:
                MIP.clSelect.selectColor(MIP.display.getCurrent(), _this, Options.getInt(Options.OPTION_COLOR8));
                break;
            case VMenuItem.VMI_COLOR_9:
                MIP.clSelect.selectColor(MIP.display.getCurrent(), _this, Options.getInt(Options.OPTION_COLOR9));
                break;
            case VMenuItem.VMI_COLOR_10:
                MIP.clSelect.selectColor(MIP.display.getCurrent(), _this, Options.getInt(Options.OPTION_COLOR10));
                break;
            case VMenuItem.VMI_HIST_CLEAR:
                build(12, VirtualList.getCurrent());
                break;
            case VMenuItem.VMI_HIST_FIND:
                MIP.getHistory().getHSL().findForm();
                break;
            case VMenuItem.VMI_HIST_INFO:
                MIP.getHistory().getHSL().Info();
                break;
            case VMenuItem.VMI_HIST_EXPORT:
                MIP.getHistory().getHSL().makeExport();
                break;
            case VMenuItem.VMI_HIST_COPY:
                MIP.getHistory().getHSL().copyCurrText();
                break;
            case VMenuItem.VMI_HIST_EXPORTALL:
                MIP.getHistory().getHSL().export(null);
                break;

            case VMenuItem.VMI_LISTS_VISIBLE:
                lastCCI.actionLists(ServerListsAction.VISIBLE_LIST);
                break;
            case VMenuItem.VMI_LISTS_INVISIBLE:
                lastCCI.actionLists(ServerListsAction.INVISIBLE_LIST);
                break;
            case VMenuItem.VMI_LISTS_IGNORE:
                lastCCI.actionLists(ServerListsAction.IGNORE_LIST);
                break;

            case VMenuItem.VMI_DELCHAT_ALL:
                ChatHistory.chatHistoryDelete(
                        lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_UIN),
                        ChatHistory.DEL_TYPE_ALL);
                ContactList.activate();
                break;
            case VMenuItem.VMI_DELCHAT_NOTCURRENT:
                ChatHistory.chatHistoryDelete(
                        lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_UIN),
                        ChatHistory.DEL_TYPE_ALL_EXCEPT_CUR);
                lastCCI.activate();
                break;
            case VMenuItem.VMI_DELCHAT_CURRENT:
                ChatHistory.chatHistoryDelete(
                        lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_UIN),
                        ChatHistory.DEL_TYPE_CURRENT);
                ContactList.activate();
                break;

            case VMenuItem.VMI_DELHIST_ALL:
                HistoryStorage.clear_all(null);
                lastCCI.activate();
                break;
            case VMenuItem.VMI_DELHIST_NOTCURRENT:
                HistoryStorage.clear_all(MIP.getHistory().getHSL().currUin);
                HistoryStorage.showHistoryList(lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_UIN), lastCCI.getStringValue(ContactListContactItem.CONTACTITEM_NAME));
                break;
            case VMenuItem.VMI_DELHIST_CURRENT:
                HistoryStorage.clearHistory(MIP.getHistory().getHSL().currUin);
                lastCCI.activate();
                break;

            case VMenuItem.VMI_SRCHSENDMSG:
                Search.sendMsg();
                break;
            case VMenuItem.VMI_SRCHINFO:
                Search.viewProfile();
                break;
            case VMenuItem.VMI_ADDCONTACT:
                Search.addContact();
                break;

            case VMenuItem.VMI_DIRBR_SELECT:
                MIP.getHistory().getHSL().dirSelected();
                break;
//                     case VMenuItem.VMI_DIRBR_OPEN:
//                        MIP.getHistory().getHSL().dirSelected();
//                        break;
            case VMenuItem.VMI_DIRBR_NEWDIR:
                MIP.getHistory().getHSL().newDir();
                break;

            case VMenuItem.VMI_UPLOADURL:
                try {
                    Icq.uploadLink(smapeURL);
                } catch (Exception e) {
                    System.out.println("exception.. urlencode");
                }
                ContextMenu.restoreDisplayable();
                break;

            case VMenuItem.VMI_ACT_EDITPROFILE:
                if (Icq.isConnected()) {
                    RequestInfoAction act4 = new RequestInfoAction(Options.getString(Options.OPTION_UIN), Options.getString(Options.OPTION_NICKNAME));
                    try {
                        Icq.requestAction(act4);
                    } catch (mipException e) {
                        mipException.handleException(e);
                        if (e.isCritical()) {
                            return;
                        }
                    }
                }
                break;
            
            case VMenuItem.VMI_TEST:
                MIP.setDsp(MIP.DSP_CL);
                //ContactListContactItem.showPopupWindow("206817631", "poofeg", "Hello World");
                VirtualList.getCurrent().showMesBox("poofeg", "Hello World", VirtualList.MB_OK);
                VirtualList.getCurrent().showMesBox("poofeg", "The following\n software may be included in this product: CS CodeViewer v1.0; Use of any of this software is governed by the terms of the license below: Copyright 1999 by CoolServlets.com.Any errors or suggested improvements to this class can be reported as instructed on CoolServlets.com. We hope you enjoy this program... your comments will encourage further development! This software is distributed under the terms of the BSD License. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:",
                        VirtualList.MB_MESSAGE);
                break;
            
            case VMenuItem.VMI_ACT_THEMES:
                build(16, ContactList.getVisibleContactListRef());
                break;
                
            case VMenuItem.VMI_STATUS_ONLINE:
            case VMenuItem.VMI_STATUS_CHAT:
            case VMenuItem.VMI_STATUS_OCCUPIED:
            case VMenuItem.VMI_STATUS_DND:
            case VMenuItem.VMI_STATUS_AWAY:
            case VMenuItem.VMI_STATUS_NA:
            case VMenuItem.VMI_STATUS_EVIL:
            case VMenuItem.VMI_STATUS_DEPRESS:
            case VMenuItem.VMI_STATUS_HOME:
            case VMenuItem.VMI_STATUS_WORK:
            case VMenuItem.VMI_STATUS_LUNCH:
            case VMenuItem.VMI_STATUS_INVISIBLE:
            case VMenuItem.VMI_STATUS_INVIS_ALL:
                MIP.SIcons.selectMainStatus(itemAction);
                build(0, ContactList.getVisibleContactListRef());
                break;      
        }
        
        if ((itemAction >= VMenuItem.VMI_LIST_THEMES) & (itemAction < VMenuItem.VMI_LIST_THEMES + 64)) {
            //String theme = "/" + (String)Options.themesList.elementAt(itemAction - VMenuItem.VMI_LIST_THEMES) + ".tsf";
            System.out.println("Theme selected /" + (String)Options.themesList.elementAt(itemAction - VMenuItem.VMI_LIST_THEMES) + ".tsf");
            try {
                Options.loadTheme(new String("/" + (String)Options.themesList.elementAt(itemAction - VMenuItem.VMI_LIST_THEMES) + ".tsf"), false);
            }
            catch (Exception e) {e.printStackTrace();}
            build(2, ContactList.getVisibleContactListRef());
        }
    }
}