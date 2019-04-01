package inf112.skeleton.app.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GraphicsLoader {
    public String[] playerColors = {"Blue", "DarkGreen", "Green", "Orange", "Pink", "Red", "Grey", "DarkYellow"};

    public String   folder_main = "graphics/",
                    folder_ui   = folder_main + "ui/",
                    folder_Buttons = folder_ui + "Buttons/",
                    folder_MainMenu = folder_ui + "MainMenu/",
                    folder_Lobby = folder_MainMenu + "Lobby/";

    public Skin     default_skin;

    public BitmapFont   default_font,
                        default_markup_font,
                        default_font_1p6,
                        default_font_1p7,
                        default_font_3p0;

    public Drawable logo,
                    btn_up,
                    btn_down,
                    btn_rounded_focused,
                    btn_rounded_unfocused,
                    btn_rounded_frozen,
                    mainMenu_h1,
                    mainMenu_h2,
                    mainMenu_body,
                    lobby_playerList_bg,
                    chatBox_bg;

    public Color    color_primary;

    public ImageTextButton.ImageTextButtonStyle btnStyle_rounded_focused,
                                                btnStyle_rounded_unfocused,
                                                btnStyle_rounded_frozen,
                                                btnStyle_players[];

    /**
     * Loads and store all graphics
     */
    public GraphicsLoader() {
        /*
         * Skins and fonts
         */
        default_skin = getSkin(folder_ui + "uiskin.json");
        default_font = default_skin.getFont("default-font");

        default_markup_font = getSkin(folder_ui + "uiskin.json").getFont("default-font");
        default_markup_font.getData().markupEnabled = true;

        default_font_1p6 = getSkin(folder_ui + "uiskin.json").getFont("default-font");
        default_font_1p6.getData().setScale(1.6f);

        default_font_1p7 = getSkin(folder_ui + "uiskin.json").getFont("default-font");
        default_font_1p7.getData().setScale(1.7f);

        default_font_3p0 = getSkin(folder_ui + "uiskin.json").getFont("default-font");
        default_font_3p0.getData().setScale(3f);


        /*
         * Drawables
         */
        logo = getDrawable(folder_ui + "robocop_logo_500W.png");
        btn_up = getDrawable(folder_ui + "triangleBlack.png");
        btn_down = getDrawable(folder_ui + "triangleBlackRot.png");

        btn_rounded_focused = getDrawable(folder_Buttons + "btn_rounded_focused.png");
        btn_rounded_unfocused = getDrawable(folder_Buttons + "btn_rounded_unfocused.png");
        btn_rounded_frozen = getDrawable(folder_Buttons + "btn_rounded_frozen.png");

        mainMenu_h1 = getDrawable(folder_MainMenu + "h1.png");
        mainMenu_h2 = getDrawable(folder_MainMenu + "h2.png");
        mainMenu_body = getDrawable(folder_MainMenu + "main.png");

        lobby_playerList_bg = getDrawable(folder_Lobby + "player_bg.png");

        chatBox_bg = getDrawable(folder_ui + "chatStyleOpac.png");


        /*
         * Colors
         */
        color_primary = new Color(0.6f,0.4f,0.2f,1);


        /*
         * Styles
         */
        btnStyle_rounded_focused    = styleFromDrawable(btn_rounded_focused, default_font, Color.BLACK);
        btnStyle_rounded_unfocused  = styleFromDrawable(btn_rounded_unfocused, default_font, Color.BLACK);
        btnStyle_rounded_frozen     = styleFromDrawable(btn_rounded_frozen, default_font, Color.BLACK);

        // Each player has their own robot-color, which is represented as a separate button in the lobby-tab
        btnStyle_players = new ImageTextButton.ImageTextButtonStyle[8];
        for (int i = 0; i < playerColors.length; i++) {
            btnStyle_players[i] = styleFromDrawable(getDrawable(folder_Lobby + "player_" + playerColors[i] + ".png"), default_font_1p6, Color.BLACK);
        }
    }

    /**
     * Craetes a new skin from a link
     * @param link path to skin-file
     * @return new skin
     */
    private Skin getSkin(String link) {
        return new Skin(Gdx.files.internal(link));
    }

    /**
     * Create a new drawable from a link
     * @param link path to drawable-file (Most often .png)
     * @return new drawable
     */
    public Drawable getDrawable(String link) {
        return new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(link))));
    }


    /**
     * Temporary function to create an ugly button.
     * @param text inside button
     * @return new button
     */
    TextButton generateTextButton(String text) {
        Drawable tmpD = getDrawable(folder_Buttons+ "textButtonStyle.png");
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(tmpD, tmpD, tmpD, default_font_3p0);
        textButtonStyle.fontColor = Color.BLACK;
        textButtonStyle.downFontColor = Color.WHITE;
        return new TextButton(text, textButtonStyle);
    }

    /**
     * Creates a new ImageTextButtonStyle from a drawable, given a font and a color
     * @param tmpD the given drawable
     * @param font text-font
     * @param color text-color
     * @return new ImageButtonStyle
     */
    public ImageTextButton.ImageTextButtonStyle styleFromDrawable(Drawable tmpD, BitmapFont font, Color color) {
        ImageTextButton.ImageTextButtonStyle tmpStyle = new ImageTextButton.ImageTextButtonStyle(tmpD, tmpD, tmpD, font);
        tmpStyle.fontColor = color;
        return tmpStyle;
    }
}
