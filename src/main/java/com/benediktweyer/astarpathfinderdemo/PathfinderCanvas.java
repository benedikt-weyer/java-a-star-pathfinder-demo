package com.benediktweyer.astarpathfinderdemo;

import java.util.Set;

import io.github.benediktweyer.astarpathfinder.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PathfinderCanvas extends Canvas {
    private final GraphicsContext gc;
    
    public PathfinderCanvas(int width, int height) {
        super(width, height);
        this.gc = super.getGraphicsContext2D();
    }

    /**
	 * Renders the nodes on the canvas
	 * @param nodeMatrix
	 * @param tileSize
	 * @param windwoWidth
	 * @param windowHeight
	 * @param openList
	 * @param closedList
	 * @param startNode
	 * @param endNode
	 */
	public void render(Node[][] nodeMatrix, int tileSize, int windwoWidth, int windowHeight, Set<Node> openList, Set<Node> closedList, Node startNode, Node endNode){
		
		// clear the canvas
		gc.clearRect(0, 0, super.getWidth(), super.getHeight());

		// loop through the node matrix and render the nodes
		for(int x=0; x<nodeMatrix.length; x++) {
			for(int y=0; y<nodeMatrix[0].length; y++) {

				// get the node at the position
				Node node = nodeMatrix[x][y];

				// if the node is passable, set the fill color to white
				if(node.isPassable()){
					gc.setFill(Color.WHITE);
				}
				// if the node is not passable, set the fill color to black
				if(!node.isPassable()){
					gc.setFill(Color.BLACK);
				}

				
				// if the node is in the open list, set the fill color to yellow
				if(openList != null && openList.contains(node)){
					gc.setFill(Color.YELLOW);
				}
				// if the node is in the closed list, set the fill color to orange
				if(closedList != null && closedList.contains(node)){
					gc.setFill(Color.ORANGE);
				}
				// if the node is the way, set the fill color to green
				if(node.isTheWay()){
					gc.setFill(Color.GREEN);
				}
				// if the node is the start node, set the fill color to red
				if(node.equals(startNode)){
					gc.setFill(Color.RED);
				}
				// if the node is the end node, set the fill color to pink
				if(node.equals(endNode)){
					gc.setFill(Color.PINK);
				}

				
				// draw the node
				gc.fillRect(x*tileSize, y*tileSize, tileSize, tileSize);
				gc.strokeRect(x*tileSize, y*tileSize, tileSize, tileSize);
			}
		}

	}

}