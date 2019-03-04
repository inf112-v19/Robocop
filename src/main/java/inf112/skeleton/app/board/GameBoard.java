package inf112.skeleton.app.board;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.entity.Entity;
import inf112.skeleton.app.board.entity.Player;
import inf112.skeleton.app.board.entity.Robot;
import inf112.skeleton.app.card.Card;
import inf112.skeleton.app.card.CardMove;
import inf112.skeleton.common.packet.IncomingPacket;
import inf112.skeleton.common.packet.MovementPacket;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.PlayerInitPacket;
import inf112.skeleton.common.specs.Directions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public abstract class GameBoard {

    protected ArrayList<Entity> entities;
    protected Map<String, Player> players;
    Gson gson = new Gson();


    public GameBoard() {
        entities = new ArrayList<>();
        players = new HashMap<>();
    }

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public void render(OrthographicCamera camera, SpriteBatch batch) {
        for (Entity entity : entities) {
            entity.render(batch);

        }
    }

    public void update() {
        for (Player player : players.values()) {
            player.update();

        }
        for (Entity entity : entities) {
            entity.update();

        }


    }

    public Entity getFirstEntity() {
        if (entities.isEmpty()) {
            return null;
        }
        return entities.get(0);
    }

    public void moveEntity(Directions dir) throws NoSuchElementException {

        Packet packet = new Packet(IncomingPacket.MOVEMENT_ACTION.ordinal(), new MovementPacket(dir, 1));
        RoboRally.channel.writeAndFlush(gson.toJson(packet) + "\r\n");
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

    public void moveEntityCard(Entity e, Card card) throws NoSuchElementException {
        if (entities.contains(e)) {
            System.out.println("Current location: " + e.getX() + " " + e.getY());
            if (canRobotMove((Robot) e, card)) {
                CardMove type = card.getType();
                if (type == CardMove.ROTATERIGHT) {
                    e.rotateRight();
                } else if (type == CardMove.ROTATELEFT) {
                    e.rotateLeft();
                } else if (type == CardMove.ROTATE180) {
                    e.rotate180();
                } else if (type == CardMove.FORWARD1) {
                    e.moveForwardBackward(1);
                } else if (type == CardMove.FORWARD2) {
                    e.moveForwardBackward(2);
                } else if (type == CardMove.FORWARD3) {
                    e.moveForwardBackward(3);
                } else if (type == CardMove.BACKWARD1) {
                    e.moveForwardBackward(-1);
                }
                System.out.println("Moved to location: " + e.getX() + " " + e.getY());
            }
        } else {
            throw new NoSuchElementException("Entity does not exist on this gameboard");
        }
    }

    //TODO Fix this borked method.
    private boolean canRobotMove(Robot e, Card card) {
        int curX = (int) e.getX();
        int curY = (int) e.getY();
        Directions facing = e.getFacingDirection();
        int moveAmount = translateCardMoveAmount(card);

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

    private int translateCardMoveAmount(Card card) {
        if (card.getType() == CardMove.FORWARD1)
            return 1;
        if (card.getType() == CardMove.FORWARD2)
            return 2;
        if (card.getType() == CardMove.FORWARD3)
            return 3;
        if (card.getType() == CardMove.BACKWARD1)
            return -1;
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
        this.players.put(pkt.getName(),new Player(pkt.getName(), pkt.getPos(), pkt.getHealth(), Directions.SOUTH));
    }

    public Player getPlayer(String name) {
        return this.players.get(name);
    }
}
