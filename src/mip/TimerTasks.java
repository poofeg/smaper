package mip;

import mip.util.ResourceBundle;
import java.util.TimerTask;
import java.util.Timer;
import mip.comm.Action;
import mip.comm.Icq;

public class TimerTasks extends TimerTask implements javax.microedition.lcdui.CommandListener
{
	public static final int SC_AUTO_REPAINT = 1;

	public static final int SC_HIDE_KEYLOCK = 2;
	public static final int SC_RESET_TEXT_AND_IMG = 3;

	
	public static final int VL_SWITCHOFF_BKLT = 10;
	//#sijapp cond.if target="MOTOROLA"#
	public static final int VL_SWITCHOFF_LED    = 11;
	public static final int VL_LED_CHANGE_STATE = 12;
	//#sijapp cond.end#
	public static final int ICQ_AUTOSTATUS = 13;
	private static int oldStatus = (int)Options.getLong(Options.OPTION_ONLINE_STATUS);
	public static int delay = Options.getInt(Options.OPTION_AUTOSTATUS_DELAY) * 60000;        
        private static java.util.Timer timer;
        
	public static final int ICQ_KEEPALIVE = 100;

	private int type = -1;

	private Action action;

	boolean wasError = false;

	public TimerTasks(Action action)
	{
		this.action = action;
	}

	public TimerTasks(int type)
	{
		this.type = type;
	}



	public void run()
	{
		//SplashCanvas.setMessage("");
		if (wasError) return;
		if (type != -1)
		{
			switch (type)
			{
			case SC_AUTO_REPAINT:
				SplashCanvas.Repaint();
				break;
			case SC_HIDE_KEYLOCK:
				SplashCanvas.showKeylock = false;
				SplashCanvas.Repaint();
				break;
			case SC_RESET_TEXT_AND_IMG:
				SplashCanvas.setStatusToDraw(mipUI.getStatusImageIndex(Icq.getCurrentStatus()));
				SplashCanvas.setMessage(ResourceBundle.getString("keylock_enabled"));
				SplashCanvas.Repaint();
				break;
			/*
			case VL_SWITCHOFF_BKLT:
				DrawControls.VirtualList.setBkltOn(false);
				break;*/
			//#sijapp cond.if target="MOTOROLA"#
			case VL_SWITCHOFF_LED:
				//DrawControls.VirtualList.disableLED();
				break;
			case VL_LED_CHANGE_STATE:
				/*if (((cmode % 2) == 0) & (tries != 1))
				{
					regions[0].getControl();
					if (regions[1] != null) regions[1].getControl();
				}
				else
				{
					regions[0].releaseControl();
					if (regions[1] != null) regions[1].releaseControl();
				}
				tries--;
				cmode = (cmode++ % 2);
				if (tries < 1)
				{
					DrawControls.VirtualList.disableLED();
					cancel();
				}*/
				break;
			//#sijapp cond.end#
				
			case ICQ_KEEPALIVE:
				if (Icq.isConnected() && Options.getBoolean(Options.OPTION_KEEP_CONN_ALIVE))
				{
					// Instantiate and send an alive packet
					try
					{
                    Icq.c.sendPacket(new mip.comm.Packet(5, new byte[0]));
					}
					catch (mipException e)
					{
						mipException.handleException(e);
						if (e.isCritical()) cancel();
					}
				}
				break;
			case ICQ_AUTOSTATUS:
                            if(Icq.isConnected() && ((Icq.getCurrentStatus() != ContactList.STATUS_NA)))
                            {
                                if((Icq.getCurrentStatus() != ContactList.STATUS_AWAY) && 
                                        (Options.getBoolean(Options.OPTION_AUTOAWAY_ENABLE))) 
                                {
                                    oldStatus = (int)Options.getLong(Options.OPTION_ONLINE_STATUS);
                                    statusChange(ContactList.STATUS_AWAY);
                                    StatusIcons.haveToRestoreStatus = true;
                                }
                                else if(Options.getBoolean(Options.OPTION_AUTONA_ENABLE)){
                                    if((StatusIcons.haveToRestoreStatus = true) && !(Options.getBoolean(Options.OPTION_AUTOAWAY_ENABLE)))
                                        oldStatus = (int)Options.getLong(Options.OPTION_ONLINE_STATUS);
                                    statusChange(ContactList.STATUS_NA);
                                    StatusIcons.haveToRestoreStatus = true;                                    
                                }
                            } 
                            else 
                            {
                                timer.cancel();
                            }
                            break;
			}
			return;
		}

		SplashCanvas.setProgress(action.getProgress());
		try
		{
			if (action.isCompleted())
			{
				cancel();
				action.onEvent(Action.ON_COMPLETE);
			}
			else if (action.isError())
			{
				wasError = true;
				cancel();
				action.onEvent(Action.ON_ERROR);
			}
		}
		catch (Exception e){}
		}

	public static void setStatusTimer()
	{
		if (timer != null)
		{
			timer.cancel();
			timer = null;
		}    
		timer = new Timer();
		timer.schedule(new TimerTasks(ICQ_AUTOSTATUS), delay, delay);
		if (StatusIcons.haveToRestoreStatus && Options.getBoolean(Options.OPTION_AUTOONLINE))
		{
			int status = (int)Options.getLong(Options.OPTION_ONLINE_STATUS);
			if (status == ContactList.STATUS_AWAY || status == ContactList.STATUS_NA)
				statusChange(oldStatus);
		}
	}
        
	private static void statusChange(int status)
	{
		try
		{
                    if (Icq.isConnected())
                    {
                        Options.setLong(Options.OPTION_ONLINE_STATUS, status);
                        Options.safe_save();     
			ContactList.repaintTree();
                        Icq.setCliStatus(false, false);
                    }
		}
		catch (mipException e)
		{
			mipException.handleException(e);   
		}      
	}

	public void commandAction(javax.microedition.lcdui.Command c, javax.microedition.lcdui.Displayable d)
	{
		try
		{
		if (c == SplashCanvas.cancelCommnad)
		{
			action.onEvent(Action.ON_CANCEL);
			cancel();
		}
		}
		catch (Exception e){}
	}
}
