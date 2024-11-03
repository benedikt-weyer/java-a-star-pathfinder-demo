package com.benediktweyer.astarpathfinder;

import java.awt.Point;
import java.util.ArrayList;
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
	public static List<Tile> allList = new ArrayList<Tile>();
	public static List<Tile> openList = new ArrayList<Tile>();
	public static List<Tile> closedList = new ArrayList<Tile>();
	
	public static int tileSize = 20;
	public static int GCostMultiplier = 1;
	
	public static boolean reset=true;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Group g = new Group();
		Scene scene = new Scene(g);
		stage.setScene(scene);
		
		stage.setWidth(1600+6);
		stage.setHeight(800+29);
		stage.setResizable(false);
		stage.show();
		
		
		//Canvas
		Canvas c = new Canvas(stage.getWidth(), stage.getHeight());
		g.getChildren().add(c);
		GraphicsContext gc = c.getGraphicsContext2D();
		
		//gc.fillRect(0, 0, 1600, 800);
		
		
		for(int x=0; x<1600; x+=tileSize) {
			for(int y=0; y<800; y+=tileSize) {
				gc.strokeRect(x, y, tileSize, tileSize);
				
				allList.add(new Tile(x/tileSize, y/tileSize));
			}
		}
		
		//specify start node
		allList.get(Util.getIndex(4, 4)).setNodeSpecial(1);
		
		//specify end node
		allList.get(Util.getIndex(60, 36)).setNodeSpecial(2);
		
		new AnimationTimer() {
			
			@Override
			public void handle(long time) {
				gc.clearRect(0, 0, 1600, 800);
				
				for(Tile t : allList) {
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
				
				for(Tile t : openList) {
					gc.setFill(Color.YELLOW);
					gc.fillRect(t.getX()*tileSize, t.getY()*tileSize, tileSize, tileSize);
					gc.setFill(Color.BLACK);
					
					
				}
				
				for(Tile t : allList) {
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
						Tile start=null, end=null;
						
						for(Tile t : allList) {
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
							Tile parent = allList.get(parentID);
							
							addToOpenList(Util.getIndex(parent.getX(), parent.getY()-1), parentID);
							addToOpenList(Util.getIndex(parent.getX()+1, parent.getY()-1), parentID);
							addToOpenList(Util.getIndex(parent.getX()+1, parent.getY()), parentID);
							addToOpenList(Util.getIndex(parent.getX()+1, parent.getY()+1), parentID);
							addToOpenList(Util.getIndex(parent.getX(), parent.getY()+1), parentID);
							addToOpenList(Util.getIndex(parent.getX()-1, parent.getY()+1), parentID);
							addToOpenList(Util.getIndex(parent.getX()-1, parent.getY()), parentID);
							addToOpenList(Util.getIndex(parent.getX()-1, parent.getY()-1), parentID);
							
							Tile lowestFCostTile = null;
							
							for(Tile t : openList) {
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
						for(Tile t : allList) {
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
					Tile t = allList.get(index);
					
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
		scene.setOnMouseDragged(eh);
	}
	
	private static void addToOpenList(int index, int parentID) {
		if(index != -1) {
			Tile t = allList.get(index);
			if(openList.contains(t)) {
				Tile pt = allList.get(parentID);
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
	}

}
