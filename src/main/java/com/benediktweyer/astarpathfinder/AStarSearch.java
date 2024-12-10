package com.benediktweyer.astarpathfinder;

import java.util.ArrayList;
import java.util.Comparator;
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

        this.currentNode = startNode;
    }


    private Node currentNode = null;
    //private double currentNodeGCosts = 0;
    //private double currentNodeFCosts = 0;

    public boolean calculate(){

        //Node currentNode = startNode;
        //double currentTotalGCosts = 0;

        

        for(NodeRelation nodeRelation : currentNode.getNodeRelations()){
            Node neighbourNode = nodeRelation.getTargetNode();

            double gCosts = currentNode.getGCost() + nodeRelation.getTravelCosts();
            double fCosts = gCosts + nodeRelation.getTargetNode().getHCost();

            neighbourNode.setGCost(gCosts);
            neighbourNode.setFCost(fCosts);

        }

        Node nextNode = currentNode.getNodeRelations().stream()
            .map(nodeRelation -> nodeRelation.getTargetNode())
            .min(Comparator.comparingDouble(Node::getFCost))
            .orElse(null);

        System.out.println(nextNode);

        if(nextNode != null){
            nextNode.setTheWay(true);
            currentNode = nextNode;

        }
        
        


        return true;
    }
    
}
