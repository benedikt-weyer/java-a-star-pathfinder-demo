package com.benediktweyer.astarpathfinder;

public class NodeRelation {
    private Node targetNode;
    private double travelCosts;

    
    public NodeRelation(Node targetNode, double travelCosts) {
        this.targetNode = targetNode;
        this.travelCosts = travelCosts;
    }


    public Node getTargetNode() {
        return targetNode;
    }


    public void setTargetNode(Node targetNode) {
        this.targetNode = targetNode;
    }


    public double getTravelCosts() {
        return travelCosts;
    }


    public void setTravelCosts(double travelCosts) {
        this.travelCosts = travelCosts;
    }


    @Override
    public String toString() {
        return "NodeRelation [targetNode=" + targetNode.getNodeType() + ", travelCosts=" + travelCosts + "]";
    }

    
}
