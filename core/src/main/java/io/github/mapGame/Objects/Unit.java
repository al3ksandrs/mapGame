package io.github.mapGame.Objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

// Unit.java
public class Unit {
    private int q, r;
    private Color color;

    private boolean moved;

    private int marchDistance;

    private int unitStrength;
    private Sprite unitIcon;

    public Unit(int q, int r, Color color, int unitStrength, int marchDistance, String iconPath) {
        this.q = q;
        this.r = r;
        this.color = color;
        this.moved = false;
        this.unitStrength = unitStrength;
        this.marchDistance = marchDistance;
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

    public void setQ(int q) {
        this.q = q;
    }

    public void setR(int r) {
        this.r = r;
    }

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

    public int getMarchDistance() {
        return marchDistance;
    }

    public void setMarchDistance(int marchDistance) {
        this.marchDistance = marchDistance;
    }
}
