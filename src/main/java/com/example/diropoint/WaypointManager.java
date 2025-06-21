package com.example.diropoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.example.diropoint.waypoint.Waypoint;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.util.math.Vec3d;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class WaypointManager {
    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .create();
    private static final File CONFIG_DIR = new File("config/diropoint");
    private static final File WAYPOINTS_FILE = new File(CONFIG_DIR, "waypoints.json");
    private static WaypointManager INSTANCE;

    private Map<DimensionType, List<Waypoint>> waypointsByDimension = new HashMap<>();
    private Map<String, List<Waypoint>> waypointsByGroup = new HashMap<>();
    private List<Waypoint> temporaryWaypoints = new ArrayList<>();
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

    public void addWaypoint(Waypoint waypoint) {
        if (waypoint.isTemporary()) {
            temporaryWaypoints.add(waypoint);
        } else {
            DimensionType dimension = waypoint.getDimension();
            waypointsByDimension.computeIfAbsent(dimension, k -> new ArrayList<>()).add(waypoint);
            
            if (waypoint.getGroup() != null && !waypoint.getGroup().isEmpty()) {
                waypointsByGroup.computeIfAbsent(waypoint.getGroup(), k -> new ArrayList<>()).add(waypoint);
            }
        }
        
        lastAddedWaypoint = waypoint;
        saveWaypoints();
    }

    public void removeWaypoint(Waypoint waypoint) {
        if (waypoint.isTemporary()) {
            temporaryWaypoints.remove(waypoint);
        } else {
            DimensionType dimension = waypoint.getDimension();
            if (waypointsByDimension.containsKey(dimension)) {
                waypointsByDimension.get(dimension).remove(waypoint);
            }
            
            if (waypoint.getGroup() != null) {
                waypointsByGroup.computeIfPresent(waypoint.getGroup(), (k, v) -> {
                    v.remove(waypoint);
                    return v.isEmpty() ? null : v;
                });
            }
        }

        if (waypoint == lastAddedWaypoint) {
            lastAddedWaypoint = null;
        }
        
        saveWaypoints();
    }

    public List<Waypoint> getWaypoints(DimensionType dimension) {
        List<Waypoint> waypoints = new ArrayList<>();
        
        // Add permanent waypoints for the dimension
        if (waypointsByDimension.containsKey(dimension)) {
            waypoints.addAll(waypointsByDimension.get(dimension));
        }
        
        // Add temporary waypoints for the dimension
        waypoints.addAll(temporaryWaypoints.stream()
            .filter(w -> w.getDimension() == dimension)
            .collect(Collectors.toList()));
        
        return waypoints;
    }

    public List<Waypoint> getWaypointsByGroup(String group) {
        return waypointsByGroup.getOrDefault(group, Collections.emptyList());
    }

    public Set<String> getGroups() {
        return waypointsByGroup.keySet();
    }

    public List<Waypoint> getTemporaryWaypoints() {
        return temporaryWaypoints;
    }

    public void clearTemporaryWaypoints() {
        temporaryWaypoints.clear();
        saveWaypoints();
    }

    public Waypoint getLastWaypoint() {
        return lastAddedWaypoint;
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

        player.networkHandler.sendCommand(command);
    }

    public void importWaypoints(String json) {
        try {
            Type type = new TypeToken<Map<DimensionType, List<Waypoint>>>() {}.getType();
            Map<DimensionType, List<Waypoint>> imported = GSON.fromJson(json, type);
            
            // Merge imported waypoints with existing ones
            for (Map.Entry<DimensionType, List<Waypoint>> entry : imported.entrySet()) {
                waypointsByDimension.computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
                    .addAll(entry.getValue());
                
                // Update groups
                for (Waypoint waypoint : entry.getValue()) {
                    if (waypoint.getGroup() != null && !waypoint.getGroup().isEmpty()) {
                        waypointsByGroup.computeIfAbsent(waypoint.getGroup(), k -> new ArrayList<>())
                            .add(waypoint);
                    }
                }
            }
            
            saveWaypoints();
        } catch (Exception e) {
            DiroPointMod.LOGGER.error("Failed to import waypoints", e);
        }
    }

    public String exportWaypoints() {
        return GSON.toJson(waypointsByDimension);
    }

    public void save() {
        saveWaypoints();
    }

    private void saveWaypoints() {
        try {
            if (!CONFIG_DIR.exists()) {
                CONFIG_DIR.mkdirs();
            }

            Map<String, Object> data = new HashMap<>();
            data.put("waypoints", waypointsByDimension);
            data.put("temporary", temporaryWaypoints);

            try (FileWriter writer = new FileWriter(WAYPOINTS_FILE)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            DiroPointMod.LOGGER.error("Failed to save waypoints", e);
        }
    }

    private void loadWaypoints() {
        if (!WAYPOINTS_FILE.exists()) {
            return;
        }

        try (FileReader reader = new FileReader(WAYPOINTS_FILE)) {
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> data = GSON.fromJson(reader, type);
            
            if (data.containsKey("waypoints")) {
                Type waypointType = new TypeToken<Map<DimensionType, List<Waypoint>>>() {}.getType();
                waypointsByDimension = GSON.fromJson(GSON.toJson(data.get("waypoints")), waypointType);
            }
            
            if (data.containsKey("temporary")) {
                Type tempType = new TypeToken<List<Waypoint>>() {}.getType();
                temporaryWaypoints = GSON.fromJson(GSON.toJson(data.get("temporary")), tempType);
            }

            // Rebuild group index
            waypointsByGroup.clear();
            for (List<Waypoint> waypoints : waypointsByDimension.values()) {
                for (Waypoint waypoint : waypoints) {
                    if (waypoint.getGroup() != null && !waypoint.getGroup().isEmpty()) {
                        waypointsByGroup.computeIfAbsent(waypoint.getGroup(), k -> new ArrayList<>())
                            .add(waypoint);
                    }
                }
            }
        } catch (IOException e) {
            DiroPointMod.LOGGER.error("Failed to load waypoints", e);
        }
    }

    public void onPlayerDeath(ClientPlayerEntity player) {
        if (player == null || !DiroPointModClient.CONFIG.createDeathWaypoints) return;

        Vec3d deathPos = player.getPos();
        Waypoint deathPoint = Waypoint.createDeathWaypoint(deathPos, MinecraftClient.getInstance().world.getDimension());
        addWaypoint(deathPoint);
    }

    public void onPlayerChangeDimension(DimensionType newDimension) {
        // Clear temporary waypoints when changing dimensions if configured
        if (DiroPointModClient.CONFIG.clearTemporaryOnDimensionChange) {
            clearTemporaryWaypoints();
        }
    }
}
