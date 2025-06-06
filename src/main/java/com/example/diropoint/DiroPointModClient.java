package com.example.diropoint;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class DiroPointModClient implements ClientModInitializer {
    private static KeyBinding openWaypointsMenu;
    
    @Override
    public void onInitializeClient() {
        // Register key binding to open waypoints menu (default key: M)
        openWaypointsMenu = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.diropoint.open_waypoints_menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            "category.diropoint"
        ));
        
        // Handle key press event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openWaypointsMenu.wasPressed()) {
                if (client.player != null) {
                    client.setScreen(new WaypointScreen());
                }
            }
        });
    }
}
