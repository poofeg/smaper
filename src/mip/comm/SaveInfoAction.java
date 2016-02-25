package mip.comm;

import java.util.Calendar;
import java.util.Date;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import mip.comm.Util;
import mip.MIP;
import mip.mipException;
import mip.Options;
import mip.mipUI;
import mip.RunnableImpl;
import mip.ContactListContactItem;
import mip.ContactList;


public class SaveInfoAction extends Action
{

	// Receive timeout
	private static final int TIMEOUT = 5 * 1000; // milliseconds
	
	//TLVs
	private static final int NICK_TLV_ID = 0x0154;
	public  static final int FIRSTNAME_TLV_ID = 0x0140; 
	private static final int LASTNAME_TLV_ID = 0x014A;
	private static final int EMAIL_TLV_ID = 0x015E;
	private static final int BDAY_TLV_ID = 0x023A;
	private static final int CITY_TLV_ID = 0x0190;
	private static final int GENDER_TLV_ID = 0x017C;
	private static final int ABOUT_TLV_ID = 0x0258;
	
	

	/****************************************************************************/
	
	private String[] strData = new String[mipUI.UI_LAST_ID];	

	// Date of init
	private Date init;
	private int packetCounter;
	private int errorCounter;

	// Constructor
	public SaveInfoAction(String[] userInfo)
	{
		super(false, true);
		strData = userInfo;
	}

	// Init action
	protected synchronized void init() throws mipException
	{
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Util.writeWord(stream ,ToIcqSrvPacket.CLI_SET_FULLINFO, false);

		Util.writeAsciizTLV(NICK_TLV_ID, stream, strData[mipUI.UI_NICK], false);
		
		Util.writeAsciizTLV(FIRSTNAME_TLV_ID, stream, strData[mipUI.UI_FIRST_NAME], false);
		Util.writeAsciizTLV(LASTNAME_TLV_ID, stream, strData[mipUI.UI_LAST_NAME], false);
		Util.writeAsciizTLV(CITY_TLV_ID, stream, strData[mipUI.UI_CITY], false);

		 /* Email */
		String email = strData[mipUI.UI_EMAIL];
		if ((email != null) && (email.length() != 0))
		Util.writeAsciizTLV(EMAIL_TLV_ID, stream, strData[mipUI.UI_EMAIL], false);
 
	                 /* Birsday */
		String birthday = strData[mipUI.UI_BDAY];
	 	if (birthday != null)
            {
                String[] bDate =  Util.explode(birthday, '.');
                if (bDate.length == 3)
	 	            {
	 	                         Util.writeWord(stream, BDAY_TLV_ID, false);
	 	                         Util.writeWord(stream, 6, false);
	 	                         Util.writeWord(stream, Integer.parseInt(bDate[2]), false);
	 	                         Util.writeWord(stream, Integer.parseInt(bDate[1]), false);
	 	                         Util.writeWord(stream, Integer.parseInt(bDate[0]), false);
	 	            }
	 	    }
	 	 
	 	                 /* Gender */
	 	                 Util.writeWord(stream, GENDER_TLV_ID, false);
	 	                 Util.writeWord(stream, 1, false);
	 	                 Util.writeByte(stream, Util.stringToGender(strData[mipUI.UI_GENDER]));
		
		ByteArrayOutputStream sexStream = new ByteArrayOutputStream();
		Util.writeByte( sexStream, Util.stringToGender(strData[mipUI.UI_GENDER]) );
		Util.writeTLV( GENDER_TLV_ID, stream, sexStream, false );
		
		Util.writeAsciizTLV(ABOUT_TLV_ID, stream, strData[mipUI.UI_ABOUT], false);
		
		
		ToIcqSrvPacket packet = new ToIcqSrvPacket(0,Options.getString(Options.OPTION_UIN), ToIcqSrvPacket.CLI_META_SUBCMD, new byte[0], stream.toByteArray());
		Icq.c.sendPacket(packet);
		
		// Save date
		this.init = new Date();
	}
	

	// Forwards received packet, returns true if packet was consumed
	protected synchronized boolean forward(Packet packet) throws mipException
	{
		boolean consumed = false; 

		// Watch out for SRV_FROMICQSRV packet
		if (packet instanceof FromIcqSrvPacket)
		{
			FromIcqSrvPacket fromIcqSrvPacket = (FromIcqSrvPacket) packet;

			// Watch out for SRV_META packet
			if (fromIcqSrvPacket.getSubcommand() != FromIcqSrvPacket.SRV_META_SUBCMD)
				return false;
			
			// Get packet data
			DataInputStream stream = Util.getDataInputStream(fromIcqSrvPacket.getData(), 0);
			
			try
			{
				int type = Util.getWord(stream, false);
				switch (type)
				{
					case FromIcqSrvPacket.META_SET_FULLINFO_ACK: //  full user information
					{
						if(stream.readByte() != 0x0A)
						{
							errorCounter++;
							break;
						}
						
						consumed = true;
						packetCounter++;
						break;
					}
				}
			}
			catch(Exception e)
			{
			}
		}

		return (consumed);
	}
	
	// Returns true if the action is completed
	public synchronized boolean isCompleted()
	{
		return (packetCounter >= 1);
	}

	// Returns true if an error has occured
	public synchronized boolean isError()
	{
		return (this.init.getTime() + SaveInfoAction.TIMEOUT < System.currentTimeMillis())
				||
				errorCounter > 0;
	}
	
	public int getProgress()
	{
		return packetCounter > 0 ? 100 : 0;
	}
	
	 public void onEvent(int eventTuype) throws mipException
	{
	    	switch (eventTuype)
	    	{
		    	case ON_COMPLETE:
		    		ContactList.activate();
		    		break;
	    	}
	}
}