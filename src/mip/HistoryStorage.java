//#sijapp cond.if modules_HISTORY is "true" #

package mip;

import java.util.*;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Font;
import javax.microedition.rms.RecordStore;
import mip.util.ResourceBundle;
import java.io.*;
import javax.microedition.lcdui.*;
//import javax.microedition.midlet.MIDlet;

import DrawControls.ListItem;
import DrawControls.VirtualList;
import DrawControls.TextList;
import DrawControls.VirtualListCommands;

import mip.comm.Util;
//#sijapp cond.if target="SIEMENS2" | target="MOTOROLA" | target="MIDP2"#
import javax.microedition.io.Connector;
//#sijapp cond.if target is "SIEMENS2"#
import com.siemens.mp.io.file.FileConnection;
import com.siemens.mp.io.file.FileSystemRegistry;
//#sijapp cond.else#
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
//#sijapp cond.end#
import mip.ContactListContactItem;
//#sijapp cond.end#
// Class to cache one line in messages list
// All fields are public to easy and fast access
class CachedRecord
{
        String shortText, text, date, from;
	byte type; // 0 - incoming message, 1 - outgoing message
//	boolean contains_url;
}

class HistorysList extends VirtualList implements VirtualListCommands
{
    // Constructor
    public HistorysList()
    {
        super(null);
    }
        
    public void onKeyPress(VirtualList sender, int keyCode, int type)
    {
        return;
    }
    
    public void onItemSelected(VirtualList sender)
    {
        return;
    }
    
    public void onCursorMove(VirtualList sender)
    {
        return;
    }
    
    protected void get(int index, ListItem item)
    {
        return;
    }
    
    protected int getSize()
    {
        return 0;
    }
}

// Visual messages history list
class HistoryStorageList extends VirtualList
                        implements CommandListener, VirtualListCommands
                        //#sijapp cond.if target="SIEMENS2" | target="MOTOROLA" | target="MIDP2"#
                        , Runnable
                        //#sijapp cond.end#
{
	static TextList messText;
	
	private final static int SELECTOR_DEL_HISTORY = 2;
	
	// list UIN
	public static String currUin  = new String(),
	                     currName = new String();
	
	// Controls for finding text
	private static Form frmFind;
	private static Command cmdFindOk;
	private static Command cmdFindCancel;
	private static TextField tfldFind;
	private static ChoiceGroup chsFind;
	
	// Constructor
	public HistoryStorageList()
	{
		super(null);
                MIP.setDsp(MIP.DSP_HISTORY);
		setVLCommands(this);
		mipUI.setColorScheme(this);
	}
	
	// VirtualList command impl.
	public void onCursorMove(VirtualList sender)
	{
            // user select some history storage line
            if (sender == this)
            {
                CachedRecord record = HistoryStorage.getCachedRecord(currUin, getCurrIndex());
                
                if (record == null) return;
                setCaption(record.from + " " + record.date);
            }
        }
        
        // VirtualList command impl.
        public void onKeyPress(VirtualList sender, int keyCode, int type)
	{
            if ((sender == messText) && (type == VirtualList.KEY_PRESSED))
            {
                try
                {
                    switch ( getGameAction(keyCode) )
                    {
                        case Canvas.LEFT:
                            moveInList(-1);
                            break;
                        case Canvas.RIGHT:
                            moveInList(1);
                            break;
                        case Canvas.FIRE:
                            showMessText();
                            break;
                    }
                }
                catch (Exception e) {}
            }
        }
        
        // Moves on next/previous message in list and shows message text
        private void moveInList(int offset)
        {
            moveCursor(offset, false);
            showMessText();
        }
        
        // VirtualList command impl.
        public void onItemSelected(VirtualList sender)
        {
                if (sender == this)
                {
                    showMessText();
                }
        }
        
	//#sijapp cond.if target="MOTOROLA"|target="MIDP2"|target="SIEMENS2"#
	private Object fileSystem; 
	private boolean cp1251;
	private String exportUin;
	private String directory;
	private TextList messTextList;

        public void export(String uin)
        {
            exportUin = uin;
            try
            {
                FileSystem2 fb = new FileSystem2();
                fb.browse(null, true);
                fileSystem = fb;
            } catch (Exception e)
            {
            }
        }
        
        private static final int MODE_EXPORT_HISTORY = 1;
        private static final int MODE_SHOW_EH_MESSAGE = 2;
        private int currentMode;
        private String currMessage;
        
        public void run()
        {
            switch (currentMode)
            {
                case MODE_EXPORT_HISTORY:
                    System.out.println("Start history export!");
                    if (exportUin == null) startExport(null);
                    else startExport(new ContactListContactItem[]{ ContactList.getItembyUIN(exportUin) });
                    break;
		
                case MODE_SHOW_EH_MESSAGE:
                    messTextList = new TextList(currName);
                    mipUI.setColorScheme(messTextList);
                    messTextList.setFontSize(Font.SIZE_LARGE);
                    //messTextList.setMode(VirtualList.MODE_TEXT);
                    messTextList.addBigText(currMessage, messTextList.getTextColor(), Font.STYLE_PLAIN, -1);

                    /////
                    messTextList.activate(MIP.display);
                    break;
            }
        }

        private void exportUinToStream(ContactListContactItem item, OutputStream os) throws IOException
        {
            CachedRecord record;
            String uin = item.getStringValue(ContactListContactItem.CONTACTITEM_UIN);
            int max = HistoryStorage.getRecordCount(uin);
            if (max > 0)
            {
                String nick = (item.getStringValue(
                        ContactListContactItem.CONTACTITEM_NAME).length() > 0) ? item
                        .getStringValue(ContactListContactItem.CONTACTITEM_NAME)
                        : uin;
                SplashCanvas.setMessage(nick);
                SplashCanvas.setProgress(0);
                StringBuffer str_buf = new StringBuffer().append("\r\n").append('\t')
                        .append(ResourceBundle.getString("message_history_with"))
                        .append(nick).append(" (").append(uin).append(")\r\n").append('\t')
                        .append(ResourceBundle.getString("export_date"))
                        .append(Util.getDateString(false, true)).append("\r\n\r\n");
                os.write(Util.stringToByteArray(str_buf.toString(), !cp1251));
                for (int i = 0; i < max; i++)
                {
                    record = HistoryStorage.getRecord(uin, i);
                    os.write(Util.stringToByteArray(" " + ((record.type == 0) ? nick : Options
                            .getString(Options.OPTION_NICKNAME)) + " (" + record.date + "):\r\n", !cp1251));
                    String curr_msg_text = record.text.trim();
                    StringBuffer msg_str_buf = new StringBuffer(curr_msg_text.length());
                    for (int k = 0; k < curr_msg_text.length(); k++)
                    {
                        char cc = curr_msg_text.charAt(k);
                        char nc;
                        switch (cc)
                        {
                            case '\n':
                                nc = curr_msg_text.charAt(k++);
                                msg_str_buf = msg_str_buf.append('\r').append('\n');
                                if ((nc != '\r') & (nc != '\n'))
                                    msg_str_buf = msg_str_buf.append(nc);
                                break;
                            case '\r':
                                nc = curr_msg_text.charAt(k++);
                                msg_str_buf = msg_str_buf.append('\r').append('\n');
                                if ((nc != '\n') & (nc != '\r'))
                                    msg_str_buf = msg_str_buf.append(nc);
                                break;
                            default:
                                msg_str_buf = msg_str_buf.append(cc);
                        }
                    }
                    msg_str_buf = msg_str_buf.append('\r').append('\n');
                    os.write(Util.stringToByteArray(msg_str_buf.toString(), !cp1251));
                    os.flush();
                    SplashCanvas.setProgress((100 * i) / max);
                }
            }
        }

        private void exportUinToFile(ContactListContactItem item, String filename) throws IOException
        {
            if (HistoryStorage.getRecordCount(item.getStringValue(ContactListContactItem.CONTACTITEM_UIN)) > 0)
            {
                FileSystem2 fs = null;
                OutputStream os = null;
                try
                {
                    fs = (FileSystem2)fileSystem;
                    fs.openFile(filename);
                    os = fs.openOutputStream();
                    if (!cp1251) os.write(new byte[]{ (byte) 0xef, (byte) 0xbb, (byte) 0xbf });
                    exportUinToStream(item, os);
                }
                finally
                {
                    if (os != null) os.close();
                    if (fs != null) fs.close();
                }
            }
        }

	public void startExport(ContactListContactItem[] citems)
	{
            cp1251 = Options.getBoolean(Options.OPTION_CP1251_HACK);
            SplashCanvas.setMessage(ResourceBundle.getString("exporting", ResourceBundle.FLAG_ELLIPSIS));
            SplashCanvas.setProgress(0);
            MIP.display.setCurrent(mip.MIP.getSplashCanvasRef());
            if (citems == null)
                citems = ContactList.getContactItems();
            try
            {
                for (int i = 0; i < citems.length; i++)
                    exportUinToFile(
                            citems[i],
                            directory
                            + "smaper_hist_"
                            + citems[i].getStringValue(ContactListContactItem.CONTACTITEM_UIN)
                            + ".txt");
                ((FileSystem2)fileSystem).close();
                currMessage = ResourceBundle.getString("export_complete");
            }
            catch (Exception e)
            {
                currMessage = e.toString()+e.getMessage();
            }
            fileSystem = null;
            
            System.out.println("currMessage="+currMessage);
            
            // Show success (or error message) in main thread
            currentMode = MODE_SHOW_EH_MESSAGE;
            MIP.display.callSerially(this);
        }
	//#sijapp cond.end#

	void makeExport()
	{
		export(currUin);
	}
        
	public void commandAction(Command c, Displayable d)
	{		
		// user select ok command in find screen
		if (c == cmdFindOk)
		{
			HistoryStorage.find
			(
				currUin,
				tfldFind.getString(),
				chsFind.isSelected(1),
				chsFind.isSelected(0)
			);
		}
		
		else if (c == cmdFindCancel)
		{
			this.activate(MIP.display);
		}
		
	} // end 'commandAction'
	
        public void Info()
        {  // info
			RecordStore rs = HistoryStorage.getRS();
			
			try
			{
				Alert alert = new Alert
				(
					ResourceBundle.getString("history_info"),
					(new StringBuffer())
						.append(ResourceBundle.getString("hist_cur")).append(": ").append(getSize()).append("\n")
						.append(ResourceBundle.getString("hist_size")).append(": ").append(rs.getSize()/1024).append("\n")
						.append(ResourceBundle.getString("hist_avail")).append(": ").append(rs.getSizeAvailable()/1024).append("\n")
						.toString(),
					null,
					AlertType.INFO 
				);
				alert.setTimeout(Alert.FOREVER);
				MIP.display.setCurrent(alert);
			}
			catch (Exception e)
			{
			}            
        }
        void copyCurrText()
		{
			int index = getCurrIndex();
			if (index == -1) return;
			CachedRecord record = HistoryStorage.getCachedRecord(currUin, index);
			if (record == null) return;
			mipUI.setClipBoardText
			(
				(record.type == 0),
				record.date,
				record.from,
				record.text
			);
		}
	void findForm()
	{
		if (frmFind == null)
			{
				frmFind = new Form( ResourceBundle.getString("find") );
				cmdFindOk = new Command(ResourceBundle.getString("find"),   Command.ITEM, 1);
				cmdFindCancel = new Command(ResourceBundle.getString("back"),   Command.BACK, 2);
				tfldFind = new TextField
				(
						ResourceBundle.getString("text_to_find"),
						"",
						64,
						TextField.ANY
				);
				
				chsFind = new ChoiceGroup(ResourceBundle.getString("options"), Choice.MULTIPLE);
				chsFind.append(ResourceBundle.getString("find_backwards"), null);
				chsFind.append(ResourceBundle.getString("find_case_sensitiv"), null);
				chsFind.setSelectedIndex(0, true);
				
				frmFind.addCommand(cmdFindOk);
				frmFind.addCommand(cmdFindCancel);
				frmFind.append(tfldFind);
				frmFind.append(chsFind);
				frmFind.setCommandListener(this);
			}
			MIP.display.setCurrent(frmFind);
	}
	// Show text message of current message of messages list
	void showMessText()
	{
		if (this.getCurrIndex() >= this.getSize()) return;
		if (messText == null)
		{
			messText = new TextList(null);
			messText.setCursorMode(TextList.SEL_NONE);
//			messText.setVLCommands(this);
			mipUI.setColorScheme(messText);
		}
		
		CachedRecord record = HistoryStorage.getRecord(currUin, this.getCurrIndex()); 
		
		messText.clear();
		messText.addBigText(record.date+":", messText.getTextColor(), Font.STYLE_BOLD, -1);
		messText.doCRLF(-1);
		
		//#sijapp cond.if modules_SMILES is "true" #
		Emotions.addTextWithEmotions(messText, record.text, Font.STYLE_PLAIN, messText.getTextColor(), -1, -1);
		//#sijapp cond.else#
		messText.addBigText(record.text, messText.getTextColor(), Font.STYLE_PLAIN, -1);
		//#sijapp cond.end#
		
		messText.doCRLF(-1);
		messText.setCaption(record.from);
		MIP.setDsp(MIP.DSP_HISTORYMSG);
		messText.activate(MIP.display);
		messText.repaint();
	}
	
	// returns UIN of list
	static String getCurrUin()
	{
		return currUin;
	}
	
	// sets UIN for list
	static void setCurrUin(String currUin_, String currName_)
	{
		currUin  = currUin_;
		currName = currName_;
	}
	
	// returns size of messages history list
	protected int getSize()
	{
		return HistoryStorage.getRecordCount(currUin);
	}
	  
	// returns messages history list item data
	protected void get(int index, ListItem item)
	{
		CachedRecord record = HistoryStorage.getCachedRecord(currUin, index);
		if (record == null) return;
		item.text = record.shortText;
		item.color = (record.type == 0) ? getTextColor() : Options.getInt(Options.OPTION_COLOR5);
	}
        
        public void activateFS() {
            FileSystem2 fs = (FileSystem2)fileSystem;
            fs.acvivate();
        }
        
        public void dirSelected()
        {
            FileSystem2 fs = (FileSystem2)fileSystem;
            if(fs.saveHere())
            {
                directory = fs.getValue();
                currentMode = MODE_EXPORT_HISTORY;
                (new Thread(this)).start();                
            }
        }
        
        public void newDir()
        {
            FileSystem2 fs = (FileSystem2)fileSystem;
            fs.newDir();
        }        
}

// History storage implementation
public class HistoryStorage
{
	//===================================//
	//                                   //
	//    Data storage implementation    //
	//                                   //
	//===================================//
	
	static final public int CLEAR_EACH_DAY   = 0;
	static final public int CLEAR_EACH_WEEK  = 1;
	static final public int CLEAR_EACH_MONTH = 2;
	
	static final private int VERSION = 1;
	
	static final private String prefix = "hist";
	
	private static RecordStore recordStore;
	private static HistoryStorageList list;
	private static HistorysList hlist;
	private static String currCacheUin = new String();
	private static Hashtable cachedRecords;
	
	final static private int TEXT_START_INDEX = 1;
	
	public HistoryStorage()
	{
		try
		{
			RecordStore.deleteRecordStore("history");
		}
		catch (Exception e)
		{
			
		}
	}
	
	public static HistoryStorageList getHSL()
	{
		return list;
	}
	

	// Add message text to contact history
	static public void addText
	(
		String uin,  // uin sended text  
		String text, // text to save
		byte type,   // type of message 0 - incoming, 1 - outgouing
		String from, // text sender
		long time    // date of message
	)
	{
		byte[] buffer, textData;
		int textLen;
		boolean lastLine = false;
		
		RecordStore recordStore = null;
		
		if (list != null)
		{
			if (list.getSize() == 0) lastLine = true;
			else if (list.getCurrIndex() == (list.getSize()-1)) lastLine = true;
		}
		
		boolean isCurrenty = currCacheUin.equals(uin);
		
		try
		{
			recordStore = isCurrenty ? HistoryStorage.recordStore : RecordStore.openRecordStore(getRSName(uin), true); 
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
			DataOutputStream das = new DataOutputStream(baos);
			das.writeUTF(from);
			das.writeUTF(text);
			das.writeUTF(Util.getDateString(false, time, true));
			textData = baos.toByteArray();
			textLen = textData.length;
			
			buffer = new byte[textLen+TEXT_START_INDEX];
			Util.putDWord(buffer, 0, recordStore.getNextRecordID());
			buffer[0] = type;
			System.arraycopy(textData, 0, buffer, TEXT_START_INDEX, textLen);
			
			recordStore.addRecord(buffer, 0, buffer.length);
			
			if (!isCurrenty && (recordStore != null))
			{
				recordStore.closeRecordStore();
				recordStore = null;
			}
		}
		catch (Exception e) 
		{
		}
		
		if ( (list != null) && HistoryStorageList.getCurrUin().equals(uin) )
		{
			list.repaint();
			if (lastLine) list.setCurrentItem(list.getSize()-1);
		}
	}
	
	// Returns reference for record store
	static RecordStore getRS()
	{
		return recordStore;
	}
	
	// Returns record store name for UIN
	static private String getRSName(String uin)
	{
		return prefix+uin;
	}
	
	// Opens record store for UIN
	static private void openUINRecords(String uin)
	{
		if (currCacheUin.equals(uin)) return;
		
		try
		{
			if (recordStore != null)
			{
				recordStore.closeRecordStore();
				recordStore = null;
				System.gc();
			}
			recordStore = RecordStore.openRecordStore(getRSName(uin), true);
		}
		catch (Exception e)
		{
			recordStore = null;
			return;
		}
		currCacheUin = uin;
		
		if (cachedRecords == null) cachedRecords = new Hashtable();
	}
	
	// Returns record count for UIN
	static public int getRecordCount(String uin)
	{
		openUINRecords(uin);
		int result;
		try
		{
			result = recordStore.getNumRecords();
		}
		catch (Exception e)
		{
			result = 0;			
		}
		
		return result; 
	}
	
	// Returns full data of stored message
	static public CachedRecord getRecord(String uin, int recNo)
	{
		openUINRecords(uin);
		byte[] data;
		CachedRecord result = new CachedRecord();
		
		try
		{
			data = recordStore.getRecord(recNo+1);
			result.type = data[0];
			ByteArrayInputStream bais = new ByteArrayInputStream(data, TEXT_START_INDEX, data.length-TEXT_START_INDEX);
			DataInputStream dis = new DataInputStream(bais);
			result.from = dis.readUTF();
			result.text = dis.readUTF();
			result.date = dis.readUTF();
		}
		catch (Exception e)
		{
			result.text = result.date = result.from = "error"; 
			return null;
		}
		
		return result;
	}
	
	// returns cached short text of the message for history list
	static public CachedRecord getCachedRecord(String uin, int recNo)
	{
		int maxLen = 20;
		CachedRecord cachedRec = (CachedRecord)cachedRecords.get(new Integer(recNo)); 
		if (cachedRec != null) return cachedRec;
		
		cachedRec = getRecord(uin, recNo);
		if (cachedRec == null) return null;
		if (cachedRec.text.length() > maxLen) 
			cachedRec.shortText = cachedRec.text.substring(0, maxLen)+"...";
		else
			cachedRec.shortText = cachedRec.text;
		
        cachedRec.shortText = cachedRec.shortText.replace('\n', ' ');
        cachedRec.shortText = cachedRec.shortText.replace('\r', ' ');
        
		cachedRecords.put(new Integer(recNo), cachedRec);
		
		return cachedRec;
	}
	
        static public void showHistorysList(String uin, String nick)
        {
            if (hlist == null)
            {
                String caption = ResourceBundle.getString("history");
                hlist = new HistorysList();
                hlist.setCaption(caption);
            }
        }
        
	// Shows history list on mobile phone screen
	static public void showHistoryList(String uin, String nick)
	{
		if (list == null)
		{
			String caption = ResourceBundle.getString("history");
			list = new HistoryStorageList();
			list.setCaption(caption);
		}
		
		HistoryStorageList.setCurrUin(uin, nick);
		
		list.lock();
		
		if (list.getSize() != 0) list.setCurrentItem(list.getSize()-1);
		list.unlock();
		MIP.setDsp(MIP.DSP_HISTORY);
                list.setSoftNames("back", "comlist");
		list.activate(MIP.display);
	}
	
	// Clears messages history for UIN
	static synchronized public void clearHistory(String uin)
	{
		try
		{
			openUINRecords(uin);
			recordStore.closeRecordStore();
			recordStore = null;
			System.gc();
			RecordStore.deleteRecordStore( getRSName(uin) );
			if (cachedRecords != null) cachedRecords.clear();
			currCacheUin = new String();
		}
		catch (Exception e)
		{
		}
	}
	
	// Clears cache before hiding history list
	static public void clearCache()
	{
		if (cachedRecords != null)
		{
			cachedRecords.clear();
			cachedRecords = null;
		}
		
		list = null;
		
		currCacheUin = new String();
	}
	
	// Sets color scheme for history UI controls
	static public void setColorScheme()
	{
		if (list != null)
		{
			mipUI.setColorScheme(list);
			if (HistoryStorageList.messText != null) 
				mipUI.setColorScheme(HistoryStorageList.messText);
		}
	}
                
	static synchronized private boolean find_intern(String uin, String text, boolean case_sens, boolean back)
	{
		int index = list.getCurrIndex();
		if ((index < 0) || (index >= list.getSize())) return false;
		if (!case_sens) text = text.toLowerCase();
		int size = getRecordCount(uin);
		
		for (;;)
		{
			if ((index < 0) || (index >= size)) break;
			CachedRecord record = getRecord(uin, index);
			String search_text = 
				case_sens ? record.text : record.text.toLowerCase();
			if (search_text.indexOf(text) != -1)

			{
				list.setCurrentItem(index);
                                list.setSoftNames("back", "select");
				list.activate(MIP.display);
				return true;
			}
			
			if (back) index--;
			else index++;
		}
		return false;
	}
	
	// find text
	static void find(String uin, String text, boolean case_sens, boolean back)
	{
		if (list == null) return;
		boolean result = find_intern(uin, text, case_sens, back);
		if (result == true) return;
		
		Alert alert = new Alert
		(
			ResourceBundle.getString("find"),
			(new StringBuffer())
				.append(text)
			    .append("\n")
				.append(ResourceBundle.getString("not_found"))
				.toString(),
			null,
			AlertType.INFO
		);
		
		alert.setTimeout(Alert.FOREVER);
                list.setSoftNames("back", "select");
		list.activate(MIP.display, alert);
	}
	
	// Clears all records for all uins
	static synchronized void clear_all(String except)
	{
		String exceptRMS = (except == null) ? null : getRSName(except);
		
		try
		{
			if (recordStore != null)
			{
				recordStore.closeRecordStore();
				recordStore = null;
				
				currCacheUin = new String();
			}
			
			String[] stores = RecordStore.listRecordStores();
			
			for (int i = 0; i < stores.length; i++)
			{
				String store = stores[i];
				if (store.indexOf(prefix) == -1) continue;
				if (exceptRMS != null) if ( exceptRMS.equals(store) ) continue;
				RecordStore.deleteRecordStore(store);
			}
		}
		catch (Exception e)
		{

		}
		System.gc();
	}
}

//#sijapp cond.end#

