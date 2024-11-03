package com.benediktweyer.astarpathfinder;

public class Node {
	private int parentID;
	
	private int GCost=0;
	private int FCost;
	private int HCost;
	
	private NodeType nodeType = NodeType.PASSABLE;
	
	private boolean theWay=false;
	private boolean passable=true;

	public Node() {

	}

	public int getParentID() {
		return parentID;
	}

	public void setParent(int parentID) {
		this.parentID = parentID;
	}

	public int getGCost() {
		return GCost;
	}

	public void setGCost(int gCost) {
		GCost = gCost;
	}

	public int getFCost() {
		return FCost;
	}

	public void setFCost(int fCost) {
		FCost = fCost;
	}

	public int getHCost() {
		return HCost;
	}

	public void setHCost(int hCost) {
		HCost = hCost;
	}

	public boolean isTheWay() {
		return theWay;
	}

	public void setTheWay(boolean theWay) {
		this.theWay = theWay;
	}

	public boolean isPassable() {
		return passable;
	}

	public void setPassable(boolean passable) {
		this.passable = passable;
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}
	
	
	
}
