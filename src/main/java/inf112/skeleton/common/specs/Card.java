package inf112.skeleton.common.specs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import inf112.skeleton.common.specs.CardType;

import java.util.HashMap;

public class Card {
    private int priority;
    private CardType type;
    private HashMap<CardType, Drawable> drawables;

    public Card(int priority, CardType type) {
        this.priority = priority;
        this.type = type;
    }

    public CardType getType() {
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
            for (CardType move : CardType.values())
                drawables.put(move, new TextureRegionDrawable(new TextureRegion(
                        new Texture(Gdx.files.internal("graphics/ui/cards/" + move.name() + ".png")))));
        }
        return drawables.get(type);
    }
}
