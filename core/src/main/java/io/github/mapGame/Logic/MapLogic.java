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

//    public void outLineCalculation(){
//        for (Hex hex : hexLogic.getReachableHexes()) {
//            for (int[] dir : directions) {
//                int neighborQ = hex.getQ() + dir[0];
//                int neighborR = hex.getR() + dir[1];
//                Hex neighbor = hexLogic.findHex(neighborQ, neighborR);
//
//                // If the neighbor is not in the reachable hexes, draw this edge
//                if (neighbor == null || !reachableHexes.contains(neighbor)) {
//                    Vector2 center = axialLogic.axialToPixel(hex.getQ(), hex.getR(), hexSize * zoomFactor);
//                    int edgeIndex = hexLogic.getDirectionIndex(dir);
//                    Vector2 start = hexagonVertices[edgeIndex].cpy().add(center);
//                    Vector2 end = hexagonVertices[(edgeIndex + 1) % 6].cpy().add(center);
//                    shapeRenderer.line(start, end);
//                }
//            }
//        }
//    }
}
