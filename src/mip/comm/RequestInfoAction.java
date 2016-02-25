package mip.comm;


import java.util.Date;
import java.io.DataInputStream;

import mip.comm.Util;
import mip.mipException;
import mip.Options;
import mip.mipUI;
import mip.RunnableImpl;
import mip.ContactListContactItem;
import mip.ContactList;


public class RequestInfoAction extends Action
{

	// Receive timeout
	private static final int TIMEOUT = 12 * 1000; // milliseconds

	private boolean infoShown;

	/****************************************************************************/
	
	private String[] strData = new String[mipUI.UI_LAST_ID];	

	// Date of init
	private Date init;
	private int packetCounter;
	private String existingNick;


	// Constructor
	public RequestInfoAction(String uin, String nick)
	{
		super(false, true);
		existingNick = nick;
		infoShown = false;
		packetCounter = 0;
		strData[mipUI.UI_UIN] = uin;
	}
	 
	// Init action
	protected void init() throws mipException
	{

		// Send a CLI_METAREQINFO packet
		byte[] buf = new byte[6];
		Util.putWord(buf, 0, ToIcqSrvPacket.CLI_META_REQMOREINFO_TYPE, false);
		Util.putDWord(buf, 2, Long.parseLong(strData[mipUI.UI_UIN]), false);
		//Util.showBytes(buf);
		ToIcqSrvPacket packet = new ToIcqSrvPacket(0, Options.getString(Options.OPTION_UIN), ToIcqSrvPacket.CLI_META_SUBCMD, new byte[0], buf);
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
			if (fromIcqSrvPacket.getSubcommand() != FromIcqSrvPacket.SRV_META_SUBCMD) return false;
			
			// Get packet data
			DataInputStream stream = Util.getDataInputStream(fromIcqSrvPacket.getData(), 0);
			
			// Watch out for SRV_METAGENERAL packet
			try
			{
				int type = Util.getWord(stream, false);
				stream.readByte(); // Success byte
				
				switch (type)
				{
				case FromIcqSrvPacket.SRV_META_GENERAL_TYPE: //  basic user information
					{
						strData[mipUI.UI_NICK]   = Util.readAsciiz(stream);     // nickname
						// first name + last name
						String fistName = Util.readAsciiz(stream);
						String lastName = Util.readAsciiz(stream);
						strData[mipUI.UI_FIRST_NAME] = fistName; 
                        strData[mipUI.UI_LAST_NAME] = lastName; 

						if ((fistName.length() != 0) || (lastName.length() != 0)) strData[mipUI.UI_NAME] = fistName+" "+lastName;
						strData[mipUI.UI_EMAIL]  = Util.readAsciiz(stream);     // email
						strData[mipUI.UI_CITY]   = Util.readAsciiz(stream);     // home city
						strData[mipUI.UI_STATE]  = Util.readAsciiz(stream);     // home state
						strData[mipUI.UI_PHONE]  = Util.readAsciiz(stream);     // home phone
						strData[mipUI.UI_FAX]    = Util.readAsciiz(stream);     // home fax
						strData[mipUI.UI_ADDR]   = Util.readAsciiz(stream);     // home address
						strData[mipUI.UI_CPHONE] = Util.readAsciiz(stream);     // cell phone
						packetCounter++;
						consumed = true;
						break;
					}
					
				case 0x00DC: // more user information
					{
						int age = Util.getWord(stream, false);
						strData[mipUI.UI_AGE]       = (age != 0) ? Integer.toString(age) : new String();
						strData[mipUI.UI_GENDER]    = Util.genderToString( stream.readByte() );
						strData[mipUI.UI_HOME_PAGE] = Util.readAsciiz(stream);
						int year = Util.getWord(stream, false);
						int mon  = stream.readByte();
						int day  = stream.readByte();
						strData[mipUI.UI_BDAY] = (year != 0) ? day+"."+mon+"."+year : new String();
						packetCounter++;
						consumed = true;
						break;
					}
						
				case 0x00D2: // work user information
					{
						for (int i = mipUI.UI_W_CITY; i <= mipUI.UI_W_ADDR; i++) strData[i] = Util.readAsciiz(stream); // city - address
						Util.readAsciiz(stream);                             // work zip code
						Util.getWord(stream, false);                         // work country code
						strData[mipUI.UI_W_NAME] = Util.readAsciiz(stream); // work company
						strData[mipUI.UI_W_DEP]  = Util.readAsciiz(stream); // work department
						strData[mipUI.UI_W_POS]  = Util.readAsciiz(stream); // work position
						packetCounter++;
						consumed = true;
						break;
					}
						
				case 0x00E6: // user about information
					{
						strData[mipUI.UI_ABOUT] = Util.readAsciiz(stream); // notes string
						packetCounter++;
						consumed = true;
						break;
					}
						
				case 0x00F0: // user interests information
					{
						StringBuffer sb = new StringBuffer();
						int counter = stream.readByte();
						for (int i = 0; i < counter; i++)
						{
							Util.getWord(stream, false);
							String item = Util.readAsciiz(stream);
							if (item.trim().length() == 0) continue;
							if (sb.length() != 0) sb.append(", ");
							sb.append(item);
						}
						strData[mipUI.UI_INETRESTS] = sb.toString();
						packetCounter++;
						consumed = true;
						break;
					}
				}
			
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			// is completed?
			if (isCompleted())
			{
				Util.storeLastUserInfo( strData ); 
              if (!infoShown) 
				{
					if (packetCounter >= 5) infoShown = true;
					if (strData[mipUI.UI_UIN] != Options.getString(Options.OPTION_UIN)){
						RunnableImpl.callSerially(RunnableImpl.TYPE_SHOW_USER_INFO, (Object)strData);
						tryToChangeName();
					}
					else
					{
						mip.EditInfo.showEditForm(Util.getLastUserInfo(), mip.MIP.display.getCurrent() ); 
					}
					
				}
			}
			
		} // end 'if (packet instanceof FromIcqSrvPacket)'

		return (consumed);
	}
	
	// Rename contact if its name consists of digits
	private void tryToChangeName()
	{
		if (strData[mipUI.UI_UIN].equals(existingNick))
		{
			ContactListContactItem item = ContactList.getItembyUIN(strData[mipUI.UI_UIN]);
			item.rename(strData[mipUI.UI_NICK]);
		}
	}


	// Returns true if the action is completed
	public synchronized boolean isCompleted()
	{
		return (packetCounter >= 5);
	}


	// Returns true if an error has occured
	public synchronized boolean isError()
	{
		return (this.init.getTime() + RequestInfoAction.TIMEOUT < System.currentTimeMillis());
	}


}
