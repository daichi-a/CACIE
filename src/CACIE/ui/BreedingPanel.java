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
    
    private ArrayList<JLabel> labels;
    private JLabel draggedLabel;
    private Point dragOffset;
    private Point lastMousePosition;
    
    // Grid positions for the two grids
    private Rectangle grid1Bounds;
    private Rectangle grid2Bounds;
    
    public BreedingPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.LIGHT_GRAY);
        
        labels = new ArrayList<>();
        
        // Initialize grid bounds
        grid1Bounds = new Rectangle(50, 50, GRID1_WIDTH * CELL_WIDTH, GRID1_HEIGHT * CELL_HEIGHT);
        grid2Bounds = new Rectangle(50, 250, GRID2_WIDTH * CELL_WIDTH, GRID2_HEIGHT * CELL_HEIGHT);
        
        // Create sample labels
        createLabels();
        
        // Add mouse listeners to the panel
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }
        });
        
        // Add a button to add new labels
        JButton addButton = new JButton("Add Label");
        addButton.setBounds(50, 550, 100, 30);
        addButton.addActionListener(e -> addNewLabel());
        add(addButton);
    }
    
    private void createLabels() {
        // Create labels for the first grid (4x3)
        for (int i = 0; i < GRID1_WIDTH * GRID1_HEIGHT; i++) {
            JLabel label = createDraggableLabel("Label " + (i + 1), grid1Bounds.x + (i % GRID1_WIDTH) * CELL_WIDTH, 
                                               grid1Bounds.y + (i / GRID1_WIDTH) * CELL_HEIGHT);
            labels.add(label);
            add(label);
        }
        
        // Create labels for the second grid (8x4)
        for (int i = 0; i < GRID2_WIDTH * GRID2_HEIGHT; i++) {
            JLabel label = createDraggableLabel("Label " + (i + 1 + GRID1_WIDTH * GRID1_HEIGHT), 
                                               grid2Bounds.x + (i % GRID2_WIDTH) * CELL_WIDTH, 
                                               grid2Bounds.y + (i / GRID2_WIDTH) * CELL_HEIGHT);
            labels.add(label);
            add(label);
        }
    }
    
    private JLabel createDraggableLabel(String text, int x, int y) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setBounds(x, y, CELL_WIDTH, CELL_HEIGHT);
        label.setOpaque(true);
        label.setBackground(Color.CYAN);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        return label;
    }
    
    private void handleMousePressed(MouseEvent e) {
        for (JLabel label : labels) {
            if (label.getBounds().contains(e.getPoint())) {
                draggedLabel = label;
                dragOffset = new Point(label.getX() - e.getX(), label.getY() - e.getY());
                lastMousePosition = e.getPoint();
                label.setOpaque(true);
                label.setBackground(Color.RED);
                break;
            }
        }
    }
    
    private void handleMouseDragged(MouseEvent e) {
        if (draggedLabel != null) {
            int newX = e.getX() + dragOffset.x;
            int newY = e.getY() + dragOffset.y;
            
            // Move the label
            draggedLabel.setLocation(newX, newY);
            
            // Snap to grid if near grid boundary
            snapToGrid(e.getPoint());
            
            // Update the label's position in the panel
            draggedLabel.repaint();
        }
    }
    
    private void handleMouseReleased(MouseEvent e) {
        if (draggedLabel != null) {
            // Determine which grid the label was dropped in
            Rectangle targetGrid = determineTargetGrid(e.getPoint());
            
            if (targetGrid != null) {
                // Snap to grid
                Point snappedPosition = snapToGrid(e.getPoint());
                
                // Update label position
                draggedLabel.setLocation(snappedPosition);
                
                // Set action command based on grid and position
                String actionCommand = getActionCommand(targetGrid, snappedPosition);
                draggedLabel.setActionCommand(actionCommand);
                
                // Notify that label was moved
                System.out.println("Label moved to: " + actionCommand);
            }
            
            // Reset drag state
            draggedLabel.setOpaque(true);
            draggedLabel.setBackground(Color.CYAN);
            draggedLabel = null;
        }
    }
    
    private Point snapToGrid(Point mousePoint) {
        Rectangle targetGrid = determineTargetGrid(mousePoint);
        if (targetGrid != null) {
            // Calculate grid cell position
            int gridX = (mousePoint.x - targetGrid.x) / CELL_WIDTH;
            int gridY = (mousePoint.y - targetGrid.y) / CELL_HEIGHT;
            
            // Clamp to grid bounds
            gridX = Math.max(0, Math.min(gridX, getGridWidth(targetGrid) - 1));
            gridY = Math.max(0, Math.min(gridY, getGridHeight(targetGrid) - 1));
            
            // Calculate snapped position
            int snappedX = targetGrid.x + gridX * CELL_WIDTH;
            int snappedY = targetGrid.y + gridY * CELL_HEIGHT;
            
            return new Point(snappedX, snappedY);
        }
        return mousePoint;
    }
    
    private Rectangle determineTargetGrid(Point point) {
        if (grid1Bounds.contains(point)) {
            return grid1Bounds;
        } else if (grid2Bounds.contains(point)) {
            return grid2Bounds;
        }
        return null;
    }
    
    private int getGridWidth(Rectangle grid) {
        if (grid.equals(grid1Bounds)) {
            return GRID1_WIDTH;
        } else if (grid.equals(grid2Bounds)) {
            return GRID2_WIDTH;
        }
        return 0;
    }
    
    private int getGridHeight(Rectangle grid) {
        if (grid.equals(grid1Bounds)) {
            return GRID1_HEIGHT;
        } else if (grid.equals(grid2Bounds)) {
            return GRID2_HEIGHT;
        }
        return 0;
    }
    
    private String getActionCommand(Rectangle grid, Point position) {
        int gridWidth = getGridWidth(grid);
        int gridHeight = getGridHeight(grid);
        
        int gridX = (position.x - grid.x) / CELL_WIDTH;
        int gridY = (position.y - grid.y) / CELL_HEIGHT;
        
        // Determine grid type and position
        String gridType = grid.equals(grid1Bounds) ? "GRID1" : "GRID2";
        return gridType + "_POS_" + gridX + "_" + gridY;
    }
    
    private void addNewLabel() {
        JLabel newLabel = createDraggableLabel("New Label", 50, 50);
        labels.add(newLabel);
        add(newLabel);
        newLabel.repaint();
    }
    
    // Method to add action listener to labels
    public void addLabelActionListener(ActionListener listener) {
        for (JLabel label : labels) {
            // Add action listener to the label (this would need to be implemented with custom component)
            // For simplicity, we'll just use the command system
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw grid 1
        g.setColor(Color.BLACK);
        g.drawRect(grid1Bounds.x, grid1Bounds.y, grid1Bounds.width, grid1Bounds.height);
        for (int i = 1; i < GRID1_WIDTH; i++) {
            g.drawLine(grid1Bounds.x + i * CELL_WIDTH, grid1Bounds.y, grid1Bounds.x + i * CELL_WIDTH, grid1Bounds.y + grid1Bounds.height);
        }
        for (int i = 1; i < GRID1_HEIGHT; i++) {
            g.drawLine(grid1Bounds.x, grid1Bounds.y + i * CELL_HEIGHT, grid1Bounds.x + grid1Bounds.width, grid1Bounds.y + i * CELL_HEIGHT);
        }
        
        // Draw grid 2
        g.drawRect(grid2Bounds.x, grid2Bounds.y, grid2Bounds.width, grid2Bounds.height);
        for (int i = 1; i < GRID2_WIDTH; i++) {
            g.drawLine(grid2Bounds.x + i * CELL_WIDTH, grid2Bounds.y, grid2Bounds.x + i * CELL_WIDTH, grid2Bounds.y + grid2Bounds.height);
        }
        for (int i = 1; i < GRID2_HEIGHT; i++) {
            g.drawLine(grid2Bounds.x, grid2Bounds.y + i * CELL_HEIGHT, grid2Bounds.x + grid2Bounds.width, grid2Bounds.y + i * CELL_HEIGHT);
        }
        
        // Label text
        g.setColor(Color.BLACK);
        g.drawString("Grid 1 (4x3)", grid1Bounds.x, grid1Bounds.y - 10);
        g.drawString("Grid 2 (8x4)", grid2Bounds.x, grid2Bounds.y - 10);
    }
}
