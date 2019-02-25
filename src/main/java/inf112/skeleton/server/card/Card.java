package inf112.skeleton.server.card;

public class Card {
    private int priority;
    private CardMove type;

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
}
