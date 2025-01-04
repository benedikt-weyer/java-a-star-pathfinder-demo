package com.benediktweyer.astarpathfinderdemo;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.github.benediktweyer.astarpathfinder.AStarPathfinder;
import io.github.benediktweyer.astarpathfinder.Node;
import io.github.benediktweyer.astarpathfinder.NodeRelation;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class App extends Application{

	// Set basic JavaFX GUI parameters
	final int WINDOW_WIDTH = 1600;
	final int WINDOW_HEIGHT = 800;
	final int TILE_SIZE = 20;


	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		// Create root node (StackPane)
        StackPane root = new StackPane();

		// Create a scene with the specified width and height
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		// Stage configuration
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
		
		
		// Create a canvas with the same width and height as the scene
		Canvas canvas = new Canvas(scene.getWidth(), scene.getHeight());
		root.getChildren().add(canvas);
		// Get the graphics context of the canvas
		GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
		

		// Generate a 2D array of nodes based on the canvas size and tile size
		Node[][] nodeMatrix = generateNodes2D((int) Math.floor(canvas.getWidth()/TILE_SIZE), (int) Math.floor(canvas.getHeight()/TILE_SIZE));

		// Specify the start and end nodes
		int startNodeX=4, startNodeY=4;
		int endNodeX = 60, endNodeY = 36;

		Node startNode = nodeMatrix[startNodeX][startNodeY];
		Node endNode = nodeMatrix[endNodeX][endNodeY];

		// Calculate the H-Costs for the nodes
		calculateHCostsNodes2D(nodeMatrix, endNodeX, endNodeY);


		// Create an AStarPathfinder object with the start and end nodes
		AStarPathfinder aStarSearch = new AStarPathfinder(startNode, endNode);
		

		// Create new mouse event handler
		EventHandler<MouseEvent> mousEventHandler = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				// get position of the tile in the node matrix based on the mouse position
				Point tilePosition = windowToNodePosition((int) e.getX(), (int) e.getY(), TILE_SIZE);

				// get the node with the tile position
				Node selectedNode = nodeMatrix[tilePosition.x][tilePosition.y];
				
				// set the node to passable or not passable based on the mouse button
				if(e.getButton() == MouseButton.PRIMARY) {
					if(selectedNode.isPassable()){
						selectedNode.setPassable(false);
					}
				}else if(e.getButton() == MouseButton.SECONDARY) {
					if(!selectedNode.isPassable()){
						selectedNode.setPassable(true);
					}
				}

				// rerender the nodes
				render(nodeMatrix, graphicsContext, TILE_SIZE, WINDOW_WIDTH, WINDOW_HEIGHT, aStarSearch.getOpenSet(), aStarSearch.getClosedSet(), startNode, endNode);
			}
		};
		
		// Set the mouse event handlers for the scene
		scene.setOnMousePressed(mousEventHandler);
		scene.setOnMouseDragged(mousEventHandler);

		
		// Set the key event handler for the scene
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent e) {
				// if the enter key is pressed, calculate the path
				if(e.getEventType() == KeyEvent.KEY_PRESSED) {
					if(e.getCode() == KeyCode.ENTER){
						aStarSearch.calculate();

						// rerender the nodes
						render(nodeMatrix, graphicsContext, TILE_SIZE, WINDOW_WIDTH, WINDOW_HEIGHT, aStarSearch.getOpenSet(), aStarSearch.getClosedSet(), startNode, endNode);
					}
				}
			}
		});


		// initial render
		render(nodeMatrix, graphicsContext, TILE_SIZE, WINDOW_WIDTH, WINDOW_HEIGHT, aStarSearch.getOpenSet(), aStarSearch.getClosedSet(), startNode, endNode);
	}

	/**
	 * Generates a 2D array of nodes with the specified x and y size
	 * @param xSize
	 * @param ySize
	 * @return a 2D array of nodes
	 */
	private Node[][] generateNodes2D(int xSize, int ySize){
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
	private void calculateHCostsNodes2D(Node[][] nodeMatrix, int endNodeX, int endNodeY){
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

	/**
	 * Converts the window position to a node position in a 2D array of nodes
	 * @param xWindow
	 * @param yWindow
	 * @param tileSize
	 * @return a point with the node position
	 */
	private Point windowToNodePosition(int xWindow, int yWindow, int tileSize){
		return new Point((int) Math.floor(xWindow / tileSize), (int) Math.floor(yWindow / tileSize));
	}


	/**
	 * Renders the nodes on the canvas
	 * @param nodeMatrix
	 * @param graphicsContext
	 * @param tileSize
	 * @param windwoWidth
	 * @param windowHeight
	 * @param openList
	 * @param closedList
	 * @param startNode
	 * @param endNode
	 */
	private void render(Node[][] nodeMatrix, GraphicsContext graphicsContext, int tileSize, int windwoWidth, int windowHeight, Set<Node> openList, Set<Node> closedList, Node startNode, Node endNode){
		
		// clear the canvas
		graphicsContext.clearRect(0, 0, 1600, 800);

		// loop through the node matrix and render the nodes
		for(int x=0; x<nodeMatrix.length; x++) {
			for(int y=0; y<nodeMatrix[0].length; y++) {

				// get the node at the position
				Node node = nodeMatrix[x][y];

				// if the node is passable, set the fill color to white
				if(node.isPassable()){
					graphicsContext.setFill(Color.WHITE);
				}
				// if the node is not passable, set the fill color to black
				if(!node.isPassable()){
					graphicsContext.setFill(Color.BLACK);
				}

				
				// if the node is in the open list, set the fill color to yellow
				if(openList != null && openList.contains(node)){
					graphicsContext.setFill(Color.YELLOW);
				}
				// if the node is in the closed list, set the fill color to orange
				if(closedList != null && closedList.contains(node)){
					graphicsContext.setFill(Color.ORANGE);
				}
				// if the node is the way, set the fill color to green
				if(node.isTheWay()){
					graphicsContext.setFill(Color.GREEN);
				}
				// if the node is the start node, set the fill color to red
				if(node.equals(startNode)){
					graphicsContext.setFill(Color.RED);
				}
				// if the node is the end node, set the fill color to pink
				if(node.equals(endNode)){
					graphicsContext.setFill(Color.PINK);
				}

				
				// draw the node
				graphicsContext.fillRect(x*tileSize, y*tileSize, tileSize, tileSize);
				graphicsContext.strokeRect(x*tileSize, y*tileSize, tileSize, tileSize);
			}
		}

	}

}
