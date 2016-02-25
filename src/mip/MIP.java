package mip;


import mip.comm.Icq;
import mip.util.ResourceBundle;
import DrawControls.VirtualList;
import java.util.Timer;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.lcdui.Displayable;
import com.studio.motionwelder.*;
//import mip.TLightController;

public class MIP extends MIDlet
{
	public static final int DSP_MAINMENU         =  0;
	public static final int DSP_CL               =  1;
	public static final int DSP_OPTIONS          =  2;
	public static final int DSP_ABOUT            =  3;
	public static final int DSP_STATUS           =  4;
	public static final int DSP_XSTATUS          =  5;
	public static final int DSP_PSTATUS          =  6;
	public static final int DSP_CHAT             =  7;
	public static final int DSP_HISTORY          =  8;
	public static final int DSP_GROUPS           =  9;
	public static final int DSP_EDITPROFILE      = 10;
	public static final int DSP_COLORLIST        = 11;
	public static final int DSP_SPLASH           = 12;
	public static final int DSP_STATUSES         = 13;
	public static final int DSP_CLIENTID         = 14;
	public static final int DSP_EMOTIONS         = 15;
	public static final int DSP_CONTACTMENU      = 16;
	public static final int DSP_PROFILE          = 17;
	public static final int DSP_DC               = 18;
	public static final int DSP_WEATHER          = 19;
	public static final int DSP_HISTORYMSG       = 20;
        public static final int DSP_DIRBROWSER       = 21;
        public static final int DSP_PRLISTS          = 22;
	public static final int DSP_DELCHAT          = 23;
	public static final int DSP_DELHIST          = 24;
	public static final int DSP_SEARCHRESULTS    = 25;
	public static final int DSP_DIRMENU          = 26;
        public static final int DSP_THEMES           = 27;
        public static final int DSP_MB_MESSAGE       = 28;
        public static final int DSP_MB_YESNO         = 29;
        public static final int DSP_MB_OK            = 30;
        public static final int DSP_NOTEMP           = 31;
    
        // Version
	public static String VERSION;


	// Application main object
	public static MIP MIP;
	//public static int light = 0;
	public static String uin;
	public static boolean isFullyConnected = false;
	
	public static StatusIcons SIcons;
	public static ContextMenu cMenu; 
	//anim smiles functionality
	public static ResourceLoader rcLoader; 
	public static MSpriteData animationData;    

	// Display object
	public static Display display;


	/****************************************************************************/


	// ICQ object
	private static Icq icq;

	// Options container 	 
    private Options o;

	// Main menu object
	private MainMenu mm;


	// Contact list object
	private static ContactList cl;

	// Chat history object
	private ChatHistory ch;
	
    // #sijapp cond.if target is "MIDP2" #
	static private boolean is_phone_SE;
    // #sijapp cond.end #
	// #sijapp cond.if target is "MOTOROLA" & (modules_FILES="true"|modules_HISTORY="true")#
	static public final boolean supports_JSR75;
	// #sijapp cond.end#

	// Timer object
	private static Timer t;


	// #sijapp cond.if modules_TRAFFIC is "true" #
	// Traffic counter
	private Traffic traffic;
	// #sijapp cond.end#


	// Splash canvas object
	private static SplashCanvas sc;
	
	// Storage for messages
	// #sijapp cond.if modules_HISTORY is "true" #
	private static HistoryStorage history;

        // #sijapp cond.end#
	public static ColorSelector clSelect;
	
	private static mipUI ui;
	
	public static final String microeditionPlatform = System.getProperty("microedition.platform");
	public static final String microeditionProfiles = System.getProperty("microedition.profiles");

	//#sijapp cond.if target="MOTOROLA"|target="MIDP2"#
	static
	{
		rcLoader = ResourceLoader.getInstance();
		// #sijapp cond.if target is "MIDP2" #
		if (microeditionPlatform != null)
			is_phone_SE = (microeditionPlatform.toLowerCase().indexOf("ericsson") != -1);
		// #sijapp cond.end#
		// #sijapp cond.if target is "MOTOROLA" & (modules_FILES="true"|modules_HISTORY="true")#
		boolean jsr75 = false;
		try
		{
			jsr75 = Class.forName("javax.microedition.io.file.FileConnection") != null;
		}
		catch (ClassNotFoundException cnfe)
		{
		}
		finally
		{
			supports_JSR75 = jsr75;
		}
		// #sijapp cond.end#

		
	}
	//#sijapp cond.end#

	
	static smilesDraw smilesThread;
	class smilesDraw extends Thread {
         smilesDraw() {
		 }
		  private int noSmilesDelay = 0;

        public void run() {
            while (true) {
                if ((MIP.lastDsp == MIP.DSP_CHAT) | (MIP.lastDsp == MIP.DSP_EMOTIONS)) {
                    DrawControls.VirtualList.repaintStatic();
                } else if ((mipUI.getCurrentScreen() instanceof VirtualList) & Options.getBoolean(Options.OPTION_SECONDS)) {
                    noSmilesDelay += 1;

                    if (noSmilesDelay == 9) {
                        DrawControls.VirtualList.repaintStatic();
                        noSmilesDelay = 0;
                    }
                }

                try {
                    smilesThread.sleep(120);
                } catch (Exception e) {
                }
            }
        }
     }
	// Start mip
	public void startApp() throws MIDletStateChangeException
	{
		RunnableImpl.setMidlet(this);
		
		// Return if MIDlet has already been initialized
		if (MIP.MIP != null)
		{
			showWorkScreen();
			return;
		}
		
		// Save MIDlet reference
		MIP.MIP = this;
		
		// Get mip version
		MIP.VERSION = "###VERSION###";
		
		// Create options container 	 
        this.o = new Options();
		
		// Create splash canvas object
		this.sc = new SplashCanvas(ResourceBundle.getString("loading"));
		
		// Check available heap memory, display warning if less then 250 KB
		if (Runtime.getRuntime().totalMemory() < 512000)
		{
			Alert errorMsg = new Alert(ResourceBundle.getString("warning"), mipException.getErrDesc(170, 0), null, AlertType.WARNING);
			errorMsg.setTimeout(Alert.FOREVER);
			Display.getDisplay(this).setCurrent(errorMsg, this.sc);
			try
			{
				Thread.sleep(3000);
			}
			catch (InterruptedException e)
			{
				// Do nothing
			}
		}
		// Display splash canvas
		else
		{
			Display.getDisplay(this).setCurrent(this.sc);
		}
	
		// Get display object (and update progress indicator)
		MIP.display = Display.getDisplay(this);
		SplashCanvas.setProgress(10);
		SplashCanvas.setStatusToDraw(-1);

		// Create ICQ object (and update progress indicator)
		SplashCanvas.setMessage("Loading... 10%");
		SplashCanvas.setProgress(10);
		this.icq = new Icq();
		this.clSelect = new ColorSelector();

		
		// Create object for text storage (and update progress indicator)
		// #sijapp cond.if modules_HISTORY is "true" #
		SplashCanvas.setMessage("Loading... 20%");
		SplashCanvas.setProgress(20);
		history = new HistoryStorage();
		
		// #sijapp cond.end#
		// Create and load emotion icons
		//#sijapp cond.if modules_SMILES is "true" #
		
			SplashCanvas.setMessage("Loading... 25%");
			SplashCanvas.setProgress(25);
			
			try{
                            if (Options.getBoolean(Options.OPTION_USE_SMILES)) {
                                animationData = MSpriteLoader.loadMSprite("/smiles.anu", true, rcLoader);
                            }
			}catch (Exception e) {
				
			}
			new Emotions();
		
		// #sijapp cond.end#

		// Initialize main menu object (and update progress indicator)
		SplashCanvas.setMessage("Loading... 55%");
		SplashCanvas.setProgress(55);
        this.mm = new MainMenu();
		this.SIcons = new StatusIcons();
		
		
		// #sijapp cond.if modules_TRAFFIC is "true" #
		// Create traffic Object (and update progress indicator)
		SplashCanvas.setMessage("Loading... 65%");
		SplashCanvas.setProgress(65);
		this.traffic = new Traffic();
		
		// #sijapp cond.end#
		
		// Create contact list object (and update progress indicator)
		SplashCanvas.setMessage("Loading... 80%");
		SplashCanvas.setProgress(80);
		this.cl = new ContactList();
		ContactList.beforeConnect();
		this.cMenu = new ContextMenu();
		
		// Create chat hisotry object (and update progress indicator)
		SplashCanvas.setMessage("Loading... 85%");
		SplashCanvas.setProgress(85);
		this.ch = new ChatHistory();
		

		// Create timer object (and update progress indicator)
		SplashCanvas.setMessage("Loading... 99%");
		SplashCanvas.setProgress(99);
		this.t = new Timer();
		
		ui = new mipUI();
		System.gc();
		// set color scheme for all forms
		mipUI.setColorScheme();
		if ((Options.getBoolean(Options.OPTION_ANIMSMILES) & (animationData != null)) | Options.getBoolean(Options.OPTION_SECONDS))
		{
			smilesThread = new smilesDraw();
			smilesThread.start();
		}
                try {
                    Options.loadTheme(Options.getString(Options.OPTION_THEME), true);
                    Options.loadThemeList();
                }
                catch (Exception e) {e.printStackTrace();}
		if (Options.getBoolean(Options.OPTION_AUTO_CONNECT))
        {
		    // Connect
			Icq.reconnect_attempts = Options.getInt(Options.OPTION_RECONNECT_NUMBER);
            ContactList.beforeConnect();
            Icq.connect();
        } 
		else
        {
            // Activate main menu
			ContactList.activate();
        }
		
	}
	// Pause
	public void pauseApp()
	{
		// Do nothing
	}
	
        public static void setDsp(int dsp) {
            if (chDsp) {
                lastDsp = dsp;
            } else return;
            
            switch(lastDsp) {
//                case DSP_CL: VirtualList.getCurrent().setSoftNames("menu", "contact"); break;
//                case DSP_CHAT: VirtualList.getCurrent().setSoftNames("backcl", "comlist"); break;
//                case DSP_MB_MESSAGE: VirtualList.getCurrent().setSoftNames("", "close"); break;
//                case DSP_MB_YESNO: VirtualList.getCurrent().setSoftNames("no", "yes"); break;
//                case DSP_MB_OK: VirtualList.getCurrent().setSoftNames("", "ok"); break;
            }
        }
        
        public static void setTempDsp(int dsp) {
            System.out.print("setTempDsp");
            System.out.println(dsp);
            tempDsp = lastDsp;
            setDsp(dsp);
            chDsp = false;
        }
        
        public static void setSavedDsp() {
            System.out.print("setSavedDsp");
            System.out.println(tempDsp);
            chDsp = true;
            setDsp(tempDsp);
        }
        
        static int tempDsp = DSP_NOTEMP;
	public static int lastDsp;
        static boolean chDsp = true;
	
	public static void takeMeBack(VirtualList sender)
	{
		if (!Options.getBoolean(Options.OPTION_SWAPSOFTS)){
			performSoftAction(false, sender);
		}
		else
		{
			performSoftAction(true, sender);
		}
	}
	public static void leftAction(VirtualList sender)
	{
            switch(lastDsp) {
                case DSP_SEARCHRESULTS:
                    if (sender.getUIState() == 1) {sender.setUIState(0); break;} else ContactList.activate();
                    break;
                    
                case DSP_CL:
                case DSP_OPTIONS:
                case DSP_GROUPS:
                case DSP_EDITPROFILE:
                case DSP_STATUSES:
                case DSP_CLIENTID:
                    ContactList.activate();
                    MIP.setDsp(MIP.DSP_MAINMENU);
                    ContextMenu.build(0, ContactList.tree);
                    break;
                    
                case DSP_HISTORYMSG:
                    MIP.getHistory().getHSL().activate(MIP.display);
                    MIP.setDsp(MIP.DSP_HISTORY);
                    break;
                    
                case DSP_STATUS:
                case DSP_XSTATUS:
                case DSP_PSTATUS:
                    ContactList.activate();
                    MIP.setDsp(MIP.DSP_STATUSES);
                    ContextMenu.build(6, ContactList.tree);
                    break;
                    
                case DSP_MAINMENU:
                    ContextMenu.performAction(VMenuItem.VMI_ACT_SHOWCL);
                    break;
                    
                case DSP_ABOUT:
                case DSP_HISTORY:
                case DSP_CONTACTMENU:
                case DSP_PROFILE:
                case DSP_DC:
                    if (lastDsp == MIP.DSP_CONTACTMENU) ContextMenu.restoreDisplayable();
                    
                    if (sender.getUIState() == 1) sender.setUIState(0);
                    else if (lastDsp == MIP.DSP_HISTORY) {
                        MIP.getHistory().clearCache();
                        MIP.getHistory().getHSL().messText = null;
                        ContactList.activate();
                    } else if (lastDsp == MIP.DSP_ABOUT) ContactList.activate();
                    else {
                        if ((lastDsp != MIP.DSP_DC) || (lastDsp != MIP.DSP_PROFILE)) ContextMenu.restoreDisplayable();
                        sender.setUIState(0);
                        ContextMenu.activateLast();
                    }
                    break;
                    
                case DSP_DIRMENU:
                    MIP.setDsp(MIP.DSP_DIRBROWSER);
                    MIP.getHistory().getHSL().activateFS();
                    break;
                    
                case DSP_DIRBROWSER:
                    MIP.setDsp(MIP.DSP_HISTORY);
                    MIP.getHistory().getHSL().activate(MIP.display);
                    break;
                    
                case DSP_CHAT:
                case DSP_WEATHER:
                    ContactList.activate();
                    break;
                    
                case DSP_COLORLIST:
                    ContextMenu.build(16, sender);
                    break;
                    
                case DSP_THEMES:
                    ContextMenu.build(2, sender);
                    break;
                    
                case DSP_SPLASH:
                    break;
                    
                case DSP_EMOTIONS:
                    Emotions.takeMeBack();
                    break;
                    
                case DSP_PRLISTS:
                    ContextMenu.build(1, sender);
                    break;
                    
                case DSP_DELCHAT:
                    ContextMenu.build(1, sender);
                    break;
                    
                case DSP_DELHIST:
                    ContextMenu.build(8, sender);
                    break;
            }
	}
	
	public static void rightAction(VirtualList sender)
	{
		switch(lastDsp)
		{
		
			case DSP_SEARCHRESULTS:
					ContextMenu.build(14, sender);
					break;
					
			case DSP_PROFILE:
                        case DSP_DC:
				if (sender.getUIState() == 0) ContextMenu.build(7, sender);
				else sender.makeClick();
				break;
				
			case DSP_STATUS:
                        case DSP_XSTATUS:
                        case DSP_PSTATUS:
                        case DSP_CLIENTID:
				MIP.SIcons.select();
				break;
				
			case DSP_EMOTIONS:
				Emotions.select();
				break;

                    case DSP_MB_MESSAGE:
                    case DSP_MB_OK:
                        VirtualList.getCurrent().closeLastMesBox();
//                        VirtualList.getCurrent()) == 0)
//                            VirtualList.getCurrent().activate(display);
                        break;
                        
			case DSP_MAINMENU:
                        case DSP_OPTIONS:
                        case DSP_CONTACTMENU:
                        case DSP_STATUSES:
                        case DSP_PRLISTS:
                        case DSP_DELCHAT:
                        case DSP_DELHIST:
                        case DSP_DIRMENU:
                        case DSP_THEMES:
				sender.makeClick();
				break;
				
			case DSP_CHAT:
                        case DSP_CL: //case 18:
				ContextMenu.saveDisplayable();
				ContextMenu.build(1, sender);
				break;
				
			case DSP_HISTORY:
				if (sender.getUIState() == 0) ContextMenu.build(8, sender);
				else sender.makeClick();
				break;	
				
			case DSP_HISTORYMSG:
				if (sender.getUIState() == 0) ContextMenu.build(9, sender);
				else sender.makeClick();
				break;
                                
                        case DSP_DIRBROWSER:
                            	ContextMenu.saveDisplayable();
				ContextMenu.build(15, sender);
                                //MIP.getHistory().getHSL().dirSelectedFS();
                                break;
		}
	}
	public static void performSoftAction(boolean right, VirtualList sender)
	{
		System.out.println("current dsp - " + lastDsp);
		try{
		if (!right)
		{
			if (!Options.getBoolean(Options.OPTION_SWAPSOFTS)) leftAction(sender);
			else rightAction(sender);
		}
		else 
		{
			if (!Options.getBoolean(Options.OPTION_SWAPSOFTS)) rightAction(sender);
			else leftAction(sender);
		}
		}catch (Exception e){}
	}
        
	public static void setBkltOn(boolean ferever)
	{
//#sijapp cond.if target="MOTOROLA" | target="MIDP2"#
		if ( !Options.getBoolean(Options.OPTION_LIGHT_MANUAL) ) return;
		MIP.display.flashBacklight(ferever ? Integer.MAX_VALUE : Options.getInt(Options.OPTION_LIGHT_TIMEOUT));
		//System.out.println("setBkltOn, ferever="+ferever);
//#sijapp cond.end#
	}
	
	public static void setBkltOff()
	{
//#sijapp cond.if target="MOTOROLA" | target="MIDP2"#
		if ( !Options.getBoolean(Options.OPTION_LIGHT_MANUAL) ) return;
		MIP.display.flashBacklight(1);
		//System.out.println("setBkltOff");
//#sijapp cond.end#
	}        
        
	/*
	public static void light(int brightness)
	{
		if (!(TLightController.GetInstance(MIP.MIP).CanControl())) return;
		light = brightness;
		TLightController.GetInstance(MIP.MIP).SetBrightness(light);
	}
	
	public static void light()
	{
		if (!(TLightController.GetInstance(MIP.MIP).CanControl())) return;
		if (!(TLightController.GetInstance(MIP.MIP).CanControlBrightness()))
		{
			if (light == 0) light = 255;
			else light = 0;
		}
		else
		{
			switch (light)
			{
				case 0:
				light = 51;
				break;
				
				case 51:
				light = 102;
				break;
				
				case 102:
				light = 153;
				break;
				
				case 153:
				light = 204;
				break;
				
				case 204:
				light = 255;
				break;
				
				case 255:
				light = 0;
				break;
			}
		}
		TLightController.GetInstance(MIP.MIP).SetBrightness(light);
	}
	
*/	
	// Destroy mip
	public void destroyApp(boolean unconditional) throws MIDletStateChangeException
	{
        // Disconnect
        Icq.disconnect();       
		//smilesThread.interrupt();
        
        // Save traffic
        //#sijapp cond.if modules_TRAFFIC is "true" #
		try 
		{
			Traffic.save();
		} 
		catch (Exception e) 
		{ // Do nothing
		} 
		//#sijapp cond.end#
		
		MIP.display.setCurrent(null);
		MIP.notifyDestroyed();
	}


	// Returns a reference to ICQ object
	public static Icq getIcqRef()
	{
		return (MIP.icq);
	}
	
    // Returns a reference to options container 	 
    public Options getOptionsRef() 	 
    { 	 
            return (this.o); 	 
    }

	// Returns a reference to the main menu object
	public MainMenu getMainMenuRef()
	{
		return (this.mm);
	}


	// Returns a reference to the contact list object
	public static ContactList getContactListRef()
	{
		return (MIP.cl);
	}
	
	// Returns a reference to the chat history list object
	public ChatHistory getChatHistoryRef()
	{
		return (this.ch);
	}

	// #sijapp cond.if modules_HISTORY is "true" #
	// Returns a reference to the stored history object
	public static HistoryStorage getHistory()
	{
		return (MIP.history);
	}

	// #sijapp cond.end#

	// Returns a reference to the timer object
	public static Timer getTimerRef()
	{
		return (MIP.t);
	}
	
	// Cancels the timer and makes a new one
	public static void cancelTimer()
	{
		try
		{
			MIP.t.cancel();
		}
		catch(IllegalStateException e){}
		MIP.t = new Timer();
	}


	// Returns a reference to splash canvas object
	public static SplashCanvas getSplashCanvasRef()
	{
		return (MIP.sc);
	}


	// #sijapp cond.if modules_TRAFFIC is "true" #
	// Return a reference to traffic object
	public static Traffic getTrafficRef()
	{
		return (MIP.traffic);
	}
	// #sijapp cond.end#
	
	public static mipUI getUIRef()
	{
		return ui;
	}
	
	static public void showWorkScreen()
	{
		if (SplashCanvas.locked()) SplashCanvas.show();
		else ContactList.activate();
	}
	
	// #sijapp cond.if target is "MIDP2" #
	// Set the minimize state of midlet
	static public void setMinimized(boolean mini)
	{
		if (mini)
		{
			MIP.display.setCurrent(null);
		}
		else
		{
			Displayable disp = MIP.display.getCurrent();
			if ((disp == null) || !disp.isShown()) showWorkScreen();
		}
	}
	
	static public boolean is_phone_SE()
	{
		return is_phone_SE;
	}	
	
    // #sijapp cond.end #
}

