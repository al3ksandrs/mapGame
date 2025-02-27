package io.github.mapGame.Logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import java.util.List;

import io.github.mapGame.Objects.Hex;
import io.github.mapGame.Objects.Player;

public class EconomyLogic {
    private int lastIncome = 0;
    private int playerIncome = 0;

    public int calculateCurrentIncome(List<Hex> hexGrid) {
        playerIncome = 0;
        for (Hex hex : hexGrid) {
            if (hex.getColor().equals(Color.RED)) { // Assuming player's color is RED
                playerIncome += hex.getTax();
            }
        }
        return playerIncome;
    }

    public int getLastIncome() {
        return lastIncome;
    }

    public void setLastIncome(int lastIncome) {
        this.lastIncome = lastIncome;
    }
}
