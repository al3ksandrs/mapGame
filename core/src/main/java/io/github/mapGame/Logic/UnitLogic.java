package io.github.mapGame.Logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

import io.github.mapGame.Objects.Hex;
import io.github.mapGame.Logic.AxialLogic;
import io.github.mapGame.Objects.Unit;

public class UnitLogic {
    AxialLogic axialLogic = new AxialLogic();
    private List<Unit> units;
    private Unit selectedUnit;

    public boolean isWithinDistance(Hex start, Hex target, int maxDistance) {
        int distance = axialLogic.axialDistance(start.getQ(), start.getR(), target.getQ(), target.getR());
        return distance <= maxDistance;
    }

    public List<Unit> getUnitsAtHex(int q, int r) {
        List<Unit> unitsAtHex = new ArrayList<>();
        for (Unit unit : units) {
            if (unit.getQ() == q && unit.getR() == r) {
                unitsAtHex.add(unit);
            }
        }
        return unitsAtHex;
    }

    public void drawUnits(SpriteBatch batch, BitmapFont font, AxialLogic axialLogic, float hexSize, CameraLogic cameraLogic, GlyphLayout glyphLayout) {
        for (Unit unit : units) {
            Vector2 pos = axialLogic.axialToPixel(unit.getQ(), unit.getR(), hexSize * cameraLogic.getZoomFactor());
            Sprite icon = unit.getUnitIcon();
            icon.setPosition(pos.x - icon.getWidth() / 2, pos.y - icon.getHeight() / 2);
            icon.draw(batch);

            // Draw strength text
            font.setColor(Color.WHITE);
            String strength = Integer.toString(unit.getUnitStrength());
            glyphLayout.setText(font, strength);
            float textWidth = glyphLayout.width;
            float textHeight = glyphLayout.height;
            font.draw(batch, strength, pos.x - textWidth / 2, pos.y + icon.getHeight() / 2 + textHeight);
        }
    }

    public void handleMerge(Hex targetHex, Unit selectedUnit, List<Unit> units) {
        List<Unit> unitsInHex = getUnitsAtHex(targetHex.getQ(), targetHex.getR());
        List<Unit> friendlyUnits = new ArrayList<>();
        for (Unit u : unitsInHex) {
            if (u.getColor().equals(selectedUnit.getColor())) {
                friendlyUnits.add(u);
            }
        }
        friendlyUnits.add(selectedUnit); // Include the selected unit in the merge

        if (friendlyUnits.size() >= 1) {
            int totalStrength = 0;
            int maxMarch = 0;
            for (Unit u : friendlyUnits) {
                totalStrength += u.getUnitStrength();
                maxMarch = Math.max(maxMarch, u.getMarchDistance());
            }
            units.removeAll(friendlyUnits);
            Unit mergedUnit = new Unit(
                targetHex.getQ(), // Set to target hex's Q
                targetHex.getR(), // Set to target hex's R
                selectedUnit.getColor(),
                totalStrength,
                maxMarch,
                "icons/infantry.png"
            );
            mergedUnit.setMoved(true);
            units.add(mergedUnit);
        }
    }

    public List<Unit> getUnits() {
        return units;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }

    public Unit getSelectedUnit() {
        return selectedUnit;
    }

    public void setSelectedUnit(Unit selectedUnit) {
        this.selectedUnit = selectedUnit;
    }
}
