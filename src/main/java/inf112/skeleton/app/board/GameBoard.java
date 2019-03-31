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

    public Entity getFirstEntity() {
        if (entities.isEmpty()) {
            return null;
        }
        return entities.get(0);
    }

    public void moveEntity(Directions dir) throws NoSuchElementException {

        Packet packet = new Packet(ToServer.MOVEMENT_ACTION.ordinal(), new MovementPacket(dir, 1));
        RoboRally.channel.writeAndFlush(Tools.GSON.toJson(packet) + "\r\n");
//        if (entities.contains(e)) {
//            switch (dir) {
//                case NORTH:
//                    e.moveY(1);
//                    break;
//                case SOUTH:
//                    e.moveY(-1);
//                    break;
//                case WEST:
//                    e.moveX(-1);
//                    break;
//                case EAST:
//                    e.moveX(1);
//                    break;
//            }
//        } else {
//            throw new NoSuchElementException("Entity does not exist on this gameboard");
//        }
    }

    /**
     * public void moveEntityCard(Entity e, Card card) throws NoSuchElementException {
     * if (entities.contains(e)) {
     * System.out.println("Current location: " + e.getX() + " " + e.getY());
     * if (canRobotMove((Robot) e, card)) {
     * CardType2 type = card.getType();
     * if (type == CardType2.ROTATERIGHT) {
     * e.rotateRight();
     * } else if (type == CardType2.ROTATELEFT) {
     * e.rotateLeft();
     * } else if (type == CardType2.ROTATE180) {
     * e.rotate180();
     * } else if (type == CardType2.FORWARD1) {
     * e.moveForwardBackward(1);
     * } else if (type == CardType2.FORWARD2) {
     * e.moveForwardBackward(2);
     * } else if (type == CardType2.FORWARD3) {
     * e.moveForwardBackward(3);
     * } else if (type == CardType2.BACKWARD1) {
     * e.moveForwardBackward(-1);
     * }
     * System.out.println("Moved to location: " + e.getX() + " " + e.getY());
     * }
     * } else {
     * throw new NoSuchElementException("Entity does not exist on this gameboard");
     * }
     * }
     */
    //TODO Fix this borked method.
    private boolean canRobotMove(Robot e, Card card) {
        int curX = (int) e.getX();
        int curY = (int) e.getY();
        Directions facing = e.getFacingDirection();
        int moveAmount = translateCardTypeAmount(card);

        if (facing == Directions.NORTH || facing == Directions.SOUTH) {
            for (int i = moveAmount; i > 0; i--) {
                if (!isValidTile(curX, curY - i)) {
                    return false;
                }
            }
        } else {
            for (int i = moveAmount; i > 0; i--) {
                if (!isValidTile(curX - i, curY)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isValidTile(int x, int y) {
        if (x > 0 && x < getWidth() - 1 && y > 0 && y < getHeight() - 1) {
            TileDefinition tileDef = getTileDefinitionByCoordinate(0, x, y);
            if (tileDef.isCollidable())
                return false;
            return true;
        }
        return false;
    }

    private int translateCardTypeAmount(Card card) {
        return 0;
    }

    public abstract void dispose();

    /**
     * Gets a tile by pixel position within the board, at a specified layer.
     *
     * @param layer
     * @param x
     * @param y
     * @return
     */
    public TileDefinition getTileDefinitionByLocation(int layer, float x, float y) {
        return this.getTileDefinitionByCoordinate(
                layer,
                (int) (x / TileDefinition.TILE_SIZE),
                (int) (y / TileDefinition.TILE_SIZE)
        );
    }

    /**
     * Gets a tile at a specified coordinate on the game board.
     *
     * @param layer
     * @param col
     * @param row
     * @return
     */
    public abstract TileDefinition getTileDefinitionByCoordinate(int layer, int col, int row);


    public abstract int getWidth();

    public abstract int getHeight();

    public abstract int getLayers();


    public void addPlayer(PlayerInitPacket pkt) {
        Gdx.app.log("Gameboard clientside - addPlayer", "RoboRally.username: " + RoboRally.username);
        Gdx.app.log("Gameboard clientside - addPlayer", "pkt.getName: " + pkt.getName());
        Gdx.app.log("Gameboard clientside - addPlayer", "RoboRally.username equals pkt.getName: " + pkt.getName().equalsIgnoreCase(RoboRally.username));
        if (pkt.getName().equalsIgnoreCase(RoboRally.username)) {
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
