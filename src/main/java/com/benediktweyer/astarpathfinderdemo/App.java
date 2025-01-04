package com.benediktweyer.astarpathfinderdemo;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import io.github.benediktweyer.astarpathfinder.AStarPathfinder;
import io.github.benediktweyer.astarpathfinder.Node;
import io.github.benediktweyer.astarpathfinder.NodeRelation;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
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
		

		EventHandler<MouseEvent> mousEventHandler = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				Point tilePosition = windowToNodePosition((int) e.getX(), (int) e.getY(), TILE_SIZE);
				
				Node selectedNode = nodeMatrix[tilePosition.x][tilePosition.y];
				
				if(e.getButton() == MouseButton.PRIMARY) {
					if(selectedNode.isPassable()){
						selectedNode.setPassable(false);
					}
				}else if(e.getButton() == MouseButton.SECONDARY) {
					if(!selectedNode.isPassable()){
						selectedNode.setPassable(true);
					}
				}

				render(nodeMatrix, graphicsContext, TILE_SIZE, WINDOW_WIDTH, WINDOW_HEIGHT, aStarSearch.getOpenSet(), aStarSearch.getClosedSet(), startNode, endNode);

			}
		};
		
		scene.setOnMousePressed(mousEventHandler);
		scene.setOnMouseDragged(mousEventHandler);

		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent e) {
				if(e.getEventType() == KeyEvent.KEY_PRESSED) {
					if(e.getCode() == KeyCode.ENTER){
						aStarSearch.calculate();

						render(nodeMatrix, graphicsContext, TILE_SIZE, WINDOW_WIDTH, WINDOW_HEIGHT, aStarSearch.getOpenSet(), aStarSearch.getClosedSet(), startNode, endNode);
					}
				}
			}
		});


		render(nodeMatrix, graphicsContext, TILE_SIZE, WINDOW_WIDTH, WINDOW_HEIGHT, aStarSearch.getOpenSet(), aStarSearch.getClosedSet(), startNode, endNode);
	}


	private Node[][] generateNodes2D(int xSize, int ySize){

		Node[][] nodeMatrix = new Node[xSize][ySize];

		//create Nodes
		for(int x=0; x<xSize; x++) {
			for(int y=0; y<ySize; y++) {				
				nodeMatrix[x][y] = new Node();
			}
		}

		//create and add node relations
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

	
	private void calculateHCostsNodes2D(Node[][] nodeMatrix, int endNodeX, int endNodeY){

		for(int x=0; x<nodeMatrix.length; x++) {
			for(int y=0; y<nodeMatrix[0].length; y++) {
				double hCosts = (Math.abs(endNodeX-x) + Math.abs(endNodeY-y)) / 2d;
				nodeMatrix[x][y].setHCost(hCosts);
			}
		}
	}


	private Point windowToNodePosition(int xWindow, int yWindow, int tileSize){
		return new Point((int) Math.floor(xWindow / tileSize), (int) Math.floor(yWindow / tileSize));
	}



	private void render(Node[][] nodeMatrix, GraphicsContext graphicsContext, int tileSize, int windwoWidth, int windowHeight, Set<Node> openList, Set<Node> closedList, Node startNode, Node endNode){
		graphicsContext.clearRect(0, 0, 1600, 800);

		for(int x=0; x<nodeMatrix.length; x++) {
			for(int y=0; y<nodeMatrix[0].length; y++) {

				Node node = nodeMatrix[x][y];

				

				if(node.isPassable()){
					graphicsContext.setFill(Color.WHITE);
				}

				if(!node.isPassable()){
					graphicsContext.setFill(Color.BLACK);
				}

				

				if(openList != null && openList.contains(node)){
					graphicsContext.setFill(Color.YELLOW);
				}

				if(closedList != null && closedList.contains(node)){
					graphicsContext.setFill(Color.ORANGE);
				}

				if(node.isTheWay()){
					graphicsContext.setFill(Color.GREEN);
				}

				if(node.equals(startNode)){
					graphicsContext.setFill(Color.RED);
				}

				if(node.equals(endNode)){
					graphicsContext.setFill(Color.PINK);
				}

				
				
				graphicsContext.fillRect(x*tileSize, y*tileSize, tileSize, tileSize);
				graphicsContext.strokeRect(x*tileSize, y*tileSize, tileSize, tileSize);
					
			}
		}

	}

}
