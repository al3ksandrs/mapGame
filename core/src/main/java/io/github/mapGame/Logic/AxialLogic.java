package io.github.mapGame.Logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class AxialLogic {
    private Vector2 cameraOffset = new Vector2(0, 0);

    public int axialDistance(int q1, int r1, int q2, int r2) {
        int dq = q1 - q2;
        int dr = r1 - r2;
        return (Math.abs(dq) + Math.abs(dr) + Math.abs(dq + dr)) / 2;
    }

    public Vector2 axialToPixel(int q, int r, float size) {
        float x = size * (3f / 2 * q);
        float y = size * ((float) Math.sqrt(3) * (r + q / 2f));
        return new Vector2(x + Gdx.graphics.getWidth() / 2 + cameraOffset.x, y + Gdx.graphics.getHeight() / 2 + cameraOffset.y);
    }
}
