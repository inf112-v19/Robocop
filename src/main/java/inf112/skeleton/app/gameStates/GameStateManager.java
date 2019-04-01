package inf112.skeleton.app.gameStates;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Stack;

public class GameStateManager {
    private Stack<GameState> states;

    /**
     * Initialize game-state manager
     */
    public GameStateManager() {
        states = new Stack<>();
    }

    /**
     * Add new game-state on-top of all others.
     * @param state new game-state
     */
    public void push(GameState state) {
        states.push(state);
    }

    /**
     * Remove the current game-state.
     */
    public void pop() {
        states.pop();
    }

    /**
     * Replace current game-state (if any) by another
     * @param state new game-state
     */
    public void set(GameState state) {
        try {
            states.pop();
        } catch (EmptyStackException e) {}
        states.push(state);
    }

    /**
     * get current game-state
     * @return current game-state
     */
    public GameState peek(){
        try {
            return states.peek();
        } catch (EmptyStackException e) {
            return null;
        }
    }

    /**
     * Update current game-state
     * @param dt delta-time, not used.
     */
    public void update(float dt) {
        try {
            states.peek().update(dt);
        } catch (EmptyStackException e) {}
    }

    /**
     * Render current game-state
     * @param sb sprite-batch
     */
    public void render(SpriteBatch sb) {
        try {
            states.peek().render(sb);
        } catch (EmptyStackException e) {}
    }

    /**
     * Remove and dispose of current game-state
     */
    public void dispose() {
        while (!states.empty()) {
            states.pop().dispose();
        }
    }

    /**
     * Notify each game-state that the window has been resized.
     */
    public void resize(int width, int height) {
        Iterator<GameState> iterator = states.iterator();
        while(iterator.hasNext()) {
            iterator.next().resize(width, height);
        }
    }
}
