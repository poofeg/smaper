package mip.comm;

import java.io.*;
import mip.Options;
import mip.ContactList;
import mip.ContactListContactItem;
import mip.mipException;

public final class XtrazSM
{

    public XtrazSM()
    {
    }
    public static void b(String s, String s1)
    {
        int i;
        if((i = s1.indexOf("<NR><RES>")) < 0)
            return;
        int j;
        if((j = s1.indexOf("</RES></NR>")) < 0)
            return;
        String s2;
        int k;
        if((k = (s2 = Util.DeMangleXml(s1.substring(i + 9, j))).indexOf("<val srv_id='")) < 0)
            return;
        int i1;
        if((i1 = s2.indexOf(">", k)) < 0)
            return;
        if(s2.substring(k + 13, i1 - 1).toLowerCase().compareTo("cawaysrv") != 0)
            return;
        int j1;
        if((j1 = s2.indexOf("<uin>")) < 0)
            return;
        int k1;
        if((k1 = s2.indexOf("</uin>")) < 0)
            return;
        if(s2.substring(j1 + 5, k1).compareTo(s) != 0)
            return;
        int l1;
        if((l1 = s2.indexOf("<index>")) < 0)
            return;
        int i2;
        if((i2 = s2.indexOf("</index>")) < 0)
            return;
        s2.substring(l1 + 7, i2);
        int j2;
        if((j2 = s2.indexOf("<title>")) < 0)
            return;
        int k2;
        if((k2 = s2.indexOf("</title>")) < 0)
            return;
        String s3 = s2.substring(j2 + 7, k2);
        int l2;
        if((l2 = s2.indexOf("<desc>")) < 0)
            return;
        int i3;
        if((i3 = s2.indexOf("</desc>")) < 0)
            return;
        String s4 = s2.substring(l2 + 6, i3);
       ContactListContactItem l3;
        if((l3 =ContactList.getItembyUIN(s)) != null)
        {
            l3.setStringValue(ContactListContactItem.CONTACTITEM_xTitle, s3);
            l3.setStringValue(ContactListContactItem.CONTACTITEM_xMessage, s4);
			l3.addXtraz(s3, s4);
			ContactList.repaintTree();
			//ContactList.addMessage(new PlainMessage(l3.getStringValue(0), Options.getString(Options.OPTION_UIN), Util.createCurrentDate(false), ResourceBundle.getString("xtraz_m") + ":\n" + s3 + "\n" + s4, false), false);
            //a(s3, s4);
        }
	    return;
    }

    public static void a(String s, String s1, int i, long l1, long l2)
    {
		int j = 0;
        if (!(Options.getBoolean(Options.OPTION_SHOWXTRAZMSG))) return;
		int k;
        if((k = s1.indexOf("<QUERY>")) < 0)
            return;
        int i1;
        if((i1 = s1.indexOf("</QUERY>")) < 0)
			return;
        int j1;
        if((j1 = s1.indexOf("<NOTIFY>")) < 0)
            return;
        int k1;
        if((k1 = s1.indexOf("</NOTIFY>")) < 0)
            return;
        String s2;
        int i2;
        if((i2 = (s2 = Util.DeMangleXml(s1.substring(k + 7, i1))).indexOf("<PluginID>")) < 0)
            return;
        int j2;
        if((j2 = s2.indexOf("</PluginID>")) < 0)
            return;
        if(s2.substring(i2 + 10, j2).toLowerCase().compareTo("srvmng") != 0)
            return;
        String s3;
        if((s3 = Util.DeMangleXml(s1.substring(j1 + 8, k1))).indexOf("AwayStat") < 0)
            return;
        int k2;
        if((k2 = s3.indexOf("<senderId>")) < 0)
            return;
        int i3;
        if((i3 = s3.indexOf("</senderId>")) < 0)
            return;
        if(s3.substring(k2 + 10, i3).compareTo(s) != 0)
            return;
       ContactListContactItem l3;
	    if((l3 =ContactList.getItembyUIN(s)) != null)
			a(s, j, Options.getString(Options.OPTION_STATUS_TITLE), Options.getString(Options.OPTION_STATUS_MESSAGE), i, l1, l2); //Xtraz SM Title & Message!!
        return;
        
    }

	//s1 = Xtitle, s2 = Xmessage
    private static void a(String s, int i, String s1, String s2, int j, long l1, long l2)
    {
        byte abyte0[];
        String s3 = "<NR><RES>" + Util.MangleXml("<ret event='OnRemoteNotification'><srv><id>cAwaySrv</id><val srv_id='cAwaySrv'><Root><CASXtraSetAwayMessage></CASXtraSetAwayMessage><uin>" + Options.getString(Options.OPTION_UIN) + "</uin>" + "<index>" + "1" + "</index>" + "<title>" + s1 + "</title>" + "<desc>" + s2 + "</desc></Root></val></srv></ret>") + "</RES></NR>";
        abyte0 = a(s, j, l1, l2, s3);
        SnacPacket SnacPacket1 = new SnacPacket(4, 11, 0L, new byte[0], abyte0);
        try{
			Icq.c.sendPacket(SnacPacket1);}
		catch (Exception e){}
        return;
    }

    public static void a(String s, int ID) throws mipException
    {
        SnacPacket SnacPacket1;
        String s1 = "<N><QUERY>" + Util.MangleXml("<Q><PluginID>srvMng</PluginID></Q>") + "</QUERY><NOTIFY>" + Util.MangleXml("<srv><id>cAwaySrv</id><req><id>AwayStat</id><trans>1") +Util.MangleXml("</trans><senderId>" + Options.getString(Options.OPTION_UIN) + "</senderId></req></srv>") + "</NOTIFY></N>";
        byte abyte0[] = b(s, Util.getCounter(), System.currentTimeMillis(), 0L, s1);
        SnacPacket1 = new SnacPacket(4, 6, 0L, new byte[0], abyte0);
        Icq.c.sendPacket(SnacPacket1);
        return;
     }

    private static byte[] a(String s, int i, long l1, long l2, String s1)
    {
        byte abyte0[] = new byte[0];
        int j = 0;
        try
        {
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            (new DataOutputStream(bytearrayoutputstream)).writeUTF(s1);
            j = (abyte0 = bytearrayoutputstream.toByteArray()).length - 2;
        }
        catch(Exception e) { }
        int k = 0;
        byte abyte1[];
        k = a(abyte1 = new byte[s.length() + 64 + (84 + (8 + j))], 0, s, l1, l2, i, (byte)26, (byte)0);
        k = a(abyte1, k);
        k = b(abyte1, k);
        Util.putWord(abyte1, k, j + 4, false);
        k += 4;
        Util.putWord(abyte1, k, j, false);
        k += 4;
        if(j > 0)
            System.arraycopy(abyte0, 2, abyte1, k, j);
        return abyte1;
    }

    private static byte[] b(String s, int i, long l1, long l2, String s1)
    {
        int j = 0;
        int k = 92 + s1.length();
        byte abyte0[];
        Util.putDWord(abyte0 = new byte[11 + s.length() + 95 + k + 4], 0, l1, false);
        Util.putDWord(abyte0, 4, l2, false);
        Util.putWord(abyte0, 8, 2);
        Util.putByte(abyte0, 10, s.length());
        System.arraycopy(Util.stringToByteArray(s), 0, abyte0, 11, s.length());
        j = 11 + s.length();
        j = a(abyte0, j, 55 + k, l1, l2, 1);
        j = a(abyte0, j, i, 0, 256, k);
        j = a(abyte0, j);
        j = b(abyte0, j);
        Util.putWord(abyte0, j, s1.length() + 4, false);
        j += 4;
        Util.putWord(abyte0, j, s1.length(), false);
        j += 4;
        System.arraycopy(Util.stringToByteArray(s1), 0, abyte0, j, s1.length());
        j += s1.length();
        Util.putDWord(abyte0, j, 0x30000L);
        return abyte0;
    }

    private static int a(byte abyte0[], int i, int j, long l1, long l2, int k)
    {
        Util.putWord(abyte0, i, 5);
        i += 2;
        Util.putWord(abyte0, i, 36 + j);
        i += 2;
        Util.putWord(abyte0, i, 0);
        i += 2;
        Util.putDWord(abyte0, i, l1, false);
        i += 4;
        Util.putDWord(abyte0, i, l2, false);
        i += 4;
        Util.putDWord(abyte0, i, 0x9461349L);//some unknown stuff...
        Util.putDWord(abyte0, i + 4, 0x4c7f11d1L);
        Util.putDWord(abyte0, i + 8, 0xffffffff82224445L);
        Util.putDWord(abyte0, i + 12, 0x53540000L);
        i += 16;
        Util.putDWord(abyte0, i, 0xa0002L);
        i += 4;
        Util.putDWord(abyte0, i, k);
        i += 2;
        Util.putDWord(abyte0, i, 0xf0000L);
        return i += 4;
    }

    private static int a(byte abyte0[], int i, String s, long l1, long l2, int j, 
            byte byte0, byte byte1)
    {
        Util.putDWord(abyte0, i, l1, false);
        i += 4;
        Util.putDWord(abyte0, i, l2, false);
        i += 4;
        Util.putWord(abyte0, i, 2);
        i += 2;
        Util.putByte(abyte0, i, s.length());
        i++;
        System.arraycopy(Util.stringToByteArray(s), 0, abyte0, i, s.length());
        i += s.length();
        Util.putWord(abyte0, i, 3);
        i += 2;
        Util.putWord(abyte0, i, 27, false);
        i += 2;
        Util.putWord(abyte0, i, 8);
        i++;
        Util.putDWord(abyte0, i, 0L);
        Util.putDWord(abyte0, i + 4, 0L);
        Util.putDWord(abyte0, i + 8, 0L);
        Util.putDWord(abyte0, i + 12, 0L);
        i += 16;
        Util.putDWord(abyte0, i, 3L);
        i += 4;
        Util.putDWord(abyte0, i, 4L);
        i += 4;
        Util.putWord(abyte0, i, j, false);
        i += 2;
        Util.putWord(abyte0, i, 14, false);
        i += 2;
        Util.putWord(abyte0, i, j, false);
        i += 2;
        Util.putDWord(abyte0, i, 0L);
        i += 4;
        Util.putDWord(abyte0, i, 0L);
        i += 4;
        Util.putDWord(abyte0, i, 0L);
        i += 4;
        Util.putByte(abyte0, i, byte0);
        i++;
        Util.putByte(abyte0, i, byte1);
        i++;
        Util.putWord(abyte0, i, 0, false);
        i += 2;
        Util.putWord(abyte0, i, 0);
        return i += 2;
    }

    private static int a(byte abyte0[], int i, int j, int k, int i1, int j1)
    {
        Util.putWord(abyte0, i, 10001);
        i += 2;
        Util.putWord(abyte0, i, 51 + j1);
        i += 2;
        Util.putWord(abyte0, i, 27, false);
        i += 2;
        Util.putByte(abyte0, i, 8);
        i++;
        Util.putDWord(abyte0, i, 0L);
        Util.putDWord(abyte0, i + 4, 0L);
        Util.putDWord(abyte0, i + 8, 0L);
        Util.putDWord(abyte0, i + 12, 0L);
        i += 16;
        Util.putDWord(abyte0, i, 3L);
        i += 4;
        Util.putDWord(abyte0, i, 0L);
        i += 4;
        Util.putWord(abyte0, i, j, false);
        i += 2;
        Util.putWord(abyte0, i, 14, false);
        i += 2;
        Util.putWord(abyte0, i, j, false);
        i += 2;
        Util.putDWord(abyte0, i, 0L);
        i += 4;
        Util.putDWord(abyte0, i, 0L);
        i += 4;
        Util.putDWord(abyte0, i, 0L);
        i += 4;
        Util.putByte(abyte0, i, 26);
        i++;
        Util.putByte(abyte0, i, 0);
        i++;
        Util.putWord(abyte0, i, k, false);
        i += 2;
        Util.putWord(abyte0, i, i1);
        return i += 2;
    }

    private static int a(byte abyte0[], int i)
    {
        Util.putWord(abyte0, i, 1, false);
        i += 2;
        Util.putByte(abyte0, i, 0);
        return ++i;
    }

    private static int b(byte abyte0[], int i)
    {
        Util.putWord(abyte0, i, 79, false);
        i += 2;
        Util.putDWord(abyte0, i, 0x3b60b3efL); // unknown stuff..
        Util.putDWord(abyte0, i + 4, 0xffffffffd82a6c45L);
        Util.putDWord(abyte0, i + 8, 0xffffffffa4e09c5aL);
        Util.putDWord(abyte0, i + 12, 0x5e67e865L);
        i += 16;
        Util.putWord(abyte0, i, 8, false);
        i += 2;
        Util.putDWord(abyte0, i, 42L, false);
        i += 4;
        System.arraycopy(Util.stringToByteArray("Script Plug-in: Remote Notification Arrive"), 0, abyte0, i, 42);
        i += 42;
        Util.putDWord(abyte0, i, 256L);
        i += 4;
        Util.putDWord(abyte0, i, 0L);
        i += 4;
        Util.putDWord(abyte0, i, 0L);
        i += 4;
        Util.putWord(abyte0, i, 0);
        i += 2;
        Util.putByte(abyte0, i, 0);
        return ++i;
    }
}