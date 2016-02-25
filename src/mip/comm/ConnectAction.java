package mip.comm;

import java.util.*;

import mip.ContactListContactItem;
import mip.ContactListGroupItem;
import mip.ContactListItem;
import mip.ContactList;
import mip.MIP;
import mip.mipException;
import mip.Options;
import mip.RunnableImpl;
import mip.comm.Icq.HTTPConnection;
import mip.SplashCanvas;
import mip.TimerTasks;

public class ConnectAction extends Action
{
    // Action states
    public static final int STATE_ERROR = -1;
    public static final int STATE_INIT_DONE = 0;
	public static final int STATE_AUTHKEY_REQUESTED = 1;
    public static final int STATE_CLI_IDENT_SENT = 2;
    public static final int STATE_CLI_DISCONNECT_SENT = 3;
    public static final int STATE_CLI_COOKIE_SENT = 4;
    public static final int STATE_CLI_CHECKROSTER_SENT = 5;
    public static final int STATE_CLI_REQOFFLINEMSGS_SENT = 6;
    public static final int STATE_CLI_ACKOFFLINEMSGS_SENT = 7;
		// Privacy Lists
	private Hashtable ignoreList    = new Hashtable();
	private Hashtable invisibleList = new Hashtable();
	private Hashtable visibleList   = new Hashtable();

	
	public static final String[] servers =
	{"bucp1-vip-m.blue.aol.com", "bucp2-vip-m.blue.aol.com", "ibucp-vip-m.blue.aol.com"};	
			
    // CLI_SETICBM packet data
    public static final byte[] CLI_SETICBM_DATA = Util.explodeToBytes("0,0,0,0,0,0B,1F,40,3,E7,3,E7,0,0,0,0", ',', 16);
	

    // CLI_SETSTATUS packet data
    public static final byte[] CLI_SETSTATUS_DATA =
	{(byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x04, 
	  (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00, // Online status
      (byte) 0x00, (byte) 0x0C, (byte) 0x00, (byte) 0x25, // TLVs
      (byte) 0xC0, (byte) 0xA8, (byte) 0x00, (byte) 0x02, // 192.168.0.2, cannot get own IP address
      (byte) 0x00, (byte) 0x00, (byte) 0xAB, (byte) 0xBD, // Port 0
      (byte) 0x00, // Firewall
      (byte) 0x7A, (byte) 0x69, // Support protocol version 31337
      (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, 
      (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x50, 
      (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // 3                                                                                                                                                                 // follow
      (byte) 0x08, (byte) 0x00, (byte) 0x04, (byte) 0x00, // Timestamp 1
      (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x4C, 
	  (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0F,
	  (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x1F,
	  (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00};
	

    // CLI_READY packet data
    public static final byte[] CLI_READY_DATA =
    {
    		(byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04,
			(byte) 0x01, (byte) 0x10, (byte) 0x08, (byte) 0xE4,
			(byte) 0x00, (byte) 0x13, (byte) 0x00, (byte) 0x04,
			(byte) 0x01, (byte) 0x10, (byte) 0x08, (byte) 0xE4,
			(byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x01,
			(byte) 0x01, (byte) 0x10, (byte) 0x08, (byte) 0xE4,
			(byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x01,
			(byte) 0x01, (byte) 0x10, (byte) 0x08, (byte) 0xE4,
			(byte) 0x00, (byte) 0x15, (byte) 0x00, (byte) 0x01,
			(byte) 0x01, (byte) 0x10, (byte) 0x08, (byte) 0xE4,
			(byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01,
			(byte) 0x01, (byte) 0x10, (byte) 0x08, (byte) 0xE4,
			(byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x01,
			(byte) 0x01, (byte) 0x10, (byte) 0x08, (byte) 0xE4,
			(byte) 0x00, (byte) 0x09, (byte) 0x00, (byte) 0x01,
			(byte) 0x01, (byte) 0x10, (byte) 0x08, (byte) 0xE4,
			(byte) 0x00, (byte) 0x0A, (byte) 0x00, (byte) 0x01,
			(byte) 0x01, (byte) 0x10, (byte) 0x08, (byte) 0xE4,
			(byte) 0x00, (byte) 0x0B, (byte) 0x00, (byte) 0x01,
			(byte) 0x01, (byte) 0x10, (byte) 0x08, (byte) 0xE4
    };
    // Timeout
    //#sijapp cond.if modules_PROXY is "true" #
    public static final int TIME_OUT = 20 * 1000;
    //#sijapp cond.end #
    public int TIMEOUT = 20 * 1000; // milliseconds

    /** *********************************************************************** */

    // UIN
    private String uin;
	
    // Password
    private String password;
	
	private int iServer;

    // Server host
    private String srvHost;

    // Server port
    private String srvPort;

    // Action state
    private int state;

    // Last activity
    private Date lastActivity = new Date();
    private boolean active;

    // Temporary variables
	private String server;
    private byte[] cookie;
    private boolean srvReplyRosterRcvd;

    // Constructor
    public ConnectAction(String uin, String password, String srvHost, String srvPort, int nServer)
    {
    	super(true, false);
        this.uin = new String(uin);
        this.password = new String(password);
        this.srvHost = new String(srvHost);
        this.srvPort = new String(srvPort);
		iServer = nServer;
    }

    // Returns the UID
    public String getUin()
    {
        return (new String(this.uin));
    }

    // Returns the password
    public String getPassword()
    {
        return (new String(this.password));
    }

    // Returns the server host
    public String getSrvHost()
    {
        return (new String(this.srvHost));
    }

    // Returns the server port
    public String getSrvPort()
    {
        return (new String(this.srvPort));
    }

    // Init action
    protected void init() throws mipException
    {
        //#sijapp cond.if modules_PROXY is "true" #
        int retry = 1;
        try
        {
            retry = Integer.parseInt(Options.getString(Options.OPTION_AUTORETRY_COUNT));
            retry = (retry > 0) ? retry : 1;
        } catch (NumberFormatException e)
        {
            retry = 1;
        }

        this.TIMEOUT = ConnectAction.TIME_OUT * retry;
        //#sijapp cond.end#
 
        // Check parameters
        if ((this.uin.length() == 0) || (this.password.length() == 0))
        {
            this.state = ConnectAction.STATE_ERROR;
			throw (new mipException(117, 0));
			
        }

        // Open connection
        //#sijapp cond.if modules_PROXY is "true" #

        for (int i = 0; i < retry; i++)
        {
            //#sijapp cond.end#

            try
            {
            	if (!Options.getBoolean(Options.OPTION_MULTIPLESERVERS))
				{
					Icq.c.connect(this.srvHost + ":" + this.srvPort);
                }
				else
				{	
					Icq.c.connect(servers[iServer] + ":" + this.srvPort);
				}
				//#sijapp cond.if modules_PROXY is "true" #
                break;
                //#sijapp cond.end #
            } catch (mipException e)
            {
                //#sijapp cond.if modules_PROXY is "true" #
                if (i >= (retry - 1) || ((this.lastActivity.getTime() + this.TIMEOUT) < System.currentTimeMillis()))
                {
                    //#sijapp cond.end #

                    this.state = ConnectAction.STATE_ERROR;
                    throw (e);
                }
                //#sijapp cond.if modules_PROXY is "true" #
                else
                    if ((this.lastActivity.getTime() + this.TIMEOUT) > System.currentTimeMillis())
                    {
                    	Icq.c.close();
                        try
                        {
                            // Wait the given time
                            Thread.sleep(2000);
                        } catch (InterruptedException er)
                        {
                            // Do nothing
                        }
                    }
            }
        }
        //#sijapp cond.end #

        // Set STATE_INIT
        this.state = ConnectAction.STATE_INIT_DONE;

        // Update activity timestamp
        this.lastActivity = new Date();

    }

    // Forwards received packet, returns true if packet has been consumed
    protected boolean forward(Packet packet) throws mipException
    {
        //#sijapp cond.if modules_PROXY is "true" #

        int retry = 1;
        try
        {
            retry = Integer.parseInt(Options.getString(Options.OPTION_AUTORETRY_COUNT));
            retry = (retry > 0) ? retry : 1;
        } catch (NumberFormatException e)
        {
            retry = 1;
        }
        //#sijapp cond.end #

        // Set activity flag
        this.active = true;

        // Catch mipExceptions
        try
        {

            // Flag indicates whether packet has been consumed or not
            boolean consumed = false;

            // Watch out for STATE_INIT_DONE
            if (this.state == ConnectAction.STATE_INIT_DONE)
            {
                // Watch out for SRV_CLI_HELLO packet
                if (packet instanceof ConnectPacket)
                {
                    ConnectPacket connectPacket = (ConnectPacket) packet;
                    if (connectPacket.getType() == ConnectPacket.SRV_CLI_HELLO)
                    {
						if (Options.getBoolean(Options.OPTION_MD5_LOGIN)) {
							Icq.c.sendPacket(new ConnectPacket());
							byte[] buf = new byte[4 + this.uin.length()];
							Util.putWord(buf, 0, 0x0001);
							Util.putWord(buf, 2, this.uin.length());
							byte[] uinRaw = Util.stringToByteArray(this.uin);
							System.arraycopy(uinRaw, 0, buf, 4, uinRaw.length);
							Icq.c.sendPacket(new SnacPacket(0x0017, 0x0006, 0, new byte[0], buf));
						} else {
							// Send a CLI_IDENT packet as reply
							ConnectPacket reply = new ConnectPacket(this.uin, this.password);
							Icq.c.sendPacket(reply);
						}

                        // Move to next state
                        this.state = !Options.getBoolean(Options.OPTION_MD5_LOGIN) ? ConnectAction.STATE_CLI_IDENT_SENT :
							STATE_AUTHKEY_REQUESTED;

                        // Packet has been consumed
                        consumed = true;

                    }
                }

            }
			else if (state == STATE_AUTHKEY_REQUESTED) {
				if (packet instanceof SnacPacket) {
					SnacPacket snacPacket = (SnacPacket)packet;
					if ((snacPacket.getFamily() == 0x0017) && (snacPacket.getCommand() == 0x0007)) {
						byte[] rbuf = snacPacket.getData();
						int len = Util.getWord(rbuf, 0);
						byte[] authkey = new byte[len];
						System.arraycopy(rbuf, 2, authkey, 0, len);
						rbuf = null;
						byte[] buf = new byte[2 + 2 + this.uin.length() + 2 + 2 + 16];
						int marker = 0;
						Util.putWord(buf, marker, 0x0001);
						marker += 2;
						Util.putWord(buf, marker, this.uin.length());
						marker += 2;
						byte[] uinRaw = Util.stringToByteArray(this.uin);
						System.arraycopy(uinRaw, 0, buf, marker, uinRaw.length);
						marker += uinRaw.length;
						Util.putWord(buf, marker, 0x0025);
						marker += 2;
						Util.putWord(buf, marker, 0x0010);
						marker += 2;
						byte[] md5buf = new byte[authkey.length + this.password.length() + Util.AIM_MD5_STRING.length];
						int md5marker = 0;
						System.arraycopy(authkey, 0, md5buf, md5marker, authkey.length);
						md5marker += authkey.length;
						byte[] passwordRaw = Util.stringToByteArray(this.password);
						System.arraycopy(passwordRaw, 0, md5buf, md5marker, passwordRaw.length);
						md5marker += passwordRaw.length;
						System.arraycopy(Util.AIM_MD5_STRING, 0, md5buf, md5marker, Util.AIM_MD5_STRING.length);
						byte[] hash = Util.calculateMD5(md5buf);
						System.arraycopy(hash, 0, buf, marker, 16);
						Icq.c.sendPacket(new SnacPacket(0x0017, 0x0002, 0, new byte[0], buf));
						state = STATE_CLI_IDENT_SENT;
					} else if ((snacPacket.getFamily() == 0x0017) && (snacPacket.getCommand() == 0x0003)) {
								int errcode = -1;
								
							byte[] buf = snacPacket.getData();
							int marker = 0;
							while (marker < buf.length) {
								byte[] tlvData = Util.getTlv(buf, marker);
								int tlvType = Util.getWord(buf, marker);
								marker += 4 + tlvData.length;
								switch (tlvType) {
									case 0x0008:
										errcode = Util.getWord(tlvData, 0);
										break;
									case 0x0004:
										//System.out.println(Util.byteArrayToString(tlvData));
										break;
									
								}
								tlvData=null;
							}
							int toThrow = 100;
								switch (errcode) {
								
						// Multiple logins
						case 0x0001:
							toThrow = 110;
							break;
						// Bad password
						case 0x0004: case 0x0005:
							toThrow = 111;
							break;
						// Non-existant UIN
						case 0x0007: case 0x0008:
							toThrow = 112;
							break;
						// Too many clients from same IP
						case 0x0015: case 0x0016: case 0x0017:
							toThrow = 113;
							break;
						// Rate exceeded
						case 0x0018: case 0x001d:
							toThrow = 114;
							break;
						case 0x0002:
							toThrow = 500;
							break;
						case 0x0003:
							toThrow = 501;
							break;
						case 0x0006:
							toThrow = 502;
							break;
						case 0x000a: case 0x000b:
							toThrow = 503;
							break;
						case 0x000f:
							toThrow = 504;
							break;
						case 0x000d: case 0x000e:
							toThrow = 505;
							break;
						case 0x0010:
							toThrow = 506;
							break;
						case 0x001e:
							toThrow = 507;
							break;
					}
					throw new mipException(toThrow, errcode);
				}
				consumed = true;
							
				}
			}
            // Watch out for STATE_CLI_IDENT_SENT
            else if (this.state == ConnectAction.STATE_CLI_IDENT_SENT)
			{
				int errcode = -1;
				if (Options.getBoolean(Options.OPTION_MD5_LOGIN)) {
					if (packet instanceof SnacPacket) {
						SnacPacket snacPacket = (SnacPacket)packet;
						if ((snacPacket.getFamily() == 0x0017) && (snacPacket.getCommand() == 0x0003)) {
							byte[] buf = snacPacket.getData();
							int marker = 0;
							while (marker < buf.length) {
								byte[] tlvData = Util.getTlv(buf, marker);
								int tlvType = Util.getWord(buf, marker);
								marker += 4 + tlvData.length;
								switch (tlvType) {
									case 0x0008:
										errcode = Util.getWord(tlvData, 0);
										break;
									case 0x0005:
										this.server = Util.byteArrayToString(tlvData);
										break;
									case 0x0006:
										this.cookie = tlvData;
										break;
								}
							}
						}
					} else if (packet instanceof DisconnectPacket) {
						consumed = true;
					}
				} else {
					// watch out for channel 4 packet
					if (packet instanceof DisconnectPacket) {
						DisconnectPacket disconnectPacket = (DisconnectPacket) packet;
						// Watch out for SRV_COOKIE packet
						if (disconnectPacket.getType() == DisconnectPacket.TYPE_SRV_COOKIE) {
							// Save cookie
							this.cookie = disconnectPacket.getCookie();
							this.server = disconnectPacket.getServer();
						}
						// Watch out for SRV_GOODBYE packet
						else if (disconnectPacket.getType() == DisconnectPacket.TYPE_SRV_GOODBYE)
							errcode = disconnectPacket.getError();
						consumed = true;
					}
				}

				if (errcode != -1) {
					int toThrow = 100;
					switch (errcode) {
						// Multiple logins
						case 0x0001:
							toThrow = 110;
							break;
						// Bad password
						case 0x0004: case 0x0005:
							toThrow = 111;
							break;
						// Non-existant UIN
						case 0x0007: case 0x0008:
							toThrow = 112;
							break;
						// Too many clients from same IP
						case 0x0015: case 0x0016: case 0x0017:
							toThrow = 113;
							break;
						// Rate exceeded
						case 0x0018: case 0x001d:
							toThrow = 114;
							break;
						case 0x0002:
							toThrow = 500;
							break;
						case 0x0003:
							toThrow = 501;
							break;
						case 0x0006:
							toThrow = 502;
							break;
						case 0x000a: case 0x000b:
							toThrow = 503;
							break;
						case 0x000f:
							toThrow = 504;
							break;
						case 0x000d: case 0x000e:
							toThrow = 505;
							break;
						case 0x0010:
							toThrow = 506;
							break;
						case 0x001e:
							toThrow = 507;
							break;
					}
					throw new mipException(toThrow, errcode);
				}

				if (consumed & (this.server != null) & (this.cookie != null)) {
					// Close connection (only if not HTTP Connection)
					if (!(Icq.c instanceof HTTPConnection))
						Icq.c.close();
					//#sijapp cond.if target is "DEFAULT" | target is "MIDP2"#
					if (Options.getBoolean(Options.OPTION_SHADOW_CON)) try
					{
						// Wait the given time before starting the
						// new connection
						Thread.sleep(2000);
					} catch (InterruptedException e) {}
					//#sijapp cond.end#
					// Open connection
					//#sijapp cond.if modules_PROXY is "true" #
					for (int i = 0; i < retry; i++)
					{
						try
						{
							//#sijapp cond.end #
							Icq.c.connect(server);
							//#sijapp cond.if modules_PROXY is "true" #
							break;

						} catch (mipException e)
						{
							if (i >= (retry - 1) || ((this.lastActivity.getTime() + this.TIMEOUT) < System.currentTimeMillis()))
							{
								this.active = false;
								this.state = ConnectAction.STATE_ERROR;
								throw (e);
							}
							else if ((this.lastActivity.getTime() + this.TIMEOUT) > System.currentTimeMillis())
							{
								Icq.c.close();
								try
								{
									// Wait the given time
									Thread.sleep(2000);
								} catch (InterruptedException er)
								{
									// Do nothing
								}
							}
						}
					}
					//#sijapp cond.end #
					// Move to next state
					this.state = ConnectAction.STATE_CLI_DISCONNECT_SENT;
				}

			}
			// Watch out for STATE_CLI_DISCONNECT_SENT
			else if (this.state == ConnectAction.STATE_CLI_DISCONNECT_SENT)
			{

				// Watch out for SRV_HELLO packet
				if (packet instanceof ConnectPacket)
				{
					ConnectPacket connectPacket = (ConnectPacket) packet;
					if (connectPacket.getType() == ConnectPacket.SRV_CLI_HELLO)
					{

						// Send a CLI_COOKIE packet as reply
						ConnectPacket reply = new ConnectPacket(this.cookie);
						Icq.c.sendPacket(reply);
						
						byte[] tmp_packet;
						
						Icq.setCLI_USERINFO(Options.getInt(Options.OPTION_CLI_ID));
						
						// Send a CLI_SETICBM packet
						SnacPacket reply1;
						
						//#sijapp cond.if target isnot "DEFAULT"#
						if (Options.getInt(Options.OPTION_TYPING_MODE) > 0)
						{
							reply1 = new SnacPacket(SnacPacket.CLI_SETICBM_FAMILY, SnacPacket.CLI_SETICBM_COMMAND, 0x00000000, new byte[0], ConnectAction.CLI_SETICBM_DATA);
						}
						else
						{
							//#sijapp cond.end#
							tmp_packet = ConnectAction.CLI_SETICBM_DATA;
							tmp_packet[5] = 0x03;
							reply1 = new SnacPacket(SnacPacket.CLI_SETICBM_FAMILY, SnacPacket.CLI_SETICBM_COMMAND, 0x00000000, new byte[0], tmp_packet);
							//#sijapp cond.if target isnot "DEFAULT"#
						}
						//#sijapp cond.end#
						Icq.c.sendPacket(reply1);
						
						// Move to next state
						this.state = ConnectAction.STATE_CLI_COOKIE_SENT;

						// Packet has been consumed
						consumed = true;

					}
				}

			}
			// Watch out for STATE_CLI_COOKIE_SENT
			else if (this.state == ConnectAction.STATE_CLI_COOKIE_SENT)
			{
				if (Options.getBoolean(Options.OPTION_LOADCL))
				{
				// Send a CLI_REQROSTER or
				// CLI_CHECKROSTER packet
					long versionId1 = ContactList.getSsiListLastChangeTime();
					int versionId2 = ContactList.getSsiNumberOfItems();
					if (((versionId1 == -1) && (versionId2 == -1)) || (ContactList.getSize() == 0))
					{
						SnacPacket reply2 = new SnacPacket(SnacPacket.CLI_REQROSTER_FAMILY, SnacPacket.CLI_REQROSTER_COMMAND, 0x00000000, new byte[0], new byte[0]);
						Icq.c.sendPacket(reply2);
						this.state = ConnectAction.STATE_CLI_CHECKROSTER_SENT;
					}
					else
					{
						byte[] data = new byte[6];
						Util.putDWord(data, 0, versionId1);
						Util.putWord(data, 4, versionId2);
						SnacPacket reply2 = new SnacPacket(SnacPacket.CLI_CHECKROSTER_FAMILY, SnacPacket.CLI_CHECKROSTER_COMMAND, 0x00000000, new byte[0], data);
						Icq.c.sendPacket(reply2);
						//data = null;
						this.state = ConnectAction.STATE_CLI_CHECKROSTER_SENT;
					}
				}
				else
				{
					readyPacket();
					this.state = ConnectAction.STATE_CLI_REQOFFLINEMSGS_SENT;
				}
				// Move to next state
				

			}
			// Watch out for STATE_CLI_CHECKROSTER_SENT
			else if (this.state == ConnectAction.STATE_CLI_CHECKROSTER_SENT)
			{

				// Watch out for SNAC packet
				if (packet instanceof SnacPacket)
				{
					SnacPacket snacPacket = (SnacPacket) packet;

					// Watch out for
					// SRV_REPLYROSTEROK
					if ((snacPacket.getFamily() == SnacPacket.SRV_REPLYROSTEROK_FAMILY) && (snacPacket.getCommand() == SnacPacket.SRV_REPLYROSTEROK_COMMAND))
					{
						this.srvReplyRosterRcvd = true;

						// Packet has been consumed
						consumed = true;

					}
					// watch out for SRV_REPLYROSTER
					// packet
					else if ((snacPacket.getFamily() == SnacPacket.SRV_REPLYROSTER_FAMILY) && (snacPacket.getCommand() == SnacPacket.SRV_REPLYROSTER_COMMAND))
					{
						if (snacPacket.getFlags() != 1) this.srvReplyRosterRcvd = true;

						// System.out.println("Flag:
						// "+snacPacket.getFlags());

						// Initialize vector for
						// items
						Vector items = new Vector();

						// Get data
						byte[] buf = snacPacket.getData();
						int marker = 0;

						// Check length
						if (buf.length < 3) { throw (new mipException(115, 0)); }

						// Skip
						// SRV_REPLYROSTER.UNKNOWN
						marker += 1;

						// Iterate through all
						// items
						int count = Util.getWord(buf, marker);
						marker += 2;
				
						
						//System.out.println("Elements in serverlist: "+count);
						for (int i = 0; i < count; i++)
						{

							// Check length
							if (buf.length < marker + 2) { throw (new mipException(115, 1)); }

							// Get name length
							int nameLen = Util.getWord(buf, marker);
							marker += 2;

							// Check length
							if (buf.length < marker + nameLen + 2 + 2 + 2 + 2) { throw (new mipException(115, 2)); }

							// Get name
							String name = Util.byteArrayToString(buf, marker, nameLen, Util.isDataUTF8(buf, marker, nameLen));
							marker += nameLen;

							// Get group, id and type
							int group = Util.getWord(buf, marker);
							int id = Util.getWord(buf, marker + 2);
							int type = Util.getWord(buf, marker + 4);
							marker += 6;

							// Get length of the following TLVs
							int len = Util.getWord(buf, marker);
							marker += 2;

							// Check length
							if (buf.length < marker + len) { throw (new mipException(115, 3)); }

							// Normal contact
							if (type == 0x0000)
							{
								// ByteArrayOutputStream serverData = new ByteArrayOutputStream();
								
								// Get nick
								String nick = new String(name);
								
								boolean noAuth = false;
								while (len > 0)
								{
									byte[] tlvData = Util.getTlv(buf, marker);
									if (tlvData == null) { throw (new mipException(115, 4)); }
									int tlvType = Util.getWord(buf, marker);
									if (tlvType == 0x0131)
									{
										nick = Util.byteArrayToString(tlvData, true);
									}
									else if (tlvType == 0x0066)
									{
										noAuth = true;
									}
									
									//else if (tlvType == 0x006D) /* Server-side additional data */
									//{
									//	Util.writeWord(serverData, tlvType, true);
									//	Util.writeWord(serverData, tlvData.length, true);
									//	Util.writeByteArray(serverData, tlvData);
									//	
									//	Util.showBytes(serverData.toByteArray());
									//}
									
									len -= 4;
									len -= tlvData.length;
									marker += 4 + tlvData.length;
								}
								if (len != 0) { throw (new mipException(115, 5)); }

								// Add this contact item to the vector
																// Add this contact item to the vector
								try
								{
									ContactListContactItem item = new ContactListContactItem(id, group, name, nick, noAuth, true);
									// if (serverData.size() != 0) item.ssData = serverData.toByteArray();

									// Privacy Lists
									Integer iid = (Integer)invisibleList.get(name);
									if (iid != null) {
										item.setInvisibleId(iid.intValue());
									}
									Integer vid = (Integer)visibleList.get(name);
									if (vid != null) {
										item.setVisibleId(vid.intValue());
									}
									Integer xid = (Integer)ignoreList.get(name);
									if (xid != null) {
										item.setIgnoreId(xid.intValue());
									}

									items.addElement(item);
								}

								catch (Exception e)
								{
									// Contact with wrong uin was received  
								}

							}
							// Group of contacts
							else if (type == 0x0001)
							{
								//System.out.println("g "+i+": "+name);
								// Skip TLVs
								marker += len;

								// Add this group item to the vector
																// Add this group item to the vector
								if (group != 0x0000)
								{
									items.addElement(new ContactListGroupItem(group, name));
								}
							}

                            // Privacy Lists
                            // Permit record ("Allow" list in AIM, and "Visible" list in ICQ)
                            else if (type == 0x0002) 
                            {
                               marker += len;
                               visibleList.put(name, new Integer(id));
                            }    
                            // Deny record ("Block" list in AIM, and "Invisible" list in ICQ)
                            else if (type == 0x0003) 
                            {
                               marker += len;
                               invisibleList.put(name, new Integer(id));
                            }
                            // Ignore list record.
                            else if (type == 0x000E) 
                            {
                               marker += len;
                               ignoreList.put(name, new Integer(id));
                            }

							// My visibility settings
							else if (type == 0x0004)
							{
								while (len > 0)
								{
									byte[] tlvData = Util.getTlv(buf, marker);
									if (tlvData == null) { throw (new mipException(115, 110)); }
									int tlvType = Util.getWord(buf, marker);

									if (tlvType == 0x00CA)
									{
										Options.setInt(Options.OPTION_VISIBILITY_ID, (int)id);
									}

									len -= 4;
									len -= tlvData.length;
									marker += 4 + tlvData.length;
								}
								if (len != 0) { throw (new mipException(115, 111)); }
							}
							// All other item types
							else
							{
								//System.out.println("x "+i+": ");
								// Skip TLVs
								marker += len;

							}

						}

						// Check length
						if (buf.length != marker + 4) { throw (new mipException(115, 6)); }

						// Get timestamp
						int timestamp = (int)Util.getDWord(buf, marker);
						
						int m, n;
						
												
						// Update contact list
						ContactListItem[] itemsAsArray = new ContactListItem[items.size()];
						items.copyInto(itemsAsArray);
						ContactList.update(snacPacket.getFlags(), timestamp, count, itemsAsArray);
					items = null;	
						// Packet has been consumed
					consumed = true;

					}

					// Check if all required packets have been received
					if (this.srvReplyRosterRcvd)
					{
						readyPacket();
					}

				}

			}
			// Watch out for STATE_CLI_REQOFFLINEMSGS_SENT
			else if (this.state == ConnectAction.STATE_CLI_REQOFFLINEMSGS_SENT)
			{

				if (packet instanceof FromIcqSrvPacket)
				{
					FromIcqSrvPacket fromIcqSrvPacket = (FromIcqSrvPacket) packet;

					// Watch out for SRV_OFFLINEMSG
					if (fromIcqSrvPacket.getSubcommand() == FromIcqSrvPacket.SRV_OFFLINEMSG_SUBCMD)
					{

						// Get raw data
						byte[] buf = fromIcqSrvPacket.getData();

						// Check length
						if (buf.length < 14) return false;
							

						// Extract UIN
						long uinRaw = Util.getDWord(buf, 0, false);

						String uin = String.valueOf(uinRaw);
						
						// Extract date of dispatch
						long date = Util.createLongTime
									  (
											Util.getWord(buf, 4, false),
											Util.getByte(buf, 6),
											Util.getByte(buf, 7),
											Util.getByte(buf, 8),
											Util.getByte(buf, 9),
											0
									   );
						
						//System.out.println("Offline mess:");
						//System.out.println("hour="+(int)Util.getByte(buf, 8));
						//System.out.println("min="+(int)Util.getByte(buf, 9));
						//System.out.println();

						// Get type
						int type = Util.getWord(buf, 10, false);

						// Get text length
						int textLen = Util.getWord(buf, 12, false);

						// Check length
						//if (buf.length != 14 + textLen) { throw (new mipException(116, 1)); }
						//29.08.2008
						//trying to fix offline msgs bug?

						// Get text
						String text = Util.removeCr(Util.byteArrayToString(buf, 14, textLen, Util.isDataUTF8(buf, 14, textLen)));

						// Normal message
						if (type == 0x0001)
						{
							 // Forward message to contact list
							PlainMessage message = new PlainMessage(uin, this.uin, Util.gmtTimeToLocalTime(date), text, true);
							RunnableImpl.addMessageSerially(message);
						}
						// URL message
						else if (type == 0x0004)
						{

							// Search for delimiter
							int delim = text.indexOf(0xFE);

							// Split message, if delimiter could be found
							String urlText;
							String url;
							if (delim != -1)
							{
								urlText = text.substring(0, delim);
								url = text.substring(delim + 1);
							}
							else
							{
								urlText = text;
								url = "";
							}

							// Forward message message to contact list
							UrlMessage message = new UrlMessage(uin, this.uin, Util.gmtTimeToLocalTime(date), url, urlText);
							RunnableImpl.addMessageSerially(message);
						}

						// Packet has been consumed
						consumed = true;

					}
					// Watch out for SRV_DONEOFFLINEMSGS
					else if (fromIcqSrvPacket.getSubcommand() == FromIcqSrvPacket.SRV_DONEOFFLINEMSGS_SUBCMD)
					{

						// Send a CLI_TOICQSRV/CLI_ACKOFFLINEMSGS packet
						ToIcqSrvPacket reply = new ToIcqSrvPacket(0x00000000, this.uin, ToIcqSrvPacket.CLI_ACKOFFLINEMSGS_SUBCMD, new byte[0], new byte[0]);
						Icq.c.sendPacket(reply);

						// Move to next state
						this.state = ConnectAction.STATE_CLI_ACKOFFLINEMSGS_SENT;

						// Move to STATE_CONNECTED
						Icq.setConnected();

						// Packet has been consumed
						consumed = true;

					}

				}

			}

            // Update activity timestamp and reset activity flag
            this.lastActivity = new Date();
            this.active = false;

            // Return consumption flag
            return (consumed);

        }
        // Catch mipExceptions
        catch (mipException e)
        {

            // Update activity timestamp and reset activity flag
            this.lastActivity = new Date();
            this.active = false;

            // Set error state if exception is critical
            if (e.isCritical()) this.state = ConnectAction.STATE_ERROR;

            // Forward exception
            throw (e);

        }

    }

    // Returns true if the action is completed
    public boolean isCompleted()
    {
        return (this.state == ConnectAction.STATE_CLI_ACKOFFLINEMSGS_SENT);
    }

    // Returns true if an error has occured
    public boolean isError()
    {
    	if ((this.state != ConnectAction.STATE_ERROR) && !this.active && (this.lastActivity.getTime() + this.TIMEOUT < System.currentTimeMillis()))
        {
    		mipException e = new mipException(118, 0);
    		if( !Icq.reconnect(e) )
    			mipException.handleException(e);
            this.state = ConnectAction.STATE_ERROR;
        }
        return (this.state == ConnectAction.STATE_ERROR);
    }

    // Returns a number between 0 and 100 (inclusive) which indicates the current progress
    public int getProgress()
    {
        switch (this.state)
        {
    case ConnectAction.STATE_INIT_DONE:
			if(Options.getBoolean(Options.OPTION_MULTIPLESERVERS)) 
			{	
				SplashCanvas.setMessage(String.valueOf(iServer + 1) + ": " + servers[iServer]);
            }
			else
			{
				SplashCanvas.setMessage(this.srvHost);
			}
			return 10;
		case STATE_AUTHKEY_REQUESTED:
			if(Options.getBoolean(Options.OPTION_MULTIPLESERVERS)) 
			{	
				SplashCanvas.setMessage(String.valueOf(iServer + 1) + ": " + servers[iServer]);
            }
			else
			{
				SplashCanvas.setMessage(this.srvHost);
			}
			return 25;    case ConnectAction.STATE_CLI_IDENT_SENT:
			SplashCanvas.setMessage("CliIdent Sent..");
            return 35;
        case ConnectAction.STATE_CLI_DISCONNECT_SENT:
			SplashCanvas.setMessage("CliDisconnect Received..");
            return 50;
        case ConnectAction.STATE_CLI_COOKIE_SENT:
			SplashCanvas.setMessage("CliCookie Sent..");
            return 65;
        case ConnectAction.STATE_CLI_CHECKROSTER_SENT:
			SplashCanvas.setMessage("CliCheckRoster Sent..");
            return 75;
        case ConnectAction.STATE_CLI_REQOFFLINEMSGS_SENT:
			SplashCanvas.setMessage("CliReqOffMsg Sent..");
            return 90;
        case ConnectAction.STATE_CLI_ACKOFFLINEMSGS_SENT:
			SplashCanvas.setMessage("Finished! =)");
            return 100;
        default:
			//SplashCanvas.setMessage("Initialization..");
			return (0);
        }
    }
    
	void readyPacket() throws mipException
	{
		SnacPacket reply1 = new SnacPacket(SnacPacket.CLI_ROSTERACK_FAMILY, SnacPacket.CLI_ROSTERACK_COMMAND, 0x00000000, new byte[0], new byte[0]);
					Icq.c.sendPacket(reply1);
					
						Icq.setCliStatus(true, true);
						
						// Send a CLI_READY packet
						SnacPacket reply2 = new SnacPacket(SnacPacket.CLI_READY_FAMILY, SnacPacket.CLI_READY_COMMAND, 0x00000000, new byte[0], ConnectAction.CLI_READY_DATA);
						Icq.c.sendPacket(reply2);

						// Send a CLI_TOICQSRV/CLI_REQOFFLINEMSGS packet
						ToIcqSrvPacket reply3 = new ToIcqSrvPacket(0x00000000, this.uin, ToIcqSrvPacket.CLI_REQOFFLINEMSGS_SUBCMD, new byte[0], new byte[0]);
						Icq.c.sendPacket(reply3);

						// Move to next state
						this.state = ConnectAction.STATE_CLI_REQOFFLINEMSGS_SENT;
						
	}
	
    public void onEvent(int eventType) throws mipException
    {
    	switch (eventType)
    	{
    	case ON_COMPLETE:
			ContactList.activate();
			ContactList.updateCheck();
			Options.safe_save();
			MIP.isFullyConnected = true;
    		break;
		case ON_CANCEL:
		if (this.state < ConnectAction.STATE_CLI_ACKOFFLINEMSGS_SENT) 
			{
				this.state = ConnectAction.STATE_ERROR;
				Icq.disconnect();
				ContactList.activate();
                                if ((Options.getBoolean(Options.OPTION_AUTOAWAY_ENABLE)) || (Options.getBoolean(Options.OPTION_AUTONA_ENABLE)))
                                    TimerTasks.setStatusTimer();
			}
		}
    }

}