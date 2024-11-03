package com.benediktweyer.astarpathfinder;

public class Tile {
	private int parentID;
	private int x,y;
	
	private int GCost=0;
	private int FCost;
	private int HCost;
	
	private int nodeSpecial=0;
	
	private boolean theWay=false;
	private boolean passable=true;

	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
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

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getNodeSpecial() {
		return nodeSpecial;
	}

	public void setNodeSpecial(int nodeSpecial) {
		this.nodeSpecial = nodeSpecial;
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
	
	
}
