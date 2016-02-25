package mip.comm;


import mip.ContactListContactItem;


public class PlainMessage extends Message
{
	// Message text
	private String text;


	// Constructs an incoming message
	public PlainMessage(String sndrUin, String rcvrUin, long date, String text, boolean offline)
	{
		super(date, rcvrUin, sndrUin, MESSAGE_TYPE_AUTO);
		this.text = text;
		this.offline = offline;
	}

	// Constructs an outgoing message
	public PlainMessage(String sndrUin, ContactListContactItem rcvr, int _messageType, long date, String text)
	{
		super(date, null, sndrUin, _messageType);
		this.rcvr = rcvr;
		this.text = text;
	}


	// Returns the message text
	public String getText()
	{
		return this.text;
	}


}
