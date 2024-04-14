package swan.g09.cs230a2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.geometry.Point2D;

/**
 * The level class handles reading and parsing level format data.
 * @author Barnaby Morley-Smith
 * @version 0.1
 */
public class Level {

    /**
     * Regex pattern for matching lines containing a button connection.
     */
    private static final Pattern BUTTON_CONN_PATTERN
            = Pattern.compile("^\\((\\d+), *(\\d+)\\) -> \\((\\d+), *(\\d+)\\)");

    /**
     * Regex pattern for matching lines containing a socket count.
     */
    private static final Pattern SOCKET_COUNT_PATTERN
            = Pattern.compile("^\\((\\d+), *(\\d+)\\) # (\\d+)");

    /**
     * Regex pattern for matching lines containing an actor's facing direction.
     */
    private static final Pattern ACTOR_FACING_PATTERN
            = Pattern.compile("^\\((\\d+), *(\\d+)\\) @ ([NESW])");

    /**
     * Regex pattern for matching lines containing a slot in the inventory.
     */
    private static final Pattern INVENTORY_SLOT_PATTERN
            = Pattern.compile("^([crygbe]) */ *(\\d+)");

    /**
     * In save files, whether this was from a custom save and if not, whether it is the last level.
     */
    private static final Pattern LEVEL_FLAGS_PATTERN
            = Pattern.compile("(\\d+|null), *([01])");

    /**
     * Whether a boat path currently has a boat on it.
     */
    private static final Pattern BOAT_PRESENCE_PATTERN =
            Pattern.compile("^\\((\\d+), *(\\d+)\\) _(\\??)");

    /**
     * Index used for groups in regex matching.
     */
    private static final int REGEX_MATCHER_GROUP_1 = 1;

    /**
     * Index used for groups in regex matching.
     */
    private static final int REGEX_MATCHER_GROUP_2 = 2;

    /**
     * Index used for groups in regex matching.
     */
    private static final int REGEX_MATCHER_GROUP_3 = 3;

    /**
     * Index used for groups in regex matching.
     */
    private static final int REGEX_MATCHER_GROUP_4 = 4;

    /**
     * The file handle for the level file.
     */
    private final File file;

    /**
     * The input stream for the level file if it's being read from resources.
     */
    private final InputStream stream;

    /**
     * The list of strings used for parsing the tile grid into a layer.
     */
    private final LinkedList<String> tileGrid = new LinkedList<>();

    /**
     * The list of strings used for parsing the actor grid into a layer.
     */
    private final LinkedList<String> actorGrid = new LinkedList<>();

    /**
     * The list of strings used for parsing the item grid into a layer.
     */
    private final LinkedList<String> itemGrid = new LinkedList<>();

    /**
     * Map storing the socket counts for sockets at given coordinates.
     */
    private final Map<Point2D, Integer> socketCounts = new HashMap<>();

    /**
     * Map storing the directions actors are facing at given coordinates.
     */
    private final Map<Point2D, Direction> actorDirections = new HashMap<>();

    /**
     * Map storing the connections to traps for buttons at given coordinates.
     */
    private final Map<Point2D, LinkedList<Point2D>> buttonConnections = new HashMap<>();

    /**
     * Map storing the precense of boats and whether it is reversing.
     */
    private final Map<Point2D, Boolean> boatPresences = new HashMap<>();

    /**
     * The inventory read from the level file. (Defaults to empty inventory.
     */
    private final int[] inventory = new int[]{0, 0, 0, 0, 0, 0};

    /**
     * The level number read from the level file, if the level is an autosave.
     *
     */
    private Integer levelNumber;

    /**
     * The boolean read from the level file, if the level is an autosave.
     *
     */
    private boolean isLastLevel;

    /**
     * The width of the level, specified in the parsed file.
     */
    private int width;

    /**
     * The height of the level, specified in the parsed file.
     */
    private int height;

    /**
     * The duration of the level, specified in the parsed file.
     */
    private int duration;


    /**
     * Stores the instances of the barnacles.
     */
    public static ArrayList<Barnacle> barnacles = new ArrayList<>();

    /**
     *  Stores block instances
     */
    public static ArrayList<Block> blocksList = new ArrayList<>();

    /**
     * The fov of the level, specified in the parsed file.
     */
    private double levelFov;

    /**
     * Default constructor for Level.
     *
     * @param filePath The file path to load the level from.
     * @throws FileNotFoundException If the file path does not point to a file
     * an exception will be thrown.
     */
    public Level(String filePath) throws FileNotFoundException {
        file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException("filePath does not point to a file.");
        }
        stream = null;
    }

    /**
     * Constructor for Level using resources folder.
     *
     * @param resourceStream The input stream to load the level from.
     * @throws IOException If the file could not be read from resources.
     */
    public Level(InputStream resourceStream) throws IOException {
        file = null;
        stream = resourceStream;
    }

    /**
     * Reads the level data from the file path provided in the constructor.
     * Separate from constructor to allow reloading a level from disk when
     * necessary.
     *
     * @throws FileNotFoundException If the file no longer exists, this method
     * throws an exception.
     * @throws InputMismatchException If the level file has errors, this method
     * throws an exception.
     */
    public void readFile() throws FileNotFoundException, InputMismatchException {
        Scanner reader;
        if (stream == null) {
            reader = new Scanner(file);
        } else {
            reader = new Scanner(stream);
        }
        int lineNumber = 1; // For descriptive exceptions.

        try {
            // Read grid size then progress to the next line
            reader.useDelimiter("(\\r?\\n)|(, *)");
            width = reader.nextInt();
            height = reader.nextInt();
            lineNumber++;
            reader.nextLine();
            reader.useDelimiter("(\\r?\\n)");

            // Read level duration
            duration = reader.nextInt();
            lineNumber++;
            reader.nextLine();

            //Read FOV
            levelFov = reader.nextDouble();
            lineNumber += 2;
            reader.nextLine();
            reader.nextLine();

            // Read tile grid
            tileGrid.clear();
            for (int i = 0; i < height; i++) {
                lineNumber++;
                tileGrid.add(reader.nextLine());
            }
            lineNumber++;
            reader.nextLine();

            // Read actor grid
            actorGrid.clear();
            for (int i = 0; i < height; i++) {
                lineNumber++;
                actorGrid.add(reader.nextLine());
            }
            lineNumber++;
            reader.nextLine();

            // Read item grid
            itemGrid.clear();
            for (int i = 0; i < height; i++) {
                lineNumber++;
                itemGrid.add(reader.nextLine());
            }

            // Read meta data (button connections, socket counts etc.)
            while (reader.hasNextLine()) {
                lineNumber++;
                String line = reader.nextLine();
                if (line.matches(BUTTON_CONN_PATTERN.pattern())) {
                    // Button connection
                    addButtonConnection(line);
                } else if (line.matches(SOCKET_COUNT_PATTERN.pattern())) {
                    // Socket count
                    setSocketCount(line);
                } else if (line.matches(ACTOR_FACING_PATTERN.pattern())) {
                    // Actor facing
                    setActorFacing(line);
                } else if (line.matches(INVENTORY_SLOT_PATTERN.pattern())) {
                    // Inventory slot
                    setInventorySlot(line);
                } else if (line.matches(LEVEL_FLAGS_PATTERN.pattern())) {
                    Matcher matcher = LEVEL_FLAGS_PATTERN.matcher(line);
                    if (matcher.matches()) {
                        if (matcher.group(REGEX_MATCHER_GROUP_1).equals("null")) {
                            levelNumber = null;
                        } else {
                            levelNumber = Integer.valueOf(matcher.group(REGEX_MATCHER_GROUP_1));
                        }
                        isLastLevel = matcher.group(REGEX_MATCHER_GROUP_2).equals("1");
                    }
                } else if (line.matches(BOAT_PRESENCE_PATTERN.pattern())) {
                    // Boat on boat path
                    setBoatPresence(line);
                }
            }
        } catch (InputMismatchException e) {
            reader.close();
            throw new InputMismatchException("Level format was incorrect at line " + lineNumber);
        } catch (NoSuchElementException e) {
            reader.close();
            throw new InputMismatchException("Level file ended early at " + (lineNumber - 1));
        }

        reader.close();
    }

    /**
     * Adds a button connection based on the provided line.
     * @param line The line to be parsed.
     */
    private void addButtonConnection(String line) {
        Matcher matcher = BUTTON_CONN_PATTERN.matcher(line);
        if (matcher.matches()) {
            int buttonX = Integer.parseInt(matcher.group(REGEX_MATCHER_GROUP_1));
            int buttonY = Integer.parseInt(matcher.group(REGEX_MATCHER_GROUP_2));
            int trapX = Integer.parseInt(matcher.group(REGEX_MATCHER_GROUP_3));
            int trapY = Integer.parseInt(matcher.group(REGEX_MATCHER_GROUP_4));

            Point2D coordinate = new Point2D(buttonX, buttonY);
            if (!buttonConnections.containsKey(coordinate)) {
                buttonConnections.put(coordinate, new LinkedList<>());
            }
            LinkedList<Point2D> connectionList = buttonConnections.get(coordinate);
            connectionList.add(new Point2D(trapX, trapY));
        }
    }

    /**
     * Set the socket count based on the provided line.
     *
     * @param line The line to be parsed.
     */
    private void setSocketCount(String line) {
        Matcher matcher = SOCKET_COUNT_PATTERN.matcher(line);
        if (matcher.matches()) {
            int socketX = Integer.parseInt(matcher.group(REGEX_MATCHER_GROUP_1));
            int socketY = Integer.parseInt(matcher.group(REGEX_MATCHER_GROUP_2));
            int count = Integer.parseInt(matcher.group(REGEX_MATCHER_GROUP_3));

            socketCounts.put(new Point2D(socketX, socketY), count);
        }
    }

    /**
     * Set the direction an actor is facing based on the provided line.
     *
     * @param line The line to be parsed.
     */
    private void setActorFacing(String line) {
        Matcher matcher = ACTOR_FACING_PATTERN.matcher(line);
        if (matcher.matches()) {
            int actorX = Integer.parseInt(matcher.group(REGEX_MATCHER_GROUP_1));
            int actorY = Integer.parseInt(matcher.group(REGEX_MATCHER_GROUP_2));

            Direction dir = Direction.parseString(matcher.group(REGEX_MATCHER_GROUP_3));

            actorDirections.put(new Point2D(actorX, actorY), dir);
        }
    }

    /**
     * Sets a slot in the player's inventory based on the provided line.
     *
     * @param line The line to be parsed.
     */
    private void setInventorySlot(String line) {
        Matcher matcher = INVENTORY_SLOT_PATTERN.matcher(line);
        if (matcher.matches()) {
            String slot = matcher.group(REGEX_MATCHER_GROUP_1);
            int count = Integer.parseInt(matcher.group(REGEX_MATCHER_GROUP_2));

            Player.InventorySlot inventorySlot = Player.InventorySlot.fromString(slot);
            inventory[inventorySlot.ordinal()] = count;
        }
    }

    /**
     * Puts a boat on a boat path based on the provided line.
     * @param line The line to be parsed
     */
    private void setBoatPresence(String line) {
        Matcher matcher = BOAT_PRESENCE_PATTERN.matcher(line);
        if (matcher.matches()) {
            int boatPathX = Integer.parseInt(matcher.group(REGEX_MATCHER_GROUP_1));
            int boatPathY = Integer.parseInt(matcher.group(REGEX_MATCHER_GROUP_2));
            Boolean boatPathReversing = matcher.group(REGEX_MATCHER_GROUP_3).equals("?");

            boatPresences.put(new Point2D(boatPathX, boatPathY), boatPathReversing);
        }
    }

    /**
     * Creates the correct Tile for the corresponding character in the level format key.
     * @param gridChar The character to convert.
     * @param coordinate The coordinate of the tile.
     * @return The Tile which was created.
     */
    private Tile parseTileFromChar(Character gridChar, Point2D coordinate) {
        return switch (gridChar) {
            case 'P' -> new Path(coordinate);
            case 'D' -> new Dirt(coordinate);
            case 'W' -> new Wall(coordinate);
            case 'E' -> new Exit(coordinate);
            case 'C' -> new Button(coordinate);
            case 'T' -> new Trap(coordinate);
            case 'O' -> new Water(coordinate);
            case 'S' -> new ChipSocket(coordinate);
            case 'R' -> new LockedDoor(coordinate, 'R');
            case 'G' -> new LockedDoor(coordinate, 'G');
            case 'Y' -> new LockedDoor(coordinate, 'Y');
            case 'B' -> new LockedDoor(coordinate, 'B');
            case 'I' -> new Ice(coordinate, Ice.IceType.NORMAL);
            case 'U' -> new Ice(coordinate, Ice.IceType.TOP_LEFT);
            case 'J' -> new Ice(coordinate, Ice.IceType.TOP_RIGHT);
            case 'K' -> new Ice(coordinate, Ice.IceType.BOTTOM_LEFT);
            case 'L' -> new Ice(coordinate, Ice.IceType.BOTTOM_RIGHT);
            case 'A' -> new BoatPath(coordinate, Direction.NORTH, false);
            case 'F' -> new BoatPath(coordinate, Direction.EAST, false);
            case 'H' -> new BoatPath(coordinate, Direction.SOUTH, false);
            case 'M' -> new BoatPath(coordinate, Direction.WEST, false);
            default -> null;
        };
    }

    /**
     * Creates the correct Actor for the corresponding character in the level
     * format key.
     *
     * @param gridChar The character to convert.
     * @param coordinate The coordinate of the tile.
     * @return The Actor which was created.
     */
    private Actor parseActorFromChar(Character gridChar, Point2D coordinate) {
        return switch (gridChar) {
            case '*' ->
                new Player(coordinate);
            case '#' ->
                new Block(coordinate);
            case '@' ->
                new PinkBall(coordinate);
            case '%' ->
                new Bug(coordinate, false);
            case '$' ->
                new Bug(coordinate, true);
            case '^' ->
                new Frog(coordinate);
            case ';' ->
                new Barnacle(coordinate);
            default ->
                null;
        };
    }

    /**
     * Creates the correct Item for the corresponding character in the level
     * format key.
     *
     * @param gridChar The character to convert.
     * @param coordinate The coordinate of the tile.
     * @return The Item which was created.
     */
    private Item parseItemFromChar(Character gridChar, Point2D coordinate) {
        return switch (gridChar) {
            case 'c' ->
                new ComputerChip(coordinate);
            case 'r' ->
                new Key(coordinate, 'R');
            case 'g' ->
                new Key(coordinate, 'G');
            case 'y' ->
                new Key(coordinate, 'Y');
            case 'b' ->
                new Key(coordinate, 'B');
            case ']' ->
                new Speed(coordinate);
            case 'v' ->
                new Invinc(coordinate);
            case 'x' ->
                new ExtraLife(coordinate);
            case '+' ->
                new IncreaseTime(coordinate);
            default ->
                null;
        };
    }

    /**
     * Constructs the tile layer for the level data that has been loaded.
     *
     * @return The Layer storing all the Tiles in the level.
     */
    public Layer<Tile> getTileLayer() {
        Layer<Tile> layer = new Layer<>(this);

        for (int y = 0; y < height; y++) {
            String line = tileGrid.get(y);
            for (int x = 0; x < Math.min(width, line.length()); x++) {
                Point2D coordinate = new Point2D(x, y);
                Tile tile = parseTileFromChar(line.charAt(x), coordinate);

                if (tile != null) {
                    if (tile.getType() == TileType.CHIP_SOCKET && socketCounts.containsKey(coordinate)) {
                        int socketCount = socketCounts.get(coordinate);
                        ChipSocket socket = (ChipSocket) tile;
                        socket.setRequiredChips(socketCount);
                    }

                    if (tile.getType() == TileType.BOAT_PATH && boatPresences.containsKey(coordinate)) {
                        BoatPath boatPath = (BoatPath) tile;
                        boatPath.moveBoatTo(false);
                        if (boatPresences.get(coordinate)) {
                            boatPath.setReverse();
                        }
                    }

                    layer.setAtPosition(coordinate, tile);
                }
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Point2D coordinate = new Point2D(x, y);
                Tile tile = layer.getAtPosition(coordinate);
                if (tile != null && tile.getType() == TileType.BUTTON && buttonConnections.containsKey(coordinate)) {
                    ArrayList<Trap> connectedTraps = new ArrayList<>();
                    LinkedList<Point2D> trapCoordinates = buttonConnections.get(coordinate);
                    for (Point2D trapCoordinate : trapCoordinates) {
                        Tile maybeTrap = layer.getAtPosition(trapCoordinate);
                        if (maybeTrap != null && maybeTrap.getType() == TileType.TRAP) {
                            connectedTraps.add((Trap) maybeTrap);
                        }
                    }
                    Button button = (Button) tile;
                    button.setLinkedTraps(connectedTraps);
                }
            }
        }

        return layer;
    }

    /**
     * Constructs the actor layer for the level data that has been loaded.
     *
     * @return The Layer storing all the Actors in the level.
     */
    public Layer<Actor> getActorLayer() {
        Layer<Actor> layer = new Layer<>(this);

        for (int y = 0; y < height; y++) {
            String line = actorGrid.get(y);
            for (int x = 0; x < Math.min(width, line.length()); x++) {
                Point2D coordinate = new Point2D(x, y);
                Actor actor = parseActorFromChar(line.charAt(x), coordinate);
                if (actor != null) {
                    if (actorDirections.containsKey(coordinate)) {
                        actor.setFacingDir(actorDirections.get(coordinate));
                    }

                    if (actor instanceof Player player) {
                        player.setInventory(inventory);
                    }

                    if (actor instanceof Barnacle barnacle) {
                        barnacles.add(barnacle);
                    }

                    if (actor instanceof Block block) {
                        blocksList.add(block);
                    }

                    layer.setAtPosition(coordinate, actor);
                }
            }
        }

        return layer;
    }

    /**
     * Constructs the item layer for the level data that has been loaded.
     *
     * @return The Layer storing all the Items in the level.
     */
    public Layer<Item> getItemLayer() {
        Layer<Item> layer = new Layer<>(this);

        for (int y = 0; y < height; y++) {
            String line = itemGrid.get(y);
            for (int x = 0; x < Math.min(width, line.length()); x++) {
                Point2D coordinate = new Point2D(x, y);
                Item item = parseItemFromChar(line.charAt(x), coordinate);
                if (item != null) {
                    layer.setAtPosition(coordinate, item);
                }
            }
        }

        return layer;
    }

    /**
     * Get the duration of the level from the parsed file.
     *
     * @return The duration of the level.
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Returns whether the level is the last in the load order, if loaded from
     * autosave.
     *
     * @return if level is last in load order
     *
     */
    public boolean isLastLevel() {
        return isLastLevel;
    }

    /**
     * Returns the player's inventory.
     * @return the player's inventory.
     * */
    public int[] getInventory() {
        return inventory;
    }

    /**
     * Returns the level number, if loading from an autosave.
     *
     * @return the level number.
     *
     */
    public Integer getLevelNumber() {
        return levelNumber;
    }

    /**
     * Gets the width of the currently loaded level.
     *
     * @return The width of the level.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the currently loaded level.
     *
     * @return The height of the level.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the fov of the currently loaded level.
     * @return The height of the level.
     */
    public double getLevelFov() {
        return levelFov;
    }
}
