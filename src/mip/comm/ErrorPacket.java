package mip.comm;


import mip.mipException;


public class ErrorPacket extends Packet
{


	// Returns the package as byte array
	public byte[] toByteArray()
	{
		return (null);
	}


	// Parses given byte array and returns a Packet object
	public static Packet parse(byte[] buf, int off, int len) throws mipException
	{
		return (null);
	}


	// Parses given byte array and returns a Packet object
	public static Packet parse(byte[] buf) throws mipException
	{
		return (ErrorPacket.parse(buf, 0, buf.length));
	}


}
