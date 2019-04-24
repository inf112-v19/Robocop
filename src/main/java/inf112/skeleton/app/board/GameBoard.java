package inf112.skeleton.app.board;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.entity.Entity;
import inf112.skeleton.app.board.entity.Flag;
import inf112.skeleton.app.board.entity.Player;
import inf112.skeleton.app.gameStates.Playing.HUD;
import inf112.skeleton.app.gameStates.Playing.State_Playing;
import inf112.skeleton.common.packet.data.*;

import java.util.ArrayList;
import java.util.Map;
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

    public void update(State_Playing playing) {
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
            if (myPlayer.getRobot() != null) {
                if (playing.cameraHandler.isFollowing()) {
                    playing.cameraHandler.updatePosition(myPlayer.getRobot().getPos());
                }
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

    public void receiveFlags(FlagsPacket packet) {
        for (int i = 0; i < packet.flags.length; i++) {
            float x = packet.flags[i].getPos().x;
            float y = packet.flags[i].getPos().y;
            int number = packet.flags[i].getNumber();
            Flag flag = new Flag(x,y,number);
            entities.add(flag);
        }
    }

    public void forceSelect() {
        if (myPlayer != null) {
            myPlayer.forceSelect();
            /*Gdx.app.log("GameBoard - forceSelect", "myPlayer is not null.");
            if(myPlayer.cards != null && myPlayer.selectedCards != null) {
                Gdx.app.log("GameBoard - forceSelect", "cards and selectedCards are not null.");
                for (int i = 0; i < 5; i++) {
                    Gdx.app.log("GameBoard - forceSelect", "Setting selected card " + i + " to " + myPlayer.cards[i].toString());
                    myPlayer.selectedCards[i] = myPlayer.cards[i];
                }
            }*/
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

    public Map getPlayers() {
        return players;
    }

}
