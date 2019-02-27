package inf112.skeleton.app.gameStates;

// Source: https://youtu.be/24p1Mvx6KFg


import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Iterator;
import java.util.Stack;

public class GameStateManager {
    private Stack<GameState> states;
    private Iterator<GameState> iterator;

    public GameStateManager() {
        states = new Stack<GameState>();
    }

    public void push(GameState state) {
        states.push(state);
        state.focus();
    }

    public void pop() {
        states.pop();
        states.peek().focus();
    }

    public void set(GameState state) {
        states.pop();
        states.push(state);
        state.focus();
    }

    public void update(float dt) {
        GameState currentState = states.peek();
        currentState.handleInput();
        currentState.update(dt);
    }

    public void render(SpriteBatch sb) {
        states.peek().render(sb);
    }

    public void dispose() {
        while (!states.empty()) {
            states.pop().dispose();
        }
    }

    public void resize(int width, int height) {
        iterator = states.iterator();
        while(iterator.hasNext()) {
            iterator.next().resize(width, height);
        }
    }
}
