package com.benediktweyer.astarpathfinderdemo;

import java.util.ArrayList;
import java.util.List;

import io.github.benediktweyer.astarpathfinder.Node;
import io.github.benediktweyer.astarpathfinder.NodeRelation;

public class NodeMatrixGenerator {
    private static final int TILE_SIZE = 20;

    /**
	 * Generates a 2D array of nodes with the specified x and y size
	 * @param xSize
	 * @param ySize
	 * @return a 2D array of nodes
	 */
	public Node[][] generateNodes2D(int xSize, int ySize){
		// create node matrix
		Node[][] nodeMatrix = new Node[xSize][ySize];

		// create and add nodes
		for(int x=0; x<xSize; x++) {
			for(int y=0; y<ySize; y++) {				
				nodeMatrix[x][y] = new Node();
			}
		}

		// create and add node relations
		for(int x=0; x<xSize; x++) {
			for(int y=0; y<ySize; y++) {
				List<NodeRelation> nodeRelations = new ArrayList<>();

				// up
				if(y>0){
					NodeRelation upNodeRelation = new NodeRelation(nodeMatrix[x][y-1], 1);
					nodeRelations.add(upNodeRelation);
				}
				// up right
				if(y>0 && x < xSize - 1){
					NodeRelation upRightNodeRelation = new NodeRelation(nodeMatrix[x+1][y-1], 1.4);
					nodeRelations.add(upRightNodeRelation);
				}
				// right
				if(x < xSize - 1){
					NodeRelation rightNodeRelation = new NodeRelation(nodeMatrix[x+1][y], 1);
					nodeRelations.add(rightNodeRelation);
				}
				// down right
				if(y < ySize - 1 && x < xSize - 1){
					NodeRelation downRightNodeRelation = new NodeRelation(nodeMatrix[x+1][y+1], 1.4);
					nodeRelations.add(downRightNodeRelation);
				}
				// down
				if(y < ySize - 1){
					NodeRelation downNodeRelation = new NodeRelation(nodeMatrix[x][y+1], 1);
					nodeRelations.add(downNodeRelation);
				}
				// down left
				if(y < ySize - 1 && x>0){
					NodeRelation downLeftNodeRelation = new NodeRelation(nodeMatrix[x-1][y+1], 1.4);
					nodeRelations.add(downLeftNodeRelation);
				}
				// left
				if(x>0){
					NodeRelation leftNodeRelation = new NodeRelation(nodeMatrix[x-1][y], 1);
					nodeRelations.add(leftNodeRelation);
				}
				// up left
				if(y>0 && x>0){
					NodeRelation upLeftNodeRelation = new NodeRelation(nodeMatrix[x-1][y-1], 1.4);
					nodeRelations.add(upLeftNodeRelation);
				}

				//set node relation
				nodeMatrix[x][y].getNodeRelations().addAll(nodeRelations);
			}
		}
		
		return nodeMatrix;
	}

	/**
	 * Calculates the H-Costs for the nodes based on the end node position in a 2D array of nodes.
	 * The H-Costs are calculated by the Manhattan distance between the node and the end node.
	 * The H-Costs are stored in the nodes.
	 * @param nodeMatrix
	 * @param endNodeX
	 * @param endNodeY
	 */
	public void calculateHCostsNodes2D(Node[][] nodeMatrix, int endNodeX, int endNodeY){
		// loop through the node matrix and calculate the H-Costs
		for(int x=0; x<nodeMatrix.length; x++) {
			for(int y=0; y<nodeMatrix[0].length; y++) {
				// calculate H-Costs
				double hCosts = (Math.abs(endNodeX-x) + Math.abs(endNodeY-y)) / 2d;
				// set H-Costs
				nodeMatrix[x][y].setHCost(hCosts);
			}
		}
	}
}