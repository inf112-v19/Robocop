package inf112.skeleton.app.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Temporary button generator.
 */
public class ButtonGenerator {
    private TextButton.TextButtonStyle textButtonStyle;

    /**
     * Sets style of the text buttons which should be generated.
     */
    public ButtonGenerator() {
        Drawable textButtonBackground = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("graphics/ui/textButtonStyle.png"))));

        textButtonStyle = new TextButton.TextButtonStyle(textButtonBackground, textButtonBackground, textButtonBackground, new BitmapFont());
        textButtonStyle.fontColor = Color.BLACK;
        textButtonStyle.downFontColor = Color.WHITE;
        textButtonStyle.font.getData().setScale(3f);
    }

    /**
     * Generates a text-button
     * @param displayText text displayed on button
     * @return text-button
     */
    public TextButton generate(String displayText) {
        return new TextButton(displayText, textButtonStyle);
    }
}
