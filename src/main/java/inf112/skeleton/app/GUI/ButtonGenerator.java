package inf112.skeleton.app.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ButtonGenerator {
    private static final Color  fontColorNormal     = new Color(0f, 0f, 0f, 1f),
            fontColorPressed    = new Color(30f, 30f, 30f, 1f);
    private static final float  fontScale           = 3f;
    private static final String buttonStyleLink = "textButtonStyle.png";
    Drawable textButtonBackground;
    TextButton.TextButtonStyle textButtonStyle;
    BitmapFont font;

    public ButtonGenerator() {

        // Font for the text in our button
        font = new BitmapFont();
        font.getData().setScale(fontScale);

        textButtonBackground =
                new TextureRegionDrawable(new TextureRegion(
                        new Texture(Gdx.files.internal(buttonStyleLink))));

        // Button design
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = fontColorNormal;
        textButtonStyle.downFontColor = fontColorPressed;
        textButtonStyle.down = textButtonBackground;
        textButtonStyle.up = textButtonBackground;
    }

    public TextButton generate(String displayText) {
        return new TextButton(displayText, textButtonStyle);
    }
}
