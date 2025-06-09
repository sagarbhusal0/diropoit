package com.example.diropoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaypointManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_DIR = new File("config/diropoint");
    private static final File WAYPOINTS_FILE = new File(CONFIG_DIR, "waypoints.json");
    private static WaypointManager INSTANCE;

    private Map<DimensionType, List<Waypoint>> waypointsByDimension = new HashMap<>();
    private Waypoint lastAddedWaypoint;

    public static WaypointManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WaypointManager();
        }
        return INSTANCE;
    }

    private WaypointManager() {
        loadWaypoints();
    }

    public static void addWaypoint(String name, int x, int y, int z) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        
        DimensionType dimension = player.getWorld().getDimension();
        Waypoint waypoint = new Waypoint(name, x, y, z, dimension);
        getInstance().addWaypoint(waypoint);
        getInstance().lastAddedWaypoint = waypoint;
    }

    public void addWaypoint(Waypoint waypoint) {
        DimensionType dimension = waypoint.getDimension();
        if (!waypointsByDimension.containsKey(dimension)) {
            waypointsByDimension.put(dimension, new ArrayList<>());
        }
        waypointsByDimension.get(dimension).add(waypoint);
        lastAddedWaypoint = waypoint;
        saveWaypoints();
    }

    public static Waypoint getLastWaypoint() {
        return getInstance().lastAddedWaypoint;
    }

    public void removeWaypoint(Waypoint waypoint) {
        DimensionType dimension = waypoint.getDimension();
        if (waypointsByDimension.containsKey(dimension)) {
            waypointsByDimension.get(dimension).remove(waypoint);
            if (waypoint == lastAddedWaypoint) {
                lastAddedWaypoint = null;
            }
            saveWaypoints();
        }
    }

    public static void teleportToWaypoint(ClientPlayerEntity player, Waypoint waypoint) {
        if (player == null || waypoint == null || !player.hasPermissionLevel(2)) {
            return;
        }

        String command = String.format("/tp %d %d %d", 
            waypoint.getX(), 
            waypoint.getY(), 
            waypoint.getZ()
        );
        
        player.networkHandler.sendCommand(command.substring(1)); // Remove the leading '/'
    }

    private void saveWaypoints() {
        try {
            if (!CONFIG_DIR.exists()) {
                CONFIG_DIR.mkdirs();
            }
            FileWriter writer = new FileWriter(WAYPOINTS_FILE);
            GSON.toJson(waypointsByDimension, writer);
            writer.close();
        } catch (IOException e) {
            DiroPointMod.LOGGER.error("Failed to save waypoints", e);
        }
    }

    private void loadWaypoints() {
        if (!WAYPOINTS_FILE.exists()) {
            return;
        }
        try {
            FileReader reader = new FileReader(WAYPOINTS_FILE);
            Type type = new TypeToken<Map<DimensionType, List<Waypoint>>>(){}.getType();
            waypointsByDimension = GSON.fromJson(reader, type);
            reader.close();
        } catch (IOException e) {
            DiroPointMod.LOGGER.error("Failed to load waypoints", e);
        }
    }

    public List<Waypoint> getWaypoints(DimensionType dimension) {
        return waypointsByDimension.getOrDefault(dimension, new ArrayList<>());
    }
}
