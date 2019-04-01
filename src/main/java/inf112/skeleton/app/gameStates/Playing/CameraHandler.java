package inf112.skeleton.app.gameStates.Playing;

import com.badlogic.gdx.Gdx;
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


    /**
     * Initialize camera handler
     * @param camera of game
     * @param inputContainer storage of input-states (key-pressed, etc.)
     */
    public CameraHandler(OrthographicCamera camera, InputContainer inputContainer) {
        this.camera = camera;
        this.inputContainer = inputContainer;
    }

    /**
     * Check whether a key is pressed
     * @param key specific key to check
     * @return true if key pressed
     */
    private Boolean isPressed(int key) {
        return this.inputContainer.keys[key];
    }

    /**
     * Call subfunctions to handle input
     */
    public void handle() {
        handleKeys();
        handleMouseMovement();
        handleScroll();
        camera.update();
    }


    /**
     * Whenever a key is pressed, handle what happens
     */
    private void handleKeys() {
        int speedMultiplier = isPressed(K_SHIFT_LEFT) ? 3 : 1;

        // Misc camera functions

        // Zoom in (plus-key)
        if (isPressed(K_PLUS)) {
            camera.zoom = max(camera.zoom - zoomAmount, zoomMin);
        }

        // Zoom out (minus key)
        if (isPressed(K_MINUS)) {
            camera.zoom = min(camera.zoom + zoomAmount, zoomMax);
        }

        // Camera movement

        // Move camera based on keys (up, down, left, right) pressed
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

    /**
     * Zoom camera
     */
    private void handleScroll() {
        int scrollMultiplier = inputContainer.scrollAmount;
        if (scrollMultiplier != 0) {
            inputContainer.scrollAmount = 0;
            camera.zoom = min(max(camera.zoom + scrollAmount * scrollMultiplier, zoomMin), zoomMax);
        }
    }

    /**
     * Whenever the mouse is clicked while dragged across the screen, the camera should move.
     */
    private void handleMouseMovement() {
        if (Gdx.input.isTouched()) {
            camera.translate((-Gdx.input.getDeltaX()) * camera.zoom, (Gdx.input.getDeltaY()) * camera.zoom);
        }
    }
}
