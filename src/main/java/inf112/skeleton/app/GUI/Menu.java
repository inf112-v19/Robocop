package inf112.skeleton.app.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import inf112.skeleton.app.Action.Action;

public class Menu {
    Stage stage;
    Table buttons;
    ButtonGenerator btnGen;

    public Menu() {
        // New stage to display the menu buttons
        stage = new Stage();


        // Table of all buttons
        buttons = new Table();
        buttons.setFillParent(true); // Automatic resize and placement on screen
        buttons.setDebug(false);      // Display corners of table-cells/buttons
        //buttons.setBackground()
        stage.addActor(buttons);

        btnGen = new ButtonGenerator();
    }

    public void add(String displayText, final Action onClickAction) {
        TextButton button = btnGen.generate(displayText);

        buttons.add(button).width(350).padBottom(1).uniform();
        buttons.row();
        button.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        onClickAction.invoke();
                    }
                }
        );
    }


    public void render(SpriteBatch sb) {
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
    }

    public void focus() {
        // Give stage access to mouse and keyboard input
        Gdx.input.setInputProcessor(stage);
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }
}