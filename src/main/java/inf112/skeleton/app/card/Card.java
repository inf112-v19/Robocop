package inf112.skeleton.app.card;

public class Card {
    private int priority;
    private String type;

    public Card(int priority, String type) {
        this.priority = priority;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public int getPriority() {
        return priority;
    }

    public String toString() {
        return "Type: " + type + " | Priority: " + priority;
    }
}
