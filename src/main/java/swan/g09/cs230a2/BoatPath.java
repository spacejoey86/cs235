package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * A boat that moves back and forth, and the player can walk on and off it.
 * @author Dylan
 */
public class BoatPath extends ActionTile {
    private final Direction pushDirection;
    private Boolean boatHere;
    private Boolean movedThisTick = false;
    private int ticksSinceMove = 0;
    private static final int MOVE_INTERVAL = 7;

    public BoatPath(Point2D position, Direction pushDirection, Boolean boatHere) {
        super(TileType.BOAT_PATH, "sprites/Water.png", position);
        this.pushDirection = pushDirection;
        this.boatHere = boatHere;
        if (this.boatHere) {
            updateImagePath("sprites/Trap_Set.png");
        }
    }

    @Override
    public void walkedOn(Actor actor) {
        if (actor.getType() == TileType.PLAYER && !this.boatHere) {
            GameManager.removeActor(getPosition());
            GameManager.endGame(GameManager.DeathState.DROWN);
        }
    }


    private void moveBoatTo() {
        this.boatHere = true;
        this.movedThisTick = true;

        updateImagePath("sprites/Trap_Set.png");
    }

    private void moveBoatAway() {
        this.boatHere = false;
        updateImagePath("sprites/Water.png");

        Actor rider = GameManager.checkActor(this.getPosition());
        if (rider != null) {
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
                nextBoatTile.moveBoatTo();
                this.moveBoatAway();
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
