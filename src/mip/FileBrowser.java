//#sijapp cond.if (target="MOTOROLA"|target="MIDP2"|target="SIEMENS2")&(modules_FILES="true"|modules_HISTORY="true")#
package mip;

//#sijapp cond.if target="MIDP2"|target="MOTOROLA"#
import javax.microedition.io.file.*;
import javax.microedition.io.*;
//#sijapp cond.elseif target="SIEMENS2"#
//#import com.siemens.mp.io.file.FileConnection;
//#import com.siemens.mp.io.file.FileSystemRegistry;
//#sijapp cond.end#
import javax.microedition.io.Connector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import mip.util.ResourceBundle;
import java.util.Enumeration;
import java.util.Vector;
import java.io.*;
import DrawControls.*;

//#sijapp cond.if target="MOTOROLA"#

interface MotoFileSystem
{
	public void open(String fileName) throws IOException;
	public void enumRoots(Vector result) throws IOException;
	public void enumFiles(Vector result, String fileName) throws IOException;
	public void closeFileConn() throws IOException;
	public OutputStream openOutputStream() throws IOException;
	public InputStream openInputStream() throws IOException;
	public long fileSize() throws IOException;
        public void mkdir(String dir) throws IOException;
}

class MotoFileSystemHelper implements MotoFileSystem
{
	private com.motorola.io.FileConnection fileConn;
	
	public void open(String fileName) throws IOException
	{
		fileConn = (com.motorola.io.FileConnection)Connector.open("file://" + fileName);
	}
	
	public void enumRoots(Vector result) throws IOException
	{
		String[] roots = com.motorola.io.FileSystemRegistry.listRoots();
		for (int i = 0; i < roots.length; i++) result.addElement(roots[i]);
	}
	
	public void enumFiles(Vector result, String fileName) throws IOException
	{
		try
		{
			com.motorola.io.FileConnection fileconn = (com.motorola.io.FileConnection)Connector.open("file://" + fileName);
			String[] list = fileconn.list();
			fileconn.close();
			result.addElement(FileSystem2.PARENT_DIRECTORY);
			for (int i = 0; i < list.length; i++) result.addElement(list[i]);
		}
		catch (Exception e) {}
	}
	
	public void closeFileConn() throws IOException
	{
		if (fileConn == null) return;
		fileConn.close();
		fileConn = null;
	}
	
	public OutputStream openOutputStream() throws IOException
	{
		if ( !fileConn.exists() ) fileConn.create();
		return fileConn.openOutputStream();
	}
	
	public InputStream openInputStream() throws IOException
	{
		return fileConn.openInputStream();
	}
	
	public long fileSize() throws IOException
	{
		return (fileConn == null) ? -1 : fileConn.fileSize();
	}
        
        public void mkdir(String dir) throws IOException
	{

	}
}

class StdFileSystemHelper implements MotoFileSystem
{
	private FileConnection fileConn;
	
	public void open(String fileName) throws IOException
	{
		fileConn = (FileConnection)Connector.open("file://" + fileName);
	}
	
	public void enumRoots(Vector result)
	{
		Enumeration roots = FileSystemRegistry.listRoots();
		while (roots.hasMoreElements()) result.addElement(roots.nextElement());
	}
	
	public void enumFiles(Vector result, String fileName) throws IOException
	{
		FileConnection fileconn = (FileConnection) Connector.open("file://" + fileName);
		Enumeration list = fileconn.list();
		fileconn.close();
		
		result.addElement(FileSystem2.PARENT_DIRECTORY);
		while (list.hasMoreElements())
		{
			String filename = (String) list.nextElement();
			result.addElement(filename);
		}
	}
	
	public void closeFileConn() throws IOException
	{
		if (fileConn == null) return;
		fileConn.close();
		fileConn = null;
	}
	
	public OutputStream openOutputStream() throws IOException
	{
		if ( !fileConn.exists() ) fileConn.create();
		return fileConn.openOutputStream();
	}
	
	public InputStream openInputStream() throws IOException
	{
		return fileConn.openInputStream();
	}
	
	public long fileSize() throws IOException
	{
		return (fileConn == null) ? -1 : fileConn.fileSize();
	}
	
        public void mkdir(String dir) throws IOException
	{
            if(fileConn != null)
            {
                FileConnection fileConn = (FileConnection) Connector.open(dir);
                fileConn.mkdir();
            }
	}
}
//#sijapp cond.end#

class FileSystemList extends TextList implements  VirtualListCommands {

    private FileSystem2 parent;
    
    public FileSystemList(FileSystem2 _parent)
    {
        super(null);
        parent = _parent;
        setVLCommands(this);
        mipUI.setColorScheme(this);
    }
        
    public void onKeyPress(VirtualList sender, int keyCode, int type) {
        try {
            switch (getGameAction(keyCode)) {
                case Canvas.LEFT:
                    parent.goBack();
                    break;
                case Canvas.RIGHT:
                case Canvas.FIRE:
                    parent.itemSelected();
                    break;
            }
        } catch (Exception e) {}
    }
    
    public void onItemSelected(VirtualList sender)
    {
        parent.itemSelected();
    }
    
    public void onCursorMove(VirtualList sender)
    {
        //setCaption(getCurrText(0, false));
    }
}

class FileSystem2 implements CommandListener, Runnable
{
	private FileSystemList list;
	final static String ROOT_DIRECTORY = "/";
	final static String PARENT_DIRECTORY = "../";
	private ImageList imageList;
	private final static int MODE_SCAN_DIRECTORY = 1;
	private final static int MODE_SHOW_RESULTS   = 2;
	private int currentMode;
	private String currentDir;
	private String selectedItem;
	private Vector items;
	private boolean onlyDirs;
        private TextBox newDirTextBox;
        private FileSystem2 _this;
	
	//#sijapp cond.if target="MOTOROLA"#
	MotoFileSystem motoFileConnection;
	//#sijapp cond.else#
	private FileConnection fileConnection;
	//#sijapp cond.end#
	
        public FileSystem2() {
            //#sijapp cond.if target="MOTOROLA"#
            try {
                Class.forName("javax.microedition.io.file.FileConnection");
                motoFileConnection = new StdFileSystemHelper();
            } catch (ClassNotFoundException cnfe) {
                motoFileConnection = new MotoFileSystemHelper();
            }
            //#sijapp cond.end#
            
            imageList = new ImageList();
            try {
                imageList.load("/fs.png", -1, -1, -1);
            } catch (java.io.IOException e) {}
            
            items = new Vector();
            list = new FileSystemList(this);
        }
	
	public void browse(String root, boolean onlyDirs)
	{
		this.onlyDirs = onlyDirs;
		
		if (root == null) root = ROOT_DIRECTORY;
		
		mipUI.setColorScheme(list);
		list.activate(MIP.display);
		showFolder(root);
	}

	private void showFolder(String path)
	{
		if (currentMode == MODE_SCAN_DIRECTORY) return;
		currentMode = MODE_SCAN_DIRECTORY;
		currentDir = path;            
                
		new Thread(this).start();
	}
	
	//#sijapp cond.if target="MOTOROLA"#
	private void enumRootsMoto(Vector result) throws IOException
	{
		motoFileConnection.enumRoots(result);
	}
	
	private void enumFilesMoto(Vector result, String fileName) throws IOException
	{
		motoFileConnection.enumFiles(result, fileName);
	}
	
	//#sijapp cond.else#
	
	private void enumRootsStd(Vector result)
	{
		Enumeration roots = FileSystemRegistry.listRoots();
		while (roots.hasMoreElements()) result.addElement(roots.nextElement());
	}
	
	private void enumFilesStd(Vector result, String fileName) throws IOException
	{
		FileConnection fileconn;
		//#sijapp cond.if target="SIEMENS2"#
		fileconn = (FileConnection) Connector.open("file://" + fileName);
		//#sijapp cond.else#
		fileconn = (FileConnection) Connector.open("file://localhost"+fileName);
		//#sijapp cond.end#

		Enumeration list = fileconn.list();
		fileconn.close();
		
		result.addElement(PARENT_DIRECTORY);
		while (list.hasMoreElements())
		{
			String filename = (String) list.nextElement();
			result.addElement(filename);
		}
	}
	
	//#sijapp cond.end#
	
	public void run() 
	{
		switch (currentMode)
		{
		case MODE_SCAN_DIRECTORY:
			try
			{
				items.removeAllElements();
				if (currentDir.equals(ROOT_DIRECTORY))
				{
					//#sijapp cond.if target="MOTOROLA"#
					enumRootsMoto(items);
					//#sijapp cond.else#
					enumRootsStd(items);
					//#sijapp cond.end#
				}
				else
				{
					//#sijapp cond.if target="MOTOROLA"#
					enumFilesMoto(items, currentDir);
					//#sijapp cond.else#
					enumFilesStd(items, currentDir);
					//#sijapp cond.end#
				}
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			currentMode = MODE_SHOW_RESULTS;
			MIP.display.callSerially(this);
			break;
			
		case MODE_SHOW_RESULTS:
			MIP.setDsp(MIP.DSP_DIRBROWSER);
                        list.setSoftNames("back", "select");
                        
			// Show last path element at caption 
			int index1 = -1, index2 = -1;
			for (int i = currentDir.length()-1; i >= 0; i--)
			{
				boolean isDelim = (currentDir.charAt(i) == '/');
				if (isDelim)
				{
					if (index1 == -1) index1 = i;
					else if (index2 == -1) index2 = i;
					else break;
				}
			}
			
			list.setCaption
			(
				(index1 != -1) && (index2 != -1) ? 
				currentDir.substring(index2+1, index1) : 
				"Root"
			);

			// Show directory content
			list.clear();
			
			// Show dirs
			for (int i = 0; i < items.size(); i++)
			{
				String itemText = (String)items.elementAt(i);
				if (!itemText.endsWith("/")) continue;
                                //list.
				mipUI.addTextListItem(list, itemText, imageList.elementAt(0), i, true);
			}
			
			// Show files
			if (!onlyDirs) for (int i = 0; i < items.size(); i++)
			{
				String itemText = (String)items.elementAt(i);
				if (itemText.endsWith("/")) continue;
				mipUI.addTextListItem(list, itemText, imageList.elementAt(1), i, true);
                        }
			break;
		}
	}
  
        public void goBack() {
            String parentDir = null;
            for (int i = currentDir.length()-2; i >= 0; i--) {
                if (currentDir.charAt(i) == '/') {
                    parentDir = currentDir.substring(0, i+1);
                    break;
                }
            }
            if (parentDir != null) showFolder(parentDir);
            return;
        }
        
        public void itemSelected() {
            if (list.getCurrIndex() < 0) return;
            String itemText = list.getCurrText(0, false);
            if (itemText.equals(PARENT_DIRECTORY)) {
                goBack();
            } else if (itemText.endsWith("/")) {
                showFolder(currentDir+itemText);
            }
            return;
        }
        
        public boolean saveHere() {
            if (!currentDir.equals(ROOT_DIRECTORY)) {
                selectedItem = currentDir;
                return true;
            } else return false;
        }
        
        public void acvivate() {
            MIP.setDsp(MIP.DSP_DIRBROWSER);
            list.setSoftNames("back", "select");
            currentMode = MODE_SHOW_RESULTS;
            showFolder(currentDir);
            list.activate(MIP.display);
        }
        
        public void newDir() {
            newDirTextBox = new TextBox(ResourceBundle.getString("new_dir"), null, 30, TextField.ANY | TextField.INITIAL_CAPS_SENTENCE);
            newDirTextBox.addCommand(mipUI.cmdOk);
            newDirTextBox.addCommand(mipUI.cmdCancel);
            newDirTextBox.setCommandListener(_this);
            MIP.display.setCurrent(newDirTextBox);
        }
        
        public void commandAction(Command c, Displayable d) {
            if (c == mipUI.cmdOk){
                String dirName = newDirTextBox.getString();
                try {
                    //#sijapp cond.if target="MOTOROLA"#
                    motoFileConnection.mkdir(currentDir+dirName);
                    //#sijapp cond.else#
                    FileConnection fileConn = (FileConnection) Connector.open(currentDir+dirName);
                    fileConn.mkdir();
                    //#sijapp cond.end#
                } catch (Exception e){ }
            } else {
            }
            newDirTextBox.setString(null);
       }
        
	public boolean isActive()
	{
		return (list == null) ? false : list.isActive();
	}
	
	public String getValue()
	{
		return selectedItem;
	}
	
	public void openFile(String fileName) throws IOException
	{
		close();
		//#sijapp cond.if target="MOTOROLA"#
		motoFileConnection.open(fileName);
		//#sijapp cond.else#
		fileConnection = (FileConnection) Connector.open("file://" + fileName);		
		//#sijapp cond.end#
	}
	
	public InputStream openInputStream() throws IOException
	{
		InputStream result;
		//#sijapp cond.if target="MOTOROLA"#
		result = motoFileConnection.openInputStream();
		//#sijapp cond.else#
		result = fileConnection.openInputStream();
		//#sijapp cond.end#
		return result;
	}
	
	public OutputStream openOutputStream() throws IOException
	{
		OutputStream result;
		//#sijapp cond.if target="MOTOROLA"#
		result = motoFileConnection.openOutputStream();
		//#sijapp cond.else#
		if ( !fileConnection.exists() ) fileConnection.create();
		result = fileConnection.openOutputStream();
		//#sijapp cond.end#
		return result;
	}
	
	public void close() throws IOException
	{
		//#sijapp cond.if target="MOTOROLA"#
		motoFileConnection.closeFileConn();
		//#sijapp cond.else#
		if (fileConnection != null)
		{
			fileConnection.close();
			fileConnection = null;
		}
		//#sijapp cond.end#
	}
	
	public long fileSize() throws IOException
	{
		long result = 0;
		//#sijapp cond.if target="MOTOROLA"#
		motoFileConnection.fileSize();
		//#sijapp cond.else#
		result = (fileConnection == null) ? -1 : fileConnection.fileSize();
		//#sijapp cond.end#
		return result;
	}      
}

//#sijapp cond.end#