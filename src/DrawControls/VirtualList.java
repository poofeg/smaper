package DrawControls;

import javax.microedition.lcdui.*;
import java.util.Vector;

import mip.ContactList;
import mip.Options;
import mip.MIP;
import mip.SplashCanvas;
import mip.VMenuItem;
import mip.mipUI;
import mip.util.ResourceBundle;
import mip.comm.Util;
import java.util.Timer;
import java.util.TimerTask;
import mip.TimerTasks;
import mip.StatusIcons;

//! This class is base class of owner draw list controls
/*!
    It allows you to create list with different colors and images. 
    Base class of VirtualDrawList is Canvas, so it draw itself when
    paint event is heppen. VirtualList have cursor controlled of
    user
*/
class VirtualCanvas extends Canvas implements Runnable
{
	VirtualList currentControl;	
	private Timer repeatTimer = new Timer();
	private TimerTask timerTask;
	private int lastKeyKode;
	void cancelKeyRepeatTask()
	 	         {
	 	                 if (timerTask != null) timerTask.cancel();
						 lastKeyKode = 0;
						timerTask = null;
}
	public VirtualCanvas ()
	{
		
		setFullScreenMode(true);
		
	}
	
	public void run()
    	{
    		if (timerTask == null) return;
    		currentControl.keyRepeated(lastKeyKode);
    	}
	
	protected void paint(Graphics g)
	{
		if (currentControl != null) currentControl.paint(g); 
	}

	
	protected void showNotify()
	{
		setFullScreenMode(true);
		if (currentControl != null) currentControl.showNotify();
		cancelKeyRepeatTask();
	}
		
	
	protected void keyPressed(int keyCode)
	{
		cancelKeyRepeatTask();
		lastKeyKode = keyCode;
		if ((Options.getBoolean(Options.OPTION_AUTOAWAY_ENABLE)) || (Options.getBoolean(Options.OPTION_AUTONA_ENABLE)))
                    TimerTasks.setStatusTimer();
			if (currentControl != null) currentControl.keyPressed(keyCode);
			
			if  (!(getGameAction(keyCode) == Canvas.FIRE))
                        { timerTask = new TimerTask() {
				public void run()
				{
					mip.MIP.display.callSerially(VirtualCanvas.this);
				}
			};
			repeatTimer.schedule(timerTask, 500, 50);}
	}
	
	protected void keyReleased(int keyCode)
	{
		if (currentControl != null) currentControl.keyReleased(keyCode);
		cancelKeyRepeatTask();		
	}
	
	//#sijapp cond.if target is "MIDP2"#
	/*protected void pointerDragged(int x, int y)
	{
		if (currentControl != null) currentControl.pointerDragged(x, y); 
	}
	
	protected void pointerPressed(int x, int y)
	{
		if (currentControl != null) currentControl.pointerPressed(x, y); 
	}
	
	protected void pointerReleased(int x, int y)
	{
		if (currentControl != null) currentControl.pointerReleased(x, y);
	}*/
	//todo fix
	//#sijapp cond.end#
}

public abstract class VirtualList
{
	/*! Use dotted mode of cursor. If item of list 
	 is selected, dotted rectangle drawn around  it*/
	public final static int SEL_DOTTED = 2;
	private static VirtualCanvas virtualCanvas = new VirtualCanvas();
	boolean menuItemsVisible;

	/*! Does't show cursor at selected item. */
	public final static int SEL_NONE = 3;
	public void setTitle(String str)
	{
		virtualCanvas.setTitle(str);
	}
	
	 public static VirtualList getCurrent()
	 	         {
	 	                 return virtualCanvas.isShown() ? virtualCanvas.currentControl : null;
	 	         }
				 

	/*! Constant for medium sized font of caption and item text */
	public final static int MEDIUM_FONT = Font.SIZE_MEDIUM;

	/*! Constant for large sized font of caption and item text */
	public final static int LARGE_FONT = Font.SIZE_LARGE;

	/*! Constant for small sized font of caption and item text */
	public final static int SMALL_FONT = Font.SIZE_SMALL;
	
	// Key event type
	public final static int KEY_PRESSED = 1;
	public final static int KEY_REPEATED = 2;
	public final static int KEY_RELEASED = 3;
	
	// Set of fonts for quick selecting
	private Font normalFont, boldFont, italicFont;
	
	// Font for drawing caption
	private static Font capFont;
	// Commands to react to VL events
	private VirtualListCommands vlCommands;

	// Caption of VL
	private String caption;
	private String leftKey = "Left";
	private String rightKey = "Right";
        static private String leftMenuKey = "Left";
	static private String rightMenuKey = "Right";
	
	private static final String SPLASH_IMG = "/back.png";
	private static Image splash;

	
	// Used by "Invalidate" method to prevent invalidate when locked 
	private boolean dontRepaint = false;

	// Images for VL
	private ImageList imageList = null;

	// Index for current item of VL
	protected int currItem = 0;
	
	// Used for passing params of items whan painting 
	final static protected ListItem paintedItem;
	
	// Used to catch changes to repaint data
	private int lastCurrItem = 0, lastTopItem = 0;

	private static VirtualList current;
	private static boolean fullScreen = true;
	
	private Image capImage;
	private Image capImage2;
	private static boolean lastLeftKey;
	
        private boolean drawOnlyMenu = false;
        
	private int 
		topItem     = 0,            // Index of top visilbe item 
		fontSize    = MEDIUM_FONT,  // Current font size of VL
		bkgrndColor = 0xFFFFFF,     // bk color of VL
		cursorColor = 0x808080,     // Used when drawing focus rect.
		textColor   = 0x000000,     // Default text color.
		capBkColor  = 0xA00000,
		capTxtColor = 0x00,     // Color of caption text
		cursorMode  = SEL_DOTTED;   // Cursor mode
	
	static
	{
		capFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		int width = capFont.getHeight() / 10;
		paintedItem = new ListItem();
	}
	
	public void setSoftNames(String left, String right) {
            leftKey = ResourceBundle.getString(left);
            rightKey = ResourceBundle.getString(right);
        }
	
        static public void setMenuSoftNames(String left, String right) {
            leftMenuKey = ResourceBundle.getString(left);
            rightMenuKey = ResourceBundle.getString(right);
        }
        
	static public void setFullScreenMode(boolean value)
	{
		setFullScreen(value);
	}
	static public void setFullScreen(boolean value)
	{
		
		if (fullScreen == value) return;
		fullScreen = value;
		if (current != null)
		{
			VirtualList.virtualCanvas.setFullScreenMode(fullScreen);
		}
		
	}
	 public int getGameAction(int keyCode)
	 	         {
	 	                 return virtualCanvas.getGameAction(keyCode);
	 	         }
	 	         public void repaint()
	 	         {
	 	                 if (isActive()) virtualCanvas.repaint();
	 	         }
				 public static void repaintStatic()
				 {
					virtualCanvas.repaint();
				 }
				 
	
	
	public Vector menuItemsData = null, clickedMenuItems = null;
	int lastManuIndex;
	//! Create new virtual list with default values  
	public VirtualList
	(
		String capt //!< Caption text of new virtual list
	)
	{
		super();
		setCaption(capt);
		//#sijapp cond.if target is "SIEMENS2"# 
		this.fontSize = Font.SIZE_SMALL;
		//#sijapp cond.else#
		this.fontSize = Font.SIZE_MEDIUM;
		//#sijapp cond.end#
		createSetOfFonts(this.fontSize);
		this.cursorMode = SEL_DOTTED;
		setFullScreenMode(true);
	}

	// public VirtualList
	public VirtualList
	(
		String capt,      //!< Caption text of new virtual list
		int capTextColor, //!< Caption text color
		int backColor,    //!< Control back color
		int fontSize,     /*!< Control font size. This font size if used both for caption and text in tree nodes */
		int cursorMode    /*!< Cursor mode. Can be VirtualList.SEL_DOTTED or VirtualList.SEL_INVERTED */
	)
	{
		super();
		setCaption(capt);
		this.capTxtColor = capTextColor;
		this.bkgrndColor = backColor;
		
		this.fontSize = fontSize; 
		createSetOfFonts(this.fontSize);
		this.cursorMode = cursorMode;
		setFullScreenMode(true);
	}

	//! Request number of list elements to be shown in list
	/*! You must return number of list elements in successtor of
	    VirtualList. Class calls method "getSize" each time before it drawn */
	abstract protected int getSize();

	//! Request of data of one list item
	/*! You have to reload this method. With help of method "get" class finds out
	    data of each item. Method "get" is called each time when list item 
	    is drawn */
	abstract protected void get
	(
		int index,    //!< Number of requested list item 
		ListItem item //!< Data of list item. Fill this object with item data.
	);

	Font getQuickFont(int style)
	{
		switch (style)
		{
		case Font.STYLE_BOLD:   return boldFont;
		case Font.STYLE_PLAIN:  return normalFont;
		case Font.STYLE_ITALIC: return italicFont;
		}
		return Font.getFont(Font.FACE_SYSTEM, style, fontSize);
	}
	
	// returns height of draw area in pixels  
	protected int getDrawHeight()
	{
		return getHeightInternal()-getCapHeight();
	}
	

	//! Sets new font size and invalidates items
	public void setFontSize(int value)
	{
		if (fontSize == value) return;
		fontSize = value;
		createSetOfFonts(fontSize);
		checkTopItem();
		invalidate();
	}
	
	public void setCapImage(Image image, Image image2)
	{
		if (capImage == image && capImage2 == image2) return;
		capImage = image;
		capImage2 = image2;
		invalidate();
	}
	
	public void setVLCommands(VirtualListCommands vlCommands)
	{
		this.vlCommands = vlCommands;
	}
	
	public void setColors(int capTxt, int capbk, int bkgrnd, int cursor, int text)
	{
		capBkColor  = capbk;
		capTxtColor = capTxt;
		bkgrndColor = bkgrnd;
		cursorColor = cursor;
		textColor   = text;
		if (isActive()) virtualCanvas.repaint();
	}
	
	private void createSetOfFonts(int size)
	{
		normalFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN,  fontSize); 
		boldFont   = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD,   fontSize);
		italicFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, fontSize);
	}
	
	public int getFontSize()
	{
		return fontSize;
	}
	public boolean isActive()
	 	         {
	 	                 return (virtualCanvas.currentControl == this) && virtualCanvas.isShown();
	 	         }
	 	 
	 	         public void activate(Display display)
	 	         {
	 	                 if (isActive()) return;
	 	                 virtualCanvas.currentControl = this;
						 setUIState(0);
						 virtualCanvas.cancelKeyRepeatTask();
	 	                 display.setCurrent(virtualCanvas);
	 	                 repaint();
	 	         }
				 public void justShow(Display display)
				 {
					virtualCanvas.currentControl = this;
					virtualCanvas.cancelKeyRepeatTask();
					display.setCurrent(virtualCanvas);
					repaint();
				 }
	 	 
	 	         public void activate(Display display, Alert alert)
	 	         {
	 	                 if (isActive()) return;
	 	                 virtualCanvas.currentControl = this;
						 virtualCanvas.cancelKeyRepeatTask();
	 	                 display.setCurrent(alert, virtualCanvas);
	 	                 repaint();
	 	         }

	public int getTextColor()
	{
		return textColor;
	}

	//! Returns number of visibled lines of text which fits in screen 
	public int getVisCount()
	{
		int size = getSize();
		int y = 0;
		int counter = 0, i;
		int height = getDrawHeight() - 17;
		int topItem = this.topItem;
		
		if (size == 0) return 0;
		
		if (topItem < 0) topItem = 0;
		if (topItem >= size) topItem = size-1;
		
		for (i = topItem; i < (size-1); i++)
		{
			y += getItemHeight(i);
			if (y > height) return counter;
			counter++;
		}
		
		y = height;
		counter = 0;
		for (i = size-1; i >= 0; i--)
		{
			y -= getItemHeight(i);
			if (y < 0) break;
			counter++;
		}
		
		return counter;
	}

	public void setCursorMode(int value)
	{
		if (cursorMode == value) return;
		cursorMode = value;
		invalidate();
	}
	
	public int getCursorMode()
	{
		return cursorMode;
	}
	
	protected void showNotify()
	{
		
	
	}

	public int getItemHeight(int itemIndex)
	{
		int imgHeight, fontHeight = getFontHeight();
		if (imageList != null) imgHeight = imageList.getHeight() + 1;
		else imgHeight = 0;
		return (fontHeight > imgHeight) ? fontHeight : imgHeight;
	}
	
	protected void invalidate()
	{
		if (dontRepaint) return;
		if (isActive()) virtualCanvas.repaint();   
	}
        
	public void setImageList(ImageList list)
	{
		imageList = list;
		invalidate();
	}

	//! Return current image list, used for tree node icons
	/*! If no image list stored, null is returned */
	public ImageList getImageList()
	{
		return imageList;
	}

	protected void checkCurrItem()
	{
		if (currItem < 0) currItem = getSize() - 1;
		if (currItem >= getSize()) currItem = 0;
	}
	
	// check for position of top element of list and change it, if nesessary
	protected void checkTopItem()
	{
		int size = getSize();
		int visCount = getVisCount();
		
		if (size == 0)
		{
			topItem = 0;
			return;
		}
		
		if (currItem >= (topItem + visCount - 1)) topItem = currItem - visCount + 1;
		if (currItem < topItem) topItem = currItem;
		
		if ((size - topItem) <= visCount) topItem = (size > visCount) ? (size - visCount) : 0;
		if (topItem < 0) topItem = 0;
	}

	// Check does item with index visible
	protected boolean visibleItem(int index)
	{
		return (index >= topItem) && (index <= (topItem + getVisCount()));
	}

	protected void storelastItemIndexes()
	{
		lastCurrItem = currItem;
		lastTopItem = topItem;
	}

	protected void repaintIfLastIndexesChanged()
	{
		if ((lastCurrItem != currItem) || (lastTopItem != topItem))
		{
			invalidate();
			if (vlCommands != null) vlCommands.onCursorMove(this);
		}
	}

	protected void moveCursor(int step, boolean moveTop)
	{
		setCLCI();
		storelastItemIndexes();
		if (moveTop && (cursorMode == SEL_NONE)) topItem += step;
		currItem += step;
		checkCurrItem();
		checkTopItem();
		repaintIfLastIndexesChanged();
	}

	protected void itemSelected() {}

	
	private int getExtendedGameAction(int keyCode)
	{
        String strCode = null;
       
        try 
        {
            strCode = virtualCanvas.getKeyName(keyCode).toLowerCase();
        } 
        catch(IllegalArgumentException e) {} // Do nothing
        if (strCode != null) 
        {
            if ("soft1".equals(strCode) || "soft 1".equals(strCode)
                    || "soft_1".equals(strCode) || "softkey 1".equals(strCode)
                    || strCode.startsWith("left soft")) 
            {
                return KEY_CODE_LEFT_MENU;
            }
            if ("soft2".equals(strCode) || "soft 2".equals(strCode)
                    || "soft_2".equals(strCode) || "softkey 4".equals(strCode)
                    || strCode.startsWith("right soft")) {
                return KEY_CODE_RIGHT_MENU;
            }
            
            if ("on/off".equals(strCode) || "back".equals(strCode)) {
                return KEY_CODE_BACK_BUTTON;
            }
        }
		
        try
        {
        	int gameAct = virtualCanvas.getGameAction(keyCode);
        	if (gameAct > 0) return gameAct;
        }
        catch (Exception e) {} // Do nothing
        switch (keyCode)
        {
        case -6: case -21: case 21: case 105: case -202: case 113: case 57345:
        	return KEY_CODE_LEFT_MENU;
        	
        case -7: case -22: case 22: case 106: case -203: case 112: case 57346:
        	return KEY_CODE_RIGHT_MENU;
        	
        case -11: 
        	return KEY_CODE_BACK_BUTTON;
        }
		
		
        
        return KEY_CODE_UNKNOWN;
	}
	
	 public static int getWidth()
	 	         {
 	                 return virtualCanvas.getWidth();
	 	         }
	 	 
	 	         public static int getHeight()
	 	         {
	 	                 return virtualCanvas.getHeight();
	 	         }
	public void setCLCI(){}
	
	private static final int KEY_CODE_LEFT_MENU = 1000001;
	private static final int KEY_CODE_RIGHT_MENU = 1000002;
	private static final int KEY_CODE_BACK_BUTTON = 1000003;
	private static final int KEY_CODE_UNKNOWN = 1000004;
	
	public void makeClick()
	{
		uiState = UI_STATE_NORMAL;
		VMenuItem item = (VMenuItem) menuItems.elementAt(curMenuItemIndex);
		item.performAction();
		invalidate();
	}
	
	private static void moveSelectedMenuItem(int offset, int size, boolean moveOnlyView)
	 	         {
	 	                 if (!moveOnlyView)
	 	                 {
	 	                         curMenuItemIndex += offset;
	 	                         if (curMenuItemIndex >= size) curMenuItemIndex = 0;
	 	                         if (curMenuItemIndex < 0) curMenuItemIndex = size - 1;
	 	                         if (curMenuItemIndex >= topMenuItem+visibleItemsMenuCount)
	 	                                 topMenuItem = curMenuItemIndex-visibleItemsMenuCount+1;
	 	                         if (curMenuItemIndex < topMenuItem)
	 	                                 topMenuItem = curMenuItemIndex;
	 	                 }
	 	                 else
	 	                 {
	 	                         topMenuItem += offset;
	 	                         if (topMenuItem < 0) topMenuItem = size - 1;
	 	                         if (topMenuItem >= size-visibleItemsMenuCount) topMenuItem = size-visibleItemsMenuCount;
	 	                 }
         }	         
	
	private void keyReaction(int keyCode)
	{
		setCLCI();
		lastManuIndex = curMenuItemIndex;
		try
		{
		
			switch (getExtendedGameAction(keyCode))
			{
			case KEY_CODE_LEFT_MENU:
				if (uiState == 0) lastLeftKey = true;
				MIP.performSoftAction(false, this);
				break;
				
				
			case KEY_CODE_RIGHT_MENU:
				if (uiState == 0) lastLeftKey = false;
				MIP.performSoftAction(true, this);
				break;
				
				
			case KEY_CODE_BACK_BUTTON:
				if (uiState == 1) 
				{
					uiState = 0;
					invalidate();
				}
				else MIP.takeMeBack(this);
				break;
			case Canvas.DOWN:
                                switch(uiState) {
                                    case UI_STATE_MENU_VISIBLE:
                                        moveSelectedMenuItem(1, menuItems.size(), false);
                                        break;
                                    case UI_STATE_MENU_MB_VISIBLE:
                                    case UI_STATE_MB_VISIBLE:
                                        scrollMesBox(1);
                                        break;
                                    default:
                                        moveCursor(1, false); 
                                        break;
                                } 
				break;
			case Canvas.UP:
				switch(uiState) {
                                    case UI_STATE_MENU_VISIBLE:
                                        moveSelectedMenuItem(-1, menuItems.size(), false);
                                        break;
                                    case UI_STATE_MENU_MB_VISIBLE:
                                    case UI_STATE_MB_VISIBLE:
                                        scrollMesBox(-1);
                                        break;
                                    default:
                                        moveCursor(-1, false); 
                                        break;
                                } 
				break;
			case Canvas.FIRE:
				if (uiState == 1)
				{
					makeClick();
				}
				else
				{
					itemSelected();
					if (vlCommands != null) vlCommands.onItemSelected(this);
				}
				break;
			}
		}
		catch (Exception e) // getGameAction throws exception on motorola
		{                   // when opening flipper & SE K700i throws it in many cases
			
		}
		 if (uiState == 1)
		{
                    invalidate();
                    return;
		}
		switch (keyCode)
		{
		case Canvas.KEY_NUM1:
			storelastItemIndexes();
			currItem = topItem = 0;
			repaintIfLastIndexesChanged();
			break;
			
		case Canvas.KEY_NUM7:
			storelastItemIndexes();
			int endIndex = getSize() - 1;
			currItem = endIndex;
			checkTopItem();
			repaintIfLastIndexesChanged();
			break;
		case Canvas.KEY_NUM3:
			moveCursor(-getVisCount(), false);
			break;
			
		case Canvas.KEY_NUM9:
			moveCursor(getVisCount(), false);
			break;
		}

		
	}
	
	public void doKeyreaction(int keyCode, int type)
	{
		switch (type)
		{
		case KEY_PRESSED:
			//if (!Options.getBoolean(Options.OPTION_LIGHT_MANUAL))				flashBklt(Options.getInt(Options.OPTION_LIGHT_TIMEOUT)*1000);
			if (vlCommands != null) vlCommands.onKeyPress(this, keyCode, type);
			keyReaction(keyCode);
			break;
		case KEY_REPEATED:
			switch(keyCode)
			{
				case -5: case -11: case -6: case -21: case 21: case 105: case -202: case 113: case 57345: case -7: case -22: case 22: case 106: case -203: case 112: case 57346:
					break;
				default:
					if (vlCommands != null) vlCommands.onKeyPress(this, keyCode, type);
					keyReaction(keyCode);
					break;
			}
		break;
		}
	}

	protected void keyPressed(int keyCode)
	{
		doKeyreaction(keyCode, KEY_PRESSED);
	}

	protected void keyRepeated(int keyCode)
	{
		doKeyreaction(keyCode, KEY_REPEATED);
	}
	
	protected void keyReleased(int keyCode)
	{
		doKeyreaction(keyCode, KEY_RELEASED);
	}
	
	//#sijapp cond.if target is "MIDP2"#
	private static long lastPointerTime = 0;
	private static int lastPointerYCrd = -1;
	private static int lastPointerXCrd = -1;
	private static int lastPointerTopItem = -1;
	
	protected void pointerDragged(int x, int y)
	{
		if (lastPointerTopItem == -1) return;
		int height = getHeightInternal()-getCapHeight();
		int itemCount = getSize();
		int visCount = getVisCount();
		if (itemCount == visCount) return;
		storelastItemIndexes();
		topItem = lastPointerTopItem+(itemCount)*(y-lastPointerYCrd)/height;
		if (topItem < 0) topItem = 0;
		if (topItem > (itemCount-visCount)) topItem = itemCount-visCount; 
		repaintIfLastIndexesChanged();
	}
	
	protected boolean pointerPressedOnUtem(int index, int x, int y)
	{
		return false;
	}
	
	static int abs(int value)
	{
		return (value < 0) ? -value : value;
	}
	
	protected void pointerPressed(int x, int y)
	{
		int itemY1 = getCapHeight();
		
		// is pointing on scroller
		if (x >= (getWidthInternal()-6))
		{
			if ((srcollerY1 <= y) && (y < srcollerY2))
			{
				lastPointerYCrd = y;
				lastPointerTopItem = topItem;
			}
			return;
		}
		
		// is pointing on data area
		lastPointerTopItem = -1;
		
		int size = getSize();
		for (int i = topItem; i < size; i++)
		{
			int height = getItemHeight(i);
			int itemY2 = itemY1+height;
			if ((itemY1 <= y) && (y < itemY2))
			{
				setCurrentItem(i);
				
				if (pointerPressedOnUtem(i, x, y) == false)
				{
					long time = System.currentTimeMillis();
					if (((time-lastPointerTime) < 500) && 
					     (abs(x-lastPointerXCrd) < 10) &&
					     (abs(y-lastPointerYCrd) < 10))
					{
						itemSelected();
						if (vlCommands != null) vlCommands.onItemSelected(this);
					}
					lastPointerTime = time;
				}
				break;
			}
			itemY1 = itemY2;
		}
		
		lastPointerXCrd = x;
		lastPointerYCrd = y;
	}
	//#sijapp cond.end#

	//! Set caption text for list
	public void setCaption(String capt)
	{
		if (caption != null) if (caption.equals(capt)) return;
		caption = capt;
		
		invalidate();
	}
	
	public String getCaption()
	{
		return caption; 
	}

	public void setTopItem(int index)
	{
		storelastItemIndexes();
		currItem = topItem = index;
		checkTopItem();
		repaintIfLastIndexesChanged();
	}

	// public void setCurrentItem(int index)
	public void setCurrentItem(int index)
	{
		storelastItemIndexes();
		currItem = index;
		checkTopItem();
		repaintIfLastIndexesChanged();
	}

	// public int getCurrIndex()
	public int getCurrIndex()
	{
		return currItem;
	}
	
	// Return height of caption in pixels
	private int getCapHeight()
	{
		int capHeight = 0;
		if (caption != null) capHeight = capFont.getHeight()+2;
		if (capImage != null)
		{
			int imgHeight = capImage.getHeight()+2; 
			if (imgHeight > capHeight) capHeight = imgHeight;
		}
			 
		return capHeight+1;
	}
	
	
	protected int drawCaption(Graphics g)
	{
		if (caption == null) return 0;
		
		int width = getWidthInternal();
		g.setFont(capFont);
		int height = getCapHeight();
		drawGradient(g, Options.getInt(Options.OPTION_COLOR2), Options.getInt(Options.OPTION_COLOR1), 0, 0, width, height - 4);
		g.setColor(Options.getInt(Options.OPTION_COLOR1));
		g.drawLine(0, height - 4, width, height - 4);
		g.drawLine(0, height - 3, width, height - 3);
		g.setColor(0x8C8C8C);
		g.drawLine(0, height - 2, width, height - 2);
		g.setColor(bkgrndColor);
		g.drawLine(0, height - 1, width, height - 1);
		
		
		int x = 1;
		if (Options.getBoolean(Options.OPTION_HASSHIFT)) x+= Options.getInt(Options.OPTION_SHIFT);
		if (capImage != null)
		{
			g.drawImage(capImage, x, (height-capImage.getHeight())/2, Graphics.TOP | Graphics.LEFT);
			x += capImage.getWidth()+1;
		}
		if (capImage2 != null)
		{
			g.drawImage(capImage2, x, (height-capImage2.getHeight())/2, Graphics.TOP | Graphics.LEFT);
			x += capImage2.getWidth()+1;
		}
		
		g.setColor(capTxtColor);
		g.drawString(caption, x, (height-capFont.getHeight())/2, Graphics.TOP | Graphics.LEFT);
		return height - 1;
		
	}
	
	
	
	
	protected int drawButtons(Graphics g)
	{
		int width = getWidthInternal();
		g.setFont(capFont);
		int height = getHeightInternal();
		int drw = height - 20;
		drawGradient(g, Options.getInt(Options.OPTION_COLOR1), Options.getInt(Options.OPTION_COLOR2), 0, drw, width, height);
		g.setColor(0);
		g.drawLine(0, drw - 1, width, drw - 1);
		g.setColor(0x8C8C8C);
		g.drawLine(0, drw - 2, width, drw - 2);
		g.setColor(capTxtColor);
                String left = "Left", right = "Right"; 
                switch (uiState) {
                    case UI_STATE_NORMAL:
                        right = rightKey;
                        left = leftKey;
                        break;
                    case UI_STATE_MENU_VISIBLE:
                        right = rightMenuKey;
                        left = leftMenuKey;
                        break;
                    case UI_STATE_MENU_MB_VISIBLE:
                    case UI_STATE_MB_VISIBLE:
                        switch (((mesBoxItem)mesBoxItems.firstElement()).type) {
                            case MB_OK:
                                left = "";
                                right = ResourceBundle.getString("ok");
                                break;
                            case MB_YESNO:
                                left = ResourceBundle.getString("no");
                                right = ResourceBundle.getString("yes");
                                break;
                            case MB_MESSAGE:
                                left = "";
                                right = ResourceBundle.getString("close");
                                break;
                        }
                        break;
                }
                g.drawString(Options.getBoolean(Options.OPTION_SWAPSOFTS) ? right : left, 2, height - (capFont.getHeight() / 2) + 1, Graphics.BASELINE | Graphics.LEFT);
                g.drawString(Options.getBoolean(Options.OPTION_SWAPSOFTS) ? left : right, width - 3, height - (capFont.getHeight() / 2) + 1, Graphics.BASELINE | Graphics.RIGHT);
		g.drawString(Options.getBoolean(Options.OPTION_SECONDS) ? Util.getDateString(true, true) : Util.getDateString(true, false) , width / 2, height - (capFont.getHeight() / 2) + 1, Graphics.BASELINE | Graphics.HCENTER);
		return height;
	}
	
	
	protected static void drawGradient(Graphics g, int color1, int color2, int x1, int y1, int x2, int y2)
	{
		performGradient(g, color1, color2, x1, y1, x2, y2, true);
	}
	
	protected static void drawGradient(Graphics g, int color1, int color2, int x1, int y1, int x2, int y2, boolean half)
	{
		performGradient(g, color1, color2, x1, y1, x2, y2, half);
	}
	
	protected static void performGradient(Graphics g, int color1, int color2, int x1, int y1, int x2, int y2, boolean half)
	{
		int[] interp;
		int height = half ? (y2 - y1) / 2 : y2 - y1;
		//Making colors array
		int a1 = (color1 >> 24) & 0xff;
        int r1 = (color1 >> 16) & 0xff;
        int g1 = (color1 >>  8) & 0xff;
        int b1 = (color1) & 0xff;
        int da = ((color2 >> 24) & 0xff) - a1;
        int dr = ((color2 >> 16) & 0xff) - r1;
        int dg = ((color2 >>  8) & 0xff) - g1;
        int db = ((color2) & 0xff) - b1;
        interp = new int[257];
        for (int i = 0; i <= 256; i++) 
		{
            int rgb =
            (((int) (a1 + da * i / 256)) << 24) |
            (((int) (r1 + dr * i / 256)) << 16) |
            (((int) (g1 + dg * i / 256)) <<  8) |
            (((int) (b1 + db * i / 256))      );
            interp[i] = rgb;
        }
		//painting
		if (half)
		{
			for(int i=0;i<height*2;i++) 
			{
				if (i + 1 > height) 
				{
					g.setColor(color2);
				}
				else
				{
					g.setColor(interp[(interp.length*i/height)]);
				}
				g.drawLine(x1, y1 + i, x2, y1 + i);
			}
			g.drawLine(x1, y2 - 1, x2, y2 - 1);
		}
		else
		{
			for(int i=0;i<height;i++) 
			{
				g.setColor(interp[(interp.length*i/height)]);
				g.drawLine(x1, y1 + i, x2, y1 + i);
			}
		}
	}
	
	
	
	
	protected boolean isItemSelected(int index)
	{
		return ((currItem == index) && (cursorMode != SEL_NONE));
	}

	// private int drawItem(int index, Graphics g, int top_y, int th, ListItem item)
	private int drawItem(int index, Graphics g,	int yCrd, int itemWidth, int itemHeight, int fontHeight)
	{
		try{drawItemData(g, index, 2, yCrd, itemWidth - itemHeight / 3, yCrd + itemHeight, fontHeight);}
		catch (Exception e) {}
		return yCrd + itemHeight;
	}
	
	private static int srcollerY1 = -1;
	private static int srcollerY2 = -1;

	// Draw scroller is items doesn't fit in VL area 
	private void drawScroller(Graphics g, int topY, int visCount)
	{
		int width = getWidthInternal() - 1;
		int height = getHeightInternal() - 18;
		g.setStrokeStyle(Graphics.SOLID);
		g.setColor(0x00);
		g.drawLine(width, topY, width, height);
		g.drawLine(width - 5, topY, width - 5, height);
		g.setColor(0x4C4C4C);
		g.drawLine(width - 4, topY, width - 4, height);
		g.setColor(0x8C8C8C);
		g.drawLine(width - 3, topY, width - 3, height);
		g.setColor(0xCCCCCC);
		g.drawLine(width - 2, topY, width - 2, height);
		g.setColor(0xE5E5E5);
		g.drawLine(width - 1, topY, width - 1, height);
		
		int itemCount = getSize();
		boolean haveToShowScroller = ((itemCount > visCount) && (itemCount > 0));
		
		if (haveToShowScroller)
		{
			int drawSize = getDrawHeight();
			int sliderSize = ((drawSize-topY)*visCount/itemCount) + 1;
			if (sliderSize < 7) sliderSize = 7;
			srcollerY1 = (topItem * (drawSize - sliderSize - topY) / (itemCount-visCount) + topY);
			srcollerY2 = srcollerY1 + sliderSize;
			g.setColor(0xE58D8D);
			g.drawLine(width - 4, srcollerY1, width - 4, srcollerY2);
			g.drawLine(width - 4, srcollerY1 + 1, width - 1, srcollerY1 + 1);
			g.setColor(0xA03030);
			g.drawLine(width - 3, srcollerY1, width - 3, srcollerY2);
			g.setColor(0xA01010);
			g.drawLine(width - 2, srcollerY1, width - 2, srcollerY2);
			g.setColor(0x6F0B0B);
			g.drawLine(width - 1, srcollerY1, width - 1, srcollerY2);
			//g.setColor(Options.getInt(Options.OPTION_COLOR1));
			g.drawLine(width - 1, srcollerY2 - 1, width - 1, srcollerY2 - 1);
			
			g.setColor(0x00);
			g.drawLine(width - 5, srcollerY1, width, srcollerY1);
			g.drawLine(width - 5, srcollerY2, width, srcollerY2);
		}
		
	}
	
	static public void drawRect(Graphics g, int color1, int color2, int x1, int y1, int x2, int y2)
	{
		int r1 = ((color1 & 0xFF0000) >> 16);
		int g1 = ((color1 & 0x00FF00) >> 8);
		int b1 =  (color1 & 0x0000FF);
		int r2 = ((color2 & 0xFF0000) >> 16);
		int g2 = ((color2 & 0x00FF00) >> 8);
		int b2 =  (color2 & 0x0000FF);
		int count = (y2-y1)/3;
		if (count < 0) count = -count;
		if (count < 8) count = 8;
		y2++;
		x2++;
		for (int i = 0; i < count; i++)
		{
			int crd1 = i * (y2 - y1) / count + y1;
			int crd2 = (i + 1) * (y2 - y1) / count + y1;
			if (crd1 == crd2) continue;
			g.setColor(i * (r2 - r1) / (count-1) + r1, i * (g2 - g1) / (count-1) + g1, i * (b2 - b1) / (count-1) + b1);
			g.fillRect(x1, crd1, x2-x1, crd2-crd1);
		}
	}
 
	private static Font mesBoxFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        private static Font mesBoxCaptionFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);        
        static private int topLine = 0;
        int maxVisible = 0;
        static Vector stringList = new Vector();  
        private static Vector mesBoxItems = new Vector();
        static class mesBoxItem {
            String caption, text;
            byte type; // 0 - message
        }
        private int lastUiState = 0;
                
        final public static byte MB_OK      = 0;
        final public static byte MB_YESNO   = 1;
        final public static byte MB_MESSAGE = 2;
        
        private void scrollMesBox(int offset) {
            System.out.println(topLine);
            System.out.println(maxVisible);
            System.out.println(stringList.size());
            if ((topLine == 0) & (offset == -1)) return;
            if ((topLine + maxVisible >= stringList.size()) & (offset == 1)) return;
            topLine += offset;
            invalidate();
        }
        
        public void showMesBox(String caption, String text, byte type) {
            System.out.println("showMesBox");
            if (mesBoxItems.size() == 0) {
                switch (type) {
                    case MB_OK: MIP.setTempDsp(MIP.DSP_MB_OK); break;
                    case MB_YESNO: MIP.setTempDsp(MIP.DSP_MB_YESNO); break;
                    case MB_MESSAGE: MIP.setTempDsp(MIP.DSP_MB_MESSAGE); break;
                }
                if (uiState == UI_STATE_MENU_VISIBLE) {
                    uiState = UI_STATE_MENU_MB_VISIBLE;
                } else {
                    uiState = UI_STATE_MB_VISIBLE;
                }
            }
            mesBoxItem item = new mesBoxItem();
            item.caption = caption;
            item.text = text;
            item.type = type;
            mesBoxItems.addElement(item);
            invalidate();
        }
        
        public void closeLastMesBox() {
            mesBoxItems.removeElementAt(0);
            topLine = 0;
            stringList.removeAllElements();
            MIP.setSavedDsp();
            if (mesBoxItems.size() != 0) {
                switch (((mesBoxItem)mesBoxItems.firstElement()).type) {
                    case MB_OK: MIP.setTempDsp(MIP.DSP_MB_OK); break;
                    case MB_YESNO: MIP.setTempDsp(MIP.DSP_MB_YESNO); break;
                    case MB_MESSAGE: MIP.setTempDsp(MIP.DSP_MB_MESSAGE); break;
                }
            } else {
                if (uiState == UI_STATE_MENU_MB_VISIBLE) {
                    uiState = UI_STATE_MENU_VISIBLE;
                } else {
                    uiState = UI_STATE_NORMAL;
                }
            }
            invalidate();
        }
        
        static public int getMBCount() {
            return mesBoxItems.size();
        }
        
        private void drawMesBoxes(Graphics g) {
            if (mesBoxItems.size() == 0) return;
            
            String caption = ((mesBoxItem)mesBoxItems.firstElement()).caption;
            int width = getWidthInternal() - 20;
            int height = getHeightInternal() - 50;
            int captionHeight = mesBoxCaptionFont.getHeight() + 4;
            final int x = 7;
            final int y = 20;
            //Vector stringList = new Vector();
            
            g.setColor(0x000000);
            g.drawRect(x, y, width, height);
            performGradient(g, 0xFFFFFF, 0xBBBBBB, 8, 21, width+6, captionHeight+20, true);
            performGradient(g, 0xFFFFFF, 0xBBBBBB, 8, captionHeight+21, width+6, height+20, false);
            g.setColor(0x00);
            g.drawLine(x+1, y+captionHeight, width + x - 1, y+captionHeight);
            
            g.setStrokeStyle(Graphics.SOLID);
            g.setColor(0x00);
            g.drawLine(width + 2, 21 + captionHeight, width + 2, 19 + height);
            g.setColor(0x4C4C4C);
            g.drawLine(width + 3, 21 + captionHeight, width + 3, 19 + height);
            g.setColor(0x8C8C8C);
            g.drawLine(width + 4, 21 + captionHeight, width + 4, 19 + height);
            g.setColor(0xCCCCCC);
            g.drawLine(width + 5, 21 + captionHeight, width + 5, 19 + height);
            g.setColor(0xE5E5E5);
            g.drawLine(width + 6, 21 + captionHeight, width + 6, 19 + height);
            
            height = height - captionHeight;
            width = width - 5;
            maxVisible = height / mesBoxFont.getHeight();
            
            g.setColor(0x000000);
            g.setFont(mesBoxCaptionFont);
            g.drawString("[" + String.valueOf(mesBoxItems.size()) + "] " + caption, x + 1, y + 1,  Graphics.LEFT|Graphics.TOP);
            g.setFont(mesBoxFont);
            
            if (stringList.size() == 0) {
                int j = 0;  // substring new end
                int n = 0;  // end of line
                int k = 0;  // substring begin
                int i = 0;  // search begin
                String text = ((mesBoxItem)mesBoxItems.firstElement()).text.trim();
               
                for (; k < text.length();) {
                    j = text.indexOf(' ', i); // space chr
                    n = text.indexOf('\n', i); // /n chr
                    if ((n > -1) & j < n) j = n; // which chr have little index
                    if((j < 0) || (mesBoxFont.substringWidth(text, k, j-k) > width - 5) || (text.charAt(j) == '\n')) {
                        if (j < 0) { // end of text
                            j = text.length();
                            i = j;
                        }
                        if (i == k) { // line contain 1 word
                            i = j;
                        }
                        for(; mesBoxFont.substringWidth(text, k, i-k) > width; i--); // wrap word if too long
                        stringList.addElement(text.substring(k, i).trim());
                        k = i;
                    } else {
                        i = j+1;
                    }
                }
            }
            
            if  (maxVisible < stringList.size()) {
                    int sliderSize = ((height)*maxVisible/stringList.size());
                    if (sliderSize < 7) sliderSize = 7;
                    srcollerY1 = (topLine * (height - sliderSize) / (stringList.size()-maxVisible)) + y + captionHeight;
                    srcollerY2 = srcollerY1 + sliderSize - 1;
                    g.setStrokeStyle(Graphics.SOLID);
                    g.setColor(0xE58D8D);
                    g.drawLine(x + width + 1, srcollerY1, x + width + 1, srcollerY2);
                    g.drawLine(x + width + 1, srcollerY1 + 1, x + width + 4, srcollerY1 + 1);
                    g.setColor(0xA03030);
                    g.drawLine(x + width + 2, srcollerY1, x + width + 2, srcollerY2);
                    g.setColor(0xA01010);
                    g.drawLine(x + width + 3, srcollerY1, x + width + 3, srcollerY2);
                    g.setColor(0x6F0B0B);
                    g.drawLine(x + width + 4, srcollerY1, x + width + 4, srcollerY2);
                    
                    g.setColor(0x00);
                    g.drawLine(x + width, srcollerY1, x + width + 4, srcollerY1);
                    g.drawLine(x + width, srcollerY2, x + width + 4, srcollerY2);
                }
            
            int visibleItems = stringList.size() - topLine;
            if (visibleItems > maxVisible) visibleItems = maxVisible;
            for (int i = topLine, t = 0; i < visibleItems + topLine; i++, t++) {
                g.drawString((String)stringList.elementAt(i), 10, captionHeight + y + 1 + t*mesBoxFont.getHeight(),  Graphics.LEFT|Graphics.TOP);
            }
            return;
        }
        
	//! returns font height
	public int getFontHeight()
	{
		return getQuickFont(Font.STYLE_PLAIN).getHeight();
	}  

	static public Image getSplashImage()
	{
		if (VirtualList.splash == null)
		{
			try
			{
				VirtualList.splash = Image.createImage(VirtualList.SPLASH_IMG);
			}
			catch (Exception e)
			{
				VirtualList.splash = SplashCanvas.getSplashImage();
			}
		}
		return VirtualList.splash;
	}
	
		
	public void setUIState(int ui)
	{
		uiState= ui;
		invalidate();
	}
	
	public int getUIState()
	{
		return uiState;
	}
		
	//NEW MENU STYLE!
	public static int curMenuItemIndex;
	private int uiState;
	//private boolean drawOnlyMenu = false;
	public Vector menuItems = new Vector();
	static public int getInverseColor(int color)
	 	         {
	 	                 int r = (color & 0xFF);
	 	                 int g = ((color & 0xFF00) >> 8);
	 	                 int b = ((color & 0xFF0000) >> 16);
	 	                 return ((r+g+b) > 3*127) ? 0 : 0xFFFFFF;
	 	         }
	private static final int UI_STATE_NORMAL = 0;
	private static final int UI_STATE_MENU_VISIBLE = 1;
        private static final int UI_STATE_MB_VISIBLE = 2;
        private static final int UI_STATE_MENU_MB_VISIBLE = 3;
	private static Font menuItemsFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);
	
	
	public void initMenu()
	{
		int drawHeight = getDrawHeight() - 8;
		int menuItemsCount = menuItems.size();
		int menuHeight = getMenuHeight(menuItemsCount);
		if (menuHeight > drawHeight)
		{
			visibleItemsMenuCount = drawHeight/getMenuHeight(1);
			topMenuItem = 0;
		}
		else
		{
			visibleItemsMenuCount = menuItemsCount;
			topMenuItem = 0;
		}
	}
	
	private void drawMenuItems(Graphics g, int menuBarHeight)
	{
		if ((uiState == UI_STATE_MENU_VISIBLE) || (uiState == UI_STATE_MENU_MB_VISIBLE)) drawManuItems(g, menuItems, getHeightInternal()-21, lastLeftKey, 0, 0);
		return;
	}
	 	 
	private void drawManuItems(Graphics g, Vector items, int bottom, boolean left, int curX, int curY)
	{
		int fontHeight = menuItemsFont.getHeight();
		int layer = fontHeight/3;
		int vert_layer = fontHeight/2;
		int itemsCount = menuItems.size();
	 	boolean showMenuIcons = Options.getBoolean(Options.OPTION_SHOWMENUICONS);
		int width = 0;
		int height = getMenuHeight(visibleItemsMenuCount);
		int itemHeight = getMenuHeight(1);
		for (int i = items.size()-1; i >= 0; i--)
		{
			VMenuItem item = (VMenuItem)items.elementAt(i);
			int txtWidth = menuItemsFont.stringWidth(item.getText());
			if (txtWidth > width) width = txtWidth;
		}
		width += layer*2;
	 	if (showMenuIcons) width+=17; 
		if (width > getWidth()-4) width = getWidth()-4;
		int y = bottom-height-1;
		int x;
		if (left) x = 0;
		else {
                    x = getWidthInternal()-width-1;
                    if  (visibleItemsMenuCount < items.size()) {
                        x = x - 5;
                        if (width > getWidth() - 9) {
                            width = width - 5;
                        }
                    }
                }
		g.setColor(transformColorLight(bkgrndColor, 42));
		
		
		drawGradient(g, bkgrndColor, transformColorLight(bkgrndColor, 48), x, y, x + width, y + (height / 2), false);
		drawGradient(g, transformColorLight(bkgrndColor, 48), bkgrndColor, x, y + height / 2, x + width, y + height, false);
		g.setFont(menuItemsFont);
		int itemY = y;
	 	
		for (int i = topMenuItem, j = 0; j < visibleItemsMenuCount; i++, j++)
		{
			if (i == curMenuItemIndex)
			{
				g.setColor(0xFFFFFF);
				g.drawLine(x, itemY, x + width, itemY);
				drawGradient(g, 0xE1E1E1, 0xFFFFFF, x, itemY + 1, x + width, itemY +1 + itemHeight, false);
				g.setColor(Options.getInt(Options.OPTION_COLOR2));
			}
			itemY += itemHeight;
		}
		g.drawRect(x, y - 1, width, height + 1);
		itemY = y;
		for (int i = topMenuItem, j = 0; j < visibleItemsMenuCount; i++, j++)
		{
			VMenuItem item = (VMenuItem)items.elementAt(i);
			g.setColor((i == curMenuItemIndex) ? 0x000000 /*transformColorLight(Options.getInt(Options.OPTION_COLOR6), 48)*/ : 0xFFFFFF /*capTxtColor*/);
			try{
				if (showMenuIcons) g.drawImage(getILImage(item.usedIL, item.getImageIndex()), x+layer-1, itemY+1, Graphics.TOP | Graphics.LEFT);
			}
			catch (Exception e){}
			g.drawString(item.getText(),showMenuIcons ? x+layer + 17 : x+layer, itemY+2, Graphics.LEFT|Graphics.TOP);
			itemY += itemHeight;
		}
		if  (visibleItemsMenuCount < items.size()) {
                    g.setStrokeStyle(Graphics.SOLID);
                    g.setColor(Options.getInt(Options.OPTION_COLOR2));
                    g.drawLine(x + width + 5, y, x + width + 5, y + height - 1);
                    g.drawLine(x + width, y - 1, x + width + 5, y - 1);
                    g.drawLine(x + width, y + height, x + width + 5, y + height);
                    g.setColor(0x00);
                    g.drawLine(x + width, y, x + width, y + height - 1);
                    g.setColor(0x4C4C4C);
                    g.drawLine(x + width + 1, y, x + width + 1, y + height - 1);
                    g.setColor(0x8C8C8C);
                    g.drawLine(x + width + 2, y, x + width + 2, y + height - 1);
                    g.setColor(0xCCCCCC);
                    g.drawLine(x + width + 3, y, x + width + 3, y + height - 1);
                    g.setColor(0xE5E5E5);
                    g.drawLine(x + width + 4, y, x + width + 4, y + height - 1);

                    int sliderSize = ((height)*visibleItemsMenuCount/items.size());
                    if (sliderSize < 7) sliderSize = 7;
                    srcollerY1 = (topMenuItem * (height - sliderSize) / (items.size()-visibleItemsMenuCount)) + y;
                    srcollerY2 = srcollerY1 + sliderSize - 1;
                    g.setColor(0xE58D8D);
                    g.drawLine(x + width + 1, srcollerY1, x + width + 1, srcollerY2);
                    g.drawLine(x + width + 1, srcollerY1 + 1, x + width + 4, srcollerY1 + 1);
                    g.setColor(0xA03030);
                    g.drawLine(x + width + 2, srcollerY1, x + width + 2, srcollerY2);
                    g.setColor(0xA01010);
                    g.drawLine(x + width + 3, srcollerY1, x + width + 3, srcollerY2);
                    g.setColor(0x6F0B0B);
                    g.drawLine(x + width + 4, srcollerY1, x + width + 4, srcollerY2);
                    
                    g.setColor(0x00);
                    g.drawLine(x + width, srcollerY1, x + width + 4, srcollerY1);
                    g.drawLine(x + width, srcollerY2, x + width + 4, srcollerY2);

//                    g.setColor(0xFFFFFF);
//                    System.out.println("yes");
//                    g.drawRect(x+width, y-1, 4, height+1);
                }
	}
	
	static private int visibleItemsMenuCount;
	static private int topMenuItem;
	
	private static int getMenuHeight(int count)
	{
		int fontHeight = menuItemsFont.getHeight();
		boolean showMenuIcons = Options.getBoolean(Options.OPTION_SHOWMENUICONS);
		if ((showMenuIcons) && (18 > fontHeight))  fontHeight = 18;
		return fontHeight*count;
	}
	
	
	
	
	
	
	
	
	
	
	
		
	// private int drawItems(Graphics g, int top_y)
	private int drawItems(Graphics g, int top_y, int fontHeight)
	{
		int grCursorY1 = -1, grCursorY2 = -1; 
		int height = getHeightInternal() - 20;
		int size = getSize();
		int i, y;
		int itemWidth = getWidthInternal()-6;
		
		// Fill background
		
		g.setColor(bkgrndColor);
		g.fillRect(0, top_y, itemWidth, height-top_y);
		
		// Draw cursor
		y = top_y;
		for (i = topItem; i < size; i++)
		{
			int itemHeight = getItemHeight(i);
			if (isItemSelected(i))
			{
				if (grCursorY1 == -1) grCursorY1 = y;
				grCursorY2 = y+itemHeight-1; 
			}
			y += itemHeight;
			if (y >= height) break;
		}
		
		if (grCursorY1 != -1)
		{
			grCursorY1--;
			grCursorY2++;
			drawGradient(g, transformColorLight(Options.getInt(Options.OPTION_COLOR10), -48), Options.getInt(Options.OPTION_COLOR10), 0, grCursorY1, itemWidth, grCursorY2 - 2);
			g.setStrokeStyle(Graphics.SOLID);
			g.setColor(Options.getInt(Options.OPTION_COLOR10));
			boolean isCursorUpper = (topItem >= 1) ? isItemSelected(topItem-1) : false;  
			if (!isCursorUpper) g.drawLine(1, grCursorY1, itemWidth-2, grCursorY1);
			g.drawLine(0, grCursorY1+1, 0, grCursorY2-3);
			g.drawLine(itemWidth-1, grCursorY1+1, itemWidth-1, grCursorY2-3);
			g.drawLine(1, grCursorY2 - 2, itemWidth-2, grCursorY2 - 2);
		}
		if (Options.getBoolean(Options.OPTION_BACKGROUND))
		{
			Image bImage = getSplashImage();
			if (bImage != null)
			{
				g.drawImage(bImage, this.getWidth() / 2, this.getHeight() / 2, Graphics.HCENTER | Graphics.VCENTER);
			}
		
		}
		// Draw items
		paintedItem.clear();
		y = top_y;
		for (i = topItem; i < size; i++)
		{
			int itemHeight = getItemHeight(i);
			g.setStrokeStyle(Graphics.SOLID);
			int x1 = 1;
			int x2 = itemWidth-2;
			int y1 = y;
			int y2 = y + itemHeight;
			drawItemData(g, i, x1, y1, x2, y2, fontHeight);
			y += itemHeight;
			if (y >= height) break;
		}
		
		return y;
	}
	
	static protected int transformColorLight(int color, int light)
	{
		int r = (color & 0xFF) + light;
		int g = ((color & 0xFF00) >> 8) + light;
		int b = ((color & 0xFF0000) >> 16) + light;
		if (r < 0) r = 0;
		if (r > 255) r = 255;
		if (g < 0) g = 0;
		if (g > 255) g = 255;
		if (b < 0) b = 0;
		if (b > 255) b = 255;
		return r | (g << 8) | (b << 16);
	}

	void init()
	{
	}
	
	void destroy()
	{
	}

	public String copyText()
	{
		return null;
	}

	public void paintAllOnGraphics(Graphics graphics) {
            int visCount = getVisCount();
            int y = drawCaption(graphics);
            drawItems(graphics, y + 1, getFontHeight());
            drawScroller(graphics, y, visCount);
            drawButtons(graphics);
            drawMenuItems(graphics, 20);
            drawMesBoxes(graphics);
        }

	static Image bDIimage = null;

	protected void paint(Graphics g)
	{
		if (dontRepaint) return;
		
		if (virtualCanvas.isDoubleBuffered())
		{
			paintAllOnGraphics(g);
		}
		else
		{
			try
			{
				if (bDIimage == null) bDIimage = Image.createImage(getWidthInternal(), getHeightInternal());
                                paintAllOnGraphics(bDIimage.getGraphics());
				g.drawImage(bDIimage, 0, 0, Graphics.TOP | Graphics.LEFT);
			}
			catch (Exception e)
			{
                                paintAllOnGraphics(g);	
                        }
		} 	
	}

	private Image getILImage(int IL, int index)
	{
		try
		{
			switch(IL)
			{
				case 0:
					return ContactList.menuIcons.elementAt(index);
					
				case 1:
					return MIP.SIcons.images.elementAt(index);
					
				case 2:
					return MIP.SIcons.images2.elementAt(index);
					
				case 3:
					return MIP.SIcons.images4.elementAt(index);
					
				case 4:
					return MIP.SIcons.images3.elementAt(index);
				default:
					return MIP.SIcons.images.elementAt(index);
				}
		}
		catch (Exception e) 
		{
			return null;
		}
	}
	
	// protected void drawItemData
	protected void drawItemData(Graphics g, int index, int x1, int y1, int x2, int y2, int fontHeight)
	{
		paintedItem.clear();
		get(index, paintedItem);
		int x = paintedItem.horizOffset+x1;
		Image pImg = getILImage(paintedItem.usedIL, paintedItem.imageIndex);
		try
		{
			g.drawImage(pImg, x, (y1 + y2 - pImg.getHeight()) / 2, Graphics.TOP | Graphics.LEFT);
			x += pImg.getWidth() + 1;
		}
		catch (Exception e){}
		
		if ((MIP.SIcons.images2 != null) && (paintedItem.xIndex >=0) && (paintedItem.xIndex < MIP.SIcons.images2.size()))
		{
			g.drawImage
			(
				MIP.SIcons.images2.elementAt(paintedItem.xIndex),
				x,
				(y1 + y2 - MIP.SIcons.images2.getHeight()) / 2,
				Graphics.TOP | Graphics.LEFT
			);
			x += MIP.SIcons.images2.getWidth() + 3;
		}
		if (paintedItem.PrivateImg != null)
				{
					g.drawImage(paintedItem.PrivateImg, x - 1, ((y1 + y2 - paintedItem.PrivateImg.getHeight()) / 2), Graphics.TOP
									| Graphics.LEFT);
					x += paintedItem.PrivateImg.getWidth() + 1;
				}
		
		if ((StatusIcons.images3 != null) && (paintedItem.cliIcon >=0) && (paintedItem.cliIcon < StatusIcons.images3.size()))
		{
			g.drawImage
			(
				StatusIcons.images3.elementAt(paintedItem.cliIcon),
				MIP.display.getCurrent().getWidth() - 23,
				(y1 + y2 - StatusIcons.images3.getHeight()) / 2,
				Graphics.TOP | Graphics.LEFT
			);
		}
		
		
		if (paintedItem.text != null) 
		{
			g.setFont(getQuickFont(paintedItem.fontStyle));
            g.setColor(paintedItem.color);
			g.drawString(paintedItem.text, x, (y1 + y2 - fontHeight) / 2, Graphics.TOP | Graphics.LEFT);
		}
	}

	public void lock()
	{
		dontRepaint = true;
	}

	protected void afterUnlock()
	{
	}

	public void unlock()
	{
		dontRepaint = false;
		afterUnlock();
		invalidate();
	}

	protected boolean getLocked()
	{
		return dontRepaint;
	}
	
	private static int forcedWidth = -1;
	private static int forcedHeight = -1;
	
	public void setForcedSize(int width, int height)
	{
		forcedWidth = width;
		forcedHeight = height;
	}
	
	protected static int getHeightInternal()
	{
		return (forcedHeight == -1) ? getHeight() : forcedHeight;
	}
	
	protected static int getWidthInternal()
	{
		return (forcedWidth == -1) ? getWidth() : forcedWidth;
	}
	

	public static boolean bklt_on = true;
	private static java.util.Timer switchoffTimer;

        public static void setBkltOn(boolean on) {
            if (on != bklt_on) {
                bklt_on = on;
                try{
                    if (bklt_on) {
                        MIP.setBkltOff();
                    } else {
                        MIP.setBkltOn(false);
                    }
                } catch (Exception e) {
                }
            }
        }
        public static void flashBklt(int msec) {
            try {
                setBkltOn(true);
                
                if (switchoffTimer != null) {
                    switchoffTimer.cancel();
                }
                
                (switchoffTimer = new java.util.Timer())
                            .schedule(new mip.TimerTasks(mip.TimerTasks.VL_SWITCHOFF_BKLT), msec);
            } catch (Exception e) {}
        }
	
	protected void hideNotify()
	{
	/*	if (!Options.getBoolean(Options.OPTION_LIGHT_MANUAL) & !(MIP.display.getCurrent() instanceof Canvas))
		{
			if (switchoffTimer != null) switchoffTimer.cancel();
			setBkltOn(true);
		}
	*/	
	}
	public static final int BKLT_TYPE_BLINKING = 1;
	public static final int BKLT_TYPE_LIGHTING = 2;

}