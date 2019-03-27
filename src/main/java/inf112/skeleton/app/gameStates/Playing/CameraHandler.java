package inf112.skeleton.app.gameStates.Playing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import inf112.skeleton.app.Action.InputContainer;

import static java.lang.Math.max;
import static java.lang.Math.min;


public class CameraHandler {
    private OrthographicCamera camera;
    private InputContainer inputContainer;
    private int baseCameraMovementSpeed = 10;
    private float   scrollAmount        = 0.25f,
                    zoomAmount          = 0.083f,
                    zoomMin             = 0.5f,
                    zoomMax             = 5.0f;

    private static int  K_PLUS          = 70,
                        K_MINUS         = 69,
                        K_LEFT          = 21,
                        K_UP            = 19,
                        K_RIGHT         = 22,
                        K_DOWN          = 20,
                        K_SHIFT_LEFT    = 59;


    public CameraHandler(OrthographicCamera camera, InputContainer inputContainer) {
        this.camera = camera;
        this.inputContainer = inputContainer;
    }

    // Helper functions (should probably not be here)
    private Boolean isPressed(int key) {
        return this.inputContainer.keys[key];
    }

    public void handle() {
        handleKeys();
        handleMouseMovement();
        handleScroll();
        camera.update();
    }


    private void handleKeys() {
        int speedMultiplier;

        // Misc future keys


        // Misc camera functions
        speedMultiplier = isPressed(K_SHIFT_LEFT) ? 3 : 1;
        if (isPressed(K_PLUS)) {        // ZOOM IN
            camera.zoom = max(camera.zoom - zoomAmount, zoomMin);
        }
        if (isPressed(K_MINUS)) {       // ZOOM OUT
            camera.zoom = min(camera.zoom + zoomAmount, zoomMax);
        }

        // Camera movement
        if (isPressed(K_UP)) {
            camera.translate(0, baseCameraMovementSpeed * speedMultiplier);
        }
        if (isPressed(K_DOWN)) {
            camera.translate(0, -baseCameraMovementSpeed * speedMultiplier);
        }
        if (isPressed(K_LEFT)) {
            camera.translate(-baseCameraMovementSpeed * speedMultiplier, 0);
        }
        if (isPressed(K_RIGHT)) {
            camera.translate(baseCameraMovementSpeed * speedMultiplier, 0);
        }
    }

    private void handleScroll() {
        int scrollMultiplier = inputContainer.scrollAmount;
        if (scrollMultiplier != 0) {
            inputContainer.scrollAmount = 0;
            camera.zoom = min(max(camera.zoom + scrollAmount * scrollMultiplier, zoomMin), zoomMax);
            System.out.println(camera.zoom);
        }
    }

    private void handleMouseMovement() {
        if (Gdx.input.isTouched()) {
            camera.translate((-Gdx.input.getDeltaX()) * camera.zoom, (Gdx.input.getDeltaY()) * camera.zoom);
        }
    }
}
