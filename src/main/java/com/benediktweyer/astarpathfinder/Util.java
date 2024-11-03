package com.benediktweyer.astarpathfinder;

public class Util {

	public static int getIndex(int x, int y) {
		if(y>=800/App.tileSize || x>=1600/App.tileSize || y<0 || x<0) {
			return -1;
		}
		
		return y + x*(800/App.tileSize);
	}
	
}
