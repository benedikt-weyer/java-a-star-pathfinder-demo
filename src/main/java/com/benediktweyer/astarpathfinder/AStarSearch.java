package com.benediktweyer.astarpathfinder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AStarSearch {
    private List<Node> nodes;
    private Node startNode;
    private Node endNode;

    private double hCostMultiplier = 2;

    //private List<Node> allList = new ArrayList<>();
	private Set<Node> openSet = new HashSet<>();
	private Set<Node> closedSet = new HashSet<>();

    public AStarSearch(List<Node> nodes, Node startNode, Node endNode) {
        this.nodes = nodes;
        this.startNode = startNode;
        this.endNode = endNode;

        this.currentNode = startNode;
        this.closedSet.add(startNode);
        this.closedSet.add(endNode);
    }


    private Node currentNode = null;

    public boolean calculate(){

        //move current node to closed list
        openSet.remove(currentNode);
        closedSet.add(currentNode);

        //check neighbour nodes
        for(NodeRelation nodeRelation : currentNode.getNodeRelations()){
            
            Node neighbourNode = nodeRelation.getTargetNode();

            if(!closedSet.contains(neighbourNode) && neighbourNode.getNodeType() == NodeType.PASSABLE){
                double gCosts = currentNode.getGCost() + nodeRelation.getTravelCosts();
                double fCosts = gCosts + nodeRelation.getTargetNode().getHCost() * hCostMultiplier;
    
                //set/update f and g cost and parent node
                if(neighbourNode.getFCost() > fCosts){
                    neighbourNode.setGCost(gCosts);
                    neighbourNode.setFCost(fCosts);
                    neighbourNode.setParent(currentNode);
                }
                
    
                //add neighbour nodes to open list
                openSet.add(neighbourNode);
            }
        }


        Node nextNode = openSet.stream()
            .min(
                Comparator.comparingDouble(Node::getFCost)
                .thenComparing(Node::getHCost)
                )
            .orElse(null);

        System.out.println(String.format("Next Node: %s\n", nextNode));

        if(nextNode != null){
            currentNode = nextNode;

        }
        
        


        return true;
    }

    public Set<Node> getOpenSet() {
        return openSet;
    }

    public Set<Node> getClosedSet() {
        return closedSet;
    }
    

    
}