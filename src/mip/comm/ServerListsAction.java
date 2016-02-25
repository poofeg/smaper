package mip.comm;

import java.util.*;
import java.io.*;
import mip.comm.*;
import mip.*;

public class ServerListsAction extends Action {

	// Receive timeout
	private static final int TIMEOUT = 3 * 1000; // milliseconds
	
    public static final int VISIBLE_LIST   = 0x0002;
    public static final int INVISIBLE_LIST = 0x0003;
    public static final int IGNORE_LIST    = 0x000E;

    public static final int ADD_INTO_LIST      = 0;
    public static final int REMOVE_FROM_LIST   = 1;

	/****************************************************************************/
	
	// Date of init

    private int subaction;
    private int list;
    private ContactListContactItem item;
    private Date init;
    private int id;

	// Constructor
	public ServerListsAction(int list, ContactListContactItem item) {
		super(false, true);
        this.list      = list;
        this.item      = item;
	}

    // Init action
	public synchronized void init() throws mipException {
        id = 0;
        switch (list) {
            case VISIBLE_LIST:
                id = item.getVisibleId();
                break;
                
            case INVISIBLE_LIST:
                id = item.getInvisibleId();
                break;
                
            case IGNORE_LIST:
                id = item.getIgnoreId();
                break;
            
        }

        if (id == 0) {
            id = Util.createRandomId();
            subaction = ADD_INTO_LIST;
        } else {
            subaction = REMOVE_FROM_LIST;
        }
        		
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Util.writeLenAndString(stream, Integer.toString(item.getUIN()), true);
        Util.writeWord(stream, 0, true);
        Util.writeWord(stream, id, true);
        Util.writeWord(stream, list, true);
		Util.writeWord(stream, 0, false);

        SnacPacket packet = null;
        switch (subaction) {
            case ADD_INTO_LIST:
                packet = new SnacPacket(SnacPacket.CLI_ROSTERADD_FAMILY, SnacPacket.CLI_ROSTERADD_COMMAND, 0, new byte[0], stream.toByteArray());
                break;
                
            case REMOVE_FROM_LIST:
                packet = new SnacPacket(SnacPacket.CLI_ROSTERDELETE_FAMILY, SnacPacket.CLI_ROSTERDELETE_COMMAND, 0, new byte[0], stream.toByteArray());
                id = 0;
                break;
        }
        //if (packet != null) {
        Icq.c.sendPacket(packet);
        //} else {
        //    throw new mipException();
        //}
        init = new Date();
	}
	
    private int packetCounter = 0;
    
	// Forwards received packet, returns true if packet was consumed
	public synchronized boolean forward(Packet packet) throws mipException {

        // Watch out for SRV_FROMICQSRV packet
		if (packet instanceof SnacPacket) {
            SnacPacket snacPacket = (SnacPacket)packet;
            if (snacPacket.getFamily() != SnacPacket.SRV_UPDATEACK_FAMILY
                    || snacPacket.getCommand() != SnacPacket.SRV_UPDATEACK_COMMAND) {
                return false;
            }
			//FromIcqSrvPacket fromIcqSrvPacket = (FromIcqSrvPacket) packet;
            byte[] data = snacPacket.getData();
            int result = Util.getWord(data, 0, false);
            if (result == 0) {
                switch (list) {
                    case VISIBLE_LIST:
                        item.setVisibleId(id);
                        break;

                    case INVISIBLE_LIST:
                        item.setInvisibleId(id);
                        break;

                    case IGNORE_LIST:
                        item.setIgnoreId(id);
                        break;
                }
                ContactList.update(item);
            }
            packetCounter++;
            return true;
		}

		return false;
	}
	
	// Returns true if the action is completed
	public synchronized boolean isCompleted() {
		return (packetCounter >= 1);
	}

	// Returns true if an error has occured
	public synchronized boolean isError() {
		return (init.getTime() + TIMEOUT) < System.currentTimeMillis();
	}
	
	public int getProgress() {
		return packetCounter * 100 / 1;
	}
}
