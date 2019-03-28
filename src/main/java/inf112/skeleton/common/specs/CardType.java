package inf112.skeleton.common.specs;

import java.util.HashMap;

public enum CardType {
    ROTATERIGHT("rotate right",0,1, 18, 80, 20),
    ROTATELEFT("rotate left",0,3, 18, 70, 20),
    ROTATE180("rotate 180", 0,2, 6, 10, 10),
    FORWARD1("forward 1",1,0, 18, 490, 10),
    FORWARD2("forward 2",2,0, 12, 670, 10),
    FORWARD3("forward 3",3,0, 6, 790, 10),
    BACKWARD1("backward 1",-1,0, 6, 430, 10),
    GREY("blank",0,0, 0, 0, 0);

    public String name;
    public int moveAmount;
    public int turnAmount;
    public int amountOfCards;
    public int basePriority;
    public int priorityDiff;

    CardType(String name, int moveAmount, int turnAmount, int amountOfCards, int basePriority, int priorityDiff) {
        this.name = name;
        this.moveAmount = moveAmount;
        this.turnAmount = turnAmount;
        this.amountOfCards = amountOfCards;
        this.basePriority = basePriority;
        this.priorityDiff = priorityDiff;

    }

    public static HashMap<String, CardType> CardList;

    static {
        CardList = new HashMap<>();
        for (CardType cardType : CardType.values()) {
            CardList.put(cardType.name, cardType);

        }
    }
}
