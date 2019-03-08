package inf112.skeleton.app.card;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.HashMap;

public class Card {
    private int priority;
    private CardMove type;
    private HashMap<CardMove, Drawable> drawables;

    public Card(int priority, CardMove type) {
        this.priority = priority;
        this.type = type;
    }

    public CardMove getType() {
        return type;
    }

    public int getPriority() {
        return priority;
    }

    public String toString() {
        return "Type: " + type + " | Priority: " + priority;
    }

    public Drawable getDrawable() {
        // Fetch all card-textures if unfetched
        if (drawables == null) {
            drawables = new HashMap<>();
            for (CardMove move : CardMove.values())
                drawables.put(move, new TextureRegionDrawable(new TextureRegion(
                        new Texture(Gdx.files.internal("cards/" + move.name() + ".png")))));
        }
        return drawables.get(type);
    }
}
