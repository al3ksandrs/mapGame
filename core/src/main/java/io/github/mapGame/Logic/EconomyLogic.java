package io.github.mapGame.Logic;

import com.badlogic.gdx.graphics.Color;

import java.util.List;

import io.github.mapGame.Objects.Hex;

public class EconomyLogic {
    private int lastIncome = 0;

    public int calculateCurrentIncome(List<Hex> hexGrid) {
        int totalIncome = 0;
        for (Hex hex : hexGrid) {
            if (hex.getColor().equals(Color.RED)) { // Assuming player's color is RED
                totalIncome += hex.getTax();
            }
        }
        return totalIncome;
    }

    public int getLastIncome() {
        return lastIncome;
    }

    public void setLastIncome(int lastIncome) {
        this.lastIncome = lastIncome;
    }
}
