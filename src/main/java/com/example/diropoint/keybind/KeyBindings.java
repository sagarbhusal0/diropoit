package com.example.diropoint.keybind;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static KeyBinding openWaypointScreen;
    public static KeyBinding quickAddWaypoint;
    public static KeyBinding toggleWaypointVisibility;
    public static KeyBinding teleportToLastWaypoint;

    public static void register() {
        openWaypointScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.diropoint.open_waypoint_screen",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "category.diropoint.general"
        ));

        quickAddWaypoint = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.diropoint.quick_add_waypoint",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            "category.diropoint.general"
        ));

        toggleWaypointVisibility = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.diropoint.toggle_visibility",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "category.diropoint.general"
        ));

        teleportToLastWaypoint = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.diropoint.teleport_last",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            "category.diropoint.general"
        ));
    }
} 