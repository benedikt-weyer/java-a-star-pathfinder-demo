package com.benediktweyer.astarpathfinder;

import java.util.ArrayList;
import java.util.List;

public class Node {
	private int parentID;
	
	private double GCost=0;
	private double FCost;
	private double HCost;
	
	private NodeType nodeType = NodeType.PASSABLE;
	
	private boolean theWay=false;
	private boolean passable=true;

	private List<NodeRelation> nodeRelations = new ArrayList<>();

	public Node() {

	}

	public int getParentID() {
		return parentID;
	}

	public void setParent(int parentID) {
		this.parentID = parentID;
	}

	public double getGCost() {
		return GCost;
	}

	public void setGCost(double gCost) {
		GCost = gCost;
	}

	public double getFCost() {
		return FCost;
	}

	public void setFCost(double fCost) {
		FCost = fCost;
	}

	public double getHCost() {
		return HCost;
	}

	public void setHCost(double hCost) {
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

	public List<NodeRelation> getNodeRelations() {
		return nodeRelations;
	}

	@Override
	public String toString() {
		return "Node [GCost=" + GCost + ", FCost=" + FCost + ", HCost=" + HCost + ", nodeType=" + nodeType + ", theWay="
				+ theWay + ", passable=" + passable + ", nodeRelations=" + nodeRelations + "]";
	}
	
	
	
}
