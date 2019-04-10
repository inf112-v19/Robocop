package inf112.skeleton.common.specs;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import inf112.skeleton.app.RoboRally;

public class Card {
    private int priority;
    private CardType type;

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
        if (checked)
            return RoboRally.graphics.card_drawables_checked.get(type);
        else return RoboRally.graphics.card_drawables.get(type);
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

    public boolean getPushed() {
        return type == CardType.BACKWARD1;
    }
}
