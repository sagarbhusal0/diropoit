package com.example.diropoint.keybind;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    private static final String CATEGORY = "key.categories.diropoint";

    // Main waypoint management
    public static KeyBinding openWaypointScreen;
    public static KeyBinding quickAddWaypoint;
    public static KeyBinding toggleWaypointVisibility;
    public static KeyBinding teleportToLastWaypoint;

    // HUD controls
    public static KeyBinding toggleHud;
    public static KeyBinding cycleHudPosition;
    public static KeyBinding increaseHudScale;
    public static KeyBinding decreaseHudScale;

    // Waypoint navigation
    public static KeyBinding cycleNearbyWaypoints;
    public static KeyBinding focusNextWaypoint;
    public static KeyBinding focusPreviousWaypoint;
    public static KeyBinding clearTemporaryWaypoints;

    public static void register() {
        // Main waypoint management
        openWaypointScreen = registerKeybind(
            "key.diropoint.open_screen",
            GLFW.GLFW_KEY_B,
            CATEGORY
        );

        quickAddWaypoint = registerKeybind(
            "key.diropoint.quick_add",
            GLFW.GLFW_KEY_N,
            CATEGORY
        );

        toggleWaypointVisibility = registerKeybind(
            "key.diropoint.toggle_visibility",
            GLFW.GLFW_KEY_V,
            CATEGORY
        );

        teleportToLastWaypoint = registerKeybind(
            "key.diropoint.teleport_last",
            GLFW.GLFW_KEY_Y,
            CATEGORY
        );

        // HUD controls
        toggleHud = registerKeybind(
            "key.diropoint.toggle_hud",
            GLFW.GLFW_KEY_H,
            CATEGORY
        );

        cycleHudPosition = registerKeybind(
            "key.diropoint.cycle_hud_position",
            GLFW.GLFW_KEY_P,
            CATEGORY
        );

        increaseHudScale = registerKeybind(
            "key.diropoint.increase_hud_scale",
            GLFW.GLFW_KEY_EQUAL,
            CATEGORY
        );

        decreaseHudScale = registerKeybind(
            "key.diropoint.decrease_hud_scale",
            GLFW.GLFW_KEY_MINUS,
            CATEGORY
        );

        // Waypoint navigation
        cycleNearbyWaypoints = registerKeybind(
            "key.diropoint.cycle_nearby",
            GLFW.GLFW_KEY_C,
            CATEGORY
        );

        focusNextWaypoint = registerKeybind(
            "key.diropoint.focus_next",
            GLFW.GLFW_KEY_RIGHT_BRACKET,
            CATEGORY
        );

        focusPreviousWaypoint = registerKeybind(
            "key.diropoint.focus_previous",
            GLFW.GLFW_KEY_LEFT_BRACKET,
            CATEGORY
        );

        clearTemporaryWaypoints = registerKeybind(
            "key.diropoint.clear_temporary",
            GLFW.GLFW_KEY_DELETE,
            CATEGORY
        );
    }

    private static KeyBinding registerKeybind(String name, int key, String category) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
            name,
            InputUtil.Type.KEYSYM,
            key,
            category
        ));
    }
} 