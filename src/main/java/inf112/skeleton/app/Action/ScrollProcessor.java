package inf112.skeleton.app.Action;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class ScrollProcessor implements InputProcessor {
    OrthographicCamera camera;

    public ScrollProcessor(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    public boolean keyDown(int i) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        if (i != 0) {
            camera.zoom += i/4.0;
            if(camera.zoom >5.0) {
                camera.zoom = (float) 5.0;
            }
            if(camera.zoom <1.0) {
                camera.zoom = (float) 1.0;
            }
            camera.update();

            System.out.println(camera.zoom);
        }
        return false;
    }
}
