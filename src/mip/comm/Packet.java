package mip.comm;


import mip.mipException;


public class Packet
{


	// Channel constants
	public static final int CHANNEL_CONNECT = 0x01;
	public static final int CHANNEL_SNAC = 0x02;
	public static final int CHANNEL_ERROR = 0x03;
	public static final int CHANNEL_DISCONNECT = 0x04;
	public static final int CHANNEL_PING = 0x05;


	// FLAP sequence number
	protected int sequence;
	
	protected int flapChannel;
	protected byte[] flapData;
	
	// Returns the FLAP sequence number
	public int getSequence()
	{
		return (this.sequence);
	}

	// Sets the FLAP sequence number
	void setSequence(int sequence)
	{
		this.sequence = sequence;
	}

	protected Packet() {}

	public Packet(int channel, byte[] data) {
		flapChannel = channel;
		flapData = data;
	}

	// Returns the package as byte array
	public byte[] toByteArray() {
		byte[] buf = new byte[6 + flapData.length];
		Util.putByte(buf, 0, 0x2a);
		Util.putByte(buf, 1, flapChannel);
		Util.putWord(buf, 2, Icq.getFlapSequence());
		Util.putWord(buf, 4, flapData.length);
		System.arraycopy(flapData, 0, buf, 6, flapData.length);
		return buf;
	}

	
	// Parses given byte array and returns a Packet object
	public static Packet parse(byte[] buf, int off, int len) throws mipException
	{

		// Check length (min. 6 bytes)
		if (len < 6)
	    {
			throw (new mipException(130, 0));
		}

		// Verify FLAP.ID
		if (Util.getByte(buf, off) != 0x2A)
		{
			throw (new mipException(130, 1));
		}

		// Get and verify FLAP.CHANNEL
		int channel = Util.getByte(buf, off + 1);
		if ((channel < 1) || (channel > 5))
		{
			throw (new mipException(130, 2));
		}

		// Verify FLAP.LENGTH
		int length = Util.getWord(buf, off + 4);
		if ((length + 6) != len)
		{
			throw (new mipException(130, 3));
		}

		// Parsing is done by a subclass
		switch (channel)
		{
			case Packet.CHANNEL_CONNECT:
				return (ConnectPacket.parse(buf, off, len));
			case Packet.CHANNEL_SNAC:
				return (SnacPacket.parse(buf, off, len));
			case Packet.CHANNEL_DISCONNECT:
				return (DisconnectPacket.parse(buf, off, len));
			default:
				return null;
		}

	}


	// Parses given byte array and returns a Packet object
	public static Packet parse(byte[] buf) throws mipException
	{
		return (Packet.parse(buf, 0, buf.length));
	}


}
