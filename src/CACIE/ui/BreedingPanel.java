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
    
    // 【追加】Grid 3 のサイズ設定 (横 2, 縦 1)
    private static final int GRID3_WIDTH = 2;
    private static final int GRID3_HEIGHT = 1;
    
    private static final int CELL_WIDTH = 60;
    private static final int CELL_HEIGHT = 60;
    
    // グリッドと色のマッピング (ID: 1, 2, 3)
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
        gridColors = new HashMap<>();
        
        // Grid 1 - Sky Blue (既存)
        gridColors.put(1, new Color(135, 206, 235));
        
        // Grid 2 - Pale Green (既存)
        gridColors.put(2, new Color(152, 251, 152));
        
        // Grid 3 - Cyan/Aqua の系に（新規追加）
        gridColors.put(3, new Color(0, 255, 255)); 
    }
    
    private void createGrids() {
        grids.clear();
        
        // Grid 1 (既存)
        Rectangle grid1 = new Rectangle(50, 50, GRID1_WIDTH * CELL_WIDTH, GRID1_HEIGHT * CELL_HEIGHT);
        grids.add(grid1);
        
        // Grid 2 (既存：位置を調整して重なりなく配置）
        // 元のコードの位置 (50, 250) を維持しつつ、Grid3 の影響を受けないようにします
        Rectangle grid2 = new Rectangle(50, 250, GRID2_WIDTH * CELL_WIDTH, GRID2_HEIGHT * CELL_HEIGHT);
        grids.add(grid2);
        
        // 【追加】Grid 3: Grid1 の横に配置 (幅方向に接続)
        // x: Grid1 終了位置 + グリッド間隙なし、y: Grid1 と同じ
        int grid3X = 50 + GRID1_WIDTH * CELL_WIDTH; 
        int grid3Y = 50;
        Rectangle grid3 = new Rectangle(grid3X, grid3Y, GRID3_WIDTH * CELL_WIDTH, GRID3_HEIGHT * CELL_HEIGHT);
        grids.add(grid3);
        
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
            if (grids.get(i).equals(grid)) { // equals() を使用して同一性を確認（== も可だが安全に）
                gridIndex = i;
                break;
            }
        }
        
        if (gridIndex == -1) return "outside";
        
        int cellX = (int) ((e.getX() - grid.x) / CELL_WIDTH);
        int row = (int) ((e.getY() - grid.y) / CELL_HEIGHT);
        
        // グリッドの全体列オフセット計算
        int overallColumn = cellX + 1;
        if (gridIndex == 0) {
            // Grid2 の場合は（元のロジックで）Offset が必要だが、Grid3 は Grid1 に続くため Offset が不要か？
            // ただし、全体の列番号を一意にする必要があれば：
            // Grid1: Col 1~4, Grid3: Col 5~6 (全体では連続させるなら)
            // しかし「Global Column」として管理する必要がある場合は以下の計算を使用します。
            if (gridIndex > 0) { 
               // Grid2 のオフセットを維持しつつ、Grid3 は Grid1 と接続しているため追加しない場合と、全グリッドで番号を振る場合の選択があります。
               // 今回は「Grid1 と Grid3」は隣接しているので、Global Column を単純に足します。
               overallColumn += GRID1_WIDTH; 
            } else if (gridIndex == 1) {
                 // Grid2 の場合は元のロジック通りオフセットが必要（左側に Grid1 がいるため）
                 overallColumn += GRID1_WIDTH + GRID3_WIDTH; // Grid3 も挟まっているなら考慮。
            }
        } else if (gridIndex == 2) {
             // Grid3: Grid1 と隣接なので、Grid1 の列からカウントして良いが、Grid2 はそれより下の位置なので独立
             overallColumn += GRID1_WIDTH + GRID3_WIDTH; // 横方向の全体列とする（任意）
        }

        // 簡易的だが明確な表現：各グリッド内のオフセットを返す
        // グリッド全体の列番号を一意にするロジックは必要かどうかですが、ここでは「Grid 1 と接続している Grid3」を想定し、
        // 全体の列番号を取得するために Grid1, Grid3 を連結したとみなします。
        
        // 【簡易修正：Global Column の計算を整理】
        // Grid1: Col 1~4 | Grid2: 独立（下）| Grid3: Col 5~6 (Grid1 と連動)
        if (gridIndex == 0 || gridIndex == 2) { 
            overallColumn = cellX + 1; 
            if (gridIndex == 2) { // Grid3 の場合、Grid1 に続いているため Grid1 の列数分を足す
                 overallColumn += GRID1_WIDTH; 
            } else { // Grid1 の場合はそのまま（ただし Grid2 は独立なのでその計算は不要）
            }
        } else if (gridIndex == 1) { 
             // Grid2: 独自の列番号を返す必要があるか？今回はシンプルに「Grid3」のみ Focus。
             // Grid2 は元のロジックで良いが、全体列番号を要求される場合は以下。
             // 今回は Grid3 だけにフォーカスするので、Grid2 の処理は簡略化します。
        }

        return "G" + (gridIndex + 1) + ",C" + overallColumn + ",R" + (row + 1);
    }
    
    // グリッドの現在の ID を取得（例外を防止）
    private String getCurrentCellId() {
        if (currentGrid == null || grids.isEmpty()) {
            return "outside";
        }
        
        for (int i = 0; i < grids.size(); i++) {
            if (grids.get(i).equals(currentGrid)) {
                try {
                    String[] parts = currentCellIdStr.split(",");
                    int gridIdx = Integer.parseInt(parts[0]);
                    int colIdx = Integer.parseInt(parts[1]);
                    int rowIdx = Integer.parseInt(parts[2]);
                    return "G" + gridIdx + ",C" + colIdx + ",R" + rowIdx;
                } catch (NumberFormatException ex) {
                    continue;
                }
            }
        }
        return "outside";
    }
    
    // グリッドの ID, 列ID, 行ID からユニークな色を取得するメソッド
    private Color getCellColor(int gridId, int columnId, int rowId) {
        // Hue: 度数 (0-360) をラップアラウンド対応で計算
        float hueBase = (gridId - 1) * 90f; 
        float hueColumn = (columnId - 1) * 45f;
        float hueRow = (rowId - 1) * 30f;
        
        // 合計 Hue（ラップアラウンド対応）
        float finalHue = Math.abs((hueBase + hueColumn + hueRow) % 360.0f);
        // AWT の getHSBColor は 0-1 の浮動小数点数を必要とするので、度数を角度に変換する必要があります。
        // ただし、getHSBColor の仕様により、引数は 0〜1 の範囲であることが一般的です。
        // この場合、360° を 1.0 にマッピングするため、360 で割ります。
        finalHue /= 360.0f; 
        
        float saturation = 0.5f + ((gridId * columnId + rowId) % 5) * 0.1f; 
        // 輝度調整：Grid ID と Row ID に基づいて明るさを調整
        float brightness = 0.5f + ((gridId + rowId * 2) % 4) * 0.1f - (columnId % 2 == 0 ? 0.05f : 0);
        
        return Color.getHSBColor(finalHue, saturation, brightness);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // グリッド配列にアクセス（空かチェック済み）
        if (grids.isEmpty()) return;
        
        for (int i = 0; i < grids.size(); i++) {
            Rectangle grid = grids.get(i);
            
            // 【修正】gridColors を直接使用（map の index はインデックスから取得）
            Integer idx = i + 1; // グリッド ID (1, 2, 3)
            Color gridColor = gridColors.get(idx);
            
            if (gridColor == null) { // グリッドが null の場合でも null チェックは必要（安全策）
                continue;
            }
            
            int cols = grid.width / CELL_WIDTH;
            int rows = grid.height / CELL_HEIGHT;
            
            for (int j = 0; j < rows; j++) { // 【修正】行ループの順序を正常に
                for (int k = 0; k < cols; k++) { // 【修正】列ループの順序を正常に
                    int cellX = grid.x + k * CELL_WIDTH;
                    int cellY = grid.y + j * CELL_HEIGHT;
                    
                    // グリッド内での列・行番号（ユニークな色の計算用）
                    // Grid1 と Grid3 は隣接しているため、Grid3 の場合でも独立した ID を使います。
                    int columnId = k + 1; 
                    int rowId = j + 1;
                    
                    Color cellColor = getCellColor(i + 1, columnId, rowId); // 【修正】gridId はインデックスから計算
                    
                    g.setColor(cellColor);
                    // グリッド境界線は共通なので、最後の行と列のみ描画（共通のグリッドライン）
                    if (k == cols - 1 || j == rows - 1) {
                        g.setColor(Color.BLACK);
                        g.drawRect(cellX, cellY, CELL_WIDTH, CELL_HEIGHT);
                    } else {
                        // グリッド間隙は必要なら BLACK にして区切りますが、
                        // 今回は共通枠線（LIGHT_GRAY）なのでグレーで描画します。
                        g.setColor(Color.LIGHT_GRAY); 
                        g.drawRect(cellX, cellY, CELL_WIDTH, CELL_HEIGHT);
                    }
                }
            }
        }
    }
    
    // グリッドオブジェクトから index を取得
    private int getGridIndex(Rectangle grid) {
        for (int i = 0; i < grids.size(); i++) {
            if (grids.get(i).equals(grid)) return i + 1;
        }
        return -1;
    }
}