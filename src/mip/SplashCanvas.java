package mip;

import DrawControls.TextList;
import mip.comm.Util;
import mip.comm.Icq;
import mip.comm.Action;
import mip.util.ResourceBundle;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.util.Timer;
import java.io.ByteArrayInputStream;



public class SplashCanvas extends Canvas
{
	static private SplashCanvas _this;
	
	public final static Command cancelCommnad = new Command(ResourceBundle.getString("cancel"), Command.BACK, 1);
	
	//Timer for repaint
	static private Timer t1,t2;

	// Location of the splash image (inside the JAR file)
	//private static final String SPLASH_IMG = "/logo.png";

	// Image object, holds the splash image
	private static Image splash;
	
	private static boolean cancelShown = false;



	// Font used to display the logo (if image is not available)
	private static Font logoFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE);
	
	// Font used to display the version nr
	private static Font versionFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);

	// Font (and font height in pixels) used to display informational messages
	private static Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
	private static int height = font.getHeight();
	

	// Initializer block
	static
	{
	}


	/*****************************************************************************/


	// Message to display beneath the splash image
	static private String message;


	// Progress in percent
	static private int progress; // = 0


	// True if keylock has been enabled
	static private boolean isLocked;


	// Number of available messages
	static private int availableMessages;
	
	// Time since last key # pressed 
	static private long poundPressTime;

	// Should the keylock message be drawn to the screen?
	static protected boolean showKeylock;

	static private int status_index = -1;

	// Constructor
	public SplashCanvas(String message)
	{
		_this = this;
	    setFullScreenMode(true);
		setMessage(message);
		showKeylock = false;
	}

	// Constructor, blank message
	public SplashCanvas()
	{
		this(null);
	}

	// Returns the informational message
	static public synchronized String getMessage()
	{
		return (message);
	}


	// Sets the informational message
	static public synchronized void setMessage(String message)
	{
		SplashCanvas.message = new String(message);
		setProgress(0);
	}

	public static synchronized void setStatusToDraw(int st_index)
	{
		status_index = st_index;
	}


	// Returns the current progress in percent
	static public synchronized int getProgress()
	{
		return (progress);
	}
	
		public static int getAreaWidth()
	{
		return _this.getWidth();
	}
	
	static public Image getSplashImage()
	{
		if (SplashCanvas.splash == null)
		{
			try
			{
				if (!Options.getString(Options.OPTION_USERID).equals("-1")){
					ByteArrayInputStream bis = new ByteArrayInputStream(Options.loadImage());
					SplashCanvas.splash = Image.createImage(bis);
					bis.close();
				}
				else
				{
					SplashCanvas.splash = Image.createImage("/logo.png");
				}
			}
			catch (Exception e)
			{
				SplashCanvas.splash = null;
			}
		}
		return SplashCanvas.splash;
	}
	
	static public void show()
	{
		if (t2 != null)
		{
			t2.cancel();
			t2 = null;
		}
		MIP.display.setCurrent(_this);
	}
	
	static public void addCmd(Command cmd)
	{
	//	_this.addCommand(cmd);
	}
	
	static public void removeCmd(Command cmd)
	{
		//_this.removeCommand(cmd);
	}
	
	static public void setCmdListener(CommandListener l)
	{
		//_this.setCommandListener(l);
	}
	
	static public void Repaint()
	{
		_this.repaint();
	}

	// Sets the current progress in percent (and request screen refresh)
	static public synchronized void setProgress(int progress)
	{
		if (SplashCanvas.progress == progress) return;
		int previousProgress = SplashCanvas.progress;
		SplashCanvas.progress = progress;
		if( progress < previousProgress )
			_this.repaint();
		else
			_this.repaint(0, _this.getHeight() - SplashCanvas.height - 2, _this.getWidth(), SplashCanvas.height + 2);
	}
	
	// Enable keylock
	static public synchronized void lock()
	{
		if (isLocked) return;
		
		isLocked = true;
		//VirtualList.setBkltOn(false);
		setStatusToDraw(mipUI.getStatusImageIndex(Icq.getCurrentStatus()));
		setMessage(ResourceBundle.getString("keylock_enabled"));
		MIP.display.setCurrent(_this);
		
		if (!cancelShown)
		{
			SplashCanvas._this.removeCommand(SplashCanvas.cancelCommnad);
		}
				
		
		if (Options.getBoolean(Options.OPTION_DISPLAY_DATE))
		{
			(t2 = new Timer()).schedule(new TimerTasks(TimerTasks.SC_AUTO_REPAINT), 20000, 20000);
		}
	}


	// Disable keylock
	static public synchronized void unlock(boolean showContactList)
	{
		if (!isLocked) return;
		
		isLocked = false;
		availableMessages = 0;
       // if (Options.getBoolean(Options.OPTION_LIGHT_MANUAL)) VirtualList.setBkltOn(true);

		if (Options.getBoolean(Options.OPTION_DISPLAY_DATE) && (t2 != null)) t2.cancel();
		
		if (_this.isShown()) ContactList.activate();
	}
    
    // Is the screen locked?
	static public boolean locked()
    {
        return (isLocked);
    }

	protected void hideNotify()
	{
		SplashCanvas.splash = null;
	}
	
	// Called when message has been received
	static public synchronized void messageAvailable()
	{
		if (isLocked)
		{
			++availableMessages;
			_this.repaint();
		}
	}
	
	// Called when a key is pressed
	protected void keyPressed(int keyCode)
	{
		if (isLocked)
		{
		    if (keyCode == Canvas.KEY_POUND)
			{
		        poundPressTime = System.currentTimeMillis();
		    }
			else
		    {
				if (t1 != null) t1.cancel();
		        showKeylock = true;
                this.repaint();
			//	VirtualList.flashBklt(2500);
	
		    }
		}
	}
	
	private void tryToUnlock(int keyCode)
	{
		if (!isLocked) return;
		if (keyCode != Canvas.KEY_POUND)
		{
			poundPressTime = 0;
			return;
		}
		
	
		if ((poundPressTime != 0) && ((System.currentTimeMillis()-poundPressTime) > 900))
		{
			unlock(true);
			poundPressTime = 0;
		}
	}

	// Called when a key is released
	protected void keyReleased(int keyCode)
	{
		tryToUnlock(keyCode);
	}
	
	protected void keyRepeated(int keyCode)
	{
		tryToUnlock(keyCode);
	}

	// Render the splash image
	protected void paint(Graphics g)
	{	
        // Do we need to draw the splash image?
		if (g.getClipY() < this.getHeight() - SplashCanvas.height - 2)
		{
			// Draw background
			g.setColor(0xFF,0xff,0xff);
			g.fillRect(0,0,this.getWidth(),this.getHeight());

			// Display splash image (or text)
			Image image = getSplashImage();
			if (image != null)
			{
				g.drawImage(image, this.getWidth() / 2, this.getHeight() / 2, Graphics.HCENTER | Graphics.VCENTER);
			}

			// Display message icon, if keylock is enabled
			if (isLocked && availableMessages > 0)
			{
				g.drawImage(ContactList.eventPlainMessageImg, 1, this.getHeight()-(2*SplashCanvas.height)-9, Graphics.LEFT | Graphics.TOP);
				g.setColor(0, 0, 0);
				g.setFont(SplashCanvas.font);
				g.drawString("# " + availableMessages, ContactList.eventPlainMessageImg.getWidth() + 4, this.getHeight()-(2*SplashCanvas.height)-5, Graphics.LEFT | Graphics.TOP);
			}
            


			// Draw the date bellow notice if set up to do so
			if (Options.getBoolean(Options.OPTION_DISPLAY_DATE))
			{
				g.setColor(0, 0, 0);
				g.setFont(SplashCanvas.font);
				g.drawString(Util.getDateString(false, false), this.getWidth() / 2, 12, Graphics.TOP | Graphics.HCENTER);
				g.drawString(Util.getCurrentDay(), this.getWidth() / 2, 13+SplashCanvas.font.getHeight(), Graphics.TOP | Graphics.HCENTER);
			}
            // Display the keylock message if someone hit the wrong key
            if (showKeylock)
            {
                
                // Init the dimensions
                int x,y,size_x,size_y;
                size_x = this.getWidth()/10*8;
                size_y = Font.getFont(Font.FACE_SYSTEM,Font.STYLE_PLAIN,Font.SIZE_MEDIUM).getHeight()*TextList.getLineNumbers(ResourceBundle.getString("keylock_message"),size_x-8,0,0,0)+8;
                x = this.getWidth()/2-(this.getWidth()/10*4);
                y = this.getHeight()/2-(size_y/2);
                
                
                g.setColor(0, 0, 0);
                g.fillRect(x,y,size_x,size_y);
                g.setColor(255,255,255);
                g.drawRect(x+2,y+2,size_x-5,size_y-5);
                TextList.showText(g,ResourceBundle.getString("keylock_message"),x+4,y+4,size_x-8,size_y-8,TextList.MEDIUM_FONT,0,0xffffff);
                
				(t1 = new Timer()).schedule(new TimerTasks(TimerTasks.SC_HIDE_KEYLOCK), 2000);

            }

		}

		// Draw white bottom bar
		g.setColor(0xff, 0, 0);
		g.setStrokeStyle(Graphics.DOTTED);
		g.drawLine(0, this.getHeight() - SplashCanvas.height - 3, this.getWidth(), this.getHeight() - SplashCanvas.height - 3);

		g.setColor(0, 0, 0);
		g.setFont(SplashCanvas.font);

		Image draw_img = null;
		int im_width = 0;
		if (status_index != -1)
		{
			draw_img = MIP.SIcons.images.elementAt(status_index);
			im_width = draw_img.getWidth();
		}

		// Draw the progressbar message
		g.drawString(message, (this.getWidth() / 2) + (im_width / 2), this.getHeight(), Graphics.BOTTOM | Graphics.HCENTER);

		if (draw_img != null)
		{
			g.drawImage(draw_img, (this.getWidth() / 2) - (font.stringWidth(message) / 2) + (im_width / 2), this.getHeight() - (height / 2) - 2, Graphics.VCENTER | Graphics.RIGHT);
		}

		// Draw current progress
		int progressPx = this.getWidth() * progress / 100;
		if (progressPx < 1) return;

		g.setClip(0, this.getHeight() - SplashCanvas.height - 2, progressPx, SplashCanvas.height + 2);
		g.setColor(Options.getInt(Options.OPTION_COLOR1));
		
		g.fillRect(0, this.getHeight() - SplashCanvas.height - 2, progressPx, SplashCanvas.height + 2);
		//drawGradient(g, 0x4C4C4C, 0xE5E5E5, 0, this.getHeight() - SplashCanvas.height - 2, progressPx, SplashCanvas.height + 2);
		//drawGradient(g, Options.getInt(Options.OPTION_COLOR1), Options.getInt(Options.OPTION_COLOR2), 0, this.getHeight() - SplashCanvas.height - 2, progressPx, SplashCanvas.height + 2);
		g.setColor(0xFFFFFF);
		// Draw the progressbar message
		g.drawString(message, (this.getWidth() / 2) + (im_width / 2), this.getHeight(), Graphics.BOTTOM | Graphics.HCENTER);

		if (draw_img != null)
		{
			g.drawImage(draw_img, (this.getWidth() / 2) - (font.stringWidth(message) / 2) + (im_width / 2), this.getHeight() - (height / 2), Graphics.VCENTER | Graphics.RIGHT);
		}

	}
	

	public static void startTimer()
	{
		if (status_index != 8)
		{
			new Timer().schedule(new TimerTasks(TimerTasks.SC_RESET_TEXT_AND_IMG), 3000);

		}
}
	
	
	public static void addTimerTask(String captionLngStr, Action action, boolean canCancel)
	{
		if (t2 != null)
		{
			t2.cancel();
			t2 = null;
		}
		
		isLocked = false;
		
		TimerTasks timerTask = new TimerTasks(action); 
		
		//SplashCanvas._this.removeCommand(SplashCanvas.cancelCommnad);
		cancelShown = canCancel;
		
		if (canCancel)
		{
			//SplashCanvas._this.addCommand(SplashCanvas.cancelCommnad);
			//SplashCanvas._this.setCommandListener(timerTask);
		}
		
		SplashCanvas.setMessage(captionLngStr);
		SplashCanvas.setProgress(0);
		MIP.display.setCurrent(SplashCanvas._this);
		
		mip.MIP.getTimerRef().schedule(timerTask, 1000, 1000);
	}
}
