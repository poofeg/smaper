package mip;

public interface ContactListItem
{
	// Checks whether some other object is equal to this one
	public abstract boolean equals(Object obj);

	// returns image index of tree node. Is used for visual tree
	public int getImageIndex();
	
	public int getXIndex();
	
	public String getText();
	 public String getSortText();
	 	 
	 	         public int getSortWeight();
	
	public int getTextColor();
	
	public int getFontStyle();
	
	public int getCliIcon();
}
