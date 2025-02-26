package io.github.mapGame.Objects;

import com.badlogic.gdx.graphics.Color;

import java.util.Objects;

// Hex.java
public class Hex {
    private int q, r; // Axial coordinates
    private Color color;

    private int tax;

    private int defence;

    public Hex(int q, int r, Color color, int tax, int defence) {
        this.q = q;
        this.r = r;
        this.color = color;
        this.tax = tax;
        this.defence = defence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hex hex = (Hex) o;
        return q == hex.q && r == hex.r;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r);
    }

    // Getters and setters
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    public int getQ() { return q; }
    public int getR() { return r; }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }
}
