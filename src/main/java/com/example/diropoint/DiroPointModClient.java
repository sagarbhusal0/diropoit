package com.example.diropoint;

import com.example.diropoint.config.ModConfig;
import com.example.diropoint.keybind.KeyBindings;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.text.Text;

public class DiroPointModClient implements ClientModInitializer {
    public static ModConfig CONFIG;
    
    @Override
    public void onInitializeClient() {
        // Register config
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        
        // Register keybindings
        KeyBindings.register();
        
        // Handle key press events
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (KeyBindings.openWaypointScreen.wasPressed()) {
                if (client.player != null) {
                    client.setScreen(new WaypointScreen());
                }
            }
            
            if (KeyBindings.quickAddWaypoint.wasPressed()) {
                if (client.player != null) {
                    WaypointManager.addWaypoint(
                        "Quick Waypoint",
                        (int) client.player.getX(),
                        (int) client.player.getY(),
                        (int) client.player.getZ()
                    );
                    if (CONFIG.enableSounds) {
                        // Play success sound
                        client.player.playSound(net.minecraft.sound.SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    }
                }
            }
            
            if (KeyBindings.toggleWaypointVisibility.wasPressed()) {
                CONFIG.showInWorld = !CONFIG.showInWorld;
                String state = CONFIG.showInWorld ? "visible" : "hidden";
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("Waypoints are now " + state), true);
                }
            }
            
            if (KeyBindings.teleportToLastWaypoint.wasPressed()) {
                if (client.player != null && client.player.hasPermissionLevel(2)) {
                    Waypoint lastWaypoint = WaypointManager.getLastWaypoint();
                    if (lastWaypoint != null) {
                        WaypointTeleportCommand.teleportToWaypoint(client.player, lastWaypoint);
                    }
                }
            }
        });
    }
}
