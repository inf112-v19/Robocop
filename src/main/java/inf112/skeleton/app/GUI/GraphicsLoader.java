package inf112.skeleton.app.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import inf112.skeleton.common.specs.CardType;

import java.util.HashMap;

public class GraphicsLoader {
    public String[] playerColors = {"Blue", "DarkGreen", "Green", "Orange", "Pink", "Red", "Grey", "DarkYellow"};

    public String   folder_main = "graphics/",
                    folder_ui   = folder_main + "ui/",
                    folder_Buttons = folder_ui + "Buttons/",
                    folder_MainMenu = folder_ui + "MainMenu/",
                    folder_Lobby = folder_MainMenu + "Lobby/",
                    folder_Lobbies = folder_MainMenu + "Lobbies/",
                    folder_ChatBox = folder_ui + "ChatBox/",
                    folder_MessageBox = folder_ui + "MessageBox/",
                    folder_StatusBar = folder_ui + "StatusBar/",
                    folder_Cards = folder_ui + "Cards/";

    public Skin     default_skin,
                    chatBox_skin,
                    chatBox_skin2;

    public BitmapFont   default_font,
                        default_markup_font,
                        default_markup_font_0p8,
                        default_font_1p5,
                        default_font_1p6,
                        default_font_1p7,
                        default_font_3p0;

    public Drawable logo,
                    btn_rounded_focused,
                    btn_rounded_unfocused,
                    btn_rounded_frozen,
                    mainMenu_h1,
                    mainMenu_h2,
                    mainMenu_body,
                    lobby_playerList_bg,
                    lobbies_view_bg,
                    lobbies_bg,
                    lobbies_header,
                    lobbies_horizontal_line,
                    btn_update,
                    btn_add,
                    pixel_black,
                    messageBox_bg,
                    sb_bg,
                    sb_life,
                    sb_lifeInactive,
                    sb_damage,
                    sb_damageInactive,
                    sb_powerDown,
                    sb_powerDownInactive;

    public HashMap<CardType, Drawable> card_drawables;
    public HashMap<CardType, Drawable> card_drawables_checked;

    public Color    color_primary;

    public ImageTextButton.ImageTextButtonStyle btnStyle_rounded_focused;
    public ImageTextButton.ImageTextButtonStyle btnStyle_rounded_unfocused;
    public ImageTextButton.ImageTextButtonStyle btnStyle_rounded_frozen;
    public ImageTextButton.ImageTextButtonStyle btnStyle_lobbies_entry_focused;
    public ImageTextButton.ImageTextButtonStyle btnStyle_lobbies_entry_unfocused;
    public Label.LabelStyle                     labelStyle_markup_enabled,
                                                labelStyle_markup_enabled_0p8;

    public ImageTextButton.ImageTextButtonStyle[] btnStyle_players;

    /**
     * Loads and store all graphics
     */
    public GraphicsLoader() {
        /*
         * Skins and fonts
         */
        default_skin = getSkin(folder_ui + "uiskin.json");
        default_font = default_skin.getFont("default-font");

        chatBox_skin = getSkin(folder_ChatBox + "uiskin.json");
        chatBox_skin2 = getSkin(folder_ChatBox + "uiskin.json");

        default_markup_font = getSkin(folder_ui + "uiskin.json").getFont("default-font");
        default_markup_font.getData().markupEnabled = true;

        default_markup_font_0p8 = getSkin(folder_ui + "uiskin.json").getFont("default-font");
        default_markup_font_0p8.getData().markupEnabled = true;
        default_markup_font_0p8.getData().setScale(0.8f);

        default_font_1p5 = getSkin(folder_ui + "uiskin.json").getFont("default-font");
        default_font_1p5.getData().setScale(1.5f);

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

        btn_rounded_frozen = getDrawable(folder_Buttons + "btn_rounded_frozen.png");
        btn_rounded_focused = getDrawable(folder_Buttons + "btn_rounded_focused.png");
        btn_rounded_unfocused = getDrawable(folder_Buttons + "btn_rounded_unfocused.png");

        mainMenu_h1 = getDrawable(folder_MainMenu + "h1.png");
        mainMenu_h2 = getDrawable(folder_MainMenu + "h2.png");
        mainMenu_body = getDrawable(folder_MainMenu + "main.png");

        lobby_playerList_bg = getDrawable(folder_Lobby + "player_bg.png");

        lobbies_bg = getDrawable(folder_Lobbies + "Lobbies_bg.png");
        lobbies_header = getDrawable(folder_Lobbies + "Lobbies_header.png");
        lobbies_view_bg = getDrawable(folder_Lobbies + "LobbiesView_bg.png");
        lobbies_horizontal_line = getDrawable(folder_Lobbies + "Lobbies_horizontalLine.png");

        btn_add = getDrawable(folder_Lobbies + "plusButtonFocused.png");
        btn_update = getDrawable(folder_Lobbies + "updateButtonFocused.png");
        pixel_black = getDrawable(folder_ChatBox + "pixel_black.png");

        messageBox_bg = getDrawable(folder_MessageBox + "bg.png");

        card_drawables = new HashMap<>();
        card_drawables_checked = new HashMap<>();
        for (CardType move : CardType.values()) {
            card_drawables.put(move, getDrawable(folder_Cards + move.name() + ".png"));
            card_drawables_checked.put(move, getDrawable(folder_Cards + "checked/" + move.name() + ".png"));
        }

        sb_bg = getDrawable(folder_StatusBar + "bg.png");
        sb_life = getDrawable(folder_StatusBar + "Life_Green.png");
        sb_damage = getDrawable(folder_StatusBar + "Damage_Yellow.png");
        sb_powerDown = getDrawable(folder_StatusBar + "PowerDown_Active.png");

        sb_lifeInactive = getDrawable(folder_StatusBar + "Life_Grey.png");
        sb_damageInactive = getDrawable(folder_StatusBar + "Damage_Grey.png");
        sb_powerDownInactive = getDrawable(folder_StatusBar + "PowerDown_Inactive.png");


        /*
         * Colors
         */
        color_primary = new Color(0.2f,0.2f,0.3f,1);


        /*
         * Styles
         */
        btnStyle_rounded_frozen     = styleFromDrawable(btn_rounded_frozen, default_font, Color.BLACK);
        btnStyle_rounded_focused    = styleFromDrawable(btn_rounded_focused, default_font, Color.BLACK);
        btnStyle_rounded_unfocused  = styleFromDrawable(btn_rounded_unfocused, default_font, Color.BLACK);

        // Each player has their own robot-color, which is represented as a separate button in the lobby-tab
        btnStyle_players = new ImageTextButton.ImageTextButtonStyle[8];
        for (int i = 0; i < playerColors.length; i++) {
            btnStyle_players[i] = styleFromDrawable(getDrawable(folder_Lobby + "player_" + playerColors[i] + ".png"), default_font_1p6, Color.BLACK);
        }

        btnStyle_lobbies_entry_focused = styleFromDrawable(getDrawable(folder_Lobbies + "LobbyButtonFocused.png"), default_font_1p5, Color.BLACK);
        btnStyle_lobbies_entry_unfocused = styleFromDrawable(getDrawable(folder_Lobbies + "LobbyButtonUnfocused.png"), default_font_1p5, Color.BLACK);

        labelStyle_markup_enabled = new Label.LabelStyle(default_markup_font, Color.YELLOW);
        labelStyle_markup_enabled_0p8 = new Label.LabelStyle(default_markup_font_0p8, Color.YELLOW);
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
        ImageTextButton.ImageTextButtonStyle tmpStyle = new ImageTextButton.ImageTextButtonStyle(tmpD, tmpD, tmpD, font == null ? default_font : font);
        tmpStyle.fontColor = color;
        return tmpStyle;
    }
}
