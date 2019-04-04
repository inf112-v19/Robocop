package inf112.skeleton.common.specs;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import inf112.skeleton.app.RoboRally;

import java.util.HashMap;

public class Card {
    private int priority;
    private CardType type;
    private static HashMap<CardType, Drawable> drawables;
    private static HashMap<CardType, Drawable> drawables_checked;

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

    public Drawable getDrawable(boolean checked) {
        // Fetch all card-textures if unfetched
        if (drawables == null) {
            drawables = new HashMap<>();
            drawables_checked = new HashMap<>();
            for (CardType move : CardType.values()) {
                drawables.put(move, RoboRally.graphics.getDrawable(RoboRally.graphics.folder_ui + "properCards/" + move.name() + ".png"));
                drawables_checked.put(move, RoboRally.graphics.getDrawable(RoboRally.graphics.folder_ui + "properCards/checked/" + move.name() + ".png"));
            }
        }
        return (checked ? drawables_checked : drawables).get(type);
    }

    public boolean equals(Object b) {
        if(!(b instanceof Card)) {
            return false;
        }
        if(b == this) {
            return true;
        }
        return ((Card) b).priority == this.priority && ((Card) b).type == this.type;
    }
}
