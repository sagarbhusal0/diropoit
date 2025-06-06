package com.example.diropoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.world.dimension.DimensionType;

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

    private Map<DimensionType, List<Waypoint>> waypointsByDimension = new HashMap<>();

    public WaypointManager() {
        loadWaypoints();
    }

    public void addWaypoint(Waypoint waypoint) {
        DimensionType dimension = waypoint.getDimension();
        if (!waypointsByDimension.containsKey(dimension)) {
            waypointsByDimension.put(dimension, new ArrayList<>());
        }
        waypointsByDimension.get(dimension).add(waypoint);
        saveWaypoints();
    }

    public void removeWaypoint(Waypoint waypoint) {
        DimensionType dimension = waypoint.getDimension();
        if (waypointsByDimension.containsKey(dimension)) {
            waypointsByDimension.get(dimension).remove(waypoint);
            saveWaypoints();
        }
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
}
