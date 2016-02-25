package mip.comm;

import mip.Options;

public class SystemNotice extends Message {

	// Types of system messages
	public static final int SYS_NOTICE_YOUWEREADDED = 1;
	public static final int SYS_NOTICE_AUTHREPLY = 2;
	public static final int SYS_NOTICE_AUTHREQ = 3;
	public static final int SYS_NOTICE_AUTHORISE = 4;
	public static final int SYS_NOTICE_REQUAUTH = 5;

	/****************************************************************************/

	// Type of the note
	private int sysnotetype;

	// Was the Authorisation granted
	private boolean AUTH_granted;

	// What was the reason
	private String reason;

	// Constructs system notice
	public SystemNotice(int _sysnotetype, String _uin, boolean _AUTH_granted, String _reason) 
	{
		super(Util.createCurrentDate(false), Options.getString(Options.OPTION_UIN), _uin, MESSAGE_TYPE_AUTO);
		sysnotetype = _sysnotetype;
		AUTH_granted = _AUTH_granted;
		reason = _reason;
	}

	// Get AUTH_granted
	public boolean isAUTH_granted() {
		return AUTH_granted;
	}

	// Get Reason
	public String getReason() {
		return reason;
	}

	// Get Sysnotetype
	public int getSysnotetype() {
		return sysnotetype;
	}

}
