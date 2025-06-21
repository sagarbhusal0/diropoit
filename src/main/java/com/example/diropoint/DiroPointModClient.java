package com.example.diropoint;

import com.example.diropoint.config.ModConfig;
import com.example.diropoint.keybind.KeyBindings;
import com.example.diropoint.render.WaypointHudRenderer;
import com.example.diropoint.render.WaypointRenderer;
import com.example.diropoint.gui.WaypointScreen;
import com.example.diropoint.waypoint.Waypoint;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;

public class DiroPointModClient implements ClientModInitializer {
    public static ModConfig CONFIG;
    private static boolean hudVisible = true;
    private static int focusedWaypointIndex = -1;

    @Override
    public void onInitializeClient() {
        // Register config
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        
        // Register keybindings
        KeyBindings.register();
        
        // Register renderers
        WorldRenderEvents.AFTER_TRANSLUCENT.register(WaypointRenderer::renderWaypoints);
        HudRenderCallback.EVENT.register(WaypointHudRenderer::render);
        
        // Register cleanup handler
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null && lastDimension != null) {
                // World unloaded, clean up
                WaypointManager.getInstance().save();
                lastDimension = null;
                focusedWaypointIndex = -1;
            }
        });

        // Handle key press events
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Main waypoint management
            if (KeyBindings.openWaypointScreen.wasPressed()) {
                client.setScreen(new WaypointScreen());
            }
            
            if (KeyBindings.quickAddWaypoint.wasPressed()) {
                Vec3d pos = client.player.getPos();
                WaypointManager.getInstance().addWaypoint(new Waypoint(
                    "Quick Waypoint",
                    pos,
                    client.world.getDimension(),
                    CONFIG.defaultWaypointColor,
                    Waypoint.WaypointIcon.getDefaultForDimension(client.world.getDimension())
                ));
                if (CONFIG.enableSounds) {
                    client.player.playSound(net.minecraft.sound.SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                }
                client.player.sendMessage(Text.literal("Waypoint added at " + 
                    String.format("(%.0f, %.0f, %.0f)", pos.x, pos.y, pos.z)), true);
            }
            
            if (KeyBindings.toggleWaypointVisibility.wasPressed()) {
                CONFIG.showInWorld = !CONFIG.showInWorld;
                String state = CONFIG.showInWorld ? "visible" : "hidden";
                client.player.sendMessage(Text.literal("Waypoints are now " + state), true);
            }
            
            if (KeyBindings.teleportToLastWaypoint.wasPressed() && client.player.hasPermissionLevel(2)) {
                Waypoint lastWaypoint = WaypointManager.getInstance().getLastWaypoint();
                if (lastWaypoint != null) {
                    WaypointManager.teleportToWaypoint(client.player, lastWaypoint);
                }
            }

            // HUD controls
            if (KeyBindings.toggleHud.wasPressed()) {
                hudVisible = !hudVisible;
                String state = hudVisible ? "visible" : "hidden";
                client.player.sendMessage(Text.literal("Waypoint HUD is now " + state), true);
            }

            if (KeyBindings.cycleHudPosition.wasPressed()) {
                ModConfig.HudPosition[] positions = ModConfig.HudPosition.values();
                CONFIG.hudPosition = positions[(CONFIG.hudPosition.ordinal() + 1) % positions.length];
                client.player.sendMessage(Text.literal("HUD position: " + CONFIG.hudPosition.name()), true);
            }

            if (KeyBindings.increaseHudScale.wasPressed()) {
                CONFIG.hudScale = Math.min(2.0f, CONFIG.hudScale + 0.1f);
                client.player.sendMessage(Text.literal(String.format("HUD scale: %.1f", CONFIG.hudScale)), true);
            }

            if (KeyBindings.decreaseHudScale.wasPressed()) {
                CONFIG.hudScale = Math.max(0.5f, CONFIG.hudScale - 0.1f);
                client.player.sendMessage(Text.literal(String.format("HUD scale: %.1f", CONFIG.hudScale)), true);
            }

            // Waypoint navigation
            if (KeyBindings.cycleNearbyWaypoints.wasPressed()) {
                DimensionType dimension = client.world.getDimension();
                var waypoints = WaypointManager.getInstance().getWaypoints(dimension);
                if (!waypoints.isEmpty()) {
                    focusedWaypointIndex = (focusedWaypointIndex + 1) % waypoints.size();
                    Waypoint focused = waypoints.get(focusedWaypointIndex);
                    client.player.sendMessage(Text.literal("Focused waypoint: " + focused.getName()), true);
                }
            }

            if (KeyBindings.focusNextWaypoint.wasPressed()) {
                cycleFocusedWaypoint(client, 1);
            }

            if (KeyBindings.focusPreviousWaypoint.wasPressed()) {
                cycleFocusedWaypoint(client, -1);
            }

            if (KeyBindings.clearTemporaryWaypoints.wasPressed()) {
                WaypointManager.getInstance().clearTemporaryWaypoints();
                client.player.sendMessage(Text.literal("Cleared all temporary waypoints"), true);
            }
        });

        // Handle dimension changes
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.world != null) {
                DimensionType currentDimension = client.world.getDimension();
                if (lastDimension != null && lastDimension != currentDimension) {
                    WaypointManager.getInstance().onPlayerChangeDimension(currentDimension);
                }
                lastDimension = currentDimension;
            }
        });
    }

    private static DimensionType lastDimension = null;

    private void cycleFocusedWaypoint(MinecraftClient client, int direction) {
        if (client.world == null) return;

        var waypoints = WaypointManager.getInstance().getWaypoints(client.world.getDimension());
        if (waypoints.isEmpty()) return;

        if (focusedWaypointIndex == -1) {
            focusedWaypointIndex = direction > 0 ? 0 : waypoints.size() - 1;
        } else {
            focusedWaypointIndex = Math.floorMod(focusedWaypointIndex + direction, waypoints.size());
        }

        Waypoint focused = waypoints.get(focusedWaypointIndex);
        client.player.sendMessage(Text.literal("Focused waypoint: " + focused.getName()), true);
    }

    public static boolean isHudVisible() {
        return hudVisible && !(CONFIG.hideInSpectator && MinecraftClient.getInstance().player.isSpectator());
    }

    public static int getFocusedWaypointIndex() {
        return focusedWaypointIndex;
    }

    public static void setFocusedWaypointIndex(int index) {
        focusedWaypointIndex = index;
    }
}
