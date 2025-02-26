package io.github.mapGame.Logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

import io.github.mapGame.Objects.Hex;
import io.github.mapGame.Logic.HexLogic;
import io.github.mapGame.Logic.UnitLogic;

public class MapLogic {
    HexLogic hexLogic = new HexLogic();
    UnitLogic unitLogic = new UnitLogic();

    public List<Hex> generateHexGrid(int width, int height) {
        List<Hex> grid = new ArrayList<>();
        for (int q = -width; q <= width; q++) {
            for (int r = -height; r <= height; r++) {
                if (Math.abs(q + r) <= width) {
                    grid.add(new Hex(q, r, Color.GRAY, 0,0));
                }
            }
        }
        return grid;
    }
}
