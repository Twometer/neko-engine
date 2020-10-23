package de.twometer.orion.gui.widget;

import de.twometer.orion.gui.core.BindProperty;
import de.twometer.orion.gui.core.IPropertyDecoder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Grid extends ContainerBase {

    @BindProperty(decoder = GridDefDecoder.class)
    public List<GridDef> rows;

    @BindProperty(decoder = GridDefDecoder.class)
    public List<GridDef> cols;

    private GridLayoutEngine engine;

    public static class GridDef {

        public static final int AUTO = -1;
        public static final int FILL_REMAINING = -2;

        public int value;

        public GridDef(int value) {
            this.value = value;
        }

    }

    public static class GridDefDecoder implements IPropertyDecoder<List<GridDef>> {
        @Override
        public List<GridDef> decode(String str, Class<?> requiredType) {
            return Arrays.stream(str.split(",")).map(s -> new GridDef(parseGridDef(s))).collect(Collectors.toList());
        }

        private int parseGridDef(String s) {
            if (s.equals("*"))
                return GridDef.FILL_REMAINING;
            else if (s.equalsIgnoreCase("auto"))
                return GridDef.AUTO;
            else return Integer.parseInt(s);
        }
    }


    @Override
    public void onRelayout() {
        super.onRelayout();
        if (engine == null)
            engine = new GridLayoutEngine(this);

        relayoutChildren();

        engine.recalculate();
        for (var child : getChildren())
            engine.layout(child);
    }
}
