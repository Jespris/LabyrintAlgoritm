package src;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.UIManager;
import javax.swing.JOptionPane;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;


public class Ex1 {
    private static final int WIDTH = 800;  // Size of the window in pixels
    private static final int HEIGHT = 800;
    
    static int CELLS_IN_ROW = 10;    // The size of the maze is cells*cells (default is 20*20)


    
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

	private int[] alreadyDoneArray;// = new int[cells*cells];

	/*
	public void createDoneArray() {
		for (int i = 0; i >= cells*cells; i++) {
			alreadyDoneArray[i] = -1;
		}
	}*/



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
    
    public void paintComponent(Graphics g) {
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
		createMaze(cells, g);
    }

    private void createMaze (int cells, Graphics g) {

		DisjointSet disjointSet = new DisjointSet(cells*cells);
		int randomCell = random.nextInt(cells*cells);
		GetNeighbour neighbour;
		int col;
		int row;
		int root1;
		int root2;
		int iterationCounter = 0;
		int iterationCounter2 = 0;
		alreadyDoneArray = new int[cells*cells];
		Arrays.fill(alreadyDoneArray, -1);
		System.out.println("Array element one: " + alreadyDoneArray[0]);
		do {


			if(isArrayFull()) {
				System.out.println("isArrayFull() returns true.");
				break;
			}



				if (alreadyDone(randomCell)){

					for (int i = 0; i < cells * cells; i++) {
						if (alreadyDoneArray[i] == -1) {
							int current = randomCell;
							do {
								randomCell = random.nextInt(cells*cells);
								iterationCounter++;
								System.out.println("Randomizer loop has run " + iterationCounter + " times.");
							} while (randomCell == current);
							System.out.println("Randomizer loop has run " + iterationCounter + " times and stopped.");

						}
					}
				}
/*
				do {
					randomCell = random.nextInt(cells*cells);
					iterationCounter++;
					System.out.println("Randomizer loop has run " + iterationCounter + " times.");
				} while (!alreadyDone(randomCell, first));

				first = false;
				System.out.println("Randomizer loop has run " + iterationCounter + " times and stopped.");
				*/



			// System.out.println("New random cell: " + randomCell);
			neighbour = getNeighbour(randomCell);
			// System.out.println("Neighbour: " + neighbour.getCellIndex());
			root1 = disjointSet.Find(randomCell);  // root of random cell set
			root2 = disjointSet.Find(neighbour.getCellIndex()); // root of neighbour set
			if (root1 != root2){
				// union and remove wall between them
				col = getCol(randomCell);
				row = getRow(randomCell);
				disjointSet.Union(root1, root2);  // make the cells part of same disjoint set
				drawWall(col, row, neighbour.getWallIndex(), g);  // un-draw the wall

			}
			iterationCounter2++;
			System.out.println("Execution loop has run " + iterationCounter2 + " times.");
		} while (!mazeIsComplete());
		System.out.println("Execution loop has run " + iterationCounter2 + " times and stopped.");



    }

	private GetNeighbour getNeighbour(final int cell) {
		int neighbour = -1;
		int direction;

		direction = random.nextInt(4);
		if (direction == 1){
			// up
			if (getRow(cell) == 0){
				// upper wall, opposite wall is down
				neighbour = cell + cells;
				direction = 3;
			} else {
				neighbour = cell - cells;
			}
		}
		if (direction == 3){
			// down
			if (getRow(cell) == cells - 1){
				// lower wall, opposite is up
				neighbour = cell - cells;
				direction = 1;
			} else {
				neighbour = cell + cells;
			}
		}
		if (direction == 0){
			// left
			if (getCol(cell) == 0){
				// left wall, opposite is right
				neighbour = cell + 1;
				direction = 2;
			} else {
				neighbour = cell - 1;
			}
		}
		if (direction == 2){
			// right
			if (getCol(cell) == cells - 1){
				// right wall, opposite is left
				neighbour = cell - 1;
				direction = 0;
			} else {
				neighbour = cell + 1;
			}
		}

		assert neighbour != -1;
		return new GetNeighbour(direction, neighbour);
	}

	private boolean alreadyDone(final int cell){


/*
		if (isItFirstTime) {
			alreadyDoneArray = new int[cells*cells];
			Arrays.fill(alreadyDoneArray, -1);
		}
		*/


		if (alreadyDoneArray[cell] == cell && alreadyDoneArray[cell] != -1){
			//System.out.println("Current element being treated: " + alreadyDoneArray[cell]);
			return true;

		} else {
			alreadyDoneArray[cell] = cell;

			for (int i = 0; i < cells*cells; i++) {

				System.out.println("Array element " + i + " is: " + alreadyDoneArray[i]);
			}


			return false;
		}
	}

	private int getRow(final int index){
		return Math.floorDiv(index, cells);
	}

	private int getCol(final int index){
		return index % cells;
	}


	private boolean isArrayFull(){

		int vacancyCounter = 0;
		int elementCounter = 0;

		System.out.println("isArrayFull() executing... ");
		for (int i = 0; i < cells*cells; i++) {
			System.out.println("Array element " + i + " is: " + alreadyDoneArray[i]);
			if (alreadyDoneArray[i] != -1) {
				elementCounter++;
			}
			else {
				vacancyCounter++;
			}
			System.out.println("Number of elements currently: " + elementCounter);
			System.out.println("Number of vacancies currently: " + vacancyCounter);
		}

		int countSum = elementCounter + vacancyCounter;

		for (int i = 0; i < cells*cells; i++) {
			if (alreadyDoneArray[i] == -1) {
				System.out.println("isArrayFull() returning false. Array counter sum: " + countSum);
				return false;
			}
			if (countSum==cells*cells) {
				System.out.println("isArrayFull() returning true. Array counter sum: " + countSum);
				return true;
			}
		}

		System.out.println("isArrayFull() returning false. Array counter sum: " + countSum);
		return false;

	}

	private boolean mazeIsComplete() {  //final DisjointSet disjointSet
		/*
		for (int i=0; i<disjointSet.getSize(); i++){
			if (disjointSet.Find(i) != rootsIndex){
				// System.out.println("Not same root found at index: " + i);
				return false;
			}
		}

		 */

		if (isArrayFull()) {
			System.out.println("isArrayFull() returns true.");
			return true;
		}

		return false;
	}


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
