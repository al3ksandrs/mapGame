package io.github.mapGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.List;

import io.github.mapGame.Objects.Hex;
import io.github.mapGame.Objects.Player;
import io.github.mapGame.Objects.Unit;
import io.github.mapGame.Logic.AxialLogic;
import io.github.mapGame.Logic.CameraLogic;
import io.github.mapGame.Logic.EconomyLogic;
import io.github.mapGame.Logic.HexLogic;
import io.github.mapGame.Logic.MapLogic;
import io.github.mapGame.Logic.UnitLogic;

public class HexGridGame extends ApplicationAdapter implements InputProcessor {
    EconomyLogic economyLogic = new EconomyLogic();
    AxialLogic axialLogic = new AxialLogic();
    CameraLogic cameraLogic = new CameraLogic();
    HexLogic hexLogic = new HexLogic();
    MapLogic mapLogic = new MapLogic();
    UnitLogic unitLogic = new UnitLogic();
    ShapeRenderer shapeRenderer = hexLogic.getShapeRenderer();
    List<Hex> hexGrid = hexLogic.getHexGrid();
    List<Unit> units = unitLogic.getUnits();
    Unit selectedUnit = unitLogic.getSelectedUnit();
    float zoomFactor = cameraLogic.getZoomFactor();
    private BitmapFont font;
    private GlyphLayout glyphLayout;

    private Stage stage;
    private SpriteBatch batch;
    private Skin skin;

    Vector2 cameraOffset = cameraLogic.getCameraOffset();
    private Vector2 lastTouch = new Vector2();
    private boolean dragging = false;

    private Player player;

    private int outlineWidth = 7;
    private Color outlineColor = Color.CYAN;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        hexLogic.setShapeRenderer(shapeRenderer);
        hexLogic.setCameraLogic(cameraLogic);
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(3f);
        glyphLayout = new GlyphLayout();
        hexGrid = mapLogic.generateHexGrid(4, 2);
        hexLogic.setHexGrid(hexGrid);
        units = new ArrayList<>();
        unitLogic.setUnits(units);
        player = new Player(0);
        Unit initialUnit = new Unit(1, 1, Color.RED, 3, 2, "icons/infantry.png");
        Unit initialUnit2 = new Unit(2, 2, Color.RED, 3, 2, "icons/infantry.png");
        units.add(initialUnit);
        units.add(initialUnit2);

        // Set the initial hex's color to match the unit's color
        for (Unit unit : units){
            Hex startingHex = hexLogic.findHex(unit.getQ(), unit.getR());
            if (startingHex != null) {
                startingHex.setColor(unit.getColor());
                startingHex.setTax(1);
            }
        }
        stage = new Stage();
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage); // Stage handles UI input
        multiplexer.addProcessor(this);  // HexGridGame handles game input
        Gdx.input.setInputProcessor(multiplexer);
        hexLogic.precomputeHexagonVertices();
        createBottomBar();
    }

    private void createBottomBar() {
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Create a table for the bottom bar
        Table bottomBar = new Table();
        bottomBar.setFillParent(true);
        bottomBar.bottom();

        // Add the "Next Turn" button to the bottom bar
        TextButton nextTurnButton = new TextButton("Next Turn", skin);
        nextTurnButton.getLabel().setFontScale(3f);
        nextTurnButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                nextTurn();
            }
        });

        // Add the button to the bottom bar
        bottomBar.add(nextTurnButton).pad(10).width(300).height(100);

        // Add the bottom bar to the stage
        stage.addActor(bottomBar);
    }

    @Override
    public void render() {
        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw hex grid
        hexLogic.drawHexGrid(shapeRenderer, reachableHexes);

        // Draw movement outlines
        if (selectedUnit != null) {
            Gdx.gl.glLineWidth(outlineWidth);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(outlineColor);
            hexLogic.outLineCalculation(reachableHexes);
            shapeRenderer.end();
            Gdx.gl.glLineWidth(1);
        }

        // Draw units
        batch.begin();
        unitLogic.drawUnits(batch, font, axialLogic, hexLogic.getHexSize(), cameraLogic, glyphLayout);
        // Draw UI elements
        font.draw(batch, "Money: " + player.getMoney(), 40, Gdx.graphics.getHeight() - 40);
        int currentIncome = economyLogic.calculateCurrentIncome(hexLogic.getHexGrid());
        font.draw(batch, "+" + currentIncome, 300, Gdx.graphics.getHeight() - 40);
        batch.end();

        stage.draw();
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

    List<Hex> reachableHexes = hexLogic.getReachableHexes();

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        dragging = true;
        lastTouch.set(screenX, screenY);
        Vector2 touchPos = new Vector2(screenX, Gdx.graphics.getHeight() - screenY);
        Hex touchedHex = hexLogic.getHexAtPosition(touchPos);

        if (touchedHex != null) {
            if (selectedUnit == null) {
                // Select a unit if it hasn't moved
                for (Unit unit : units) {
                    if (!unit.hasMoved() && unit.getQ() == touchedHex.getQ() && unit.getR() == touchedHex.getR()) {
                        selectedUnit = unit;
                        reachableHexes = hexLogic.getReachableHexes(selectedUnit, selectedUnit.getMarchDistance());
                        break;
                    }
                }
            } else {
                if (!selectedUnit.hasMoved() && reachableHexes.contains(touchedHex)) {
                    List<Unit> unitsInHex = unitLogic.getUnitsAtHex(touchedHex.getQ(), touchedHex.getR());
                    boolean hasFriendly = false;
                    for (Unit u : unitsInHex) {
                        if (u.getColor().equals(selectedUnit.getColor())) {
                            hasFriendly = true;
                            break;
                        }
                    }

                    if (hasFriendly) {
                        // Merge with friendly units
                        unitLogic.handleMerge(touchedHex, selectedUnit, units);
                    } else {
                        // Move the unit to the empty hex
                        selectedUnit.setQ(touchedHex.getQ());
                        selectedUnit.setR(touchedHex.getR());
                        selectedUnit.setMoved(true);
                        // Capture the hex
                        touchedHex.setColor(selectedUnit.getColor());
                        touchedHex.setTax(1);
                    }

                    // Deselect and clear highlights
                    selectedUnit = null;
                    reachableHexes.clear();
                } else {
                    // Deselect if tapping a non-reachable hex
                    selectedUnit = null;
                    reachableHexes.clear();
                }
            }
        } else {
            // Deselect if tapping outside any hex
            if (selectedUnit != null) {
                selectedUnit = null;
                reachableHexes.clear();
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
            cameraLogic.clampCameraOffset(); // Clamp the camera offset
            lastTouch.set(screenX, screenY);
        }
        return true;
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

    public void nextTurn() {
        for (Unit unit : units) {
            unit.setMoved(false);
        }
        // Add current income to player's money
        int currentIncome = economyLogic.calculateCurrentIncome(hexLogic.getHexGrid());

        Gdx.app.log("Economy", "Current Income: " + currentIncome);
        Gdx.app.log("Economy", "Money before: " + player.getMoney());

        player.setMoney(player.getMoney() + currentIncome);

        Gdx.app.log("Economy", "Money after: " + player.getMoney());
    }

    @Override
    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
        batch.dispose();
        for (Unit unit : units) {
            unit.getUnitIcon().getTexture().dispose();
        }
        stage.dispose();
    }
}
