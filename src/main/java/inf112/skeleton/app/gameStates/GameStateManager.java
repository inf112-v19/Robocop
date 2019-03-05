package inf112.skeleton.app.gameStates;

// Source: https://youtu.be/24p1Mvx6KFg


import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.EmptyStackException;
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
        try {
            states.pop();
        } catch (EmptyStackException e) {}
        states.push(state);
        state.focus();
    }
    public GameState peek(){
        return states.peek();
    }


    public void update(float dt) {
        try {
            GameState currentState = states.peek();
            currentState.handleInput();
            currentState.update(dt);
        } catch (EmptyStackException e) {}
    }

    public void render(SpriteBatch sb) {
        try {
            states.peek().render(sb);
        } catch (EmptyStackException e) {}
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
