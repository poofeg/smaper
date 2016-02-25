package mip.comm;

import mip.ContactListContactItem;

public abstract class Message
{

    // Static variables for message type;
    public static final int MESSAGE_TYPE_AUTO     = 0x0000;
    public static final int MESSAGE_TYPE_NORM     = 0x0001;
    public static final int MESSAGE_TYPE_EXTENDED = 0x001a;
    public static final int MESSAGE_TYPE_AWAY     = 0x03e8;
    public static final int MESSAGE_TYPE_OCC      = 0x03e9;
    public static final int MESSAGE_TYPE_NA       = 0x03ea;
    public static final int MESSAGE_TYPE_DND      = 0x03eb;
    public static final int MESSAGE_TYPE_FFC      = 0x03ec;


    // Message type
    private int messageType;
    
    protected boolean offline;
    
    // Senders UIN (set for both incoming and outgoing messages)
    private String sndrUin;

    // Receivers UIN (set only for incoming messages)
    private String rcvrUin;

    // Receiver object (set only for outgoing messages)
    protected ContactListContactItem rcvr;

    // Date of dispatch
    private long newDate;
    
    protected Message(long date, String rcvrUin, String sndrUin, int messageType)
    {
    	newDate          = date;
    	this.rcvrUin     = rcvrUin;
    	this.sndrUin     = sndrUin;
    	this.messageType = messageType;
    }

    // Returns the senders UIN
    public String getSndrUin()
    {
        return (new String(this.sndrUin));
    }

    // Returns the receivers UIN
    public String getRcvrUin()
    {
        if (this.rcvrUin != null)
        {
            return (new String(this.rcvrUin));
        }
        else
        {
            return (this.rcvr.getStringValue(ContactListContactItem.CONTACTITEM_UIN));
        }
    }
    
    // Returns the message type
    public int getMessageType()
    {
        return(this.messageType);
    }

    // Returns the receiver
    public ContactListContactItem getRcvr()
    {
        return (this.rcvr);
    }

    public boolean getOffline()
    {
    	return offline;
    }
    
    public long getNewDate()
    {
    	return newDate;
    }

}
