package DrawControls;

import DrawControls.VirtualList;

public interface VirtualListCommands
{
	public void onKeyPress(VirtualList sender, int keyCode, int type);
	public void onCursorMove(VirtualList sender);
	public void onItemSelected(VirtualList sender);
}