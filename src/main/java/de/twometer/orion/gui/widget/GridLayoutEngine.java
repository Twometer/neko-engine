package de.twometer.orion.gui.widget;

import de.twometer.orion.gui.core.Point;
import de.twometer.orion.gui.core.Size;

public class GridLayoutEngine {

    private final Grid grid;

    private final int[] rows;

    private final int[] cols;

    public GridLayoutEngine(Grid grid) {
        this.grid = grid;
        this.rows = new int[grid.rows.size()];
        this.cols = new int[grid.cols.size()];
    }

    public void recalculate() {
        int h = grid.getInternalSize().getHeight();
        int hn = 0;
        int w = grid.getInternalSize().getWidth();
        int wn = 0;

        for (int c = 0; c < cols.length; c++) {
            var def = grid.cols.get(c).value;
            if (def == Grid.GridDef.AUTO) {
                cols[c] = findWidestChildInCol(c);
            } else if (def != Grid.GridDef.FILL_REMAINING) {
                cols[c] = def;
            } else wn++;
            w -= cols[c];
        }

        for (int r = 0; r < rows.length; r++) {
            var def = grid.rows.get(r).value;
            if (def == Grid.GridDef.AUTO) {
                rows[r] = findHighestChildInRow(r);
            } else if (def != Grid.GridDef.FILL_REMAINING) {
                rows[r] = def;
            } else hn++;
            h -= rows[r];
        }

        if (hn == 0) hn++;
        if (wn == 0) wn++;

        int fillWidth = w / wn;
        int fillHeight = h / hn;
        for (int c = 0; c < cols.length; c++)
            if (grid.cols.get(c).value == Grid.GridDef.FILL_REMAINING)
                cols[c] = fillWidth;

        for (int r = 0; r < rows.length; r++)
            if (grid.rows.get(r).value == Grid.GridDef.FILL_REMAINING)
                rows[r] = fillHeight;
    }

    private int findHighestChildInRow(int row) {
        int highest = 0;
        for (var widget : grid.getChildren()) {
            var childRow = widget.getForeignProperty(Grid.class, "Row").intValue(0);
            var size = widget.getExternalSize();
            if (row == childRow && highest < size.getHeight())
                highest = size.getHeight();
        }
        return highest;
    }

    private int findWidestChildInCol(int col) {
        int widest = 0;
        for (var widget : grid.getChildren()) {
            var childCol = widget.getForeignProperty(Grid.class, "Col").intValue(0);
            var size = widget.getExternalSize();
            if (col == childCol && widest < size.getWidth())
                widest = size.getWidth();
        }
        return widest;
    }

    public void layout(WidgetBase widget) {
        var row = widget.getForeignProperty(Grid.class, "Row").intValue(0);
        var rowSpawn = widget.getForeignProperty(Grid.class, "RowSpan").intValue(1);
        var col = widget.getForeignProperty(Grid.class, "Col").intValue(0);
        var colSpan = widget.getForeignProperty(Grid.class, "ColSpan").intValue(1);

        widget.setPosition(getCellPosition(row, col));
        widget.setMaximumSize(getCellSize(row, col, rowSpawn, colSpan));
    }

    private Point getCellPosition(int r, int c) {
        int x = grid.getPosition().getX() + grid.getPadding().getWidth();
        int y = grid.getPosition().getY() + grid.getPadding().getHeight();
        for (int i = 0; i < c; i++)
            x += cols[i];
        for (int i = 0; i < r; i++)
            y += rows[i];
        return new Point(x, y);
    }

    private Size getCellSize(int r, int c, int rs, int cs) {
        int w = 0;
        int h = 0;
        for (int i = r; i < r + rs; i++) h += rows[i];
        for (int i = c; i < c + cs; i++) w += cols[i];
        return new Size(w, h);
    }

}
