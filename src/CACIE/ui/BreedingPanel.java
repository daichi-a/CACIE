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
    
    private ArrayList<JButton> buttons;
    private JButton draggedButton;
    private Point dragOffset;
    private Rectangle grid1Bounds;
    private Rectangle grid2Bounds;

    private int mouseX, mouseY;
    private boolean mousePressed = false;
    
    public BreedingPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.LIGHT_GRAY);
        
        buttons = new ArrayList<>();
        
        // Initialize grid bounds
        grid1Bounds = new Rectangle(50, 50, GRID1_WIDTH * CELL_WIDTH, GRID1_HEIGHT * CELL_HEIGHT);
        grid2Bounds = new Rectangle(50, 250, GRID2_WIDTH * CELL_WIDTH, GRID2_HEIGHT * CELL_HEIGHT);
        
        // Create sample buttons
        createButtons();
        
        // Add mouse listeners to the panel
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
                mousePressed = true;
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
                mousePressed = false;
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
                mouseX = e.getX();
                mouseY = e.getY();
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e){
                //System.out.println("x:" + e.getX() + " y:" + e.getY());
                mouseX = e.getX();
                mouseY = e.getY();
                repaint();
            }
        });
        
        // Add a button to add new buttons
        JButton addButton = new JButton("Add Button");
        addButton.setBounds(50, 550, 100, 30);
        addButton.addActionListener(e -> addNewButton());
        add(addButton);
    }


    private void createButtons() {
        // Create buttons for the first grid (4x3)
        for (int i = 0; i < GRID1_WIDTH * GRID1_HEIGHT; i++) {
            JButton button = createDraggableButton("Button " + (i + 1), 
                    grid1Bounds.x + (i % GRID1_WIDTH) * CELL_WIDTH, 
                    grid1Bounds.y + (i / GRID1_WIDTH) * CELL_HEIGHT);
            buttons.add(button);
            add(button);
        }
        
        // Create buttons for the second grid (8x4)
        for (int i = 0; i < GRID2_WIDTH * GRID2_HEIGHT; i++) {
            JButton button = createDraggableButton("Button " + (i + 1 + GRID1_WIDTH * GRID1_HEIGHT), 
                    grid2Bounds.x + (i % GRID2_WIDTH) * CELL_WIDTH, 
                    grid2Bounds.y + (i / GRID2_WIDTH) * CELL_HEIGHT);
            buttons.add(button);
            add(button);
        }
    }
    
    private JButton createDraggableButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, CELL_WIDTH, CELL_HEIGHT);
        button.setOpaque(true);
        button.setBackground(Color.CYAN);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        return button;
    }
    
    private void handleMousePressed(MouseEvent e) {
        // Find which button was pressed
        for (JButton button : buttons) {
            if (button.getBounds().contains(e.getPoint())) {
                draggedButton = button;
                // Calculate offset between mouse and button position
                dragOffset = new Point(button.getX() - e.getX(), button.getY() - e.getY());
                // Highlight the dragged button
                button.setOpaque(true);
                button.setBackground(Color.RED);
                break;
            }
        }
        repaint();
    }
    
    private void handleMouseDragged(MouseEvent e) {
        if (draggedButton != null) {
            // Calculate new position with offset - this makes the button follow the cursor exactly
            int newX = e.getX() + dragOffset.x;
            int newY = e.getY() + dragOffset.y;
            
            // Move the button
            draggedButton.setLocation(newX, newY);
            
            // Repaint to show the movement
            draggedButton.repaint();
        }
        repaint();
    }
    
    private void handleMouseReleased(MouseEvent e) {
        if (draggedButton != null) {
            // Determine which grid the button was dropped in
            Rectangle targetGrid = determineTargetGrid(e.getPoint());
            
            if (targetGrid != null) {
                // Snap to grid
                Point snappedPosition = snapToGrid(e.getPoint());
                
                // Update button position
                draggedButton.setLocation(snappedPosition);
                
                // Set action command based on grid and position
                String actionCommand = getActionCommand(targetGrid, snappedPosition);
                //draggedButton.setActionCommand(actionCommand);
                System.out.println(actionCommand);
                
                // Notify that button was moved
                System.out.println("Button moved to: " + actionCommand);
            }
            
            // Reset drag state
            draggedButton.setOpaque(true);
            draggedButton.setBackground(Color.CYAN);
            draggedButton = null;
        }
        repaint();
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
        int gridX = (position.x - grid.x) / CELL_WIDTH;
        int gridY = (position.y - grid.y) / CELL_HEIGHT;
        
        // Determine grid type and position
        String gridType = grid.equals(grid1Bounds) ? "GRID1" : "GRID2";
        return gridType + "_POS_" + gridX + "_" + gridY;
    }
    
    private void addNewButton() {
        JButton newButton = createDraggableButton("New Button", 50, 50);
        buttons.add(newButton);
        add(newButton);
        newButton.repaint();
    }
    
    // Method to add action listener to buttons
    public void addLabelActionListener(ActionListener listener) {
        for (JButton button : buttons) {
            button.addActionListener(listener);
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

        Color color1 = new java.awt.Color(0x99, 0, 0x0FF);//RGBの色を指定して色を作成
		g.setColor(color1);//次に描画する時の色を指定
		g.drawRect(mouseX-20, mouseY-20, 40, 40);
    }

}