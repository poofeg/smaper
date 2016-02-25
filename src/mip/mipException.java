package mip;


import mip.util.ResourceBundle;
import mip.comm.Icq;

import javax.microedition.lcdui.*;

public class mipException extends Exception
			//#sijapp cond.if target="MOTOROLA"#
			implements CommandListener
			//#sijapp cond.end#
{


	// Returns the error description for the given error code
	public static String getErrDesc(int errCode, int extErrCode)
	{
	
		String errDesc = ResourceBundle.getString("error_" + errCode);
		int ext = errDesc.indexOf("EXT");
		if (ext != -1) return (errDesc.substring(0, ext) + extErrCode + errDesc.substring(ext + 3));
		return errDesc;
	}


	/****************************************************************************/


	// True, if this is a critical exception
	protected boolean critical;


	// True, if an error message should be presented to the user
	protected boolean displayMsg;
	


	private static int _ErrCode;

	public int getErrCode()
	{
		return _ErrCode;
	}
	
	// Constructs a critical mipException
	public mipException(int errCode, int extErrCode)
	{
		super(mipException.getErrDesc(errCode, extErrCode));
		this._ErrCode = errCode;
		this.critical = true;
		this.displayMsg = true;
	}


	// Constructs a non-critical mipException
	public mipException(int errCode, int extErrCode, boolean displayMsg)
	{
		super(mipException.getErrDesc(errCode, extErrCode));
		this._ErrCode = errCode;
		this.critical = false;
		this.displayMsg = displayMsg;

	}
	



	// Returns true if an error message should be presented to the user
	public boolean isDisplayMsg()
	{
		return (this.displayMsg);
	}


	// Returns true if this is a critical exception
	public boolean isCritical()
	{
		return (this.critical);
	}
	



	// Exception handler
	public synchronized static Alert handleException(mipException e)
	{
		// Critical exception
	    if (e.isCritical())
		{

			// Reset comm. subsystem
	
			Icq.resetServerCon();
			
			
			// Set offline status for all contacts and reset online counters 
			ContactList.setStatusesOffline();
			SplashCanvas.setStatusToDraw(mipUI.getStatusImageIndex(ContactList.STATUS_OFFLINE));
		
			
			// Unlock splash (if locked)
			if (SplashCanvas.locked()) SplashCanvas.unlock(true);

			// Display error message
			
			Alert errorMsg = new Alert(ResourceBundle.getString("error"), e.getMessage(), null, AlertType.ERROR);
			errorMsg.setTimeout(Alert.FOREVER);
			//#sijapp cond.if target="MOTOROLA"#
			errorMsg.setCommandListener(e);
			//#sijapp cond.end#
			ContactList.activate(errorMsg);
			return(errorMsg);
			
		}
		// Non-critical exception
		else
		{

			// Display error message, if required
			if (e.isDisplayMsg())
			{
				Alert errorMsg = new Alert(ResourceBundle.getString("warning"), e.getMessage(), null, AlertType.WARNING);
				errorMsg.setTimeout(Alert.FOREVER);
				
				SplashCanvas.unlock(false);
				ContactList.activate(errorMsg);
			
				return(errorMsg);
			}
			return(null);
		}

	}

	//#sijapp cond.if target="MOTOROLA"#
	public void commandAction(Command c, Displayable d)
		{

		ContactList.activate();
		}
	//#sijapp cond.end#


}
