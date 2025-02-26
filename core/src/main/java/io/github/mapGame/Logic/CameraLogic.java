package io.github.mapGame.Logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class CameraLogic {
    private Vector2 cameraOffset = new Vector2(0, 0);
    private float zoomFactor = 1.0f;

    public void clampCameraOffset() {
        float minX = -Gdx.graphics.getWidth() / 2;
        float maxX = Gdx.graphics.getWidth() / 2;
        float minY = -Gdx.graphics.getHeight() / 2;
        float maxY = Gdx.graphics.getHeight() / 2;

        cameraOffset.x = Math.max(minX, Math.min(maxX, cameraOffset.x));
        cameraOffset.y = Math.max(minY, Math.min(maxY, cameraOffset.y));
    }

    public Vector2 getCameraOffset() {
        return cameraOffset;
    }

    public void setCameraOffset(Vector2 cameraOffset) {
        this.cameraOffset = cameraOffset;
    }

    public float getZoomFactor() {
        return zoomFactor;
    }

    public void setZoomFactor(float zoomFactor) {
        this.zoomFactor = zoomFactor;
    }
}
