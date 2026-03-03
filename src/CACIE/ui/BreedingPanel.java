package CACIE.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class BreedingPanel extends JPanel {
    private static final int GRID1_WIDTH = 4;
    private static final int GRID1_HEIGHT = 3;
    private static final int GRID2_WIDTH = 8;
    private static final int GRID2_HEIGHT = 4;
    
    private static final int CELL_WIDTH = 60;
    private static final int CELL_HEIGHT = 60;
    
    // List of grid rectangles with cell IDs to detect mouse position
    private ArrayList<Rectangle> grids;
    private Rectangle currentGrid;
    
    public BreedingPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.LIGHT_GRAY);
        
        // Create grid list
        createGrids();
        
        // Add mouse motion listener to detect current grid on every movement
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMoved(e);
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                // Also call the same handler for drag events
                handleMouseMoved(e);
            }
        });
    }
    
    private void createGrids() {
        // Create two grids with individual cell IDs
        grids = new ArrayList<>();
        
        // Grid 1 at position (50, 50) with size 4x3
        Rectangle grid1 = new Rectangle(50, 50, GRID1_WIDTH * CELL_WIDTH, GRID1_HEIGHT * CELL_HEIGHT);
        grids.add(grid1);
        
        // Grid 2 at position (50, 250) with size 8x4
        Rectangle grid2 = new Rectangle(50, 250, GRID2_WIDTH * CELL_WIDTH, GRID2_HEIGHT * CELL_HEIGHT);
        grids.add(grid2);
        
        // Initialize current grid to null
        currentGrid = null;
    }
    
    private void handleMouseMoved(MouseEvent e) {
        // Determine which cell the mouse is currently in
        String oldCellId = (currentGrid != null) ? getCurrentCellId() : "outside";
        
        Rectangle newGrid = getGridAtPosition(e.getPoint());
        String newCellId = (newGrid != null) ? getCurrentCellIdFromGrid(newGrid, e) : "outside";
        
        // If the mouse moved into a different cell, print the cell ID change
        if (!oldCellId.equals(newCellId)) {
            System.out.println("Mouse entered/changed to: " + newCellId);
            
            // Update current grid
            currentGrid = newGrid;
        }
    }
    
    private Rectangle getGridAtPosition(Point point) {
        for (Rectangle grid : grids) {
            if (grid.contains(point)) {
                return grid;
            }
        }
        return null;
    }
    
    // Get the current cell ID from the current grid
    private String getCurrentCellId() {
        if (currentGrid == null || !grids.isEmpty()) {
            for (int i = 0; i < grids.size(); i++) {
                if (grids.get(i) == currentGrid) {
                    return "G" + (i + 1); // Grid ID
                }
            }
        }
        return null;
    }
    
    // Get the cell ID from a specific grid
    private String getCurrentCellIdFromGrid(Rectangle grid, MouseEvent e) {
        int gridIndex = -1;
        for (int i = 0; i < grids.size(); i++) {
            if (grids.get(i) == grid) {
                gridIndex = i;
                break;
            }
        }
        
        if (gridIndex == -1) return "Invalid Grid";
        
        // Calculate cell position
        int offsetX = (grid.width / CELL_WIDTH);  // Grid 1: 4 cells, Grid 2: 8 cells
        int offsetY = grid.y;  // Grid 1 y=50, Grid 2 y=250
        
        // Determine which grid it is by position
        if (grid.y == 50) {
            // Grid 1 at y=50
            offsetX = 4;
            offsetY = 50;
        } else {
            // Grid 2 at y=250
            offsetX = 8;
            offsetY = 250;
        }
        
        int cellX = (int) ((grid.x + e.getX() - grid.x) / CELL_WIDTH);
        int cellY = (int) ((e.getY() - grid.y) / CELL_HEIGHT);
        
        // Calculate overall column considering the gap between grids
        int overallColumn = cellX;
        if (gridIndex == 1) {
            // For Grid 2, add offset for Grid 1's columns
            overallColumn = GRID1_WIDTH + offsetX + cellX - (GRID1_WIDTH);
            overallColumn += GRID1_WIDTH;
            overallColumn = GRID1_WIDTH + cellX;
        } else {
            overallColumn = cellX;
        }
        
        int row = 0;
        if (grid.y == 50) {
            // Grid 1 rows
            row = cellY / CELL_HEIGHT;
        } else {
            // Grid 2 is below Grid 1, so add rows from first grid
            row = GRID1_HEIGHT + cellY / CELL_HEIGHT;
        }
        
        return "C" + (overallColumn + 1) + ",R" + (row + 1);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw all grids using drawRect
        for (Rectangle grid : grids) {
            g.setColor(Color.BLACK);
            g.drawRect(grid.x, grid.y, grid.width, grid.height);
            
            // Draw grid lines
            for (int i = 1; i < (grid.width / CELL_WIDTH); i++) {
                g.drawLine(grid.x + i * CELL_WIDTH, grid.y, 
                          grid.x + i * CELL_WIDTH, grid.y + (grid.height / CELL_HEIGHT));
            }
            for (int i = 1; i < (grid.height / CELL_HEIGHT); i++) {
                g.drawLine(grid.x, grid.y + i * CELL_HEIGHT, 
                          grid.x + (grid.width / CELL_WIDTH) * CELL_WIDTH, grid.y + i * CELL_HEIGHT);
            }
        }
    }
}