/**
 * @Author Connor Milford
 * @Version 1.0
 */

package swan.g09.cs230a2;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static swan.g09.cs230a2.GameManager.getPlayerInstance;
import static swan.g09.cs230a2.GameManager.getPlayerPosition;
import static swan.g09.cs230a2.GameManager.removeActor;


/**
 * The Barnacle class creates a type of monster that traps the player if it is in a horizontal line from the barnacle.
 * */
public class Barnacle extends Actor {

    /**
     * The tiles that the barnacle cannot trap the player through.
     * */

    private static final ArrayList<TileType> BLOCKED_TILES =
            new ArrayList<>(List.of(TileType.BLOCK, TileType.WALL));

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
     * @param url the url of the barnacle image.
     * */
    public Barnacle(Point2D position, String url) {
        super(TileType.BARNACLE, url, BLOCKED_TILES, position);
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

        int direction = (int) Math.signum(barnaclePos.getX() - playerPos.getX());

        // Iterate over the tiles between the player and the barnacle
        double startX = playerPos.getX();
        double endX = barnaclePos.getX();
        for (double x = startX + direction; x != endX; x += direction) {
            Point2D tilePos = new Point2D(x, playerPos.getY());
            // Check if the tile at the current position is blocked
            if (blockedTilesMap.containsValue(tilePos)) {
                return false; // Player is blocked by a tile
            }
        }

        // Check if the player is on the same Y coordinate as the barnacle
        return playerPos.getY() == barnaclePos.getY();
    }

    /**
     * Traps the player, starting the barnacle event.
     * */
    public void trapPlayer() {
        if (canTrapPlayer()) {
            getPlayerInstance().setTrapped(true);
            playerTrapped = true;
            ChipsChallengeApplication.startEvent();
        }
    }

    /**
     * Searches through the map for blocked tiles to exclude from the canTrapPlayer() method.
     * @return a hashmap of blocked tiles, that do not need to be checked.
     * */
    private HashMap<Tile, Point2D> getBlockedTiles() {
        HashMap<Tile, Point2D> blockedTilesMap = new HashMap<>();

        // First, add the blocked tiles from BLOCKED_TILES
        for (TileType tileType : BLOCKED_TILES) {
            for (Point2D pos : GameManager.getBlockedTile(tileType)) {
                Tile blockedTile = GameManager.checkTile(pos);
                blockedTilesMap.put(blockedTile, pos);
            }
        }

        // Then, add the blocked tiles from Level.blocksList
        for (Tile blockedTile : GameManager.getBlocks()) {
            Point2D pos = blockedTile.getPosition();
            blockedTilesMap.put(blockedTile, pos);
        }

        return blockedTilesMap;
    }

    /**
     * Checks if the barnacle needs to be removed from the map.
     * */
    private void checkEvent() {
        if (BarnacleEvent.isEventWon()) {
            removeActor(findNearest().getPosition());
            getPlayerInstance().setTrapped(false);
            ChipsChallengeApplication.endEvent();
            BarnacleEvent.setEventWon(false);

        }
    }

    /**
     * Finds the nearest barnacle in order to delete it.
     * @return the nearest barnacle to the player.
     */

    private Barnacle findNearest() {
        Point2D playerPos = getPlayerInstance().getPosition();
        return GameManager.getBarnacles().stream()
                .min(Comparator.comparingDouble(b -> Math.sqrt(Math.pow(b.getPosition()
                        .getX() - playerPos.getX(), 2)
                        + Math.pow(b.getPosition().getY() - playerPos.getY(), 2))))
                .orElse(null);
    }
}
