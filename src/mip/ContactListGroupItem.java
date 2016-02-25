package mip;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mip.comm.Util;
import javax.microedition.lcdui.Font;


public class ContactListGroupItem implements ContactListItem
{
	// Persistent variables
	private int id;
	private String name;
	 private int messCount;
	 	         private int onlineCount;
	 	         private int totalCount;
	

	
	// Constructor for an existing group item
	public ContactListGroupItem(int id, String name)
	{
		this.id = id;
		this.name = new String(name);
		onlineCount = totalCount = 0;
	}
	
	public ContactListGroupItem()
	{
		
	}
	
	public String getSortText()
	 	         {
	 	                 return name;
	 	         }
	 	 
	 	         public int getSortWeight()
	 	         {
	 	                 return 0;
	 	         }
	public int getMessCount()
	 	         {
	 	                 return messCount;
	 	         }
	 	 
	 	         public void changeMessCount(int inc)
	 	         {
	 	                 messCount += inc;
	 	                  if (messCount < 0) messCount = 0;
	 	         }
	 public void setMessCount(int value)
	 	         {
	 	                 messCount = value;
}
	// Constructor for a new group item
	public ContactListGroupItem(String name)
	{
        this.id = Util.createRandomId();
		this.name = new String(name);
		onlineCount = totalCount = 0;
	}

	public void setCounters(int online, int total)
	{
	    onlineCount = online;
	    totalCount  = total;
	}
	
	public void updateCounters(int onlineInc, int totalInc)
	{
	    onlineCount += onlineInc;
	    totalCount  += totalInc;
	}
	
	public String getText()
    {
        String result;
        
        if ((onlineCount != 0) && 
        	 !Options.getBoolean(Options.OPTION_CL_HIDE_OFFLINE)) 
            result = name+" ("+Integer.toString(onlineCount)+"/"
                         +Integer.toString(totalCount)+")";
        else result = name;
        return result;
    }

    public int getImageIndex()
    {
        return (messCount == 0) ? -1 :8;
    }
	
	public int getXIndex()
	{
		return -1;
	}
	public int getCliIcon()
	{
		return -1;
	}
    
    public int getTextColor()
    {
    	return Options.getInt(Options.OPTION_COLOR4);
    }

	// Returns the group item id
	public int getId()
	{
		return (this.id);
	}


	// Sets the group item id
	public void setId(int id)
	{
		this.id = id;
	}


	// Returns the group item name
	public String getName()
	{
		return (new String(this.name));
	}


	// Sets the group item name
	public void setName(String name)
	{
		this.name = new String(name);
	}


	// Checks whether some other object is equal to this one
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ContactListGroupItem)) return (false);
		ContactListGroupItem gi = (ContactListGroupItem) obj;
		return (this.id == gi.getId());
	}
	
	public int getFontStyle()
	{
		return Font.STYLE_PLAIN;
	}
	
	public void saveToStream(DataOutputStream stream) throws IOException
	{
		stream.writeByte(1);
		stream.writeInt(id);
		stream.writeUTF(name);
	}
	
	public void loadFromStream(DataInputStream stream) throws IOException
	{
		id = stream.readInt();
		name = stream.readUTF();
	}


}
