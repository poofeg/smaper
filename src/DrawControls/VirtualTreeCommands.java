package DrawControls;

public interface VirtualTreeCommands
{
	public void VTGetItemDrawData(TreeNode src, ListItem dst);
	public void VTnodeClicked(TreeNode node);
	public int compareNodes(TreeNode node1, TreeNode node2);
}
