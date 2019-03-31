package inf112.skeleton.app.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GraphicsLoader {
    // Links
    public String   folder_main = "graphics/",
                    folder_ui   = folder_main + "ui/";

    // Skins
    public Skin     default_skin;

    // Drawables
    public Drawable logo;

    // Colors
    public Color color_primary;


    public GraphicsLoader() {
        // Skins
        default_skin = getSkin(folder_ui + "uiskin.json");

        // Drawables
        logo = getDrawable(folder_ui + "robocop_logo_500W.png");

        // Colors
        color_primary = new Color(0.6f,0.4f,0.2f,1);
    }

    public Skin getSkin(String link) {
        return new Skin(Gdx.files.internal(link));
    }

    public Drawable getDrawable(String link) {
        return new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(link))));
    }
}
