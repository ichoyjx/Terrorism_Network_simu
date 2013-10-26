
package terroristSimulation;

import java.awt.Color;
import uchicago.src.sim.gui.OvalNetworkItem;
import uchicago.src.sim.network.DefaultDrawableNode;
import uchicago.src.sim.network.DefaultNode;

/**
 * @author ichoyjx
 *Node, specify each node in the agentList in TSModel.
 */
public class TSNode extends DefaultDrawableNode{
	private boolean isTreeNode;
	private String lable;
	private int faith;
	private int resource;
	//faith will not rise after 20 times rising. you can change the value here.
	private int riseFaithLimit = 20;
	//resource will not rise after 20 times rising. you can change the value here.
	private int riseResourceLimit = 20;
	
	private int treeNumber=0;
	
	private boolean isTopNode=false;
	private boolean isBackup = false;
	public TSNode(){}
	
	public TSNode(int x, int y, String label){
		init(x, y, label);
	}
	
	public void init(int x, int y, String label){
		lable = label;
		OvalNetworkItem oval = new OvalNetworkItem(x, y);
		oval.setLabel(lable);
		setDrawable(oval);
	}
	public void makeEdgeTo(DefaultNode node, float strength, Color color){
		if(node == null)
			return;
		if(!(hasEdgeTo(node))){
			TSEdge edge = new TSEdge(this, node, strength, color);
			addOutEdge(edge);
			node.addInEdge(edge);
		}
	}
	public boolean getIsTreeNode()
	{
		return isTreeNode;
	}
	public void setIsTreeNode(boolean _isTreeNode)
	{
		isTreeNode = _isTreeNode;
	}

	public int getResource() {
		return resource;
	}

	public void setResource(int _resource) {
		riseResourceLimit--;
		if(riseResourceLimit>0)
			this.resource = _resource;
		else
		{
			//System.out.println("Node "+lable+" cannot rise its resource any more.");
		}
	}

	public int getFaith() {
		return faith;
	}

	public void setFaith(int _faith) {
		riseFaithLimit--;
		if(riseFaithLimit>0)
			this.faith = _faith;
		else
		{
			//System.out.println("Node "+lable+" cannot rise its faith any more.");
		}
	}
	public double calculateDistance(TSNode node)
	{
		//calculate the distance from this to the input node.
		return Math.sqrt((this.getX()-node.getX())*(this.getX()-node.getX())+(this.getY()-node.getY())*(this.getY()-node.getY()));
	}
	public String getLable()
	{
		return lable;
	}

	public int getTreeNumber() {
		return treeNumber;
	}

	public void setTreeNumber(int treeNumber) {
		this.treeNumber = treeNumber;
	}

	public boolean isTopNode() {
		return isTopNode;
	}

	public void setTopNode(boolean isTopNode) {
		this.isTopNode = isTopNode;
	}

	public boolean isBackup() {
		return isBackup;
	}

	public void setBackup(boolean isBackup) {
		this.isBackup = isBackup;
	}
}
