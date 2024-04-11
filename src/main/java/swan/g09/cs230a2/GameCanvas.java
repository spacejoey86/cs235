package swan.g09.cs230a2;

import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * The canvas for rendering the game.
 *
 * @author Barnaby Morley-Smith
 * @version 0.1
 */
class GameCanvas extends Canvas {
    /**
     * The minimum size (in pixels) that a tile may be rendered.
     */
    private static final int MIN_TILE_SIZE = 32;

    /**
     * The maximum size (in pixels) that a tile may be rendered.
     */
    private static final int MAX_TILE_SIZE = 64;

    /**
     * Current Tile size in pixels.
     */
    private static int currentTileSize;

    /**
     * The amount (in pixels) of padding to render the game grid with.
     */
    private static final int PADDING_AMOUNT = 32;

    /**
     * The background colour for the canvas.
     */
    private static final Color CANVAS_BACKGROUND = Color.web("#404040");

    /**
     * Rotation value for actors facing North.
     */
    private static final int NORTH_ACTOR_ROTATION = 270;

    /**
     * Rotation value for actors facing East.
     */
    private static final int EAST_ACTOR_ROTATION = 0;

    /**
     * Rotation value for actors facing South.
     */
    private static final int SOUTH_ACTOR_ROTATION = 90;

    /**
     * Rotation value for actors facing West.
     */
    private static final int WEST_ACTOR_ROTATION = 180;

    /**
     * Map for caching tile, actor and item sprites (by image path).
     */
    private static final HashMap<String, Image> TILE_IMAGE_CACHE = new HashMap<>();

    /**
     * The X offset of a chip socket's text, proportional to tile size.
     */
    public static final double CHIP_SOCKET_TEXT_OFFSET_X = 0.5;

    /**
     * The Y offset of a chip socket's text, proportional to tile size.
     */
    public static final double CHIP_SOCKET_TEXT_OFFSET_Y = 0.625;

    /**
     * The font size of a chip socket's text, proportional to tile size.
     */
    public static final double CHIP_SOCKET_FONT_SIZE = 0.33;

    /**
     * Default constructor for GameCanvas.
     */
    GameCanvas() {
        // Redraw canvas when size changes.
        widthProperty().addListener(evt -> {
            if (!GameManager.isGameTimerRunning()) {
                draw();
            }
        });
        heightProperty().addListener(evt -> {
            if (!GameManager.isGameTimerRunning()) {
                draw();
            }
        });

        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(CANVAS_BACKGROUND);
        gc.fillRect(0, 0, getWidth(), getHeight());
    }

    /**
     * Draw the game grid onto the canvas.
     */
    public void draw() {
        // Draw canvas.
        double width = getWidth();
        double height = getHeight();
        GraphicsContext gc = getGraphicsContext2D();

        double paddedWidth = width - PADDING_AMOUNT - PADDING_AMOUNT;
        double paddedHeight = height - PADDING_AMOUNT - PADDING_AMOUNT;
        int scaledTileSize = Math.max(
                MIN_TILE_SIZE,
                Math.min(
                        (int) Math.min(
                                paddedWidth / GameManager.getLevelWidth(),
                                paddedHeight / GameManager.getLevelHeight()
                        ),
                        MAX_TILE_SIZE
                )
        );
        currentTileSize = scaledTileSize;
        int gridLeft = ((int) width / 2) - (GameManager.getLevelWidth() * scaledTileSize / 2);
        int gridTop = ((int) height / 2) - (GameManager.getLevelHeight() * scaledTileSize / 2);

        Point2D playerPosition = GameManager.getPlayerPosition();

        if (playerPosition != null) {
            // If the grid width is too large, center it on the player
            if (GameManager.getLevelWidth() * scaledTileSize > paddedWidth) {
                int canvasMiddleX = (int) width / 2;
                int playerCenterX = (scaledTileSize * (int) playerPosition.getX() + scaledTileSize / 2);

                gridLeft = Math.min(
                        PADDING_AMOUNT,
                        Math.max(
                                canvasMiddleX - playerCenterX,
                                (int) width - PADDING_AMOUNT - GameManager.getLevelWidth() * scaledTileSize
                        )
                );
            }

            // If the grid height is too large, center it on the player
            if (GameManager.getLevelHeight() * scaledTileSize > paddedHeight) {
                int canvasMiddleY = (int) height / 2;
                int playerCenterY = (scaledTileSize * (int) playerPosition.getY() + scaledTileSize / 2);

                gridTop = Math.min(
                        PADDING_AMOUNT,
                        Math.max(
                                canvasMiddleY - playerCenterY,
                                (int) height - PADDING_AMOUNT - GameManager.getLevelHeight() * scaledTileSize
                        )
                );
            }
        }

        // Fill the borders, don't fill the entire canvas because it causes flickering
        gc.setFill(CANVAS_BACKGROUND);
        gc.fillRect(0, 0, gridLeft, height);
        gc.fillRect(0, 0, width, gridTop);
        gc.fillRect(gridLeft + GameManager.getLevelWidth() * scaledTileSize, 0, width, height);
        gc.fillRect(0, gridTop + GameManager.getLevelHeight() * scaledTileSize, width, height);

        renderTiles(gridLeft, gridTop, scaledTileSize);
    }

    /**
     * Renders all the tiles, actors and items from the level grid.
     * @param gridLeft Where on the canvas the left of the grid should be.
     * @param gridTop Where on the canvas the top of the grid should be.
     * @param tileSize The size to render tiles.
     */
    private void renderTiles(int gridLeft, int gridTop, int tileSize) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setImageSmoothing(false);

        for (int x = 0; x < GameManager.getLevelWidth(); x++) {
            for (int y = 0; y < GameManager.getLevelHeight(); y++) {
                Point2D coordinate = new Point2D(x, y);

                Tile tile = GameManager.checkTile(coordinate);
                Actor actor = GameManager.checkActor(coordinate);
                Item item = GameManager.checkItem(coordinate);

                if (tile != null) {
                    if (TILE_IMAGE_CACHE.containsKey(tile.getImagePath())) {
                        Image img = TILE_IMAGE_CACHE.get(tile.getImagePath());
                        gc.drawImage(img, gridLeft + x * tileSize, gridTop + y * tileSize, tileSize, tileSize);
                    } else {
                        try (InputStream stream = getClass().getResourceAsStream(tile.getImagePath())) {
                            if (stream != null) {
                                Image img = new Image(stream);
                                gc.drawImage(img, gridLeft + x * tileSize, gridTop + y * tileSize,
                                        tileSize, tileSize);

                                TILE_IMAGE_CACHE.put(tile.getImagePath(), img); // Cache the image for this tile
                            } else {
                                System.out.println("Image not found for tile at: (" + x + ", " + y + ")");
                            }
                        } catch (IOException e) {
                            System.out.println("Failed loading image: " + tile.getImagePath());
                        }
                    }
                    if (tile instanceof ChipSocket chipSocket) {
                        gc.setTextAlign(TextAlignment.CENTER);
                        gc.setTextBaseline(VPos.CENTER);
                        gc.setFill(Color.BLACK);
                        Font font = new Font("Consolas Bold", tileSize * CHIP_SOCKET_FONT_SIZE);
                        gc.setFont(font);
                        gc.fillText(
                                Integer.toString(chipSocket.getRequiredChips()),
                                gridLeft + x * tileSize + tileSize * CHIP_SOCKET_TEXT_OFFSET_X,
                                gridTop + y * tileSize + tileSize * CHIP_SOCKET_TEXT_OFFSET_Y
                        );
                    }
                } else {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(gridLeft + x * tileSize, gridTop + y * tileSize, tileSize, tileSize);
                }

                if (actor != null) {
                    try (InputStream stream = getClass().getResourceAsStream(actor.getImagePath())) {
                        if (stream != null) {
                            Image rotatedImage = getRotatedImage(actor);
                            gc.drawImage(rotatedImage, gridLeft + x * tileSize, gridTop + y * tileSize,
                                    tileSize, tileSize);
                        } else {
                            System.out.println("Image not found for actor at: (" + x + ", " + y + ")");
                        }
                    } catch (IOException e) {
                        System.out.println("Failed loading image: " + actor.getImagePath());
                    }
                }

                if (item != null) {
                    if (TILE_IMAGE_CACHE.containsKey(item.getImagePath())) {
                        Image img = TILE_IMAGE_CACHE.get(item.getImagePath());
                        gc.drawImage(img, gridLeft + x * tileSize, gridTop + y * tileSize, tileSize, tileSize);
                    } else {
                        try (InputStream stream = getClass().getResourceAsStream(item.getImagePath())) {
                            if (stream != null) {
                                Image img = new Image(stream);
                                gc.drawImage(img, gridLeft + x * tileSize, gridTop + y * tileSize,
                                        tileSize, tileSize);

                                TILE_IMAGE_CACHE.put(item.getImagePath(), img); // Cache the image for this item
                            } else {
                                System.out.println("Image not found for item at: (" + x + ", " + y + ")");
                            }
                        } catch (IOException e) {
                            System.out.println("Failed loading image: " + item.getImagePath());
                        }
                    }
                }
            }
        }
    }

    /**
     * Get a rotated Image with the correct rotation for an Actor.
     * @param actor The actor to use the rotation of.
     * @return The rotated Image
     */
    private static Image getRotatedImage(Actor actor) throws IOException {
        String spritePath = actor.getImagePath();

        // Get the cached image if possible, otherwise load it from resources
        Image img;
        if (TILE_IMAGE_CACHE.containsKey(spritePath)) {
            img = TILE_IMAGE_CACHE.get(spritePath);
        } else {
            InputStream stream = GameCanvas.class.getResourceAsStream(spritePath);
            if (stream == null) {
                throw new IOException("Image stream was null!");
            }

            img = new Image(stream);
        }

        int rotation = NORTH_ACTOR_ROTATION;
        switch (actor.getFacingDir()) {
            case EAST -> {
                rotation = EAST_ACTOR_ROTATION;
            }
            case SOUTH -> {
                rotation = SOUTH_ACTOR_ROTATION;
            }
            case WEST -> {
                rotation = WEST_ACTOR_ROTATION;
            }
            default -> {
                // Do nothing
            }
        }

        ImageView iv = new ImageView(img);
        iv.setRotate(rotation);

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return iv.snapshot(params, null);
    }

    public static int getCurrentTileSize() {
        return currentTileSize;
    }
}
