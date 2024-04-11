/**
 * @Author Connor Milford
 * @Version 1.0
 */

package swan.g09.cs230a2;

import javafx.geometry.Point2D;
import java.util.*;
import static swan.g09.cs230a2.GameManager.*;


/**
 * The Barnacle class creates a type of monster that traps the player if it is in a horizontal line from the barnacle.
 * */
public class Barnacle extends Actor {

    /**
     * The tiles that the barnacle cannot trap the player through.
     * */

    private static final ArrayList<TileType> BLOCKED_TILES =
            new ArrayList<>(List.of(TileType.WALL, TileType.BLOCK));

    /**
     * Stores how many times the barnacle has ticked.
     * */
    private static int currentTick = 0;

    /**
     * Boolean that states if the player is currently trapped within the barnacle,
     * true if the player is trapped.
     * */
    private static boolean playerTrapped;

    /**
     * Constructor to intialize barnacle instance.
     * @param position the position of the barnacle.
     * */
    public Barnacle(Point2D position) {
        super(TileType.BARNACLE, "sprites/barnacle.png", BLOCKED_TILES, position);
    }

    /**
     * Handles the tick operation for the Barnacle,
     * determining if the player can be trapped, or if the barnacle should be destroyed.
     */
    @Override
    protected void tick() {
        currentTick++;
        trapPlayer();
        checkEvent();
    }


    /**
     * Checks if the actor can move,
     * since the barnacle is stationary, it always returns false.
     *
     * @param dir The direction to check for movement.
     * @return False, since the barnacle cannot move.
     */
    @Override
    protected boolean checkMove(Direction dir) {
        return false;
    }

    /**
     * Checks if the player can be trapped, by checking through a map of positions.
     *
     * @return true, if the player can be trapped.
     * */
    private boolean canTrapPlayer() {
        Point2D playerPos = getPlayerPosition();
        Point2D barnaclePos = this.getPosition();
        HashMap<Tile, Point2D> blockedTilesMap = getBlockedTiles(); // map used to keep track of blocked tiles

        // check if the player is in front of any blocked tile
        boolean blocked = false;

        for (Map.Entry<Tile, Point2D> entry : blockedTilesMap.entrySet()) {
            Point2D blockedTilePos = entry.getValue();
            // check tiles to the right and left of the player
            if ((blockedTilePos.getX() == playerPos.getX() + 1 || blockedTilePos.getX() == playerPos.getX() - 1)
                    && blockedTilePos.getY() == playerPos.getY()) {
                blocked = true;
                break; // No need to continue checking if player is already blocked
            }
        }


        boolean playerInDir = playerPos.getY() == barnaclePos.getY() && !blocked;



        return !blocked && playerInDir;
    }

    /**
     * Traps the player, starting the barnacle event.
     * */
    public void trapPlayer() {
        if (canTrapPlayer()) {
            getPlayerInstance().setTrapped(true);
            ChipsChallengeApplication.startEvent();
        }
    }

    /**
     * Searches through the map for blocked tiles to exclude from the canTrapPlayer() method.
     * @returns a hashmap of blocked tiles, that do not need to be checked.
     * */
    private HashMap<Tile, Point2D> getBlockedTiles() {
        HashMap<Tile, Point2D> blockedTilesMap = new HashMap<>();

        for (TileType tileType: BLOCKED_TILES) {
            for (Point2D pos :GameManager.getBlockedTile(tileType)) {
                blockedTilesMap.put(GameManager.checkTile(pos), GameManager.checkTile(pos).getPosition());
            }
        }
        return blockedTilesMap;
    }

    /**
     * Checks if the barnacle needs to be removed from the map.
     * */
    private void checkEvent() {
        if (BarnacleEvent.eventWon) {
            removeActor(this.getPosition());
            getPlayerInstance().setTrapped(false);
            ChipsChallengeApplication.endEvent();
            BarnacleEvent.eventWon = false;
        }
    }





}