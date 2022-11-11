package src;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.UIManager;
import javax.swing.JOptionPane;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import java.util.Random;


public class Ex1 {
    private static final int WIDTH = 800;  // Size of the window in pixels
    private static final int HEIGHT = 800;
    
    static int CELLS_IN_ROW = 100;    // The size of the maze is cells*cells (default is 20*20)


    
    public static void main(String[] args) {
	
		// Get the size of the maze from the command line
		if (args.length > 0) {
			try {
				CELLS_IN_ROW = Integer.parseInt(args[0]);  // The maze is of size cells*cells
			} catch (NumberFormatException e) {
				System.err.println("Argument " + args[0] + " should be an integer");
				System.exit(-1);
			}
		}
		// Check that the size is valid
		if ( (CELLS_IN_ROW <= 1) || (CELLS_IN_ROW > 100) ) {
			System.err.println("Invalid size, must be between 2 and 100 ");
			System.exit(-1);
		}
		Runnable r = () -> {
			// Create a JComponent for the maze
			MazeComponent mazeComponent = new MazeComponent(WIDTH, HEIGHT, CELLS_IN_ROW);
			// Change the text of the OK button to "Close"
			UIManager.put("OptionPane.okButtonText", "Close");
			JOptionPane.showMessageDialog(null, mazeComponent, "Maze " + CELLS_IN_ROW + " by " + CELLS_IN_ROW,
						  JOptionPane.INFORMATION_MESSAGE);
		};
		SwingUtilities.invokeLater(r);
	}
}

class MazeComponent extends JComponent {
    protected int width;
    protected int height;
    protected int cells;
    protected int cellWidth;
    protected int cellHeight;
    Random random;

    // Draw a maze of size w*h with c*c cells
    MazeComponent(int w, int h, int c) {
        super();
        cells = c;                // Number of cells
		cellWidth = w/cells;      // Width of a cell
		cellHeight = h/cells;     // Height of a cell
		width =  c*cellWidth;     // Calculate exact dimensions of the component
		height = c*cellHeight;
		setPreferredSize(new Dimension(width+1,height+1));  // Add 1 pixel for the border
		random = new Random();
	}
    
    public void paintComponent(final Graphics g) {
		g.setColor(Color.yellow);                    // Yellow background
		g.fillRect(0, 0, width, height);

		// Draw a grid of cells
		g.setColor(Color.blue);                 // Blue lines
		for (int i = 0; i<=cells; i++) {        // Draw horizontal grid lines
			g.drawLine (0, i*cellHeight, cells*cellWidth, i*cellHeight);
		}
		for (int j = 0; j<=cells; j++) {       // Draw vertical grid lines
			g.drawLine (j*cellWidth, 0, j*cellWidth, cells*cellHeight);
		}

		// Mark entry and exit cells
		paintCell(0,0,Color.green, g);               // Mark entry cell
		drawWall(-1, 0, 2, g);                       // Open up entry cell
		paintCell(cells-1, cells-1,Color.pink, g);   // Mark exit cell
		drawWall(cells-1, cells-1, 2, g);            // Open up exit cell

		g.setColor(Color.yellow);                 // Use yellow lines to remove existing walls
		createMaze(g);
    }

    private void createMaze (final Graphics g) {
		// helper methods for this method are found in region "HELPER METHODS IN CREATE MAZE" below

		final float startTime = System.nanoTime();  // documents current system time in nanoseconds

		// declare variables used in the while loop below
		int cellsRemaining = cells * cells - 1;  // nr of cells in maze
		DisjointSet disjointSet = new DisjointSet(cells*cells);
		int randomCell;
		GetNeighbour neighbour;
		int root1;
		int root2;

		while (cellsRemaining > 0){  // loop while we haven't checked all cell indexes

			randomCell = random.nextInt(cells*cells);  // get a random int in range(0, size of maze)
			neighbour = getNeighbour(randomCell);  // get a random neighbour of the random cell
			root1 = disjointSet.Find(randomCell);  // root of random cell set
			root2 = disjointSet.Find(neighbour.getCellIndex()); // root of neighbour set

			if (root1 != root2){
				// union and remove wall between them
				disjointSet.Union(root1, root2);  // make the cells part of same disjoint set
				cellsRemaining--;  // this cell is now done, we can decrement the cellsRemaining counter

				drawWall( getCol(randomCell), getRow(randomCell), neighbour.getWallIndex(), g);  // un-draw the wall
			}
		}

		// Prints out roots of entrance and exit to make sure we did things correctly
		System.out.println("Entrance: " + disjointSet.Find(0));
		System.out.println("Exit: " + disjointSet.Find(cells * cells - 1));

		final float endTime = System.nanoTime();  // used for printing execution time below:
		System.out.println("Execution time: " + (endTime - startTime)/1000000 + "ms");
    }
	// region [HELPER METHODS IN CREATE MAZE]
	private GetNeighbour getNeighbour(final int cellIndex) {
		// method for getting a random neighbour given a cell index
		int neighbour = -1;
		int direction;

		direction = random.nextInt(4);
		if (direction == 1){
			// up
			if (getRow(cellIndex) == 0){
				// upper wall, opposite wall is down
				neighbour = cellIndex + cells;
				direction = 3;
			} else {
				neighbour = cellIndex - cells;
			}
		}
		if (direction == 3){
			// down
			if (getRow(cellIndex) == cells - 1){
				// lower wall, opposite is up
				neighbour = cellIndex - cells;
				direction = 1;
			} else {
				neighbour = cellIndex + cells;
			}
		}
		if (direction == 0){
			// left
			if (getCol(cellIndex) == 0){
				// left wall, opposite is right
				neighbour = cellIndex + 1;
				direction = 2;
			} else {
				neighbour = cellIndex - 1;
			}
		}
		if (direction == 2){
			// right
			if (getCol(cellIndex) == cells - 1){
				// right wall, opposite is left
				neighbour = cellIndex - 1;
				direction = 0;
			} else {
				neighbour = cellIndex + 1;
			}
		}

		assert neighbour != -1;
		// we need to return both the neighbour cell index and wall side, hence the class
		return new GetNeighbour(direction, neighbour);
	}

	private int getRow(final int index){
		// helper method to find row given an index
		return Math.floorDiv(index, cells);
	}

	private int getCol(final int index) {
		// helper method to find column given an index
		return index % cells;
	}
	// endregion

	// Paints the interior of the cell at position x,y with colour c
    private void paintCell(int x, int y, Color c, Graphics g) {
		int x_pos = x*cellWidth;    // Position in pixel coordinates
		int y_pos = y*cellHeight;
		g.setColor(c);
		g.fillRect(x_pos+1, y_pos+1, cellWidth-1, cellHeight-1);
    }

    
    // Draw the wall w in cell (x,y) (0=left, 1=up, 2=right, 3=down)
    private void drawWall(int col, int row, int wallSide, Graphics g) {
		int x_pos = col*cellWidth;    // Position in pixel coordinates
		int y_pos = row*cellHeight;

		switch (wallSide) {
			case (0) ->       // Wall to the left
					g.drawLine(x_pos, y_pos + 1, x_pos, y_pos + cellHeight - 1);
			case (1) ->       // Wall at top
					g.drawLine(x_pos + 1, y_pos, x_pos + cellWidth - 1, y_pos);
			case (2) ->      // Wall to the right
					g.drawLine(x_pos + cellWidth, y_pos + 1, x_pos + cellWidth, y_pos + cellHeight - 1);
			case (3) ->      // Wall at bottom
					g.drawLine(x_pos + 1, y_pos + cellHeight, x_pos + cellWidth - 1, y_pos + cellHeight);
		}
    }
}
