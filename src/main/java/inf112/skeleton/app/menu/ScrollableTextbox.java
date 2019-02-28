package inf112.skeleton.app.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/*
 * Circular message-box.
 */
public class ScrollableTextbox {
    TextField[] lines;
    int         lineAmount,
                lineLimit;

    TextField.TextFieldStyle txtStyle;
    TextField emptyField;

    Stage stage;
    Table display;

    int displayFrom;

    ImageButton button_up;
    ImageButton button_down;


    int tableWidth = 600,
        tableHeight = 140;

    public ScrollableTextbox(int limit, InputMultiplexer inputMultiplexer) {
        lines = new TextField[limit];
        lineAmount = displayFrom = 0;
        lineLimit = limit;

        txtStyle = new TextField.TextFieldStyle();
        txtStyle.font = new BitmapFont();
        txtStyle.fontColor = Color.YELLOW;
        emptyField = new TextField("", txtStyle);

        stage = new Stage();
        inputMultiplexer.addProcessor(stage);

        display = new Table();
        //display.setFillParent(true); // Automatic resize and placement on screen
        display.setDebug(false);      // Display corners of table-cells/buttons
        display.setSize(tableWidth,tableHeight);

        button_up   = new ImageButton(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("triangleBlack.png")))));
        button_down = new ImageButton(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("triangleBlackRot.png")))));

        button_up.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        System.out.println("Hello dude");
                    }
                }
        );
        button_down.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        System.out.println("Hello dude");
                    }
                }
        );

        int arrowButtonSize = (tableHeight - 10) / 5;

        // Add empty cells to the table.
        display.add(emptyField).width(tableWidth-arrowButtonSize - 10).padBottom(1).padTop(3).uniform();
        display.add(button_up).width(arrowButtonSize).height(arrowButtonSize).padRight(4);
        display.row();
        for (int i = 0 ; i < 3 ; i++) {
            display.add(emptyField).width(tableWidth-arrowButtonSize - 10).padBottom(1).uniform();
            display.row();
        }
        display.add(emptyField).width(tableWidth-arrowButtonSize - 10).padBottom(4).uniform();
        display.add(button_down).width(arrowButtonSize).height(arrowButtonSize).padRight(4);
        display.row();
        display.background(new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("chatStyleOpac.png")))));


        stage.addActor(button_up);
        //inputContainer.addActor(button_down);
    }

    public void push(String str) {
        lines[lineAmount++ % lineLimit] = new TextField(str, txtStyle);
    }

    public void render(Batch sb) {
        display.draw(sb,1);
        button_up.draw(sb,1);
    }

    public void setPosition(int x, int y) {
        display.setPosition(x,y);
    }

}
