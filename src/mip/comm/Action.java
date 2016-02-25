package mip.comm;


import mip.mipException;


public abstract class Action
{
	final static public int ON_COMPLETE = 1;
	final static public int ON_CANCEL   = 2;
	final static public int ON_ERROR    = 3;
	
	// ICQ object
	protected Icq icq;
	
	private boolean exclusive, executableConnected;

	
	protected Action(boolean exclusive, boolean executableConnected)
	{
		this.exclusive = exclusive;
		this.executableConnected = executableConnected;
	}

	// Set ICQ object
	protected void setIcq(Icq icq)
	{
		this.icq = icq;
	}

	// Returns true if the action can be performed
	final public boolean isExecutable()
	{
		if (executableConnected) return Icq.isConnected();
		return Icq.isNotConnected();
	}


	// Returns true if this is an exclusive command
	final public boolean isExclusive()
	{
		return exclusive;
	}


	// Init action
	protected abstract void init() throws mipException;


	// Forwards received packet, returns true if packet was consumed
	protected abstract boolean forward(Packet packet) throws mipException;


	// Returns true if the action is completed
	public abstract boolean isCompleted();


	// Returns ture if an error has occured
	public abstract boolean isError();

	// Returns a number between 0 and 100 (inclusive) which indicates the progress
	public int getProgress()
	{
		if (this.isCompleted())
			return (100);
		else
			return (0);
	}
	
	public void onEvent(int eventType) throws mipException {} 
}
