package swan.g09.cs230a2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The InputManager class allows other classes to create handlers for input events
 * it provides static helper functions for other parts of the program.
 * Automatically triggers event handlers when provided with a JavaFX scene.
 *
 * @author Barnaby Morley-Smith
 * @version 0.1
 */
public final class InputManager {
    /**
     * Stores a mapping from key codes to a list of handlers to run when a key is pressed.
     */
    private static final HashMap<KeyCode, ArrayList<Runnable>> KEYDOWN_HANDLERS = new HashMap<>();
    /**
     * Stores a mapping from key codes to a list of handlers to run when a key is released.
     */
    private static final HashMap<KeyCode, ArrayList<Runnable>> KEYUP_HANDLERS = new HashMap<>();
    /**
     * Stores a mapping from key codes to a list of handlers to run every tick a key is pressed down.
     */
    private static final HashMap<KeyCode, ArrayList<Runnable>> TICK_HANDLERS = new HashMap<>();
    /**
     * Stores the currently pressed keys.
     */
    private static final HashSet<KeyCode> PRESSED_KEYS = new HashSet<>();

    /**
     * Overides the default constructor to enforce that this class will never be instantiated
     */
    private InputManager() {
        throw new AssertionError();
    }

    /**
     * EventHandler for handling general key events from the scene.
     * Runs the relevant handlers for specific events.
     */
    private static final EventHandler<? super KeyEvent> KEY_EVENT_HANDLER = (KeyEvent keyEvent) -> {
        // Key-down handlers
        if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
            // Get list of handlers for the event key code
            ArrayList<Runnable> handlers = KEYDOWN_HANDLERS.get(keyEvent.getCode());
            if (handlers != null) {
                // Run each handler individually
                for (Runnable handler : handlers) {
                    handler.run();
                }
            }

            // Add key to list of pressed keys
            PRESSED_KEYS.add(keyEvent.getCode());
        }

        // Key-up handlers
        if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED) {
            // Get list of handlers for the event key code
            ArrayList<Runnable> handlers = KEYDOWN_HANDLERS.get(keyEvent.getCode());
            if (handlers != null) {
                // Run each handler individually
                for (Runnable handler : handlers) {
                    handler.run();
                }
            }

            // Remove key from list of pressed keys
            PRESSED_KEYS.remove(keyEvent.getCode());
        }
    };

    /**
     * The scene which is being listened to for events.
     */
    private static Scene scene;

    /**
     * Sets the scene to listen to events from.
     * Stops listening on the previous scene if it exists.
     *
     * @param newScene The scene to listen to.
     */
    public static void setScene(Scene newScene) {
        if (scene != null) {
            scene.removeEventFilter(KeyEvent.ANY, KEY_EVENT_HANDLER);
        }

        PRESSED_KEYS.clear();
        scene = newScene;
        scene.addEventFilter(KeyEvent.ANY, KEY_EVENT_HANDLER);
    }

    /**
     * De-registers all currently registered event handlers.
     */
    public static void clearAllHandlers() {
        clearKeydownHandlers();
        clearKeyupHandlers();
        clearTickHandlers();
    }

    /**
     * De-registers event handlers for key-down events.
     */
    public static void clearKeydownHandlers() {
        KEYDOWN_HANDLERS.clear();
    }

    /**
     * De-registers event handlers for key-up events.
     */
    public static void clearKeyupHandlers() {
        KEYUP_HANDLERS.clear();
    }

    /**
     * De-registers event handlers for tick-based events.
     */
    public static void clearTickHandlers() {
        TICK_HANDLERS.clear();
    }

    /**
     * Adds a Runnable to the list of key-down handlers for the specified key code.
     *
     * @param keyCode The key code to listen for.
     * @param handler The handler to be run when events are received.
     */
    public static void addKeydownHandler(KeyCode keyCode, Runnable handler) {
        // Ensure a list of handlers exists in the map for the specified key code
        if (!KEYDOWN_HANDLERS.containsKey(keyCode)) {
            KEYDOWN_HANDLERS.put(keyCode, new ArrayList<>());
        }

        // Add the handler to the list
        ArrayList<Runnable> list = KEYDOWN_HANDLERS.get(keyCode);
        list.add(handler);
    }

    /**
     * Adds a Runnable to the list of key-up handlers for the specified key code.
     *
     * @param keyCode The key code to listen for.
     * @param handler The handler to be run when events are received.
     */
    public static void addKeyupHandler(KeyCode keyCode, Runnable handler) {
        // Ensure a list of handlers exists in the map for the specified key code
        if (!KEYUP_HANDLERS.containsKey(keyCode)) {
            KEYUP_HANDLERS.put(keyCode, new ArrayList<>());
        }

        // Add the handler to the list
        ArrayList<Runnable> list = KEYUP_HANDLERS.get(keyCode);
        list.add(handler);
    }

    /**
     * Adds a Runnable to the list of tick-based handlers for the specified key code.
     *
     * @param keyCode The key code to listen for.
     * @param handler The handler to be run when events are received.
     */
    public static void addTickHandler(KeyCode keyCode, Runnable handler) {
        // Ensure a list of handlers exists in the map for the specified key code
        if (!TICK_HANDLERS.containsKey(keyCode)) {
            TICK_HANDLERS.put(keyCode, new ArrayList<>());
        }

        // Add the handler to the list
        ArrayList<Runnable> list = TICK_HANDLERS.get(keyCode);
        list.add(handler);
    }


    /**
     * Method run by Timer to trigger tick-based event handlers.
     */
    public static void tick() {
        // Iterate over currently pressed down keys
        for (KeyCode keyCode : PRESSED_KEYS) {
            // Get list of handlers for key code
            ArrayList<Runnable> handlers = TICK_HANDLERS.get(keyCode);
            if (handlers != null) {
                // Run each handler individually
                for (Runnable handler : handlers) {
                    handler.run();
                }
            }
        }
    }
}
