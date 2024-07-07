package tides;

import java.util.*;

import tides.GridLocation;
import tides.WeightedQuickUnionUF;

/**
 * This class contains methods that provide information about select terrains 
 * using 2D arrays. Uses floodfill to flood given maps and uses that 
 * information to understand the potential impacts. 
 * Instance Variables:
 *  - a double array for all the heights for each cell
 *  - a GridLocation array for the sources of water on empty terrain 
 * 
 * @author Original Creator Keith Scharz (NIFTY STANFORD) 
 * @author Vian Miranda (Rutgers University)
 */
public class RisingTides {

    // Instance variables
    private double[][] terrain;     // an array for all the heights for each cell
    private GridLocation[] sources; // an array for the sources of water on empty terrain 

    /**
     * DO NOT EDIT!
     * Constructor for RisingTides.
     * @param terrain passes in the selected terrain 
     */
    public RisingTides(Terrain terrain) {
        this.terrain = terrain.heights;
        this.sources = terrain.sources;
    }

    /**
     * Find the lowest and highest point of the terrain and output it.
     * 
     * @return double[][], with index 0 and index 1 being the lowest and 
     * highest points of the terrain, respectively
     */
    public double[] elevationExtrema() {

        /* WRITE YOUR CODE BELOW */
        double lowest = 10000000;
        double highest = -10000000;
        for (int row = 0; row < terrain.length; row++) { 
            for (int column = 0; column < terrain[row].length; column++) {
                if (terrain[row][column] < lowest) lowest = terrain[row][column];
                if (terrain[row][column] > highest) highest = terrain[row][column];
            }
        }
        double extrema[] = {lowest, highest};
        return extrema;
    }

    /**
     * Implement the floodfill algorithm using the provided terrain and sources.
     * 
     * All water originates from the source GridLocation. If the height of the 
     * water is greater than that of the neighboring terrain, flood the cells. 
     * Repeat iteratively till the neighboring terrain is higher than the water 
     * height.
     * 
     * 
     * @param height of the water
     * @return boolean[][], where flooded cells are true, otherwise false
     */
    public boolean[][] floodedRegionsIn(double height) {
        
        /* WRITE YOUR CODE BELOW */
        boolean[][] resulting = new boolean[terrain.length][terrain[0].length];
        ArrayList<GridLocation> water = new ArrayList<GridLocation>();
        for (int i = 0; i < sources.length; i++) {
            GridLocation source = sources[i];
            water.add(source);
            resulting[source.row][source.col] = true;
        }
        while (water.size() != 0) {
            GridLocation current = water.remove(0);
            int x = current.row;
            int y = current.col;
            for (int i = -1; i < 2; i += 2) {
                int xneighbor = x + i;
                int yneighbor = y + i;
                if ((xneighbor >= 0) && (xneighbor < terrain.length)) {
                    if (resulting[xneighbor][y] == false) {
                        if (terrain[xneighbor][y] <= height) {
                            water.add(new GridLocation(xneighbor,y));
                            resulting[xneighbor][y] = true;
                        }
                    }
                }
                if ((yneighbor >= 0) && (yneighbor < terrain[0].length)) {
                    if (resulting[x][yneighbor] == false) {
                        if (terrain[x][yneighbor] <= height) {
                            water.add(new GridLocation(x, yneighbor));
                            resulting[x][yneighbor] = true;
                        }
                    }
                }
            }
        }
        return resulting; // substitute this line. It is provided so that the code compiles.
    }

    /**
     * Checks if a given cell is flooded at a certain water height.
     * 
     * @param height of the water
     * @param cell location 
     * @return boolean, true if cell is flooded, otherwise false
     */
    public boolean isFlooded(double height, GridLocation cell) {
        
        /* WRITE YOUR CODE BELOW */
        boolean[][] floodcells = floodedRegionsIn(height);
        return floodcells[cell.row][cell.col]; // substitute this line. It is provided so that the code compiles.
    }

    /**
     * Given the water height and a GridLocation find the difference between 
     * the chosen cells height and the water height.
     * 
     * If the return value is negative, the Driver will display "meters below"
     * If the return value is positive, the Driver will display "meters above"
     * The value displayed will be positive.
     * 
     * @param height of the water
     * @param cell location
     * @return double, representing how high/deep a cell is above/below water
     */
    public double heightAboveWater(double height, GridLocation cell) {
        
        /* WRITE YOUR CODE BELOW */
        int x = cell.row;
        int y = cell.col;
        return (terrain[x][y] - height); // substitute this line. It is provided so that the code compiles.
    }

    /**
     * Total land available (not underwater) given a certain water height.
     * 
     * @param height of the water
     * @return int, representing every cell above water
     */
    public int totalVisibleLand(double height) {
        
        /* WRITE YOUR CODE BELOW */
        boolean[][] floodedRegions = floodedRegionsIn(height);
        int notFlooded = 0;
        for (int i = 0; i < floodedRegions.length; i++) {
            for (int j = 0; j < floodedRegions[i].length; j++) {
                if (floodedRegions[i][j] == false) notFlooded++;
            }
        }
        return notFlooded; // substitute this line. It is provided so that the code compiles.
    } 


    /**
     * Given 2 heights, find the difference in land available at each height. 
     * 
     * If the return value is negative, the Driver will display "Will gain"
     * If the return value is positive, the Driver will display "Will lose"
     * The value displayed will be positive.
     * 
     * @param height of the water
     * @param newHeight the future height of the water
     * @return int, representing the amount of land lost or gained
     */
    public int landLost(double height, double newHeight) {
        
        /* WRITE YOUR CODE BELOW */
        int oldh = totalVisibleLand(height);
        int newh = totalVisibleLand(newHeight);
        return (oldh - newh); // substitute this line. It is provided so that the code compiles.
    }

    /**
     * Count the total number of islands on the flooded terrain.
     * 
     * Parts of the terrain are considered "islands" if they are completely 
     * surround by water in all 8-directions. Should there be a direction (ie. 
     * left corner) where a certain piece of land is connected to another 
     * landmass, this should be considered as one island. A better example 
     * would be if there were two landmasses connected by one cell. Although 
     * seemingly two islands, after further inspection it should be realized 
     * this is one single island. Only if this connection were to be removed 
     * (height of water increased) should these two landmasses be considered 
     * two separate islands.
     * 
     * @param height of the water
     * @return int, representing the total number of islands
     */
    public int numOfIslands(double height) {
        
        /* WRITE YOUR CODE BELOW */
        WeightedQuickUnionUF uf = new WeightedQuickUnionUF(terrain.length, terrain[0].length);
        boolean[][] flood = floodedRegionsIn(height);
        for (int i = 0; i < terrain.length; i++) {
            for (int j = 0; j < terrain[i].length; j++) {
                GridLocation current = new GridLocation(i,j);
                if (flood[i][j] == false) {
                    for (int k = (i - 1); (k < (i + 2)) && (k < terrain.length); k++) {
                    for (int l = (j - 1); (l < (j + 2)) && (l < terrain[0].length); l++) {
                        if ((k >= 0) && (l >= 0)) {
                            GridLocation neighbor = new GridLocation(k,l);
                            if ((flood[k][l] == false) && (neighbor != current)) uf.union(neighbor, current);
                        }
                    }
                }
                }
            }
        }
        ArrayList<GridLocation> nodes = new ArrayList<GridLocation>();
        for (int i = 0; i < terrain.length; i++) {
            for (int j = 0; j < terrain[i].length; j++) {
                GridLocation current2 = new GridLocation(i,j);
                if (flood[i][j] == false) {
                    boolean inNodes = false;
                    for (int k = 0; k < nodes.size(); k++) {
                        if (nodes.get(k) == uf.find(current2)) inNodes = true;
                    }
                    if (inNodes == false) nodes.add(uf.find(current2));
                }
            }
        }
        return nodes.size(); // substitute this line. It is provided so that the code compiles.
    }
}