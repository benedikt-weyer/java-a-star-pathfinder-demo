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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class App extends Application{

	// Set basic JavaFX GUI parameters
	final int WINDOW_WIDTH = 1600;
	final int WINDOW_HEIGHT = 800;
	final int TILE_SIZE = 20; // Assuming tileSize is 20, replace with actual value


	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		Group g = new Group();
		Scene scene = new Scene(g);
		stage.setScene(scene);
		
		stage.setWidth(WINDOW_WIDTH);
		stage.setHeight(WINDOW_HEIGHT);
		stage.setResizable(false);
		stage.show();
		
		
		//Canvas
		Canvas c = new Canvas(stage.getWidth(), stage.getHeight());
		g.getChildren().add(c);
		GraphicsContext gc = c.getGraphicsContext2D();
		

		//generate node-list
		Node[][] nodeMatrix = generateNodes2D(WINDOW_WIDTH/TILE_SIZE, WINDOW_HEIGHT/TILE_SIZE);

		//specify start & end node
		int startNodeX=4, startNodeY=4;
		int endNodeX = 60, endNodeY = 36;

		Node startNode = nodeMatrix[startNodeX][startNodeY];
		Node endNode = nodeMatrix[endNodeX][endNodeY];

		//calculate H-Costs
		calculateHCostsNodes2D(nodeMatrix, endNodeX, endNodeY);


		//create AStarSearch object
		AStarPathfinder aStarSearch = new AStarPathfinder(startNode, endNode);
		

		//Create and start render loop
		new AnimationTimer() {
			@Override
			public void handle(long time) {
				//render
				render(nodeMatrix, gc, TILE_SIZE, WINDOW_WIDTH, WINDOW_HEIGHT, aStarSearch.getOpenSet(), aStarSearch.getClosedSet(), startNode, endNode);
			}
		}.start();
		

		

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

						//render(nodeMatrix, gc, tileSize, windwoWidth, windowHeight, aStarSearch.getOpenSet(), aStarSearch.getClosedSet());
					}
				}
			}
		});
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



	private void render(Node[][] nodeMatrix, GraphicsContext gc, int tileSize, int windwoWidth, int windowHeight, Set<Node> openList, Set<Node> closedList, Node startNode, Node endNode){
		gc.clearRect(0, 0, 1600, 800);

		for(int x=0; x<nodeMatrix.length; x++) {
			for(int y=0; y<nodeMatrix[0].length; y++) {

				Node node = nodeMatrix[x][y];

				

				if(node.isPassable()){
					gc.setFill(Color.WHITE);
				}

				if(!node.isPassable()){
					gc.setFill(Color.BLACK);
				}

				

				if(openList != null && openList.contains(node)){
					gc.setFill(Color.YELLOW);
				}

				if(closedList != null && closedList.contains(node)){
					gc.setFill(Color.ORANGE);
				}

				if(node.isTheWay()){
					gc.setFill(Color.GREEN);
				}

				if(node.equals(startNode)){
					gc.setFill(Color.RED);
				}

				if(node.equals(endNode)){
					gc.setFill(Color.PINK);
				}

				
				
				gc.fillRect(x*tileSize, y*tileSize, tileSize, tileSize);
				gc.strokeRect(x*tileSize, y*tileSize, tileSize, tileSize);
					
			}
		}

	}

}
