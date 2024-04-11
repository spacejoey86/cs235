package swan.g09.cs230a2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import javafx.geometry.Point2D;

/**
 * The {@code Frog} class, a subclass of {@code Actor}, represents a type of monster that tries to move
 * in the shortest path to the player's location.
 *
 * @author Barnaby Morley-Smith
 */
public class Frog extends Actor {
    /**
     * How many ticks should pass between each time the frog moves.
     */
    private static final int MOVE_INTERVAL = 9;

    /**
     * The list of tiles that the frog can walk on.
     * Includes trap even though frogs don't walk on them, this is accounted for in the pathfinding.
     */
    private static final ArrayList<TileType> WALKABLE =
            new ArrayList<>(List.of(TileType.PATH, TileType.BUTTON, TileType.TRAP));

    /**
     * Stores how many times the frog has been ticked.
     */
    private int currentTick = 0;

    /**
     * Constructor to initialize a Frog instance.
     *
     * @param position The initial position of the Frog.
     */
    public Frog(Point2D position) {
        super(TileType.FROG, "sprites/Frog.png", WALKABLE, position);
    }


    /**
     * Handles the tick operation for the Frog,
     * determining the next movement direction.
     * Moves the Frog in the determined direction or
     * handles alternative actions if unable to move.
     */
    @Override
    protected void tick() {
        currentTick++;

        if (currentTick % MOVE_INTERVAL != 0 || isTrapped()) {
            return;
        }

        Direction nextDirection = determineNextDirection();

        if (nextDirection != null && checkMove(nextDirection)) {
            move(nextDirection);
            setFacingDir(nextDirection);
        }
    }

    /**
     * Checks if the Frog can move in a specific direction based
     * on the grid layout and obstacles.
     *
     * @param dir The direction to check for movement.
     * @return True if the Frog can move in the specified direction;
     * otherwise, false.
     */
    @Override
    protected boolean checkMove(Direction dir) {
        Point2D newPosition = dir.calculateNewPosition(this.getPosition());
        GameManager.checkTile(newPosition);

        TileType newTileType = checkPosition(newPosition);
        if (!canWalkOverTile(newTileType)) {
            return false;
        }

        if (isTileOccupiedByActor(newPosition)) {
            Actor collidedActor = GameManager.checkActor(newPosition);
            if (collidedActor instanceof Player) {
                if (!((Player) collidedActor).isInvincible()) {
                    GameManager.endGame(GameManager.DeathState.BUG_KILL);
                    GameManager.removeActor(newPosition);
                }  // Player is invincible, no action taken
            }
            return false;
        }
        return true;
    }


    /**
     * Determines the next direction for the Frog to move based
     * on the A* pathfinding algorithm.
     *
     * @return The next valid direction for the Frog to move,
     * or null if no available path.
     */
    private Direction determineNextDirection() {
        ArrayList<Point2D> path = findShortestPath();
        if (!path.isEmpty()) {
            return posToDirection(path.get(0));
        } else {
            return pickRandomDir();
        }
    }

    /**
     * Finds the direction pointing towards a position.
     * @param dest The destination position.
     * @return The direction to move to reach the position.
     */
    private Direction posToDirection(Point2D dest) {
        Point2D pos = getPosition();
        if (dest.getY() < pos.getY()) {
            return Direction.NORTH;
        } else if (dest.getX() > pos.getX()) {
            return Direction.EAST;
        } else if (dest.getY() > pos.getY()) {
            return Direction.SOUTH;
        } else if (dest.getX() < pos.getX()) {
            return Direction.WEST;
        }

        return null;
    }

    /**
     * Finds the shortest path from the current position to the player's position.
     * @return The list of positions that make up the path.
     */
    private ArrayList<Point2D> findShortestPath() {
        Point2D start = getPosition();
        Point2D end = GameManager.getPlayerPosition();

        if (end == null) {
            return new ArrayList<>();
        }

        ArrayList<Point2D> closedList = new ArrayList<>();
        HashMap<Point2D, PathFindNode> nodes = new HashMap<>();

        PathFindNode rootNode = new PathFindNode(start);
        nodes.put(start, rootNode);

        PriorityQueue<PathFindDetails> openList =
                new PriorityQueue<>((o1, o2) -> (int) Math.round(o1.getVal() - o2.getVal()));

        openList.add(new PathFindDetails(0.0, start));

        while (!openList.isEmpty()) {
            PathFindDetails p = openList.peek();
            Point2D pos = p.getPos();

            // Remove from the open list
            openList.poll();
            closedList.add(pos);

            for (Direction direction : Direction.values()) {
                Point2D neighbour = directionToPosition(pos, direction);

                if (positionTraversable(neighbour, true)) {
                    if (neighbour.equals(end)) {
                        PathFindNode neighbourNode = new PathFindNode(neighbour);
                        neighbourNode.setParent(nodes.get(pos));
                        nodes.put(neighbour, neighbourNode);
                        return nodeParentsToList(neighbourNode);
                    } else if (!closedList.contains(neighbour)
                            && positionTraversable(neighbour, true)
                            && checkPosition(neighbour) != TileType.TRAP) {
                        PathFindNode node = nodes.get(pos);
                        int newG = node.getG() + 1;
                        double newH = calculateHVal(neighbour, end);
                        double newF = newG + newH;

                        PathFindNode neighbourNode = nodes.get(neighbour);
                        if (neighbourNode == null || neighbourNode.getF() > newF) {
                            if (neighbourNode == null) {
                                neighbourNode = new PathFindNode(neighbour);
                                neighbourNode.setParent(nodes.get(pos));
                                nodes.put(neighbour, neighbourNode);
                            }

                            openList.add(new PathFindDetails(newF, neighbour));

                            neighbourNode.setG(newG);
                            neighbourNode.setH(newH);
                        }
                    }
                }
            }
        }

        return new ArrayList<>();
    }

    /**
     * Picks a random, and valid, direction.
     * @return The randomly picked direction.
     */
    private Direction pickRandomDir() {
        List<Direction> dirs = Arrays.asList(Direction.values());
        Collections.shuffle(dirs);
        for (Direction dir : dirs) {
            if (checkMove(dir)) {
                return dir;
            }
        }
        return null;
    }

    /**
     * Calculate the h value of a position for the A* search algorithm.
     * @param current The current position
     * @param end The destination position.
     * @return The h value
     */
    private double calculateHVal(Point2D current, Point2D end) {
        return Math.sqrt(Math.pow((current.getX() - end.getX()), 2.0) + Math.pow((current.getY() - end.getY()), 2.0));
    }

    /**
     * Create a list of positions (a path) from the PathFindNode parent heirarchy.
     * @param node The destination node.
     * @return A path as a list of positions, constructed from the nested parents of the node.
     */
    private ArrayList<Point2D> nodeParentsToList(PathFindNode node) {
        ArrayList<Point2D> list = new ArrayList<>();

        PathFindNode n = node;
        while (n.getParent() != null) {
            list.add(0, n.getPos());
            n = n.getParent();
        }

        return list;
    }
}
