package com.benediktweyer.astarpathfinder;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	public static List<Node> allList = new ArrayList<Node>();
	public static List<Node> openList = new ArrayList<Node>();
	public static List<Node> closedList = new ArrayList<Node>();
	
	public static int tileSize = 20;
	public static int GCostMultiplier = 1;
	
	public static boolean reset=true;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		//set basic jafafx gui parameters
		int windwoWidth = 1600;
		int windowHeight = 800;

		Group g = new Group();
		Scene scene = new Scene(g);
		stage.setScene(scene);
		
		stage.setWidth(windwoWidth);
		stage.setHeight(windowHeight+40);
		stage.setResizable(false);
		stage.show();
		
		
		//Canvas
		Canvas c = new Canvas(stage.getWidth(), stage.getHeight());
		g.getChildren().add(c);
		GraphicsContext gc = c.getGraphicsContext2D();
		

		//generate node-list
		Node[][] nodeMatrix = generateNodes2D(windwoWidth/tileSize, windowHeight/tileSize);

		//specify start & end node
		int startNodeX=4, startNodeY=4;
		int endNodeX = 60, endNodeY = 36;

		nodeMatrix[startNodeX][startNodeY].setNodeType(NodeType.START);
		nodeMatrix[endNodeX][endNodeY].setNodeType(NodeType.END);

		//calculate H-Costs
		calculateHCostsNodes2D(nodeMatrix, endNodeX, endNodeY);


		//flaten node matrix
		List<Node> nodeList = Arrays.stream(nodeMatrix)
                .flatMap(Arrays::stream)
                .toList();

		//create AStarSearch object
		AStarSearch aStarSearch = new AStarSearch(nodeList, nodeMatrix[startNodeX][startNodeY], nodeMatrix[endNodeX][endNodeY]);
		

		//Create and start render loop
		new AnimationTimer() {
			@Override
			public void handle(long time) {
				//render
				render(nodeMatrix, gc, tileSize, windwoWidth, windowHeight);
			}
		}.start();
		

		

		EventHandler<MouseEvent> mousEventHandler = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				Point tilePosition = windowToNodePosition((int) e.getX(), (int) e.getY(), tileSize);
				
				Node selectedNode = nodeMatrix[tilePosition.x][tilePosition.y];
				
				if(e.getButton() == MouseButton.PRIMARY) {
					if(selectedNode.getNodeType() == NodeType.PASSABLE){
						selectedNode.setNodeType(NodeType.UNPASSABLE);
					}
				}else if(e.getButton() == MouseButton.SECONDARY) {
					if(selectedNode.getNodeType() == NodeType.UNPASSABLE){
						selectedNode.setNodeType(NodeType.PASSABLE);
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
					}
				}
			}
		});

		
		/* //specify start node
		allList.get(Util.getIndex(4, 4)).setNodeSpecial(1);
		
		//specify end node
		allList.get(Util.getIndex(60, 36)).setNodeSpecial(2);
		
		new AnimationTimer() {
			
			@Override
			public void handle(long time) {
				gc.clearRect(0, 0, 1600, 800);
				
				for(Node t : allList) {
					if(t.getNodeSpecial() != 0) {
						switch (t.getNodeSpecial()) {
							case 1:
								gc.setFill(Color.GREEN);
								break;
							case 2:
								gc.setFill(Color.RED);
								break;
							case 3:
								gc.setFill(Color.PINK);
								break;
						}
						
						if(t.isTheWay()) {
							gc.setFill(Color.MAGENTA);
						}
						
						gc.fillRect(t.getX()*tileSize, t.getY()*tileSize, tileSize, tileSize);
						gc.setFill(Color.BLACK);
					}
					
				}
				
				for(Node t : openList) {
					gc.setFill(Color.YELLOW);
					gc.fillRect(t.getX()*tileSize, t.getY()*tileSize, tileSize, tileSize);
					gc.setFill(Color.BLACK);
					
					
				}
				
				for(Node t : allList) {
					if(!t.isPassable()) {
						gc.fillRect(t.getX()*tileSize, t.getY()*tileSize, tileSize, tileSize);
					}
					
					gc.strokeRect(t.getX()*tileSize, t.getY()*tileSize, tileSize, tileSize);
				}
				
				
			}
		}.start();
		
		
		
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent e) {
				if(e.getEventType() == KeyEvent.KEY_PRESSED) {
					if(e.getCode() == KeyCode.ENTER && reset){
						reset=false;
						
						//pathfinding
						Point startC=new Point(), endC=new Point();
						Node start=null, end=null;
						
						for(Node t : allList) {
							if(t.getNodeSpecial() != 0) {
								switch (t.getNodeSpecial()) {
									case 1:
										startC = new Point(t.getX(), t.getY());
										start=t;
										break;
									case 2:
										endC = new Point(t.getX(), t.getY());
										end=t;
										break;
								}
							}
						}
						
						int parentID = Util.getIndex(startC.x, startC.y);
						closedList.add(start);
						
						
						
						while(!closedList.contains(end)) {
							Node parent = allList.get(parentID);
							
							addToOpenList(Util.getIndex(parent.getX(), parent.getY()-1), parentID);
							addToOpenList(Util.getIndex(parent.getX()+1, parent.getY()-1), parentID);
							addToOpenList(Util.getIndex(parent.getX()+1, parent.getY()), parentID);
							addToOpenList(Util.getIndex(parent.getX()+1, parent.getY()+1), parentID);
							addToOpenList(Util.getIndex(parent.getX(), parent.getY()+1), parentID);
							addToOpenList(Util.getIndex(parent.getX()-1, parent.getY()+1), parentID);
							addToOpenList(Util.getIndex(parent.getX()-1, parent.getY()), parentID);
							addToOpenList(Util.getIndex(parent.getX()-1, parent.getY()-1), parentID);
							
							Node lowestFCostTile = null;
							
							for(Node t : openList) {
								//t.setParent(parentID);
								
								
								if(t.getX() + t.getY() == parent.getX() + parent.getY() + 2 || t.getX() + t.getY() == parent.getX() + parent.getY() -2) {
									if(t.getGCost() == 0) {
										t.setGCost(14*GCostMultiplier + parent.getGCost());
									}
								}else {
									if(t.getGCost() == 0) {
										t.setGCost(10*GCostMultiplier + parent.getGCost());
									}
								}
								
								t.setHCost(Math.abs(t.getX()-endC.x) + Math.abs(t.getY()-endC.y));
								
								t.setFCost(t.getGCost() + t.getHCost());
								
								if(lowestFCostTile == null) {
									lowestFCostTile = t;
								}else if(t.getFCost() <= lowestFCostTile.getFCost()){
									if(t.getFCost() < lowestFCostTile.getFCost()) {
										lowestFCostTile = t;
									}else if(t.getHCost() < lowestFCostTile.getHCost()){
										lowestFCostTile = t;
									}
								}
							}
							//System.out.println(Util.getIndex(lowestFCostTile.getX(), lowestFCostTile.getY()));
							lowestFCostTile.setNodeSpecial(3);
							closedList.add(lowestFCostTile);
							openList.remove(lowestFCostTile);
							parentID = Util.getIndex(lowestFCostTile.getX(), lowestFCostTile.getY());
						}
						
						end.setTheWay(true);
						int wayParentID = end.getParentID();
						
						while(!start.isTheWay()) {
							allList.get(wayParentID).setTheWay(true);
							wayParentID = allList.get(wayParentID).getParentID();
						}
						
					}
					
					if(e.getCode() == KeyCode.DELETE) {
						for(Node t : allList) {
							//reset
							t.setFCost(0);
							t.setGCost(0);
							t.setHCost(0);
							t.setNodeSpecial(0);
							//t.setPassable(true);
							t.setTheWay(false);
						}
						
						openList.clear();
						closedList.clear();
						
						//specify start node
						allList.get(Util.getIndex(4, 4)).setNodeSpecial(1);
						
						//specify end node
						allList.get(Util.getIndex(60, 36)).setNodeSpecial(2);
						
						reset=true;
					}
				}
			}
		});
		
		EventHandler<MouseEvent> eh = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				Point p = new Point((int) e.getX(), (int) e.getY());
				
				//System.out.println((p.x - (p.x % tileSize))/40);
				int index = Util.getIndex((p.x - (p.x % tileSize))/tileSize, (p.y - (p.y % tileSize))/tileSize);
				
				if(index != -1) {
					Node t = allList.get(index);
					
					if(t.getNodeSpecial() == 0) {
						if(e.getButton() == MouseButton.PRIMARY) {
							t.setPassable(false);
						}else if(e.getButton() == MouseButton.SECONDARY) {
							t.setPassable(true);
						}
					}
				}
			}
		};
		
		scene.setOnMousePressed(eh);
		scene.setOnMouseDragged(eh); */
	}
	
	/* private static void addToOpenList(int index, int parentID) {
		if(index != -1) {
			Node t = allList.get(index);
			if(openList.contains(t)) {
				Node pt = allList.get(parentID);
				int i= 10*GCostMultiplier;
				if(pt.getX() + pt.getY() == t.getX() + t.getY() + 2 || pt.getX() + pt.getY() == t.getX() + t.getY() -2) {
					i=14*GCostMultiplier;
				}
				if(pt.getGCost() + i < t.getGCost()) {
					t.setParent(parentID);
					t.setGCost(pt.getGCost() + i);
				}
			}else if(!closedList.contains(t) && t.isPassable()) {
				openList.add(t);
				t.setParent(parentID);
			}
		}
	} */

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


	private void render(Node[][] nodeMatrix, GraphicsContext gc, int tileSize, int windwoWidth, int windowHeight){
		gc.clearRect(0, 0, 1600, 800);

		for(int x=0; x<nodeMatrix.length; x++) {
			for(int y=0; y<nodeMatrix[0].length; y++) {

				Node node = nodeMatrix[x][y];

				switch(node.getNodeType()) {
					case PASSABLE:
						gc.setFill(Color.WHITE);
						break;
					case UNPASSABLE:
						gc.setFill(Color.BLACK);
						break;
					case START:
						gc.setFill(Color.RED);
						break;
					case END:
						gc.setFill(Color.PINK);
						break;
				}

				if(node.isTheWay()){
					gc.setFill(Color.YELLOW);
				}
				
				gc.fillRect(x*tileSize, y*tileSize, tileSize, tileSize);
				gc.strokeRect(x*tileSize, y*tileSize, tileSize, tileSize);
					
			}
		}

	}

}
