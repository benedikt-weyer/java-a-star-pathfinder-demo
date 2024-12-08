package com.benediktweyer.astarpathfinder;

import java.util.ArrayList;
import java.util.List;

public class AStarSearch {
    private List<Node> nodes;
    private Node startNode;
    private Node endNode;

    //private List<Node> allList = new ArrayList<>();
	private List<Node> openList = new ArrayList<>();
	private List<Node> closedList = new ArrayList<>();

    public AStarSearch(List<Node> nodes, Node startNode, Node endNode) {
        this.nodes = nodes;
        this.startNode = startNode;
        this.endNode = endNode;
    }


    public boolean calculate(){

        return true;
    }
    
}
