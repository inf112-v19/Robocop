package inf112.skeleton.app.GUI;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import inf112.skeleton.app.RoboRally;

public class Message_ok extends Table {

    private final int   height_ok = 34,
                        width_ok = 100,
                        padTopBottom = 20;

    public boolean isOK = false;

    /**
     * Initialize message box with a clickable button for closing.
     * Note: When button clicked, a ChangeEvent will be fired. Check isOK to find if button is clicked.
     * @param message to display to user
     */
    public Message_ok(String message) {
        setBackground(RoboRally.graphics.messageBox_bg);

        add(new Label(message, RoboRally.graphics.labelStyle_markup_enabled)).pad(padTopBottom, 0, padTopBottom, 0).row();

        ImageTextButton okButton = new ImageTextButton("Got it!", RoboRally.graphics.btnStyle_rounded_focused);
        okButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                isOK = true;
            }
        });

        add(okButton).padBottom(5).size(width_ok, height_ok);
    }
}
