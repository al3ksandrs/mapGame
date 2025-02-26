package io.github.mapGame.Logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

import io.github.mapGame.Objects.Hex;
import io.github.mapGame.Objects.Unit;

public class HexLogic {
    private int hexSize = 100;
    private Vector2[] hexagonVertices;
    private ShapeRenderer shapeRenderer;
    private static final int[][] directions = {
        {1, 0}, {1, -1}, {0, -1},
        {-1, 0}, {-1, 1}, {0, 1}
    };
    private List<Hex> hexGrid;
    private AxialLogic axialLogic = new AxialLogic();
    private CameraLogic cameraLogic = new CameraLogic();

    List<Hex> reachableHexes = new ArrayList<>();

    public void precomputeHexagonVertices() {
        hexagonVertices = new Vector2[6];
        for (int i = 0; i < 6; i++) {
            double angle = 2 * Math.PI / 6 * i;
            float x = hexSize * (float) Math.cos(angle);
            float y = hexSize * (float) Math.sin(angle);
            hexagonVertices[i] = new Vector2(x, y);
        }
    }

    public Hex getHexAtPosition(Vector2 position) {
        for (Hex hex : hexGrid) {
            Vector2 hexPos = axialLogic.axialToPixel(hex.getQ(), hex.getR(), hexSize * cameraLogic.getZoomFactor());
            if (position.dst(hexPos) < hexSize * cameraLogic.getZoomFactor()) {
                return hex;
            }
        }
        return null;
    }

    public void fillHexagon(int q, int r) {
        Vector2 center = axialLogic.axialToPixel(q, r, hexSize * cameraLogic.getZoomFactor());
        float[] vertices = new float[12];
        for (int i = 0; i < 6; i++) {
            vertices[i * 2] = hexagonVertices[i].x + center.x;
            vertices[i * 2 + 1] = hexagonVertices[i].y + center.y;
        }
        shapeRenderer.triangle(vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], vertices[5]);
        shapeRenderer.triangle(vertices[0], vertices[1], vertices[4], vertices[5], vertices[6], vertices[7]);
        shapeRenderer.triangle(vertices[0], vertices[1], vertices[6], vertices[7], vertices[8], vertices[9]);
        shapeRenderer.triangle(vertices[0], vertices[1], vertices[8], vertices[9], vertices[10], vertices[11]);
    }

    public void drawHexagon(int q, int r) {
        Vector2 center = axialLogic.axialToPixel(q, r, hexSize * cameraLogic.getZoomFactor());
        for (int i = 0; i < 6; i++) {
            Vector2 start = hexagonVertices[i].cpy().add(center);
            Vector2 end = hexagonVertices[(i + 1) % 6].cpy().add(center);
            shapeRenderer.line(start, end);
        }
    }

    public void drawHexGrid(ShapeRenderer shapeRenderer, List<Hex> reachableHexes) {
        // Draw filled hexagons
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Hex hex : hexGrid) {
            if (reachableHexes.contains(hex)) {
                shapeRenderer.setColor(new Color(1, 1, 0, 0.5f));
            } else {
                shapeRenderer.setColor(hex.getColor());
            }
            fillHexagon(hex.getQ(), hex.getR());
        }
        shapeRenderer.end();

        // Draw hex borders
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        for (Hex hex : hexGrid) {
            drawHexagon(hex.getQ(), hex.getR());
        }
        shapeRenderer.end();
    }

    public List<Hex> getReachableHexes(Unit unit, int marchDistance) {
        List<Hex> queue = new ArrayList<>();
        List<Hex> visited = new ArrayList<>();
        Hex startHex = findHex(unit.getQ(), unit.getR());
        if (startHex == null) return reachableHexes;

        Color unitColor = unit.getColor();
        queue.add(startHex);
        visited.add(startHex);

        while (!queue.isEmpty()) {
            Hex currentHex = queue.remove(0);
            reachableHexes.add(currentHex);

            for (int[] dir : directions) {
                int newQ = currentHex.getQ() + dir[0];
                int newR = currentHex.getR() + dir[1];
                Hex neighbor = findHex(newQ, newR);

                if (neighbor != null && !visited.contains(neighbor)) {
                    int distance = axialLogic.axialDistance(startHex.getQ(), startHex.getR(), neighbor.getQ(), neighbor.getR());

                    if (distance <= marchDistance) {
                        if (neighbor.getColor().equals(unitColor)) {
                            queue.add(neighbor);
                            visited.add(neighbor);
                        } else if (distance == 1) {
                            reachableHexes.add(neighbor);
                            visited.add(neighbor);
                        }
                    }
                }
            }
        }
        return reachableHexes;
    }


    public Hex findHex(int q, int r) {
        for (Hex hex : hexGrid) {
            if (hex.getQ() == q && hex.getR() == r) {
                return hex;
            }
        }
        return null;
    }

    public int getDirectionIndex(int[] dir) {
        for (int i = 0; i < directions.length; i++) {
            if (directions[i][0] == dir[0] && directions[i][1] == dir[1]) {
                return i;
            }
        }
        return -1;
    }

    public void outLineCalculation(List<Hex> reachableHexes) {
        for (Hex hex : reachableHexes) {
            for (int[] dir : directions) {
                int neighborQ = hex.getQ() + dir[0];
                int neighborR = hex.getR() + dir[1];
                Hex neighbor = findHex(neighborQ, neighborR);

                if (neighbor == null || !reachableHexes.contains(neighbor)) {
                    Vector2 center = axialLogic.axialToPixel(hex.getQ(), hex.getR(), hexSize * cameraLogic.getZoomFactor());
                    int edgeIndex = getDirectionIndex(dir);
                    Vector2 start = hexagonVertices[edgeIndex].cpy().add(center);
                    Vector2 end = hexagonVertices[(edgeIndex + 1) % 6].cpy().add(center);
                    shapeRenderer.line(start, end);
                }
            }
        }
    }


    public List<Hex> getHexGrid() {
        return hexGrid;
    }

    public void setHexGrid(List<Hex> hexGrid) {
        this.hexGrid = hexGrid;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public void setShapeRenderer(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
    }

    public Vector2[] getHexagonVertices() {
        return hexagonVertices;
    }

    public void setHexagonVertices(Vector2[] hexagonVertices) {
        this.hexagonVertices = hexagonVertices;
    }

    public int getHexSize() {
        return hexSize;
    }

    public void setHexSize(int hexSize) {
        this.hexSize = hexSize;
    }

    public List<Hex> getReachableHexes() {
        return reachableHexes;
    }

    public void setReachableHexes(List<Hex> reachableHexes) {
        this.reachableHexes = reachableHexes;
    }

}
