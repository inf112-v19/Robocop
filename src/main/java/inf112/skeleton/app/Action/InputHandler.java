package inf112.skeleton.app.Action;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;

        /*
        Each key on the keyboard (with some exceptions) are represented in Keys.class as an int between 0 and 255.
        To update key status, you change the specific index. To check if a key is pressed,
        you look up the boolean value at the key's index.
        This approach makes it much easier to implement additional keys,
        as you only need to add an additional line to the handleKeys method.
        */
public class InputHandler implements InputProcessor {
    private boolean[] keys;
    private OrthographicCamera camera;

    public InputHandler(OrthographicCamera camera) {
        this.camera = camera;
        this.keys = new boolean[255];
    }

    public void handleKeys() {
        int movement = 10;
        int acceleration = 1;

        //Misc future keys


        //Misc camera functions
        if(keys[Keys.SHIFT_LEFT])
            acceleration = 3;
        if(keys[Keys.PLUS]) {   //ZOOM IN
            camera.zoom -= 1/12.0;
            if(camera.zoom < 1.0)
                camera.zoom = (float) 1.0;
        }
        if(keys[Keys.MINUS]) {  //ZOOM OUT
            camera.zoom += 1/12.0;
            if(camera.zoom > 5.0)
                camera.zoom = (float) 5.0;
        }

        //Camera movement
        if(keys[Keys.UP])
            camera.translate(0,movement*acceleration);
        else if(keys[Keys.DOWN])
            camera.translate(0,-movement*acceleration);
        if(keys[Keys.LEFT])
            camera.translate(-movement*acceleration,0);
        else if(keys[Keys.RIGHT])
            camera.translate(movement*acceleration,0);
    }

    @Override
    public boolean keyDown(int i) {
        keys[i] = true;
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        keys[i] = false;
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
