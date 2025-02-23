package io.github.mapGame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

// Unit.java
public class Unit {
    private int q, r;
    private Color color;

    private boolean moved;

    private int unitStrength;
    private Sprite unitIcon;

    public Unit(int q, int r, Color color, int unitStrength, String iconPath) {
        this.q = q;
        this.r = r;
        this.color = color;
        this.moved = false;
        this.unitStrength = unitStrength;
        this.unitIcon = new Sprite(new Texture(iconPath));
        this.unitIcon.setSize(100, 100);
    }

    public void moveTo(int newQ, int newR) {
        this.q = newQ;
        this.r = newR;
    }

    // Getters
    public Color getColor() { return color; }
    public int getQ() { return q; }
    public int getR() { return r; }

    public boolean hasMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public Sprite getUnitIcon() { return unitIcon; }

    public int getUnitStrength() {
        return unitStrength;
    }

    public void setUnitStrength(int unitStrength) {
        this.unitStrength = unitStrength;
    }
}
