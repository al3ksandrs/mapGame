package io.github.mapGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.List;

public class HexGridGame extends ApplicationAdapter implements InputProcessor {
    private ShapeRenderer shapeRenderer;
    private List<Hex> hexGrid;
    private List<Unit> units;
    private Unit selectedUnit;
    private float hexSize = 100;
    private float zoomFactor = 1.0f; // Zoom factor
    private Vector2[] hexagonVertices;

    private static final int[][] directions = {
        {1, 0}, {1, -1}, {0, -1},
        {-1, 0}, {-1, 1}, {0, 1}
    };
    private Stage stage;
    private SpriteBatch batch;
    private Skin skin;

    private Vector2 cameraOffset = new Vector2(0, 0);
    private Vector2 lastTouch = new Vector2();
    private boolean dragging = false;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        hexGrid = generateHexGrid(4, 4);
        units = new ArrayList<>();
        Unit initialUnit = new Unit(1, 1, Color.RED, 1, 2, "icons/infantry.png");
        Unit initialUnit2 = new Unit(2, 2, Color.RED, 1, 2, "icons/infantry.png");
        units.add(initialUnit);
        units.add(initialUnit2);
        // Set the initial hex's color to match the unit's color
        Hex startingHex = findHex(initialUnit.getQ(), initialUnit.getR());
        if (startingHex != null) {
            startingHex.setColor(initialUnit.getColor());
        }
        stage = new Stage();
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage); // Stage handles UI input
        multiplexer.addProcessor(this);  // HexGridGame handles game input
        Gdx.input.setInputProcessor(multiplexer);
        precomputeHexagonVertices();
        createNextTurnButton();
    }

    private void createNextTurnButton() {
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        TextButton nextTurnButton = new TextButton("Next Turn", skin);
        nextTurnButton.setSize(150, 50);
        nextTurnButton.setPosition(Gdx.graphics.getWidth() - 300, 100);
        nextTurnButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                nextTurn();
            }
        });
        stage.addActor(nextTurnButton);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw hexagons
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Hex hex : hexGrid) {
            if (reachableHexes.contains(hex)) {
                shapeRenderer.setColor(Color.YELLOW); // Highlight color
            } else {
                shapeRenderer.setColor(hex.getColor());
            }
            fillHexagon(hex.getQ(), hex.getR());
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        for (Hex hex : hexGrid) {
            drawHexagon(hex.getQ(), hex.getR());
        }
        shapeRenderer.end();

        // Draw unit icons
        batch.begin();
        for (Unit unit : units) {
            Vector2 pos = axialToPixel(unit.getQ(), unit.getR(), hexSize * zoomFactor);
            Sprite icon = unit.getUnitIcon();
            icon.setPosition(pos.x - icon.getWidth() / 2, pos.y - icon.getHeight() / 2); // Center the icon
            icon.draw(batch);
        }
        batch.end();

        // Draw UI
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    private void precomputeHexagonVertices() {
        hexagonVertices = new Vector2[6];
        for (int i = 0; i < 6; i++) {
            double angle = 2 * Math.PI / 6 * i;
            float x = hexSize * (float) Math.cos(angle);
            float y = hexSize * (float) Math.sin(angle);
            hexagonVertices[i] = new Vector2(x, y);
        }
    }

    private List<Hex> generateHexGrid(int width, int height) {
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

    private Vector2 axialToPixel(int q, int r, float size) {
        float x = size * (3f / 2 * q);
        float y = size * ((float) Math.sqrt(3) * (r + q / 2f));
        return new Vector2(x + Gdx.graphics.getWidth() / 2 + cameraOffset.x, y + Gdx.graphics.getHeight() / 2 + cameraOffset.y);
    }

    private void fillHexagon(int q, int r) {
        Vector2 center = axialToPixel(q, r, hexSize * zoomFactor);
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

    private void drawHexagon(int q, int r) {
        Vector2 center = axialToPixel(q, r, hexSize * zoomFactor);
        for (int i = 0; i < 6; i++) {
            Vector2 start = hexagonVertices[i].cpy().add(center);
            Vector2 end = hexagonVertices[(i + 1) % 6].cpy().add(center);
            shapeRenderer.line(start, end);
        }
    }

    private Hex getHexAtPosition(Vector2 position) {
        for (Hex hex : hexGrid) {
            Vector2 hexPos = axialToPixel(hex.getQ(), hex.getR(), hexSize * zoomFactor);
            if (position.dst(hexPos) < hexSize * zoomFactor) {
                return hex;
            }
        }
        return null;
    }

    private List<Hex> getReachableHexes(Unit unit, int marchDistance) {
        List<Hex> reachableHexes = new ArrayList<>();
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
                    int distance = axialDistance(startHex.getQ(), startHex.getR(), neighbor.getQ(), neighbor.getR());

                    // Check if the neighbor is within marchDistance
                    if (distance <= marchDistance) {
                        // If the neighbor is the same color, add it to the queue
                        if (neighbor.getColor().equals(unitColor)) {
                            queue.add(neighbor);
                            visited.add(neighbor);
                        }
                        // If the neighbor is a different color, allow movement only if it's exactly one hex away
                        else if (distance == 1) {
                            reachableHexes.add(neighbor); // Add to reachable hexes but don't explore further
                            visited.add(neighbor);
                        }
                    }
                }
            }
        }
        return reachableHexes;
    }

    private Hex findHex(int q, int r) {
        for (Hex hex : hexGrid) {
            if (hex.getQ() == q && hex.getR() == r) {
                return hex;
            }
        }
        return null;
    }

    private boolean isWithinDistance(Hex start, Hex target, int maxDistance) {
        int distance = axialDistance(start.getQ(), start.getR(), target.getQ(), target.getR());
        return distance <= maxDistance;
    }

    private int axialDistance(int q1, int r1, int q2, int r2) {
        int dq = q1 - q2;
        int dr = r1 - r2;
        return (Math.abs(dq) + Math.abs(dr) + Math.abs(dq + dr)) / 2;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    private List<Hex> reachableHexes = new ArrayList<>();

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        dragging = true;
        lastTouch.set(screenX, screenY);
        Vector2 touchPos = new Vector2(screenX, Gdx.graphics.getHeight() - screenY);
        Hex touchedHex = getHexAtPosition(touchPos);
        if (touchedHex != null) {
            if (selectedUnit == null) {
                for (Unit unit : units) {
                    if (!unit.hasMoved() && unit.getQ() == touchedHex.getQ() && unit.getR() == touchedHex.getR()) {
                        selectedUnit = unit;
                        reachableHexes = getReachableHexes(selectedUnit, selectedUnit.getMarchDistance());
                        break;
                    }
                }
            } else {
                if (!selectedUnit.hasMoved() && reachableHexes.contains(touchedHex)) {
                    selectedUnit.moveTo(touchedHex.getQ(), touchedHex.getR());
                    // If the hex is not the same color as the unit, capture it
                    if (!touchedHex.getColor().equals(selectedUnit.getColor())) {
                        touchedHex.setColor(selectedUnit.getColor());
                    }
                    selectedUnit.setMoved(true);
                    selectedUnit = null;
                    reachableHexes.clear(); // Clear highlights after moving
                }
            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        dragging = false;
        return true;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (dragging) {
            float dx = screenX - lastTouch.x;
            float dy = screenY - lastTouch.y;
            cameraOffset.add(dx, -dy);
            clampCameraOffset(); // Clamp the camera offset
            lastTouch.set(screenX, screenY);
        }
        return true;
    }

    private void clampCameraOffset() {
        float minX = -Gdx.graphics.getWidth() / 2;
        float maxX = Gdx.graphics.getWidth() / 2;
        float minY = -Gdx.graphics.getHeight() / 2;
        float maxY = Gdx.graphics.getHeight() / 2;

        cameraOffset.x = Math.max(minX, Math.min(maxX, cameraOffset.x));
        cameraOffset.y = Math.max(minY, Math.min(maxY, cameraOffset.y));
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // Adjust zoom factor based on scroll input
        zoomFactor += amountY * 0.1f; // Adjust the zoom speed as needed
        zoomFactor = Math.max(0.5f, Math.min(2.0f, zoomFactor)); // Clamp zoom factor
        return true;
    }

    private boolean isAdjacent(int q1, int r1, int q2, int r2) {
        int[][] directions = { {1, 0}, {0, 1}, {-1, 1}, {-1, 0}, {0, -1}, {1, -1} };
        for (int[] dir : directions) {
            if (q1 + dir[0] == q2 && r1 + dir[1] == r2) return true;
        }
        return false;
    }

    public void nextTurn() {
        for (Unit unit : units) {
            unit.setMoved(false);
        }
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        for (Unit unit : units) {
            unit.getUnitIcon().getTexture().dispose(); // Dispose of the unit's texture
        }
        stage.dispose();
    }
}
