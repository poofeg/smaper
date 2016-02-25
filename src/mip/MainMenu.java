package mip;

import javax.microedition.lcdui.Image;

import mip.util.ResourceBundle;

public class MainMenu
{
	static private Image getStatusImage()
	{
		int imageIndex = mipUI.getStatusImageIndex(Options.getLong(Options.OPTION_ONLINE_STATUS));
		return MIP.SIcons.images.elementAt(imageIndex);
	}
	// =)
	static public Image getXImage()
	{
				int xStatusIndex = Options.getInt(Options.OPTION_XSTATUS);
				if (xStatusIndex == -1)
				{
					return ContactList.getXList().elementAt(0);
				}
				return ContactList.getXList().elementAt(xStatusIndex);
	}
	
	static public Image getXImage2(int image)
	{
				if (image != -1)
				{
					return ContactList.getXList().elementAt(image);
				}
				return null;
	}
	

	/* Builds the main menu (visual list) */


	//#sijapp cond.if target isnot "DEFAULT" # 
            static private String getSoundValue(boolean value) 
            { 
                     return ResourceBundle.getString(value ? "#sound_on" : "#sound_off"); 
            } 
    //#sijapp cond.end# 

	
		
}
