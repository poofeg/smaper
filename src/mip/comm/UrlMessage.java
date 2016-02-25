package mip.comm;

import mip.ContactListContactItem;


public class UrlMessage extends Message
{


	// URL
	private String url;


	// Message text
	private String text;

	// Constructs an incoming message
	public UrlMessage(String sndrUin, String rcvrUin, long date, String url, String text)
	{
		super(date, rcvrUin, sndrUin, MESSAGE_TYPE_AUTO);
		this.url = url;
		this.text = text;
	}

	// Constructs an outgoing message
	public UrlMessage(String sndrUin, ContactListContactItem rcvr, int _messageType, long date, String url, String text)
	{
		super(date, null, sndrUin, _messageType);
		this.rcvr = rcvr;
		this.url = url;
		this.text = text;
	}


	// Returns the URL
	public String getUrl()
	{
		return this.url;
	}


	// Returns the message text
  	public String getText()
	{
		return this.text;
	}


}
