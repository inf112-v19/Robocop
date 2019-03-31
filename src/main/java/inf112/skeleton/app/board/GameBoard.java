package inf112.skeleton.app.board;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.entity.Entity;
import inf112.skeleton.app.board.entity.Player;
import inf112.skeleton.app.board.entity.Robot;
import inf112.skeleton.app.gameStates.Playing.HUD;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.ToServer;
import inf112.skeleton.common.packet.data.*;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.common.specs.TileDefinition;
import inf112.skeleton.common.utility.Tools;

import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GameBoard {

    public HUD hud;
    protected ArrayList<Entity> entities;
    protected Map<String, Player> players;
    public Player myPlayer = null;
    private CardHandPacket foo = null;


    public GameBoard() {
        entities = new ArrayList<>();
        players = new ConcurrentHashMap<>();
    }

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public void render(OrthographicCamera camera, SpriteBatch batch) {
        for (Entity entity : entities) {
            entity.render(batch);

        }
        for (Entity entity : entities) {
            entity.renderName(batch, camera.zoom);
        }
    }

    public void update() {
        for (Player player : players.values()) {
            player.update();

        }
        for (Entity entity : entities) {
            entity.update();

        }
        if (myPlayer != null) {
            if (myPlayer.cards == null && foo != null) {
                Gdx.app.log("Gameboard clientside - update", "Trying to receive lost cardHandPacket");
                myPlayer.receiveCardHandPacket(foo);
            }
            myPlayer.update();
        }
    }

    public void receiveCard(CardPacket packet) {
        if (myPlayer != null) {
            myPlayer.receiveCardPacket(packet);
        }
    }

    public void receiveCardHand(CardHandPacket packet) {
        Gdx.app.log("Gameboard clientside - receiveCardHand", "Receiving card hand.");
        if (myPlayer != null) {
            Gdx.app.log("Gameboard clientside - receiveCardHand", "MyPlayer exists!");
            myPlayer.receiveCardHandPacket(packet);
        } else {
            foo = packet;
            Gdx.app.log("Gameboard clientside - receiveCardHand", "MyPlayer does not exist - saving packet for later");
        }
    }


    public abstract void dispose();



    public abstract int getWidth();

    public abstract int getHeight();



    public void addPlayer(PlayerInitPacket pkt) {
        if (pkt.getUUID().equalsIgnoreCase(RoboRally.clientInfo)) {
            this.myPlayer = new Player(pkt.getUUID(), pkt.getName(), pkt.getPos(), pkt.getHealth(), pkt.getSlot(), pkt.getFacing());
            return;
        }
        this.players.put(pkt.getUUID(), new Player(pkt.getUUID(), pkt.getName(), pkt.getPos(), pkt.getHealth(), pkt.getSlot(), pkt.getFacing()));
    }

    public void setupPlayer(PlayerInitPacket pkt) {
        this.myPlayer = new Player(pkt.getUUID(), pkt.getName(), pkt.getPos(), pkt.getHealth(), pkt.getSlot(), pkt.getFacing());
    }

    public void removePlayer(PlayerRemovePacket pkt) {
        Player leavingPlayer = this.getPlayer(pkt.getUUID());
        this.entities.remove(leavingPlayer.getRobot());
        this.players.remove(pkt.getUUID());
    }

    public Player getPlayer(String uuid) {
        if (uuid.equalsIgnoreCase(RoboRally.clientInfo)) {
            return myPlayer;
        }
        return this.players.get(uuid);
    }

}
