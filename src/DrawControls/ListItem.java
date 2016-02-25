package DrawControls;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

//! Data for list item
/*! All members of class are made as public 
    in order to easy access. 
 */
public class ListItem
{
	public String text; //!< Text of node

	Image image; // Used for TextList in SEL_NONE mode

	private int itemWidth, itemHeigth;
	public Image PrivateImg;
	public int horizOffset;


	public int fontStyle, //!< Font style
			color,        //!< Color of node text
			imageIndex,   //!< Index of node image. Must be -1 for disabling image
			xIndex,
			cliIcon, 
			usedIL = 0;
			
	public boolean isIgnore = false, isVisible = false, isInvisible = false;

	ListItem()
	{
		color = imageIndex = 0;
		fontStyle = Font.STYLE_PLAIN;
	}

	
	//! Set all member to default values
	public void clear()
	{
		text = null;
		image = null;
		color = 0;
		imageIndex = -1;
		xIndex = -1;
		cliIcon = -1;
		isVisible = false;
		isInvisible = false;
		isIgnore = false;
		horizOffset = 0;
		fontStyle = Font.STYLE_PLAIN;
	}

	//! Copy data of class to another object
	
	
}