package terroristSimulation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.io.*;

import uchicago.src.reflector.ListPropertyDescriptor;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Network2DDisplay;
import uchicago.src.sim.network.DefaultNode;
import uchicago.src.sim.util.Random;

/**
 * @author ichoyjx
 * Model, controls all the behavior of the Nodes and Edges.
 */
public class TSModel extends SimModelImpl {

	private Color background = Color.black;
	private Color linkColor = Color.red;
	private Color NodeColor = Color.blue;
	private Color LabelColor = Color.white;
	private int numNodes = 25;
	private ArrayList<TSNode> agentList = new ArrayList<TSNode>();
	private int worldXSize = 500;
	private int worldYSize = 500;
	private int initialSteps = 1;
	private String layoutType = "Random";
	private String backgroundColor = "black";
	private String edgeColor = "Red";
	private String nodeColor = "Blue";
	private String labelColor = "White";
	private DisplaySurface surface;
	private Schedule schedule;
	private BasicAction initialAction;
	private static int treeCount = 0;
	private int backupNode = 0;
	private String fileDirectory = "C:\\TerroistSimulation\\";
	public TSModel() {
		Vector<String> vect = new Vector<String>();
		vect.add("Random");
		vect.add("TreeNode");
		ListPropertyDescriptor pd = new ListPropertyDescriptor("LayoutType",
				vect);
		descriptors.put("LayoutType", pd);
		
		Vector<String> vectBC = new Vector<String>();
		vectBC.add("Black");
		vectBC.add("White");
		ListPropertyDescriptor pdBC = new ListPropertyDescriptor("BackgroundColor",vectBC);
		descriptors.put("BackgroundColor", pdBC);
		
		Vector<String> vectEC = new Vector<String>();
		vectEC.add("Red");
		vectEC.add("White");
		vectEC.add("Yellow");
		vectEC.add("Blue");
		vectEC.add("Green");
		vectEC.add("Black");
		vectEC.add("White");
		ListPropertyDescriptor pdEC = new ListPropertyDescriptor("EdgeColor",vectEC);
		descriptors.put("EdgeColor", pdEC);
		
		Vector<String> vectNC = new Vector<String>();
		vectNC.add("Red");
		vectNC.add("White");
		vectNC.add("Yellow");
		vectNC.add("Blue");
		vectNC.add("Green");
		vectNC.add("Black");
		vectNC.add("White");
		ListPropertyDescriptor pdNC = new ListPropertyDescriptor("NodeColor",vectNC);
		descriptors.put("NodeColor", pdNC);
		
		Vector<String> vectLC = new Vector<String>();
		vectLC.add("Red");
		vectLC.add("White");
		vectLC.add("Yellow");
		vectLC.add("Blue");
		vectLC.add("Green");
		vectLC.add("Black");
		vectLC.add("White");
		ListPropertyDescriptor pdLC = new ListPropertyDescriptor("LabelColor",vectLC);
		descriptors.put("LabelColor", pdLC);
		
		
	}

	public void removeInitialAction() {
		schedule.removeAction(initialAction);
	}

	public void createInitialLinks() {
		refreshLinks();
	}

	public void clearLinks() {
		//all links  will be cleared here.
		for (int i = 0; i < agentList.size(); i++) {
			agentList.get(i).clearInEdges();
			agentList.get(i).clearOutEdges();
		}
	}
	
	public void removeTopNodeAt(int tickCount)
	{
		//remove one node at every tickCount.
		//this simple logic is find out the node
		//that has the most links.
		if(this.getTickCount()%tickCount==0)
		{
			int indegree=0;
			TSNode tmpNode = null;
			for(int i=0;i<agentList.size();i++)
			{
				if(!agentList.get(i).isTopNode()&&indegree<agentList.get(i).getNumInEdges())
				{
					indegree = agentList.get(i).getNumInEdges();
					tmpNode = agentList.get(i);
				}
			}
			System.out.println("Node "+tmpNode.getLable()+" dead");
			removeNode(tmpNode);
		}
	}
	
	private void removeNode(TSNode node) {
		// remove the node from agentList
		// but we just set its color to the background color,
		//not really remove it from the UI.
		if(node == null)
			return;
		removeEdges(node);
		node.setWidth(0);
		node.setColor(background);
		node.setLabelColor(background);
		node.setX(worldXSize+20);
		node.setY(worldYSize+20);
		agentList.remove(node);
		System.gc();
	}

	public void removeEdges(DefaultNode node) {
		//remove all the links for the input node.
		for (int i = 0; i < agentList.size(); i++) {
			DefaultNode otherNode = agentList.get(i);
			if (!(node == otherNode)) {
				node.removeEdgesFrom(otherNode);
				node.removeEdgesTo(otherNode);
			}
		}

	}

	public void refreshLinks() {
		if (layoutType == "Random") {
			//first loop, try to link for each node.
			for (int i = 0; i < agentList.size(); i++) {
				
				TSNode node = agentList.get(i);
				if(node.isBackup())
					continue;
				TSNode otherNode = getLinkNode(node);
				if(otherNode == null)
				{
					return;
				}
				if (!(node == otherNode)) {
					node.makeEdgeTo(otherNode, 1, linkColor);
					if(otherNode.getTreeNumber() != 0)
					{
						node.setTreeNumber(node.getTreeNumber());
					}
					else if(node.getTreeNumber() != 0)
					{
						otherNode.setTreeNumber(otherNode.getTreeNumber());
					}
					else
					{
						otherNode.setTreeNumber(++treeCount);
						node.setTreeNumber(treeCount);
					}
				}
			}
			//Second loop, try to link the top of each forest
			ArrayList<TSNode> topNodes= new ArrayList<TSNode>();
			for(int i = 1;i<=treeCount;i++)
			{
				int maxEdge=0;
				TSNode subTopNode = null;
				for(int j = 0;j<agentList.size();j++)
				{
					TSNode tmpNode = agentList.get(j);
					if(tmpNode.getTreeNumber() == i&&maxEdge<tmpNode.getNumInEdges())
					{
						maxEdge = tmpNode.getNumInEdges();
						subTopNode = tmpNode;
					}
				}
				if(subTopNode!=null)
				{
					topNodes.add(subTopNode);
				}
			}
			for(int i=0;i<topNodes.size()-1;i++)
			{
				topNodes.get(i).makeEdgeTo(topNodes.get(i+1), 1, linkColor);
			}
		} else if (layoutType == "TreeNode") {
			// modify your tree node links here.
		}
	}
	private TSNode getTopLinkNode(TSNode node) {
		//find the best Parent node for the input node.
		//this simple logic is (faith+resource)/distance.
		//modify your logic here.
		double maxEdge= -1;
		TSNode retNode = null;
		for (int i = 0; i < agentList.size(); i++) {
			TSNode tmpNode = agentList.get(i);
			if(tmpNode.isBackup()||tmpNode.getTreeNumber()==node.getTreeNumber()||tmpNode.hasEdgeToOrFrom(node))
				continue;
			double value =(tmpNode.getFaith()+tmpNode.getResource()+tmpNode.getNumInEdges()*10)/node.calculateDistance(tmpNode);
			if(maxEdge<value)
			{
				maxEdge = value;
				retNode = agentList.get(i);
			}
		}
		if(retNode==null)
			System.out.println("NULL!!!");
		return retNode;
	}
	private TSNode getLinkNode(TSNode node) {
		//find the best Parent node for the input node.
		//this simple logic is (faith+resource)/distance.
		//modify your logic here.
		if (!agentList.contains(node))
			return null;
		double maxMarks = -1;
		TSNode retNode = null;
		for (int i = 0; i < agentList.size(); i++) {
			TSNode tmpNode = agentList.get(i);
			if (tmpNode.isBackup()||tmpNode.equals(node)||tmpNode.hasEdgeToOrFrom(node))
				continue;
			double temMarks = (tmpNode.getFaith() + tmpNode.getResource()+tmpNode.getNumInEdges()*10)
					/ tmpNode.calculateDistance(node);
			if (maxMarks < temMarks) {
				maxMarks = temMarks;
				retNode = tmpNode;
			}
		}
		return retNode;
	}
	public void addNode(String lable) {
		if (layoutType == "Random") {
			int x = Random.uniform.nextIntFromTo(0, worldXSize - 20);
			int y = Random.uniform.nextIntFromTo(0, worldYSize - 20);
			int faith = Random.uniform.nextIntFromTo(1, 100);
			int resource = Random.uniform.nextIntFromTo(1, 100);
			TSNode node = new TSNode(x, y, lable);
			node.setFaith(faith);
			node.setResource(resource);
			node.setColor(NodeColor);
			node.setLabelColor(LabelColor);
			agentList.add(node);
		} else if (layoutType == "TreeNode") {
			// Add your special Tree Node here.
		}
	}

	private void addBackupNode(String lable)
	{
		if (layoutType == "Random") {
			int faith = Random.uniform.nextIntFromTo(1, 100);
			int resource = Random.uniform.nextIntFromTo(1, 100);
			TSNode node = new TSNode(worldXSize+20, worldYSize+20, lable);
			node.setFaith(faith);
			node.setResource(resource);
			node.setColor(background);
			node.setLabelColor(background);
			node.setBackup(true);
			agentList.add(node);
		} else if (layoutType == "TreeNode") {
			// Add your special Tree Node here.
		}
	}
	public void initialAction() {
		System.out.println("initialAction");
		createInitialLinks();
		surface.updateDisplay();

	}

	public void addResourceAndFaith() {
		//randomly add faith and resource for one node.
		if (agentList.size() <= 0)
			return;
		int index = Random.uniform.nextIntFromTo(0, agentList.size() - 1);
		TSNode node = agentList.get(index);
		int faith = Random.uniform.nextIntFromTo(0, 20);
		int resource = Random.uniform.nextIntFromTo(0, 20);
		if(node.isBackup())
			return;
		node.setFaith(faith + node.getFaith());
		node.setResource(resource + node.getResource());
		System.out.println("Add faith "+faith+" and resource "+resource+" to node "+node.getLable());
	}

	public void randomCreateNode()
	{
		int rd = Random.uniform.nextIntFromTo(0, 100);
		if(rd<10)
		{
			String ret = getBackupNode();
			if(ret!="")
				System.out.println("New Node "+ret+" Created");
		}
	}
	private String getBackupNode()
	{
		for(int i=agentList.size()-1;i>=0;i--)
		{
			if(agentList.get(i).isBackup())
			{
				TSNode node = agentList.get(i);
				int x = Random.uniform.nextIntFromTo(0, worldXSize - 20);
				int y = Random.uniform.nextIntFromTo(0, worldYSize - 20);
				node.setX(x);
				node.setY(y);
				node.setColor(Color.green);
				node.setLabelColor(LabelColor);
				node.setBackup(false);
				return node.getLable();
			}
		}
		return "";
	}
	public void mainAction() throws Exception{
		//this method will be called on every tick count.
		//add your custom requirement here.
		//now will remove one node every ten steps,
		//modify the input args for customize.	
		//System.out.println("mainAction");
		//removeTopNodeAt(10);
		System.out.println(this.getTickCount()+"-------------------------------------");
		
		calDegreeDistribution();
		calAverageShortestPaths();
		calClusteringCoefficient();
		
		addResourceAndFaith();
		
		randomCreateNode();
		
		randomRemoveNode();
		
		clearLinks();
		
		refreshLinks();
		
		surface.updateDisplayDirect();
		surface.updateDisplay();
		System.out.println();
		if(agentList.size()==0)
			this.stop();
	}
	
	public void calDegreeDistribution()
	{
		if(!(agentList.size()>0))
			return;
		int[] d=new int[6];
		double totalNum=0;
		//output degree---------------
		for(int i = 0;i<agentList.size();i++)
		{
			TSNode tmpNode = agentList.get(i);
			if(tmpNode.isBackup())
				continue;
			totalNum++;
			ArrayList<TSNode> totalNodes = new ArrayList<TSNode>();
			ArrayList<TSNode> fromNodes = tmpNode.getFromNodes();
			ArrayList<TSNode> toNodes = tmpNode.getToNodes();
			totalNodes.addAll(fromNodes);
			for(int index = 0;index<toNodes.size();index++)
			{
				if(!totalNodes.contains(toNodes.get(index)))
					totalNodes.add(toNodes.get(index));
			}
			String out = tmpNode.getLable()+" "+totalNodes.size();
			writeString(fileDirectory+"degreeDistribution.txt",out+"\r\n");
			
			//calculate Distribution
			switch(totalNodes.size())
			{
			case 1:
				d[0]++;
				break;
			case 2:
				d[1]++;
				break;
			case 3:
				d[2]++;
				break;
			case 4:
				d[3]++;
				break;
			case 5:
				d[4]++;
				break;
			case 6:
				d[5]++;
				break;
			}
		}
		writeString(fileDirectory+"degreeDistribution.txt","\r\n");
		//--------------------------------------------------------
		//output Distribution-----------------------------------
		for(int i = 0;i<6;i++)
		{
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append((int)this.getTickCount()-1);
			stringBuilder.append(" ");
			stringBuilder.append((double)d[i]/totalNum);
			writeString(fileDirectory+"d"+(i+1)+".txt",stringBuilder.toString()+"\r\n");
		}
		
		
	}
	
	public void calAverageShortestPaths() throws Exception
	{
		int totalNum=0;
		int totalPath=0;
		if(!(agentList.size()>0))
			return;
		for(int i =0;i<agentList.size();i++)
		{
			if(agentList.get(i).isBackup())
				continue;
			totalNum++;
			TSNode tmpNode = agentList.get(i);
			ArrayList<TSNode> U = new ArrayList<TSNode>();
			ArrayList<TSNode> S = new ArrayList<TSNode>();
			for(int t=0;t<agentList.size();t++)
			{
				if(!agentList.get(t).isBackup())
					U.add(agentList.get(t));
			}
			Map<String, Integer> resultMap = new HashMap<String, Integer>();
			resultMap.put(tmpNode.getLable(), 0);
			S.add(tmpNode);
			U.remove(tmpNode);
			while(!U.isEmpty())
			{
				int min = Integer.MAX_VALUE;
				TSNode nextNode = null;
				for(int j=0;j<S.size();j++)
				{
					ArrayList<TSNode> tmpSet = new ArrayList<TSNode>();
					ArrayList<TSNode>  fromNodes = S.get(j).getFromNodes();
					tmpSet.addAll(fromNodes);
					ArrayList<TSNode>  toNodes = S.get(j).getToNodes();
					for(int index = 0;index<toNodes.size();index++)
					{
						if(!tmpSet.contains(toNodes.get(index)))
							tmpSet.add(toNodes.get(index));
					}
					tmpSet.removeAll(S);
					for(int k=0;k<tmpSet.size();k++)
					{
						
						int distance = 1+resultMap.get(S.get(j).getLable());
						//System.out.println("cal distance "+S.get(j).getLable()+" to "+ tmpSet.get(k).getLable()+" is "+1);
						//System.out.println("0 to "+S.get(j).getLable()+" is "+resultMap.get(S.get(j).getLable()));
						//System.out.println("Then 0 to "+tmpSet.get(k).getLable()+" distance is "+distance);
						if(min>distance)
						{
							min = distance;
							nextNode = tmpSet.get(k);
						}
					}
				}
				if(nextNode!=null)
				{
					S.add(nextNode);
					U.remove(nextNode);
					resultMap.put(nextNode.getLable(), min);
					//System.out.println("0 to "+nextNode.getLable()+" is "+min);
					//System.out.println("add"+nextNode.getLable());
				}
				else
				{
					throw new Exception("Node must link with other nodes!");
				}
			}
			//for(int z=0;z<agentList.size();z++)
			//{
			//	if(!agentList.get(z).isBackup())
			//		System.out.println("Distance from Node "+tmpNode.getLable()+" to Node "+agentList.get(z).getLable()+" is "+resultMap.get(agentList.get(z).getLable()));
			//}
			for(int cnt=i;cnt<agentList.size();cnt++)
			{
				if(agentList.get(cnt).isBackup())
					continue;
				totalPath+=resultMap.get(agentList.get(cnt).getLable());
			}
		}
		double total=totalNum*(totalNum-1)/2;
		String out = (int)(this.getTickCount()-1)+" "+String.valueOf((double)totalPath/total);
		writeString(fileDirectory+"averageShortestPaths.txt",out+"\r\n");
	}
	
	public void calClusteringCoefficient()
	{
		if(!(agentList.size()>0))
			return;
		double totalCC = 0;
		int totalNum=0;
		for(int i = 0;i<agentList.size();i++)
		{
			TSNode tmpNode = agentList.get(i);
			
			double sigleCC = 0;
			if(tmpNode.isBackup())
				continue;
			totalNum++;
			ArrayList<TSNode> totalNodes = new ArrayList<TSNode>();
			ArrayList<TSNode> fromNodes = tmpNode.getFromNodes();
			ArrayList<TSNode> toNodes = tmpNode.getToNodes();
			totalNodes.addAll(fromNodes);
			for(int index = 0;index<toNodes.size();index++)
			{
				if(!totalNodes.contains(toNodes.get(index)))
					totalNodes.add(toNodes.get(index));
			}
			for(int j=0;j<totalNodes.size();j++)
			{
				for(int k = j+1;k<totalNodes.size();k++)
				{
					if(totalNodes.get(j).hasEdgeToOrFrom(totalNodes.get(k)))
					{
						sigleCC++;
					}
				}
			}
			if(totalNodes.size()>1)
				sigleCC = sigleCC/(totalNodes.size()*(totalNodes.size()-1));
			totalCC+=sigleCC;
			String out = tmpNode.getLable()+" "+sigleCC;
			writeString(fileDirectory+"clusteringCoefficient.txt",out+"\r\n");
		}
		writeString(fileDirectory+"clusteringCoefficient.txt","\r\n");
		String out = this.getTickCount()-1+" "+totalCC/totalNum;
		writeString(fileDirectory+"clusteringCoefficientAVG.txt",out+"\r\n");
		
	}
	private void randomRemoveNode() {
		
		int rd = Random.uniform.nextIntFromTo(0, 100);
		if(rd<10)
		{
			int index = Random.uniform.nextIntFromTo(0, agentList.size()-1);
			if(agentList.size()>0)
			{
				System.out.println("Node "+agentList.get(index).getLable()+" dead");
				removeNode(agentList.get(index));
			}
		}
		
	}

	private void writeString(String filePath,String content)
	{
		try
		{
		File directory = new File(fileDirectory);
		if(!directory.exists())
		{
			directory.mkdir();
		}
			
        File file = new File(filePath);
        if(!file.exists())
            file.createNewFile();
        FileOutputStream out = new FileOutputStream(filePath,true);
        byte[] cbyte = content.getBytes();
        out.write(cbyte);
        out.close();
		}
		catch(Exception ex)
		{
			System.out.println("Failed write to "+filePath);
		}
	}
	
	public String[] getInitParam() {
		String[] params = { "numNodes", "worldXSize", "worldYSize",
				"LayoutType","BackgroundColor","EdgeColor","NodeColor","LabelColor" };
		return params;
	}
	
	public void setup(){
		Random.createUniform();
		if (surface != null)
			surface.dispose();
		surface = null;
		schedule = null;
		agentList.clear();
		System.gc();

		surface = new DisplaySurface(this, "TSModel Display");
		registerDisplaySurface("Main Display", surface);
		schedule = new Schedule();
		agentList = new ArrayList<TSNode>();
		
		treeCount = 0;
	}

	public void begin() {
		buildModel();
		buildDisplay();
		buildSchedule();
		surface.display();
	}

	// builds the model
	public void buildModel() {
		System.out.println("buildModel");
		for (int i = 0; i < numNodes; i++) {
			addNode(String.valueOf(i));
		}
		backupNode = (int) (numNodes*1.2);
		for(int i=numNodes;i<backupNode;i++)
		{
			addBackupNode(String.valueOf(i));
		}
	}
	
	public void buildDisplay() {
		System.out.println("buildDisplay");
		Network2DDisplay display = new Network2DDisplay(agentList, worldXSize,
				worldYSize);
		surface.addDisplayableProbeable(display, "TS Display");
		surface.addZoomable(display);
		surface.setBackground(background);
		addSimEventListener(surface);
	}

	private void buildSchedule() {
		System.out.println("buildSchedule");
		initialAction = schedule.scheduleActionAt(1, this, "initialAction");
		schedule.scheduleActionAt(initialSteps, this, "removeInitialAction",
				Schedule.LAST);
		schedule.scheduleActionBeginning(initialSteps + 1, this, "mainAction");
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public String getName() {
		return "TSModel";
	}
	

	public String getLayoutType() {
		return layoutType;
	}

	public void setLayoutType(String type) {
		layoutType = type;
	}

	public void setNumNodes(int n) {
		numNodes = n;
	}

	public int getNumNodes() {
		return numNodes;
	}

	public int getWorldXSize() {
		return worldXSize;
	}

	public void setWorldXSize(int size) {
		worldXSize = size;
	}

	public int getWorldYSize() {
		return worldYSize;
	}

	public void setWorldYSize(int size) {
		worldYSize = size;
	}

	public static void main(String[] args) {
		uchicago.src.sim.engine.SimInit init = new uchicago.src.sim.engine.SimInit();
		TSModel model = new TSModel();
		init.loadModel(model, null, false);
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
		switch (backgroundColor)
		{
		case"Black":
			this.background = Color.black;
			break;
		case"White":
			this.background = Color.white;
			break;
		}
	}

	public String getEdgeColor() {
		return edgeColor;
	}

	public void setEdgeColor(String edgeColor) {
		this.edgeColor = edgeColor;
		switch (edgeColor)
		{
		case"Red":
			this.linkColor = Color.red;
			break;
		case"White":
			this.linkColor = Color.white;
			break;
		case"Yellow":
			this.linkColor = Color.yellow;
			break;
		case"Blue":
			this.linkColor = Color.blue;
			break;
		case"Green":
			this.linkColor = Color.green;
			break;
		case"Black":
			this.linkColor = Color.black;
			break;
		case"white":
			this.linkColor = Color.white;
			break;
		}
	}

	public String getNodeColor() {
		return nodeColor;
	}

	public void setNodeColor(String nodeColor) {
		this.nodeColor = nodeColor;
		switch (nodeColor)
		{
		case"Red":
			this.NodeColor = Color.red;
			break;
		case"White":
			this.NodeColor = Color.white;
			break;
		case"Yellow":
			this.NodeColor = Color.yellow;
			break;
		case"Blue":
			this.NodeColor = Color.blue;
			break;
		case"Green":
			this.NodeColor = Color.green;
			break;
		case"Black":
			this.NodeColor = Color.black;
			break;
		case"white":
			this.NodeColor = Color.white;
			break;
		}
	}

	public String getLabelColor() {
		return labelColor;
	}

	public void setLabelColor(String labelColor) {
		this.labelColor = labelColor;
		switch (labelColor)
		{
		case"Red":
			this.LabelColor = Color.red;
			break;
		case"White":
			this.LabelColor = Color.white;
			break;
		case"Yellow":
			this.LabelColor = Color.yellow;
			break;
		case"Blue":
			this.LabelColor = Color.blue;
			break;
		case"Green":
			this.LabelColor = Color.green;
			break;
		case"Black":
			this.LabelColor = Color.black;
			break;
		case"white":
			this.LabelColor = Color.white;
			break;
		}
	}
}
