package inf112.skeleton.app.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.entity.Player;
import inf112.skeleton.common.specs.Card;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static inf112.skeleton.common.specs.CardType.GREY;

public class StatusBar extends Table {

    private LinkedHashMap<String, Table> rows;

    private HashMap<String, Image[]> lifeButtons;
    private HashMap<String, Image[]> damageButtons;
    private HashMap<String, Image> powerDownButtons;
    private HashMap<String, ImageTextButton[]> cards;
    private HashMap<String, int[]> ldp;

    private ImageTextButton.ImageTextButtonStyle greyCard = RoboRally.graphics.styleFromDrawable(RoboRally.graphics.card_drawables.get(GREY), null, Color.RED);;

    private static final int    username_width = 150,
                                iconSize = 40,
                                pad = 5,
                                height = 46,

                                numLives = 3,
                                numDamage = 9,
                                numCards = 5,

                                cardHeight = iconSize,
                                cardWidth = (int)(128f/192f * iconSize), // Card width / card height * scale

                                width = username_width + pad + (pad + iconSize) * (numLives + numDamage + 1) + (pad + cardWidth) * numCards;

    public StatusBar() {
        super();
        clear();
    }

    @Override
    public float getWidth() {
        return (float)width;
    }

    @Override
    public float getHeight() {
        return (float)(height * rows.size());
    }

    /**
     * Add the status of a new user to the bar.
     * @param name username
     */
    public void addStatus(String name) {
        Table userRow = new Table();
        userRow.setBackground(RoboRally.graphics.sb_bg);

        userRow.add(new Label(name + ":", RoboRally.graphics.labelStyle_markup_enabled)).size(username_width, height).pad(0,pad,0,pad);

        // Power down
        Image powerDownButton = new Image(RoboRally.graphics.sb_powerDownInactive);
        userRow.add(powerDownButton).size(iconSize, iconSize).padRight(pad);
        powerDownButtons.put(name, powerDownButton);

        // Lives
        Image[] livesButtons = new Image[numLives];
        for (int i = 0 ; i < numLives ; i++) {
            livesButtons[i] = new Image(RoboRally.graphics.sb_life);
            userRow.add(livesButtons[i]).size(iconSize, iconSize).padRight(pad);
        }
        lifeButtons.put(name, livesButtons);

        // Damage taken
        Image[] damageTakenButtons = new Image[numDamage];
        for (int i = 0 ; i < numDamage ; i++) {
            damageTakenButtons[i] = new Image(RoboRally.graphics.sb_damageInactive);
            userRow.add(damageTakenButtons[i]).size(iconSize, iconSize).padRight(pad);
        }
        damageButtons.put(name, damageTakenButtons);

        // Cards
        ImageTextButton[] pCards = new ImageTextButton[numCards];
        for (int i = 0 ; i < numCards ; i++) {
            pCards[i] = new ImageTextButton("",greyCard);
            userRow.add(pCards[i]).size(cardWidth, cardHeight).padRight(pad);
        }
        cards.put(name, pCards);

        // Set initial life/damage/powerdown count.
        ldp.put(name, new int[]{3,0,0});

        add(userRow).size(width, height).row();
        rows.put(name, userRow);
    }

    /*
     * Add/Remove life/damage to the given user.
     * Note: Will crash if invalid action.
     *
     * @param name
     */

    public void addDamage(String name) {
        damageButtons.get(name)[numDamage-1-ldp.get(name)[1]++].setDrawable(RoboRally.graphics.sb_damage);
    }

    public void removeDamage(String name) {
        damageButtons.get(name)[numDamage-1-ldp.get(name)[1]--].setDrawable(RoboRally.graphics.sb_damageInactive);
    }

    public void addLife(String name) {
        lifeButtons.get(name)[ldp.get(name)[0]++ - 1].setDrawable(RoboRally.graphics.sb_life);
    }

    public void removeLife(String name) {
        lifeButtons.get(name)[ldp.get(name)[0]-- - 1].setDrawable(RoboRally.graphics.sb_lifeInactive);
    }

    /**
     * Notify that a powerdown is to happen next round.
     * @param name of user
     * @param powerDown or not
     */
    public void powerDown(String name, boolean powerDown) {
        powerDownButtons.get(name).setDrawable(powerDown ? RoboRally.graphics.sb_powerDown : RoboRally.graphics.sb_powerDownInactive);
        ldp.get(name)[2] = powerDown ? 1 : 0;
    }

    /**
     * Replace all cards in status bar with grey cards.
     */
    public void hideCards() {
        for (ImageTextButton[] pCards : cards.values()) {
            for (int i = 0 ; i < numCards ; i++) {
                if (!pCards[i].getStyle().equals(greyCard)) {
                    pCards[i].setStyle(greyCard);
                }
            }
        }
    }

    private void displayCards(Player player) {
        ImageTextButton[] pCards = cards.get(player.name);
        if (pCards == null) {
            Gdx.app.error("pCards not set for user with name <" + player.name + ">","");
            return;
        }

        for (int i = 0 ; i < numCards ; i++) {
            Card card = player.selectedCards[i];
            pCards[i].setStyle( (card == null) ? greyCard : RoboRally.graphics.styleFromDrawable(card.getDrawable(false), null, Color.RED));
            if (card == null) {
                System.out.println("No cards for player <" + player.name + ">.");
            }
        }
    }

    public void displayCards() {
        for (Player player : (Collection<Player>)RoboRally.gameBoard.getPlayers().values()) {
            displayCards(player);
        }
        displayCards(RoboRally.gameBoard.myPlayer);
    }

    public int size() {
        return rows.size();
    }

    public void clear() {
        clearChildren();

        ldp = new HashMap<>();
        rows = new LinkedHashMap<>();
        lifeButtons = new HashMap<>();
        damageButtons = new HashMap<>();
        powerDownButtons = new HashMap<>();
        cards = new HashMap<>();
    }
}
