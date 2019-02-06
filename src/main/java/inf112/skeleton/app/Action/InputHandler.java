package inf112.skeleton.app.Action;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class InputHandler {
    boolean pressingRight = false;
    boolean pressingLeft = false;
    boolean pressingUp = false;
    boolean pressingDown = false;
    boolean pressingShift = false;
    private OrthographicCamera camera;

    public InputHandler(OrthographicCamera camera) {
        this.camera = camera;
    }

    public void pressKey(int key) {
        switch (key){
            case Keys.UP:
                pressingUp = true;
                break;
            case Keys.DOWN:
                pressingDown = true;
                break;
            case Keys.LEFT:
                pressingLeft = true;
                break;
            case Keys.RIGHT:
                pressingRight = true;
                break;
            case Keys.SHIFT_LEFT:
                pressingShift = true;
                break;
        }
    }

    public void releseKey(int key) {
        switch (key){
            case Keys.UP:
                pressingUp = false;
                break;
            case Keys.DOWN:
                pressingDown = false;
                break;
            case Keys.LEFT:
                pressingLeft = false;
                break;
            case Keys.RIGHT:
                pressingRight = false;
                break;
            case Keys.SHIFT_LEFT:
                pressingShift = false;
                break;
        }
    }

    public void handleKeys() {
        int acceleration = 1;
        if(pressingShift)
            acceleration = 3;
        if(pressingUp) {
            camera.translate(0, 10*acceleration);
        } else if (pressingDown) {
            camera.translate(0,-10*acceleration);
        }
        if(pressingRight) {
            camera.translate(10*acceleration,0);
        } else if (pressingLeft) {
            camera.translate(-10*acceleration,0);
        }
    }
}
