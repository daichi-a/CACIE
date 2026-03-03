package CACIE.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BreedingPanel extends JPanel {
    private static final int GRID1_WIDTH = 4;
    private static final int GRID1_HEIGHT = 3;
    private static final int GRID2_WIDTH = 8;
    private static final int GRID2_HEIGHT = 4;
    
    private static final int CELL_WIDTH = 60;
    private static final int CELL_HEIGHT = 60;
    
    // グリッドと色のマッピング (ID: 1, 2)
    private Map<Integer, Color> gridColors;
    private ArrayList<Rectangle> grids = new ArrayList<>();
    private Rectangle currentGrid;
    private String currentCellIdStr = "outside";

    public BreedingPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.LIGHT_GRAY);
        setOpaque(false);
        
        // グリッドの配色を設定
        createGridColors();
        
        // グリッドを作成
        createGrids();
        
        // マウスイベントリスナーを追加
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMoved(e);
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseMoved(e);
            }
        });
    }
    
    private void createGridColors() {
        // グリッド ID からユニークな色を割り当てる
        gridColors = new HashMap<>();
        
        // Grid 1 - Sky Blue  
        gridColors.put(1, new Color(135, 206, 235));
        
        // Grid 2 - Pale Green  
        gridColors.put(2, new Color(152, 251, 152));
    }
    
    private void createGrids() {
        grids.clear();
        
        // Grid 1 at position (50, 50) with size 4x3
        Rectangle grid1 = new Rectangle(50, 50, GRID1_WIDTH * CELL_WIDTH, GRID1_HEIGHT * CELL_HEIGHT);
        grids.add(grid1);
        
        // Grid 2 at position (50, 250) with size 8x4
        Rectangle grid2 = new Rectangle(50, 250, GRID2_WIDTH * CELL_WIDTH, GRID2_HEIGHT * CELL_HEIGHT);
        grids.add(grid2);
        
        currentGrid = null;
    }
    
    private void handleMouseMoved(MouseEvent e) {
        String oldCellIdStr = getCurrentCellId();
        
        Rectangle newGrid = getGridAtPosition(e.getPoint());
        String newCellIdStr = (newGrid != null) ? getCurrentCellIdFromGrid(newGrid, e) : "outside";
        
        if (!oldCellIdStr.equals(newCellIdStr)) {
            System.out.println("Mouse entered/changed to: " + newCellIdStr);
            currentGrid = newGrid;
            currentCellIdStr = newCellIdStr;
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
    
    // グリッドから現在のセル ID を取得（Gx,Cy,Rz 形式）
    private String getCurrentCellIdFromGrid(Rectangle grid, MouseEvent e) {
        int gridIndex = -1;
        for (int i = 0; i < grids.size(); i++) {
            if (grids.get(i) == grid) {
                gridIndex = i;
                break;
            }
        }
        
        if (gridIndex == -1) return "outside";
        
        // セルの位置を計算（列番号）
        int cellX = (int) ((e.getX() - grid.x) / CELL_WIDTH);
        
        // 行番号の計算：セルの高さで区切る（重要！）
        int row = (int) ((e.getY() - grid.y) / CELL_HEIGHT);
        
        // グリッド 2 の場合は列オフセットを追加（グリッド座標系での全体列）
        int overallColumn = cellX + 1;
        if (gridIndex == 1) {
            overallColumn += GRID1_WIDTH;
        }
        
        return "G" + (gridIndex + 1) + ",C" + overallColumn + ",R" + (row + 1);
    }
    
    // グリッドの現在の ID を取得（例外を防止）
    private String getCurrentCellId() {
        if (currentGrid == null || grids.isEmpty()) {
            return "outside";
        }
        
        for (int i = 0; i < grids.size(); i++) {
            if (grids.get(i) == currentGrid) {
                // セル情報を取得して返す
                try {
                    String[] parts = currentCellIdStr.split(",");
                    int gridIdx = Integer.parseInt(parts[0]);
                    int colIdx = Integer.parseInt(parts[1]);
                    int rowIdx = Integer.parseInt(parts[2]);
                    return "G" + gridIdx + ",C" + colIdx + ",R" + rowIdx;
                } catch (NumberFormatException ex) {
                    // 文字列が不正な場合（例: "outside"）は無視して元の ID を返す
                    continue;
                }
            }
        }
        return "outside";
    }
    
    // グリッドの ID, 列ID, 行ID からユニークな色を取得するメソッド
    private Color getCellColor(int gridId, int columnId, int rowId) {
        // HSV を使用して、GridID/ColumnID/RowID の3つを組み合わせてユニークな色を生成
        
        // 基礎的な Hue: GridID で大幅に変化させる（各グリッドが異なる色系を持つ）
        float hueBase = (gridId - 1) * 90f; // Grid 1: 0°, Grid 2: 90°で大きく分ける
        
        // Column の影響：列ごとの微細な変化を加える（45°周期で変化させる）
        float hueColumn = (columnId - 1) * 45f;
        
        // Row の影響：行ごとのさらなる微細な変化を加える（30°周期で変化させる）
        float hueRow = (rowId - 1) * 30f;
        
        // これらを合計して最終的な Hue を設定
        float finalHue = Math.abs((hueBase + hueColumn + hueRow) % 360.0f);
        
        // Saturation と Brightness も ID に依存させて、同じ色になり難くする
        float saturation = 0.5f + ((gridId * columnId + rowId) % 5) * 0.1f; 
        float brightness = 0.5f + ((gridId + rowId * 2) % 4) * 0.1f - (columnId % 2 == 0 ? 0.05f : 0);
        
        // 0-1 の範囲に正規化（HSV は 0-1 です）
        float finalSaturation = saturation; 
        float finalBrightness = brightness;
        
        return Color.getHSBColor(finalHue, finalSaturation, finalBrightness);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // すべてのグリッドを描画
        for (Rectangle grid : grids) {
            // グリッドの色を取得 (index+1 いため ID に対応)
            int gridId = grids.indexOf(grid) + 1;
            Color gridColor = gridColors.get(gridId);
            
            if (gridColor != null) {
                // セルごとに異なる色を生成して描画
                for (int i = 0; i < grid.width / CELL_WIDTH; i++) {
                    for (int j = 0; j < grid.height / CELL_HEIGHT; j++) {
                        int cellX = grid.x + i * CELL_WIDTH;
                        int cellY = grid.y + j * CELL_HEIGHT;
                        
                        // セル ID を計算（重要：row は i,j に直接マッピング）
                        int columnId = i + 1;
                        int rowId = j + 1;
                        
                        // 配色決定関数を呼び出して色を取得
                        Color cellColor = getCellColor(gridId, columnId, rowId);
                        
                        // セルを塗りつぶす
                        g.setColor(cellColor);
                        g.fillRect(cellX, cellY, CELL_WIDTH - 1, CELL_HEIGHT - 1);
                        
                        // セルの枠線を描画
                        if (i == grid.width / CELL_WIDTH - 1 || j == grid.height / CELL_HEIGHT - 1) {
                            g.setColor(Color.BLACK);
                            g.drawRect(cellX, cellY, CELL_WIDTH, CELL_HEIGHT);
                        } else {
                            // インナーの枠線はより薄い色で描画
                            g.setColor(Color.LIGHT_GRAY);
                            g.drawRect(cellX, cellY, CELL_WIDTH, CELL_HEIGHT);
                        }
                    }
                }
            }
        }
    }
}