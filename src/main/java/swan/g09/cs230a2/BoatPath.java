package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * A boat that moves back and forth, and the player can walk on and off it.
 * @author Dylan
 */
public class BoatPath extends ActionTile {
    private Actor ridingActor;
    private final Direction pushDirection;
    private Boolean boatHere;
    private Boolean movedThisTick = false;
    private int ticksSinceMove = 0;
    private static final int MOVE_INTERVAL = 7;

    public BoatPath(Point2D position, Direction pushDirection, Boolean boatHere) {
        super(TileType.BOAT_PATH, "sprites/Water.png", position);
        this.ridingActor = null;
        this.pushDirection = pushDirection;
        this.boatHere = boatHere;
    }

    @Override
    public void walkedOn(Actor actor) {
        this.ridingActor = actor;
    }

    @Override
    public void walkedOff(Actor actor) {
        this.ridingActor = null;
    }

    private void moveBoatTo(Actor actor) {
        this.boatHere = true;
        this.ridingActor = actor;
        this.movedThisTick = true;

        updateImagePath("sprites/Trap_Set.png");
    }

    private void moveBoatAway() {
        this.boatHere = false;
        updateImagePath("sprites/Water.png");

        if (this.ridingActor instanceof Actor rider) {
            rider.move(this.pushDirection);
        }
    }

    @Override
    public void tick() {
        if (this.boatHere) {
            Point2D nextPosition = this.pushDirection.calculateNewPosition(this.getPosition());
            Tile nextTile = GameManager.checkTile(nextPosition);
            if (ticksSinceMove > BoatPath.MOVE_INTERVAL && !this.movedThisTick && nextTile instanceof BoatPath nextBoatTile) {
                this.ticksSinceMove = 0;
                System.out.println("test");
                this.moveBoatAway();
                nextBoatTile.moveBoatTo(this.ridingActor);
            } else {
                this.movedThisTick = false;
                this.ticksSinceMove += 1;
            }
        }
    }

    public char toChar() {
        if (this.boatHere) {
            return switch (this.pushDirection) {
                case Direction.NORTH -> 'N';
                case Direction.EAST -> 'Q';
                case Direction.SOUTH -> 'V';
                case Direction.WEST -> 'X';
            };
        } else {
            return switch (this.pushDirection) {
                case Direction.NORTH -> 'A';
                case Direction.EAST -> 'F';
                case Direction.SOUTH -> 'H';
                case Direction.WEST -> 'M';
            };
        }
    }
}
