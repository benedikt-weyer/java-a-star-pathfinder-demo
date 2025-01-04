package com.benediktweyer.astarpathfinderdemo;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import io.github.benediktweyer.astarpathfinder.AStarPathfinder;
import io.github.benediktweyer.astarpathfinder.Node;
import io.github.benediktweyer.astarpathfinder.NodeRelation;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
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
		
		
		// Initialize the pathfinder visualization canvas
        // The canvas dimensions match the scene size for full window coverage
		PathfinderCanvas pathfinderCanvas = new PathfinderCanvas((int) scene.getWidth(), (int) scene.getHeight());
		root.getChildren().add(pathfinderCanvas);

		// Generate a 2D array of nodes based on the canvas size and tile size
		Node[][] nodeMatrix = NodeMatrixGenerator.generateNodes2D((int) Math.floor(scene.getWidth()/TILE_SIZE), (int) Math.floor(scene.getHeight()/TILE_SIZE));

		// Specify the start and end nodes
		int startNodeX=4, startNodeY=4;
		int endNodeX = 60, endNodeY = 36;

		Node startNode = nodeMatrix[startNodeX][startNodeY];
		Node endNode = nodeMatrix[endNodeX][endNodeY];

		// Calculate the H-Costs for the nodes
		NodeMatrixGenerator.calculateHCostsNodes2D(nodeMatrix, endNodeX, endNodeY);


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
				pathfinderCanvas.render(nodeMatrix, TILE_SIZE, WINDOW_WIDTH, WINDOW_HEIGHT, aStarSearch.getOpenSet(), aStarSearch.getClosedSet(), startNode, endNode);
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
						pathfinderCanvas.render(nodeMatrix, TILE_SIZE, WINDOW_WIDTH, WINDOW_HEIGHT, aStarSearch.getOpenSet(), aStarSearch.getClosedSet(), startNode, endNode);
					}
				}
			}
		});


		// initial render
		pathfinderCanvas.render(nodeMatrix, TILE_SIZE, WINDOW_WIDTH, WINDOW_HEIGHT, aStarSearch.getOpenSet(), aStarSearch.getClosedSet(), startNode, endNode);
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
	
}
