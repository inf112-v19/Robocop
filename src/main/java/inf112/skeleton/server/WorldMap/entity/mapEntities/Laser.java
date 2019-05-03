package inf112.skeleton.server.WorldMap.entity.mapEntities;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.Direction;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.server.WorldMap.GameBoard;
import inf112.skeleton.server.WorldMap.entity.Player;
import inf112.skeleton.server.WorldMap.entity.TileEntity;

import java.util.ArrayList;

public class Laser extends TileEntity {

    private int length;

    public Laser(TiledMapTile tile, int x, int y, TiledMapTileLayer.Cell cell, GameBoard board) {
        super(tile, x, y, cell, board);
    }

    /**
     * Do an action to a player, for example harm it when walking into lasers
     *
     * @param player
     */
    @Override
    public void walkOn(Player player) {
        checkLength();
    }

    public void checkIfPlayerAffected(Player player) {
        Vector2 toCheck = this.getPos().cpy();

        for (int i = 0; i <= length + 1; i++) {
            if (i != 0) {
                toCheck = getTileInDirection(Direction.values()[(getDirection().ordinal() + 1) % 4], i);

            }
            if (player.getCurrentPos().dst(toCheck) == 0 ){
                player.getHit();
            }
        }
    }

    /**
     * Actions to be ran every tick
     */
    @Override
    public void update() {
        checkLength();
    }

    public void checkLength() {
        int length = 0;
        ArrayList<Player> players = getBoard().players[Tools.coordToIndex(getPos().x, getPos().y, getBoard().getWidth())];
        for (Player player : players) {
            if (detectCollision(player.getCurrentPos())) {
                this.length = 0;
                return;
            }
        }
        outerloop:
        while (true) {
            Vector2 toCheck = getTileInDirection(Direction.values()[(getDirection().ordinal() + 1) % 4], length + 1);
            if ((int) toCheck.y >= getBoard().getHeight() ||
                    (int) toCheck.y <= 1 ||
                    (int) toCheck.x >= getBoard().getWidth() ||
                    (int) toCheck.x <= 1) {
                break;
            }
            ArrayList<TileEntity> wallsAtLoc = getBoard().walls[Tools.coordToIndex(toCheck.x, toCheck.y, getBoard().getWidth())];
            for (TileEntity wall : wallsAtLoc) {
                if (!wall.canEnter(Direction.values()[(getDirection().ordinal() + 1) % 4])) {
                    break outerloop;
                }
                if (!wall.canLeave(Direction.values()[(getDirection().ordinal() + 1) % 4])) {
                    length++;
                    break outerloop;
                }
            }

            players = getBoard().players[Tools.coordToIndex(toCheck.x, toCheck.y, getBoard().getWidth())];
            for (Player player : players) {
                if (detectCollision(player.getCurrentPos())) {
                    break outerloop;
                }
            }

            length++;
        }
        this.length = length;
    }

    /**
     * If a player move on top of the tileEntity, should it continue walking
     *
     * @return if it can continue walking
     */
    @Override
    public boolean canContinueWalking() {
        return true;
    }

    @Override
    public boolean canEnter(Direction walkingDirection) {
        return true;
    }

    @Override
    public boolean canLeave(Direction walkingDirection) {
        return true;
    }
}
